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
     * Determines the SoC constraint with the levl soc.
     *
     * @param soc the current SoC.
     * @param levlSocPercentDischarged the percentage of the battery capacity that levl has discharged.
     * @return a Limit object representing the SoC constraint.
     */
    public Limit determineSocConstraintWithLevlSocPercent(int soc, int levlSocPercentDischarged) {
        var lowerLimit = this.shouldNotCharge(soc, levlSocPercentDischarged) ? 0 : Integer.MIN_VALUE;
        var upperLimit = this.shouldNotDischarge(soc, levlSocPercentDischarged) ? 0 : Integer.MAX_VALUE;
        return new Limit(lowerLimit, upperLimit);
    }

    private boolean shouldNotDischarge(Integer soc, Integer levlSocPercentDischarged) {
        return soc <= this.getLowerSocLimitPercent(levlSocPercentDischarged);
    }

    private boolean shouldNotCharge(Integer soc, Integer levlSocPercentDischarged) {
        return soc >= this.getUpperSocLimitPercent(levlSocPercentDischarged);
    }

    private int getUpperSocLimitPercent(int levlSocPercentDischarged) {
        if (this.hasLevlOverallDischarged(levlSocPercentDischarged)) {
            return this.socUpperBoundPercent - levlSocPercentDischarged;
        }
        return this.socUpperBoundPercent;
    }

    private int getLowerSocLimitPercent(int levlSocPercentDischarged) {
        if (this.hasLevlOverallCharged(levlSocPercentDischarged)) {
            return this.socLowerBoundPercent - levlSocPercentDischarged;
        }
        return this.socLowerBoundPercent;
    }

    private boolean hasLevlOverallDischarged(int levlSocPercentDischarged) {
        return levlSocPercentDischarged > 0;
    }

    private boolean hasLevlOverallCharged(int levlSocPercentDischarged) {
        return levlSocPercentDischarged < 0;
    }
}