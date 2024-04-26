package io.openems.edge.levl.controller.workflow;

import com.google.gson.JsonObject;
import io.openems.common.exceptions.OpenemsError;
import io.openems.common.jsonrpc.base.JsonrpcRequest;
import io.openems.common.jsonrpc.base.JsonrpcResponseSuccess;
import io.openems.common.jsonrpc.request.UpdateComponentConfigRequest;
import io.openems.common.session.Language;
import io.openems.common.session.Role;
import io.openems.edge.common.component.AbstractOpenemsComponent;
import io.openems.edge.common.component.ComponentManager;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.common.event.EdgeEventConstants;
import io.openems.edge.common.jsonapi.JsonApi;
import io.openems.edge.common.user.ManagedUser;
import io.openems.edge.common.user.User;
import io.openems.edge.controller.api.Controller;
import io.openems.edge.ess.api.ManagedSymmetricEss;
import io.openems.edge.ess.power.api.Phase;
import io.openems.edge.ess.power.api.Pwr;
import io.openems.edge.levl.controller.controllers.common.LevlWorkflowReference;
import io.openems.edge.levl.controller.controllers.common.Limit;
import io.openems.edge.levl.controller.workflow.storage.LevlWorkflowStateConfigProvider;
import io.openems.edge.meter.api.ElectricityMeter;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.osgi.service.event.propertytypes.EventTopics;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Designate(ocd = Config.class, factory = true)
@Component(//
		name = "Levl.Workflow", // This name has to be kept for compatibility reasons
		immediate = true, //
		configurationPolicy = ConfigurationPolicy.REQUIRE //
)
@EventTopics({ //
		EdgeEventConstants.TOPIC_CYCLE_BEFORE_CONTROLLERS, //
		EdgeEventConstants.TOPIC_CYCLE_AFTER_WRITE, //
})
public class LevlWorkflowComponentImpl extends AbstractOpenemsComponent
		implements EventHandler, OpenemsComponent, JsonApi, LevlWorkflowReference, LevlWorkflowComponent {

	private Config config;
	private final Logger log = LoggerFactory.getLogger(LevlWorkflowComponentImpl.class);

	@Reference
	protected ComponentManager componentManager;
	@Reference
	protected ConfigurationAdmin cm;
	@Reference 
	protected ManagedSymmetricEss ess;
	@Reference
	protected ElectricityMeter meter;

	@Reference(cardinality = ReferenceCardinality.OPTIONAL)
	protected volatile LevlWorkflowStateConfigProvider levlWorkflowSavedState;

	private boolean wasRestoredFromConfig = false;
	protected LevlWorkflowState levlState = new LevlWorkflowState();

	public LevlWorkflowComponentImpl() {
		super(//
				OpenemsComponent.ChannelId.values(), //
				Controller.ChannelId.values(), //
				LevlWorkflowComponent.ChannelId.values() //
		);
	}

	@Activate
	protected void activate(ComponentContext context, Config config) {
		super.activate(context, config.id(), config.alias(), config.enabled());
		this.config = config;
		if (OpenemsComponent.updateReferenceFilter(this.cm, this.servicePid(), "ess", config.ess_id())) {
			return;
		}
		if (OpenemsComponent.updateReferenceFilter(this.cm, this.servicePid(), "meter", config.meter_id())) {
			return;
		}
		if (OpenemsComponent.updateReferenceFilter(this.cm, this.servicePid(), "levlWorkflowSavedState",
				config.levl_workflow_state_id())) {
			return;
		}
	}

	@Override
	@Deactivate
	protected void deactivate() {
		super.deactivate();
	}

	// Passiert jede Sekunde durch OpenEMS
	@Override
	public void handleEvent(Event event) {
		this.tryToRestoreStateIfRequired();
		this.levlState.updateState();
		switch (event.getTopic()) {
		case EdgeEventConstants.TOPIC_CYCLE_BEFORE_CONTROLLERS -> this.levlState.determineNextDischargePower();
		case EdgeEventConstants.TOPIC_CYCLE_AFTER_WRITE -> {
			Optional<Integer> actualPowerW = this.ess.getDebugSetActivePowerChannel().getNextValue().asOptional();
			this.levlState.checkActualDischargePower(actualPowerW);
			this.updateChannels(actualPowerW);
			this.saveState();
		}
		}
	}

	private void updateChannels(Optional<Integer> actualPowerW) {
		actualPowerW.ifPresent(this::_setActualDischargePowerW);
		_setRealizedPowerW(this.levlState.getLastCompletedRequestDischargePowerW());
		_setPrimaryUseCaseDischargePowerW(this.levlState.getPrimaryUseCaseActivePowerW());
		_setLevlDischargePowerW(this.levlState.getActualLevlPowerW());
		_setLastControlRequestTimestamp(this.levlState.getLastCompletedRequestTimestamp());
	}

	@Override
	public void setPrimaryUseCaseActivePowerW(int originalActivePowerW) {
		this.levlState.setPrimaryUseCaseActivePowerW(originalActivePowerW);
	}

	@Override
	public int getNextDischargePowerW() {
		return this.levlState.getNextDischargePowerW();
	}

	@Override
	public Limit getLevlUseCaseConstraints() {
		return this.levlState.getLevlUseCaseConstraints(this.meter.getActivePower(), this.ess.getSoc());
	}

	@Override
	public Limit determinePrimaryUseCaseConstraints() {
		int minPower = this.ess.getPower().getMinPower(this.ess, Phase.ALL, Pwr.ACTIVE);
		int maxPower = this.ess.getPower().getMaxPower(this.ess, Phase.ALL, Pwr.ACTIVE);
		return this.levlState.determinePrimaryUseCaseConstraints(this.ess.getSoc(), this.ess.getCapacity(), minPower,
				maxPower);
	}

	@Override
	public CompletableFuture<? extends JsonrpcResponseSuccess> handleJsonrpcRequest(User user, JsonrpcRequest request)
			throws OpenemsError.OpenemsNamedException {
		if (LevlControlRequest.METHOD.equals(request.getMethod())) {
			var levlControlRequest = LevlControlRequest.from(request);
			this.levlState.handleRequest(levlControlRequest, this.config.physical_soc_lower_bound_percent(),
					this.config.physical_soc_upper_bound_percent());
			return CompletableFuture.completedFuture(JsonrpcResponseSuccess
					.from(this.generateResponse(request.getId(), levlControlRequest.getLevlRequestId())));
		}
		throw OpenemsError.JSONRPC_UNHANDLED_METHOD.exception(request.getMethod());
	}

	private JsonObject generateResponse(UUID requestId, String levlRequestId) {
		JsonObject response = new JsonObject();
		var result = new JsonObject();
		result.addProperty("levlRequestId", levlRequestId);
		response.addProperty("id", requestId.toString());
		response.add("result", result);
		return response;
	}

	public void saveState() {
		var currentLevlWorkflowSavedState = this.levlWorkflowSavedState;
		if (currentLevlWorkflowSavedState == null) {
			return;
		}
		try {
			var stateMemento = this.levlState.save();
			var properties = new LevlWorkflowStateConverter().asProperties(stateMemento);
			var request = new UpdateComponentConfigRequest(currentLevlWorkflowSavedState.id(), properties);
			try {
				// TODO: 14.02.2024 Dennis: Was wird hier gemacht? Hier wird auch ein
				// jsonRpcRequest erstellt?
				var user = new ManagedUser("admin", "Admin", Language.DEFAULT, Role.ADMIN, "", "");
				var response = this.componentManager.handleJsonrpcRequest(user, request);
				response.whenComplete((r, e) -> {
					if (e != null) {
						this.log.error("### could not save state to config: " + e.getMessage());
					}
				});
			} catch (OpenemsError.OpenemsNamedException e) {
				throw new RuntimeException(e);
			}
		} catch (Exception e) {
			this.log.error("### could not save state to config: " + e.getMessage());

		}
	}

	void tryToRestoreStateIfRequired() {
		if (this.wasRestoredFromConfig) {
			return;
		}

		var currentLevlWorkflowSavedState = this.levlWorkflowSavedState;
		if (currentLevlWorkflowSavedState == null) {
			return;
		}
		try {
			var configState = currentLevlWorkflowSavedState.getConfig();
			if (configState != null) {
				this.levlState.restore(new LevlWorkflowStateConverter().levlWorkflowComponentFromConfig(configState));
				this.log.info("### restored state from config");
				this.levlState.initAfterRestore();
				this.wasRestoredFromConfig = true;
			}
		} catch (Exception e) {
			this.log.error("### could not restore state from config: " + e.getMessage());
		}
	}
}
