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
import static org.mockito.Mockito.*;

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
        wrappedEss = mock(ReadOnlyManagedSymmetricEss.class);
        ess = mock(ManagedSymmetricEss.class);
        realController = mock(ControllerEssPeakShavingImpl.class);
        levlControllerAction = mock(LevlControllerAction.class);

        underTest = new LevlControllerEssPeakshavingImpl();
        underTest.componentManager = mock(ComponentManager.class);
        underTest.ess = ess;
        underTest.activate(new DummyComponentContext(), config);
        underTest.wrappedEss = wrappedEss;
        underTest.realController = realController;
        underTest.levlControllerAction = levlControllerAction;
    }

    @Test
    public void run_ControllerSetsValue() throws OpenemsError.OpenemsNamedException {
        when(wrappedEss.getReceivedActivePowerEqualsWithPid()).thenReturn(VALUE);

        underTest.run();

        verify(realController).run();
        verify(wrappedEss).reset();
        verify(levlControllerAction).onlyIncreaseAbsolutePower(VALUE);
    }

    @Test
    public void run_ControllerSetsNoValue_UseCaseIsNotInvoked() throws OpenemsError.OpenemsNamedException {
        when(wrappedEss.getReceivedActivePowerEqualsWithPid()).thenReturn(null);

        underTest.run();

        verify(realController).run();
        verify(wrappedEss).reset();
        verify(levlControllerAction, never()).onlyIncreaseAbsolutePower(anyInt());
    }


}