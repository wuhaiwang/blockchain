package com.seasun.management.model;

import java.math.BigDecimal;
import java.util.Date;

public class FnPlatShareConfig {
    private Long id;

    private Long platId;

    private Long projectId;

    private Integer year;

    private Integer month;

    private BigDecimal sharePro;

    private BigDecimal shareAmount;

    private BigDecimal fixedNumber;

    private Long createBy;

    private String remark;

    private BigDecimal weight;

    private Date createTime;

    private Date updateTime;

    public BigDecimal getShareAmount() {
        return shareAmount;
    }

    public void setShareAmount(BigDecimal shareAmount) {
        this.shareAmount = shareAmount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPlatId() {
        return platId;
    }

    public void setPlatId(Long platId) {
        this.platId = platId;
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

    public BigDecimal getSharePro() {
        return sharePro;
    }

    public void setSharePro(BigDecimal sharePro) {
        this.sharePro = sharePro;
    }

    public BigDecimal getFixedNumber() {
        return fixedNumber;
    }

    public void setFixedNumber(BigDecimal fixedNumber) {
        this.fixedNumber = fixedNumber;
    }

    public Long getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Long createBy) {
        this.createBy = createBy;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
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