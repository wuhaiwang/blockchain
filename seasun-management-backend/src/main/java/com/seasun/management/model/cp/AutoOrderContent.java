package com.seasun.management.model.cp;

import java.util.Date;

public class AutoOrderContent {
    private Integer issueId;

    private String workName;

    private String workAmount;

    private String workCost;

    private String fileNames;

    private String workRequirements;

    private String theFormatWorks;

    private String endDate;

    private String acceptanceCriteria;

    private String remark;

    private String other;

    private Date opTime;

    private String makeType;

    private String content;

    public Integer getIssueId() {
        return issueId;
    }

    public void setIssueId(Integer issueId) {
        this.issueId = issueId;
    }

    public String getWorkName() {
        return workName;
    }

    public void setWorkName(String workName) {
        this.workName = workName == null ? null : workName.trim();
    }

    public String getWorkAmount() {
        return workAmount;
    }

    public void setWorkAmount(String workAmount) {
        this.workAmount = workAmount == null ? null : workAmount.trim();
    }

    public String getWorkCost() {
        return workCost;
    }

    public void setWorkCost(String workCost) {
        this.workCost = workCost == null ? null : workCost.trim();
    }

    public String getFileNames() {
        return fileNames;
    }

    public void setFileNames(String fileNames) {
        this.fileNames = fileNames == null ? null : fileNames.trim();
    }

    public String getWorkRequirements() {
        return workRequirements;
    }

    public void setWorkRequirements(String workRequirements) {
        this.workRequirements = workRequirements == null ? null : workRequirements.trim();
    }

    public String getTheFormatWorks() {
        return theFormatWorks;
    }

    public void setTheFormatWorks(String theFormatWorks) {
        this.theFormatWorks = theFormatWorks == null ? null : theFormatWorks.trim();
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate == null ? null : endDate.trim();
    }

    public String getAcceptanceCriteria() {
        return acceptanceCriteria;
    }

    public void setAcceptanceCriteria(String acceptanceCriteria) {
        this.acceptanceCriteria = acceptanceCriteria == null ? null : acceptanceCriteria.trim();
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other == null ? null : other.trim();
    }

    public Date getOpTime() {
        return opTime;
    }

    public void setOpTime(Date opTime) {
        this.opTime = opTime;
    }

    public String getMakeType() {
        return makeType;
    }

    public void setMakeType(String makeType) {
        this.makeType = makeType == null ? null : makeType.trim();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }
}