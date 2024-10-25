package io.openems.edge.levl.simulator;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import junit.framework.Assert;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.IntStream;

@RunWith(Parameterized.class)
public class OneWeekDataContainerTest {

    public static final int QUARTER_HOURS_ONE_WEEK = 672;

    private final LocalDateTime now;
    private final int secondsOneWeek;
    private final int expectedIndex;
    private OneWeekDataContainer oneWeekDataContainer;

    public OneWeekDataContainerTest(LocalDateTime now, int secondsOneWeek, int expectedIndex) {
        this.now = now;
        this.secondsOneWeek = secondsOneWeek;
        this.expectedIndex = expectedIndex;
    }

    @Before
    public void setUp() {
        oneWeekDataContainer = new OneWeekDataContainer(IntStream.range(0, secondsOneWeek).mapToObj(i -> new Float[0]).toList());
    }

    @After
    public void tearDown() {
        oneWeekDataContainer = null;
    }

    @Parameters
    public static Collection<Object[]> provideData() {
        return Arrays.asList(new Object[][]{
                {LocalDateTime.of(2023, 8, 7, 0, 0, 0), QUARTER_HOURS_ONE_WEEK, 0},
                {LocalDateTime.of(2023, 8, 7, 0, 14, 59), QUARTER_HOURS_ONE_WEEK, 0},
                {LocalDateTime.of(2023, 8, 7, 0, 15, 0), QUARTER_HOURS_ONE_WEEK, 1},
                {LocalDateTime.of(2023, 8, 7, 8, 0, 0), QUARTER_HOURS_ONE_WEEK, 32},
                {LocalDateTime.of(2023, 8, 9, 8, 0, 0), QUARTER_HOURS_ONE_WEEK, 224},
                {LocalDateTime.of(2023, 8, 13, 8, 0, 0), QUARTER_HOURS_ONE_WEEK, 608},
                {LocalDateTime.of(2023, 8, 13, 23, 45, 0), QUARTER_HOURS_ONE_WEEK, 671},
                {LocalDateTime.of(2023, 8, 14, 0, 45, 0), QUARTER_HOURS_ONE_WEEK, 3},
                {LocalDateTime.of(2023, 10, 29, 23, 59, 0), QUARTER_HOURS_ONE_WEEK, 671},
                {LocalDateTime.of(2023, 8, 7, 23, 59, 0), 700, 99},
                {LocalDateTime.of(2023, 8, 13, 23, 59, 0), 100, 99}
        });
    }

    @Test
    public void testSetIndexToCurrentValue() {
        oneWeekDataContainer.setIndexToCurrentValue(now);
        Assert.assertEquals(expectedIndex, oneWeekDataContainer.currentIndex);
    }
}
