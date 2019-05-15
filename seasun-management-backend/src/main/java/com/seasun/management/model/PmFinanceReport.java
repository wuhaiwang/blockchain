package com.seasun.management.model;

import java.util.Date;

public class PmFinanceReport {
    private Long id;

    private Long projectId;

    private Integer year;

    private Integer month;

    private String incomeFloatReason;

    private String costFloatReason;

    private String profitFloatReason;

    private Integer status;

    private Date createTime;

    private Date updateTime;

    public interface Status{
        Integer INITIALIZED = 0;
        Integer SUBMITTED = 1;
        Integer PUBLISHED = 2;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public String getIncomeFloatReason() {
        return incomeFloatReason;
    }

    public void setIncomeFloatReason(String incomeFloatReason) {
        this.incomeFloatReason = incomeFloatReason == null ? null : incomeFloatReason.trim();
    }

    public String getCostFloatReason() {
        return costFloatReason;
    }

    public void setCostFloatReason(String costFloatReason) {
        this.costFloatReason = costFloatReason == null ? null : costFloatReason.trim();
    }

    public String getProfitFloatReason() {
        return profitFloatReason;
    }

    public void setProfitFloatReason(String profitFloatReason) {
        this.profitFloatReason = profitFloatReason == null ? null : profitFloatReason.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}