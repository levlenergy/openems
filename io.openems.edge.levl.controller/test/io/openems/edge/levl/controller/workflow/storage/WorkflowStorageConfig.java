package io.openems.edge.levl.controller.workflow.storage;

import java.lang.annotation.Annotation;

public class WorkflowStorageConfig implements Config {
    private String configId;
    private String alias;
    private Class<? extends Annotation> annotationType;
    private String webconsoleConfigurationHint;
    private int actualLevlPowerW;
    private int nextDischargePowerW;
    private int primaryUsecasActivePowerW;
    private int nextRequestEfficiencyPercent;
    private int currentRequestEfficiencyPercent;
    private int gridPowerLimitWUpper;
    private int gridPowerLimitWLower;
    private int levlSocConstraintsUpperLogicalPercent;
    private int levlSocConstraintsLowerLogicalPercent;
    private int levlSocConstraintUpperPhysicalPercent;
    private int levlSocConstraintsLowerPhysicalPercent;
    private String lastDischargeRequestTimestamp;
    private String lastRequestRealizedDischargeEnergyWs;
    private String currentRequestRealizedDischargeEnergyWs;
    private String currentRequestRemainingDischargeEnergyWs;
    private String totalDischargeEnergyWsAtBatteryScaledWithEfficiency;
    private String totalRealizedDischargeEnergyWs;
    private boolean nextInfluenceSellToGrid;
    private boolean nextDischargeRequestActive;
    private String nextDischargeRequestDeadline;
    private String nextDischargeRequestStart;
    private String nextDischargeRequestEnergyWs;
    private String nextDischargeRequestTimestamp;
    private String nextDischargeRequestId;
    private boolean currentInfluenceSellToGrid;
    private boolean currentDischargeRequestActive;
    private String currentDischargeRequestDeadline;
    private String currentDischargeRequestStart;
    private String currentDischargeRequestEnergyWs;
    private String currentDischargeRequestTimestamp;
    private String currentDischargeRequestId;
    private boolean enabled;


    @Override
    public String id() {
        return this.configId;
    }

    @Override
    public String alias() {
        return this.alias;
    }

    @Override
    public boolean enabled() {
        return this.enabled;
    }

    @Override
    public String current_discharge_request_id() {
        return this.currentDischargeRequestId;
    }

    @Override
    public String current_discharge_request_timestamp() {
        return this.currentDischargeRequestTimestamp;
    }

    @Override
    public String current_discharge_request_energy_ws() {
        return this.currentDischargeRequestEnergyWs;
    }

    @Override
    public String current_discharge_request_start() {
        return this.currentDischargeRequestStart;
    }

    @Override
    public String current_discharge_request_deadline() {
        return this.currentDischargeRequestDeadline;
    }

    @Override
    public boolean current_discharge_request_active() {
        return this.currentDischargeRequestActive;
    }

    @Override
    public String next_discharge_request_id() {
        return this.nextDischargeRequestId;
    }

    @Override
    public String next_discharge_request_timestamp() {
        return this.nextDischargeRequestTimestamp;
    }

    @Override
    public String next_discharge_request_energy_ws() {
        return this.nextDischargeRequestEnergyWs;
    }

    @Override
    public String next_discharge_request_start() {
        return this.nextDischargeRequestStart;
    }

    @Override
    public String next_discharge_request_deadline() {
        return this.nextDischargeRequestDeadline;
    }

    @Override
    public boolean next_discharge_request_active() {
        return this.nextDischargeRequestActive;
    }

    @Override
    public String total_realized_discharge_energy_ws() {
        return this.totalRealizedDischargeEnergyWs;
    }

    @Override
    public String total_discharge_energy_ws_at_battery_scaled_with_efficiency() {
        return this.totalDischargeEnergyWsAtBatteryScaledWithEfficiency;
    }

    @Override
    public String current_request_remaining_discharge_energy_ws() {
        return this.currentRequestRemainingDischargeEnergyWs;
    }

    @Override
    public String current_request_realized_discharge_energy_ws() {
        return this.currentRequestRealizedDischargeEnergyWs;
    }

    @Override
    public String last_request_realized_discharge_energy_ws() {
        return this.lastRequestRealizedDischargeEnergyWs;
    }

    @Override
    public String last_discharge_request_timestamp() {
        return this.lastDischargeRequestTimestamp;
    }

    @Override
    public int levl_soc_constraints_lower_physical_percent() {
        return this.levlSocConstraintsLowerPhysicalPercent;
    }

    @Override
    public int levl_soc_constraints_upper_physical_percent() {
        return this.levlSocConstraintUpperPhysicalPercent;
    }

    @Override
    public int levl_soc_constraints_lower_logical_percent() {
        return this.levlSocConstraintsLowerLogicalPercent;
    }

    @Override
    public int levl_soc_constraints_upper_logical_percent() {
        return this.levlSocConstraintsUpperLogicalPercent;
    }

    @Override
    public int grid_power_limit_w_lower() {
        return this.gridPowerLimitWLower;
    }

    @Override
    public int grid_power_limit_w_upper() {
        return this.gridPowerLimitWUpper;
    }

    @Override
    public int current_request_efficiency_percent_multiplied_by_hundred() {
        return this.currentRequestEfficiencyPercent;
    }

    @Override
    public int next_request_efficiency_percent_multiplied_by_hundred() {
        return this.nextRequestEfficiencyPercent;
    }

    @Override
    public int primary_use_case_active_power_w() {
        return this.primaryUsecasActivePowerW;
    }

    @Override
    public int next_discharge_power_w() {
        return this.nextDischargePowerW;
    }

    @Override
    public int actual_levl_power_w() {
        return this.actualLevlPowerW;
    }
    
    @Override
	public boolean next_influence_sell_to_grid() {
		return this.nextInfluenceSellToGrid;
	}
	
    @Override
	public boolean current_influence_sell_to_grid() {
		return this.currentInfluenceSellToGrid;
	}

    @Override
    public String webconsole_configurationFactory_nameHint() {
        return this.webconsoleConfigurationHint;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return this.annotationType;
    }

    public void setConfigId(String configId) {
        this.configId = configId;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public void setAnnotationType(Class<? extends Annotation> annotationType) {
        this.annotationType = annotationType;
    }

    public void setWebconsoleConfigurationHint(String webconsoleConfigurationHint) {
        this.webconsoleConfigurationHint = webconsoleConfigurationHint;
    }

    public void setActualLevlPowerW(int actualLevlPowerW) {
        this.actualLevlPowerW = actualLevlPowerW;
    }

    public void setNextDischargePowerW(int nextDischargePowerW) {
        this.nextDischargePowerW = nextDischargePowerW;
    }

    public void setPrimaryUsecasActivePowerW(int primaryUsecasActivePowerW) {
        this.primaryUsecasActivePowerW = primaryUsecasActivePowerW;
    }

    public void setNextRequestEfficiencyPercent(int nextRequestEfficiencyPercent) {
        this.nextRequestEfficiencyPercent = nextRequestEfficiencyPercent;
    }

    public void setCurrentRequestEfficiencyPercent(int currentRequestEfficiencyPercent) {
        this.currentRequestEfficiencyPercent = currentRequestEfficiencyPercent;
    }

    public void setGridPowerLimitWUpper(int gridPowerLimitWUpper) {
        this.gridPowerLimitWUpper = gridPowerLimitWUpper;
    }

    public void setGridPowerLimitWLower(int gridPowerLimitWLower) {
        this.gridPowerLimitWLower = gridPowerLimitWLower;
    }

    public void setLevlSocConstraintsUpperLogicalPercent(int levlSocConstraintsUpperLogicalPercent) {
        this.levlSocConstraintsUpperLogicalPercent = levlSocConstraintsUpperLogicalPercent;
    }

    public void setLevlSocConstraintsLowerLogicalPercent(int levlSocConstraintsLowerLogicalPercent) {
        this.levlSocConstraintsLowerLogicalPercent = levlSocConstraintsLowerLogicalPercent;
    }

    public void setLevlSocConstraintUpperPhysicalPercent(int levlSocConstraintUpperPhysicalPercent) {
        this.levlSocConstraintUpperPhysicalPercent = levlSocConstraintUpperPhysicalPercent;
    }

    public void setLevlSocConstraintsLowerPhysicalPercent(int levlSocConstraintsLowerPhysicalPercent) {
        this.levlSocConstraintsLowerPhysicalPercent = levlSocConstraintsLowerPhysicalPercent;
    }

    public void setLastDischargeRequestTimestamp(String lastDischargeRequestTimestamp) {
        this.lastDischargeRequestTimestamp = lastDischargeRequestTimestamp;
    }

    public void setLastRequestRealizedDischargeEnergyWs(String lastRequestRealizedDischargeEnergyWs) {
        this.lastRequestRealizedDischargeEnergyWs = lastRequestRealizedDischargeEnergyWs;
    }

    public void setCurrentRequestRealizedDischargeEnergyWs(String currentRequestRealizedDischargeEnergyWs) {
        this.currentRequestRealizedDischargeEnergyWs = currentRequestRealizedDischargeEnergyWs;
    }

    public void setCurrentRequestRemainingDischargeEnergyWs(String currentRequestRemainingDischargeEnergyWs) {
        this.currentRequestRemainingDischargeEnergyWs = currentRequestRemainingDischargeEnergyWs;
    }

    public void setTotalDischargeEnergyWsAtBatteryScaledWithEfficiency(String totalDischargeEnergyWsAtBatteryScaledWithEfficiency) {
        this.totalDischargeEnergyWsAtBatteryScaledWithEfficiency = totalDischargeEnergyWsAtBatteryScaledWithEfficiency;
    }

    public void setTotalRealizedDischargeEnergyWs(String totalRealizedDischargeEnergyWs) {
        this.totalRealizedDischargeEnergyWs = totalRealizedDischargeEnergyWs;
    }

    public void setNextDischargeRequestActive(boolean nextDischargeRequestActive) {
        this.nextDischargeRequestActive = nextDischargeRequestActive;
    }

    public void setNextDischargeRequestDeadline(String nextDischargeRequestDeadline) {
        this.nextDischargeRequestDeadline = nextDischargeRequestDeadline;
    }

    public void setNextDischargeRequestStart(String nextDischargeRequestStart) {
        this.nextDischargeRequestStart = nextDischargeRequestStart;
    }

    public void setNextDischargeRequestEnergyWs(String nextDischargeRequestEnergyWs) {
        this.nextDischargeRequestEnergyWs = nextDischargeRequestEnergyWs;
    }

    public void setNextDischargeRequestTimestamp(String nextDischargeRequestTimestamp) {
        this.nextDischargeRequestTimestamp = nextDischargeRequestTimestamp;
    }

    public void setNextDischargeRequestId(String nextDischargeRequestId) {
        this.nextDischargeRequestId = nextDischargeRequestId;
    }

    public void setCurrentDischargeRequestActive(boolean currentDischargeRequestActive) {
        this.currentDischargeRequestActive = currentDischargeRequestActive;
    }

    public void setCurrentDischargeRequestDeadline(String currentDischargeRequestDeadline) {
        this.currentDischargeRequestDeadline = currentDischargeRequestDeadline;
    }

    public void setCurrentDischargeRequestStart(String currentDischargeRequestStart) {
        this.currentDischargeRequestStart = currentDischargeRequestStart;
    }

    public void setCurrentDischargeRequestEnergyWs(String currentDischargeRequestEnergyWs) {
        this.currentDischargeRequestEnergyWs = currentDischargeRequestEnergyWs;
    }

    public void setCurrentDischargeRequestTimestamp(String currentDischargeRequestTimestamp) {
        this.currentDischargeRequestTimestamp = currentDischargeRequestTimestamp;
    }

    public void setCurrentDischargeRequestId(String currentDischargeRequestId) {
        this.currentDischargeRequestId = currentDischargeRequestId;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

	public void setCurrentInfluenceSellToGrid(boolean currentInfluenceSellToGrid) {
		this.currentInfluenceSellToGrid = currentInfluenceSellToGrid;
	}

	public void setNextInfluenceSellToGrid(boolean nextInfluenceSellToGrid) {
		this.nextInfluenceSellToGrid = nextInfluenceSellToGrid;
	}
}