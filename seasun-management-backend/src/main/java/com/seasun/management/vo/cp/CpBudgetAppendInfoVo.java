package com.seasun.management.vo.cp;

import java.math.BigDecimal;
import java.util.Date;

public class CpBudgetAppendInfoVo {
    private Long id;

    private Long cpBudgetId;

    private BigDecimal amount;

    private Date createTime;

    private String createTimeStr;

    private String reason;

    private String type;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCpBudgetId() {
        return cpBudgetId;
    }

    public void setCpBudgetId(Long cpBudgetId) {
        this.cpBudgetId = cpBudgetId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason == null ? null : reason.trim();
    }

    public String getCreateTimeStr() {
        return createTimeStr;
    }

    public void setCreateTimeStr(String createTimeStr) {
        this.createTimeStr = createTimeStr;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}