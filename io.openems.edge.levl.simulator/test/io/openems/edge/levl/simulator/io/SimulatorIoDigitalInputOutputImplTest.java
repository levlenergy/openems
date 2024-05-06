package io.openems.edge.levl.simulator.io;

import org.junit.jupiter.api.Test;

import io.openems.common.exceptions.OpenemsException;
import io.openems.edge.common.test.AbstractComponentTest.TestCase;
import io.openems.edge.common.test.ComponentTest;

public class SimulatorIoDigitalInputOutputImplTest {

	private static final String COMPONENT_ID = "io0";

	@Test
	public void test() throws OpenemsException, Exception {
		new ComponentTest(new SimulatorIoDigitalInputOutputImpl()) //
				.activate(MyConfig.create() //
						.setId(COMPONENT_ID) //
						.setNumberOfOutputs(3) //
						.build()) //
				.next(new TestCase()); //
	}
}
