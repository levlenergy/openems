package io.openems.edge.levl.simulator;

import java.time.LocalDateTime;
import java.util.List;

public class OneWeekDataContainer extends DataContainer {

    public  static  OneWeekDataContainer of( DataContainer container) {
        return new OneWeekDataContainer(container.records);
    }

    public OneWeekDataContainer(List<Float[]> records) {
        super(records);
    }

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
