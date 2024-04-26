package io.openems.edge.levl.controller.workflow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class LevlPowerCalculator {
    private final Logger log = LoggerFactory.getLogger(LevlPowerCalculator.class);

    /**
     * This method determines the next discharge power in watts.
     * It takes the remaining request discharge energy in watt-seconds and converts it to watts.
     * If the remaining request discharge energy is greater than the maximum integer value, it returns the maximum integer value.
     * If the remaining request discharge energy is less than the minimum integer value, it returns the minimum integer value.
     *
     * @param remainingRequestDischargeEnergyWs the remaining request discharge energy in watt-seconds
     * @return the next discharge power in watts
     */
    public int determineNextDischargePowerW(long remainingRequestDischargeEnergyWs) {
        this.log.info("*********** remaining discharge energy Ws {}", remainingRequestDischargeEnergyWs);
        // remainingRequestDischargeEnergyWs realized in one Second -> Ws becomes W
        if (remainingRequestDischargeEnergyWs > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        if (remainingRequestDischargeEnergyWs < Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        }
        int nextDischargePowerW = (int)remainingRequestDischargeEnergyWs;
        this.log.debug("*********** next discharge power W {}", nextDischargePowerW);
        return nextDischargePowerW;
    }

    /**
     * This method determines the actual Levl power in watts.
     * It takes the last active power and the primary use case active power as parameters.
     * If the last active power is present, it calculates the effective Levl power.
     * If the last active power is not present, it returns 0.
     *
     * @param lastActivePowerW the last active power in watts
     * @param primaryUseCaseActivePowerW the primary use case active power in watts
     * @return the actual Levl power in watts
     */
    public int determineActualLevlPowerW(Optional<Integer> lastActivePowerW, int primaryUseCaseActivePowerW) {
        return lastActivePowerW.map(value -> this.calculateEffectiveLevlPower(primaryUseCaseActivePowerW, value)).orElse(0);
    }

    /**
     * This method calculates the effective Levl power in watts.
     * It subtracts the primary use case active power from the last active power to get the Levl power.
     *
     * @param primaryUseCaseActivePowerW the primary use case active power in watts
     * @param lastActivePowerW the last active power in watts
     * @return the effective Levl power in watts
     */
    private int calculateEffectiveLevlPower(int primaryUseCaseActivePowerW, Integer lastActivePowerW) {
        int levlPowerW = lastActivePowerW - primaryUseCaseActivePowerW;
        this.log.debug("*********** last active power W {}", lastActivePowerW);
        this.log.debug("*********** levl power W {}", levlPowerW);
        return levlPowerW;
    }
}