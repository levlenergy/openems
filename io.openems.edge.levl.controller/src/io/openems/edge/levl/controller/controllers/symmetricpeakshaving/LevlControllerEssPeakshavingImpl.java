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
import org.osgi.service.component.annotations.*;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;

@Designate(ocd = Config.class, factory = true)
@Component(//
        name = "Controller.Levl.Symmetric.PeakShaving", // This name has to be kept for compatibility reasons
        immediate = true, //
        configurationPolicy = ConfigurationPolicy.REQUIRE //
)
public class LevlControllerEssPeakshavingImpl extends AbstractOpenemsComponent implements Controller, OpenemsComponent, ControllerEssPeakShaving {

    private final Logger log = LoggerFactory.getLogger(LevlControllerEssPeakshavingImpl.class);

    @Reference
    ComponentManager componentManager;

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
        realController = createRealController();
        levlControllerAction = new LevlControllerAction(ess, levlWorkflow);
    }

    private ControllerEssPeakShavingImpl createRealController() {
        wrappedEss = new ReadOnlyManagedSymmetricEss(ess);
        levlComponentManager = new LevlComponentManager(componentManager);
        levlComponentManager.overwriteComponent(ess.id(), wrappedEss);
        try {
            var result = new ControllerEssPeakShavingImpl();
            ReflectionUtils.setAttribute(ControllerEssPeakShavingImpl.class, result, "config", config);
            ReflectionUtils.setAttribute(ControllerEssPeakShavingImpl.class, result, "componentManager", levlComponentManager);
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
            levlControllerAction.onlyIncreaseAbsolutePower(originalUnconstrainedActivePower);
        }
    }
}
