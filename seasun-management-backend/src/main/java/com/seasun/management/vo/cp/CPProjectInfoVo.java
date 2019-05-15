package com.seasun.management.vo.cp;

import java.math.BigDecimal;

public class CPProjectInfoVo {
    public interface sort{
        String sortIndex1="totalAmount";
        String sortIndex2="usedAmount";
        String sortIndex3="totalOrder";
        String sortDesc="desc";
        String sortAsc="asc";
    }
    private Long id;
    private String name;
    private String logo;
    private BigDecimal totalAmount;
    private BigDecimal surplusAmount;
    private BigDecimal payAmount;
    private BigDecimal willPayAmount;
    private Double percent;
    private Integer ordersRecord;
    private String contactName;
    private String cPProjectId;
    private String cPProjectName;

    public String getcPProjectId() {
        return cPProjectId;
    }

    public void setcPProjectId(String cPProjectId) {
        this.cPProjectId = cPProjectId;
    }

    public String getcPProjectName() {
        return cPProjectName;
    }

    public void setcPProjectName(String cPProjectName) {
        this.cPProjectName = cPProjectName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
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

    public Integer getOrdersRecord() {
        return ordersRecord;
    }

    public void setOrdersRecord(Integer ordersRecord) {
        this.ordersRecord = ordersRecord;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }
}
