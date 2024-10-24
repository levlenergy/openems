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
    // positive = discharge
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

    /**
     * Determines the constraints for the levl use case.
     *
     * @param essSoc the state of charge of the energy storage system
     * @param essActivePowerW current battery power
     * @param meterActivePowerW current grid meter power
     * @param nextPucBatteryPowerW the puc battery power for the next cycle
     * @return the constraints
     */
    public Limit getLevlUseCaseConstraints(Value<Integer> essSoc, Value<Integer> essActivePowerW, Value<Integer> meterActivePowerW, int nextPucBatteryPowerW) {
    	if (!essActivePowerW.isDefined() || !meterActivePowerW.isDefined()) {
			this.log.warn("essActivePowerW or meterActivePowerW not defined");
    		return new Limit(0, 0);
		}
    	
    	var nextPucGridPowerW = this.calculatePucGridPowerForNextCycle(meterActivePowerW.get(), essActivePowerW.get(), nextPucBatteryPowerW);
    	
    	if (this.levlUseCaseAllowed(nextPucGridPowerW)) {
	        var gridConstraints = this.determineShiftedGridConstraints(nextPucGridPowerW);
	        var socConstraints = this.levlSocConstraints.determineLevlUseCaseSocConstraints(essSoc);
	        return gridConstraints.intersect(socConstraints);
    	}
    	return new Limit(0, 0);
    }
    
    private int calculatePucGridPowerForNextCycle(int meterActivePowerW, int essActivePowerW, int nextPucBatteryPowerW) {
    	int gridPowerWithoutEss = meterActivePowerW + essActivePowerW;
    	return gridPowerWithoutEss - nextPucBatteryPowerW;
    }
    
	private boolean levlUseCaseAllowed(int nextPucGridPowerW) {
		this.log.debug("isInfluenceSellToGridAllowed: " + this.dischargeState.isInfluenceSellToGridAllowed());	
		if (this.dischargeState.isInfluenceSellToGridAllowed()) {
			return true;
		}
		
		this.log.debug("nextPucGridPowerW: " + nextPucGridPowerW);
		if (nextPucGridPowerW < 0) {
			return false;
		}
		
		return true;
	}

    /**
     * Determines the shifted grid constraints.
     *
     * @param nextPucGridPowerW the puc grid power for the next cycle
     * @return the shifted grid constraints
     */
    private Limit determineShiftedGridConstraints(int nextPucGridPowerW) {
        // invert because values are switched in openems
        Limit gridConstraints = this.gridPowerLimitW.invert();
        return gridConstraints.shiftBy(nextPucGridPowerW).ensureValidLimitWithZero();
    }

    /**
     * Determines the levlPowerW by subtracting the calculated primaryUseCaseActivePowerW from the overall realized power (actualPowerW).
     * Updating the discharge state with the calculated levl power.
     *
     * @param actualPowerW the actual power in watts (positive = discharge)
     */
    void checkActualDischargePower(Optional<Integer> actualPowerW) {
        this.actualLevlPowerW = this.calculator.determineActualLevlPowerW(actualPowerW, this.primaryUseCaseActivePowerW);
        this.dischargeState.handleRealizedDischargePowerWForOneSecond(this.actualLevlPowerW);
    }

    /**
     * Determines the next discharge power.
     */
    void determineNextDischargePower() {
    	var remainingRequestDischargeEnergyWs = this.dischargeState.getCurrentRequestRemainingDischargePowerWs();
    	var totalDischargeEnergyWsAtBatteryScaledWithEfficiency = this.dischargeState.getTotalDischargeEnergyWsAtBatteryScaledWithEfficiency();
    	
    	this.log.info("*********** remaining discharge energy Ws {}", remainingRequestDischargeEnergyWs);
    	this.log.info("*********** totalDischargeEnergyWsAtBatteryScaledWithEfficiency {}", totalDischargeEnergyWsAtBatteryScaledWithEfficiency);
    	
        this.nextDischargePowerW = this.calculator.determineNextDischargePowerW(remainingRequestDischargeEnergyWs);
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
    	var currentCycleRealizedDischargeEnergyWs = this.dischargeState.getCurrentRequestRealizedDischargeEnergyWithEfficiencyWs();
    	this.log.info("########## Received Request ##########");
    	this.log.info("totalRealizedDischargeEnergyWh: {}", request.getTotalRealizedDischargeEnergyWh());   	
    	var totalRealizedDischargeEnergyWs = (long) request.getTotalRealizedDischargeEnergyWh() * 3600;
    	this.log.info("totalRealizedDischargeEnergyWs: {}", totalRealizedDischargeEnergyWs); 
    	this.log.info("currentCycleRealizedDischargeEnergyWs: {}", currentCycleRealizedDischargeEnergyWs);
    	var totalRealizedDischargeEnergyIncludingCurrentCycleWs = totalRealizedDischargeEnergyWs + currentCycleRealizedDischargeEnergyWs;
    	this.log.info("totalRealizedDischargeEnergyIncludingCurrentCycleWs: {}", totalRealizedDischargeEnergyIncludingCurrentCycleWs);   	    	
    	
        this.gridPowerLimitW = request.createGridPowerLimitW();
        this.levlSocConstraints = request.createLevlSocConstraints(physicalSocLowerPercent, physicalSocUpperPercent);

        var dischargeRequest = request.createDischargeRequest(LocalDateTime.now(this.clock));
        this.dischargeState.handleReceivedRequest(request.getEfficiencyPercent(), dischargeRequest, totalRealizedDischargeEnergyIncludingCurrentCycleWs);
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