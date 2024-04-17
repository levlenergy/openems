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

    public void addPowers(int originalUnconstrainedActivePower) throws OpenemsError.OpenemsNamedException {
        applyStrategy(originalUnconstrainedActivePower, new AddPowerStrategy());
    }

    public void onlyIncreaseAbsolutePower(Integer originalUnconstrainedActivePower) throws OpenemsError.OpenemsNamedException {
        applyStrategy(originalUnconstrainedActivePower, new OnlyIncreaseAbsolutePowerStrategy());
    }

    private void applyStrategy(int originalUnconstrainedActivePower, PowerStrategy powerStrategy) throws OpenemsError.OpenemsNamedException {
        int originalActivePower = determinePrimaryUseCasePower(originalUnconstrainedActivePower);
        int levlNextDischargePowerW = levlWorkflow.getNextDischargePowerW();

        Limit levlUseCaseConstraints = levlWorkflow.getLevlUseCaseConstraints();
        System.out.println("************ levlUseCaseConstraints: " + levlUseCaseConstraints + "\n");
        int gridConstrainedLevlNextDischargePowerW = levlUseCaseConstraints.apply(levlNextDischargePowerW);
        System.out.println("************ gridConstrainedLevlNextDischargePowerW: " + gridConstrainedLevlNextDischargePowerW + "\n");
        int overallPower = powerStrategy.combinePrimaryUseCaseAndLevlDischargePowerW(originalActivePower, gridConstrainedLevlNextDischargePowerW);
        System.out.println("************ overallPower: " + overallPower + "\n");
        log.debug("+++++++++++workflow request {}", levlWorkflow.getNextDischargePowerW());
        log.debug("######### overall controller power: {}", overallPower);

        this.ess.setActivePowerEquals(overallPower);
        this.ess.setReactivePowerEquals(0);
    }

    private int determinePrimaryUseCasePower(int originalUnconstrainedActivePower) {
        log.debug("######### original unconstrained controller power: {}", originalUnconstrainedActivePower);
        System.out.println("************ originalUnconstrainedActivePower: " + originalUnconstrainedActivePower + "\n");
        var constraints = levlWorkflow.determinePrimaryUseCaseConstraints();
        System.out.println("************ PUC constraints: " + constraints + "\n");
        var originalActivePower = constraints.apply(originalUnconstrainedActivePower);
        System.out.println("************ originalActivePower: " + originalActivePower + "\n");
        levlWorkflow.setPrimaryUseCaseActivePowerW(originalActivePower);
        return originalActivePower;
    }
}
