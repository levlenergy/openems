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

	/**
	 * Converts the given LevlWorkflowStateMemento into a list of UpdateComponentConfigRequest.Property.
	 * Each property represents a specific configuration attribute of the LevlWorkflowStateMemento.
	 *
	 * @param memento the LevlWorkflowStateMemento to be converted
	 * @return a list of UpdateComponentConfigRequest.Property representing the configuration attributes of the memento
	 */
	public List<UpdateComponentConfigRequest.Property> asProperties(LevlWorkflowState.LevlWorkflowStateMemento memento) {
		var properties = List.of(
				ConfigAttributes.PRIMARY_USE_CASE_ACTIVE_POWER_W.asProperty(memento.primaryUseCaseActivePowerW()),
				ConfigAttributes.NEXT_DISCHARGE_POWER_W.asProperty(memento.nextDischargePowerW()),
				ConfigAttributes.ACTUAL_LEVL_POWER_W.asProperty(memento.actualLevlPowerW()));
		return CollectionUtil.join(properties, this.asProperties(memento.state()),
				this.asProperties(memento.levlSocConstraints()), this.asProperties(memento.gridPowerLimitW()));
	}
	
	private List<UpdateComponentConfigRequest.Property> asProperties(
			LevlSocConstraints.LevlSocConstraintsMemento memento) {
		return CollectionUtil.join(this.asPhysicalContraintsProperties(memento.physicalSocConstraint()),
				this.asLogicalContraintsProperties(memento.socConstraint()));
	}

	private List<UpdateComponentConfigRequest.Property> asProperties(Limit.LimitMemento memento) {
		return List.of(ConfigAttributes.GRID_POWER_LIMIT_W_LOWER.asProperty(memento.minPower()),
				ConfigAttributes.GRID_POWER_LIMIT_W_UPPER.asProperty(memento.maxPower()));
	}

	private List<UpdateComponentConfigRequest.Property> asProperties(DischargeState.DischargeStateMemento memento) {
		var properties = List.of(
				ConfigAttributes.TOTAL_DISCHARGE_ENERGY_WS_AT_BATTERY_SCALED_WITH_EFFICIENCY
						.asProperty(memento.totalDischargeEnergyWsAtBatteryScaledWithEfficiency()),
				ConfigAttributes.CURRENT_REQUEST_REMAINING_DISCHARGE_ENERGY_WS
						.asProperty(memento.currentRequestRemainingDischargeEnergyWs()),
				ConfigAttributes.CURRENT_REQUEST_REALIZED_DISCHARGE_ENERGY_WS
						.asProperty(memento.currentRequestRealizedDischargeEnergyWs()),
				ConfigAttributes.LAST_REQUEST_REALIZED_DISCHARGE_ENERGY_WS
						.asProperty(memento.lastRequestRealizedDischargeEnergyWs()),
				ConfigAttributes.LAST_DISCHARGE_REQUEST_TIMESTAMP.asProperty(memento.lastDischargeRequestTimestamp()),
				ConfigAttributes.EFFICIENCY_PERCENT_MULTIPLIED_BY_HUNDRED
						.asProperty(memento.currentRequestEfficiencyPercent().multiply(BigDecimal.valueOf(100))
								.setScale(0, RoundingMode.HALF_UP).intValue()));
		return CollectionUtil.join(properties, this.asCurrentRequestProperties(memento.request()),
				this.asNextRequestProperties(memento.nextRequest()));
	}

	private List<UpdateComponentConfigRequest.Property> asCurrentRequestProperties(
			DischargeRequest.DischargeRequestMemento memento) {
		return List.of(ConfigAttributes.CURRENT_DISCHARGE_REQUEST_ID.asProperty(memento.lastRequestId()),
				ConfigAttributes.CURRENT_DISCHARGE_REQUEST_TIMESTAMP.asProperty(memento.requestTimestamp()),
				ConfigAttributes.CURRENT_DISCHARGE_REQUEST_ENERGY_WS.asProperty(memento.dischargeEnergyWs()),
				ConfigAttributes.CURRENT_DISCHARGE_REQUEST_START.asProperty(memento.start()),
				ConfigAttributes.CURRENT_DISCHARGE_REQUEST_DEADLINE.asProperty(memento.deadline()),
				ConfigAttributes.CURRENT_DISCHARGE_REQUEST_ACTIVE.asProperty(memento.active()),
				ConfigAttributes.CURRENT_INFLUENCE_SELL_TO_GRID.asProperty(memento.influenceSellToGrid()));
	}

	private List<UpdateComponentConfigRequest.Property> asNextRequestProperties(
			DischargeRequest.DischargeRequestMemento memento) {
		return List.of(ConfigAttributes.NEXT_DISCHARGE_REQUEST_ID.asProperty(memento.lastRequestId()),
				ConfigAttributes.NEXT_DISCHARGE_REQUEST_TIMESTAMP.asProperty(memento.requestTimestamp()),
				ConfigAttributes.NEXT_DISCHARGE_REQUEST_ENERGY_WS.asProperty(memento.dischargeEnergyWs()),
				ConfigAttributes.NEXT_DISCHARGE_REQUEST_START.asProperty(memento.start()),
				ConfigAttributes.NEXT_DISCHARGE_REQUEST_DEADLINE.asProperty(memento.deadline()),
				ConfigAttributes.NEXT_DISCHARGE_REQUEST_ACTIVE.asProperty(memento.active()),
				ConfigAttributes.NEXT_INFLUENCE_SELL_TO_GRID.asProperty(memento.influenceSellToGrid()));
	}

	private List<UpdateComponentConfigRequest.Property> asPhysicalContraintsProperties(
			SocConstraint.SocConstraintMemento memento) {
		return List.of(
				ConfigAttributes.LEVL_SOC_CONSTRAINTS_LOWER_PHYSICAL_PERCENT.asProperty(memento.socLowerBoundPercent()),
				ConfigAttributes.LEVL_SOC_CONSTRAINTS_UPPER_PHYSICAL_PERCENT
						.asProperty(memento.socUpperBoundPercent()));
	}

	private List<UpdateComponentConfigRequest.Property> asLogicalContraintsProperties(
			SocConstraint.SocConstraintMemento memento) {
		return List.of(
				ConfigAttributes.LEVL_SOC_CONSTRAINTS_LOWER_LOGICAL_PERCENT.asProperty(memento.socLowerBoundPercent()),
				ConfigAttributes.LEVL_SOC_CONSTRAINTS_UPPER_LOGICAL_PERCENT.asProperty(memento.socUpperBoundPercent()));
	}

	/**
	 * Converts the given Config into a LevlWorkflowStateMemento.
	 * The Config contains the configuration attributes for the Levl workflow state.
	 * Each attribute in the Config is mapped to a corresponding attribute in the LevlWorkflowStateMemento.
	 *
	 * @param config the Config to be converted
	 * @return a LevlWorkflowStateMemento representing the configuration attributes of the Config
	 */
	public LevlWorkflowState.LevlWorkflowStateMemento levlWorkflowComponentFromConfig(Config config) {
		return new LevlWorkflowState.LevlWorkflowStateMemento(config.primary_use_case_active_power_w(),
				config.next_discharge_power_w(), config.actual_levl_power_w(), this.dischargeStateFromConfig(config),
				this.levlSocConstraintsFromConfig(config), this.gridLimitFromConfig(config));
	}

	private DischargeState.DischargeStateMemento dischargeStateFromConfig(Config config) {
		return new DischargeState.DischargeStateMemento(Long.parseLong(config.total_discharge_energy_ws_at_battery_scaled_with_efficiency()),
				Long.parseLong(config.current_request_remaining_discharge_energy_ws()),
				Long.parseLong(config.current_request_realized_discharge_energy_ws()),
				Long.parseLong(config.last_request_realized_discharge_energy_ws()),
				BigDecimal.valueOf(config.current_request_efficiency_percent_multiplied_by_hundred() * 1.0 / 100),
				BigDecimal.valueOf(config.next_request_efficiency_percent_multiplied_by_hundred() * 1.0 / 100),
				config.last_discharge_request_timestamp(), 
				this.currentDischargeRequestFromConfig(config),
				this.nextDischargeRequestFromConfig(config));
	}

	private DischargeRequest.DischargeRequestMemento currentDischargeRequestFromConfig(Config config) {

		try {
			return new DischargeRequest.DischargeRequestMemento(config.current_discharge_request_id(),
					config.current_discharge_request_timestamp(),
					Long.parseLong(config.current_discharge_request_energy_ws()),
					config.current_influence_sell_to_grid(), 
					ConfigAttributes.parseLocalDateTime(config.current_discharge_request_start()),
					ConfigAttributes.parseLocalDateTime(config.current_discharge_request_deadline()),
					config.current_discharge_request_active());
		} catch (DateTimeParseException e) {
			this.log.error("could not parse LocalDateTime: " + e.getMessage(), e);
			return DischargeRequest.inactiveRequest().save();
		}
	}

	private DischargeRequest.DischargeRequestMemento nextDischargeRequestFromConfig(Config config) {
		try {
			return new DischargeRequest.DischargeRequestMemento(config.next_discharge_request_id(),
					config.next_discharge_request_timestamp(),
					Long.parseLong(config.next_discharge_request_energy_ws()),
					config.next_influence_sell_to_grid(), 
					ConfigAttributes.parseLocalDateTime(config.next_discharge_request_start()),
					ConfigAttributes.parseLocalDateTime(config.next_discharge_request_deadline()),
					config.next_discharge_request_active());
		} catch (DateTimeParseException e) {
			this.log.error("could not parse LocalDateTime: " + e.getMessage());
			return DischargeRequest.inactiveRequest().save();
		}
	}

	private LevlSocConstraints.LevlSocConstraintsMemento levlSocConstraintsFromConfig(Config config) {
		return new LevlSocConstraints.LevlSocConstraintsMemento(this.physicalSocConstraintsFromConfig(config),
				this.logicalSocConstraintsFromConfig(config));
	}

	private SocConstraint.SocConstraintMemento physicalSocConstraintsFromConfig(Config config) {
		return new SocConstraint.SocConstraintMemento(config.levl_soc_constraints_lower_physical_percent(),
				config.levl_soc_constraints_upper_physical_percent());

	}

	private SocConstraint.SocConstraintMemento logicalSocConstraintsFromConfig(Config config) {
		return new SocConstraint.SocConstraintMemento(config.levl_soc_constraints_lower_logical_percent(),
				config.levl_soc_constraints_upper_logical_percent());
	}

	private Limit.LimitMemento gridLimitFromConfig(Config config) {
		return new Limit.LimitMemento(config.grid_power_limit_w_lower(), config.grid_power_limit_w_upper());
	}

}
