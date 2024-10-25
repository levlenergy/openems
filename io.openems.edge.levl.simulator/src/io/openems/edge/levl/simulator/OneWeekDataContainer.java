package io.openems.edge.levl.simulator;

import java.time.LocalDateTime;
import java.util.List;

/**
 * This class represents a container that holds data for one week.
 * It extends the DataContainer class.
 */
public class OneWeekDataContainer extends DataContainer {

    /**
     * Factory method to create a new instance of OneWeekDataContainer.
     *
     * @param container The DataContainer instance.
     * @return a new instance of OneWeekDataContainer.
     */
    public  static  OneWeekDataContainer of(DataContainer container) {
        return new OneWeekDataContainer(container.records);
    }

    /**
     * Constructor for the OneWeekDataContainer.
     *
     * @param records The list of records to be stored in the container.
     */
    public OneWeekDataContainer(List<Float[]> records) {
        super(records);
    }

    /**
     * This method sets the current index to the value corresponding to the current time.
     *
     * @param now The current date and time.
     */
    public void setIndexToCurrentValue(LocalDateTime now) {
        int secondsPerMinute = 60;
        int secondsPerHour = 60 * secondsPerMinute;
        int secondsPerDay = 24 * secondsPerHour;
        int secondsPerWeek = 7 * secondsPerDay;
        // determine seconds since Monday  0:00
        int secondsSinceMonday = (now.getDayOfWeek().getValue() - 1) * secondsPerDay + now.getHour() * secondsPerHour + now.getMinute() * secondsPerMinute + now.getSecond();
        // now, determine the correct index, w.r.t the count of the records
        this.currentIndex = secondsSinceMonday * this.records.size() / secondsPerWeek;
    }
}
