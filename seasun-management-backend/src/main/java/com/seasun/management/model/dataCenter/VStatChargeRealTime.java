package com.seasun.management.model.dataCenter;

import java.math.BigDecimal;
import java.util.Date;

public class VStatChargeRealTime {
    private BigDecimal configGameId;

    private Date beginTime;

    private Date endTime;

    private BigDecimal accountNum;

    private BigDecimal numThanYesterday;

    private BigDecimal numThanLastWeek;

    private BigDecimal chargeValue;

    private BigDecimal valueThanYesterday;

    private BigDecimal valueThanLastWeek;

    public BigDecimal getConfigGameId() {
        return configGameId;
    }

    public void setConfigGameId(BigDecimal configGameId) {
        this.configGameId = configGameId;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public BigDecimal getAccountNum() {
        return accountNum;
    }

    public void setAccountNum(BigDecimal accountNum) {
        this.accountNum = accountNum;
    }

    public BigDecimal getNumThanYesterday() {
        return numThanYesterday;
    }

    public void setNumThanYesterday(BigDecimal numThanYesterday) {
        this.numThanYesterday = numThanYesterday;
    }

    public BigDecimal getNumThanLastWeek() {
        return numThanLastWeek;
    }

    public void setNumThanLastWeek(BigDecimal numThanLastWeek) {
        this.numThanLastWeek = numThanLastWeek;
    }

    public BigDecimal getChargeValue() {
        return chargeValue;
    }

    public void setChargeValue(BigDecimal chargeValue) {
        this.chargeValue = chargeValue;
    }

    public BigDecimal getValueThanYesterday() {
        return valueThanYesterday;
    }

    public void setValueThanYesterday(BigDecimal valueThanYesterday) {
        this.valueThanYesterday = valueThanYesterday;
    }

    public BigDecimal getValueThanLastWeek() {
        return valueThanLastWeek;
    }

    public void setValueThanLastWeek(BigDecimal valueThanLastWeek) {
        this.valueThanLastWeek = valueThanLastWeek;
    }
}