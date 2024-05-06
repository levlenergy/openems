package io.openems.edge.levl.controller.workflow.strategy;

import io.openems.edge.levl.controller.controllers.common.Sign;

public class OnlyIncreaseAbsolutePowerStrategy implements PowerStrategy {
    @Override
    public int combinePrimaryUseCaseAndLevlDischargePowerW(int primaryUseCaseDischargePowerW, int levlActiveDischargeW) {
        if (Sign.haveSameSign(primaryUseCaseDischargePowerW, levlActiveDischargeW)) {
            return primaryUseCaseDischargePowerW + levlActiveDischargeW;
        }
        return primaryUseCaseDischargePowerW;
    }
}
