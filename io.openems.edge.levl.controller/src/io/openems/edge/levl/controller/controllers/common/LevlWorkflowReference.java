package io.openems.edge.levl.controller.controllers.common;

public interface LevlWorkflowReference {

    /**
     * Gets the levl use case constraints.
     *
     * @param nextPucBatteryPowerW The puc battery power for the next cycle
     * @return The levl use case constraints.
     */
    Limit getLevlUseCaseConstraints(int nextPucBatteryPowerW);

    /**
     * Determines the primary use case constraints which are physical soc bounds and levl soc (reserved energy).
     *
     * @return The primary use case constraints.
     */
    Limit determinePrimaryUseCaseConstraints();

    /**
     * Sets the active power for the primary use case.
     *
     * @param originalActivePower The original active power in watts.
     */
    void setPrimaryUseCaseActivePowerW(int originalActivePower);

    /**
     * Gets the next discharge power in watts.
     *
     * @return The next discharge power in watts.
     */
    int getNextDischargePowerW();
}