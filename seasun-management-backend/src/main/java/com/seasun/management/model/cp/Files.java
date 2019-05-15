package com.seasun.management.model.cp;

import java.math.BigDecimal;
import java.util.Date;

public class Files {
    private Integer id;

    private Integer issueId;

    private String name;

    private BigDecimal days;

    private BigDecimal priceOfPerson;

    private Date sendDate;

    private Date eta;

    private Date verifyDate;

    private Integer verifyBy;

    private Integer status;

    private Boolean entry;

    private Integer modifyTimes;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIssueId() {
        return issueId;
    }

    public void setIssueId(Integer issueId) {
        this.issueId = issueId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public BigDecimal getDays() {
        return days;
    }

    public void setDays(BigDecimal days) {
        this.days = days;
    }

    public BigDecimal getPriceOfPerson() {
        return priceOfPerson;
    }

    public void setPriceOfPerson(BigDecimal priceOfPerson) {
        this.priceOfPerson = priceOfPerson;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    public Date getEta() {
        return eta;
    }

    public void setEta(Date eta) {
        this.eta = eta;
    }

    public Date getVerifyDate() {
        return verifyDate;
    }

    public void setVerifyDate(Date verifyDate) {
        this.verifyDate = verifyDate;
    }

    public Integer getVerifyBy() {
        return verifyBy;
    }

    public void setVerifyBy(Integer verifyBy) {
        this.verifyBy = verifyBy;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Boolean getEntry() {
        return entry;
    }

    public void setEntry(Boolean entry) {
        this.entry = entry;
    }

    public Integer getModifyTimes() {
        return modifyTimes;
    }

    public void setModifyTimes(Integer modifyTimes) {
        this.modifyTimes = modifyTimes;
    }
}