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
    private boolean influenceSellToGrid;
    private DischargeRequest request;
    private DischargeRequest nextRequest;

    private DischargeStateTestBuilder() {
    }

    /**
     * Creates a new instance of DischargeStateTestBuilder.
     *
     * @return a new instance of DischargeStateTestBuilder
     */
    public static DischargeStateTestBuilder aDischargeState() {
        return new DischargeStateTestBuilder();
    }

    /**
     * Creates a new instance of DischargeStateTestBuilder with default values.
     *
     * @return a new instance of DischargeStateTestBuilder with default values
     */
    public static DischargeStateTestBuilder defaultInstance() {
        return DischargeStateTestBuilder.aDischargeState()
                .withTotalRealizedDischargeEnergyWs(10000)
                .withTotalDischargeEnergyWsAtBatteryScaledWithEfficiency(9000)
                .withCurrentRequestRemainingDischargeEnergyWs(-500)
                .withCurrentRequestRealizedDischargeEnergyWs(-400)
                .withLastCompletedRequestRealizedDischargeEnergyWs(800)
                .withCurrentEfficiencyPercent(BigDecimal.valueOf(100))
                .withNextRequestEfficiencyPercent(BigDecimal.valueOf(100))
                .withLastCompletedRequestTimestamp("DEFAULT")
                .withInfluenceSellToGrid(false)
                .withRequest(DischargeRequest.inactiveRequest())
                .withNextRequest(DischargeRequest.inactiveRequest());
    }

    /**
     * Sets the total realized discharge energy in watt-seconds.
     *
     * @param totalRealizedDischargeEnergyWs the total realized discharge energy in watt-seconds
     * @return this DischargeStateTestBuilder
     */
    public DischargeStateTestBuilder withTotalRealizedDischargeEnergyWs(long totalRealizedDischargeEnergyWs) {
        this.totalRealizedDischargeEnergyWs = totalRealizedDischargeEnergyWs;
        return this;
    }

    /**
     * Sets the total discharge energy at battery scaled with efficiency.
     *
     * @param totalDischargeEnergyWsAtBatteryScaledWithEfficiency the total discharge energy at battery scaled with efficiency
     * @return this DischargeStateTestBuilder
     */
    public DischargeStateTestBuilder withTotalDischargeEnergyWsAtBatteryScaledWithEfficiency(long totalDischargeEnergyWsAtBatteryScaledWithEfficiency) {
        this.totalDischargeEnergyWsAtBatteryScaledWithEfficiency = totalDischargeEnergyWsAtBatteryScaledWithEfficiency;
        return this;
    }

    /**
     * Sets the current request remaining discharge energy in watt-seconds.
     *
     * @param currentRequestRemainingDischargeEnergyWs the current request remaining discharge energy in watt-seconds
     * @return this DischargeStateTestBuilder
     */
    public DischargeStateTestBuilder withCurrentRequestRemainingDischargeEnergyWs(long currentRequestRemainingDischargeEnergyWs) {
        this.currentRequestRemainingDischargeEnergyWs = currentRequestRemainingDischargeEnergyWs;
        return this;
    }

    /**
     * Sets the current request realized discharge energy in watt-seconds.
     *
     * @param currentRequestRealizedDischargeEnergyWs the current request realized discharge energy in watt-seconds
     * @return this DischargeStateTestBuilder
     */
    public DischargeStateTestBuilder withCurrentRequestRealizedDischargeEnergyWs(long currentRequestRealizedDischargeEnergyWs) {
        this.currentRequestRealizedDischargeEnergyWs = currentRequestRealizedDischargeEnergyWs;
        return this;
    }

    /**
     * Sets the last completed request realized discharge energy in watt-seconds.
     *
     * @param lastCompletedRequestRealizedDischargeEnergyWs the last completed request realized discharge energy in watt-seconds
     * @return this DischargeStateTestBuilder
     */
    public DischargeStateTestBuilder withLastCompletedRequestRealizedDischargeEnergyWs(long lastCompletedRequestRealizedDischargeEnergyWs) {
        this.lastCompletedRequestRealizedDischargeEnergyWs = lastCompletedRequestRealizedDischargeEnergyWs;
        return this;
    }

    /**
     * Sets the current efficiency percent.
     *
     * @param currentEfficiencyPercent the current efficiency percent
     * @return this DischargeStateTestBuilder
     */
    public DischargeStateTestBuilder withCurrentEfficiencyPercent(BigDecimal currentEfficiencyPercent) {
        this.currentEfficiencyPercent = currentEfficiencyPercent;
        return this;
    }

    /**
     * Sets the next request efficiency percent.
     *
     * @param nextRequestEfficiencyPercent the next request efficiency percent
     * @return this DischargeStateTestBuilder
     */
    public DischargeStateTestBuilder withNextRequestEfficiencyPercent(BigDecimal nextRequestEfficiencyPercent) {
        this.nextRequestEfficiencyPercent = nextRequestEfficiencyPercent;
        return this;
    }

    /**
     * Sets the last completed request timestamp.
     *
     * @param lastCompletedRequestTimestamp the last completed request timestamp
     * @return this DischargeStateTestBuilder
     */
    public DischargeStateTestBuilder withLastCompletedRequestTimestamp(String lastCompletedRequestTimestamp) {
        this.lastCompletedRequestTimestamp = lastCompletedRequestTimestamp;
        return this;
    }
    
    /**
     * Sets the influence sell to grid boolean.
     *
     * @param influenceSellToGrid whether influence of sell to grid is allowed
     * @return this DischargeStateTestBuilder
     */
    public DischargeStateTestBuilder withInfluenceSellToGrid(boolean influenceSellToGrid) {
        this.influenceSellToGrid = influenceSellToGrid;
        return this;
    }

    /**
     * Sets the request.
     *
     * @param request the request
     * @return this DischargeStateTestBuilder
     */
    public DischargeStateTestBuilder withRequest(DischargeRequest request) {
        this.request = request;
        return this;
    }

    /**
     * Sets the next request.
     *
     * @param nextRequest the next request
     * @return this DischargeStateTestBuilder
     */
    public DischargeStateTestBuilder withNextRequest(DischargeRequest nextRequest) {
        this.nextRequest = nextRequest;
        return this;
    }

    /**
     * Builds a new instance of DischargeState.
     *
     * @return a new instance of DischargeState
     */
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