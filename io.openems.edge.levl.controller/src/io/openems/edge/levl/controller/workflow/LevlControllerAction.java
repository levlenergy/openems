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
     * @param originalUnconstrainedActivePower the original unconstrained active power
     * @throws OpenemsError.OpenemsNamedException if an error occurs
     */
    public void addPowers(int originalUnconstrainedActivePower) throws OpenemsError.OpenemsNamedException {
        this.applyStrategy(originalUnconstrainedActivePower, new AddPowerStrategy());
    }

    /**
     * Increases the absolute power of the original unconstrained active power.
     *
     * @param originalUnconstrainedActivePower the original unconstrained active power
     * @throws OpenemsError.OpenemsNamedException if an error occurs
     */
    public void onlyIncreaseAbsolutePower(Integer originalUnconstrainedActivePower) throws OpenemsError.OpenemsNamedException {
        this.applyStrategy(originalUnconstrainedActivePower, new OnlyIncreaseAbsolutePowerStrategy());
    }

    private void applyStrategy(int originalUnconstrainedActivePower, PowerStrategy powerStrategy) throws OpenemsError.OpenemsNamedException {
        int originalActivePower = this.determinePrimaryUseCasePower(originalUnconstrainedActivePower);
        int levlNextDischargePowerW = this.levlWorkflow.getNextDischargePowerW();
        
        Limit levlUseCaseConstraints = this.levlWorkflow.getLevlUseCaseConstraints();
        this.log.debug("levlUseCaseConstraints: " + levlUseCaseConstraints);
        int gridConstrainedLevlNextDischargePowerW = levlUseCaseConstraints.apply(levlNextDischargePowerW);
        this.log.debug("gridConstrainedLevlNextDischargePowerW: " + gridConstrainedLevlNextDischargePowerW);
        int overallPower = powerStrategy.combinePrimaryUseCaseAndLevlDischargePowerW(originalActivePower, gridConstrainedLevlNextDischargePowerW);
        this.log.debug("overallPower: " + overallPower);
        this.log.debug("workflow request {}", this.levlWorkflow.getNextDischargePowerW());
        this.log.debug("overall controller power: {}", overallPower);

        this.ess.setActivePowerEquals(overallPower);
        this.ess.setReactivePowerEquals(0);
    }

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