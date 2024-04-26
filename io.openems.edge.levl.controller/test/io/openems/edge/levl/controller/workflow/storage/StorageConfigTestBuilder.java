package io.openems.edge.levl.controller.workflow.storage;

import java.lang.annotation.Annotation;

public final class StorageConfigTestBuilder {
    private final String configId = "ConfigId";
    private final String alias = "DefaultConfigForTests";
    private final Class<? extends Annotation> annotationType = null;
    private final String webconsoleConfigurationHint = "wrong";
    private final int actualLevlPowerW = 111;
    private final int nextDischargePowerW = 333;
    private final int primaryUsecasActivePowerW = 222;
    private final int nextRequestEfficiencyPercent = 95_44;
    private final int currentRequestEfficiencyPercent = 99_01;
    private final int gridPowerLimitWUpper = 1200;
    private final int gridPowerLimitWLower = -1100;
    private final int levlSocConstraintsUpperLogicalPercent = 95;
    private final int levlSocConstraintsLowerLogicalPercent = 5;
    private final int levlSocConstraintUpperPhysicalPercent = 100;
    private final int levlSocConstraintsLowerPhysicalPercent = 1;
    private final String lastDischargeRequestTimestamp = "2024-02-15 14:45:00Z";
    private final String lastRequestRealizedDischargeEnergyWs = "5";
    private final String currentRequestRealizedDischargeEnergyWs = "4";
    private final String currentRequestRemainingDischargeEnergyWs = "3";
    private final String totalDischargeEnergyWsAtBatteryScaledWithEfficiency = "2";
    private final String totalRealizedDischargeEnergyWs = "1";
    private final boolean nextDischargeRequestActive = false;
    private String nextDischargeRequestDeadline = "2021-01-01T00:00:08";
    private String nextDischargeRequestStart = "2021-01-01T00:00:07";
    private final String nextDischargeRequestEnergyWs = "6";
    private final String nextDischargeRequestTimestamp = "2024-02-15 15:15:00Z";
    private final String nextDischargeRequestId = "id1";
    private final boolean currentDischargeRequestActive = true;
    private String currentDischargeRequestDeadline = "2021-01-01T00:00:11";
    private String currentDischargeRequestStart = "2021-01-01T00:00:10";
    private final String currentDischargeRequestEnergyWs = "9";
    private final String currentDischargeRequestTimestamp = "2024-02-15 15:00:00Z";
    private final String currentDischargeRequestId = "id0";
    private final boolean enabled = true;


    private StorageConfigTestBuilder() {
    }

    /**
     * Returns a new instance of the StorageConfigTestBuilder with default values.
     ** @return a new instance of StorageConfigTestBuilder
     */
    public static StorageConfigTestBuilder aDefaultStorageConfig() {
        return new StorageConfigTestBuilder();
    }

    /**
     * Sets the nextDischargeRequestDeadline property of the WorkflowStorageConfig.
     *
     * @param nextDischargeRequestDeadline the next discharge request deadline
     * @return this builder
     */
    public StorageConfigTestBuilder withNextDischargeRequestDeadline(String nextDischargeRequestDeadline) {
        this.nextDischargeRequestDeadline = nextDischargeRequestDeadline;
        return this;
    }

    /**
     * Sets the nextDischargeRequestStart property of the WorkflowStorageConfig.
     *
     * @param nextDischargeRequestStart the start time of the next discharge request
     * @return this builder
     */
    public StorageConfigTestBuilder withNextDischargeRequestStart(String nextDischargeRequestStart) {
        this.nextDischargeRequestStart = nextDischargeRequestStart;
        return this;
    }

    /**
     * Sets the currentDischargeRequestDeadline property of the WorkflowStorageConfig.
     *
     * @param currentDischargeRequestDeadline the current discharge request deadline
     * @return this builder
     */
    public StorageConfigTestBuilder withCurrentDischargeRequestDeadline(String currentDischargeRequestDeadline) {
        this.currentDischargeRequestDeadline = currentDischargeRequestDeadline;
        return this;
    }

    /**
     * Sets the currentDischargeRequestStart property of the WorkflowStorageConfig.
     *
     * @param currentDischargeRequestStart the start time of the current discharge request
     * @return this builder
     */
    public StorageConfigTestBuilder withCurrentDischargeRequestStart(String currentDischargeRequestStart) {
        this.currentDischargeRequestStart = currentDischargeRequestStart;
        return this;
    }

    public WorkflowStorageConfig build() {
        WorkflowStorageConfig workflowStorageConfig = new WorkflowStorageConfig();
        workflowStorageConfig.setConfigId(this.configId);
        workflowStorageConfig.setAlias(this.alias);
        workflowStorageConfig.setAnnotationType(this.annotationType);
        workflowStorageConfig.setWebconsoleConfigurationHint(this.webconsoleConfigurationHint);
        workflowStorageConfig.setActualLevlPowerW(this.actualLevlPowerW);
        workflowStorageConfig.setNextDischargePowerW(this.nextDischargePowerW);
        workflowStorageConfig.setPrimaryUsecasActivePowerW(this.primaryUsecasActivePowerW);
        workflowStorageConfig.setNextRequestEfficiencyPercent(this.nextRequestEfficiencyPercent);
        workflowStorageConfig.setCurrentRequestEfficiencyPercent(this.currentRequestEfficiencyPercent);
        workflowStorageConfig.setGridPowerLimitWUpper(this.gridPowerLimitWUpper);
        workflowStorageConfig.setGridPowerLimitWLower(this.gridPowerLimitWLower);
        workflowStorageConfig.setLevlSocConstraintsUpperLogicalPercent(this.levlSocConstraintsUpperLogicalPercent);
        workflowStorageConfig.setLevlSocConstraintsLowerLogicalPercent(this.levlSocConstraintsLowerLogicalPercent);
        workflowStorageConfig.setLevlSocConstraintUpperPhysicalPercent(this.levlSocConstraintUpperPhysicalPercent);
        workflowStorageConfig.setLevlSocConstraintsLowerPhysicalPercent(this.levlSocConstraintsLowerPhysicalPercent);
        workflowStorageConfig.setLastDischargeRequestTimestamp(this.lastDischargeRequestTimestamp);
        workflowStorageConfig.setLastRequestRealizedDischargeEnergyWs(this.lastRequestRealizedDischargeEnergyWs);
        workflowStorageConfig.setCurrentRequestRealizedDischargeEnergyWs(this.currentRequestRealizedDischargeEnergyWs);
        workflowStorageConfig.setCurrentRequestRemainingDischargeEnergyWs(this.currentRequestRemainingDischargeEnergyWs);
        workflowStorageConfig.setTotalDischargeEnergyWsAtBatteryScaledWithEfficiency(this.totalDischargeEnergyWsAtBatteryScaledWithEfficiency);
        workflowStorageConfig.setTotalRealizedDischargeEnergyWs(this.totalRealizedDischargeEnergyWs);
        workflowStorageConfig.setNextDischargeRequestActive(this.nextDischargeRequestActive);
        workflowStorageConfig.setNextDischargeRequestDeadline(this.nextDischargeRequestDeadline);
        workflowStorageConfig.setNextDischargeRequestStart(this.nextDischargeRequestStart);
        workflowStorageConfig.setNextDischargeRequestEnergyWs(this.nextDischargeRequestEnergyWs);
        workflowStorageConfig.setNextDischargeRequestTimestamp(this.nextDischargeRequestTimestamp);
        workflowStorageConfig.setNextDischargeRequestId(this.nextDischargeRequestId);
        workflowStorageConfig.setCurrentDischargeRequestActive(this.currentDischargeRequestActive);
        workflowStorageConfig.setCurrentDischargeRequestDeadline(this.currentDischargeRequestDeadline);
        workflowStorageConfig.setCurrentDischargeRequestStart(this.currentDischargeRequestStart);
        workflowStorageConfig.setCurrentDischargeRequestEnergyWs(this.currentDischargeRequestEnergyWs);
        workflowStorageConfig.setCurrentDischargeRequestTimestamp(this.currentDischargeRequestTimestamp);
        workflowStorageConfig.setCurrentDischargeRequestId(this.currentDischargeRequestId);
        workflowStorageConfig.setEnabled(this.enabled);
        return workflowStorageConfig;
    }
}
