package io.openems.edge.levl.controller.controllers.common;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class SignTest {

    private static Stream<Arguments> haveSameSignScenarios() {
        return Stream.of(
                Arguments.of(1, 1, true, "both positive"),
                Arguments.of(-1, -1, true, "both negative"),
                Arguments.of(1, -1, false, "one positive, one negative"),
                Arguments.of(0, 0, true, "both zero"),
                Arguments.of(0, 1, true, "one zero, one positive"),
                Arguments.of(0, -1, true, "one zero, one negative")
        );
    }

    @ParameterizedTest(name = "{index} {3}")
    @MethodSource("haveSameSignScenarios")
    public void haveSameSign(int one, int other, boolean expectedResult, String description) {
        assertThat(Sign.haveSameSign(one, other)).isEqualTo(expectedResult);
        assertThat(Sign.haveSameSign(other, one)).isEqualTo(expectedResult);
    }

}