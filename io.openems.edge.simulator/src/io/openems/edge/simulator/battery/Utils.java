package io.openems.edge.simulator.battery;

import java.util.Arrays;
import java.util.stream.Stream;

import io.openems.edge.battery.api.Battery;
import io.openems.edge.common.channel.BooleanReadChannel;
import io.openems.edge.common.channel.Channel;
import io.openems.edge.common.channel.IntegerReadChannel;
import io.openems.edge.common.channel.StateCollectorChannel;
import io.openems.edge.common.component.OpenemsComponent;

public class Utils {
	public static Stream<? extends Channel<?>> initializeChannels(BatteryDummy s) {
		// Define the channels. Using streams + switch enables Eclipse IDE to tell us if
		// we are missing an Enum value.
		return Stream.of( //
				Arrays.stream(OpenemsComponent.ChannelId.values()).map(channelId -> {
					switch (channelId) {
					case STATE:
						return new StateCollectorChannel(s, channelId);
					}
					return null;
				}), Arrays.stream(Battery.ChannelId.values()).map(channelId -> {
					switch (channelId) {
					case SOC:
					case CHARGE_MAX_CURRENT:
					case CHARGE_MAX_VOLTAGE:
					case DISCHARGE_MAX_CURRENT:
					case DISCHARGE_MIN_VOLTAGE:
						return new IntegerReadChannel(s, channelId);
					case READY_FOR_WORKING:
						return new BooleanReadChannel(s, channelId);
					}
					return null;
				})
		).flatMap(channel -> channel);
	}
}
