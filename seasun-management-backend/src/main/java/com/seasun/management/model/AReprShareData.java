package com.seasun.management.model;

import java.util.Date;

public class AReprShareData {
    private Long id;

    private String projectName;

    private String platName;

    private String shareItemId;

    private String sharedItemId;

    private String templateId;

    private Integer year;

    private Integer month;

    private Float sharePro;

    private Float shareAmount;

    private Float shareNumber;

    private Float fixedOutnumber;

    private Date createTime;

    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName == null ? null : projectName.trim();
    }

    public String getPlatName() {
        return platName;
    }

    public void setPlatName(String platName) {
        this.platName = platName == null ? null : platName.trim();
    }

    public String getShareItemId() {
        return shareItemId;
    }

    public void setShareItemId(String shareItemId) {
        this.shareItemId = shareItemId == null ? null : shareItemId.trim();
    }

    public String getSharedItemId() {
        return sharedItemId;
    }

    public void setSharedItemId(String sharedItemId) {
        this.sharedItemId = sharedItemId == null ? null : sharedItemId.trim();
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId == null ? null : templateId.trim();
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

    public Float getSharePro() {
        return sharePro;
    }

    public void setSharePro(Float sharePro) {
        this.sharePro = sharePro;
    }

    public Float getShareAmount() {
        return shareAmount;
    }

    public void setShareAmount(Float shareAmount) {
        this.shareAmount = shareAmount;
    }

    public Float getShareNumber() {
        return shareNumber;
    }

    public void setShareNumber(Float shareNumber) {
        this.shareNumber = shareNumber;
    }

    public Float getFixedOutnumber() {
        return fixedOutnumber;
    }

    public void setFixedOutnumber(Float fixedOutnumber) {
        this.fixedOutnumber = fixedOutnumber;
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