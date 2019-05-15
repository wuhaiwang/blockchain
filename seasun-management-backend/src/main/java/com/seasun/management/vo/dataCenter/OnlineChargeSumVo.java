package com.seasun.management.vo.dataCenter;

import java.math.BigDecimal;

public class OnlineChargeSumVo {

    private String time;
    private BigDecimal chargeValue;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public BigDecimal getChargeValue() {
        return chargeValue;
    }

    public void setChargeValue(BigDecimal chargeValue) {
        this.chargeValue = chargeValue;
    }
}
