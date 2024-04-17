package io.openems.edge.levl.controller.controllers.common;

import java.math.BigDecimal;

public class Units {

    public static BigDecimal convertWsToWh(long valueWs) {
        return new BigDecimal(valueWs / 3600.0);
    }

}
