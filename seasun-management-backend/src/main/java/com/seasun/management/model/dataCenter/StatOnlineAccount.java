package com.seasun.management.model.dataCenter;

import java.math.BigDecimal;
import java.util.Date;

public class StatOnlineAccount {

    private BigDecimal configGameId;

    private Date statTime;

    private BigDecimal gatewayId;

    private BigDecimal accountCount;

    public BigDecimal getConfigGameId() {
        return configGameId;
    }

    public void setConfigGameId(BigDecimal configGameId) {
        this.configGameId = configGameId;
    }

    public Date getStatTime() {
        return statTime;
    }

    public void setStatTime(Date statTime) {
        this.statTime = statTime;
    }

    public BigDecimal getGatewayId() {
        return gatewayId;
    }

    public void setGatewayId(BigDecimal gatewayId) {
        this.gatewayId = gatewayId;
    }

    public BigDecimal getAccountCount() {
        return accountCount;
    }

    public void setAccountCount(BigDecimal accountCount) {
        this.accountCount = accountCount;
    }

}