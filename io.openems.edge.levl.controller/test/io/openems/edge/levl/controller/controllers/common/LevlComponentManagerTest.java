package io.openems.edge.levl.controller.controllers.common;

import io.openems.common.exceptions.OpenemsError;
import io.openems.edge.common.component.ComponentManager;
import io.openems.edge.common.component.OpenemsComponent;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LevlComponentManagerTest {
    private ComponentManager openEmsComponentManager = mock(ComponentManager.class);
    private LevlComponentManager underTest = new LevlComponentManager(openEmsComponentManager);

    private String component1Key = "component1";
    private String component2Key = "component2";

    private OpenemsComponent component1 = mock(OpenemsComponent.class);
    private OpenemsComponent component2 = mock(OpenemsComponent.class);
    private OpenemsComponent componentLevl = mock(OpenemsComponent.class);

    @Test
    public void getComponent_ComponentNotFound() throws OpenemsError.OpenemsNamedException {
        assertThat(underTest.<OpenemsComponent>getComponent(component1Key)).isNull();
    }
    @Test
    public void getComponent_GetOverwrittenComponent() throws OpenemsError.OpenemsNamedException {
        underTest.overwriteComponent(component1Key, component1);

        assertThat(underTest.<OpenemsComponent>getComponent(component1Key)).isSameAs(component1);
    }

    @Test
    public void getComponent_ReturnsOpenEmsComponent_IfNoOverwrittenComponent() throws OpenemsError.OpenemsNamedException {
        when(openEmsComponentManager.getComponent(component2Key)).thenReturn(component2);
        underTest.overwriteComponent(component1Key, component1);

        assertThat(underTest.<OpenemsComponent>getComponent(component1Key)).isSameAs(component1);
        assertThat(underTest.<OpenemsComponent>getComponent(component2Key)).isSameAs(component2);
    }
    @Test
    public void getComponent_OverwrittenComponent_HasPrecedenceOverOpenEmsComponent() throws OpenemsError.OpenemsNamedException {
        when(openEmsComponentManager.getComponent(component1Key)).thenReturn(component1);
        underTest.overwriteComponent(component1Key, componentLevl);

        assertThat(underTest.<OpenemsComponent>getComponent(component1Key)).isSameAs(componentLevl);
    }

}