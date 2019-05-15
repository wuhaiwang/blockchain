package com.seasun.management.model;

import java.util.Date;

public class PmPlanDetail {
    private Long id;

    private Long pmPlanId;

    private String stageName;

    private Date startTime;

    private Date endTime;

    private String monthGoal;

    private String description;

    private String actualStatus;

    private String delayReason;

    private String actualProgress;

    private String cancelReason;

    private String managerId;

    private Date createTime;

    private Date updateTime;

    private Date realEndTime;

    private Boolean newFlag;

    public interface ActualStatus {
        String UNDO = "undo";
        String DOING = "doing";
        String DONE = "done";
        String CANCELLED = "cancelled";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPmPlanId() {
        return pmPlanId;
    }

    public void setPmPlanId(Long pmPlanId) {
        this.pmPlanId = pmPlanId;
    }

    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName == null ? null : stageName.trim();
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getMonthGoal() {
        return monthGoal;
    }

    public void setMonthGoal(String monthGoal) {
        this.monthGoal = monthGoal == null ? null : monthGoal.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getActualStatus() {
        return actualStatus;
    }

    public void setActualStatus(String actualStatus) {
        this.actualStatus = actualStatus;
    }

    public String getDelayReason() {
        return delayReason;
    }

    public void setDelayReason(String delayReason) {
        this.delayReason = delayReason == null ? null : delayReason.trim();
    }

    public String getActualProgress() {
        return actualProgress;
    }

    public void setActualProgress(String actualProgress) {
        this.actualProgress = actualProgress == null ? null : actualProgress.trim();
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason == null ? null : cancelReason.trim();
    }

    public String getManagerId() {
        return managerId;
    }

    public void setManagerId(String managerId) {
        this.managerId = managerId;
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

    public Date getRealEndTime() {
        return realEndTime;
    }

    public void setRealEndTime(Date realEndTime) {
        this.realEndTime = realEndTime;
    }

    public Boolean getNewFlag() {
        return newFlag;
    }

    public void setNewFlag(Boolean newFlag) {
        this.newFlag = newFlag;
    }
}