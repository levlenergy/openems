package io.openems.edge.levl.controller.workflow.storage;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(//
        name = "Levl Workflow State", //
        description = "Keeps the state of Levl Workflow for recovery on restart.")
public @interface Config {

    @AttributeDefinition(name = "Component-ID", description = "Unique ID of this Component")
    String id() default "levlWorkflowState0";

    @AttributeDefinition(name = "Alias", description = "Human-readable name of this Component; defaults to Component-ID")
    String alias() default "";

    @AttributeDefinition(name = "Is enabled?", description = "Is this Component enabled?")
    boolean enabled() default true;


    @AttributeDefinition(name = "Current-Discharge-Request-Id", description = "Id of the current discharge request")
    String current_discharge_request_id() default "";

    @AttributeDefinition(name = "Current-Discharge-Request-Timestamp", description = "Timestamp of the current discharge request")
    String current_discharge_request_timestamp() default "";

    @AttributeDefinition(name = "Current-Discharge-Request-Energy-Ws", description = "Discharge energy in Ws of the current discharge request")
    String current_discharge_request_energy_ws() default "0";

    @AttributeDefinition(name = "Current-Discharge-Request-Start", description = "Start of the current discharge request")
    String current_discharge_request_start() default "";

    @AttributeDefinition(name = "Current-Discharge-Request-Deadline", description = "Deadline of the current discharge request")
    String current_discharge_request_deadline() default "";

    @AttributeDefinition(name = "Current-Discharge-Request-Active", description = "Is the current discharge request active?")
    boolean current_discharge_request_active() default false;
    
    @AttributeDefinition(name = "Current-Influence-Sell-To-Grid", description = "Is it allowed to influence the sell to grid (current request)?")
    boolean current_influence_sell_to_grid() default false;    

    @AttributeDefinition(name = "Next-Discharge-Request-Id", description = "Id of the next discharge request")
    String next_discharge_request_id() default "";

    @AttributeDefinition(name = "Next-Discharge-Request-Timestamp", description = "Timestamp of the next discharge request")
    String next_discharge_request_timestamp() default "";

    @AttributeDefinition(name = "Next-Discharge-Request-Energy-Ws", description = "Discharge energy in Ws of the next discharge request")
    String next_discharge_request_energy_ws() default "0";

    @AttributeDefinition(name = "Next-Discharge-Request-Start", description = "Start of the next discharge request")
    String next_discharge_request_start() default "";

    @AttributeDefinition(name = "Next-Discharge-Request-Deadline", description = "Deadline of the next discharge request")
    String next_discharge_request_deadline() default "";

    @AttributeDefinition(name = "Next-Discharge-Request-Active", description = "Is the next discharge request active?")
    boolean next_discharge_request_active() default false;

    @AttributeDefinition(name = "Next-Influence-Sell-To-Grid", description = "Is it allowed to influence the sell to grid (next request)?")
    boolean next_influence_sell_to_grid() default false;   
    
    @AttributeDefinition(name = "Total-Realized-Discharge-Energy--Ws", description = "Total realized discharge energy in Ws")
    String total_realized_discharge_energy_ws() default "0";

    @AttributeDefinition(name = "Total-Realized-Discharge-Energy-Ws-At-Battery-Scaled-With-Efficiency", description = "Total realized discharge energy in Ws scaled with efficiency")
    String total_discharge_energy_ws_at_battery_scaled_with_efficiency() default "0";

    @AttributeDefinition(name = "Current-Request-Remaining-Discharge-Energy-Ws", description = "Remaining discharge energy in Ws of the current discharge request")
    String current_request_remaining_discharge_energy_ws() default "0";

    @AttributeDefinition(name = "Current-Request-Realized-Discharge-Energy-Ws", description = "Realized discharge energy in Ws of the current discharge request")
    String current_request_realized_discharge_energy_ws() default "0";

    @AttributeDefinition(name = "Last-Request-Realized-Discharge-Energy-Ws", description = "Realized discharge energy in Ws of the last discharge request")
    String last_request_realized_discharge_energy_ws() default "0";

    @AttributeDefinition(name = "Last-Discharge-Request-Timestamp", description = "Timestamp of the last discharge request")
    String last_discharge_request_timestamp() default "";

    @AttributeDefinition(name = "Levl-Soc-Constraints-Lower-Physical-Percent", description = "Physical lower SoC Bound (%) of the battery")
    int levl_soc_constraints_lower_physical_percent() default 0;

    @AttributeDefinition(name = "Levl-Soc-Constraints-Upper-Physical-Percent", description = "Physical upper SoC Bound (%) of the battery")
    int levl_soc_constraints_upper_physical_percent() default 100;

    @AttributeDefinition(name = "Levl-Soc-Constraints-Lower-Logical-Percent", description = "Logical lower SoC Bound (%) of the battery")
    int levl_soc_constraints_lower_logical_percent() default 0;

    @AttributeDefinition(name = "Levl-Soc-Constraints-Upper-Logical-Percent", description = "Logical upper SoC Bound (%) of the battery")
    int levl_soc_constraints_upper_logical_percent() default 100;

    @AttributeDefinition(name = "Grid-Power-Limit-W-Lower", description = "Lower grid power limit in W")
    int grid_power_limit_w_lower() default 0;

    @AttributeDefinition(name = "Grid-Power-Limit-W-Upper", description = "Upper grid power limit in W")
    int grid_power_limit_w_upper() default 0;

    @AttributeDefinition(name = "Efficiency-Percent", description = "Efficiency of the battery in percent (multiplied by 100)")
    int current_request_efficiency_percent_multiplied_by_hundred() default 100_00;

    @AttributeDefinition(name = "Efficiency-Percent", description = "Efficiency of the battery in percent (multiplied by 100)")
    int next_request_efficiency_percent_multiplied_by_hundred() default 100_00;

    @AttributeDefinition(name = "Primary-Use-Case-Active-Power-W", description = "Active power in W of the primary use case")
    int primary_use_case_active_power_w() default 0;

    @AttributeDefinition(name = "Next-Discharge-Power-W", description = "Discharge power in W of the next discharge request")
    int next_discharge_power_w() default 0;

    @AttributeDefinition(name = "Actual-Levl-Power-W", description = "Actual power in W of the Levl use case")
    int actual_levl_power_w() default 0;

    String webconsole_configurationFactory_nameHint() default "Levl Workflow State [{id}]";

}
