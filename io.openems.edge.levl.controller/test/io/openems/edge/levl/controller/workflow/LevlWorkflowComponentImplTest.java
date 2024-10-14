package io.openems.edge.levl.controller.workflow;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.osgi.service.event.Event;


import io.openems.edge.common.channel.IntegerReadChannel;
import io.openems.edge.common.channel.value.Value;
import io.openems.edge.common.component.ComponentManager;
import io.openems.edge.common.event.EdgeEventConstants;
import io.openems.edge.common.test.DummyComponentContext;
import io.openems.edge.common.test.DummyConfigurationAdmin;
import io.openems.edge.ess.api.ManagedSymmetricEss;
import io.openems.edge.ess.power.api.Phase;
import io.openems.edge.ess.power.api.Power;
import io.openems.edge.ess.power.api.Pwr;
import io.openems.edge.levl.controller.controllers.common.Limit;
import io.openems.edge.levl.controller.workflow.storage.LevlWorkflowStateConfigProvider;
import io.openems.edge.levl.controller.workflow.storage.StorageConfigTestBuilder;
import io.openems.edge.meter.api.ElectricityMeter;

public class LevlWorkflowComponentImplTest {
	public static final LocalDateTime NOW = LocalDateTime.now();
	private static final String ESS_ID = "ess0";
	private static final String WORKFLOW_ID = "workflowId";
	private static final int PHYSICAL_SOC_LOWER_BOUND = 0;
	private static final int PHYSICAL_SOC_UPPER_BOUND = 100;
	private static final int LAST_ACTIVE_POWER_W = 200;
	private static final int PRIMARY_USE_CASE_ACTIVE_POWER_W = 300;
	private static final int CALCULATOR_RESULT_W = 100;
	public static final long REMAINING_DISCHARGE_POWER_WS = 1234;
	private static final String METER_ID = "meterId";
	private ManagedSymmetricEss ess;
	private ElectricityMeter meter;
	private ComponentManager componentManager;
	private LevlWorkflowComponentImpl underTest;
	private LevlPowerCalculator calculator;
	private DischargeState dischargeState;
	private LevlSocConstraints levlSocConstraints;
	private Config config;

	@BeforeEach
	public void setUp() {
		this.config = mock(Config.class);
		when(this.config.id()).thenReturn(WORKFLOW_ID);
		when(this.config.ess_id()).thenReturn(ESS_ID);
		when(this.config.meter_id()).thenReturn(METER_ID);
		when(this.config.physical_soc_lower_bound_percent()).thenReturn(PHYSICAL_SOC_LOWER_BOUND);
		when(this.config.physical_soc_upper_bound_percent()).thenReturn(PHYSICAL_SOC_UPPER_BOUND);
		this.ess = mock(ManagedSymmetricEss.class);
		this.meter = mock(ElectricityMeter.class);
		this.calculator = mock(LevlPowerCalculator.class);
		this.componentManager = mock(ComponentManager.class);
		this.dischargeState = mock(DischargeState.class);
		this.levlSocConstraints = mock(LevlSocConstraints.class);
		this.underTest = new LevlWorkflowComponentImpl();
		this.underTest.ess = this.ess;
		this.underTest.meter = this.meter;
		this.underTest.componentManager = this.componentManager;
		this.underTest.cm = new DummyConfigurationAdmin();
		this.underTest.levlState.calculator = this.calculator;
		this.underTest.levlState.dischargeState = this.dischargeState;
		this.underTest.levlState.levlSocConstraints = this.levlSocConstraints;
		this.underTest.levlWorkflowSavedState = mock(LevlWorkflowStateConfigProvider.class);
		this.underTest.activate(new DummyComponentContext(), this.config);
	}

	@Test
	public void getLevlUseCaseConstraints_SoCConstraintsStricter() {
		this.underTest.levlState.gridPowerLimitW = new Limit(-2000, 1000);
		when(this.meter.getActivePower()).thenReturn(DummyValues.of(300));
		when(this.ess.getActivePower()).thenReturn(DummyValues.of(100));
		when(this.levlSocConstraints.determineLevlUseCaseSocConstraints(this.ess.getSoc()))
				.thenReturn(new Limit(-300, 1500));
		when(this.dischargeState.isInfluenceSellToGridAllowed()).thenReturn(true);
		var expected = new Limit(-300, 1500);

		var result = this.underTest.getLevlUseCaseConstraints(-200);

		assertThat(result).isEqualTo(expected);
	}

	@Test
	public void getLevlUseCaseConstraints_GridConstraintsStricter() {
		this.underTest.levlState.gridPowerLimitW = new Limit(-2000, 1000);
		when(this.meter.getActivePower()).thenReturn(DummyValues.of(300));
		when(this.ess.getActivePower()).thenReturn(DummyValues.of(100));
		when(this.levlSocConstraints.determineLevlUseCaseSocConstraints(this.ess.getSoc()))
				.thenReturn(new Limit(-2700, 3300));
		when(this.dischargeState.isInfluenceSellToGridAllowed()).thenReturn(true);

		var expected = new Limit(-400, 2600);

		var result = this.underTest.getLevlUseCaseConstraints(-200);

		assertThat(result).isEqualTo(expected);
	}

	@Test
	public void getLevlUseCaseConstraints_EssActivePowerNotDefined() {
		this.underTest.levlState.gridPowerLimitW = new Limit(-2000, 1000);
		when(this.ess.getActivePower()).thenReturn(DummyValues.of(null));
		when(this.meter.getActivePower()).thenReturn(DummyValues.of(100));
		when(this.levlSocConstraints.determineLevlUseCaseSocConstraints(this.ess.getSoc()))
				.thenReturn(new Limit(-1000, 2000));
		when(this.dischargeState.isInfluenceSellToGridAllowed()).thenReturn(true);
		var expected = new Limit(0, 0);

		var result = this.underTest.getLevlUseCaseConstraints(-200);

		assertThat(result).isEqualTo(expected);
	}

	@Test
	public void getLevlUseCaseConstraints_MeterActivePowerNotDefined() {
		this.underTest.levlState.gridPowerLimitW = new Limit(-2000, 1000);
		when(this.meter.getActivePower()).thenReturn(DummyValues.of(null));
		when(this.ess.getActivePower()).thenReturn(DummyValues.of(100));
		when(this.levlSocConstraints.determineLevlUseCaseSocConstraints(this.ess.getSoc()))
				.thenReturn(new Limit(-1000, 2000));
		when(this.dischargeState.isInfluenceSellToGridAllowed()).thenReturn(true);
		var expected = new Limit(0, 0);

		var result = this.underTest.getLevlUseCaseConstraints(-200);

		assertThat(result).isEqualTo(expected);
	}
	
	@Test
	public void getLevlUseCaseConstraints_LevlUsecaseNotAllowed() {
		when(this.meter.getActivePower()).thenReturn(DummyValues.of(-100));
		when(this.ess.getActivePower()).thenReturn(DummyValues.of(100));
		when(this.dischargeState.isInfluenceSellToGridAllowed()).thenReturn(false);
		var expected = new Limit(0, 0);

		var result = this.underTest.getLevlUseCaseConstraints(200);

		assertThat(result).isEqualTo(expected);
	}

	@Test
	public void tryToRestoreStateIfRequired_ConfigIsRestored_previouslyCurrentRequestExpired() {
		this.setupClock();
		this.underTest.levlState.dischargeState = new DischargeState();
		var oldConfig = this.underTest.levlState.save();
		assertThat(oldConfig.state().totalDischargeEnergyWsAtBatteryScaledWithEfficiency()).isZero();

		this.setupConfigWithChargeRequestsWithOffsets(-100, 100);

		this.underTest.tryToRestoreStateIfRequired();

		var updatedConfig = this.underTest.levlState.save();
		assertThat(updatedConfig.state().totalDischargeEnergyWsAtBatteryScaledWithEfficiency()).isEqualTo(2);
		assertThat(updatedConfig.state().request().dischargeEnergyWs()).isEqualTo(0);
	}

	@Test
	public void tryToRestoreStateIfRequired_ConfigIsRestored_previouslyCurrentRequestStillActive() {
		this.setupClock();
		this.underTest.levlState.dischargeState = new DischargeState();

		this.setupConfigWithChargeRequestsWithOffsets(-1, 100);

		this.underTest.tryToRestoreStateIfRequired();

		var updatedConfig = this.underTest.levlState.save();
		assertThat(updatedConfig.state().request().dischargeEnergyWs()).isEqualTo(9);
	}

	@Test
	public void tryToRestoreStateIfRequired_ConfigIsRestored_previouslyNextRequestGetsActive() {
		this.setupClock();
		this.underTest.levlState.dischargeState = new DischargeState();

		this.setupConfigWithChargeRequestsWithOffsets(-100, -1);

		this.underTest.tryToRestoreStateIfRequired();

		var updatedConfig = this.underTest.levlState.save();
		assertThat(updatedConfig.state().request().dischargeEnergyWs()).isEqualTo(6);
	}

	@Test
	public void tryToRestoreStateIfRequired_ConfigIsRestored_previouslyNextRequestAlsoExpired() {
		this.setupClock();
		this.underTest.levlState.dischargeState = new DischargeState();

		this.setupConfigWithChargeRequestsWithOffsets(-200, -100);

		this.underTest.tryToRestoreStateIfRequired();

		var updatedConfig = this.underTest.levlState.save();
		assertThat(updatedConfig.state().request().dischargeEnergyWs()).isEqualTo(0);
	}

	private void setupConfigWithChargeRequestsWithOffsets(int currentRequestOffsetSeconds,
			int nextRequestOffsetSeconds) {
		io.openems.edge.levl.controller.workflow.storage.Config storedConfig = StorageConfigTestBuilder
				.aDefaultStorageConfig()
				.withCurrentDischargeRequestStart(NOW.plusSeconds(currentRequestOffsetSeconds).toString())
				.withCurrentDischargeRequestDeadline(NOW.plusSeconds(currentRequestOffsetSeconds + 60).toString())
				.withNextDischargeRequestStart(NOW.plusSeconds(nextRequestOffsetSeconds).toString())
				.withNextDischargeRequestDeadline(NOW.plusSeconds(nextRequestOffsetSeconds + 60).toString()).build();
		when(this.underTest.levlWorkflowSavedState.getConfig()).thenReturn(storedConfig);
	}

	@Test
	public void handleEvent_BeforeControllers() {
		this.setupClock();
		when(this.dischargeState.getCurrentRequestRemainingDischargePowerWs()).thenReturn(REMAINING_DISCHARGE_POWER_WS);
		when(this.calculator.determineNextDischargePowerW(REMAINING_DISCHARGE_POWER_WS))
				.thenReturn(CALCULATOR_RESULT_W);

		this.underTest.handleEvent(new Event(EdgeEventConstants.TOPIC_CYCLE_BEFORE_CONTROLLERS, Map.of()));

		assertThat(this.underTest.getNextDischargePowerW()).isEqualTo(CALCULATOR_RESULT_W);
		verify(this.dischargeState).update(NOW);
	}

	@Test
	public void handleEvent_AfterWrite() {
		this.setupClock();
		var essChannel = mock(IntegerReadChannel.class);
		this.underTest.setPrimaryUseCaseActivePowerW(PRIMARY_USE_CASE_ACTIVE_POWER_W);
		when(this.ess.getDebugSetActivePowerChannel()).thenReturn(essChannel);
		when(essChannel.getNextValue()).thenReturn(DummyValues.of(LAST_ACTIVE_POWER_W));
		when(this.calculator.determineActualLevlPowerW(Optional.of(LAST_ACTIVE_POWER_W),
				PRIMARY_USE_CASE_ACTIVE_POWER_W)).thenReturn(CALCULATOR_RESULT_W);
		when(this.dischargeState.getLastCompletedRequestTimestamp()).thenReturn("lastRequest");

		this.underTest.handleEvent(new Event(EdgeEventConstants.TOPIC_CYCLE_AFTER_WRITE, Map.of()));

		verify(this.dischargeState).update(NOW);
		verify(this.dischargeState).handleRealizedDischargePowerWForOneSecond(CALCULATOR_RESULT_W);
		assertThat(this.underTest.getRealizedPowerWChannel().getNextValue().get())
				.isEqualTo(this.dischargeState.getLastCompletedRequestDischargePowerW());
		assertThat(this.underTest.getActualDischargePowerWChannel().getNextValue().get())
				.isEqualTo(LAST_ACTIVE_POWER_W);
		assertThat(this.underTest.getLevlDischargePowerWChannel().getNextValue().get()).isEqualTo(CALCULATOR_RESULT_W);
		assertThat(this.underTest.getPrimaryUseCaseDischargePowerWChannel().getNextValue().get())
				.isEqualTo(PRIMARY_USE_CASE_ACTIVE_POWER_W);
		assertThat(this.underTest.getLastControlRequestTimestampChannel().getNextValue().get())
				.isEqualTo("lastRequest");
	}

	private static Stream<Arguments> provideData() {
		return Stream.of(Arguments.of(-1000, 1000, 50, 50, new Limit(-100, 2000), new Limit(-100, 1000),
				"intersect with levl constraints"));
	}

	/**
	 * Determines the primary use case constraints based on the provided parameters.
	 *
	 * @param minPowerW The minimum power in watts.
	 * @param maxPowerW The maximum power in watts.
	 * @param socPercent The state of charge percentage.
	 * @param capacity The capacity.
	 * @param levlConstraint The levl constraint.
	 * @param expectedConstraint The expected constraint.
	 * @param description A description of the test case.
	 */
	@ParameterizedTest(name = "{index} {6}")
	@MethodSource("provideData")
	public void determineConstraints(int minPowerW, int maxPowerW, Integer socPercent, Integer capacity,
			Limit levlConstraint, Limit expectedConstraint, String description) {
		this.setupPower(minPowerW, maxPowerW);
		Value<Integer> socValue = DummyValues.of(socPercent);
		when(this.ess.getSoc()).thenReturn(socValue);
		Value<Integer> capacityValue = DummyValues.of(capacity);
		when(this.ess.getCapacity()).thenReturn(capacityValue);
		long totalDischargeEnergyWsAtBatteryScaledWithEfficiency = 1000;
		when(this.dischargeState.getTotalDischargeEnergyWsAtBatteryScaledWithEfficiency())
				.thenReturn(totalDischargeEnergyWsAtBatteryScaledWithEfficiency);
		when(this.levlSocConstraints.determineLimitFromPhysicalSocConstraintAndLevlSocOffset(
				totalDischargeEnergyWsAtBatteryScaledWithEfficiency, socValue, capacityValue))
				.thenReturn(levlConstraint);

		var result = this.underTest.determinePrimaryUseCaseConstraints();

		assertThat(result).isEqualTo(expectedConstraint);
	}

	private void setupPower(int minPowerW, int maxPowerW) {
		Power power = mock(Power.class);
		when(this.ess.getPower()).thenReturn(power);
		when(power.getMinPower(this.ess, Phase.ALL, Pwr.ACTIVE)).thenReturn(minPowerW);
		when(power.getMaxPower(this.ess, Phase.ALL, Pwr.ACTIVE)).thenReturn(maxPowerW);
	}

	private void setupClock() {
		this.underTest.levlState.clock = Clock.fixed(
				NOW.toInstant(this.underTest.levlState.clock.getZone().getRules().getOffset(NOW)),
				this.underTest.levlState.clock.getZone());
	}
}