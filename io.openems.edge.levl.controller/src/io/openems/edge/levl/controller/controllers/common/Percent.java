package io.openems.edge.levl.controller.controllers.common;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Percent {
    public static int calculatePercentOfTotal(BigDecimal value, int total) {
        return value.multiply(new BigDecimal(100)).divide(new BigDecimal(total), 0, RoundingMode.HALF_UP).intValue();
    }

    public static int applyPercentage(int value, BigDecimal percentage) {
        return new BigDecimal(value).multiply(percentage).divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP).intValue();
    }

    public static int undoPercentage(int value, BigDecimal percentage) {
        return new BigDecimal(value * 100).divide(percentage, RoundingMode.HALF_UP).intValue();
    }

}
