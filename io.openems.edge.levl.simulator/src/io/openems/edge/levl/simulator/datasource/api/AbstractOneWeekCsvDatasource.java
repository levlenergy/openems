package io.openems.edge.levl.simulator.datasource.api;

import io.openems.common.types.ChannelAddress;
import io.openems.common.types.OpenemsType;
import io.openems.edge.common.component.AbstractOpenemsComponent;
import io.openems.edge.common.component.ComponentManager;
import io.openems.edge.common.event.EdgeEventConstants;
import io.openems.edge.common.type.TypeUtils;
import io.openems.edge.levl.simulator.DataContainer;
import io.openems.edge.levl.simulator.OneWeekDataContainer;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public abstract class AbstractOneWeekCsvDatasource extends AbstractOpenemsComponent
        implements SimulatorDatasource, EventHandler {

    private OneWeekDataContainer data;

    protected abstract ComponentManager getComponentManager();

    protected abstract DataContainer getData() throws NumberFormatException, IOException;

    protected AbstractOneWeekCsvDatasource(io.openems.edge.common.channel.ChannelId[] firstInitialChannelIds,
                                           io.openems.edge.common.channel.ChannelId[]... furtherInitialChannelIds) {
        super(firstInitialChannelIds, furtherInitialChannelIds);
    }

    protected void activateComponent(ComponentContext context, String id, String alias, boolean enabled)
            throws NumberFormatException, IOException {
        super.activate(context, id, alias, enabled);
        this.data = OneWeekDataContainer.of(this.getData());
        this.data.setIndexToCurrentValue(LocalDateTime.now());
    }

    @Override
    public void handleEvent(Event event) {
        if (!this.isEnabled()) {
            return;
        }
        switch (event.getTopic()) {
            case EdgeEventConstants.TOPIC_CYCLE_AFTER_WRITE:
                var now = LocalDateTime.now(this.getComponentManager().getClock());
                this.data.setIndexToCurrentValue(now);
                break;
        }
    }
    
	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> getValues(OpenemsType type, ChannelAddress channelAddress) {
		// First: try full ChannelAddress
		var values = this.data.getValues(channelAddress.toString());
		if (values.isEmpty()) {
			// Not found: try Channel-ID only (without Component-ID)
			values = this.data.getValues(channelAddress.getChannelId());
		}
		return values.stream() //
				.map(v -> (T) TypeUtils.getAsType(type, v)) //
				.toList();
	}

    @Override
    public <T> T getValue(OpenemsType type, ChannelAddress channelAddress) {
        // First: try full ChannelAddress
        var valueOpt = this.data.getValue(channelAddress.toString());
        if (!valueOpt.isPresent()) {
            // Not found: try Channel-ID only (without Component-ID)
            valueOpt = this.data.getValue(channelAddress.getChannelId());
        }
        return TypeUtils.getAsType(type, valueOpt);
    }

    @Override
    public Set<String> getKeys() {
        return this.data.getKeys();
    }

    @Override
    public int getTimeDelta() {
        return 15 * 60;
    }
}
