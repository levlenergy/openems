package io.openems.edge.levl.controller.workflow.strategy;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class OnlyIncreaseAbsolutePowerStrategyTest {

    private static Stream<Arguments> powerStrategyScenarios() {
        return Stream.of(
                Arguments.of(1500, 1000, 2500, "discharge"),
                Arguments.of(1500, -1000, 1500, "discharge, levl charges, levl value is ignored"),
                Arguments.of(1500, -2000, 1500, "discharge, levl charges, result would be charge, levl value is ignored"),
                Arguments.of(-1500, -1000, -2500, "charge"),
                Arguments.of(-1500, 1000, -1500, "charge, levl discharges, value is ignored"),
                Arguments.of(-1500, 2000, -1500, "charge, levl discharges, result would be charge, value is ignored")
        );
    }

    @ParameterizedTest(name = "{index} {3}")
    @MethodSource("powerStrategyScenarios")
    public void determineActualLevlPowerW_Discharge_EssReturnsValue(int primaryUseCaseDischargePowerW, int levlDischargePowerW, int expectedResultingPowerW, String description) {
        var underTest = new OnlyIncreaseAbsolutePowerStrategy();
        int result = underTest.combinePrimaryUseCaseAndLevlDischargePowerW(primaryUseCaseDischargePowerW, levlDischargePowerW);
        assertThat(result).isEqualTo(expectedResultingPowerW);
    }


}