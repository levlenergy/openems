package io.openems.edge.levl.controller.common;

public class Efficiency {

	/**
	 * Applies an efficiency to a power/energy outside of the battery.
	 * <p>
	 * Negative values for charge; positive for discharge
	 * </p>
	 * 
	 * @param value             power/energy to which the efficiency should be
	 *                          applied
	 * @param efficiencyPercent efficiency which should be applied
	 * @return the power/energy inside the battery after applying the efficiency
	 */
	public static long apply(long value, double efficiencyPercent) {
		if (value <= 0) { // charge
			return multiplyByEfficiency(value, efficiencyPercent);
		}

		// discharge
		return divideByEfficiency(value, efficiencyPercent);
	}

	/**
	 * Unapplies an efficiency to a power/energy inside of the battery.
	 * 
	 * <p>
	 * negative values for charge; positive for discharge
	 * </p>
	 * 
	 * @param value             power/energy to which the efficiency should be
	 *                          unapplied
	 * @param efficiencyPercent efficiency which should be unapplied
	 * @return the power/energy outside the battery after unapplying the efficiency
	 */
	public static long unapply(long value, double efficiencyPercent) {
		if (value <= 0) { // charge
			return divideByEfficiency(value, efficiencyPercent);
		}

		// discharge
		return multiplyByEfficiency(value, efficiencyPercent);
	}

	private static long divideByEfficiency(long value, double efficiencyPercent) {
		return Math.round(value / (efficiencyPercent / 100));
	}

	private static long multiplyByEfficiency(long value, double efficiencyPercent) {
		return Math.round(value * efficiencyPercent / 100);
	}
}