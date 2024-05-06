package io.openems.edge.levl.controller.controllers.common;

/**
 * This class provides utility methods for checking if two numbers have the same sign.
 */
public class Sign {

    /**
     * Checks if two integers have the same sign.
     *
     * @param one   the first integer
     * @param other the second integer
     * @return true if both numbers are either positive or negative, false otherwise
     */
    public static boolean haveSameSign(int one, int other) {
        return haveSameSign((long)one, (long)other);
    }

    /**
     * Checks if two longs have the same sign.
     *
     * @param one   the first long
     * @param other the second long
     * @return true if both numbers are either positive or negative, false otherwise
     */
    public static boolean haveSameSign(long one, long other) {
        if (one >= 0 && other >= 0) {
            return true;
        }
        return one <= 0 && other <= 0;
    }
}