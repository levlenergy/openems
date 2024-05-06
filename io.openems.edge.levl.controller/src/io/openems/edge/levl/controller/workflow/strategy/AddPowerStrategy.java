package io.openems.edge.levl.controller.workflow.strategy;

public class AddPowerStrategy implements PowerStrategy {
    @Override
    public int combinePrimaryUseCaseAndLevlDischargePowerW(int primaryUseCaseDischargePowerW, int levlActiveDischargeW) {
        return primaryUseCaseDischargePowerW + levlActiveDischargeW;
    }
}
