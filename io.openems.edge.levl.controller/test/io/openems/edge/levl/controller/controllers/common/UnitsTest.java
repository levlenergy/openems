package io.openems.edge.levl.controller.controllers.common;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class UnitsTest {

    private static Stream<Arguments> provideData() {
        return Stream.of(
                Arguments.of(7200, 2.0, "exact result"),
                Arguments.of(7199, 7199.0 / 3600, "one ws missing"),
                Arguments.of(7201, 7201.0 / 3600, "one ws too much")
        );
    }

    @ParameterizedTest(name = "{index} {2}")
    @MethodSource("provideData")
    public void convertWsToWh(int valueWs, double expected, String description) {
        BigDecimal result = Units.convertWsToWh(valueWs);

        assertThat(result).isEqualTo(new BigDecimal(expected));
    }

}