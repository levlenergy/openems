package io.openems.edge.levl.simulator.app;


import io.openems.edge.common.test.ComponentTest;
import io.openems.edge.common.test.DummyComponentManager;
import io.openems.edge.common.test.DummyConfigurationAdmin;
import org.junit.jupiter.api.Test;

public class SimulatorAppImplTest {

	@Test
	public void test() throws Exception {
		new ComponentTest(new SimulatorAppImpl()) //
				.addReference("cm", new DummyConfigurationAdmin()) // #
				.addReference("componentManager", new DummyComponentManager()) //
				.activate(MyConfig.create() //
						.setId(SimulatorAppImpl.SINGLETON_SERVICE_PID) //
						.build()) //
		;
	}
}
