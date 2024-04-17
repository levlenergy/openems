package io.openems.edge.levl.controller.workflow.storage;

import io.openems.edge.common.component.AbstractOpenemsComponent;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.controller.api.Controller;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Designate(ocd = Config.class, factory = true)
@Component(//
        name = "Levl.Workflow.State", // This name has to be kept for compatibility reasons
        immediate = true, //
        configurationPolicy = ConfigurationPolicy.REQUIRE //
)
public class LevlWorkflowStateComponentImpl extends AbstractOpenemsComponent implements OpenemsComponent, LevlWorkflowStateConfigProvider {

    private final Logger log = LoggerFactory.getLogger(LevlWorkflowStateComponentImpl.class);


    private Config config;


    public LevlWorkflowStateComponentImpl() {
        super(//
                OpenemsComponent.ChannelId.values(), //
                Controller.ChannelId.values(), //
                LevlWorkflowStateComponent.ChannelId.values() //
        );
    }


    @Activate
    protected void activate(ComponentContext context, Config config) {
        super.activate(context, config.id(), config.alias(), config.enabled());
        this.config = config;
    }

    @Override
    @Deactivate
    protected void deactivate() {
        super.deactivate();
    }

    @Override
    public Config getConfig() {
        return config;
    }
}
