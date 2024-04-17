package io.openems.edge.levl.controller.workflow;

import io.openems.common.jsonrpc.request.UpdateComponentConfigRequest;
import io.openems.edge.levl.controller.controllers.common.Limit;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TestObjects {


    public static LevlWorkflowState.LevlWorkflowStateMemento levlWorkflowComponentMemento() {
        var levlSocConstraintsMemento = new LevlSocConstraints.LevlSocConstraintsMemento(new SocConstraint.SocConstraintMemento(1, 100),
                new SocConstraint.SocConstraintMemento(5, 95));
        return new LevlWorkflowState.LevlWorkflowStateMemento(222, 333, 111, dischargeState(), levlSocConstraintsMemento, new Limit.LimitMemento(-1100, 1200));
    }

    private static DischargeState.DischargeStateMemento dischargeState() {
        var now = LocalDateTime.of(2021, 1, 1, 0, 0);
        var currentRequest = new DischargeRequest.DischargeRequestMemento("id0", "2024-02-15T15:00:00Z", 9, now.plusSeconds(10), now.plusSeconds(11), true);
        var nextRequest = new DischargeRequest.DischargeRequestMemento("id1", "2024-03-13T18:00:00Z", 6, now.plusSeconds(7), now.plusSeconds(8), false);
        return new DischargeState.DischargeStateMemento(1, 2, 3, 4, 5, new BigDecimal("99.03"), new BigDecimal("95.44"), "2024-02-15T12:00:00Z", currentRequest, nextRequest);
    }

    public static String describe(UpdateComponentConfigRequest.Property property) {
        return property.getName() + "=" + property.getValue();
    }
}
