package io.openems.edge.levl.controller.workflow;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class DischargeStateTest {
	private static final LocalDateTime NOW = LocalDateTime.now();
	private static final int DELAY_START_SECONDS = 100;
	private static final int DURATION_SECONDS = 3600;
	private static final int SECOND_DELAY_START_SECONDS = 4000;
	private static final long CURRENT_REQUEST_REALIZED_DISCHARGE_ENERGY_WS = -400;
	private static final long CURRENT_REQUEST_REMAINING_DISCHARGE_ENERGY_WS = -500;
	private static final BigDecimal CURRENT_REQUEST_EFFICIENTY_PERCENT = BigDecimal.valueOf(90);
	private static final BigDecimal NEXT_REQUEST_EFFICIENTY_PERCENT = BigDecimal.valueOf(95);
	private static final double CURRENT_REQUEST_EFFICIENCY = 0.9;
	private static final String FIRST_REQUEST_TIMESTAMP = "2024-02-15 15:00:00Z";
	private static final String SECOND_REQUEST_TIMESTAMP = "2025-03-25 18:00:00Z";
	private static final String NEW_REQUEST_TIMESTAMP = "2036-04-26 12:00:00Z";
	private static final DischargeRequest FIRST_REQUEST = DischargeRequest.of(NOW, FIRST_REQUEST_TIMESTAMP, "Req01",
			1000, DELAY_START_SECONDS, DURATION_SECONDS);
	private static final DischargeRequest FIRST_REQUEST_WITH_OTHER_VALUES = DischargeRequest.of(NOW,
			FIRST_REQUEST_TIMESTAMP, "Req01", 2000, 0, 7600);
	private static final DischargeRequest SECOND_REQUEST = DischargeRequest.of(NOW, SECOND_REQUEST_TIMESTAMP, "Req02",
			2000, SECOND_DELAY_START_SECONDS, DURATION_SECONDS);
	private static final DischargeRequest NEW_REQUEST = DischargeRequest.of(NOW, NEW_REQUEST_TIMESTAMP, "Req03", 2002,
			SECOND_DELAY_START_SECONDS + 10, DURATION_SECONDS);
	private static final BigDecimal DEFAULT_EFFICIENCY_PERCENT = BigDecimal.valueOf(100);
	private static final int TOTAL_REALISED_DISCHARGE_ENERGY = 10000;
	private static final int TOTAL_REALISED_DISCHARGE_ENERGY_WITH_EFFICIENCY = 9000;

	private DischargeState expected;
	private DischargeState underTest;

	@Test
	void registerNewRequest_FirstRequest() {
		this.underTest = DischargeStateTestBuilder.Default().build();
		this.expected = DischargeStateTestBuilder.Default().withNextRequestEfficiencyPercent(NEXT_REQUEST_EFFICIENTY_PERCENT)
				.withNextRequest(FIRST_REQUEST).build();

		this.underTest.handleReceivedRequest(NEXT_REQUEST_EFFICIENTY_PERCENT, FIRST_REQUEST);

		assertThat(this.underTest).usingRecursiveComparison().isEqualTo(this.expected);
	}

	@Test
	void registerNewRequest_SecondRequestOverwritesFirstRequest() {
		this.underTest = DischargeStateTestBuilder.Default().build();
		this.expected = DischargeStateTestBuilder.Default().withNextRequestEfficiencyPercent(NEXT_REQUEST_EFFICIENTY_PERCENT)
				.withNextRequest(SECOND_REQUEST).build();

		this.underTest.handleReceivedRequest(BigDecimal.TEN, FIRST_REQUEST);
		this.underTest.handleReceivedRequest(NEXT_REQUEST_EFFICIENTY_PERCENT, SECOND_REQUEST);

		assertThat(this.underTest).usingRecursiveComparison().isEqualTo(this.expected);
	}

	@Test
	void registerNewRequest_RequestWithSameId_NewRequestIsUsed() {
		this.underTest = DischargeStateTestBuilder.Default().build();
		DischargeState expected = DischargeStateTestBuilder.Default()
				.withNextRequestEfficiencyPercent(NEXT_REQUEST_EFFICIENTY_PERCENT)
				.withNextRequest(FIRST_REQUEST_WITH_OTHER_VALUES).build();

		this.underTest.handleReceivedRequest(BigDecimal.TEN, FIRST_REQUEST);
		this.underTest.handleReceivedRequest(NEXT_REQUEST_EFFICIENTY_PERCENT, FIRST_REQUEST_WITH_OTHER_VALUES);

		assertThat(this.underTest).usingRecursiveComparison().isEqualTo(expected);
	}

	@Test
	void registerNewRequest_FirstRequestIsActive_SecondRequestIsQueued() {
		DischargeState expected = DischargeStateTestBuilder.Default()
				.withNextRequestEfficiencyPercent(NEXT_REQUEST_EFFICIENTY_PERCENT).withRequest(FIRST_REQUEST)
				.withNextRequest(NEW_REQUEST).build();
		this.underTest = DischargeStateTestBuilder.Default().withRequest(FIRST_REQUEST).build();

		this.underTest.handleReceivedRequest(NEXT_REQUEST_EFFICIENTY_PERCENT, NEW_REQUEST);

		assertThat(this.underTest).usingRecursiveComparison().isEqualTo(expected);
	}

	@Test
	void update_nextRequestHasNotStartedYet() {
		this.expected = DischargeStateTestBuilder.Default().withNextRequestEfficiencyPercent(NEXT_REQUEST_EFFICIENTY_PERCENT)
				.withNextRequest(FIRST_REQUEST).build();
		this.underTest = DischargeStateTestBuilder.Default()
				.withNextRequestEfficiencyPercent(NEXT_REQUEST_EFFICIENTY_PERCENT).withNextRequest(FIRST_REQUEST)
				.build();

		this.underTest.update(NOW.plusSeconds(DELAY_START_SECONDS));

		assertThat(this.underTest).usingRecursiveComparison().isEqualTo(this.expected);
	}

	@Test
	void update_nextRequestShouldStart() {
		this.expected = DischargeStateTestBuilder.Default().withRequest(FIRST_REQUEST)
				.withCurrentRequestRemainingDischargeEnergyWs(FIRST_REQUEST.getDischargeEnergyWs())
				.withNextRequestEfficiencyPercent(DEFAULT_EFFICIENCY_PERCENT)
				.withCurrentEfficiencyPercent(NEXT_REQUEST_EFFICIENTY_PERCENT).build();
		this.underTest = DischargeStateTestBuilder.Default()
				.withNextRequestEfficiencyPercent(NEXT_REQUEST_EFFICIENTY_PERCENT).withNextRequest(FIRST_REQUEST)
				.build();

		this.underTest.update(NOW.plusSeconds(DELAY_START_SECONDS + 1));

		assertThat(this.underTest).usingRecursiveComparison().isEqualTo(this.expected);
	}

	@Test
	void update_requestShouldContinueIfNotExpiredYet() {
		this.expected = DischargeStateTestBuilder.Default().withRequest(FIRST_REQUEST)
				.withCurrentEfficiencyPercent(CURRENT_REQUEST_EFFICIENTY_PERCENT).build();
		this.underTest = DischargeStateTestBuilder.Default().withCurrentEfficiencyPercent(CURRENT_REQUEST_EFFICIENTY_PERCENT)
				.withRequest(FIRST_REQUEST).build();

		this.underTest.update(NOW.plusSeconds(DELAY_START_SECONDS + DURATION_SECONDS));

		assertThat(this.underTest).usingRecursiveComparison().isEqualTo(this.expected);
	}

	@Test
	void update_requestShouldCompleteIfExpired() {
		this.expected = DischargeStateTestBuilder.Default().withCurrentRequestRemainingDischargeEnergyWs(0)
				.withCurrentRequestRealizedDischargeEnergyWs(0)
				.withLastCompletedRequestTimestamp(FIRST_REQUEST_TIMESTAMP)
				.withLastCompletedRequestRealizedDischargeEnergyWs(FIRST_REQUEST.getDischargeEnergyWs())
				.withRequest(DischargeRequest.inactiveRequest())
				.withCurrentEfficiencyPercent(DEFAULT_EFFICIENCY_PERCENT).build();
		this.underTest = DischargeStateTestBuilder.Default().withCurrentEfficiencyPercent(CURRENT_REQUEST_EFFICIENTY_PERCENT)
				.withRequest(FIRST_REQUEST)
				.withCurrentRequestRealizedDischargeEnergyWs(FIRST_REQUEST.getDischargeEnergyWs()).build();

		this.underTest.update(NOW.plusSeconds(DELAY_START_SECONDS + DURATION_SECONDS + 1));

		assertThat(this.underTest).usingRecursiveComparison().isEqualTo(this.expected);
	}

	@Test
	void update_requestShouldStartNextRequestIfNextRequestShouldStart() {
		this.expected = DischargeStateTestBuilder.Default().withRequest(SECOND_REQUEST)
				.withCurrentRequestRemainingDischargeEnergyWs(SECOND_REQUEST.getDischargeEnergyWs())
				.withNextRequestEfficiencyPercent(DEFAULT_EFFICIENCY_PERCENT)
				.withCurrentRequestRealizedDischargeEnergyWs(0)
				.withLastCompletedRequestRealizedDischargeEnergyWs(CURRENT_REQUEST_REALIZED_DISCHARGE_ENERGY_WS)
				.withLastCompletedRequestTimestamp(FIRST_REQUEST_TIMESTAMP)
				.withCurrentEfficiencyPercent(NEXT_REQUEST_EFFICIENTY_PERCENT).build();
		this.underTest = DischargeStateTestBuilder.Default().withRequest(FIRST_REQUEST)
				.withCurrentRequestRemainingDischargeEnergyWs(FIRST_REQUEST.getDischargeEnergyWs())
				.withNextRequest(SECOND_REQUEST).withCurrentEfficiencyPercent(CURRENT_REQUEST_EFFICIENTY_PERCENT)
				.withNextRequestEfficiencyPercent(NEXT_REQUEST_EFFICIENTY_PERCENT).build();

		this.underTest.update(NOW.plusSeconds(SECOND_DELAY_START_SECONDS + 1));

		assertThat(this.underTest).usingRecursiveComparison().isEqualTo(this.expected);
	}

	@Test
	void initAfterRestore_bothRequestsExpired() {
		this.expected = DischargeStateTestBuilder.Default().withCurrentRequestRemainingDischargeEnergyWs(0)
				.withNextRequestEfficiencyPercent(DEFAULT_EFFICIENCY_PERCENT)
				.withCurrentRequestRealizedDischargeEnergyWs(0)
				.withLastCompletedRequestRealizedDischargeEnergyWs(CURRENT_REQUEST_REALIZED_DISCHARGE_ENERGY_WS)
				.withLastCompletedRequestTimestamp(FIRST_REQUEST_TIMESTAMP)
				.withCurrentEfficiencyPercent(DEFAULT_EFFICIENCY_PERCENT).build();
		this.underTest = DischargeStateTestBuilder.Default().withRequest(FIRST_REQUEST)
				.withCurrentRequestRemainingDischargeEnergyWs(FIRST_REQUEST.getDischargeEnergyWs())
				.withNextRequest(SECOND_REQUEST).withCurrentEfficiencyPercent(CURRENT_REQUEST_EFFICIENTY_PERCENT)
				.withNextRequestEfficiencyPercent(NEXT_REQUEST_EFFICIENTY_PERCENT).build();

		this.underTest.initAfterRestore(NOW.plusSeconds(SECOND_DELAY_START_SECONDS + DURATION_SECONDS + 1));

		assertThat(this.underTest).usingRecursiveComparison().isEqualTo(this.expected);
	}

	@Test
	void initAfterRestore_firstRequestActive() {
		this.expected = DischargeStateTestBuilder.Default().withRequest(FIRST_REQUEST)
				.withCurrentRequestRemainingDischargeEnergyWs(FIRST_REQUEST.getDischargeEnergyWs())
				.withNextRequestEfficiencyPercent(CURRENT_REQUEST_EFFICIENTY_PERCENT).withNextRequest(SECOND_REQUEST)
				.withCurrentEfficiencyPercent(CURRENT_REQUEST_EFFICIENTY_PERCENT)
				.withNextRequestEfficiencyPercent(NEXT_REQUEST_EFFICIENTY_PERCENT).build();
		this.underTest = DischargeStateTestBuilder.Default().withRequest(FIRST_REQUEST)
				.withCurrentRequestRemainingDischargeEnergyWs(FIRST_REQUEST.getDischargeEnergyWs())
				.withNextRequest(SECOND_REQUEST).withCurrentEfficiencyPercent(CURRENT_REQUEST_EFFICIENTY_PERCENT)
				.withNextRequestEfficiencyPercent(NEXT_REQUEST_EFFICIENTY_PERCENT).build();

		this.underTest.initAfterRestore(NOW.plusSeconds(DELAY_START_SECONDS + DURATION_SECONDS));

		assertThat(this.underTest).usingRecursiveComparison().isEqualTo(this.expected);
	}

	@Test
	void initAfterRestore_secondRequestActive() {
		this.expected = DischargeStateTestBuilder.Default().withRequest(SECOND_REQUEST)
				.withCurrentRequestRemainingDischargeEnergyWs(SECOND_REQUEST.getDischargeEnergyWs())
				.withCurrentRequestRealizedDischargeEnergyWs(0)
				.withNextRequestEfficiencyPercent(NEXT_REQUEST_EFFICIENTY_PERCENT)
				.withNextRequest(DischargeRequest.inactiveRequest())
				.withCurrentEfficiencyPercent(NEXT_REQUEST_EFFICIENTY_PERCENT)
				.withLastCompletedRequestTimestamp(FIRST_REQUEST_TIMESTAMP)
				.withLastCompletedRequestRealizedDischargeEnergyWs(CURRENT_REQUEST_REALIZED_DISCHARGE_ENERGY_WS)
				.withNextRequestEfficiencyPercent(DEFAULT_EFFICIENCY_PERCENT).build();
		this.underTest = DischargeStateTestBuilder.Default().withRequest(FIRST_REQUEST)
				.withCurrentRequestRemainingDischargeEnergyWs(FIRST_REQUEST.getDischargeEnergyWs())
				.withNextRequest(SECOND_REQUEST).withCurrentEfficiencyPercent(CURRENT_REQUEST_EFFICIENTY_PERCENT)
				.withNextRequestEfficiencyPercent(NEXT_REQUEST_EFFICIENTY_PERCENT).build();

		this.underTest.initAfterRestore(NOW.plusSeconds(SECOND_DELAY_START_SECONDS + 1));

		assertThat(this.underTest).usingRecursiveComparison().isEqualTo(this.expected);
	}

	@Test
	void handleDischargePowerWs_shouldAdjustFieldValuesCorrectly_batteryIsBeingDischarged() {
		int currentRequestRemainingDischargeEnergyWs = 600;
		int newRealizedPowerW = 201;
		this.expected = DischargeStateTestBuilder.Default().withRequest(FIRST_REQUEST)
				.withCurrentEfficiencyPercent(CURRENT_REQUEST_EFFICIENTY_PERCENT)
				.withCurrentRequestRealizedDischargeEnergyWs(
						CURRENT_REQUEST_REALIZED_DISCHARGE_ENERGY_WS + newRealizedPowerW)
				.withCurrentRequestRemainingDischargeEnergyWs(
						currentRequestRemainingDischargeEnergyWs - newRealizedPowerW)
				.withTotalRealizedDischargeEnergyWs(TOTAL_REALISED_DISCHARGE_ENERGY + newRealizedPowerW)
				.withTotalDischargeEnergyWsAtBatteryScaledWithEfficiency(
						(long) (TOTAL_REALISED_DISCHARGE_ENERGY_WITH_EFFICIENCY
								+ newRealizedPowerW / CURRENT_REQUEST_EFFICIENCY))
				.build();

		this.underTest = DischargeStateTestBuilder.Default()
				.withCurrentRequestRemainingDischargeEnergyWs(currentRequestRemainingDischargeEnergyWs)
				.withRequest(FIRST_REQUEST).withCurrentEfficiencyPercent(CURRENT_REQUEST_EFFICIENTY_PERCENT).build();

		this.underTest.handleRealizedDischargePowerWForOneSecond(newRealizedPowerW);

		assertThat(this.underTest).usingRecursiveComparison().isEqualTo(this.expected);
	}

	@Test
	void handleDischargePowerWs_shouldAdjustFieldValuesCorrectly_batteryIsBeingCharged() {
		int newRealizedPowerW = -300;
		this.expected = DischargeStateTestBuilder.Default()
				.withRequest(FIRST_REQUEST)
				.withCurrentEfficiencyPercent(CURRENT_REQUEST_EFFICIENTY_PERCENT)
				.withCurrentRequestRealizedDischargeEnergyWs(
						CURRENT_REQUEST_REALIZED_DISCHARGE_ENERGY_WS + newRealizedPowerW)
				.withCurrentRequestRemainingDischargeEnergyWs(
						CURRENT_REQUEST_REMAINING_DISCHARGE_ENERGY_WS - newRealizedPowerW)
				.withTotalRealizedDischargeEnergyWs(TOTAL_REALISED_DISCHARGE_ENERGY + newRealizedPowerW)
				.withTotalDischargeEnergyWsAtBatteryScaledWithEfficiency(
						(long) (TOTAL_REALISED_DISCHARGE_ENERGY_WITH_EFFICIENCY
								+ CURRENT_REQUEST_EFFICIENCY * newRealizedPowerW))
				.build();
		this.underTest = DischargeStateTestBuilder.Default().withRequest(FIRST_REQUEST)
				.withCurrentEfficiencyPercent(CURRENT_REQUEST_EFFICIENTY_PERCENT).build();

		this.underTest.handleRealizedDischargePowerWForOneSecond(newRealizedPowerW);

		assertThat(this.underTest).usingRecursiveComparison().isEqualTo(this.expected);
	}

	@Test
	void handleDischargePowerWs_shouldAdjustFieldValuesCorrectly_batteryIsBeingChargedDischargedCharged() {
		this.expected = DischargeStateTestBuilder.Default().withRequest(FIRST_REQUEST)
				.withCurrentEfficiencyPercent(CURRENT_REQUEST_EFFICIENTY_PERCENT)
				.withCurrentRequestRealizedDischargeEnergyWs(CURRENT_REQUEST_REALIZED_DISCHARGE_ENERGY_WS + 390)
				.withCurrentRequestRemainingDischargeEnergyWs(CURRENT_REQUEST_REMAINING_DISCHARGE_ENERGY_WS - 390)
				.withTotalRealizedDischargeEnergyWs(TOTAL_REALISED_DISCHARGE_ENERGY + 390)
				.withTotalDischargeEnergyWsAtBatteryScaledWithEfficiency(
						TOTAL_REALISED_DISCHARGE_ENERGY_WITH_EFFICIENCY + 560)
				.build();
		this.underTest = DischargeStateTestBuilder.Default().withRequest(FIRST_REQUEST)
				.withCurrentEfficiencyPercent(CURRENT_REQUEST_EFFICIENTY_PERCENT).build();

		this.underTest.handleRealizedDischargePowerWForOneSecond(900); // scaled: 1000
		this.underTest.handleRealizedDischargePowerWForOneSecond(-600); // scaled: -540
		this.underTest.handleRealizedDischargePowerWForOneSecond(90); // scaled: 100

		assertThat(this.underTest).usingRecursiveComparison().isEqualTo(this.expected);
	}

	@Test
	void handleDischargePowerWs_requestShouldCompleteIfSignChanged_Discharge() {
		int newRealizedPowerW = 601;
		this.expected = DischargeStateTestBuilder.Default().withRequest(DischargeRequest.inactiveRequest())
				.withCurrentEfficiencyPercent(DEFAULT_EFFICIENCY_PERCENT).withCurrentRequestRealizedDischargeEnergyWs(0)
				.withCurrentRequestRemainingDischargeEnergyWs(0)
				.withTotalRealizedDischargeEnergyWs(TOTAL_REALISED_DISCHARGE_ENERGY + newRealizedPowerW)
				.withTotalDischargeEnergyWsAtBatteryScaledWithEfficiency(
						(long) (TOTAL_REALISED_DISCHARGE_ENERGY_WITH_EFFICIENCY
								+ newRealizedPowerW / CURRENT_REQUEST_EFFICIENCY + 1))
				.withLastCompletedRequestTimestamp(FIRST_REQUEST_TIMESTAMP)
				.withLastCompletedRequestRealizedDischargeEnergyWs(
						CURRENT_REQUEST_REALIZED_DISCHARGE_ENERGY_WS + newRealizedPowerW)
				.build();
		this.underTest = DischargeStateTestBuilder.Default().withCurrentRequestRemainingDischargeEnergyWs(600)
				.withRequest(FIRST_REQUEST).withCurrentEfficiencyPercent(CURRENT_REQUEST_EFFICIENTY_PERCENT).build();

		this.underTest.handleRealizedDischargePowerWForOneSecond(newRealizedPowerW);

		assertThat(this.underTest).usingRecursiveComparison().isEqualTo(this.expected);
	}

	@Test
	void handleDischargePowerWs_requestShouldCompleteIfSignChanged_Charge() {
		int newRealizedPowerW = -501;
		this.expected = DischargeStateTestBuilder.Default().withRequest(DischargeRequest.inactiveRequest())
				.withCurrentEfficiencyPercent(DEFAULT_EFFICIENCY_PERCENT).withCurrentRequestRealizedDischargeEnergyWs(0)
				.withCurrentRequestRemainingDischargeEnergyWs(0)
				.withTotalRealizedDischargeEnergyWs(TOTAL_REALISED_DISCHARGE_ENERGY + newRealizedPowerW)
				.withTotalDischargeEnergyWsAtBatteryScaledWithEfficiency(
						(long) (TOTAL_REALISED_DISCHARGE_ENERGY_WITH_EFFICIENCY
								+ CURRENT_REQUEST_EFFICIENCY * newRealizedPowerW))
				.withLastCompletedRequestTimestamp(FIRST_REQUEST_TIMESTAMP)
				.withLastCompletedRequestRealizedDischargeEnergyWs(
						CURRENT_REQUEST_REALIZED_DISCHARGE_ENERGY_WS + newRealizedPowerW)
				.build();
		this.underTest = DischargeStateTestBuilder.Default().withRequest(FIRST_REQUEST)
				.withCurrentEfficiencyPercent(CURRENT_REQUEST_EFFICIENTY_PERCENT).build();

		this.underTest.handleRealizedDischargePowerWForOneSecond(newRealizedPowerW);

		assertThat(this.underTest).usingRecursiveComparison().isEqualTo(this.expected);
	}

	@Test
	void handleDischargePowerWs_inactiveRequestShouldDoNothing() {
		this.expected = DischargeStateTestBuilder.Default().withRequest(DischargeRequest.inactiveRequest()).build();
		this.underTest = DischargeStateTestBuilder.Default().withRequest(DischargeRequest.inactiveRequest()).build();

		this.underTest.handleRealizedDischargePowerWForOneSecond(300);

		assertThat(this.underTest).usingRecursiveComparison().isEqualTo(this.expected);
	}

	@Test
	void handleDischargePowerWs_doesNotGoBeyondZero() {
		int newRealizedPowerW = -600;
		this.expected = DischargeStateTestBuilder.Default().withRequest(DischargeRequest.inactiveRequest())
				.withCurrentEfficiencyPercent(DEFAULT_EFFICIENCY_PERCENT).withCurrentRequestRealizedDischargeEnergyWs(0)
				.withCurrentRequestRemainingDischargeEnergyWs(0)
				.withTotalRealizedDischargeEnergyWs(TOTAL_REALISED_DISCHARGE_ENERGY + newRealizedPowerW)
				.withTotalDischargeEnergyWsAtBatteryScaledWithEfficiency(
						(long) (TOTAL_REALISED_DISCHARGE_ENERGY_WITH_EFFICIENCY
								+ CURRENT_REQUEST_EFFICIENCY * newRealizedPowerW))
				.withLastCompletedRequestTimestamp(FIRST_REQUEST_TIMESTAMP)
				.withLastCompletedRequestRealizedDischargeEnergyWs(
						CURRENT_REQUEST_REALIZED_DISCHARGE_ENERGY_WS + newRealizedPowerW)
				.build();
		this.underTest = DischargeStateTestBuilder.Default().withRequest(FIRST_REQUEST)
				.withCurrentEfficiencyPercent(CURRENT_REQUEST_EFFICIENTY_PERCENT).build();

		this.underTest.handleRealizedDischargePowerWForOneSecond(newRealizedPowerW);
		this.underTest.handleRealizedDischargePowerWForOneSecond(100);

		assertThat(this.underTest).usingRecursiveComparison().isEqualTo(this.expected);
	}

	@Test
	void save() {
		DischargeState.DischargeStateMemento expected = new DischargeState.DischargeStateMemento(
				TOTAL_REALISED_DISCHARGE_ENERGY, TOTAL_REALISED_DISCHARGE_ENERGY_WITH_EFFICIENCY,
				CURRENT_REQUEST_REMAINING_DISCHARGE_ENERGY_WS, CURRENT_REQUEST_REALIZED_DISCHARGE_ENERGY_WS, 100,
				CURRENT_REQUEST_EFFICIENTY_PERCENT, NEXT_REQUEST_EFFICIENTY_PERCENT, NEW_REQUEST_TIMESTAMP,
				FIRST_REQUEST.save(), SECOND_REQUEST.save());
		this.underTest = DischargeStateTestBuilder.Default().withRequest(FIRST_REQUEST).withNextRequest(SECOND_REQUEST)
				.withCurrentRequestRealizedDischargeEnergyWs(CURRENT_REQUEST_REALIZED_DISCHARGE_ENERGY_WS)
				.withCurrentRequestRemainingDischargeEnergyWs(CURRENT_REQUEST_REMAINING_DISCHARGE_ENERGY_WS)
				.withTotalRealizedDischargeEnergyWs(TOTAL_REALISED_DISCHARGE_ENERGY)
				.withTotalDischargeEnergyWsAtBatteryScaledWithEfficiency(
						TOTAL_REALISED_DISCHARGE_ENERGY_WITH_EFFICIENCY)
				.withCurrentEfficiencyPercent(CURRENT_REQUEST_EFFICIENTY_PERCENT)
				.withNextRequestEfficiencyPercent(NEXT_REQUEST_EFFICIENTY_PERCENT)
				.withLastCompletedRequestRealizedDischargeEnergyWs(100)
				.withLastCompletedRequestTimestamp(NEW_REQUEST_TIMESTAMP).build();

		DischargeState.DischargeStateMemento actual = this.underTest.save();

		assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
	}

	@Test
	void restore() {
		DischargeState.DischargeStateMemento memento = new DischargeState.DischargeStateMemento(
				TOTAL_REALISED_DISCHARGE_ENERGY, TOTAL_REALISED_DISCHARGE_ENERGY_WITH_EFFICIENCY,
				CURRENT_REQUEST_REMAINING_DISCHARGE_ENERGY_WS, CURRENT_REQUEST_REALIZED_DISCHARGE_ENERGY_WS, 100,
				CURRENT_REQUEST_EFFICIENTY_PERCENT, NEXT_REQUEST_EFFICIENTY_PERCENT, NEW_REQUEST_TIMESTAMP,
				FIRST_REQUEST.save(), SECOND_REQUEST.save());
		DischargeState expected = DischargeStateTestBuilder.Default().withRequest(FIRST_REQUEST)
				.withNextRequest(SECOND_REQUEST)
				.withCurrentRequestRealizedDischargeEnergyWs(CURRENT_REQUEST_REALIZED_DISCHARGE_ENERGY_WS)
				.withCurrentRequestRemainingDischargeEnergyWs(CURRENT_REQUEST_REMAINING_DISCHARGE_ENERGY_WS)
				.withTotalRealizedDischargeEnergyWs(TOTAL_REALISED_DISCHARGE_ENERGY)
				.withTotalDischargeEnergyWsAtBatteryScaledWithEfficiency(
						TOTAL_REALISED_DISCHARGE_ENERGY_WITH_EFFICIENCY)
				.withCurrentEfficiencyPercent(CURRENT_REQUEST_EFFICIENTY_PERCENT)
				.withNextRequestEfficiencyPercent(NEXT_REQUEST_EFFICIENTY_PERCENT)
				.withLastCompletedRequestRealizedDischargeEnergyWs(100)
				.withLastCompletedRequestTimestamp(NEW_REQUEST_TIMESTAMP).build();

		var actual = DischargeState.restore(memento);

		assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
	}
}
