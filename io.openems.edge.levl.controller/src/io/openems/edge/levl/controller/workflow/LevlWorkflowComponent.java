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

        /**
         * Returns the Doc associated with this ChannelId.
         * @return the Doc
         */
        @Override
        public Doc doc() {
            return this.doc;
        }

    }

    /**
     * Returns the IntegerReadChannel for the realized power.
     * @return the IntegerReadChannel
     */
    public default IntegerReadChannel getRealizedPowerWChannel() {
        return this.channel(ChannelId.REALIZED_DISCHARGE_POWER_W);
    }

    /**
     * Returns the value of the realized power.
     * @return the value of the realized power
     */
    public default Value<Integer> getRealizedPowerW() {
        return this.getRealizedPowerWChannel().value();
    }

    /**
     * Sets the next value of the realized power.
     * @param value the next value
     */
    public default void _setRealizedPowerW(Integer value) {
        this.getRealizedPowerWChannel().setNextValue(value);
    }

    /**
     * Returns the IntegerReadChannel for the actual discharge power.
     * @return the IntegerReadChannel
     */
    public default IntegerReadChannel getActualDischargePowerWChannel() {
        return this.channel(ChannelId.ACTUAL_DISCHARGE_POWER_W);
    }

    /**
     * Returns the StringReadChannel for the last control request timestamp.
     * @return the StringReadChannel
     */
    public default StringReadChannel getLastControlRequestTimestampChannel() {
        return this.channel(ChannelId.REQUEST_TIMESTAMP);
    }

    /**
     * Returns the value of the last control request timestamp.
     * @return the value of the last control request timestamp
     */
    public default Value<String> getLastControlRequestTimestamp() {
        return this.getLastControlRequestTimestampChannel().value();
    }

    /**
     * Sets the next value of the last control request timestamp.
     * @param value the next value
     */
    public default void _setLastControlRequestTimestamp(String value) {
        this.getLastControlRequestTimestampChannel().setNextValue(value);
    }

    /**
     * Returns the value of the actual discharge power.
     * @return the value of the actual discharge power
     */
    public default Value<Integer> getActualDischargePowerW() {
        return this.getActualDischargePowerWChannel().value();
    }

    /**
     * Sets the next value of the actual discharge power.
     * @param value the next value
     */
    public default void _setActualDischargePowerW(Integer value) {
        this.getActualDischargePowerWChannel().setNextValue(value);
    }

    /**
     * Returns the IntegerReadChannel for the primary use case discharge power.
     * @return the IntegerReadChannel
     */
    public default IntegerReadChannel getPrimaryUseCaseDischargePowerWChannel() {
        return this.channel(ChannelId.PRIMARY_USE_CASE_DISCHARGE_POWER_W);
    }

    /**
     * Returns the value of the primary use case discharge power.
     * @return the value of the primary use case discharge power
     */
    public default Value<Integer> getPrimaryUseCaseDischargePowerW() {
        return this.getPrimaryUseCaseDischargePowerWChannel().value();
    }

    /**
     * Sets the next value of the primary use case discharge power.
     * @param value the next value
     */
    public default void _setPrimaryUseCaseDischargePowerW(Integer value) {
        this.getPrimaryUseCaseDischargePowerWChannel().setNextValue(value);
    }

    /**
     * Returns the IntegerReadChannel for the levl discharge power.
     * @return the IntegerReadChannel
     */
    public default IntegerReadChannel getLevlDischargePowerWChannel() {
        return this.channel(ChannelId.LEVL_DISCHARGE_POWER_W);
    }

    /**
     * Returns the value of the levl discharge power.
     * @return the value of the levl discharge power
     */
    public default Value<Integer> getLevlDischargePowerW() {
        return this.getLevlDischargePowerWChannel().value();
    }

    /**
     * Sets the next value of the levl discharge power.
     * @param value the next value
     */
    public default void _setLevlDischargePowerW(Integer value) {
        this.getLevlDischargePowerWChannel().setNextValue(value);
    }
}