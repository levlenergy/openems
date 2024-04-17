package io.openems.edge.levl.simulator.meter.grid.acting;

import org.junit.jupiter.api.Test;

import io.openems.common.exceptions.OpenemsException;
import io.openems.edge.common.test.ComponentTest;
import io.openems.edge.common.test.DummyConfigurationAdmin;
import io.openems.edge.levl.simulator.datasource.csv.direct.SimulatorDatasourceCsvDirectImpl;

public class SimulatorGridMeterActingImplTest {

	private static final String COMPONENT_ID = "meter0";
	private static final String DATASOURCE_ID = "datasource0";

	@Test
	public void test() throws OpenemsException, Exception {
		new ComponentTest(new SimulatorGridMeterActingImpl()) //
				.addReference("cm", new DummyConfigurationAdmin()) //
				.addReference("datasource", new SimulatorDatasourceCsvDirectImpl()) //
				.activate(MyConfig.create() //
						.setId(COMPONENT_ID) //
						.setDatasourceId(DATASOURCE_ID) //
						.build()); //
		// .next(new TestCase()); // TODO requires DummyDatasource
	}
}
