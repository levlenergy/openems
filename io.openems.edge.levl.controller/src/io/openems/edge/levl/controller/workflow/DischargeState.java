package io.openems.edge.levl.controller.workflow;

import io.openems.edge.levl.controller.controllers.common.Percent;
import io.openems.edge.levl.controller.controllers.common.Sign;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DischargeState {
	private final Logger log = LoggerFactory.getLogger(DischargeState.class);

	private long totalDischargeEnergyWsAtBatteryScaledWithEfficiency = 0;
	private long currentRequestRemainingDischargeEnergyWs = 0;
	private long currentRequestRealizedDischargeEnergyWs = 0;
	private long lastCompletedRequestRealizedDischargeEnergyWs = 0;
	private BigDecimal currentEfficiencyPercent = BigDecimal.valueOf(100);
	private BigDecimal nextRequestEfficiencyPercent = BigDecimal.valueOf(100);

	private String lastCompletedRequestTimestamp = "";

	private DischargeRequest request = DischargeRequest.inactiveRequest();

	private DischargeRequest nextRequest = DischargeRequest.inactiveRequest();

	record DischargeStateMemento(long totalDischargeEnergyWsAtBatteryScaledWithEfficiency, long currentRequestRemainingDischargeEnergyWs,
			long currentRequestRealizedDischargeEnergyWs, long lastRequestRealizedDischargeEnergyWs,
			BigDecimal currentRequestEfficiencyPercent, BigDecimal nextRequestEfficiencyPercent,
			String lastDischargeRequestTimestamp, DischargeRequest.DischargeRequestMemento request, 
			DischargeRequest.DischargeRequestMemento nextRequest) {
	}

	public DischargeState() {
	}

	DischargeState(long totalDischargeEnergyWsAtBatteryScaledWithEfficiency, long currentRequestRemainingDischargeEnergyWs,
			long currentRequestRealizedDischargeEnergyWs, long lastCompletedRequestRealizedDischargeEnergyWs,
			BigDecimal currentEfficiencyPercent, BigDecimal nextRequestEfficiencyPercent,
			String lastCompletedRequestTimestamp, DischargeRequest request,
			DischargeRequest nextRequest) {
		this.totalDischargeEnergyWsAtBatteryScaledWithEfficiency = totalDischargeEnergyWsAtBatteryScaledWithEfficiency;
		this.currentRequestRemainingDischargeEnergyWs = currentRequestRemainingDischargeEnergyWs;
		this.currentRequestRealizedDischargeEnergyWs = currentRequestRealizedDischargeEnergyWs;
		this.lastCompletedRequestRealizedDischargeEnergyWs = lastCompletedRequestRealizedDischargeEnergyWs;
		this.currentEfficiencyPercent = currentEfficiencyPercent;
		this.nextRequestEfficiencyPercent = nextRequestEfficiencyPercent;
		this.lastCompletedRequestTimestamp = lastCompletedRequestTimestamp;
		this.request = request;
		this.nextRequest = nextRequest;
	}

	/**
	 * Saves the current state of this DischargeState.
	 *
	 * @return a new DischargeStateMemento containing the current state
	 */
	public DischargeStateMemento save() {
		return new DischargeStateMemento(this.totalDischargeEnergyWsAtBatteryScaledWithEfficiency, this.currentRequestRemainingDischargeEnergyWs,
				this.currentRequestRealizedDischargeEnergyWs, this.lastCompletedRequestRealizedDischargeEnergyWs,
				this.currentEfficiencyPercent, this.nextRequestEfficiencyPercent, this.lastCompletedRequestTimestamp, 
				this.request.save(), this.nextRequest.save());
	}

	/**
	 * Restores a DischargeState from a memento.
	 *
	 * @param memento the memento to restore from
	 * @return a new DischargeState with the state restored from the memento
	 */
	public static DischargeState restore(DischargeStateMemento memento) {
		return new DischargeState(memento.totalDischargeEnergyWsAtBatteryScaledWithEfficiency,
				memento.currentRequestRemainingDischargeEnergyWs,
				memento.currentRequestRealizedDischargeEnergyWs, memento.lastRequestRealizedDischargeEnergyWs,
				memento.currentRequestEfficiencyPercent, memento.nextRequestEfficiencyPercent,
				memento.lastDischargeRequestTimestamp, DischargeRequest.restore(memento.request),
				DischargeRequest.restore(memento.nextRequest));
	}

	public String getLastCompletedRequestTimestamp() {
		return this.lastCompletedRequestTimestamp;
	}

	public long getTotalDischargeEnergyWsAtBatteryScaledWithEfficiency() {
		return this.totalDischargeEnergyWsAtBatteryScaledWithEfficiency;
	}

	public long getCurrentRequestRemainingDischargePowerWs() {
		return this.currentRequestRemainingDischargeEnergyWs;
	}

	public int getLastCompletedRequestDischargePowerW() {
		return (int) (this.lastCompletedRequestRealizedDischargeEnergyWs) / DischargeRequest.QUARTER_HOUR_TO_SECONDS;
	}
	
	public boolean isInfluenceSellToGridAllowed() {
		return this.request.isInfluenceSellToGridAllowed();
	}
	
	/**
	 * Returns the realized discharge energy of the current levl cycle in Ws with efficiency applied.
	 * 
	 * @return currentRequestRealizedDischargeEnergyWs with efficiency applied
	 */
	protected long getCurrentRequestRealizedDischargeEnergyWithEfficiencyWs() {
		if (this.currentRequestRealizedDischargeEnergyWs >= 0) {
			return Percent.undoPercentage(Math.toIntExact(this.currentRequestRealizedDischargeEnergyWs),
					this.currentEfficiencyPercent);
		}
		return Percent.applyPercentage(Math.toIntExact(this.currentRequestRealizedDischargeEnergyWs),
					this.currentEfficiencyPercent);
	}

	/**
	 * Handles a received request and updates the next request and its efficiency.
	 *
	 * @param efficiencyPercent the efficiency of the received request
	 * @param receivedRequest the received request
	 * @param newLevlSocWs the updated levl soc which should be used from now on
	 */
	public void handleReceivedRequest(BigDecimal efficiencyPercent, DischargeRequest receivedRequest, long newLevlSocWs) {
		this.log.info("Received new levl request: {}", receivedRequest);
		this.nextRequestEfficiencyPercent = efficiencyPercent;
		this.nextRequest = receivedRequest;
		this.totalDischargeEnergyWsAtBatteryScaledWithEfficiency = newLevlSocWs;
	}
	
	/**
	 * Initializes the state after it has been restored.
	 *
	 * @param now the current time
	 */
	public void initAfterRestore(LocalDateTime now) {
		if (this.nextRequest.isExpired(now)) {
			this.nextRequest = DischargeRequest.inactiveRequest();
			this.nextRequestEfficiencyPercent = BigDecimal.valueOf(100);
		}
		this.update(now);
	}

	/**
	 * Updates the state based on the current time.
	 *
	 * @param now the current time
	 */
	public void update(LocalDateTime now) {
		if (this.request.isExpired(now)) {
			this.completeCurrentRequest("request expired");
		}
		if (this.nextRequest.shouldStart(now)) {
			if (this.request.isActive()) {
				this.completeCurrentRequest("next request should start");
			}

			this.request = this.nextRequest;
			this.nextRequest = DischargeRequest.inactiveRequest();
			this.log.info("Starting levl request: {}", this.request);
			this.currentEfficiencyPercent = this.nextRequestEfficiencyPercent;
			this.nextRequestEfficiencyPercent = BigDecimal.valueOf(100);
			this.currentRequestRemainingDischargeEnergyWs = this.request.getDischargeEnergyWs();
		}
	}

	/**
	 * Handles the realized discharge power for one second.
	 *
	 * @param newRealizedPowerW the new realized power in watts
	 */
	public void handleRealizedDischargePowerWForOneSecond(int newRealizedPowerW) {
		if (!this.request.isActive()) {
			return;
		}
		// newRealizedPowerW for one Second -> W becomes Ws
		this.currentRequestRealizedDischargeEnergyWs += newRealizedPowerW;

		if (newRealizedPowerW >= 0) {
			this.totalDischargeEnergyWsAtBatteryScaledWithEfficiency += Percent.undoPercentage(newRealizedPowerW,
					this.currentEfficiencyPercent);
		} else {
			this.totalDischargeEnergyWsAtBatteryScaledWithEfficiency += Percent.applyPercentage(newRealizedPowerW,
					this.currentEfficiencyPercent);
		}

		var newRemainingDischargeEnergyWs = this.currentRequestRemainingDischargeEnergyWs - newRealizedPowerW;

		boolean signHasChanged = !Sign.haveSameSign(this.currentRequestRemainingDischargeEnergyWs,
				newRemainingDischargeEnergyWs);
		this.currentRequestRemainingDischargeEnergyWs = newRemainingDischargeEnergyWs;
		if (signHasChanged) {
			this.completeCurrentRequest("request completed");
		}
	}

	/**
	 * Completes the current request and updates the state accordingly.
	 *
	 * @param reason the reason for stopping the execution of the request
	 */
	private void completeCurrentRequest(String reason) {
		this.log.info("stopped executing levl request: {} {}", reason, this.request);
		this.lastCompletedRequestTimestamp = this.request.getRequestTimestamp();
		this.lastCompletedRequestRealizedDischargeEnergyWs = this.currentRequestRealizedDischargeEnergyWs;
		this.currentRequestRemainingDischargeEnergyWs = 0;
		this.currentRequestRealizedDischargeEnergyWs = 0;
		this.currentEfficiencyPercent = BigDecimal.valueOf(100);
		this.request = DischargeRequest.inactiveRequest();
	}

}
