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
        return this.ess.id();
    }

    @Override
    public String alias() {
        return this.ess.alias();
    }

    @Override
    public boolean isEnabled() {
        return this.ess.isEnabled();
    }

    @Override
    public String servicePid() {
        return this.ess.servicePid();
    }

    @Override
    public String serviceFactoryPid() {
        return this.ess.serviceFactoryPid();
    }

    @Override
    public boolean isManaged() {
        return this.ess.isManaged();
    }

    @Override
    public ComponentContext getComponentContext() {
        return this.ess.getComponentContext();
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
        return this.ess.getStateChannel();
    }

    @Override
    public Level getState() {
        return this.ess.getState();
    }

    @Override
    public <T extends Channel<?>> T _getChannelAs(OpenemsComponent.ChannelId channelId, Class<T> type) {
        return this.ess._getChannelAs(channelId, type);
    }

    @Override
    public Power getPower() {
        return this.ess.getPower();
    }

    @Override
    public IntegerReadChannel getAllowedChargePowerChannel() {
        return this.ess.getAllowedChargePowerChannel();
    }

    @Override
    public Value<Integer> getAllowedChargePower() {
        return this.ess.getAllowedChargePower();
    }

    @Override
    public IntegerReadChannel getAllowedDischargePowerChannel() {
        return this.ess.getAllowedDischargePowerChannel();
    }

    @Override
    public Value<Integer> getAllowedDischargePower() {
        return this.ess.getAllowedDischargePower();
    }

    @Override
    public IntegerWriteChannel getSetActivePowerEqualsChannel() {
        return this.ess.getSetActivePowerEqualsChannel();
    }

    @Override
    public IntegerWriteChannel getSetActivePowerEqualsWithPidChannel() {
        return this.ess.getSetActivePowerEqualsWithPidChannel();
    }

    @Override
    public IntegerWriteChannel getSetReactivePowerEqualsChannel() {
        return this.ess.getSetReactivePowerEqualsChannel();
    }

    @Override
    public IntegerWriteChannel getSetActivePowerLessOrEqualsChannel() {
        return this.ess.getSetActivePowerLessOrEqualsChannel();
    }

    @Override
    public IntegerWriteChannel getSetActivePowerGreaterOrEqualsChannel() {
        return this.ess.getSetActivePowerGreaterOrEqualsChannel();
    }

    @Override
    public IntegerWriteChannel getSetReactivePowerLessOrEqualsChannel() {
        return this.ess.getSetReactivePowerLessOrEqualsChannel();
    }

    @Override
    public IntegerWriteChannel getSetReactivePowerGreaterOrEqualsChannel() {
        return this.ess.getSetReactivePowerGreaterOrEqualsChannel();
    }

    @Override
    public IntegerReadChannel getDebugSetActivePowerChannel() {
        return this.ess.getDebugSetActivePowerChannel();
    }

    @Override
    public Value<Integer> getDebugSetActivePower() {
        return this.ess.getDebugSetActivePower();
    }

    @Override
    public IntegerReadChannel getDebugSetReactivePowerChannel() {
        return this.ess.getDebugSetReactivePowerChannel();
    }

    @Override
    public Value<Integer> getDebugSetReactivePower() {
        return this.ess.getDebugSetReactivePower();
    }

    @Override
    public StateChannel getApplyPowerFailedChannel() {
        return this.ess.getApplyPowerFailedChannel();
    }

    @Override
    public Value<Boolean> getApplyPowerFailed() {
        return this.ess.getApplyPowerFailed();
    }

    @Override
    public void applyPower(int activePower, int reactivePower) {
        this.appliedActivePower = activePower;
        this.appliedReactivePower = reactivePower;
    }

    @Override
    public int getPowerPrecision() {
        return this.ess.getPowerPrecision();
    }

    @Override
    public Constraint[] getStaticConstraints() throws OpenemsError.OpenemsNamedException {
        return this.ess.getStaticConstraints();
    }

    @Override
    public IntegerReadChannel getSocChannel() {
        return this.ess.getSocChannel();
    }

    @Override
    public Value<Integer> getSoc() {
        return this.ess.getSoc();
    }

    @Override
    public IntegerReadChannel getCapacityChannel() {
        return this.ess.getCapacityChannel();
    }

    @Override
    public Value<Integer> getCapacity() {
        return this.ess.getCapacity();
    }

    @Override
    public Channel<GridMode> getGridModeChannel() {
        return this.ess.getGridModeChannel();
    }

    @Override
    public GridMode getGridMode() {
        return this.ess.getGridMode();
    }

    @Override
    public IntegerReadChannel getActivePowerChannel() {
        return this.ess.getActivePowerChannel();
    }

    @Override
    public Value<Integer> getActivePower() {
        return this.ess.getActivePower();
    }

    @Override
    public IntegerReadChannel getReactivePowerChannel() {
        return this.ess.getReactivePowerChannel();
    }

    @Override
    public Value<Integer> getReactivePower() {
        return this.ess.getReactivePower();
    }

    @Override
    public IntegerReadChannel getMaxApparentPowerChannel() {
        return this.ess.getMaxApparentPowerChannel();
    }

    @Override
    public Value<Integer> getMaxApparentPower() {
        return this.ess.getMaxApparentPower();
    }

    @Override
    public LongReadChannel getActiveChargeEnergyChannel() {
        return this.ess.getActiveChargeEnergyChannel();
    }

    @Override
    public Value<Long> getActiveChargeEnergy() {
        return this.ess.getActiveChargeEnergy();
    }

    @Override
    public LongReadChannel getActiveDischargeEnergyChannel() {
        return this.ess.getActiveDischargeEnergyChannel();
    }

    @Override
    public Value<Long> getActiveDischargeEnergy() {
        return this.ess.getActiveDischargeEnergy();
    }

    @Override
    public IntegerReadChannel getMinCellVoltageChannel() {
        return this.ess.getMinCellVoltageChannel();
    }

    @Override
    public Value<Integer> getMinCellVoltage() {
        return this.ess.getMinCellVoltage();
    }

    @Override
    public IntegerReadChannel getMaxCellVoltageChannel() {
        return this.ess.getMaxCellVoltageChannel();
    }

    @Override
    public Value<Integer> getMaxCellVoltage() {
        return this.ess.getMaxCellVoltage();
    }

    @Override
    public IntegerReadChannel getMinCellTemperatureChannel() {
        return this.ess.getMinCellTemperatureChannel();
    }

    @Override
    public Value<Integer> getMinCellTemperature() {
        return this.ess.getMinCellTemperature();
    }

    @Override
    public IntegerReadChannel getMaxCellTemperatureChannel() {
        return this.ess.getMaxCellTemperatureChannel();
    }

    @Override
    public Value<Integer> getMaxCellTemperature() {
        return this.ess.getMaxCellTemperature();
    }

    @Override
    public String debugLog() {
        return this.ess.debugLog();
    }

    @Override
    public boolean hasFaults() {
        return this.ess.hasFaults();
    }

    @Override
    public void setActivePowerEquals(Integer value) {
    	this.receivedActivePowerEquals = value;
    }

    @Override
    public void setActivePowerEqualsWithPid(Integer value) {
    	this.receivedActivePowerEqualsWithPid = value;
    }

    @Override
    public void setActivePowerLessOrEquals(Integer value) {
    	this.receivedActivePowerLessOrEquals = value;
    }

    @Override
    public void setActivePowerGreaterOrEquals(Integer value) {
    	this.receivedActivePowerGreaterOrEquals = value;
    }

    @Override
    public void setReactivePowerEquals(Integer value) {
    	this.receivedReactivePowerEquals = value;
    }

    @Override
    public void setReactivePowerLessOrEquals(Integer value) {
    	this.receivedReactivePowerLessOrEquals = value;
    }

    @Override
    public void setReactivePowerGreaterOrEquals(Integer value) {
    	this.receivedReactivePowerGreaterOrEquals = value;
    }

    public Integer getAppliedActivePower() {
        return this.appliedActivePower;
    }

    public Integer getAppliedReactivePower() {
        return this.appliedReactivePower;
    }

    public Integer getReceivedActivePowerEqualsWithPid() {
        return this.receivedActivePowerEqualsWithPid;
    }

    public Integer getReceivedActivePowerEquals() {
        return this.receivedActivePowerEquals;
    }

    public Integer getReceivedActivePowerLessOrEquals() {
        return this.receivedActivePowerLessOrEquals;
    }

    public Integer getReceivedActivePowerGreaterOrEquals() {
        return this.receivedActivePowerGreaterOrEquals;
    }

    public Integer getReceivedReactivePowerEquals() {
        return this.receivedReactivePowerEquals;
    }

    public Integer getReceivedReactivePowerLessOrEquals() {
        return this.receivedReactivePowerLessOrEquals;
    }

    public Integer getReceivedReactivePowerGreaterOrEquals() {
        return this.receivedReactivePowerGreaterOrEquals;
    }

    public void reset() {
        this.appliedActivePower = null;
        this.appliedReactivePower = null;
        this.receivedActivePowerEqualsWithPid = null;
        this.receivedActivePowerEquals = null;
        this.receivedActivePowerLessOrEquals = null;
        this.receivedActivePowerGreaterOrEquals = null;
        this.receivedReactivePowerEquals = null;
        this.receivedReactivePowerLessOrEquals = null;
        this.receivedReactivePowerGreaterOrEquals = null;
    }
}
