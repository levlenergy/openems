package io.openems.edge.ess.samsung.gridmeter;

import org.junit.Test;

import io.openems.edge.bridge.http.dummy.DummyBridgeHttpFactory;
import io.openems.edge.common.test.ComponentTest;
import io.openems.edge.meter.api.MeterType;

public class SamsungEssGridMeterImplTest {

	private static final String COMPONENT_ID = "charger0";

	@Test
	public void test() throws Exception {
		new ComponentTest(new SamsungEssGridMeterImpl()) //
				.addReference("httpBridgeFactory", DummyBridgeHttpFactory.ofDummyBridge()) //
				.activate(MyConfig.create() //
						.setId(COMPONENT_ID) //
						.setIp("127.0.0.1") //
						.setType(MeterType.GRID) //
						.build()) //
		;
	}

}