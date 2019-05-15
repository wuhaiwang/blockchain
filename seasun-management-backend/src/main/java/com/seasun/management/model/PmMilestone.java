package com.seasun.management.model;

import java.util.Date;

public class PmMilestone {
    private Long id;

    private Date milestoneDay;

    private Date projectEstimateDay;

    private Date endTime;

    private Integer status;

    private Long projectId;

    private String channel;

    private String risk;

    private Boolean publishFlag;

    private Date createTime;

    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getMilestoneDay() {
        return milestoneDay;
    }

    public void setMilestoneDay(Date milestoneDay) {
        this.milestoneDay = milestoneDay;
    }

    public Date getProjectEstimateDay() {
        return projectEstimateDay;
    }

    public void setProjectEstimateDay(Date projectEstimateDay) {
        this.projectEstimateDay = projectEstimateDay;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getStatus() { return status; }

    public void setStatus(Integer status) { this.status = status; }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel == null ? null : channel.trim();
    }

    public String getRisk() {
        return risk;
    }

    public void setRisk(String risk) {
        this.risk = risk == null ? null : risk.trim();
    }

    public Boolean getPublishFlag() {
        return publishFlag;
    }

    public void setPublishFlag(Boolean publishFlag) {
        this.publishFlag = publishFlag;
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