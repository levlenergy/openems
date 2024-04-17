package io.openems.edge.levl.controller.workflow;

import io.openems.edge.levl.controller.controllers.common.Percent;
import io.openems.edge.levl.controller.controllers.common.Sign;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;


public class DischargeState {
    // TODO jonas.flint 16.02.2024 aufteilen in verschiedene Funktionalitäten
    private final Logger log = LoggerFactory.getLogger(DischargeState.class);

    private long totalRealizedDischargeEnergyWs = 0;
    private long totalDischargeEnergyWsAtBatteryScaledWithEfficiency = 0;
    private long currentRequestRemainingDischargeEnergyWs = 0;
    private long currentRequestRealizedDischargeEnergyWs = 0;
    private long lastCompletedRequestRealizedDischargeEnergyWs = 0;
    private BigDecimal currentEfficiencyPercent = BigDecimal.valueOf(100);
    private BigDecimal nextRequestEfficiencyPercent = BigDecimal.valueOf(100);

    private String lastCompletedRequestTimestamp = "";

    private DischargeRequest request = DischargeRequest.inactiveRequest();

    private DischargeRequest nextRequest = DischargeRequest.inactiveRequest();

    record DischargeStateMemento(long totalRealizedDischargeEnergyWs,
                                 long totalDischargeEnergyWsAtBatteryScaledWithEfficiency,
                                 long currentRequestRemainingDischargeEnergyWs,
                                 long currentRequestRealizedDischargeEnergyWs,
                                 long lastRequestRealizedDischargeEnergyWs,
                                 BigDecimal currentRequestEfficiencyPercent,
                                 BigDecimal nextRequestEfficiencyPercent,
                                 String lastDischargeRequestTimestamp,
                                 DischargeRequest.DischargeRequestMemento request,
                                 DischargeRequest.DischargeRequestMemento nextRequest) {
    }

    public DischargeState() {
    }

    // TODO jonas.flint 16.02.2024 ganzes State Management in eigenes Package auslagern
    DischargeState(long totalRealizedDischargeEnergyWs,
                   long totalDischargeEnergyWsAtBatteryScaledWithEfficiency,
                   long currentRequestRemainingDischargeEnergyWs,
                   long currentRequestRealizedDischargeEnergyWs,
                   long lastCompletedRequestRealizedDischargeEnergyWs,
                   BigDecimal currentEfficiencyPercent,
                   BigDecimal nextRequestEfficiencyPercent,
                   String lastCompletedRequestTimestamp,
                   DischargeRequest request,
                   DischargeRequest nextRequest) {
        this.totalRealizedDischargeEnergyWs = totalRealizedDischargeEnergyWs;
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

    public DischargeStateMemento save() {
        return new DischargeStateMemento(totalRealizedDischargeEnergyWs,
                totalDischargeEnergyWsAtBatteryScaledWithEfficiency,
                currentRequestRemainingDischargeEnergyWs,
                currentRequestRealizedDischargeEnergyWs,
                lastCompletedRequestRealizedDischargeEnergyWs,
                currentEfficiencyPercent,
                nextRequestEfficiencyPercent,
                lastCompletedRequestTimestamp,
                request.save(),
                nextRequest.save());
    }

    public static DischargeState restore(DischargeStateMemento memento) {
        return new DischargeState(memento.totalRealizedDischargeEnergyWs,
                memento.totalDischargeEnergyWsAtBatteryScaledWithEfficiency,
                memento.currentRequestRemainingDischargeEnergyWs,
                memento.currentRequestRealizedDischargeEnergyWs,
                memento.lastRequestRealizedDischargeEnergyWs,
                memento.currentRequestEfficiencyPercent,
                memento.nextRequestEfficiencyPercent,
                memento.lastDischargeRequestTimestamp,
                DischargeRequest.restore(memento.request),
                DischargeRequest.restore(memento.nextRequest));
    }

    public String getLastCompletedRequestTimestamp() {
        return lastCompletedRequestTimestamp;
    }

    public long getTotalDischargeEnergyWsAtBatteryScaledWithEfficiency() {
        return totalDischargeEnergyWsAtBatteryScaledWithEfficiency;
    }

    public long getCurrentRequestRemainingDischargePowerWs() {
        return currentRequestRemainingDischargeEnergyWs;
    }

    public int getLastCompletedRequestDischargePowerW() {
        return (int) (lastCompletedRequestRealizedDischargeEnergyWs) / DischargeRequest.QUARTER_HOUR_TO_SECONDS;
    }

    public void handleReceivedRequest(BigDecimal efficiencyPercent, DischargeRequest receivedRequest) {
        log.info("Received new levl request: {}", receivedRequest);
        this.nextRequestEfficiencyPercent = efficiencyPercent;
        this.nextRequest = receivedRequest;
    }

    public void initAfterRestore(LocalDateTime now) {
        if (nextRequest.isExpired(now)) {
            nextRequest = DischargeRequest.inactiveRequest();
            nextRequestEfficiencyPercent = BigDecimal.valueOf(100);
        }
        update(now);
    }

    public void update(LocalDateTime now) {
        if (request.isExpired(now)) {
            completeCurrentRequest("request expired");
        }
        if (nextRequest.shouldStart(now)) {
            if (request.isActive()) {
                // TODO: 15.02.2024 Dennis: Das ist doch ein Fehlercase oder? Sollte levl davon wissen? Zusätzlich auf Levl-Seite prüfen
                completeCurrentRequest("next request should start");
            }

            request = nextRequest;
            nextRequest = DischargeRequest.inactiveRequest();
            log.info("Starting levl request: {}", request);
            currentEfficiencyPercent = nextRequestEfficiencyPercent;
            nextRequestEfficiencyPercent = BigDecimal.valueOf(100);
            currentRequestRemainingDischargeEnergyWs = request.getDischargeEnergyWs();
        }
    }

    public void handleRealizedDischargePowerWForOneSecond(int newRealizedPowerW) {
        if (!request.isActive()) {
            return;
        }
        // newRealizedPowerW for one Second -> W becomes Ws
        currentRequestRealizedDischargeEnergyWs += newRealizedPowerW;

        totalRealizedDischargeEnergyWs += newRealizedPowerW;
        if (newRealizedPowerW >= 0) {
            totalDischargeEnergyWsAtBatteryScaledWithEfficiency += Percent.undoPercentage(newRealizedPowerW, currentEfficiencyPercent);
        } else {
            totalDischargeEnergyWsAtBatteryScaledWithEfficiency += Percent.applyPercentage(newRealizedPowerW, currentEfficiencyPercent);
        }

        var newRemainingDischargeEnergyWs = currentRequestRemainingDischargeEnergyWs - newRealizedPowerW;

        boolean signHasChanged = !Sign.haveSameSign(currentRequestRemainingDischargeEnergyWs, newRemainingDischargeEnergyWs);
        currentRequestRemainingDischargeEnergyWs = newRemainingDischargeEnergyWs;
        if (signHasChanged) {
            completeCurrentRequest("request completed");
        }
    }

    private void completeCurrentRequest(String reason) {
        log.info("stopped executing levl request: {} {}", reason, request);
        lastCompletedRequestTimestamp = request.getRequestTimestamp();
        lastCompletedRequestRealizedDischargeEnergyWs = currentRequestRealizedDischargeEnergyWs;
        currentRequestRemainingDischargeEnergyWs = 0;
        currentRequestRealizedDischargeEnergyWs = 0;
        currentEfficiencyPercent = BigDecimal.valueOf(100);
        request = DischargeRequest.inactiveRequest();
    }

}
