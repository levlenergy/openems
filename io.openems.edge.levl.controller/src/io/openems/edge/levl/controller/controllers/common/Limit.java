package io.openems.edge.levl.controller.controllers.common;

public record Limit(int minPower, int maxPower) {

    public record LimitMemento(int minPower, int maxPower) {
    }

    public static Limit unconstrained() {
        return new Limit(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public static Limit lowerBound(int bound) {
        return new Limit(bound, Integer.MAX_VALUE);
    }

    public static Limit upperBound(int bound) {
        return new Limit(Integer.MIN_VALUE, bound);
    }

    public LimitMemento save() {
        return new LimitMemento(minPower, maxPower);
    }
    public static Limit restore(LimitMemento memento) {
        return new Limit(memento.minPower, memento.maxPower);
    }

    public int apply(int value) {
        return Math.max(Math.min(value, maxPower), minPower);
    }

    public Limit intersect(Limit otherLimit) {
        return new Limit(Math.max(minPower, otherLimit.minPower), Math.min(maxPower, otherLimit.maxPower));
    }

    public Limit invert() {
        return new Limit(-maxPower, -minPower);
    }

    public Limit shiftBy(int delta) {
        return new Limit(minPower + delta, maxPower + delta);
    }

    public Limit ensureValidLimitWithZero() {
        return new Limit(Math.min(0, minPower), Math.max(0, maxPower));
    }

}