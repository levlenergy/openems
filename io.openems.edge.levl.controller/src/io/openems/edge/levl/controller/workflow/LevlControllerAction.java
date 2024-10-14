package io.openems.edge.levl.controller.workflow;

import io.openems.common.exceptions.OpenemsError;
import io.openems.edge.ess.api.ManagedSymmetricEss;
import io.openems.edge.levl.controller.controllers.common.LevlWorkflowReference;
import io.openems.edge.levl.controller.controllers.common.Limit;
import io.openems.edge.levl.controller.workflow.strategy.AddPowerStrategy;
import io.openems.edge.levl.controller.workflow.strategy.OnlyIncreaseAbsolutePowerStrategy;
import io.openems.edge.levl.controller.workflow.strategy.PowerStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LevlControllerAction {
	private final Logger log = LoggerFactory.getLogger(LevlControllerAction.class);

	protected ManagedSymmetricEss ess;
	protected LevlWorkflowReference levlWorkflow;

	public LevlControllerAction(ManagedSymmetricEss ess, LevlWorkflowReference levlWorkflow) {
		this.ess = ess;
		this.levlWorkflow = levlWorkflow;
	}

	/**
	 * Calculates the absolute power based on levl and primary use case (puc) power.
	 *
	 * @param pucTargetPower the unconstrained target power of the primary use case (puc)
	 * @throws OpenemsError.OpenemsNamedException if an error occurs
	 */
	public void addPowers(int pucTargetPower) throws OpenemsError.OpenemsNamedException {
		this.applyStrategy(pucTargetPower, new AddPowerStrategy());
	}

	/**
	 * Increases the absolute power based on levl and primary use case (puc) power.
	 *
	 * @param pucTargetPower the unconstrained target power of the primary use case (puc)
	 * @throws OpenemsError.OpenemsNamedException if an error occurs
	 */
	public void onlyIncreaseAbsolutePower(Integer pucTargetPower)
			throws OpenemsError.OpenemsNamedException {
		this.applyStrategy(pucTargetPower, new OnlyIncreaseAbsolutePowerStrategy());
	}

	private void applyStrategy(int pucTargetPower, PowerStrategy powerStrategy) throws OpenemsError.OpenemsNamedException {
		var nextPucBatteryPowerW = this.determinePrimaryUseCasePower(pucTargetPower);
		var levlPowerW = this.calculateLevlPower(nextPucBatteryPowerW);
		var overallPowerW = powerStrategy.combinePrimaryUseCaseAndLevlDischargePowerW(nextPucBatteryPowerW, levlPowerW);
		this.logPowerValues(nextPucBatteryPowerW, overallPowerW);
		this.applyPowerSettings(overallPowerW);
	}

	private int calculateLevlPower(int nextPucBatteryPowerW) throws OpenemsError.OpenemsNamedException {
		Limit levlUseCaseConstraints = this.levlWorkflow.getLevlUseCaseConstraints(nextPucBatteryPowerW);
		this.log.debug("levlUseCaseConstraints: " + levlUseCaseConstraints);

		var levlNextDischargePowerUnconstrainedW = this.levlWorkflow.getNextDischargePowerW();
		var levlNextDischargePowerW = levlUseCaseConstraints.apply(levlNextDischargePowerUnconstrainedW);

		this.log.debug("constrained levlNextDischargePowerW: " + levlNextDischargePowerW);

		return levlNextDischargePowerW;
	}

	private void logPowerValues(int originalActivePower, int overallPower) {
		this.log.debug("originalActivePower: " + originalActivePower);
		this.log.debug("overallPower: " + overallPower);
		this.log.debug("workflow request {}", this.levlWorkflow.getNextDischargePowerW());
		this.log.debug("overall controller power: {}", overallPower);
	}

	private void applyPowerSettings(int overallPower) throws OpenemsError.OpenemsNamedException {
		this.ess.setActivePowerEquals(overallPower);
		this.ess.setReactivePowerEquals(0);
	}

	/**
	 * Calculates the puc active power considering the levl SoC.
	 * 
	 * @param pucTargetPower the unconstrained puc power of the primary use case
	 * @return the puc power within all limits in watts
	 */
	private int determinePrimaryUseCasePower(int pucTargetPower) {
		this.log.debug("puc target power unconstrained: " + pucTargetPower);
		
		var constraints = this.levlWorkflow.determinePrimaryUseCaseConstraints();
		this.log.debug("puc constraints: " + constraints);
		
		var pucTargetPowerConstrained = constraints.apply(pucTargetPower);
		this.log.debug("puc target power constrained: " + pucTargetPowerConstrained);
		
		this.levlWorkflow.setPrimaryUseCaseActivePowerW(pucTargetPowerConstrained);
		return pucTargetPowerConstrained;
	}
}