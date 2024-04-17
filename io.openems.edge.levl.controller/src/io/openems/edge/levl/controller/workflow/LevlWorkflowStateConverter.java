package io.openems.edge.levl.controller.workflow;

import io.openems.common.jsonrpc.request.UpdateComponentConfigRequest;
import io.openems.edge.levl.controller.controllers.common.CollectionUtil;
import io.openems.edge.levl.controller.controllers.common.Limit;
import io.openems.edge.levl.controller.workflow.storage.Config;
import io.openems.edge.levl.controller.workflow.storage.ConfigAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeParseException;
import java.util.List;

public class LevlWorkflowStateConverter {
    private final Logger log = LoggerFactory.getLogger(LevlWorkflowStateConverter.class);

    public List<UpdateComponentConfigRequest.Property> asProperties(LevlWorkflowState.LevlWorkflowStateMemento memento) {
        var properties = List.of(
                ConfigAttributes.PRIMARY_USE_CASE_ACTIVE_POWER_W.asProperty(memento.primaryUseCaseActivePowerW()),
                ConfigAttributes.NEXT_DISCHARGE_POWER_W.asProperty(memento.nextDischargePowerW()),
                ConfigAttributes.ACTUAL_LEVL_POWER_W.asProperty(memento.actualLevlPowerW())
        );
        return CollectionUtil.join(properties, asProperties(memento.state()), asProperties(memento.levlSocConstraints()), asProperties(memento.gridPowerLimitW()));
    }

    private List<UpdateComponentConfigRequest.Property> asProperties(DischargeState.DischargeStateMemento memento) {
        var properties = List.of(
                ConfigAttributes.TOTAL_REALIZED_DISCHARGE_ENERGY_WS.asProperty(memento.totalRealizedDischargeEnergyWs()),
                ConfigAttributes.TOTAL_DISCHARGE_ENERGY_WS_AT_BATTERY_SCALED_WITH_EFFICIENCY.asProperty(memento.totalDischargeEnergyWsAtBatteryScaledWithEfficiency()),
                ConfigAttributes.CURRENT_REQUEST_REMAINING_DISCHARGE_ENERGY_WS.asProperty(memento.currentRequestRemainingDischargeEnergyWs()),
                ConfigAttributes.CURRENT_REQUEST_REALIZED_DISCHARGE_ENERGY_WS.asProperty(memento.currentRequestRealizedDischargeEnergyWs()),
                ConfigAttributes.LAST_REQUEST_REALIZED_DISCHARGE_ENERGY_WS.asProperty(memento.lastRequestRealizedDischargeEnergyWs()),
                ConfigAttributes.LAST_DISCHARGE_REQUEST_TIMESTAMP.asProperty(memento.lastDischargeRequestTimestamp()),
                ConfigAttributes.EFFICIENCY_PERCENT_MULTIPLIED_BY_HUNDRED.asProperty(memento.currentRequestEfficiencyPercent().multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.HALF_UP).intValue())
        );
        return CollectionUtil.join(properties, asCurrentRequestProperties(memento.request()), asNextRequestProperties(memento.nextRequest()));
    }

    private List<UpdateComponentConfigRequest.Property> asCurrentRequestProperties(DischargeRequest.DischargeRequestMemento memento) {
        return List.of(
                ConfigAttributes.CURRENT_DISCHARGE_REQUEST_ID.asProperty(memento.lastRequestId()),
                ConfigAttributes.CURRENT_DISCHARGE_REQUEST_TIMESTAMP.asProperty(memento.requestTimestamp()),
                ConfigAttributes.CURRENT_DISCHARGE_REQUEST_ENERGY_WS.asProperty(memento.dischargeEnergyWs()),
                ConfigAttributes.CURRENT_DISCHARGE_REQUEST_START.asProperty(memento.start()),
                ConfigAttributes.CURRENT_DISCHARGE_REQUEST_DEADLINE.asProperty(memento.deadline()),
                ConfigAttributes.CURRENT_DISCHARGE_REQUEST_ACTIVE.asProperty(memento.active())
        );
    }

    private List<UpdateComponentConfigRequest.Property> asNextRequestProperties(DischargeRequest.DischargeRequestMemento memento) {
        return List.of(
                ConfigAttributes.NEXT_DISCHARGE_REQUEST_ID.asProperty(memento.lastRequestId()),
                ConfigAttributes.NEXT_DISCHARGE_REQUEST_TIMESTAMP.asProperty(memento.requestTimestamp()),
                ConfigAttributes.NEXT_DISCHARGE_REQUEST_ENERGY_WS.asProperty(memento.dischargeEnergyWs()),
                ConfigAttributes.NEXT_DISCHARGE_REQUEST_START.asProperty(memento.start()),
                ConfigAttributes.NEXT_DISCHARGE_REQUEST_DEADLINE.asProperty(memento.deadline()),
                ConfigAttributes.NEXT_DISCHARGE_REQUEST_ACTIVE.asProperty(memento.active())
        );
    }

    private List<UpdateComponentConfigRequest.Property> asProperties(LevlSocConstraints.LevlSocConstraintsMemento memento) {
        return CollectionUtil.join(asPhysicalContraintsProperties(memento.physicalSocConstraint()), asLogicalContraintsProperties(memento.socConstraint()));
    }

    private List<UpdateComponentConfigRequest.Property> asPhysicalContraintsProperties(SocConstraint.SocConstraintMemento memento) {
        return List.of(
                ConfigAttributes.LEVL_SOC_CONSTRAINTS_LOWER_PHYSICAL_PERCENT.asProperty(memento.socLowerBoundPercent()),
                ConfigAttributes.LEVL_SOC_CONSTRAINTS_UPPER_PHYSICAL_PERCENT.asProperty(memento.socUpperBoundPercent())
        );
    }

    private List<UpdateComponentConfigRequest.Property> asLogicalContraintsProperties(SocConstraint.SocConstraintMemento memento) {
        return List.of(
                ConfigAttributes.LEVL_SOC_CONSTRAINTS_LOWER_LOGICAL_PERCENT.asProperty(memento.socLowerBoundPercent()),
                ConfigAttributes.LEVL_SOC_CONSTRAINTS_UPPER_LOGICAL_PERCENT.asProperty(memento.socUpperBoundPercent())
        );
    }

    private List<UpdateComponentConfigRequest.Property> asProperties(Limit.LimitMemento memento) {
        return List.of(
                ConfigAttributes.GRID_POWER_LIMIT_W_LOWER.asProperty(memento.minPower()),
                ConfigAttributes.GRID_POWER_LIMIT_W_UPPER.asProperty(memento.maxPower())
        );
    }

    public LevlWorkflowState.LevlWorkflowStateMemento levlWorkflowComponentFromConfig(Config config) {
        return new LevlWorkflowState.LevlWorkflowStateMemento(
                config.primary_use_case_active_power_w(),
                config.next_discharge_power_w(),
                config.actual_levl_power_w(),
                dischargeStateFromConfig(config),
                levlSocConstraintsFromConfig(config),
                gridLimitFromConfig(config)
        );
    }

    private DischargeState.DischargeStateMemento dischargeStateFromConfig(Config config) {
        return new DischargeState.DischargeStateMemento(
                Long.parseLong(config.total_realized_discharge_energy_ws()),
                Long.parseLong(config.total_discharge_energy_ws_at_battery_scaled_with_efficiency()),
                Long.parseLong(config.current_request_remaining_discharge_energy_ws()),
                Long.parseLong(config.current_request_realized_discharge_energy_ws()),
                Long.parseLong(config.last_request_realized_discharge_energy_ws()),
                BigDecimal.valueOf(config.current_request_efficiency_percent_multiplied_by_hundred() * 1.0 / 100),
                BigDecimal.valueOf(config.next_request_efficiency_percent_multiplied_by_hundred() * 1.0 / 100),
                config.last_discharge_request_timestamp(),
                currentDischargeRequestFromConfig(config),
                nextDischargeRequestFromConfig(config)
        );
    }

    private DischargeRequest.DischargeRequestMemento currentDischargeRequestFromConfig(Config config) {

        try {
            return new DischargeRequest.DischargeRequestMemento(
                    config.current_discharge_request_id(),
                    config.current_discharge_request_timestamp(),
                    Long.parseLong(config.current_discharge_request_energy_ws()),
                    ConfigAttributes.parseLocalDateTime(config.current_discharge_request_start()),
                    ConfigAttributes.parseLocalDateTime(config.current_discharge_request_deadline()),
                    config.current_discharge_request_active()
            );
        } catch (DateTimeParseException e) {
            log.error("could not parse LocalDateTime: " + e.getMessage(), e);
            return DischargeRequest.inactiveRequest().save();
        }
    }

    private DischargeRequest.DischargeRequestMemento nextDischargeRequestFromConfig(Config config) {
        try {
            return new DischargeRequest.DischargeRequestMemento(
                    config.next_discharge_request_id(),
                    config.next_discharge_request_timestamp(),
                    Long.parseLong(config.next_discharge_request_energy_ws()),
                    ConfigAttributes.parseLocalDateTime(config.next_discharge_request_start()),
                    ConfigAttributes.parseLocalDateTime(config.next_discharge_request_deadline()),
                    config.next_discharge_request_active()
            );
        } catch (DateTimeParseException e) {
            log.error("could not parse LocalDateTime: " + e.getMessage());
            return DischargeRequest.inactiveRequest().save();
        }
    }

    private LevlSocConstraints.LevlSocConstraintsMemento levlSocConstraintsFromConfig(Config config) {
        return new LevlSocConstraints.LevlSocConstraintsMemento(
                physicalSocConstraintsFromConfig(config),
                logicalSocConstraintsFromConfig(config)
        );
    }

    private SocConstraint.SocConstraintMemento physicalSocConstraintsFromConfig(Config config) {
        return new SocConstraint.SocConstraintMemento(
                config.levl_soc_constraints_lower_physical_percent(),
                config.levl_soc_constraints_upper_physical_percent()
        );

    }

    private SocConstraint.SocConstraintMemento logicalSocConstraintsFromConfig(Config config) {
        return new SocConstraint.SocConstraintMemento(
                config.levl_soc_constraints_lower_logical_percent(),
                config.levl_soc_constraints_upper_logical_percent()
        );
    }

    private Limit.LimitMemento gridLimitFromConfig(Config config) {
        return new Limit.LimitMemento(
                config.grid_power_limit_w_lower(),
                config.grid_power_limit_w_upper()
        );
    }

}
