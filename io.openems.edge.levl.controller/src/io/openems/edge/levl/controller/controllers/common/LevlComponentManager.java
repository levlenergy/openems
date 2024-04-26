package io.openems.edge.levl.controller.controllers.common;

import io.openems.common.exceptions.OpenemsError;
import io.openems.common.jsonrpc.base.JsonrpcRequest;
import io.openems.common.jsonrpc.base.JsonrpcResponseSuccess;
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
import java.util.concurrent.CompletableFuture;

public class LevlComponentManager implements ComponentManager {
    private Map<String, OpenemsComponent> componentOverwrites = new HashMap<>();
    private ComponentManager openEmsComponentManager;

    public LevlComponentManager(ComponentManager openEmsComponentManager) {
        this.openEmsComponentManager = openEmsComponentManager;
    }

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
    public CompletableFuture<? extends JsonrpcResponseSuccess> handleJsonrpcRequest(User user, JsonrpcRequest request) {
        return null;
    }
}
