package io.openems.edge.levl.controller.workflow;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(//
        name = "Levl Workflow", //
        description = "Monitors batteries and receives instructions")
@interface Config {

    @AttributeDefinition(name = "Component-ID", description = "Unique ID of this Component")
    String id() default "levlWorkflow0";

    @AttributeDefinition(name = "Alias", description = "Human-readable name of this Component; defaults to Component-ID")
    String alias() default "";

    @AttributeDefinition(name = "Is enabled?", description = "Is this Component enabled?")
    boolean enabled() default true;

    @AttributeDefinition(name = "Ess-ID", description = "ID of Ess device to monitor.")
    String ess_id();

    @AttributeDefinition(name = "Ess target filter", description = "This is auto-generated by 'Ess-ID'.")
    String ess_target() default "(enabled=true)";

    @AttributeDefinition(name = "Grid-Meter-ID", description = "ID of the Grid-Meter.")
    String meter_id();

    @AttributeDefinition(name = "Meter target filter", description = "This is auto-generated by 'Meter-ID'.")
    String meter_target() default "(enabled=true)";

    @AttributeDefinition(name = "LevlWorkflowState-ID", description = "ID of the levl workflow state component.")
    String levl_workflow_state_id();

    @AttributeDefinition(name = "LevlWorkflowState target filter", description = "This is auto-generated by 'LevlWorkflowState-ID'.")
    String levl_workflow_state_target() default "(enabled=true)";

    String webconsole_configurationFactory_nameHint() default "Levl Workflow [{id}]";

    @AttributeDefinition(name = "Physical-SoC-Lower-Bound-Percent", description = "Physical lower SoC Bound (%) of the battery")
    int physical_soc_lower_bound_percent() default 0;

    @AttributeDefinition(name = "Physical-SoC-Upper-Bound-Percent", description = "Physical upper SoC Bound (%) of the battery")
    int physical_soc_upper_bound_percent() default 100;

}