package io.openems.edge.levl.controller.workflow.strategy;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class AddPowerStrategyTest {

    private static Stream<Arguments> powerStrategyScenarios() {
        return Stream.of(
                Arguments.of(1500, 1000, 2500, "discharge"),
                Arguments.of(1500, -1000, 500, "discharge, levl charges"),
                Arguments.of(1500, -2000, -500, "discharge, levl charges, result charge"),
                Arguments.of(-1500, -1000, -2500, "charge"),
                Arguments.of(-1500, 1000, -500, "charge, levl discharges"),
                Arguments.of(-1500, 2000, 500, "charge, levl discharges, result discharge")
        );
    }

    @ParameterizedTest(name = "{index} {3}")
    @MethodSource("powerStrategyScenarios")
    public void determineActualLevlPowerW_Discharge_EssReturnsValue(int primaryUseCaseDischargePowerW, int levlDischargePowerW, int expectedResultingPowerW, String description) {
        var underTest = new AddPowerStrategy();
        int result = underTest.combinePrimaryUseCaseAndLevlDischargePowerW(primaryUseCaseDischargePowerW, levlDischargePowerW);
        assertThat(result).isEqualTo(expectedResultingPowerW);
    }


}