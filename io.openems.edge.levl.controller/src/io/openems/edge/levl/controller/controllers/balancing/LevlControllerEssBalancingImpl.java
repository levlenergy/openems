package io.openems.edge.levl.controller.controllers.balancing;

import io.openems.common.exceptions.OpenemsError.OpenemsNamedException;
import io.openems.common.utils.ReflectionUtils;
import io.openems.edge.common.component.AbstractOpenemsComponent;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.controller.api.Controller;
import io.openems.edge.ess.api.ManagedSymmetricEss;
import io.openems.edge.levl.controller.controllers.common.LevlWorkflowReference;
import io.openems.edge.levl.controller.controllers.common.ReadOnlyManagedSymmetricEss;
import io.openems.edge.levl.controller.workflow.LevlControllerAction;
import io.openems.edge.meter.api.ElectricityMeter;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;

import java.lang.reflect.InvocationTargetException;

@Designate(ocd = Config.class, factory = true)
@Component(//
		name = "Controller.Levl.Symmetric.Balancing", // This name has to be kept for compatibility reasons
		immediate = true, //
		configurationPolicy = ConfigurationPolicy.REQUIRE //
)
public class LevlControllerEssBalancingImpl extends AbstractOpenemsComponent implements Controller, OpenemsComponent {

	@Reference
	protected ConfigurationAdmin cm;

	@Reference
	protected ManagedSymmetricEss ess;

	@Reference
	private ElectricityMeter meter;

	@Reference
	protected LevlWorkflowReference levlWorkflow;

	protected Controller realController;

	private Config config;

	protected ReadOnlyManagedSymmetricEss wrappedEss;
	protected LevlControllerAction levlControllerAction;

	public LevlControllerEssBalancingImpl() {
		super(//
				OpenemsComponent.ChannelId.values(), //
				Controller.ChannelId.values(), //
				LevlControllerEssBalancing.ChannelId.values() //
		);
	}

	@Activate
	protected void activate(ComponentContext context, Config config) {
		super.activate(context, config.id(), config.alias(), config.enabled());
		this.config = config;
		this.realController = this.createRealController();
		this.levlControllerAction = new LevlControllerAction(this.ess, this.levlWorkflow);
		if (OpenemsComponent.updateReferenceFilter(this.cm, this.servicePid(), "ess", config.ess_id())) {
			return;
		}
		if (OpenemsComponent.updateReferenceFilter(this.cm, this.servicePid(), "meter", config.meter_id())) {
			return;
		}
		if (OpenemsComponent.updateReferenceFilter(this.cm, this.servicePid(), "levlWorkflow",
				config.levl_workflow_id())) {
			return;
		}
	}

	private Controller createRealController() {
		this.wrappedEss = new ReadOnlyManagedSymmetricEss(this.ess);
		try {
			Controller result = new ControllerEssBalancingImpl();
			ReflectionUtils.setAttribute(ControllerEssBalancingImpl.class, result, "config", this.config);
			ReflectionUtils.setAttribute(ControllerEssBalancingImpl.class, result, "ess", this.wrappedEss);
			ReflectionUtils.setAttribute(ControllerEssBalancingImpl.class, result, "meter", this.meter);
			return result;
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	@Deactivate
	protected void deactivate() {
		super.deactivate();
	}

	@Override
	public void run() throws OpenemsNamedException {
		this.wrappedEss.reset();
		this.realController.run();
		Integer originalUnconstrainedActivePower = this.wrappedEss.getReceivedActivePowerEqualsWithPid();
		if (originalUnconstrainedActivePower != null) {
			this.levlControllerAction.addPowers(originalUnconstrainedActivePower);
		}
	}
}
