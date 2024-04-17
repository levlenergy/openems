package io.openems.edge.levl.controller.workflow;

import io.openems.edge.levl.controller.controllers.common.Limit;

public class SocConstraint {
    private final int socLowerBoundPercent;
    private final int socUpperBoundPercent;

    record SocConstraintMemento(int socLowerBoundPercent, int socUpperBoundPercent) {
    }

    public SocConstraint(int socLowerBoundPercent, int socUpperBoundPercent) {
        this.socLowerBoundPercent = socLowerBoundPercent;
        this.socUpperBoundPercent = socUpperBoundPercent;
    }

    public SocConstraintMemento save() {
        return new SocConstraintMemento(socLowerBoundPercent, socUpperBoundPercent);
    }

    public static SocConstraint restore(SocConstraintMemento memento) {
        return new SocConstraint(memento.socLowerBoundPercent, memento.socUpperBoundPercent);
    }

    public Limit determineSocConstraintWithCapacityOffsetPercent(int soc, int levlCapacityPercentDischarged) {
        var lowerLimit = shouldNotCharge(soc, levlCapacityPercentDischarged) ? 0 : Integer.MIN_VALUE;
        var upperLimit = shouldNotDischarge(soc, levlCapacityPercentDischarged) ? 0 : Integer.MAX_VALUE;
        return new Limit(lowerLimit, upperLimit);
    }

    private boolean shouldNotDischarge(Integer soc, Integer levlCapacityPercentDischarged) {
        return soc <= getLowerSocLimitPercent(levlCapacityPercentDischarged);
    }

    private boolean shouldNotCharge(Integer soc, Integer levlCapacityPercentDischarged) {
        return soc >= getUpperSocLimitPercent(levlCapacityPercentDischarged);
    }

    private int getUpperSocLimitPercent(int levlCapacityPercentDischarged) {
        if (hasLevlOverallDischarged(levlCapacityPercentDischarged)) {
            return socUpperBoundPercent - levlCapacityPercentDischarged;
        }
        return socUpperBoundPercent;
    }

    private int getLowerSocLimitPercent(int levlCapacityPercentDischarged) {
        if (hasLevlOverallCharged(levlCapacityPercentDischarged)) {
            return socLowerBoundPercent - levlCapacityPercentDischarged;
        }
        return socLowerBoundPercent;
    }

    private boolean hasLevlOverallDischarged(int levlCapacityPercentDischarged) {
        return levlCapacityPercentDischarged > 0;
    }

    private boolean hasLevlOverallCharged(int levlCapacityPercentDischarged) {
        return levlCapacityPercentDischarged < 0;
    }

}
