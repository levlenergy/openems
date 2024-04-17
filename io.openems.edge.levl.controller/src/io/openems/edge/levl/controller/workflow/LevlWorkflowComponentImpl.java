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
import org.osgi.service.component.annotations.*;
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
public class LevlWorkflowComponentImpl extends AbstractOpenemsComponent implements EventHandler, OpenemsComponent, JsonApi, LevlWorkflowReference, LevlWorkflowComponent {

    private Config config;
    private final Logger log = LoggerFactory.getLogger(LevlWorkflowComponentImpl.class);

    @Reference
    ComponentManager componentManager;
    @Reference
    ConfigurationAdmin cm;
    @Reference
    ManagedSymmetricEss ess;
    @Reference
    ElectricityMeter meter;

    @Reference(cardinality = ReferenceCardinality.OPTIONAL)
    volatile LevlWorkflowStateConfigProvider levlWorkflowSavedState;

    boolean wasRestoredFromConfig = false;
    LevlWorkflowState state = new LevlWorkflowState();


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
        if (OpenemsComponent.updateReferenceFilter(this.cm, this.servicePid(), "levlWorkflowSavedState", config.levl_workflow_state_id())) {
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
        tryToRestoreStateIfRequired();
        state.updateState();
        switch (event.getTopic()) {
            case EdgeEventConstants.TOPIC_CYCLE_BEFORE_CONTROLLERS -> state.determineNextDischargePower();
            case EdgeEventConstants.TOPIC_CYCLE_AFTER_WRITE -> {
                Optional<Integer> actualPowerW = ess.getDebugSetActivePowerChannel().getNextValue().asOptional();
                state.checkActualDischargePower(actualPowerW);
                updateChannels(actualPowerW);
                saveState();
            }
        }
    }

    private void updateChannels(Optional<Integer> actualPowerW) {
        actualPowerW.ifPresent(this::_setActualDischargePowerW);
        _setRealizedPowerW(state.getLastCompletedRequestDischargePowerW());
        _setPrimaryUseCaseDischargePowerW(state.getPrimaryUseCaseActivePowerW());
        _setLevlDischargePowerW(state.getActualLevlPowerW());
        _setLastControlRequestTimestamp(state.getLastCompletedRequestTimestamp());
    }

    @Override
    public void setPrimaryUseCaseActivePowerW(int originalActivePowerW) {
        state.setPrimaryUseCaseActivePowerW(originalActivePowerW);
    }

    @Override
    public int getNextDischargePowerW() {
        return state.getNextDischargePowerW();
    }

    @Override
    public Limit getLevlUseCaseConstraints() {
        return state.getLevlUseCaseConstraints(meter.getActivePower(), ess.getSoc());
    }

    @Override
    public Limit determinePrimaryUseCaseConstraints() {
        int minPower = ess.getPower().getMinPower(ess, Phase.ALL, Pwr.ACTIVE);
        int maxPower = ess.getPower().getMaxPower(ess, Phase.ALL, Pwr.ACTIVE);
        return state.determinePrimaryUseCaseConstraints(ess.getSoc(), ess.getCapacity(), minPower, maxPower);
    }

    @Override
    public CompletableFuture<? extends JsonrpcResponseSuccess> handleJsonrpcRequest(User user, JsonrpcRequest request) throws OpenemsError.OpenemsNamedException {
        if (LevlControlRequest.METHOD.equals(request.getMethod())) {
            var levlControlRequest = LevlControlRequest.from(request);
            state.handleRequest(levlControlRequest, config.physical_soc_lower_bound_percent(), config.physical_soc_upper_bound_percent());
            return CompletableFuture.completedFuture(JsonrpcResponseSuccess.from(generateResponse(request.getId(), levlControlRequest.getLevlRequestId())));
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
            var stateMemento = state.save();
            var properties = new LevlWorkflowStateConverter().asProperties(stateMemento);
            var request = new UpdateComponentConfigRequest(currentLevlWorkflowSavedState.id(), properties);
            try {
                // TODO: 14.02.2024 Dennis: Was wird hier gemacht? Hier wird auch ein jsonRpcRequest erstellt?
                var user = new ManagedUser("admin", "Admin", Language.DEFAULT, Role.ADMIN, "", "");
                var response = componentManager.handleJsonrpcRequest(user, request);
                response.whenComplete((r, e) -> {
                    if (e != null) {
                        log.error("### could not save state to config: " + e.getMessage());
                    }
                });
            } catch (OpenemsError.OpenemsNamedException e) {
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            log.error("### could not save state to config: " + e.getMessage());

        }
    }

    void tryToRestoreStateIfRequired() {
        if (wasRestoredFromConfig) {
            return;
        }

        var currentLevlWorkflowSavedState = this.levlWorkflowSavedState;
        if (currentLevlWorkflowSavedState == null) {
            return;
        }
        try {
            var configState = currentLevlWorkflowSavedState.getConfig();
            if (configState != null) {
                state.restore(new LevlWorkflowStateConverter().levlWorkflowComponentFromConfig(configState));
                log.info("### restored state from config");
                state.initAfterRestore();
                wasRestoredFromConfig = true;
            }
        } catch (Exception e) {
            log.error("### could not restore state from config: " + e.getMessage());
        }
    }
}
