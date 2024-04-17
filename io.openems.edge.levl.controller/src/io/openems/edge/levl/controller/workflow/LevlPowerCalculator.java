package io.openems.edge.levl.controller.workflow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class LevlPowerCalculator {
    private final Logger log = LoggerFactory.getLogger(LevlPowerCalculator.class);
    public int determineNextDischargePowerW(long remainingRequestDischargeEnergyWs) {
        log.info("*********** remaining discharge energy Ws {}", remainingRequestDischargeEnergyWs);
        // remainingRequestDischargeEnergyWs realized in one Second -> Ws becomes W
        if (remainingRequestDischargeEnergyWs > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        if (remainingRequestDischargeEnergyWs < Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        }
        int nextDischargePowerW = (int)remainingRequestDischargeEnergyWs;
        log.debug("*********** next discharge power W {}", nextDischargePowerW);
        return nextDischargePowerW;
    }

    public int determineActualLevlPowerW(Optional<Integer> lastActivePowerW, int primaryUseCaseActivePowerW) {
        return lastActivePowerW.map(value -> calculateEffectiveLevlPower(primaryUseCaseActivePowerW, value)).orElse(0);
    }

    private int calculateEffectiveLevlPower(int primaryUseCaseActivePowerW, Integer lastActivePowerW) {
        int levlPowerW = lastActivePowerW - primaryUseCaseActivePowerW;
        log.debug("*********** last active power W {}", lastActivePowerW);
        log.debug("*********** levl power W {}", levlPowerW);
        return levlPowerW;
    }
}
