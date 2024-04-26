package io.openems.edge.levl.controller.workflow;

import io.openems.edge.common.channel.value.Value;
import io.openems.edge.levl.controller.controllers.common.Limit;
import io.openems.edge.levl.controller.controllers.common.Percent;
import io.openems.edge.levl.controller.controllers.common.Units;

public class LevlSocConstraints {
    private final SocConstraint physicalSocConstraint;
    private final SocConstraint socConstraint;

    record LevlSocConstraintsMemento(SocConstraint.SocConstraintMemento physicalSocConstraint,
                                     SocConstraint.SocConstraintMemento socConstraint) {
    }

    public LevlSocConstraints(int physicalSocLowerBoundPercent, int physicalSocUpperBoundPercent, int levlSocLowerBoundPercent, int levlSocUpperBoundPercent) {
        this.physicalSocConstraint = new SocConstraint(physicalSocLowerBoundPercent, physicalSocUpperBoundPercent);
        this.socConstraint = new SocConstraint(levlSocLowerBoundPercent, levlSocUpperBoundPercent);
    }

    private LevlSocConstraints(SocConstraint physicalSocConstraint, SocConstraint socConstraint) {
        this.physicalSocConstraint = physicalSocConstraint;
        this.socConstraint = socConstraint;
    }

    /**
     * Creates a memento that represents the current state of this LevlSocConstraints object.
     */
    public LevlSocConstraintsMemento save() {
        return new LevlSocConstraintsMemento(this.physicalSocConstraint.save(), this.socConstraint.save());
    }

    /**
     * Restores a LevlSocConstraints object from a memento.
     */
    public static LevlSocConstraints restore(LevlSocConstraintsMemento memento) {
        return new LevlSocConstraints(SocConstraint.restore(memento.physicalSocConstraint), SocConstraint.restore(memento.socConstraint));
    }

    /**
     * Determines the limit based on the physical SoC constraint and the levl SoC offset.
     */
    public Limit determineLimitFromPhysicalSocConstraintAndLevlSocOffset(long totalDischargePowerWs, Value<Integer> soc, Value<Integer> capacity) {
        if (!soc.isDefined() || !capacity.isDefined()) {
            System.out.println("************ soc or capacity not defined\n");
            return Limit.unconstrained();
        }
        var levlCapacityPercentDischarged = this.calculateLevlCapacityOffsetPercentFromDischargePowerWs(totalDischargePowerWs, capacity.get());
        System.out.println("************ levlCapacityPercentDischarged: " + levlCapacityPercentDischarged + "\n");
        return this.physicalSocConstraint.determineSocConstraintWithCapacityOffsetPercent(soc.get(), levlCapacityPercentDischarged);
    }

    /**
     * Determines the levl use case SoC constraints.
     */
    public Limit determineLevlUseCaseSocConstraints(Value<Integer> soc) {
        if (!soc.isDefined()) {
            return Limit.unconstrained();
        }
        return this.socConstraint.determineSocConstraintWithCapacityOffsetPercent(soc.get(), 0);
    }

    private int calculateLevlCapacityOffsetPercentFromDischargePowerWs(long totalWs, int capacity) {
        return Percent.calculatePercentOfTotal(Units.convertWsToWh(totalWs), capacity);
    }

}