package io.openems.edge.levl.controller.controllers.symmetricpeakshaving;

import io.openems.common.exceptions.OpenemsError.OpenemsNamedException;
import io.openems.common.utils.ReflectionUtils;
import io.openems.edge.common.component.AbstractOpenemsComponent;
import io.openems.edge.common.component.ComponentManager;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.controller.api.Controller;
import io.openems.edge.ess.api.ManagedSymmetricEss;
import io.openems.edge.levl.controller.controllers.balancing.LevlControllerEssBalancing;
import io.openems.edge.levl.controller.controllers.common.LevlComponentManager;
import io.openems.edge.levl.controller.controllers.common.LevlWorkflowReference;
import io.openems.edge.levl.controller.controllers.common.ReadOnlyManagedSymmetricEss;
import io.openems.edge.levl.controller.workflow.LevlControllerAction;
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
        name = "Controller.Levl.Symmetric.PeakShaving", // This name has to be kept for compatibility reasons
        immediate = true, //
        configurationPolicy = ConfigurationPolicy.REQUIRE //
)
public class LevlControllerEssPeakshavingImpl extends AbstractOpenemsComponent implements Controller, OpenemsComponent, ControllerEssPeakShaving {

    @Reference
    protected ComponentManager componentManager;

    @Reference
    protected ManagedSymmetricEss ess;

    @Reference
    protected LevlWorkflowReference levlWorkflow;

    protected ControllerEssPeakShavingImpl realController;

    private Config config;

    protected ReadOnlyManagedSymmetricEss wrappedEss;
    protected LevlComponentManager levlComponentManager;
    protected LevlControllerAction levlControllerAction;


    public LevlControllerEssPeakshavingImpl() {
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
    }

    private ControllerEssPeakShavingImpl createRealController() {
        this.wrappedEss = new ReadOnlyManagedSymmetricEss(this.ess);
        this.levlComponentManager = new LevlComponentManager(this.componentManager);
        this.levlComponentManager.overwriteComponent(this.ess.id(), this.wrappedEss);
        try {
            var result = new ControllerEssPeakShavingImpl();
            ReflectionUtils.setAttribute(ControllerEssPeakShavingImpl.class, result, "config", this.config);
            ReflectionUtils.setAttribute(ControllerEssPeakShavingImpl.class, result, "componentManager", this.levlComponentManager);
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
        	this.levlControllerAction.onlyIncreaseAbsolutePower(originalUnconstrainedActivePower);
        }
    }
}
