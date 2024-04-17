package io.openems.edge.levl.simulator.datasource.csv.predefined;

import io.openems.edge.common.component.ComponentManager;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.common.event.EdgeEventConstants;
import io.openems.edge.levl.simulator.CsvUtils;
import io.openems.edge.levl.simulator.DataContainer;
import io.openems.edge.levl.simulator.datasource.api.AbstractOneWeekCsvDatasource;
import io.openems.edge.levl.simulator.datasource.api.SimulatorDatasource;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.*;
import org.osgi.service.event.EventHandler;
import org.osgi.service.event.propertytypes.EventTopics;
import org.osgi.service.metatype.annotations.Designate;

import java.io.IOException;

@Designate(ocd = Config.class, factory = true)
@Component(//
		name = "Simulator.Levl.Datasource.CSV.Predefined", //
		immediate = true, //
		configurationPolicy = ConfigurationPolicy.REQUIRE //
)
@EventTopics({ //
		EdgeEventConstants.TOPIC_CYCLE_AFTER_WRITE //
})
public class SimulatorDatasourceCsvPredefinedImpl extends AbstractOneWeekCsvDatasource
		implements SimulatorDatasourceCsvPredefined, SimulatorDatasource, OpenemsComponent, EventHandler {

	@Reference
	private ComponentManager componentManager;

	private Config config;

	public SimulatorDatasourceCsvPredefinedImpl() {
		super(//
				OpenemsComponent.ChannelId.values(), //
				SimulatorDatasourceCsvPredefined.ChannelId.values() //
		);
	}

	@Activate
	private void activate(ComponentContext context, Config config) throws NumberFormatException, IOException {
		this.config = config;
		super.activateComponent(context, config.id(), config.alias(), config.enabled());
	}

	@Override
	@Deactivate
	protected void deactivate() {
		super.deactivate();
	}

	@Override
	protected ComponentManager getComponentManager() {
		return this.componentManager;
	}

	@Override
	protected DataContainer getData() throws NumberFormatException, IOException {
		return CsvUtils.readCsvFileFromResource(SimulatorDatasourceCsvPredefinedImpl.class,
				this.config.source().filename, this.config.format(), this.config.factor());
	}
}
