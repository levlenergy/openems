package io.openems.edge.levl.controller.workflow;

import io.openems.common.exceptions.OpenemsError;
import io.openems.common.jsonrpc.base.GenericJsonrpcRequest;
import io.openems.edge.levl.controller.controllers.common.Limit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class LevlControlRequestTest {

    public static final String VALID_JSON = """
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
                   "levlRequestTimestamp": "2024-02-15 15:00:00Z",
                   "influenceSellToGrid": false,
                   "someIrrelevantfield": ""
                }
              }
            """;
    public static final String GRID_WRONG_DATA_TYPE = """
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
                   "sellToGridLimitW": "xyz",
                   "buyFromGridLimitW": 20000,
                   "levlRequestId": "MyID",
                   "levlRequestTimestamp": "2024-02-15 15:00:00Z",
                   "influenceSellToGrid": false
                }
              }
            """;
    public static final LocalDateTime NOW = LocalDateTime.of(2021, 1, 1, 0, 0, 0);

    private static Stream<Arguments> deleteOneLine() {
        return Stream.of(
                Arguments.of(5),
                Arguments.of(6),
                Arguments.of(7),
                Arguments.of(8),
                Arguments.of(9),
                Arguments.of(10),
                Arguments.of(11),
                Arguments.of(12),
                Arguments.of(13)
        );
    }

    @ParameterizedTest
    @MethodSource("deleteOneLine")
    public void isValid_OneFieldMissing(int lineToRemove) throws OpenemsError.OpenemsNamedException {
        var lines = new ArrayList<>(Arrays.stream(VALID_JSON.split("\n")).toList());
        lines.remove(lineToRemove);
        var modifiedJson = String.join("\n", lines);
        var request = GenericJsonrpcRequest.from(modifiedJson);
        assertThatThrownBy(() -> LevlControlRequest.from(request))
                .isInstanceOf(OpenemsError.OpenemsNamedException.class)
                .hasMessageContaining("not a valid Request")
                .hasMessageContaining("null");
    }

    @Test
    public void isValid_ValidRequest() throws OpenemsError.OpenemsNamedException {
        Limit expectedGridPowerLimitW = new Limit(-30000, 20000);
        DischargeRequest expectedDischargeRequest = DischargeRequest.of(NOW,
                "2024-02-15 15:00:00Z",
                "MyID",
                1000,
                false,
                20,
                60
        );
        LevlSocConstraints expectedSocConstraints = new LevlSocConstraints(0, 100, 5, 95);
        var request = GenericJsonrpcRequest.from(VALID_JSON);

        LevlControlRequest underTest = LevlControlRequest.from(request);

        assertThat(underTest.createDischargeRequest(NOW)).isEqualTo(expectedDischargeRequest);
        assertThat(underTest.createGridPowerLimitW()).isEqualTo(expectedGridPowerLimitW);
        assertThat(underTest.createLevlSocConstraints(0, 100)).usingRecursiveComparison().isEqualTo(expectedSocConstraints);
        assertThat(underTest.getEfficiencyPercent()).isEqualTo(new BigDecimal("90"));
    }

    @Test
    public void fieldWrongType() throws OpenemsError.OpenemsNamedException {
        var request = GenericJsonrpcRequest.from(GRID_WRONG_DATA_TYPE);
        assertThatThrownBy(() -> LevlControlRequest.from(request))
                .isInstanceOf(OpenemsError.OpenemsNamedException.class)
                .hasMessageContaining("not a valid Request")
                .hasMessageContaining("xyz");
    }

    @Test
    public void efficiencyPercentIsZero() throws OpenemsError.OpenemsNamedException {
        var json = VALID_JSON.replace("\"efficiencyPercent\": 90,", "\"efficiencyPercent\": 0,");
        var request = GenericJsonrpcRequest.from(json);
        assertThatThrownBy(() -> LevlControlRequest.from(request))
                .isInstanceOf(OpenemsError.OpenemsNamedException.class)
                .hasMessageContaining("efficiencyPercent must be > 0");
    }

    @Test
    public void efficiencyPercentIsNegative() throws OpenemsError.OpenemsNamedException {
        var json = VALID_JSON.replace("\"efficiencyPercent\": 90,", "\"efficiencyPercent\": -0.01,");
        var request = GenericJsonrpcRequest.from(json);
        assertThatThrownBy(() -> LevlControlRequest.from(request))
                .isInstanceOf(OpenemsError.OpenemsNamedException.class)
                .hasMessageContaining("efficiencyPercent must be > 0");
    }

    @Test
    public void efficiencyPercentIsAbove100() throws OpenemsError.OpenemsNamedException {
        var json = VALID_JSON.replace("\"efficiencyPercent\": 90,", "\"efficiencyPercent\": 100.01,");
        var request = GenericJsonrpcRequest.from(json);
        assertThatThrownBy(() -> LevlControlRequest.from(request))
                .isInstanceOf(OpenemsError.OpenemsNamedException.class)
                .hasMessageContaining("efficiencyPercent must be <= 100");
    }
}