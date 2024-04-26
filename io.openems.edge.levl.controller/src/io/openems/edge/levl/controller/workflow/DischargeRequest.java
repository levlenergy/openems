package io.openems.edge.levl.controller.workflow;

import java.time.LocalDateTime;
import java.util.Objects;

public class DischargeRequest {
	private final String requestId;
	private final String requestTimestamp;
	private final long dischargeEnergyWs;
	private final LocalDateTime start;
	private final LocalDateTime deadline;
	private final boolean active;
	public static final int QUARTER_HOUR_TO_SECONDS = 900;

	record DischargeRequestMemento(String lastRequestId, String requestTimestamp, long dischargeEnergyWs,
			LocalDateTime start, LocalDateTime deadline, boolean active) {
	}

	DischargeRequest(String requestId, String requestTimestamp, long dischargeEnergyWs, LocalDateTime start,
			LocalDateTime deadline, boolean active) {
		this.requestId = requestId;
		this.requestTimestamp = requestTimestamp;
		this.dischargeEnergyWs = dischargeEnergyWs;
		this.start = start;
		this.deadline = deadline;
		this.active = active;
	}

	public DischargeRequestMemento save() {
		return new DischargeRequestMemento(this.requestId, this.requestTimestamp, this.dischargeEnergyWs, this.start,
				this.deadline, this.active);
	}

	public static DischargeRequest restore(DischargeRequestMemento memento) {
		return new DischargeRequest(memento.lastRequestId, memento.requestTimestamp, memento.dischargeEnergyWs,
				memento.start, memento.deadline, memento.active);
	}

	public static DischargeRequest of(LocalDateTime now, String levlRequestTimestamp, String lastRequestId,
			int levlPowerW, int delayStartSeconds, int durationSeconds) {
		return new DischargeRequest(lastRequestId, levlRequestTimestamp, (long) levlPowerW * QUARTER_HOUR_TO_SECONDS,
				now.plusSeconds(delayStartSeconds), now.plusSeconds(delayStartSeconds + durationSeconds), true);
	}

	public static DischargeRequest inactiveRequest() {
		return new DischargeRequest("", "", 0, LocalDateTime.MAX, LocalDateTime.MAX, false);
	}

	public boolean isActive() {
		return this.active;
	}

	public String getRequestTimestamp() {
		return this.requestTimestamp;
	}

	public long getDischargeEnergyWs() {
		return this.dischargeEnergyWs;
	}

	public boolean isSameRequest(DischargeRequest other) {
		return this.equals(other.requestId);
	}

	public boolean isExpired(LocalDateTime now) {
		return now.isAfter(this.deadline);
	}

	public boolean shouldStart(LocalDateTime now) {
		return now.isAfter(this.start);
	}

	@Override
	public String toString() {
		return "DischargeRequest{" + "requestId='" + this.requestId + '\'' + ", requestTimestamp="
				+ this.requestTimestamp + ", dischargeEnergyWs=" + this.dischargeEnergyWs + ", start=" + this.start
				+ ", deadline=" + this.deadline + ", active=" + this.active + '}';
	}

	// TODO: 15.02.2024 Dennis: equals und hashCode anpassen
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;	
		}
		DischargeRequest that = (DischargeRequest) o;
		return this.dischargeEnergyWs == that.dischargeEnergyWs && this.active == that.active
				&& this.equals(that.requestId) && this.start.equals(that.start) && this.deadline.equals(that.deadline);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.requestId, this.dischargeEnergyWs, this.start, this.deadline, this.active);
	}

}
