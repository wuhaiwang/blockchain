package com.seasun.management.vo.cp;

import java.math.BigDecimal;

public class ProjectBudgetInfo {

    private BigDecimal totalAmount;
    private BigDecimal surplusAmount;
    private BigDecimal payAmount;
    private BigDecimal willPayAmount;
    private Integer payNumber;
    private Integer willPayNumber;
    private Integer startYear;
    private Integer endYear;

    public Integer getStartYear() {
        return startYear;
    }

    public void setStartYear(Integer startYear) {
        this.startYear = startYear;
    }

    public Integer getEndYear() {
        return endYear;
    }

    public void setEndYear(Integer endYear) {
        this.endYear = endYear;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getSurplusAmount() {
        return surplusAmount;
    }

    public void setSurplusAmount(BigDecimal surplusAmount) {
        this.surplusAmount = surplusAmount;
    }

    public BigDecimal getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
    }

    public BigDecimal getWillPayAmount() {
        return willPayAmount;
    }

    public void setWillPayAmount(BigDecimal willPayAmount) {
        this.willPayAmount = willPayAmount;
    }

    public Integer getPayNumber() {
        return payNumber;
    }

    public void setPayNumber(Integer payNumber) {
        this.payNumber = payNumber;
    }

    public Integer getWillPayNumber() {
        return willPayNumber;
    }

    public void setWillPayNumber(Integer willPayNumber) {
        this.willPayNumber = willPayNumber;
    }
}
