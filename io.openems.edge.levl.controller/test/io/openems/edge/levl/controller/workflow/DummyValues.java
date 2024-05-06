package io.openems.edge.levl.controller.workflow;

import io.openems.edge.common.channel.IntegerDoc;
import io.openems.edge.common.channel.value.Value;
import io.openems.edge.common.component.OpenemsComponent;
import org.mockito.Mockito;

public class DummyValues {

    /**
     * Creates a new Value instance with the provided integer value.
     *
     * @param value the integer value to be wrapped in a Value instance
     * @return a new Value instance containing the provided integer value
     */
    public static Value<Integer> of(Integer value) {
        return new Value<>(new IntegerDoc().createChannelInstance(Mockito.mock(OpenemsComponent.class), OpenemsComponent.ChannelId.STATE), value);
    }
}