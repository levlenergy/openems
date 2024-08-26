package io.openems.edge.levl.controller.workflow;

import io.openems.edge.common.channel.value.Value;
import io.openems.edge.levl.controller.controllers.common.Limit;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LevlWorkflowState {

    protected Clock clock = Clock.systemDefaultZone();
    protected LevlPowerCalculator calculator = new LevlPowerCalculator();
    protected DischargeState dischargeState = new DischargeState();
    protected LevlSocConstraints levlSocConstraints = new LevlSocConstraints(0, 100, 0, 100);
    protected Limit gridPowerLimitW = new Limit(0, 0);
    protected int primaryUseCaseActivePowerW;
    protected int nextDischargePowerW;
    protected int actualLevlPowerW;
    
    private final Logger log = LoggerFactory.getLogger(LevlWorkflowState.class);

    record LevlWorkflowStateMemento(int primaryUseCaseActivePowerW, int nextDischargePowerW, int actualLevlPowerW,
                                    DischargeState.DischargeStateMemento state,
                                    LevlSocConstraints.LevlSocConstraintsMemento levlSocConstraints,
                                    Limit.LimitMemento gridPowerLimitW) {
    }

    /**
     * Saves the current state of the Levl workflow.
     *
     * @return a memento of the current state
     */
    public LevlWorkflowStateMemento save() {
        return new LevlWorkflowStateMemento(this.primaryUseCaseActivePowerW, this.nextDischargePowerW, this.actualLevlPowerW, this.dischargeState.save(), this.levlSocConstraints.save(), this.gridPowerLimitW.save());
    }

    /**
     * Initializes the state after it has been restored.
     */
    public void initAfterRestore() {
        this.dischargeState.initAfterRestore(LocalDateTime.now(this.clock));
    }

    public void setPrimaryUseCaseActivePowerW(int originalActivePowerW) {
        this.primaryUseCaseActivePowerW = originalActivePowerW;
    }

    public int getPrimaryUseCaseActivePowerW() {
        return this.primaryUseCaseActivePowerW;
    }

    public String getLastCompletedRequestTimestamp() {
        return this.dischargeState.getLastCompletedRequestTimestamp();
    }

    public int getActualLevlPowerW() {
        return this.actualLevlPowerW;
    }

    public int getLastCompletedRequestDischargePowerW() {
        return this.dischargeState.getLastCompletedRequestDischargePowerW();
    }

    public int getNextDischargePowerW() {
        return this.nextDischargePowerW;
    }
    
    public boolean isInfluenceSellToGridAllowed() {
    	return this.dischargeState.isInfluenceSellToGridAllowed();
    }

    /**
     * Determines the constraints for the Levl use case.
     *
     * @param meterActivePowerW the active power of the meter in watts
     * @param essSoc the state of charge of the energy storage system
     * @return the constraints
     */
    public Limit getLevlUseCaseConstraints(Value<Integer> meterActivePowerW, Value<Integer> essSoc) {
        var gridConstraints = this.determineShiftedGridConstraints(meterActivePowerW);
        var socConstraints = this.levlSocConstraints.determineLevlUseCaseSocConstraints(essSoc);
        return gridConstraints.intersect(socConstraints);
    }

    /**
     * Determines the shifted grid constraints.
     *
     * @param meterActivePowerW the active power of the meter in watts
     * @return the shifted grid constraints
     */
    private Limit determineShiftedGridConstraints(Value<Integer> meterActivePowerW) {
        Limit gridConstraints = this.gridPowerLimitW.invert();
        if (meterActivePowerW.isDefined()) {
            return gridConstraints.shiftBy(meterActivePowerW.get() + this.actualLevlPowerW).ensureValidLimitWithZero();
        }
        return gridConstraints;
    }

    /**
     * Determines the levlPowerW by subtracting the calculated primaryUseCaseActivePowerW from the overall realized power (actualPowerW).
     * Updating the discharge state with the calculated levl power.
     *
     * @param actualPowerW the actual power in watts
     */
    void checkActualDischargePower(Optional<Integer> actualPowerW) {
        this.actualLevlPowerW = this.calculator.determineActualLevlPowerW(actualPowerW, this.primaryUseCaseActivePowerW);
        this.dischargeState.handleRealizedDischargePowerWForOneSecond(this.actualLevlPowerW);
    }

    /**
     * Determines the next discharge power.
     */
    void determineNextDischargePower() {
        this.nextDischargePowerW = this.calculator.determineNextDischargePowerW(this.dischargeState.getCurrentRequestRemainingDischargePowerWs());
        this.log.debug("Next discharge power W " + this.nextDischargePowerW);
    }

    /**
     * Updates the state.
     */
    public void updateState() {
        this.dischargeState.update(LocalDateTime.now(this.clock));
    }

    /**
     * Determines the constraints for the primary use case.
     *
     * @param essSoc the state of charge of the energy storage system
     * @param essCapacity the capacity of the energy storage system
     * @param minPower the minimum power in watts
     * @param maxPower the maximum power in watts
     * @return the constraints
     */
    public Limit determinePrimaryUseCaseConstraints(Value<Integer> essSoc, Value<Integer> essCapacity, int minPower, int maxPower) {
        var openemsLimit = new Limit(minPower, maxPower);
        var socLimit = this.levlSocConstraints.determineLimitFromPhysicalSocConstraintAndLevlSocOffset(
                this.dischargeState.getTotalDischargeEnergyWsAtBatteryScaledWithEfficiency(),
                essSoc,
                essCapacity
        );
        this.log.debug("socLimit for PUC: " + socLimit);
        return openemsLimit.intersect(socLimit);
    }

    /**
     * Handles a control request.
     *
     * @param request the control request
     * @param physicalSocLowerPercent the lower limit for the physical state of charge in percent
     * @param physicalSocUpperPercent the upper limit for the physical state of charge in percent
     */
    public void handleRequest(LevlControlRequest request, int physicalSocLowerPercent, int physicalSocUpperPercent) {
        this.gridPowerLimitW = request.createGridPowerLimitW();
        this.levlSocConstraints = request.createLevlSocConstraints(physicalSocLowerPercent, physicalSocUpperPercent);
        this.dischargeState.handleReceivedRequest(request.getEfficiencyPercent(), request.createDischargeRequest(LocalDateTime.now(this.clock)));
    }

    /**
     * Restores the state from a memento.
     *
     * @param memento the memento to restore from
     */
    void restore(LevlWorkflowStateMemento memento) {
        this.primaryUseCaseActivePowerW = memento.primaryUseCaseActivePowerW;
        this.nextDischargePowerW = memento.nextDischargePowerW;
        this.actualLevlPowerW = memento.actualLevlPowerW;
        this.dischargeState = DischargeState.restore(memento.state);
        this.levlSocConstraints = LevlSocConstraints.restore(memento.levlSocConstraints);
        this.gridPowerLimitW = Limit.restore(memento.gridPowerLimitW);
    }
}