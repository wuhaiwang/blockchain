package com.seasun.management.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class UserPerformance {
    private Long id;

    private Long workGroupId;

    private String subGroup;

    private Long parentGroup;

    private Long userId;

    private Integer year;

    private Integer month;

    private String finalPerformance;

    private String status;

    private String monthGoal;

    private Boolean subBFlag;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss", timezone = "GMT+8")
    private Date monthGoalLastModifyTime;

    private String managerComment;

    private String selfComment;

    private String selfPerformance;

    private Long fmProjectId;

    private String fmConfirmedStatus;

    private String post;

    private Integer workAge;

    private Integer workAgeInKs;

    private String workGroupName;

    private String workStatus;

    private Date createTime;

    private Date updateTime;

    private String directManagerComment;

    private Long lastModifyUser;
    //  the following are user defined...
    public interface Status {
        String unsubmitted = "未提交";
        String submitted = "已提交";
        String assessed = "已评定";
        String locked = "待确认";
        String complete = "已完成";
    }

    public interface Performance {
        String All = "全部";
        String S = "S";
        String A = "A";
        String B = "B";
        String C = "C";
        String B2 = "B-";
        String invalided = "不参与";
    }

    public final static List<String> performanceDataDictionary = Collections.unmodifiableList(Arrays.asList(Performance.S, Performance.A, Performance.B, Performance.C, Performance.invalided));

    public interface FmConfirmedStatus {
        String firstUnconfirmed = "等待首次项目确认";
        String firstConfirmed = "首次项目审核通过";
        String firstComplete = "首次项目审核完成";
        String secondUnconfirmed = "等待二次项目确认";
        String secondConfirmed = "二次项目审核通过";
        String secondComplete = "二次项目审核完成";
    }

    public Long getLastModifyUser() {
        return lastModifyUser;
    }

    public void setLastModifyUser(Long lastModifyUser) {
        this.lastModifyUser = lastModifyUser;
    }

    public String getDirectManagerComment() {
        return directManagerComment;
    }

    public void setDirectManagerComment(String directManagerComment) {
        this.directManagerComment = directManagerComment;
    }

    public Boolean getSubBFlag() {
        return subBFlag;
    }

    public void setSubBFlag(Boolean subBFlag) {
        this.subBFlag = subBFlag;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getWorkGroupId() {
        return workGroupId;
    }

    public void setWorkGroupId(Long workGroupId) {
        this.workGroupId = workGroupId;
    }

    public String getSubGroup() {
        return subGroup;
    }

    public void setSubGroup(String subGroup) {
        this.subGroup = subGroup == null ? null : subGroup.trim();
    }

    public Long getParentGroup() {
        return parentGroup;
    }

    public void setParentGroup(Long parentGroup) {
        this.parentGroup = parentGroup;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public String getFinalPerformance() {
        return finalPerformance;
    }

    public void setFinalPerformance(String finalPerformance) {
        this.finalPerformance = finalPerformance == null ? null : finalPerformance.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public String getMonthGoal() {
        return monthGoal;
    }

    public void setMonthGoal(String monthGoal) {
        this.monthGoal = monthGoal == null ? null : monthGoal.trim();
    }

    public Date getMonthGoalLastModifyTime() {
        return monthGoalLastModifyTime;
    }

    public void setMonthGoalLastModifyTime(Date monthGoalLastModifyTime) {
        this.monthGoalLastModifyTime = monthGoalLastModifyTime;
    }

    public String getManagerComment() {
        return managerComment;
    }

    public void setManagerComment(String managerComment) {
        this.managerComment = managerComment == null ? null : managerComment.trim();
    }

    public String getSelfComment() {
        return selfComment;
    }

    public void setSelfComment(String selfComment) {
        this.selfComment = selfComment == null ? null : selfComment.trim();
    }

    public String getSelfPerformance() {
        return selfPerformance;
    }

    public void setSelfPerformance(String selfPerformance) {
        this.selfPerformance = selfPerformance == null ? null : selfPerformance.trim();
    }

    public Long getFmProjectId() {
        return fmProjectId;
    }

    public void setFmProjectId(Long fmProjectId) {
        this.fmProjectId = fmProjectId;
    }

    public String getFmConfirmedStatus() {
        return fmConfirmedStatus;
    }

    public void setFmConfirmedStatus(String fmConfirmedStatus) {
        this.fmConfirmedStatus = fmConfirmedStatus == null ? null : fmConfirmedStatus.trim();
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post == null ? null : post.trim();
    }

    public Integer getWorkAge() {
        return workAge;
    }

    public void setWorkAge(Integer workAge) {
        this.workAge = workAge;
    }

    public Integer getWorkAgeInKs() {
        return workAgeInKs;
    }

    public void setWorkAgeInKs(Integer workAgeInKs) {
        this.workAgeInKs = workAgeInKs;
    }

    public String getWorkGroupName() {
        return workGroupName;
    }

    public void setWorkGroupName(String workGroupName) {
        this.workGroupName = workGroupName == null ? null : workGroupName.trim();
    }

    public String getWorkStatus() {
        return workStatus;
    }

    public void setWorkStatus(String workStatus) {
        this.workStatus = workStatus;
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