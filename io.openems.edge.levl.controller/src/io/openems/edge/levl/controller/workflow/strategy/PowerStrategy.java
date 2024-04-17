package io.openems.edge.levl.controller.workflow.strategy;

public interface PowerStrategy {
    int combinePrimaryUseCaseAndLevlDischargePowerW(int primaryUseCaseDischargePowerW, int levlActiveDischargeW);
}
