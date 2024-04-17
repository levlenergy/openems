package io.openems.edge.levl.controller.controllers.common;

import io.openems.common.channel.Level;
import io.openems.common.exceptions.OpenemsError;
import io.openems.edge.common.channel.*;
import io.openems.edge.common.channel.internal.StateCollectorChannel;
import io.openems.edge.common.channel.value.Value;
import io.openems.edge.common.component.OpenemsComponent;
import io.openems.edge.common.sum.GridMode;
import io.openems.edge.ess.api.ManagedSymmetricEss;
import io.openems.edge.ess.power.api.Constraint;
import io.openems.edge.ess.power.api.Power;
import org.osgi.service.component.ComponentContext;

import java.util.Collection;

public class ReadOnlyManagedSymmetricEss implements ManagedSymmetricEss {
    private ManagedSymmetricEss ess;
    private Integer appliedActivePower;
    private Integer appliedReactivePower;
    private Integer receivedActivePowerEqualsWithPid;
    private Integer receivedActivePowerEquals;
    private Integer receivedActivePowerLessOrEquals;
    private Integer receivedActivePowerGreaterOrEquals;
    private Integer receivedReactivePowerEquals;
    private Integer receivedReactivePowerLessOrEquals;
    private Integer receivedReactivePowerGreaterOrEquals;

    public ReadOnlyManagedSymmetricEss(ManagedSymmetricEss ess) {
        this.ess = ess;
    }

    @Override
    public String id() {
        return ess.id();
    }

    @Override
    public String alias() {
        return ess.alias();
    }

    @Override
    public boolean isEnabled() {
        return ess.isEnabled();
    }

    @Override
    public String servicePid() {
        return ess.servicePid();
    }

    @Override
    public String serviceFactoryPid() {
        return ess.serviceFactoryPid();
    }

    @Override
    public boolean isManaged() {
        return ess.isManaged();
    }

    @Override
    public ComponentContext getComponentContext() {
        return ess.getComponentContext();
    }

    @Override
    public Channel<?> _channel(String channelName) {
        throw new IllegalArgumentException("channel " + channelName + " requested");
    }

    @Override
    public Collection<Channel<?>> channels() {
        throw new IllegalArgumentException("channel list requested");
    }

    @Override
    public StateCollectorChannel getStateChannel() {
        return ess.getStateChannel();
    }

    @Override
    public Level getState() {
        return ess.getState();
    }

    @Override
    public <T extends Channel<?>> T _getChannelAs(OpenemsComponent.ChannelId channelId, Class<T> type) {
        return ess._getChannelAs(channelId, type);
    }

    @Override
    public Power getPower() {
        return ess.getPower();
    }

    @Override
    public IntegerReadChannel getAllowedChargePowerChannel() {
        return ess.getAllowedChargePowerChannel();
    }

    @Override
    public Value<Integer> getAllowedChargePower() {
        return ess.getAllowedChargePower();
    }

    @Override
    public IntegerReadChannel getAllowedDischargePowerChannel() {
        return ess.getAllowedDischargePowerChannel();
    }

    @Override
    public Value<Integer> getAllowedDischargePower() {
        return ess.getAllowedDischargePower();
    }

    @Override
    public IntegerWriteChannel getSetActivePowerEqualsChannel() {
        return ess.getSetActivePowerEqualsChannel();
    }

    @Override
    public IntegerWriteChannel getSetActivePowerEqualsWithPidChannel() {
        return ess.getSetActivePowerEqualsWithPidChannel();
    }

    @Override
    public IntegerWriteChannel getSetReactivePowerEqualsChannel() {
        return ess.getSetReactivePowerEqualsChannel();
    }

    @Override
    public IntegerWriteChannel getSetActivePowerLessOrEqualsChannel() {
        return ess.getSetActivePowerLessOrEqualsChannel();
    }

    @Override
    public IntegerWriteChannel getSetActivePowerGreaterOrEqualsChannel() {
        return ess.getSetActivePowerGreaterOrEqualsChannel();
    }

    @Override
    public IntegerWriteChannel getSetReactivePowerLessOrEqualsChannel() {
        return ess.getSetReactivePowerLessOrEqualsChannel();
    }

    @Override
    public IntegerWriteChannel getSetReactivePowerGreaterOrEqualsChannel() {
        return ess.getSetReactivePowerGreaterOrEqualsChannel();
    }

    @Override
    public IntegerReadChannel getDebugSetActivePowerChannel() {
        return ess.getDebugSetActivePowerChannel();
    }

    @Override
    public Value<Integer> getDebugSetActivePower() {
        return ess.getDebugSetActivePower();
    }

    @Override
    public IntegerReadChannel getDebugSetReactivePowerChannel() {
        return ess.getDebugSetReactivePowerChannel();
    }

    @Override
    public Value<Integer> getDebugSetReactivePower() {
        return ess.getDebugSetReactivePower();
    }

    @Override
    public StateChannel getApplyPowerFailedChannel() {
        return ess.getApplyPowerFailedChannel();
    }

    @Override
    public Value<Boolean> getApplyPowerFailed() {
        return ess.getApplyPowerFailed();
    }

    @Override
    public void applyPower(int activePower, int reactivePower) {
        appliedActivePower = activePower;
        appliedReactivePower = reactivePower;
    }

    @Override
    public int getPowerPrecision() {
        return ess.getPowerPrecision();
    }

    @Override
    public Constraint[] getStaticConstraints() throws OpenemsError.OpenemsNamedException {
        return ess.getStaticConstraints();
    }

    @Override
    public IntegerReadChannel getSocChannel() {
        return ess.getSocChannel();
    }

    @Override
    public Value<Integer> getSoc() {
        return ess.getSoc();
    }

    @Override
    public IntegerReadChannel getCapacityChannel() {
        return ess.getCapacityChannel();
    }

    @Override
    public Value<Integer> getCapacity() {
        return ess.getCapacity();
    }

    @Override
    public Channel<GridMode> getGridModeChannel() {
        return ess.getGridModeChannel();
    }

    @Override
    public GridMode getGridMode() {
        return ess.getGridMode();
    }

    @Override
    public IntegerReadChannel getActivePowerChannel() {
        return ess.getActivePowerChannel();
    }

    @Override
    public Value<Integer> getActivePower() {
        return ess.getActivePower();
    }

    @Override
    public IntegerReadChannel getReactivePowerChannel() {
        return ess.getReactivePowerChannel();
    }

    @Override
    public Value<Integer> getReactivePower() {
        return ess.getReactivePower();
    }

    @Override
    public IntegerReadChannel getMaxApparentPowerChannel() {
        return ess.getMaxApparentPowerChannel();
    }

    @Override
    public Value<Integer> getMaxApparentPower() {
        return ess.getMaxApparentPower();
    }

    @Override
    public LongReadChannel getActiveChargeEnergyChannel() {
        return ess.getActiveChargeEnergyChannel();
    }

    @Override
    public Value<Long> getActiveChargeEnergy() {
        return ess.getActiveChargeEnergy();
    }

    @Override
    public LongReadChannel getActiveDischargeEnergyChannel() {
        return ess.getActiveDischargeEnergyChannel();
    }

    @Override
    public Value<Long> getActiveDischargeEnergy() {
        return ess.getActiveDischargeEnergy();
    }

    @Override
    public IntegerReadChannel getMinCellVoltageChannel() {
        return ess.getMinCellVoltageChannel();
    }

    @Override
    public Value<Integer> getMinCellVoltage() {
        return ess.getMinCellVoltage();
    }

    @Override
    public IntegerReadChannel getMaxCellVoltageChannel() {
        return ess.getMaxCellVoltageChannel();
    }

    @Override
    public Value<Integer> getMaxCellVoltage() {
        return ess.getMaxCellVoltage();
    }

    @Override
    public IntegerReadChannel getMinCellTemperatureChannel() {
        return ess.getMinCellTemperatureChannel();
    }

    @Override
    public Value<Integer> getMinCellTemperature() {
        return ess.getMinCellTemperature();
    }

    @Override
    public IntegerReadChannel getMaxCellTemperatureChannel() {
        return ess.getMaxCellTemperatureChannel();
    }

    @Override
    public Value<Integer> getMaxCellTemperature() {
        return ess.getMaxCellTemperature();
    }

    @Override
    public String debugLog() {
        return ess.debugLog();
    }

    @Override
    public boolean hasFaults() {
        return ess.hasFaults();
    }

    @Override
    public void setActivePowerEquals(Integer value) {
        receivedActivePowerEquals = value;
    }

    @Override
    public void setActivePowerEqualsWithPid(Integer value) {
        receivedActivePowerEqualsWithPid = value;
    }

    @Override
    public void setActivePowerLessOrEquals(Integer value) {
        receivedActivePowerLessOrEquals = value;
    }

    @Override
    public void setActivePowerGreaterOrEquals(Integer value) {
        receivedActivePowerGreaterOrEquals = value;
    }

    @Override
    public void setReactivePowerEquals(Integer value) {
        receivedReactivePowerEquals = value;
    }

    @Override
    public void setReactivePowerLessOrEquals(Integer value) {
        receivedReactivePowerLessOrEquals = value;
    }

    @Override
    public void setReactivePowerGreaterOrEquals(Integer value) {
        receivedReactivePowerGreaterOrEquals = value;
    }

    public Integer getAppliedActivePower() {
        return appliedActivePower;
    }

    public Integer getAppliedReactivePower() {
        return appliedReactivePower;
    }

    public Integer getReceivedActivePowerEqualsWithPid() {
        return receivedActivePowerEqualsWithPid;
    }

    public Integer getReceivedActivePowerEquals() {
        return receivedActivePowerEquals;
    }

    public Integer getReceivedActivePowerLessOrEquals() {
        return receivedActivePowerLessOrEquals;
    }

    public Integer getReceivedActivePowerGreaterOrEquals() {
        return receivedActivePowerGreaterOrEquals;
    }

    public Integer getReceivedReactivePowerEquals() {
        return receivedReactivePowerEquals;
    }

    public Integer getReceivedReactivePowerLessOrEquals() {
        return receivedReactivePowerLessOrEquals;
    }

    public Integer getReceivedReactivePowerGreaterOrEquals() {
        return receivedReactivePowerGreaterOrEquals;
    }

    public void reset() {
        appliedActivePower = null;
        appliedReactivePower = null;
        receivedActivePowerEqualsWithPid = null;
        receivedActivePowerEquals = null;
        receivedActivePowerLessOrEquals = null;
        receivedActivePowerGreaterOrEquals = null;
        receivedReactivePowerEquals = null;
        receivedReactivePowerLessOrEquals = null;
        receivedReactivePowerGreaterOrEquals = null;
    }
}
