package com.seasun.management.vo.cp;

import com.seasun.management.dto.SimpleBudgetDto;
import com.seasun.management.dto.cp.CPOrderDto;
import com.seasun.management.vo.BasePeriodAppVo;

import java.math.BigDecimal;
import java.util.List;

public class ProjectOutcourceInfoVo {
    private BigDecimal totalAmount ;
    private BigDecimal surplusAmount;
    private BigDecimal payAmount;
    private BigDecimal willPayAmount;
    private Double percent ;
    private Integer inProgress ;
    private Integer verify ;
    private Integer totalRecord ;
    private List<CPOrderDto> orderItems;
    private List<SimpleBudgetDto> budgetItems ;
    private BasePeriodAppVo baseInfo;

    public BasePeriodAppVo getBaseInfo() {
        return baseInfo;
    }

    public void setBaseInfo(BasePeriodAppVo baseInfo) {
        this.baseInfo = baseInfo;
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

    public Double getPercent() {
        return percent;
    }

    public void setPercent(Double percent) {
        this.percent = percent;
    }

    public Integer getInProgress() {
        return inProgress;
    }

    public void setInProgress(Integer inProgress) {
        this.inProgress = inProgress;
    }

    public Integer getVerify() {
        return verify;
    }

    public void setVerify(Integer verify) {
        this.verify = verify;
    }

    public Integer getTotalRecord() {
        return totalRecord;
    }

    public void setTotalRecord(Integer totalRecord) {
        this.totalRecord = totalRecord;
    }

    public List<CPOrderDto> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<CPOrderDto> orderItems) {
        this.orderItems = orderItems;
    }

    public List<SimpleBudgetDto> getBudgetItems() {
        return budgetItems;
    }

    public void setBudgetItems(List<SimpleBudgetDto> budgetItems) {
        this.budgetItems = budgetItems;
    }
}
