package io.openems.edge.levl.controller.workflow;

import io.openems.edge.common.channel.IntegerDoc;
import io.openems.edge.common.channel.value.Value;
import io.openems.edge.common.component.OpenemsComponent;
import org.mockito.Mockito;

public class DummyValues {

    public static Value<Integer> of(Integer value) {
        return new Value<>(new IntegerDoc().createChannelInstance(Mockito.mock(OpenemsComponent.class), OpenemsComponent.ChannelId.STATE), value);
    }
}
