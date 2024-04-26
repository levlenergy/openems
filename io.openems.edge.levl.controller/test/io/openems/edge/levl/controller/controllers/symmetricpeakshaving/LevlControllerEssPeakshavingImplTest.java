package io.openems.edge.levl.controller.controllers.symmetricpeakshaving;

import io.openems.common.exceptions.OpenemsError;
import io.openems.edge.common.component.ComponentManager;
import io.openems.edge.common.test.DummyComponentContext;
import io.openems.edge.ess.api.ManagedSymmetricEss;
import io.openems.edge.levl.controller.controllers.common.ReadOnlyManagedSymmetricEss;
import io.openems.edge.levl.controller.workflow.LevlControllerAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LevlControllerEssPeakshavingImplTest {

    private static final int VALUE = 100;
    private LevlControllerEssPeakshavingImpl underTest;
    private ReadOnlyManagedSymmetricEss wrappedEss;
    private ManagedSymmetricEss ess;
    private ControllerEssPeakShavingImpl realController;
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
        this.realController = mock(ControllerEssPeakShavingImpl.class);
        this.levlControllerAction = mock(LevlControllerAction.class);

        this.underTest = new LevlControllerEssPeakshavingImpl();
        this.underTest.componentManager = mock(ComponentManager.class);
        this.underTest.ess = this.ess;
        this.underTest.activate(new DummyComponentContext(), config);
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
        verify(this.levlControllerAction).onlyIncreaseAbsolutePower(VALUE);
    }

    @Test
    public void run_ControllerSetsNoValue_UseCaseIsNotInvoked() throws OpenemsError.OpenemsNamedException {
        when(this.wrappedEss.getReceivedActivePowerEqualsWithPid()).thenReturn(null);

        this.underTest.run();

        verify(this.realController).run();
        verify(this.wrappedEss).reset();
        verify(this.levlControllerAction, never()).onlyIncreaseAbsolutePower(anyInt());
    }


}