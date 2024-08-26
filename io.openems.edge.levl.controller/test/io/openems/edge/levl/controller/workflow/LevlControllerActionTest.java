package io.openems.edge.levl.controller.workflow;

import io.openems.common.exceptions.OpenemsError;
import io.openems.edge.common.channel.value.Value;
import io.openems.edge.ess.api.ManagedSymmetricEss;
import io.openems.edge.levl.controller.controllers.common.LevlWorkflowReference;
import io.openems.edge.levl.controller.controllers.common.Limit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.stream.Stream;

public class LevlControllerActionTest {

    private ManagedSymmetricEss ess;
    private LevlWorkflowReference levlWorkflow;
    private LevlControllerAction underTest;

    private static Stream<Arguments> applyLevlUsecaseAddPowersScenarios() {
        return Stream.of(
                Scenario.of("Original charge power is below bounds, levl charges")
                        .withOriginalActivePowerW(-1000)
                        .withLevlConstraint(-500, 500)
                        .withNextDischargePowerW(-100)
                        .withShiftedLevlGridPowerWConstraint(-1500, 1500)
                        .expectResultingOriginalPowerW(-500)
                        .expectActivePowerW(-600)
                        .withInfluenceSellToGrid(true)
                        .withMeterActivePowerW(1)
                        .asArguments(),
                Scenario.of("Original charge power is below bounds, levl does nothing")
                        .withOriginalActivePowerW(-1000)
                        .withLevlConstraint(-500, 500)
                        .withNextDischargePowerW(0)
                        .withShiftedLevlGridPowerWConstraint(-1500, 1500)
                        .expectResultingOriginalPowerW(-500)
                        .expectActivePowerW(-500)
                        .withInfluenceSellToGrid(true)
                        .withMeterActivePowerW(1)
                        .asArguments(),
                Scenario.of("Original charge power is below bounds, levl discharges")
                        .withOriginalActivePowerW(-1000)
                        .withLevlConstraint(-500, 500)
                        .withNextDischargePowerW(100)
                        .withShiftedLevlGridPowerWConstraint(-1500, 1500)
                        .expectResultingOriginalPowerW(-500)
                        .expectActivePowerW(-400)
                        .withInfluenceSellToGrid(true)
                        .withMeterActivePowerW(1)
                        .asArguments(),
                Scenario.of("Original charge power is above bounds, levl charges")
                        .withOriginalActivePowerW(1000)
                        .withLevlConstraint(-500, 500)
                        .withNextDischargePowerW(-100)
                        .withShiftedLevlGridPowerWConstraint(-1500, 1500)
                        .expectResultingOriginalPowerW(500)
                        .expectActivePowerW(400)
                        .withInfluenceSellToGrid(true)
                        .withMeterActivePowerW(1)
                        .asArguments(),
                Scenario.of("Original charge power is above bounds, levl does nothing")
                        .withOriginalActivePowerW(1000)
                        .withLevlConstraint(-500, 500)
                        .withNextDischargePowerW(0)
                        .withShiftedLevlGridPowerWConstraint(-1500, 1500)
                        .expectResultingOriginalPowerW(500)
                        .expectActivePowerW(500)
                        .withInfluenceSellToGrid(true)
                        .withMeterActivePowerW(1)
                        .asArguments(),
                Scenario.of("Original charge power is above bounds, levl discharges")
                        .withOriginalActivePowerW(1000)
                        .withLevlConstraint(-500, 500)
                        .withNextDischargePowerW(100)
                        .withShiftedLevlGridPowerWConstraint(-1500, 1500)
                        .expectResultingOriginalPowerW(500)
                        .expectActivePowerW(600)
                        .withInfluenceSellToGrid(true)
                        .withMeterActivePowerW(1)
                        .asArguments(),
                Scenario.of("Original charge power is within bounds, levl charges")
                        .withOriginalActivePowerW(-500)
                        .withLevlConstraint(-500, 500)
                        .withNextDischargePowerW(-100)
                        .withShiftedLevlGridPowerWConstraint(-1500, 1500)
                        .expectResultingOriginalPowerW(-500)
                        .expectActivePowerW(-600)
                        .withInfluenceSellToGrid(true)
                        .withMeterActivePowerW(1)
                        .asArguments(),
                Scenario.of("Original charge power is within bounds, levl does nothing")
                        .withOriginalActivePowerW(-500)
                        .withLevlConstraint(-500, 500)
                        .withNextDischargePowerW(0)
                        .withShiftedLevlGridPowerWConstraint(-1500, 1500)
                        .expectResultingOriginalPowerW(-500)
                        .expectActivePowerW(-500)
                        .withInfluenceSellToGrid(true)
                        .withMeterActivePowerW(1)
                        .asArguments(),
                Scenario.of("Original charge power is within bounds, levl discharges")
                        .withOriginalActivePowerW(-500)
                        .withLevlConstraint(-500, 500)
                        .withNextDischargePowerW(100)
                        .withShiftedLevlGridPowerWConstraint(-1500, 1500)
                        .expectResultingOriginalPowerW(-500)
                        .expectActivePowerW(-400)
                        .withInfluenceSellToGrid(true)
                        .withMeterActivePowerW(1)
                        .asArguments(),
                Scenario.of("charge, grid constraint lower bound")
                        .withOriginalActivePowerW(-1000)
                        .withLevlConstraint(-500, 500)
                        .withNextDischargePowerW(-600)
                        .withShiftedLevlGridPowerWConstraint(-450, 1500)
                        .expectResultingOriginalPowerW(-500)
                        .expectActivePowerW(-950)
                        .withInfluenceSellToGrid(true)
                        .withMeterActivePowerW(1)
                        .asArguments(),
               Scenario.of("discharge, grid constraint lower bound")
                        .withOriginalActivePowerW(1000)
                        .withLevlConstraint(-500, 500)
                        .withNextDischargePowerW(600)
                        .withShiftedLevlGridPowerWConstraint(-1500, 450)
                        .expectResultingOriginalPowerW(500)
                        .expectActivePowerW(950)
                        .withInfluenceSellToGrid(true)
                        .withMeterActivePowerW(1)
                        .asArguments()
        );
        // todo: extend with test cases for influenceSellToGrid(false)
    }

    private static Stream<Arguments> applyLevlUsecaseOnlyIncreaseAbsolutePowerScenarios() {
        return Stream.of(
                Scenario.of("Original charge power is below bounds, levl charges")
                        .withOriginalActivePowerW(-1000)
                        .withLevlConstraint(-500, 500)
                        .withNextDischargePowerW(-100)
                        .withShiftedLevlGridPowerWConstraint(-1500, 1500)
                        .expectResultingOriginalPowerW(-500)
                        .expectActivePowerW(-600)
                        .withInfluenceSellToGrid(true)
                        .withMeterActivePowerW(1)
                        .asArguments(),
                Scenario.of("Original charge power is below bounds, levl does nothing")
                        .withOriginalActivePowerW(-1000)
                        .withLevlConstraint(-500, 500)
                        .withNextDischargePowerW(0)
                        .withShiftedLevlGridPowerWConstraint(-1500, 1500)
                        .expectResultingOriginalPowerW(-500)
                        .expectActivePowerW(-500)
                        .withInfluenceSellToGrid(true)
                        .withMeterActivePowerW(1)
                        .asArguments(),
                Scenario.of("Original charge power is below bounds, levl discharges, is ignored")
                        .withOriginalActivePowerW(-1000)
                        .withLevlConstraint(-500, 500)
                        .withNextDischargePowerW(100)
                        .withShiftedLevlGridPowerWConstraint(-1500, 1500)
                        .expectResultingOriginalPowerW(-500)
                        .expectActivePowerW(-500)
                        .withInfluenceSellToGrid(true)
                        .withMeterActivePowerW(1)
                        .asArguments(),
                Scenario.of("Original charge power is above bounds, levl charges, is ignored")
                        .withOriginalActivePowerW(1000)
                        .withLevlConstraint(-500, 500)
                        .withNextDischargePowerW(-100)
                        .withShiftedLevlGridPowerWConstraint(-1500, 1500)
                        .expectResultingOriginalPowerW(500)
                        .expectActivePowerW(500)
                        .withInfluenceSellToGrid(true)
                        .withMeterActivePowerW(1)
                        .asArguments(),
                Scenario.of("Original charge power is above bounds, levl does nothing")
                        .withOriginalActivePowerW(1000)
                        .withLevlConstraint(-500, 500)
                        .withNextDischargePowerW(0)
                        .withShiftedLevlGridPowerWConstraint(-1500, 1500)
                        .expectResultingOriginalPowerW(500)
                        .expectActivePowerW(500)
                        .withInfluenceSellToGrid(true)
                        .withMeterActivePowerW(1)
                        .asArguments(),
                Scenario.of("Original charge power is above bounds, levl discharges")
                        .withOriginalActivePowerW(1000)
                        .withLevlConstraint(-500, 500)
                        .withNextDischargePowerW(100)
                        .withShiftedLevlGridPowerWConstraint(-1500, 1500)
                        .expectResultingOriginalPowerW(500)
                        .expectActivePowerW(600)
                        .withInfluenceSellToGrid(true)
                        .withMeterActivePowerW(1)
                        .asArguments(),
                Scenario.of("Original charge power is within bounds, levl charges")
                        .withOriginalActivePowerW(-500)
                        .withLevlConstraint(-500, 500)
                        .withNextDischargePowerW(-100)
                        .withShiftedLevlGridPowerWConstraint(-1500, 1500)
                        .expectResultingOriginalPowerW(-500)
                        .expectActivePowerW(-600)
                        .withInfluenceSellToGrid(true)
                        .withMeterActivePowerW(1)
                        .asArguments(),
                Scenario.of("Original charge power is within bounds, levl does nothing")
                        .withOriginalActivePowerW(-500)
                        .withLevlConstraint(-500, 500)
                        .withNextDischargePowerW(0)
                        .withShiftedLevlGridPowerWConstraint(-1500, 1500)
                        .expectResultingOriginalPowerW(-500)
                        .expectActivePowerW(-500)
                        .withInfluenceSellToGrid(true)
                        .withMeterActivePowerW(1)
                        .asArguments(),
                Scenario.of("Original charge power is within bounds, levl discharges, is ignored")
                        .withOriginalActivePowerW(-500)
                        .withLevlConstraint(-500, 500)
                        .withNextDischargePowerW(100)
                        .withShiftedLevlGridPowerWConstraint(-1500, 1500)
                        .expectResultingOriginalPowerW(-500)
                        .expectActivePowerW(-500)
                        .withInfluenceSellToGrid(true)
                        .withMeterActivePowerW(1)
                        .asArguments(),
                Scenario.of("charge, grid constraint lower bound")
                        .withOriginalActivePowerW(-1000)
                        .withLevlConstraint(-500, 500)
                        .withNextDischargePowerW(-600)
                        .withShiftedLevlGridPowerWConstraint(-450, 1500)
                        .expectResultingOriginalPowerW(-500)
                        .expectActivePowerW(-950)
                        .withInfluenceSellToGrid(true)
                        .withMeterActivePowerW(1)
                        .asArguments(),
                Scenario.of("discharge, grid constraint lower bound")
                        .withOriginalActivePowerW(1000)
                        .withLevlConstraint(-500, 500)
                        .withNextDischargePowerW(600)
                        .withShiftedLevlGridPowerWConstraint(-1500, 450)
                        .expectResultingOriginalPowerW(500)
                        .expectActivePowerW(950)
                        .withInfluenceSellToGrid(true)
                        .withMeterActivePowerW(1)
                        .asArguments()
        );
    }

    @BeforeEach
    public void setUp() {
    	this.ess = mock(ManagedSymmetricEss.class);
    	this.levlWorkflow = mock(LevlWorkflowReference.class);
    	this.underTest = new LevlControllerAction(this.ess, this.levlWorkflow);
    }

    /**
     * This is a parameterized test method that applies the Levl use case add powers.
     * It calls the method under test with the original unconstrained active power from the scenario.
     *
     * @param scenario the scenario to apply
     * @throws OpenemsError.OpenemsNamedException if an error occurs
     */
    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("applyLevlUsecaseAddPowersScenarios")
    public void applyLevlUsecaseAddPowers(Scenario scenario) throws OpenemsError.OpenemsNamedException {
        when(this.levlWorkflow.determinePrimaryUseCaseConstraints()).thenReturn(scenario.levlConstraint);
        when(this.levlWorkflow.getNextDischargePowerW()).thenReturn(scenario.nextDischargePowerW);
        when(this.levlWorkflow.getLevlUseCaseConstraints()).thenReturn(scenario.gridPowerConstraint);
        when(this.levlWorkflow.isInfluenceSellToGridAllowed()).thenReturn(scenario.influenceSellToGrid);
        when(this.levlWorkflow.getMeterActivePowerW()).thenReturn(new Value<Integer>(null, scenario.meterActivePowerW));

        this.underTest.addPowers(scenario.originalUnconstrainedActivePowerW);

        verify(this.levlWorkflow).setPrimaryUseCaseActivePowerW(scenario.expectedResultingOriginalPowerW);
        verify(this.ess).setActivePowerEquals(scenario.expectedActivePowerW);
        verify(this.ess).setReactivePowerEquals(0);
    }

    /**
     * This is a parameterized test method that applies the Levl use case increase absolute power.
     * It calls the method under test with the original unconstrained active power from the scenario.
     *
     * @param scenario the scenario to apply
     * @throws OpenemsError.OpenemsNamedException if an error occurs
     */
    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("applyLevlUsecaseOnlyIncreaseAbsolutePowerScenarios")
    public void applyLevlUsecaseOnlyIncreaseAbsolutePower(Scenario scenario) throws OpenemsError.OpenemsNamedException {
        when(this.levlWorkflow.determinePrimaryUseCaseConstraints()).thenReturn(scenario.levlConstraint);
        when(this.levlWorkflow.getNextDischargePowerW()).thenReturn(scenario.nextDischargePowerW);
        when(this.levlWorkflow.getLevlUseCaseConstraints()).thenReturn(scenario.gridPowerConstraint);
        // todo: tests definieren mit eigenen cases
        when(this.levlWorkflow.isInfluenceSellToGridAllowed()).thenReturn(scenario.influenceSellToGrid);
        when(this.levlWorkflow.getMeterActivePowerW()).thenReturn(new Value<Integer>(null, scenario.meterActivePowerW));

        this.underTest.onlyIncreaseAbsolutePower(scenario.originalUnconstrainedActivePowerW);

        verify(this.levlWorkflow).setPrimaryUseCaseActivePowerW(scenario.expectedResultingOriginalPowerW);
        verify(this.ess).setActivePowerEquals(scenario.expectedActivePowerW);
        verify(this.ess).setReactivePowerEquals(0);
    }

    private static class Scenario {
        private String description;
        private int originalUnconstrainedActivePowerW;
        private Limit levlConstraint;
        private int nextDischargePowerW;
        private Limit gridPowerConstraint;
        private int expectedResultingOriginalPowerW;
        private int expectedActivePowerW;
        private boolean influenceSellToGrid;
        private int meterActivePowerW;

        public static Scenario of(String decription) {
            return new Scenario(decription);
        }

        private Scenario(String description) {
            this.description = description;
        }

        public Scenario withOriginalActivePowerW(int originalUnconstrainedActivePowerW) {
            this.originalUnconstrainedActivePowerW = originalUnconstrainedActivePowerW;
            return this;
        }

        public Scenario withLevlConstraint(int lower, int upper) {
        	this.levlConstraint = new Limit(lower, upper);
            return this;
        }

        public Scenario withNextDischargePowerW(int nextDischargePowerW) {
            this.nextDischargePowerW = nextDischargePowerW;
            return this;
        }

        public Scenario withShiftedLevlGridPowerWConstraint(int lower, int upper) {
        	this.gridPowerConstraint = new Limit(lower, upper);
            return this;
        }

        public Scenario withInfluenceSellToGrid(boolean influenceSellToGrid) {
        	this.influenceSellToGrid = influenceSellToGrid;
            return this;
        }
        

        public Scenario withMeterActivePowerW(int meterActivePowerW) {
        	this.meterActivePowerW = meterActivePowerW;
            return this;
        }

        public Scenario expectResultingOriginalPowerW(int expectedResultingOriginalPowerW) {
            this.expectedResultingOriginalPowerW = expectedResultingOriginalPowerW;
            return this;
        }

        public Scenario expectActivePowerW(int expectedActivePowerW) {
            this.expectedActivePowerW = expectedActivePowerW;
            return this;
        }

        public Arguments asArguments() {
            return Arguments.of(this);
        }

        @Override
        public String toString() {
            return this.description;
        }
    }
}