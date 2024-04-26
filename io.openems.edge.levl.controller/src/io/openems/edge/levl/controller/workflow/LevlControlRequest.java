package io.openems.edge.levl.controller.workflow;

import com.google.gson.JsonObject;
import io.openems.common.exceptions.OpenemsError;
import io.openems.common.jsonrpc.base.JsonrpcRequest;
import io.openems.edge.levl.controller.controllers.common.Limit;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class LevlControlRequest extends JsonrpcRequest {
    public static final String METHOD = "sendLevlControlRequest";
    private int sellToGridLimitW;
    private int buyFromGridLimitW;
    private String levlRequestId;
    private String levlRequestTimestamp;
    private int levlPowerW;
    private int levlChargeDelaySec;
    private int levlChargeDurationSec;
    private int levlSocLowerBoundPercent;
    private int levlSocUpperBoundPercent;
    private BigDecimal efficiencyPercent;
    private final JsonObject params;


    public static LevlControlRequest from(JsonrpcRequest request) throws OpenemsError.OpenemsNamedException {
        var params = request.getParams();
        return new LevlControlRequest(params);
    }

    public LevlControlRequest(JsonObject params) throws OpenemsError.OpenemsNamedException {
        super(LevlControlRequest.METHOD);
        this.params = params;
        try {
            this.parseFields(params);
        } catch (NullPointerException e) {
            throw OpenemsError.JSONRPC_INVALID_MESSAGE.exception("missing fields in request: " + e.getMessage());
        } catch (NumberFormatException e) {
            throw OpenemsError.JSONRPC_INVALID_MESSAGE.exception("wrong field type in request: " + e.getMessage());
        }
        if (this.efficiencyPercent.compareTo(BigDecimal.ZERO) <= 0) {
            throw OpenemsError.JSONRPC_INVALID_MESSAGE.exception("efficiencyPercent must be > 0");
        }
        if (this.efficiencyPercent.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw OpenemsError.JSONRPC_INVALID_MESSAGE.exception("efficiencyPercent must be <= 100");
        }
    }

    public String getLevlRequestId() {
        return this.levlRequestId;
    }

    public BigDecimal getEfficiencyPercent() {
        return this.efficiencyPercent;
    }

    private void parseFields(JsonObject params) {
    	this.levlRequestId = params.get("levlRequestId").getAsString();
        this.levlRequestTimestamp = params.get("levlRequestTimestamp").getAsString();
        this.levlPowerW = params.get("levlPowerW").getAsInt();
        this.levlChargeDelaySec = params.get("levlChargeDelaySec").getAsInt();
        this.levlChargeDurationSec = params.get("levlChargeDurationSec").getAsInt();
        this.levlSocLowerBoundPercent = params.get("levlSocLowerBoundPercent").getAsInt();
        this.levlSocUpperBoundPercent = params.get("levlSocUpperBoundPercent").getAsInt();
        this.sellToGridLimitW = params.get("sellToGridLimitW").getAsInt();
        this.buyFromGridLimitW = params.get("buyFromGridLimitW").getAsInt();
        this.efficiencyPercent = params.get("efficiencyPercent").getAsBigDecimal();
    }

    @Override
    public JsonObject getParams() {
        return this.params;
    }

    public Limit createGridPowerLimitW() {
        return new Limit(this.sellToGridLimitW, this.buyFromGridLimitW);
    }

    public DischargeRequest createDischargeRequest(LocalDateTime now) {
        return DischargeRequest.of(
                now,
                this.levlRequestTimestamp,
                this.levlRequestId,
                this.levlPowerW,
                this.levlChargeDelaySec,
                this.levlChargeDurationSec
        );
    }

    public LevlSocConstraints createLevlSocConstraints(int physicalSocLowerBoundPercent, int physicalSocUpperBoundPercent) {
        return new LevlSocConstraints(
                physicalSocLowerBoundPercent,
                physicalSocUpperBoundPercent,
                this.levlSocLowerBoundPercent,
                this.levlSocUpperBoundPercent
        );
    }
}
