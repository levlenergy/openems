package io.openems.edge.levl.simulator.modbus;

import org.junit.jupiter.api.Test;

import io.openems.common.exceptions.OpenemsException;
import io.openems.edge.common.test.AbstractComponentTest.TestCase;
import io.openems.edge.common.test.ComponentTest;

public class SimulatorModbusImplTest {

	private static final String COMPONENT_ID = "modbus0";

	@Test
	public void test() throws OpenemsException, Exception {
		new ComponentTest(new SimulatorModbusImpl()) //
				.activate(MyConfig.create() //
						.setId(COMPONENT_ID) //
						.build()) //
				.next(new TestCase());
	}

}
