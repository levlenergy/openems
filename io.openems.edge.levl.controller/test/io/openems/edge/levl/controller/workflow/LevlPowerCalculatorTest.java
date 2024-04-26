package io.openems.edge.levl.controller.workflow;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;


public class LevlPowerCalculatorTest {

    private LevlPowerCalculator underTest = new LevlPowerCalculator();

    private static Stream<Arguments> determineActualLevlPowerWData() {
        return Stream.of(
                Arguments.of(1500, 1000, 500, "discharge"),
                Arguments.of(-1500, 1000, -2500, "charge and levl discharge"),
                Arguments.of(-1500, -1000, -500, "charge"),
                Arguments.of(1500, -1000, 2500, "discharge and levl charge"),
                Arguments.of(null, 1000, 0, "no ess valuee"));
    }

    @ParameterizedTest(name = "{index} {3}")
    @MethodSource("determineActualLevlPowerWData")
    public void determineActualLevlPowerW_Discharge_EssReturnsValue(Integer essValue, int primaryUseCaseActivePowerW, int expectedLevlPowerW, String description) {
        int result = this.underTest.determineActualLevlPowerW(Optional.ofNullable(essValue), primaryUseCaseActivePowerW);

        assertThat(result).isEqualTo(expectedLevlPowerW);
    }

    @Test
    public void determineNextDischargePowerW() {
        var result = this.underTest.determineNextDischargePowerW(1234);
        assertThat(result).isEqualTo(1234);
    }

    @Test
    public void determineNextDischargePowerW_Large_Value() {
        var result = this.underTest.determineNextDischargePowerW(Integer.MAX_VALUE + 1L);
        assertThat(result).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    public void determineNextDischargePowerW_Large_Negative_Value() {
        var result = this.underTest.determineNextDischargePowerW(Integer.MIN_VALUE - 1L);
        assertThat(result).isEqualTo(Integer.MIN_VALUE);
    }
}