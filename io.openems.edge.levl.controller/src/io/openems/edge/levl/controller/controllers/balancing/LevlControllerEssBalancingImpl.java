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
import org.osgi.service.component.annotations.*;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;

@Designate(ocd = Config.class, factory = true)
@Component(//
        name = "Controller.Levl.Symmetric.Balancing", // This name has to be kept for compatibility reasons
        immediate = true, //
        configurationPolicy = ConfigurationPolicy.REQUIRE //
)
public class LevlControllerEssBalancingImpl extends AbstractOpenemsComponent implements Controller, OpenemsComponent {

    private final Logger log = LoggerFactory.getLogger(LevlControllerEssBalancingImpl.class);

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
        realController = createRealController();
        levlControllerAction = new LevlControllerAction(ess, levlWorkflow);
        if (OpenemsComponent.updateReferenceFilter(this.cm, this.servicePid(), "ess", config.ess_id())) {
            return;
        }
        if (OpenemsComponent.updateReferenceFilter(this.cm, this.servicePid(), "meter", config.meter_id())) {
            return;
        }
        if (OpenemsComponent.updateReferenceFilter(this.cm, this.servicePid(), "levlWorkflow", config.levl_workflow_id())) {
            return;
        }
    }

    private Controller createRealController() {
        wrappedEss = new ReadOnlyManagedSymmetricEss(ess);
        try {
            Controller result = new ControllerEssBalancingImpl();
            ReflectionUtils.setAttribute(ControllerEssBalancingImpl.class, result, "config", config);
            ReflectionUtils.setAttribute(ControllerEssBalancingImpl.class, result, "ess", wrappedEss);
            ReflectionUtils.setAttribute(ControllerEssBalancingImpl.class, result, "meter", meter);
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
        wrappedEss.reset();
        realController.run();
        Integer originalUnconstrainedActivePower = wrappedEss.getReceivedActivePowerEqualsWithPid();
        if (originalUnconstrainedActivePower != null) {
            levlControllerAction.addPowers(originalUnconstrainedActivePower);
        }
    }
}
