package io.openems.edge.levl.controller.workflow.storage;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConfigAttributesTest {

    @Test
    public void convertLocalDateTime() {
        var now = java.time.LocalDateTime.now();
        var nowString = ConfigAttributes.CURRENT_DISCHARGE_REQUEST_START.asProperty(now).getValue().getAsString();

        assertThat(ConfigAttributes.parseLocalDateTime(nowString)).isEqualTo(now);
    }

}