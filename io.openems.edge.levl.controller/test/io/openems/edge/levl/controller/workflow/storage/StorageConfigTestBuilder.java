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

    public static StorageConfigTestBuilder aDefaultStorageConfig() {
        return new StorageConfigTestBuilder();
    }


    public StorageConfigTestBuilder withNextDischargeRequestDeadline(String nextDischargeRequestDeadline) {
        this.nextDischargeRequestDeadline = nextDischargeRequestDeadline;
        return this;
    }

    public StorageConfigTestBuilder withNextDischargeRequestStart(String nextDischargeRequestStart) {
        this.nextDischargeRequestStart = nextDischargeRequestStart;
        return this;
    }

    public StorageConfigTestBuilder withCurrentDischargeRequestDeadline(String currentDischargeRequestDeadline) {
        this.currentDischargeRequestDeadline = currentDischargeRequestDeadline;
        return this;
    }

    public StorageConfigTestBuilder withCurrentDischargeRequestStart(String currentDischargeRequestStart) {
        this.currentDischargeRequestStart = currentDischargeRequestStart;
        return this;
    }

    public WorkflowStorageConfig build() {
        WorkflowStorageConfig workflowStorageConfig = new WorkflowStorageConfig();
        workflowStorageConfig.setConfigId(configId);
        workflowStorageConfig.setAlias(alias);
        workflowStorageConfig.setAnnotationType(annotationType);
        workflowStorageConfig.setWebconsoleConfigurationHint(webconsoleConfigurationHint);
        workflowStorageConfig.setActualLevlPowerW(actualLevlPowerW);
        workflowStorageConfig.setNextDischargePowerW(nextDischargePowerW);
        workflowStorageConfig.setPrimaryUsecasActivePowerW(primaryUsecasActivePowerW);
        workflowStorageConfig.setNextRequestEfficiencyPercent(nextRequestEfficiencyPercent);
        workflowStorageConfig.setCurrentRequestEfficiencyPercent(currentRequestEfficiencyPercent);
        workflowStorageConfig.setGridPowerLimitWUpper(gridPowerLimitWUpper);
        workflowStorageConfig.setGridPowerLimitWLower(gridPowerLimitWLower);
        workflowStorageConfig.setLevlSocConstraintsUpperLogicalPercent(levlSocConstraintsUpperLogicalPercent);
        workflowStorageConfig.setLevlSocConstraintsLowerLogicalPercent(levlSocConstraintsLowerLogicalPercent);
        workflowStorageConfig.setLevlSocConstraintUpperPhysicalPercent(levlSocConstraintUpperPhysicalPercent);
        workflowStorageConfig.setLevlSocConstraintsLowerPhysicalPercent(levlSocConstraintsLowerPhysicalPercent);
        workflowStorageConfig.setLastDischargeRequestTimestamp(lastDischargeRequestTimestamp);
        workflowStorageConfig.setLastRequestRealizedDischargeEnergyWs(lastRequestRealizedDischargeEnergyWs);
        workflowStorageConfig.setCurrentRequestRealizedDischargeEnergyWs(currentRequestRealizedDischargeEnergyWs);
        workflowStorageConfig.setCurrentRequestRemainingDischargeEnergyWs(currentRequestRemainingDischargeEnergyWs);
        workflowStorageConfig.setTotalDischargeEnergyWsAtBatteryScaledWithEfficiency(totalDischargeEnergyWsAtBatteryScaledWithEfficiency);
        workflowStorageConfig.setTotalRealizedDischargeEnergyWs(totalRealizedDischargeEnergyWs);
        workflowStorageConfig.setNextDischargeRequestActive(nextDischargeRequestActive);
        workflowStorageConfig.setNextDischargeRequestDeadline(nextDischargeRequestDeadline);
        workflowStorageConfig.setNextDischargeRequestStart(nextDischargeRequestStart);
        workflowStorageConfig.setNextDischargeRequestEnergyWs(nextDischargeRequestEnergyWs);
        workflowStorageConfig.setNextDischargeRequestTimestamp(nextDischargeRequestTimestamp);
        workflowStorageConfig.setNextDischargeRequestId(nextDischargeRequestId);
        workflowStorageConfig.setCurrentDischargeRequestActive(currentDischargeRequestActive);
        workflowStorageConfig.setCurrentDischargeRequestDeadline(currentDischargeRequestDeadline);
        workflowStorageConfig.setCurrentDischargeRequestStart(currentDischargeRequestStart);
        workflowStorageConfig.setCurrentDischargeRequestEnergyWs(currentDischargeRequestEnergyWs);
        workflowStorageConfig.setCurrentDischargeRequestTimestamp(currentDischargeRequestTimestamp);
        workflowStorageConfig.setCurrentDischargeRequestId(currentDischargeRequestId);
        workflowStorageConfig.setEnabled(enabled);
        return workflowStorageConfig;
    }
}
