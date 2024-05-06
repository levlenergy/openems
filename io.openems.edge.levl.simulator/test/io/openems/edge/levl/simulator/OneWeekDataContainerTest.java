package io.openems.edge.levl.simulator;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class OneWeekDataContainerTest {

    public static final int QUARTER_HOURS_ONE_WEEK = 672;

    private static Stream<Arguments> provideData() {
        return Stream.of(
                Arguments.of(LocalDateTime.of(2023, 8, 7, 0, 0, 0), QUARTER_HOURS_ONE_WEEK, 0),
                Arguments.of(LocalDateTime.of(2023, 8, 7, 0, 14, 59), QUARTER_HOURS_ONE_WEEK, 0),
                Arguments.of(LocalDateTime.of(2023, 8, 7, 0, 15, 0), QUARTER_HOURS_ONE_WEEK, 1),
                Arguments.of(LocalDateTime.of(2023, 8, 7, 8, 0, 0), QUARTER_HOURS_ONE_WEEK, 32),
                Arguments.of(LocalDateTime.of(2023, 8, 9, 8, 0, 0), QUARTER_HOURS_ONE_WEEK, 224),
                Arguments.of(LocalDateTime.of(2023, 8, 13, 8, 0, 0), QUARTER_HOURS_ONE_WEEK, 608),
                Arguments.of(LocalDateTime.of(2023, 8, 13, 23, 45, 0), QUARTER_HOURS_ONE_WEEK, 671),
                Arguments.of(LocalDateTime.of(2023, 8, 14, 0, 45, 0), QUARTER_HOURS_ONE_WEEK, 3),
                Arguments.of(LocalDateTime.of(2023, 10, 29, 23, 59, 0), QUARTER_HOURS_ONE_WEEK, 671),
                Arguments.of(LocalDateTime.of(2023, 8, 7, 23, 59, 0), 700, 99),
                Arguments.of(LocalDateTime.of(2023, 8, 13, 23, 59, 0), 100, 99)
        );
    }

    @ParameterizedTest
    @MethodSource("provideData")
    void setIndexToCurrentValue(LocalDateTime now, int secondsOneWeek, int expectedIndex) {
        OneWeekDataContainer oneWeekDataContainer = new OneWeekDataContainer(IntStream.range(0, secondsOneWeek).mapToObj(i -> new Float[0]).toList());
        oneWeekDataContainer.setIndexToCurrentValue(now);
        assertThat(oneWeekDataContainer.currentIndex).isEqualTo(expectedIndex);
    }

}