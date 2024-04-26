package io.openems.edge.levl.controller.workflow;

import java.math.BigDecimal;

public final class DischargeStateTestBuilder {
    private long totalRealizedDischargeEnergyWs;
    private long totalDischargeEnergyWsAtBatteryScaledWithEfficiency;
    private long currentRequestRemainingDischargeEnergyWs;
    private long currentRequestRealizedDischargeEnergyWs;
    private long lastCompletedRequestRealizedDischargeEnergyWs;
    private BigDecimal currentEfficiencyPercent;
    private BigDecimal nextRequestEfficiencyPercent;
    private String lastCompletedRequestTimestamp;
    private DischargeRequest request;
    private DischargeRequest nextRequest;

    private DischargeStateTestBuilder() {
    }

    public static DischargeStateTestBuilder aDischargeState() {
        return new DischargeStateTestBuilder();
    }

	public static DischargeStateTestBuilder Default() {
        return DischargeStateTestBuilder.aDischargeState()
                .withTotalRealizedDischargeEnergyWs(10000)
                .withTotalDischargeEnergyWsAtBatteryScaledWithEfficiency(9000)
                .withCurrentRequestRemainingDischargeEnergyWs(-500)
                .withCurrentRequestRealizedDischargeEnergyWs(-400)
                .withLastCompletedRequestRealizedDischargeEnergyWs(800)
                .withCurrentEfficiencyPercent(BigDecimal.valueOf(100))
                .withNextRequestEfficiencyPercent(BigDecimal.valueOf(100))
                .withLastCompletedRequestTimestamp("DEFAULT")
                .withRequest(DischargeRequest.inactiveRequest())
                .withNextRequest(DischargeRequest.inactiveRequest());
    }

    public DischargeStateTestBuilder withTotalRealizedDischargeEnergyWs(long totalRealizedDischargeEnergyWs) {
        this.totalRealizedDischargeEnergyWs = totalRealizedDischargeEnergyWs;
        return this;
    }

    public DischargeStateTestBuilder withTotalDischargeEnergyWsAtBatteryScaledWithEfficiency(long totalDischargeEnergyWsAtBatteryScaledWithEfficiency) {
        this.totalDischargeEnergyWsAtBatteryScaledWithEfficiency = totalDischargeEnergyWsAtBatteryScaledWithEfficiency;
        return this;
    }

    public DischargeStateTestBuilder withCurrentRequestRemainingDischargeEnergyWs(long currentRequestRemainingDischargeEnergyWs) {
        this.currentRequestRemainingDischargeEnergyWs = currentRequestRemainingDischargeEnergyWs;
        return this;
    }

    public DischargeStateTestBuilder withCurrentRequestRealizedDischargeEnergyWs(long currentRequestRealizedDischargeEnergyWs) {
        this.currentRequestRealizedDischargeEnergyWs = currentRequestRealizedDischargeEnergyWs;
        return this;
    }

    public DischargeStateTestBuilder withLastCompletedRequestRealizedDischargeEnergyWs(long lastCompletedRequestRealizedDischargeEnergyWs) {
        this.lastCompletedRequestRealizedDischargeEnergyWs = lastCompletedRequestRealizedDischargeEnergyWs;
        return this;
    }

    public DischargeStateTestBuilder withCurrentEfficiencyPercent(BigDecimal currentEfficiencyPercent) {
        this.currentEfficiencyPercent = currentEfficiencyPercent;
        return this;
    }

    public DischargeStateTestBuilder withNextRequestEfficiencyPercent(BigDecimal nextRequestEfficiencyPercent) {
        this.nextRequestEfficiencyPercent = nextRequestEfficiencyPercent;
        return this;
    }

    public DischargeStateTestBuilder withLastCompletedRequestTimestamp(String lastCompletedRequestTimestamp) {
        this.lastCompletedRequestTimestamp = lastCompletedRequestTimestamp;
        return this;
    }

    public DischargeStateTestBuilder withRequest(DischargeRequest request) {
        this.request = request;
        return this;
    }

    public DischargeStateTestBuilder withNextRequest(DischargeRequest nextRequest) {
        this.nextRequest = nextRequest;
        return this;
    }

    public DischargeState build() {
        return new DischargeState(this.totalRealizedDischargeEnergyWs,
        		this.totalDischargeEnergyWsAtBatteryScaledWithEfficiency,
        		this.currentRequestRemainingDischargeEnergyWs,
        		this.currentRequestRealizedDischargeEnergyWs,
        		this.lastCompletedRequestRealizedDischargeEnergyWs,
        		this.currentEfficiencyPercent,
        		this.nextRequestEfficiencyPercent,
        		this.lastCompletedRequestTimestamp,
        		this.request,
        		this.nextRequest);
    }
}
