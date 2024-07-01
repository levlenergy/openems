package io.openems.edge.levl.controller.controllers.common;

import io.openems.common.exceptions.OpenemsError;
import io.openems.common.exceptions.OpenemsError.OpenemsNamedException;
import io.openems.common.jsonrpc.request.CreateComponentConfigRequest;
import io.openems.common.jsonrpc.request.DeleteComponentConfigRequest;
import io.openems.common.jsonrpc.request.UpdateComponentConfigRequest;
import io.openems.common.types.EdgeConfig;
import io.openems.edge.common.channel.Channel;
import io.openems.edge.common.component.ComponentManager;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.common.user.User;
import org.osgi.service.component.ComponentContext;

import java.time.Clock;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LevlComponentManager implements ComponentManager {
    private Map<String, OpenemsComponent> componentOverwrites = new HashMap<>();
    private ComponentManager openEmsComponentManager;

    public LevlComponentManager(ComponentManager openEmsComponentManager) {
        this.openEmsComponentManager = openEmsComponentManager;
    }

    /**
     * Overwrites an existing component in the component manager with a new value.
     * If the component does not exist, it will be added to the component manager.
     *
     * @param key   the ID of the component to be overwritten
     * @param value the new component to replace the existing one
     */
    public void overwriteComponent(String key, OpenemsComponent value) {
    	this.componentOverwrites.put(key, value);
    }

    @Override
    public Clock getClock() {
        return null;
    }

    @Override
    public List<OpenemsComponent> getEnabledComponents() {
        return null;
    }

    @Override
    public <T extends OpenemsComponent> List<T> getEnabledComponentsOfType(Class<T> clazz) {
        return null;
    }

    @Override
    public List<OpenemsComponent> getAllComponents() {
        return null;
    }

    @Override
    public <T extends OpenemsComponent> T getComponent(String componentId) throws OpenemsError.OpenemsNamedException {
        T overwrittenComponent = (T) this.componentOverwrites.get(componentId);
        if (overwrittenComponent == null) {
            return this.openEmsComponentManager.getComponent(componentId);
        }
        return overwrittenComponent;
    }

    @Override
    public <T extends OpenemsComponent> T getPossiblyDisabledComponent(String componentId) {
        return null;
    }

    @Override
    public EdgeConfig getEdgeConfig() {
        return null;
    }

    @Override
    public String id() {
        return null;
    }

    @Override
    public String alias() {
        return null;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public ComponentContext getComponentContext() {
        return null;
    }

    @Override
    public Channel<?> _channel(String channelName) {
        return null;
    }

    @Override
    public Collection<Channel<?>> channels() {
        return null;
    }

	@Override
	public void handleCreateComponentConfigRequest(User user, CreateComponentConfigRequest request)
			throws OpenemsNamedException {
		this.openEmsComponentManager.handleCreateComponentConfigRequest(user, request);
		
	}

	@Override
	public void handleUpdateComponentConfigRequest(User user, UpdateComponentConfigRequest request)
			throws OpenemsNamedException {
		this.openEmsComponentManager.handleUpdateComponentConfigRequest(user, request);
		
	}

	@Override
	public void handleDeleteComponentConfigRequest(User user, DeleteComponentConfigRequest request)
			throws OpenemsNamedException {
		this.openEmsComponentManager.handleDeleteComponentConfigRequest(user, request);
		
	}
}
