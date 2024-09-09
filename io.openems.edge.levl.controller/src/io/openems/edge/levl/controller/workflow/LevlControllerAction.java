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
	 * Adds the powers of the original unconstrained active power.
	 *
	 * @param originalUnconstrainedActivePower the original unconstrained active
	 *                                         power
	 * @throws OpenemsError.OpenemsNamedException if an error occurs
	 */
	public void addPowers(int originalUnconstrainedActivePower) throws OpenemsError.OpenemsNamedException {
		this.applyStrategy(originalUnconstrainedActivePower, new AddPowerStrategy());
	}

	/**
	 * Increases the absolute power of the original unconstrained active power.
	 *
	 * @param originalUnconstrainedActivePower the original unconstrained active
	 *                                         power. Negative values for Charge;
	 *                                         positive for Discharge.
	 * @throws OpenemsError.OpenemsNamedException if an error occurs
	 */
	public void onlyIncreaseAbsolutePower(Integer originalUnconstrainedActivePower)
			throws OpenemsError.OpenemsNamedException {
		this.applyStrategy(originalUnconstrainedActivePower, new OnlyIncreaseAbsolutePowerStrategy());
	}

	private void applyStrategy(int originalUnconstrainedActivePower, PowerStrategy powerStrategy)
			throws OpenemsError.OpenemsNamedException {
		var pucPowerW = this.determinePrimaryUseCasePower(originalUnconstrainedActivePower);
		var levlPowerW = this.calculateLevlPower();
		var overallPowerW = powerStrategy.combinePrimaryUseCaseAndLevlDischargePowerW(pucPowerW, levlPowerW);
		this.logPowerValues(pucPowerW, overallPowerW);
		this.applyPowerSettings(overallPowerW);
	}

	private int calculateLevlPower() throws OpenemsError.OpenemsNamedException {
		Limit levlUseCaseConstraints = this.levlWorkflow.getLevlUseCaseConstraints();
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
	 * Calculates the PUC active power considering the levl SoC.
	 * 
	 * @param originalUnconstrainedActivePower the unconstrained active power of the primary use case
	 * @return the PUC active power in watts
	 */
	private int determinePrimaryUseCasePower(int originalUnconstrainedActivePower) {
		this.log.debug("original unconstrained controller power: {}", originalUnconstrainedActivePower);
		this.log.debug("originalUnconstrainedActivePower: " + originalUnconstrainedActivePower);
		var constraints = this.levlWorkflow.determinePrimaryUseCaseConstraints();
		this.log.debug("PUC constraints: " + constraints);
		var originalActivePower = constraints.apply(originalUnconstrainedActivePower);
		this.log.debug("originalActivePower: " + originalActivePower);
		this.levlWorkflow.setPrimaryUseCaseActivePowerW(originalActivePower);
		return originalActivePower;
	}
}