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
	private LevlComponentManager underTest = new LevlComponentManager(this.openEmsComponentManager);

	private String component1Key = "component1";
	private String component2Key = "component2";

	private OpenemsComponent component1 = mock(OpenemsComponent.class);
	private OpenemsComponent component2 = mock(OpenemsComponent.class);
	private OpenemsComponent componentLevl = mock(OpenemsComponent.class);

	@Test
	public void getComponent_ComponentNotFound() throws OpenemsError.OpenemsNamedException {
		assertThat(this.underTest.<OpenemsComponent>getComponent(this.component1Key)).isNull();
	}

	@Test
	public void getComponent_GetOverwrittenComponent() throws OpenemsError.OpenemsNamedException {
		this.underTest.overwriteComponent(this.component1Key, this.component1);

		assertThat(this.underTest.<OpenemsComponent>getComponent(this.component1Key)).isSameAs(this.component1);
	}

	@Test
	public void getComponent_ReturnsOpenEmsComponent_IfNoOverwrittenComponent()
			throws OpenemsError.OpenemsNamedException {
		when(this.openEmsComponentManager.getComponent(this.component2Key)).thenReturn(this.component2);
		this.underTest.overwriteComponent(this.component1Key, this.component1);

		assertThat(this.underTest.<OpenemsComponent>getComponent(this.component1Key)).isSameAs(this.component1);
		assertThat(this.underTest.<OpenemsComponent>getComponent(this.component2Key)).isSameAs(this.component2);
	}

	@Test
	public void getComponent_OverwrittenComponent_HasPrecedenceOverOpenEmsComponent()
			throws OpenemsError.OpenemsNamedException {
		when(this.openEmsComponentManager.getComponent(this.component1Key)).thenReturn(this.component1);
		this.underTest.overwriteComponent(this.component1Key, this.componentLevl);

		assertThat(this.underTest.<OpenemsComponent>getComponent(this.component1Key)).isSameAs(this.componentLevl);
	}

}