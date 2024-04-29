package io.openems.edge.levl.controller.controllers.common;

import java.math.BigDecimal;

/**
 * This class provides utility methods for unit conversions.
 */
public class Units {

    /**
     * Converts Watt-seconds (Ws) to Watt-hours (Wh).
     *
     * @param valueWs The value in Watt-seconds to be converted.
     * @return The converted value in Watt-hours.
     */
    public static BigDecimal convertWsToWh(long valueWs) {
        return new BigDecimal(valueWs / 3600.0);
    }
}