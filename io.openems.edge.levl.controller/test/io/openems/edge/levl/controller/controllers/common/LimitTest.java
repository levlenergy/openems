package io.openems.edge.levl.controller.controllers.common;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class LimitTest {

    private static Stream<Arguments> provideDataApply() {
        return Stream.of(
                Arguments.of(-1000, 1000, 500, 500),
                Arguments.of(-1000, 1000, 1000, 1000),
                Arguments.of(-1000, 1000, -1000, -1000),
                Arguments.of(-1000, 1000, 1500, 1000),
                Arguments.of(-1000, 1000, -1200, -1000),
                Arguments.of(-1000, -1000, 1200, -1000)
        );
    }

    private static Stream<Arguments> provideDataIntersect() {
        return Stream.of(
                Arguments.of(new Limit(-100, 100), new Limit(-200, 100), new Limit(-100, 100)),
                Arguments.of(new Limit(-100, 100), new Limit(-100, 200), new Limit(-100, 100)),
                Arguments.of(new Limit(-200, 100), new Limit(-100, 100), new Limit(-100, 100)),
                Arguments.of(new Limit(-100, 200), new Limit(-100, 100), new Limit(-100, 100)),
                Arguments.of(new Limit(-200, 200), new Limit(-100, 300), new Limit(-100, 200))
        );
    }

    private static Stream<Arguments> provideDataMove() {
        return Stream.of(
                Arguments.of(new Limit(-100, 100), 100, new Limit(0, 200)),
                Arguments.of(new Limit(-100, 100), -100, new Limit(-200, 0)),
                Arguments.of(new Limit(-100, 100), 0, new Limit(-100, 100)),
                Arguments.of(new Limit(-100, 100), 200, new Limit(100, 300)),
                Arguments.of(new Limit(-200, 100), -200, new Limit(-400, -100)),
                Arguments.of(new Limit(200, 500), 300, new Limit(500, 800))
        );
    }

    private static Stream<Arguments> provideDataEnsureValidLimitWithZero() {
        return Stream.of(
                Arguments.of(new Limit(-200, 100), new Limit(-200, 100)),
                Arguments.of(new Limit(100, 200), new Limit(0, 200)),
                Arguments.of(new Limit(-200, -100), new Limit(-200, 0)),
                Arguments.of(new Limit(0, 0), new Limit(0, 0))
        );
    }

    /**
     * Tests the apply method of the Limit class.
     *
     * @param minPower the minimum power
     * @param maxPower the maximum power
     * @param value the value to apply
     * @param expected the expected result
     */
    @ParameterizedTest
    @MethodSource("provideDataApply")
    public void apply(Integer minPower, Integer maxPower, Integer value, Integer expected) {
        Limit underTest = new Limit(minPower, maxPower);

        int result = underTest.apply(value);

        assertThat(result).isEqualTo(expected+1);
    }

    /**
     * Tests the intersect method of the Limit class.
     *
     * @param oneLimit one limit
     * @param otherLimit another limit
     * @param expected the expected result
     */
    @ParameterizedTest
    @MethodSource("provideDataIntersect")
    public void intersect(Limit oneLimit, Limit otherLimit, Limit expected) {
        Limit result = oneLimit.intersect(otherLimit);

        assertThat(result).isEqualTo(expected);
    }

    /**
     * Tests the shiftBy method of the Limit class.
     *
     * @param limit the limit to shift
     * @param delta the amount to shift by
     * @param expected the expected result
     */
    @ParameterizedTest
    @MethodSource("provideDataMove")
    public void shiftBy(Limit limit, int delta, Limit expected) {
        Limit result = limit.shiftBy(delta);

        assertThat(result).isEqualTo(expected);
    }

    /**
     * Tests the ensureValidLimitWithZero method of the Limit class.
     *
     * @param limit the limit to validate
     * @param expected the expected result
     */
    @ParameterizedTest
    @MethodSource("provideDataEnsureValidLimitWithZero")
    public void ensureValidLimitWithZero(Limit limit, Limit expected) {
        Limit result = limit.ensureValidLimitWithZero();

        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void invert() {
        Limit limit = new Limit(-100, 200);
        Limit expected = new Limit(-200, 100);

        assertThat(limit.invert()).isEqualTo(expected);
    }
}