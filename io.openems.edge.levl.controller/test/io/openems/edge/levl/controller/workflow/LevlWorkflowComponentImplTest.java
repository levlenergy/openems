package io.openems.edge.levl.controller.workflow;

import io.openems.common.exceptions.OpenemsError;
import io.openems.common.jsonrpc.base.GenericJsonrpcRequest;
import io.openems.common.jsonrpc.request.UpdateComponentConfigRequest;
import io.openems.edge.common.channel.IntegerReadChannel;
import io.openems.edge.common.channel.value.Value;
import io.openems.edge.common.component.ComponentManager;
import io.openems.edge.common.event.EdgeEventConstants;
import io.openems.edge.common.test.DummyComponentContext;
import io.openems.edge.common.test.DummyConfigurationAdmin;
import io.openems.edge.common.user.User;
import io.openems.edge.ess.api.ManagedSymmetricEss;
import io.openems.edge.ess.power.api.Phase;
import io.openems.edge.ess.power.api.Power;
import io.openems.edge.ess.power.api.Pwr;
import io.openems.edge.levl.controller.controllers.common.Limit;
import io.openems.edge.levl.controller.workflow.storage.LevlWorkflowStateConfigProvider;
import io.openems.edge.levl.controller.workflow.storage.StorageConfigTestBuilder;
import io.openems.edge.meter.api.ElectricityMeter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.osgi.service.event.Event;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

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
        config = mock(Config.class);
        when(config.id()).thenReturn(WORKFLOW_ID);
        when(config.ess_id()).thenReturn(ESS_ID);
        when(config.meter_id()).thenReturn(METER_ID);
        when(config.physical_soc_lower_bound_percent()).thenReturn(PHYSICAL_SOC_LOWER_BOUND);
        when(config.physical_soc_upper_bound_percent()).thenReturn(PHYSICAL_SOC_UPPER_BOUND);
        ess = mock(ManagedSymmetricEss.class);
        meter = mock(ElectricityMeter.class);
        calculator = mock(LevlPowerCalculator.class);
        componentManager = mock(ComponentManager.class);
        dischargeState = mock(DischargeState.class);
        levlSocConstraints = mock(LevlSocConstraints.class);
        underTest = new LevlWorkflowComponentImpl();
        underTest.ess = ess;
        underTest.meter = meter;
        underTest.componentManager = componentManager;
        underTest.cm = new DummyConfigurationAdmin();
        underTest.state.calculator = calculator;
        underTest.state.dischargeState = dischargeState;
        underTest.state.levlSocConstraints = levlSocConstraints;
        underTest.levlWorkflowSavedState = mock(LevlWorkflowStateConfigProvider.class);
        underTest.activate(new DummyComponentContext(), config);
    }

    @Test
    public void getLevlUseCaseConstraints_SoCConstraintsStricter() {
        underTest.state.gridPowerLimitW = new Limit(-2000, 1000);
        when(meter.getActivePower()).thenReturn(DummyValues.of(100));
        underTest.state.actualLevlPowerW = 200;
        when(levlSocConstraints.determineLevlUseCaseSocConstraints(ess.getSoc())).thenReturn(new Limit(-500, 1500));
        var expected = new Limit(-500, 1500);

        var result = underTest.getLevlUseCaseConstraints();

        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void getLevlUseCaseConstraints_GridConstraintsStricter() {
        underTest.state.gridPowerLimitW = new Limit(-2000, 1000);
        when(meter.getActivePower()).thenReturn(DummyValues.of(100));
        underTest.state.actualLevlPowerW = 200;
        when(levlSocConstraints.determineLevlUseCaseSocConstraints(ess.getSoc())).thenReturn(new Limit(-2700, 3300));
        var expected = new Limit(-700, 2300);

        var result = underTest.getLevlUseCaseConstraints();

        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void getLevlUseCaseConstraints_MeterActivePowerNotDefined() {
        underTest.state.gridPowerLimitW = new Limit(-2000, 1000);
        when(meter.getActivePower()).thenReturn(DummyValues.of(null));
        underTest.state.actualLevlPowerW = 200;
        when(levlSocConstraints.determineLevlUseCaseSocConstraints(ess.getSoc())).thenReturn(new Limit(-1000, 2000));
        var expected = new Limit(-1000, 2000);

        var result = underTest.getLevlUseCaseConstraints();

        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void tryToRestoreStateIfRequired_ConfigIsRestored_previouslyCurrentRequestExpired() {
        setupClock();
        underTest.state.dischargeState = new DischargeState();
        var oldConfig = underTest.state.save();
        assertThat(oldConfig.state().totalRealizedDischargeEnergyWs()).isZero();

        setupConfigWithChargeRequestsWithOffsets(-100, 100);

        underTest.tryToRestoreStateIfRequired();

        var updatedConfig = underTest.state.save();
        assertThat(updatedConfig.state().totalRealizedDischargeEnergyWs()).isEqualTo(1);
        assertThat(updatedConfig.state().request().dischargeEnergyWs()).isEqualTo(0);
    }

    @Test
    public void tryToRestoreStateIfRequired_ConfigIsRestored_previouslyCurrentRequestStillActive() {
        setupClock();
        underTest.state.dischargeState = new DischargeState();

        setupConfigWithChargeRequestsWithOffsets(-1, 100);

        underTest.tryToRestoreStateIfRequired();

        var updatedConfig = underTest.state.save();
        assertThat(updatedConfig.state().request().dischargeEnergyWs()).isEqualTo(9);
    }

    @Test
    public void tryToRestoreStateIfRequired_ConfigIsRestored_previouslyNextRequestGetsActive() {
        setupClock();
        underTest.state.dischargeState = new DischargeState();

        setupConfigWithChargeRequestsWithOffsets(-100, -1);

        underTest.tryToRestoreStateIfRequired();

        var updatedConfig = underTest.state.save();
        assertThat(updatedConfig.state().request().dischargeEnergyWs()).isEqualTo(6);
    }

    @Test
    public void tryToRestoreStateIfRequired_ConfigIsRestored_previouslyNextRequestAlsoExpired() {
        setupClock();
        underTest.state.dischargeState = new DischargeState();

        setupConfigWithChargeRequestsWithOffsets(-200, -100);

        underTest.tryToRestoreStateIfRequired();

        var updatedConfig = underTest.state.save();
        assertThat(updatedConfig.state().request().dischargeEnergyWs()).isEqualTo(0);
    }

    private void setupConfigWithChargeRequestsWithOffsets(int currentRequestOffsetSeconds, int nextRequestOffsetSeconds) {
        io.openems.edge.levl.controller.workflow.storage.Config storedConfig = StorageConfigTestBuilder.aDefaultStorageConfig()
                .withCurrentDischargeRequestStart(NOW.plusSeconds(currentRequestOffsetSeconds).toString())
                .withCurrentDischargeRequestDeadline(NOW.plusSeconds(currentRequestOffsetSeconds + 60).toString())
                .withNextDischargeRequestStart(NOW.plusSeconds(nextRequestOffsetSeconds).toString())
                .withNextDischargeRequestDeadline(NOW.plusSeconds(nextRequestOffsetSeconds + 60).toString())
                .build();
        when(underTest.levlWorkflowSavedState.getConfig()).thenReturn(storedConfig);
    }

    @Test
    public void handleEvent_BeforeControllers() {
        setupClock();
        when(dischargeState.getCurrentRequestRemainingDischargePowerWs()).thenReturn(REMAINING_DISCHARGE_POWER_WS);
        when(calculator.determineNextDischargePowerW(REMAINING_DISCHARGE_POWER_WS)).thenReturn(CALCULATOR_RESULT_W);

        underTest.handleEvent(new Event(EdgeEventConstants.TOPIC_CYCLE_BEFORE_CONTROLLERS, Map.of()));

        assertThat(underTest.getNextDischargePowerW()).isEqualTo(CALCULATOR_RESULT_W);
        verify(dischargeState).update(NOW);
    }

    @Test
    public void handleEvent_AfterWrite() {
        setupClock();
        var essChannel = mock(IntegerReadChannel.class);
        underTest.setPrimaryUseCaseActivePowerW(PRIMARY_USE_CASE_ACTIVE_POWER_W);
        when(ess.getDebugSetActivePowerChannel()).thenReturn(essChannel);
        when(essChannel.getNextValue()).thenReturn(DummyValues.of(LAST_ACTIVE_POWER_W));
        when(calculator.determineActualLevlPowerW(Optional.of(LAST_ACTIVE_POWER_W), PRIMARY_USE_CASE_ACTIVE_POWER_W)).thenReturn(CALCULATOR_RESULT_W);
        when(dischargeState.getLastCompletedRequestTimestamp()).thenReturn("lastRequest");

        underTest.handleEvent(new Event(EdgeEventConstants.TOPIC_CYCLE_AFTER_WRITE, Map.of()));

        verify(dischargeState).update(NOW);
        verify(dischargeState).handleRealizedDischargePowerWForOneSecond(CALCULATOR_RESULT_W);
        assertThat(underTest.getRealizedPowerWChannel().getNextValue().get()).isEqualTo(dischargeState.getLastCompletedRequestDischargePowerW());
        assertThat(underTest.getActualDischargePowerWChannel().getNextValue().get()).isEqualTo(LAST_ACTIVE_POWER_W);
        assertThat(underTest.getLevlDischargePowerWChannel().getNextValue().get()).isEqualTo(CALCULATOR_RESULT_W);
        assertThat(underTest.getPrimaryUseCaseDischargePowerWChannel().getNextValue().get()).isEqualTo(PRIMARY_USE_CASE_ACTIVE_POWER_W);
        assertThat(underTest.getLastControlRequestTimestampChannel().getNextValue().get()).isEqualTo("lastRequest");
    }

    private static Stream<Arguments> provideData() {
        return Stream.of(
                Arguments.of(-1000, 1000, 50, 50, new Limit(-100, 2000), new Limit(-100, 1000), "intersect with levl constraints")
        );
    }

    @ParameterizedTest(name = "{index} {6}")
    @MethodSource("provideData")
    public void determineConstraints(int minPowerW, int maxPowerW, Integer socPercent, Integer capacity,
                                     Limit levlConstraint, Limit expectedConstraint, String description) {
        setupPower(minPowerW, maxPowerW);
        Value<Integer> socValue = DummyValues.of(socPercent);
        when(ess.getSoc()).thenReturn(socValue);
        Value<Integer> capacityValue = DummyValues.of(capacity);
        when(ess.getCapacity()).thenReturn(capacityValue);
        long totalDischargeEnergyWsAtBatteryScaledWithEfficiency = 1000;
        when(dischargeState.getTotalDischargeEnergyWsAtBatteryScaledWithEfficiency()).thenReturn(totalDischargeEnergyWsAtBatteryScaledWithEfficiency);
        when(levlSocConstraints.determineLimitFromPhysicalSocConstraintAndLevlSocOffset(totalDischargeEnergyWsAtBatteryScaledWithEfficiency, socValue, capacityValue)).thenReturn(levlConstraint);

        var result = underTest.determinePrimaryUseCaseConstraints();

        assertThat(result).isEqualTo(expectedConstraint);
    }

    @Test
    void handleJsonrpcRequest() throws OpenemsError.OpenemsNamedException, ExecutionException, InterruptedException {
        setupClock();
        String jsonString = """
                  {
                    "id": "00000000-0000-0000-0000-000000000005",
                    "method": "sendLevlControlRequest",
                    "params":
                    {
                       "levlPowerW": 1000,
                       "levlChargeDelaySec": 20,
                       "levlChargeDurationSec": 60,
                       "levlSocLowerBoundPercent": 5,
                       "levlSocUpperBoundPercent": 95,
                       "efficiencyPercent": 90,
                       "sellToGridLimitW": -30000,
                       "buyFromGridLimitW": 20000,
                       "levlRequestId": "MyID",
                       "levlRequestTimestamp": "2024-02-15 15:00:00Z"
                    }
                  }
                """;

        var request = GenericJsonrpcRequest.from(jsonString);
        User user = mock(User.class);

        var result = underTest.handleJsonrpcRequest(user, request);

        DischargeRequest expectedDischargeRequest = DischargeRequest.of(NOW, "2024-02-15 15:00:00Z", "MyID", 1000, 20, 60);
        LevlSocConstraints expectedSocConstraints = new LevlSocConstraints(PHYSICAL_SOC_LOWER_BOUND, PHYSICAL_SOC_UPPER_BOUND, 5, 95);
        verify(dischargeState).handleReceivedRequest(BigDecimal.valueOf(90), expectedDischargeRequest);
        assertThat(underTest.state.gridPowerLimitW).isEqualTo(new Limit(-30000, 20000));
        assertThat(underTest.state.levlSocConstraints).usingRecursiveComparison().isEqualTo(expectedSocConstraints);
        assertThat(result.isDone()).isTrue();
        assertThat(result.get().getId()).isEqualTo(UUID.fromString("00000000-0000-0000-0000-000000000005"));
        assertThat(result.get().getResult().get("levlRequestId").getAsString()).isEqualTo("MyID");
    }

    @Test
    void handleJsonrpcRequest_missingFields() throws OpenemsError.OpenemsNamedException {
        setupClock();
        String jsonString = """
                  {
                    "id": "00000000-0000-0000-0000-000000000005",
                    "method": "sendLevlControlRequest",
                    "params":
                    {
                    }
                  }
                """;

        var request = GenericJsonrpcRequest.from(jsonString);
        User user = mock(User.class);

        assertThatThrownBy(() -> underTest.handleJsonrpcRequest(user, request))
                .isInstanceOf(OpenemsError.OpenemsNamedException.class)
                .hasMessageContaining("not a valid Request");
    }

    @Test
    void handleJsonrpcRequest_WrongMethod_Fails() throws OpenemsError.OpenemsNamedException {
        setupClock();
        String jsonString = """
                  {
                    "id": "00000000-0000-0000-0000-000000000005",
                    "method": "someUnknownMethod",
                    "params":
                    {
                    }
                  }
                """;

        var request = GenericJsonrpcRequest.from(jsonString);
        User user = mock(User.class);

        assertThatThrownBy(() -> underTest.handleJsonrpcRequest(user, request))
                .isInstanceOf(OpenemsError.OpenemsNamedException.class)
                .hasMessageContaining("Unhandled JSON-RPC method [someUnknownMethod]");
    }

    @Test
    public void restoreAndSave() throws OpenemsError.OpenemsNamedException {
        underTest.state.restore(TestObjects.levlWorkflowComponentMemento());
        when(underTest.levlWorkflowSavedState.id()).thenReturn("stateId");
        var captor = ArgumentCaptor.forClass(UpdateComponentConfigRequest.class);
        when(componentManager.handleJsonrpcRequest(any(), captor.capture())).thenReturn(new CompletableFuture<>());

        underTest.saveState();
        var expected = resourceAsString("expectedSavedState.json");
        assertThat(captor.getValue().getParams().toString()).isEqualTo(expected.replaceAll("\\s", ""));
    }

    private String resourceAsString(String name) {
        var stream = getClass().getResourceAsStream(name);
        try {
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setupPower(int minPowerW, int maxPowerW) {
        Power power = mock(Power.class);
        when(ess.getPower()).thenReturn(power);
        when(power.getMinPower(ess, Phase.ALL, Pwr.ACTIVE)).thenReturn(minPowerW);
        when(power.getMaxPower(ess, Phase.ALL, Pwr.ACTIVE)).thenReturn(maxPowerW);
    }

    private void setupClock() {
        underTest.state.clock = Clock.fixed(NOW.toInstant(underTest.state.clock.getZone().getRules().getOffset(NOW)), underTest.state.clock.getZone());
    }
}