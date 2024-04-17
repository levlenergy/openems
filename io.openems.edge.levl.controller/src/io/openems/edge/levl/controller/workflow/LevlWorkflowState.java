package io.openems.edge.levl.controller.workflow;

import io.openems.edge.common.channel.value.Value;
import io.openems.edge.levl.controller.controllers.common.Limit;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;


public class LevlWorkflowState {

    Clock clock = Clock.systemDefaultZone();

    LevlPowerCalculator calculator = new LevlPowerCalculator();
    DischargeState dischargeState = new DischargeState();
    LevlSocConstraints levlSocConstraints = new LevlSocConstraints(0, 100, 0, 100);
    Limit gridPowerLimitW = new Limit(0, 0);
    private int primaryUseCaseActivePowerW;
    int nextDischargePowerW;
    int actualLevlPowerW;

    record LevlWorkflowStateMemento(int primaryUseCaseActivePowerW, int nextDischargePowerW, int actualLevlPowerW,
                                    DischargeState.DischargeStateMemento state,
                                    LevlSocConstraints.LevlSocConstraintsMemento levlSocConstraints,
                                    Limit.LimitMemento gridPowerLimitW) {

    }


    public LevlWorkflowStateMemento save() {
        return new LevlWorkflowStateMemento(primaryUseCaseActivePowerW, nextDischargePowerW, actualLevlPowerW, dischargeState.save(), levlSocConstraints.save(), gridPowerLimitW.save());
    }

    public void initAfterRestore() {
        dischargeState.initAfterRestore(LocalDateTime.now(clock));
    }


    public void setPrimaryUseCaseActivePowerW(int originalActivePowerW) {
        primaryUseCaseActivePowerW = originalActivePowerW;
    }

    public int getPrimaryUseCaseActivePowerW() {
        return primaryUseCaseActivePowerW;
    }

    public String getLastCompletedRequestTimestamp() {
        return dischargeState.getLastCompletedRequestTimestamp();
    }

    public int getActualLevlPowerW() {
        return actualLevlPowerW;
    }

    public int getLastCompletedRequestDischargePowerW() {
        return dischargeState.getLastCompletedRequestDischargePowerW();
    }

    public int getNextDischargePowerW() {
        return nextDischargePowerW;
    }

    public Limit getLevlUseCaseConstraints(Value<Integer> meterActivePowerW, Value<Integer> essSoc) {
        var gridConstraints = determineShiftedGridConstraints(meterActivePowerW);
        var socConstraints = levlSocConstraints.determineLevlUseCaseSocConstraints(essSoc);
        return gridConstraints.intersect(socConstraints);
    }

    private Limit determineShiftedGridConstraints(Value<Integer> meterActivePowerW) {
        Limit gridConstraints = gridPowerLimitW.invert();
        if (meterActivePowerW.isDefined()) {
            return gridConstraints.shiftBy(meterActivePowerW.get() + actualLevlPowerW).ensureValidLimitWithZero();
        }
        return gridConstraints;
    }

    void checkActualDischargePower(Optional<Integer> actualPowerW) {
        actualLevlPowerW = calculator.determineActualLevlPowerW(actualPowerW, primaryUseCaseActivePowerW);
        dischargeState.handleRealizedDischargePowerWForOneSecond(actualLevlPowerW);
    }

    void determineNextDischargePower() {
        nextDischargePowerW = calculator.determineNextDischargePowerW(dischargeState.getCurrentRequestRemainingDischargePowerWs());
        System.out.println("*********** next discharge power W " + nextDischargePowerW);
    }

    public void updateState() {
        dischargeState.update(LocalDateTime.now(clock));
    }

    public Limit determinePrimaryUseCaseConstraints(Value<Integer> essSoc, Value<Integer> essCapacity, int minPower, int maxPower) {
        var openemsLimit = new Limit(minPower, maxPower);
        var socLimit = levlSocConstraints.determineLimitFromPhysicalSocConstraintAndLevlSocOffset(
                dischargeState.getTotalDischargeEnergyWsAtBatteryScaledWithEfficiency(),
                essSoc,
                essCapacity
        );
        System.out.println("************ socLimit for PUC: " + socLimit + "\n");
        return openemsLimit.intersect(socLimit);
    }

    public void handleRequest(LevlControlRequest request, int physicalSocLowerPercent, int physicalSocUpperPercent) {
        gridPowerLimitW = request.createGridPowerLimitW();
        levlSocConstraints = request.createLevlSocConstraints(physicalSocLowerPercent, physicalSocUpperPercent);
        dischargeState.handleReceivedRequest(request.getEfficiencyPercent(), request.createDischargeRequest(LocalDateTime.now(clock)));
    }

    void restore(LevlWorkflowStateMemento memento) {
        primaryUseCaseActivePowerW = memento.primaryUseCaseActivePowerW;
        nextDischargePowerW = memento.nextDischargePowerW;
        actualLevlPowerW = memento.actualLevlPowerW;
        dischargeState = DischargeState.restore(memento.state);
        levlSocConstraints = LevlSocConstraints.restore(memento.levlSocConstraints);
        gridPowerLimitW = Limit.restore(memento.gridPowerLimitW);
    }
}
