package io.openems.edge.levl.controller.workflow.storage;

import io.openems.edge.common.component.OpenemsComponent;

public interface LevlWorkflowStateConfigProvider extends OpenemsComponent {
    Config getConfig();
}
