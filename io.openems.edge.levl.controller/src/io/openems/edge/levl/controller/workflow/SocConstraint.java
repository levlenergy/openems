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

    /**
     * Saves the state of this SocConstraint object in a new SocConstraintMemento object.
     *
     * @return a new SocConstraintMemento object containing the state of this SocConstraint object.
     */
    public SocConstraintMemento save() {
        return new SocConstraintMemento(this.socLowerBoundPercent, this.socUpperBoundPercent);
    }

    /**
     * Restores a SocConstraint object from a SocConstraintMemento object.
     *
     * @param memento the SocConstraintMemento object to restore from.
     * @return a new SocConstraint object with the state restored from the memento.
     */
    public static SocConstraint restore(SocConstraintMemento memento) {
        return new SocConstraint(memento.socLowerBoundPercent, memento.socUpperBoundPercent);
    }

    /**
     * Determines the SoC constraint with a capacity offset.
     *
     * @param soc the current SoC.
     * @param levlCapacityPercentDischarged the percentage of the LEVL capacity that has been discharged.
     * @return a Limit object representing the SoC constraint.
     */
    public Limit determineSocConstraintWithCapacityOffsetPercent(int soc, int levlCapacityPercentDischarged) {
        var lowerLimit = this.shouldNotCharge(soc, levlCapacityPercentDischarged) ? 0 : Integer.MIN_VALUE;
        var upperLimit = this.shouldNotDischarge(soc, levlCapacityPercentDischarged) ? 0 : Integer.MAX_VALUE;
        return new Limit(lowerLimit, upperLimit);
    }

    private boolean shouldNotDischarge(Integer soc, Integer levlCapacityPercentDischarged) {
        return soc <= this.getLowerSocLimitPercent(levlCapacityPercentDischarged);
    }

    private boolean shouldNotCharge(Integer soc, Integer levlCapacityPercentDischarged) {
        return soc >= this.getUpperSocLimitPercent(levlCapacityPercentDischarged);
    }

    private int getUpperSocLimitPercent(int levlCapacityPercentDischarged) {
        if (this.hasLevlOverallDischarged(levlCapacityPercentDischarged)) {
            return this.socUpperBoundPercent - levlCapacityPercentDischarged;
        }
        return this.socUpperBoundPercent;
    }

    private int getLowerSocLimitPercent(int levlCapacityPercentDischarged) {
        if (this.hasLevlOverallCharged(levlCapacityPercentDischarged)) {
            return this.socLowerBoundPercent - levlCapacityPercentDischarged;
        }
        return this.socLowerBoundPercent;
    }

    private boolean hasLevlOverallDischarged(int levlCapacityPercentDischarged) {
        return levlCapacityPercentDischarged > 0;
    }

    private boolean hasLevlOverallCharged(int levlCapacityPercentDischarged) {
        return levlCapacityPercentDischarged < 0;
    }
}