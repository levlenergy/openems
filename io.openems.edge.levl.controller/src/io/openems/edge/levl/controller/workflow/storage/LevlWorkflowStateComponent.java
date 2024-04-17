package io.openems.edge.levl.controller.workflow.storage;

import io.openems.edge.common.channel.Doc;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.common.jsonapi.JsonApi;

public interface LevlWorkflowStateComponent extends OpenemsComponent, JsonApi {

    public enum ChannelId implements io.openems.edge.common.channel.ChannelId {
        ;

        private final Doc doc;

        private ChannelId(Doc doc) {
            this.doc = doc;
        }

        @Override
        public Doc doc() {
            return this.doc;
        }

    }
}