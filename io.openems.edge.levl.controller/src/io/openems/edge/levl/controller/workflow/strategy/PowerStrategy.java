package io.openems.edge.levl.controller.workflow.strategy;

public interface PowerStrategy {

    /**
     * Combines the primary use case discharge power and the levl active discharge power.
     *
     * @param primaryUseCaseDischargePowerW the primary use case discharge power in watts.
     * @param levlActiveDischargeW the levl active discharge power in watts.
     * @return the combined power in watts.
     */
    int combinePrimaryUseCaseAndLevlDischargePowerW(int primaryUseCaseDischargePowerW, int levlActiveDischargeW);
}