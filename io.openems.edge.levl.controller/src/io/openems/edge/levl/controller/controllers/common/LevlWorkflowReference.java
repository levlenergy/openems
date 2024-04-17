package io.openems.edge.levl.controller.controllers.common;

public interface LevlWorkflowReference {
    Limit getLevlUseCaseConstraints();

    Limit determinePrimaryUseCaseConstraints();

    void setPrimaryUseCaseActivePowerW(int originalActivePower);

    int getNextDischargePowerW();
}
