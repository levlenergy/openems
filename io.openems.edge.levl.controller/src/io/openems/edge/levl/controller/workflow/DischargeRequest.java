package io.openems.edge.levl.controller.workflow;

import java.time.LocalDateTime;
import java.util.Objects;

public class DischargeRequest {
	private final String requestId;
	private final String requestTimestamp;
	private final long dischargeEnergyWs;
	private final boolean influenceSellToGrid;
	private final LocalDateTime start;
	private final LocalDateTime deadline;
	private final boolean active;
	public static final int QUARTER_HOUR_TO_SECONDS = 900;

	record DischargeRequestMemento(String lastRequestId, String requestTimestamp, long dischargeEnergyWs,
			boolean influenceSellToGrid, LocalDateTime start, LocalDateTime deadline, boolean active) {
	}

	DischargeRequest(String requestId, String requestTimestamp, long dischargeEnergyWs, boolean influenceSellToGrid,
			LocalDateTime start, LocalDateTime deadline, boolean active) {
		this.requestId = requestId;
		this.requestTimestamp = requestTimestamp;
		this.dischargeEnergyWs = dischargeEnergyWs;
		this.influenceSellToGrid = influenceSellToGrid;
		this.start = start;
		this.deadline = deadline;
		this.active = active;
	}

	/**
	 * Saves the current state of this DischargeRequest.
	 *
	 * @return a new DischargeRequestMemento containing the current state
	 */
	public DischargeRequestMemento save() {
		return new DischargeRequestMemento(this.requestId, this.requestTimestamp, this.dischargeEnergyWs,
				this.influenceSellToGrid, this.start, this.deadline, this.active);
	}

	/**
	 * Restores a DischargeRequest from a memento.
	 *
	 * @param memento the memento to restore from
	 * @return a new DischargeRequest with the state restored from the memento
	 */
	public static DischargeRequest restore(DischargeRequestMemento memento) {
		return new DischargeRequest(memento.lastRequestId, memento.requestTimestamp, memento.dischargeEnergyWs,
				memento.influenceSellToGrid, memento.start, memento.deadline, memento.active);
	}

	/**
	 * Creates a new DischargeRequest instance with the given parameters.
	 *
	 * @param now                  the current time
	 * @param levlRequestTimestamp the request timestamp from LEVL
	 * @param lastRequestId        the last request id
	 * @param levlPowerW           the power in watts from LEVL
	 * @param influenceSellToGrid  whether it is allowed to influence sell to grid
	 * @param delayStartSeconds    the delay start time in seconds
	 * @param durationSeconds      the duration in seconds
	 * @return a new DischargeRequest instance
	 */
	public static DischargeRequest of(LocalDateTime now, String levlRequestTimestamp, String lastRequestId,
			int levlPowerW, boolean influenceSellToGrid, int delayStartSeconds, int durationSeconds) {
		return new DischargeRequest(lastRequestId, levlRequestTimestamp, (long) levlPowerW * QUARTER_HOUR_TO_SECONDS,
				influenceSellToGrid, now.plusSeconds(delayStartSeconds),
				now.plusSeconds(delayStartSeconds + durationSeconds), true);
	}

	/**
	 * Creates a new inactive DischargeRequest instance.
	 *
	 * @return a new inactive DischargeRequest instance
	 */
	public static DischargeRequest inactiveRequest() {
		return new DischargeRequest("", "", 0, true, LocalDateTime.MAX, LocalDateTime.MAX, false);
	}

	/**
	 * Checks if the request is active.
	 *
	 * @return true if the request is active, false otherwise
	 */
	public boolean isActive() {
		return this.active;
	}

	public String getRequestTimestamp() {
		return this.requestTimestamp;
	}

	public long getDischargeEnergyWs() {
		return this.dischargeEnergyWs;
	}

	public boolean isInfluenceSellToGridAllowed() {
		return this.influenceSellToGrid;
	}

	/**
	 * Checks if the request is the same as another request.
	 *
	 * @param other the other request to compare with
	 * @return true if the requests are the same, false otherwise
	 */
	public boolean isSameRequest(DischargeRequest other) {
		return this.requestId.equals(other.requestId);
	}

	/**
	 * Checks if the request is expired.
	 *
	 * @param now the current time
	 * @return true if the request is expired, false otherwise
	 */
	public boolean isExpired(LocalDateTime now) {
		return now.isAfter(this.deadline);
	}

	/**
	 * Checks if the request should start.
	 *
	 * @param now the current time
	 * @return true if the request should start, false otherwise
	 */
	public boolean shouldStart(LocalDateTime now) {
		return now.isAfter(this.start);
	}

	@Override
	public String toString() {
		return "DischargeRequest{" + "requestId='" + this.requestId + '\'' + ", requestTimestamp="
				+ this.requestTimestamp + ", dischargeEnergyWs=" + this.dischargeEnergyWs + ", influenceSellToGrid="
				+ this.influenceSellToGrid + ", start=" + this.start + ", deadline=" + this.deadline + ", active="
				+ this.active + '}';
	}

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
				&& this.requestId.equals(that.requestId) && this.requestTimestamp.equals(that.requestTimestamp)
				&& this.start.equals(that.start) && this.deadline.equals(that.deadline)
				&& this.influenceSellToGrid == that.influenceSellToGrid;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.requestId, this.requestTimestamp, this.dischargeEnergyWs, this.influenceSellToGrid,
				this.start, this.deadline, this.active);
	}

}
