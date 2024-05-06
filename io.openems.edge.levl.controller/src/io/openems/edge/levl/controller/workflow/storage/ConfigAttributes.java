package io.openems.edge.levl.controller.workflow.storage;

import io.openems.common.jsonrpc.request.UpdateComponentConfigRequest;

import java.time.LocalDateTime;

public enum ConfigAttributes {
	CURRENT_DISCHARGE_REQUEST_ID("current_discharge_request_id"),
	CURRENT_DISCHARGE_REQUEST_TIMESTAMP("current_discharge_request_timestamp"),
	CURRENT_DISCHARGE_REQUEST_ENERGY_WS("current_discharge_request_energy_ws"),
	CURRENT_DISCHARGE_REQUEST_START("current_discharge_request_start"),
	CURRENT_DISCHARGE_REQUEST_DEADLINE("current_discharge_request_deadline"),
	CURRENT_DISCHARGE_REQUEST_ACTIVE("current_discharge_request_active"),
	NEXT_DISCHARGE_REQUEST_ID("next_discharge_request_id"),
	NEXT_DISCHARGE_REQUEST_TIMESTAMP("next_discharge_request_timestamp"),
	NEXT_DISCHARGE_REQUEST_ENERGY_WS("next_discharge_request_energy_ws"),
	NEXT_DISCHARGE_REQUEST_START("next_discharge_request_start"),
	NEXT_DISCHARGE_REQUEST_DEADLINE("next_discharge_request_deadline"),
	NEXT_DISCHARGE_REQUEST_ACTIVE("next_discharge_request_active"),
	TOTAL_REALIZED_DISCHARGE_ENERGY_WS("total_realized_discharge_energy_ws"),
	TOTAL_DISCHARGE_ENERGY_WS_AT_BATTERY_SCALED_WITH_EFFICIENCY(
			"total_discharge_energy_ws_at_battery_scaled_with_efficiency"),
	CURRENT_REQUEST_REMAINING_DISCHARGE_ENERGY_WS("current_request_remaining_discharge_energy_ws"),
	CURRENT_REQUEST_REALIZED_DISCHARGE_ENERGY_WS("current_request_realized_discharge_energy_ws"),
	LAST_REQUEST_REALIZED_DISCHARGE_ENERGY_WS("last_request_realized_discharge_energy_ws"),
	LAST_DISCHARGE_REQUEST_TIMESTAMP("last_discharge_request_timestamp"),
	LEVL_SOC_CONSTRAINTS_LOWER_PHYSICAL_PERCENT("levl_soc_constraints_lower_physical_percent"),
	LEVL_SOC_CONSTRAINTS_UPPER_PHYSICAL_PERCENT("levl_soc_constraints_upper_physical_percent"),
	LEVL_SOC_CONSTRAINTS_LOWER_LOGICAL_PERCENT("levl_soc_constraints_lower_logical_percent"),
	LEVL_SOC_CONSTRAINTS_UPPER_LOGICAL_PERCENT("levl_soc_constraints_upper_logical_percent"),
	GRID_POWER_LIMIT_W_LOWER("grid_power_limit_w_lower"), GRID_POWER_LIMIT_W_UPPER("grid_power_limit_w_upper"),
	EFFICIENCY_PERCENT_MULTIPLIED_BY_HUNDRED("efficiency_percent_multiplied_by_hundred"),
	PRIMARY_USE_CASE_ACTIVE_POWER_W("primary_use_case_active_power_w"),
	NEXT_DISCHARGE_POWER_W("next_discharge_power_w"), ACTUAL_LEVL_POWER_W("actual_levl_power_w");

	private final String attributeName;

	ConfigAttributes(String attributeName) {
		this.attributeName = attributeName;
	}

	/**
	 * Returns a new Property instance with the given value.
	 *
	 * @param value the value to set
	 * @return a new Property instance
	 */
	public UpdateComponentConfigRequest.Property asProperty(String value) {
		return new UpdateComponentConfigRequest.Property(this.attributeName, value);
	}

	/**
	 * Returns a new Property instance with the given value.
	 *
	 * @param value the value to set
	 * @return a new Property instance
	 */
	public UpdateComponentConfigRequest.Property asProperty(long value) {
		return new UpdateComponentConfigRequest.Property(this.attributeName, "" + value);
	}

	/**
	 * Returns a new Property instance with the given value.
	 *
	 * @param value the value to set
	 * @return a new Property instance
	 */
	public UpdateComponentConfigRequest.Property asProperty(int value) {
		return new UpdateComponentConfigRequest.Property(this.attributeName, value);
	}

	/**
	 * Returns a new Property instance with the given value.
	 *
	 * @param value the value to set
	 * @return a new Property instance
	 */
	public UpdateComponentConfigRequest.Property asProperty(boolean value) {
		return new UpdateComponentConfigRequest.Property(this.attributeName, value);
	}

	/**
	 * Returns a new Property instance with the given value.
	 *
	 * @param time the value to set
	 * @return a new Property instance
	 */
	public UpdateComponentConfigRequest.Property asProperty(LocalDateTime time) {
		return new UpdateComponentConfigRequest.Property(this.attributeName, time.toString());
	}

	/**
	 * Parses the given string into a LocalDateTime instance.
	 *
	 * @param time the string to parse
	 * @return a LocalDateTime instance
	 */
	public static LocalDateTime parseLocalDateTime(String time) {
		return LocalDateTime.parse(time);
	}
}
