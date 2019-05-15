package com.seasun.management.dto;

import java.math.BigDecimal;
import java.util.Date;

public class SimpleBudgetDto {

    private BigDecimal amount;

    private String createTime;

    private String reason;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
