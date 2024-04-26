package io.openems.edge.levl.controller.workflow;

import io.openems.edge.levl.controller.workflow.storage.StorageConfigTestBuilder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LevlWorkflowStateConverterTest {

	@Test
	public void asProperties() {
		var state = TestObjects.levlWorkflowComponentMemento();
		var properties = new LevlWorkflowStateConverter().asProperties(state);
		assertThat(properties.stream().map(TestObjects::describe)).containsExactly(
				"primary.use.case.active.power.w=222", "next.discharge.power.w=333", "actual.levl.power.w=111",
				"total.realized.discharge.energy.ws=\"1\"",
				"total.discharge.energy.ws.at.battery.scaled.with.efficiency=\"2\"",
				"current.request.remaining.discharge.energy.ws=\"3\"",
				"current.request.realized.discharge.energy.ws=\"4\"", "last.request.realized.discharge.energy.ws=\"5\"",
				"last.discharge.request.timestamp=\"2024-02-15T12:00:00Z\"",
				"efficiency.percent.multiplied.by.hundred=9903", "current.discharge.request.id=\"id0\"",
				"current.discharge.request.timestamp=\"2024-02-15T15:00:00Z\"",
				"current.discharge.request.energy.ws=\"9\"", "current.discharge.request.start=\"2021-01-01T00:00:10\"",
				"current.discharge.request.deadline=\"2021-01-01T00:00:11\"", "current.discharge.request.active=true",
				"next.discharge.request.id=\"id1\"", "next.discharge.request.timestamp=\"2024-03-13T18:00:00Z\"",
				"next.discharge.request.energy.ws=\"6\"", "next.discharge.request.start=\"2021-01-01T00:00:07\"",
				"next.discharge.request.deadline=\"2021-01-01T00:00:08\"", "next.discharge.request.active=false",
				"levl.soc.constraints.lower.physical.percent=1", "levl.soc.constraints.upper.physical.percent=100",
				"levl.soc.constraints.lower.logical.percent=5", "levl.soc.constraints.upper.logical.percent=95",
				"grid.power.limit.w.lower=-1100", "grid.power.limit.w.upper=1200");
	}

	@Test
	public void fromProperties() {
		var config = StorageConfigTestBuilder.aDefaultStorageConfig().build();
		var converter = new LevlWorkflowStateConverter();

		var state = converter.levlWorkflowComponentFromConfig(config);

		assertThat(state).hasToString(
				"LevlWorkflowStateMemento[primaryUseCaseActivePowerW=222, nextDischargePowerW=333, actualLevlPowerW=111, "
						+ "state=DischargeStateMemento[totalRealizedDischargeEnergyWs=1, totalDischargeEnergyWsAtBatteryScaledWithEfficiency=2, currentRequestRemainingDischargeEnergyWs=3, "
						+ "currentRequestRealizedDischargeEnergyWs=4, lastRequestRealizedDischargeEnergyWs=5, currentRequestEfficiencyPercent=99.01, nextRequestEfficiencyPercent=95.44, lastDischargeRequestTimestamp=2024-02-15 14:45:00Z, "
						+ "request=DischargeRequestMemento[lastRequestId=id0, requestTimestamp=2024-02-15 15:00:00Z, dischargeEnergyWs=9, start=2021-01-01T00:00:10, deadline=2021-01-01T00:00:11, active=true], "
						+ "nextRequest=DischargeRequestMemento[lastRequestId=id1, requestTimestamp=2024-02-15 15:15:00Z, dischargeEnergyWs=6, start=2021-01-01T00:00:07, deadline=2021-01-01T00:00:08, active=false]], "
						+ "levlSocConstraints=LevlSocConstraintsMemento[physicalSocConstraint=SocConstraintMemento[socLowerBoundPercent=1, socUpperBoundPercent=100], socConstraint=SocConstraintMemento[socLowerBoundPercent=5, socUpperBoundPercent=95]], gridPowerLimitW=LimitMemento[minPower=-1100, maxPower=1200]]");
	}

	@Test
	public void fromProperties_CurrentRequest_StartTimestampInvalid() {
		var config = StorageConfigTestBuilder.aDefaultStorageConfig().withCurrentDischargeRequestStart("no date")
				.build();
		var converter = new LevlWorkflowStateConverter();

		var state = converter.levlWorkflowComponentFromConfig(config);

		assertThat(state).hasToString(
				"LevlWorkflowStateMemento[primaryUseCaseActivePowerW=222, nextDischargePowerW=333, actualLevlPowerW=111, "
						+ "state=DischargeStateMemento[totalRealizedDischargeEnergyWs=1, totalDischargeEnergyWsAtBatteryScaledWithEfficiency=2, currentRequestRemainingDischargeEnergyWs=3, "
						+ "currentRequestRealizedDischargeEnergyWs=4, lastRequestRealizedDischargeEnergyWs=5, currentRequestEfficiencyPercent=99.01, nextRequestEfficiencyPercent=95.44, lastDischargeRequestTimestamp=2024-02-15 14:45:00Z, "
						+ "request=DischargeRequestMemento[lastRequestId=, requestTimestamp=, dischargeEnergyWs=0, start=+999999999-12-31T23:59:59.999999999, deadline=+999999999-12-31T23:59:59.999999999, active=false], "
						+ "nextRequest=DischargeRequestMemento[lastRequestId=id1, requestTimestamp=2024-02-15 15:15:00Z, dischargeEnergyWs=6, start=2021-01-01T00:00:07, deadline=2021-01-01T00:00:08, active=false]], "
						+ "levlSocConstraints=LevlSocConstraintsMemento[physicalSocConstraint=SocConstraintMemento[socLowerBoundPercent=1, socUpperBoundPercent=100], socConstraint=SocConstraintMemento[socLowerBoundPercent=5, socUpperBoundPercent=95]], gridPowerLimitW=LimitMemento[minPower=-1100, maxPower=1200]]");
	}

	@Test
	public void fromProperties_NextRequest_StartTimestampInvalid() {
		var config = StorageConfigTestBuilder.aDefaultStorageConfig().withNextDischargeRequestStart("no date").build();
		var converter = new LevlWorkflowStateConverter();

		var state = converter.levlWorkflowComponentFromConfig(config);

		assertThat(state).hasToString(
				"LevlWorkflowStateMemento[primaryUseCaseActivePowerW=222, nextDischargePowerW=333, actualLevlPowerW=111, "
						+ "state=DischargeStateMemento[totalRealizedDischargeEnergyWs=1, totalDischargeEnergyWsAtBatteryScaledWithEfficiency=2, currentRequestRemainingDischargeEnergyWs=3, "
						+ "currentRequestRealizedDischargeEnergyWs=4, lastRequestRealizedDischargeEnergyWs=5, currentRequestEfficiencyPercent=99.01, nextRequestEfficiencyPercent=95.44, lastDischargeRequestTimestamp=2024-02-15 14:45:00Z, "
						+ "request=DischargeRequestMemento[lastRequestId=id0, requestTimestamp=2024-02-15 15:00:00Z, dischargeEnergyWs=9, start=2021-01-01T00:00:10, deadline=2021-01-01T00:00:11, active=true], "
						+ "nextRequest=DischargeRequestMemento[lastRequestId=, requestTimestamp=, dischargeEnergyWs=0, start=+999999999-12-31T23:59:59.999999999, deadline=+999999999-12-31T23:59:59.999999999, active=false]], "
						+ "levlSocConstraints=LevlSocConstraintsMemento[physicalSocConstraint=SocConstraintMemento[socLowerBoundPercent=1, socUpperBoundPercent=100], socConstraint=SocConstraintMemento[socLowerBoundPercent=5, socUpperBoundPercent=95]], gridPowerLimitW=LimitMemento[minPower=-1100, maxPower=1200]]");
	}
}