package io.openems.edge.levl.controller.controllers.common;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * This class provides utility methods for performing percentage calculations.
 */
public class Percent {

    /**
     * Calculates the percentage of a given value with respect to a total.
     *
     * @param value The value for which the percentage is to be calculated.
     * @param total The total value.
     * @return The percentage of the value with respect to the total, rounded to the nearest integer.
     */
    public static int calculatePercentOfTotal(BigDecimal value, int total) {
        return value.multiply(new BigDecimal(100)).divide(new BigDecimal(total), 0, RoundingMode.HALF_UP).intValue();
    }

    /**
     * Applies a given percentage to a value.
     *
     * @param value The value to which the percentage is to be applied.
     * @param percentage The percentage to be applied.
     * @return The result of applying the percentage to the value, rounded to the nearest integer.
     */
    public static int applyPercentage(int value, BigDecimal percentage) {
        return new BigDecimal(value).multiply(percentage).divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP).intValue();
    }

    /**
     * Undo of a given percentage to a value.
     *
     * @param value The value from which the percentage is to be undone.
     * @param percentage The percentage to be undone.
     * @return The original value before the percentage was applied, rounded to the nearest integer.
     */
    public static int undoPercentage(int value, BigDecimal percentage) {
        return new BigDecimal(value * 100).divide(percentage, RoundingMode.HALF_UP).intValue();
    }
}