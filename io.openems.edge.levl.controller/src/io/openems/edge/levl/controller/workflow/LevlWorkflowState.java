package io.openems.edge.levl.controller.workflow;

import io.openems.edge.common.channel.value.Value;
import io.openems.edge.levl.controller.controllers.common.Limit;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;


public class LevlWorkflowState {

	protected Clock clock = Clock.systemDefaultZone();

    protected LevlPowerCalculator calculator = new LevlPowerCalculator();
    protected DischargeState dischargeState = new DischargeState();
    protected LevlSocConstraints levlSocConstraints = new LevlSocConstraints(0, 100, 0, 100);
    protected Limit gridPowerLimitW = new Limit(0, 0);
    protected int primaryUseCaseActivePowerW;
    protected int nextDischargePowerW;
    protected int actualLevlPowerW;

    record LevlWorkflowStateMemento(int primaryUseCaseActivePowerW, int nextDischargePowerW, int actualLevlPowerW,
                                    DischargeState.DischargeStateMemento state,
                                    LevlSocConstraints.LevlSocConstraintsMemento levlSocConstraints,
                                    Limit.LimitMemento gridPowerLimitW) {

    }


    public LevlWorkflowStateMemento save() {
        return new LevlWorkflowStateMemento(this.primaryUseCaseActivePowerW, this.nextDischargePowerW, this.actualLevlPowerW, this.dischargeState.save(), this.levlSocConstraints.save(), this.gridPowerLimitW.save());
    }

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

    public Limit getLevlUseCaseConstraints(Value<Integer> meterActivePowerW, Value<Integer> essSoc) {
        var gridConstraints = this.determineShiftedGridConstraints(meterActivePowerW);
        var socConstraints = this.levlSocConstraints.determineLevlUseCaseSocConstraints(essSoc);
        return gridConstraints.intersect(socConstraints);
    }

    private Limit determineShiftedGridConstraints(Value<Integer> meterActivePowerW) {
        Limit gridConstraints = this.gridPowerLimitW.invert();
        if (meterActivePowerW.isDefined()) {
            return gridConstraints.shiftBy(meterActivePowerW.get() + this.actualLevlPowerW).ensureValidLimitWithZero();
        }
        return gridConstraints;
    }

    void checkActualDischargePower(Optional<Integer> actualPowerW) {
    	this.actualLevlPowerW = this.calculator.determineActualLevlPowerW(actualPowerW, this.primaryUseCaseActivePowerW);
        this.dischargeState.handleRealizedDischargePowerWForOneSecond(this.actualLevlPowerW);
    }

    void determineNextDischargePower() {
    	this.nextDischargePowerW = this.calculator.determineNextDischargePowerW(this.dischargeState.getCurrentRequestRemainingDischargePowerWs());
        System.out.println("*********** next discharge power W " + this.nextDischargePowerW);
    }

    public void updateState() {
    	this.dischargeState.update(LocalDateTime.now(this.clock));
    }

    public Limit determinePrimaryUseCaseConstraints(Value<Integer> essSoc, Value<Integer> essCapacity, int minPower, int maxPower) {
        var openemsLimit = new Limit(minPower, maxPower);
        var socLimit = this.levlSocConstraints.determineLimitFromPhysicalSocConstraintAndLevlSocOffset(
        		this.dischargeState.getTotalDischargeEnergyWsAtBatteryScaledWithEfficiency(),
                essSoc,
                essCapacity
        );
        System.out.println("************ socLimit for PUC: " + socLimit + "\n");
        return openemsLimit.intersect(socLimit);
    }

    public void handleRequest(LevlControlRequest request, int physicalSocLowerPercent, int physicalSocUpperPercent) {
    	this.gridPowerLimitW = request.createGridPowerLimitW();
        this.levlSocConstraints = request.createLevlSocConstraints(physicalSocLowerPercent, physicalSocUpperPercent);
        this.dischargeState.handleReceivedRequest(request.getEfficiencyPercent(), request.createDischargeRequest(LocalDateTime.now(this.clock)));
    }

    void restore(LevlWorkflowStateMemento memento) {
    	this.primaryUseCaseActivePowerW = memento.primaryUseCaseActivePowerW;
        this.nextDischargePowerW = memento.nextDischargePowerW;
        this.actualLevlPowerW = memento.actualLevlPowerW;
        this.dischargeState = DischargeState.restore(memento.state);
        this.levlSocConstraints = LevlSocConstraints.restore(memento.levlSocConstraints);
        this.gridPowerLimitW = Limit.restore(memento.gridPowerLimitW);
    }
}
