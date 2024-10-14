package io.openems.edge.levl.controller.workflow;

import io.openems.edge.levl.controller.controllers.common.Limit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Nested
public class LevlSocConstraintsTest {

    @BeforeEach
    public void setUp() {
    }

    private static Stream<Arguments> provideDeterminePrimaryUseCaseConstraints() {
        return Stream.of(Arguments.of(18, 1000, -70 * 3600, Limit.unconstrained(), "discharge, only lower limit is changed, but soc high enough"),
                Arguments.of(17, 1000, -70 * 3600, Limit.upperBound(0), "discharge, only lower limit is changed, soc reaches limit"),
                Arguments.of(16, 1000, -70 * 3600, Limit.upperBound(0), "discharge, only lower limit is changed, soc reaches limit"),
                Arguments.of(82, 1000, 70 * 3600, Limit.unconstrained(), "charge, only upper limit is changed, but soc low enough"),
                Arguments.of(83, 1000, 70 * 3600, Limit.lowerBound(0), "charge, only upper limit is changed, soc reaches limit"),
                Arguments.of(84, 1000, 70 * 3600, Limit.lowerBound(0), "charge, only upper limit is changed, soc reaches limit"),
                Arguments.of(null, 1000, -70 * 3600, Limit.unconstrained(), "soc missing, unconstrained"),
                Arguments.of(84, null, -70 * 3600, Limit.unconstrained(), "capacity missing, unconstrained")
        );
    }

    private static Stream<Arguments> provideDetermineLevlUseCaseConstraints() {
        return Stream.of(
                Arguments.of(50, Limit.unconstrained(), "unconstrained"),
                Arguments.of(10, Limit.upperBound(0), "lower bound reached"),
                Arguments.of(90, Limit.lowerBound(0), "upper bound reached"),
                Arguments.of(null, new Limit(0, 0), "soc not defined, empty limit")
        );
    }

    /**
     * This method determines the primary use case constraints based on the provided parameters.
     * It uses a parameterized test with data provided by the 'provideDeterminePrimaryUseCaseConstraints' method.
     *
     * @param soc The state of charge. Can be null.
     * @param capacityWh The capacity in watt-hours.
     * @param levlTotalDischargePowerWs The total discharge power of the LEVL device in watt-seconds.
     * @param expectedLimit The expected limit.
     * @param description A description of the test case.
     */
    @ParameterizedTest(name = "{index} {4}")
    @MethodSource("provideDeterminePrimaryUseCaseConstraints")
    public void determinePrimaryUseCaseConstraints(Integer soc, Integer capacityWh, int levlTotalDischargePowerWs, Limit expectedLimit, String description) {
        var underTest = new LevlSocConstraints(10, 90, 0, 0);

        var result = underTest.determineLimitFromPhysicalSocConstraintAndLevlSocOffset(levlTotalDischargePowerWs, DummyValues.of(soc), DummyValues.of(capacityWh));

        assertThat(result).isEqualTo(expectedLimit);
    }

    /**
     * This method determines the lower state of charge (SoC) limit percent based on the provided parameters.
     * It uses a parameterized test with data provided by the 'provideDetermineLevlUseCaseConstraints' method.
     *
     * @param soc The state of charge. Can be null.
     * @param expectedLevlPowerWs The expected power of the LEVL device in watts.
     * @param description A description of the test case.
     */
    @ParameterizedTest(name = "{index} {2}")
    @MethodSource("provideDetermineLevlUseCaseConstraints")
    public void getLowerSocLimitPercent(Integer soc, Limit expectedLevlPowerWs, String description) {
        var underTest = new LevlSocConstraints(0, 0, 10, 90);

        var result = underTest.determineLevlUseCaseSocConstraints(DummyValues.of(soc));

        assertThat(result).isEqualTo(expectedLevlPowerWs);
    }
}