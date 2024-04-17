package io.openems.edge.levl.controller.workflow;

import io.openems.common.channel.PersistencePriority;
import io.openems.common.channel.Unit;
import io.openems.common.types.OpenemsType;
import io.openems.edge.common.channel.Doc;
import io.openems.edge.common.channel.IntegerReadChannel;
import io.openems.edge.common.channel.StringReadChannel;
import io.openems.edge.common.channel.value.Value;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.common.jsonapi.JsonApi;

public interface LevlWorkflowComponent extends OpenemsComponent, JsonApi {

    public enum ChannelId implements io.openems.edge.common.channel.ChannelId {
        REALIZED_DISCHARGE_POWER_W(Doc.of(OpenemsType.INTEGER) //
                .unit(Unit.WATT) //
                .persistencePriority(PersistencePriority.HIGH)
                .text("the cumulated amount of discharge power that was realized since the last discharge request (in W)")), //
        ACTUAL_DISCHARGE_POWER_W(Doc.of(OpenemsType.INTEGER) //
                .unit(Unit.WATT) //
                .persistencePriority(PersistencePriority.HIGH)
                .text("the discharge power that was actually applied to the battery (for the current cycle, in W)")), //
        PRIMARY_USE_CASE_DISCHARGE_POWER_W(Doc.of(OpenemsType.INTEGER) //
                .unit(Unit.WATT) //
                .persistencePriority(PersistencePriority.HIGH)
                .text("the discharge power of the primary use case (for the current cycle, in W)")), //
        LEVL_DISCHARGE_POWER_W(Doc.of(OpenemsType.INTEGER) //
                .unit(Unit.WATT) //
                .persistencePriority(PersistencePriority.HIGH)
                .text("the discharge power of the levl use case (for the current cycle, in W)")), //

        REQUEST_TIMESTAMP(Doc.of(OpenemsType.STRING) //
                .persistencePriority(PersistencePriority.HIGH)
                .text("the timestamp of the last levl control request"));

        private final Doc doc;

        private ChannelId(Doc doc) {
            this.doc = doc;
        }

        @Override
        public Doc doc() {
            return this.doc;
        }

    }

    public default IntegerReadChannel getRealizedPowerWChannel() {
        return this.channel(ChannelId.REALIZED_DISCHARGE_POWER_W);
    }

    public default Value<Integer> getRealizedPowerW() {
        return this.getRealizedPowerWChannel().value();
    }

    public default void _setRealizedPowerW(Integer value) {
        this.getRealizedPowerWChannel().setNextValue(value);
    }

    public default IntegerReadChannel getActualDischargePowerWChannel() {
        return this.channel(ChannelId.ACTUAL_DISCHARGE_POWER_W);
    }

    public default StringReadChannel getLastControlRequestTimestampChannel() {
        return this.channel(ChannelId.REQUEST_TIMESTAMP);
    }

    public default Value<String> getLastControlRequestTimestamp() {
        return this.getLastControlRequestTimestampChannel().value();
    }

    public default void _setLastControlRequestTimestamp(String value) {
        this.getLastControlRequestTimestampChannel().setNextValue(value);
    }

    public default Value<Integer> getActualDischargePowerW() {
        return this.getActualDischargePowerWChannel().value();
    }

    public default void _setActualDischargePowerW(Integer value) {
        this.getActualDischargePowerWChannel().setNextValue(value);
    }

    public default IntegerReadChannel getPrimaryUseCaseDischargePowerWChannel() {
        return this.channel(ChannelId.PRIMARY_USE_CASE_DISCHARGE_POWER_W);
    }

    public default Value<Integer> getPrimaryUseCaseDischargePowerW() {
        return this.getPrimaryUseCaseDischargePowerWChannel().value();
    }

    public default void _setPrimaryUseCaseDischargePowerW(Integer value) {
        this.getPrimaryUseCaseDischargePowerWChannel().setNextValue(value);
    }

    public default IntegerReadChannel getLevlDischargePowerWChannel() {
        return this.channel(ChannelId.LEVL_DISCHARGE_POWER_W);
    }

    public default Value<Integer> getLevlDischargePowerW() {
        return this.getLevlDischargePowerWChannel().value();
    }

    public default void _setLevlDischargePowerW(Integer value) {
        this.getLevlDischargePowerWChannel().setNextValue(value);
    }
}