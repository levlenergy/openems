package io.openems.edge.levl.controller.controllers.common;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class PercentTest {

    private static Stream<Arguments> provideCalculatePercentOfTotalValues() {
        return Stream.of(
                Arguments.of(new BigDecimal(2500), 50000, 5),
                Arguments.of(new BigDecimal(2749), 50000, 5),
                Arguments.of(new BigDecimal(2750), 50000, 6),
                Arguments.of(new BigDecimal(-7500), 50000, -15),
                Arguments.of(new BigDecimal(-7749), 50000, -15),
                Arguments.of(new BigDecimal(-7750), 50000, -16)
        );
    }

    @ParameterizedTest
    @MethodSource("provideCalculatePercentOfTotalValues")
    public void calculateWsToPercent(BigDecimal value, int total, int expected) {
        assertThat(Percent.calculatePercentOfTotal(value, total)).isEqualTo(expected);
    }

    private static Stream<Arguments> provideApplyPercentageValues() {
        return Stream.of(
                Arguments.of(1000, 80, 800),
                Arguments.of(10, 50, 5),
                Arguments.of(10, 54, 5),
                Arguments.of(10, 55, 6)
        );
    }

    @ParameterizedTest
    @MethodSource("provideApplyPercentageValues")
    public void applyPercentage(int total, int percentage, int expected) {
        assertThat(Percent.applyPercentage(total, new BigDecimal(percentage))).isEqualTo(expected);
    }

    private static Stream<Arguments> provideUndoPercentageValues() {
        return Stream.of(
                Arguments.of(900, 90, 1000),
                Arguments.of(1049, 10000, 10),
                Arguments.of(1050, 10000, 11)
        );
    }

    @ParameterizedTest
    @MethodSource("provideUndoPercentageValues")
    public void undoPercentage(int total, int percentage, int expected) {
        assertThat(Percent.undoPercentage(total, new BigDecimal(percentage))).isEqualTo(expected);
    }
}