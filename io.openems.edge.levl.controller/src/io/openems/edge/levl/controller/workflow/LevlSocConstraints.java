package io.openems.edge.levl.controller.workflow;

import io.openems.edge.common.channel.value.Value;
import io.openems.edge.levl.controller.controllers.common.Limit;
import io.openems.edge.levl.controller.controllers.common.Percent;
import io.openems.edge.levl.controller.controllers.common.Units;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents the SoC constraints for the LEVL controller.
 * It provides methods to determine the limit based on the physical SoC constraint and the levl SoC offset,
 * and to determine the levl use case SoC constraints.
 */
public class LevlSocConstraints {
    private final SocConstraint physicalSocConstraint;
    private final SocConstraint socConstraint;

    private final Logger log = LoggerFactory.getLogger(LevlSocConstraints.class);    
    
    /**
     * This record represents a memento of the LevlSocConstraints object.
     */
    record LevlSocConstraintsMemento(SocConstraint.SocConstraintMemento physicalSocConstraint,
                                     SocConstraint.SocConstraintMemento socConstraint) {
    }

    /**
     * Constructor for the LevlSocConstraints class.
     *
     * @param physicalSocLowerBoundPercent Lower bound for the physical SoC constraint.
     * @param physicalSocUpperBoundPercent Upper bound for the physical SoC constraint.
     * @param levlSocLowerBoundPercent Lower bound for the LEVL SoC constraint.
     * @param levlSocUpperBoundPercent Upper bound for the LEVL SoC constraint.
     */
    public LevlSocConstraints(int physicalSocLowerBoundPercent, int physicalSocUpperBoundPercent, int levlSocLowerBoundPercent, int levlSocUpperBoundPercent) {
        this.physicalSocConstraint = new SocConstraint(physicalSocLowerBoundPercent, physicalSocUpperBoundPercent);
        this.socConstraint = new SocConstraint(levlSocLowerBoundPercent, levlSocUpperBoundPercent);
    }

    
    private LevlSocConstraints(SocConstraint physicalSocConstraint, SocConstraint socConstraint) {
        this.physicalSocConstraint = physicalSocConstraint;
        this.socConstraint = socConstraint;
    }

    /**
     * Saves the current state of this LevlSocConstraints object into a memento.
     *
     * @return A memento that represents the current state of this LevlSocConstraints object.
     */
    public LevlSocConstraintsMemento save() {
        return new LevlSocConstraintsMemento(this.physicalSocConstraint.save(), this.socConstraint.save());
    }

    /**
     * Restores a LevlSocConstraints object from a memento.
     *
     * @param memento The memento from which to restore the LevlSocConstraints object.
     * @return A new instance of LevlSocConstraints restored from the memento.
     */
    public static LevlSocConstraints restore(LevlSocConstraintsMemento memento) {
        return new LevlSocConstraints(SocConstraint.restore(memento.physicalSocConstraint), SocConstraint.restore(memento.socConstraint));
    }
    
    /**
     * Determines the limit based on the physical SoC constraint and the levl SoC.
     *
     * @param totalDischargeEnergyWs Total discharge energy in watt-seconds.
     * @param soc State of Charge value.
     * @param capacity Capacity value.
     * @return The determined limit.
     */
    public Limit determineLimitFromPhysicalSocConstraintAndLevlSocOffset(long totalDischargeEnergyWs, Value<Integer> soc, Value<Integer> capacity) {
        if (!soc.isDefined() || !capacity.isDefined()) {
            this.log.debug("soc or capacity not defined");
			return Limit.unconstrained();
        }
        var levlSocPercentDischarged = this.calculateLevlSocPercentDischarged(totalDischargeEnergyWs, capacity.get());
        this.log.debug("levlSocPercentDischarged: " + levlSocPercentDischarged);
        return this.physicalSocConstraint.determineSocConstraintWithLevlSocPercent(soc.get(), levlSocPercentDischarged);
    }


    /**
     * Determines the levl use case SoC constraints.
     *
     * @param soc State of Charge value.
     * @return The determined levl use case SoC constraints.
     */
    public Limit determineLevlUseCaseSocConstraints(Value<Integer> soc) {
        if (!soc.isDefined()) {
        	this.log.warn("essSoc not defined");
    		return new Limit(0, 0);
        }
        return this.socConstraint.determineSocConstraintWithLevlSocPercent(soc.get(), 0);
    }

    /**
     * Calculates the levl SoC in percent.
     * Positive values means levl has discharged the amount of energy
     * Negative values means levl has charged the amount of energy
     *
     * @param totalWs Total discharge energy in watt-seconds.
     * @param capacity Capacity value.
     * @return The calculated levl soc discharged in percent.
     */
    private int calculateLevlSocPercentDischarged(long totalWs, int capacity) {
        return Percent.calculatePercentOfTotal(Units.convertWsToWh(totalWs), capacity);
    }

}