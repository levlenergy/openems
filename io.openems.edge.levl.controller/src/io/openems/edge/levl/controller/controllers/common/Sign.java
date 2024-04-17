package io.openems.edge.levl.controller.controllers.common;

public class Sign {

    public static boolean haveSameSign(int one, int other) {
        return haveSameSign((long)one, (long)other);
    }

    public static boolean haveSameSign(long one, long other) {
        if (one >= 0 && other >= 0) {
            return true;
        }
        return one <= 0 && other <= 0;
    }
}
