package io.openems.edge.levl.controller.controllers.balancing;

import io.openems.common.exceptions.OpenemsError;
import io.openems.edge.common.test.DummyComponentContext;
import io.openems.edge.common.test.DummyConfigurationAdmin;
import io.openems.edge.ess.api.ManagedSymmetricEss;
import io.openems.edge.levl.controller.controllers.common.ReadOnlyManagedSymmetricEss;
import io.openems.edge.levl.controller.workflow.LevlControllerAction;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LevlControllerEssBalancingImplTest {

	private static final int VALUE = 100;
	private LevlControllerEssBalancingImpl underTest;
	private ReadOnlyManagedSymmetricEss wrappedEss;
	private ManagedSymmetricEss ess;
	private LevlControllerEssBalancingImpl realController;
	private LevlControllerAction levlControllerAction;

	@BeforeEach
	public void setUp() {
		Config config = mock(Config.class);
		when(config.ess_id()).thenReturn("ess0");
		when(config.meter_id()).thenReturn("meter0");
		when(config.levl_workflow_id()).thenReturn("workflow0");
		when(config.id()).thenReturn("controller0");
		this.wrappedEss = mock(ReadOnlyManagedSymmetricEss.class);
		this.ess = mock(ManagedSymmetricEss.class);
		this.realController = mock(LevlControllerEssBalancingImpl.class);
		this.levlControllerAction = mock(LevlControllerAction.class);

		this.underTest = new LevlControllerEssBalancingImpl();
		this.underTest.cm = new DummyConfigurationAdmin();
		this.underTest.activate(new DummyComponentContext(), config);
		this.underTest.ess = this.ess;
		this.underTest.wrappedEss = this.wrappedEss;
		this.underTest.realController = this.realController;
		this.underTest.levlControllerAction = this.levlControllerAction;
	}

	@Test
	public void run_ControllerSetsValue() throws OpenemsError.OpenemsNamedException {
		when(this.wrappedEss.getReceivedActivePowerEqualsWithPid()).thenReturn(VALUE);

		this.underTest.run();

		verify(this.realController).run();
		verify(this.wrappedEss).reset();
		verify(this.levlControllerAction).addPowers(VALUE);
	}

	@Test
	public void run_ControllerSetsNoValue_UseCaseIsNotInvoked() throws OpenemsError.OpenemsNamedException {
		when(this.wrappedEss.getReceivedActivePowerEqualsWithPid()).thenReturn(null);

		this.underTest.run();

		verify(this.realController).run();
		verify(this.wrappedEss).reset();
		verify(this.levlControllerAction, never()).addPowers(anyInt());
	}

}