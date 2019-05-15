package com.seasun.management.model;

import java.util.Date;

public class UserSalaryChange {
    private Long id;

    private Long workGroupId;

    private String subGroup;

    private Long userId;

    private Integer year;

    private Integer quarter;

    private Integer oldSalary;

    private Integer increaseSalary;

    private Integer score;

    private Integer performanceCount;

    private String evaluateType;

    private String grade;

    private String status;

    private Date lastSalaryChangeDate;

    private Integer lastSalaryChangeAmount;

    private Date lastGradeChangeDate;

    private Date createTime;

    private Date updateTime;

    private Integer workAge;

    private Integer workAgeInKs;

    private String post;

    private String workGroupName;

    public interface Status {
        String waitingForCommit = "待提交";
        String waitingForConfirm = "待确认";
        String finished = "已完成";
    }

    public interface PerformanceResult {
        String normal = "正常";
        String good = "可调薪";
        String bad = "待淘汰";
    }

    public interface EvaluateType {
        String high = "高估";
        String low = "低估";
        String normal = "正常";
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

    public Integer getQuarter() {
        return quarter;
    }

    public void setQuarter(Integer quarter) {
        this.quarter = quarter;
    }

    public Integer getOldSalary() {
        return oldSalary;
    }

    public void setOldSalary(Integer oldSalary) {
        this.oldSalary = oldSalary;
    }

    public Integer getIncreaseSalary() {
        return increaseSalary;
    }

    public void setIncreaseSalary(Integer increaseSalary) {
        this.increaseSalary = increaseSalary;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getEvaluateType() {
        return evaluateType;
    }

    public void setEvaluateType(String evaluateType) {
        this.evaluateType = evaluateType == null ? null : evaluateType.trim();
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade == null ? null : grade.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public Date getLastSalaryChangeDate() {
        return lastSalaryChangeDate;
    }

    public void setLastSalaryChangeDate(Date lastSalaryChangeDate) {
        this.lastSalaryChangeDate = lastSalaryChangeDate;
    }

    public Integer getLastSalaryChangeAmount() {
        return lastSalaryChangeAmount;
    }

    public void setLastSalaryChangeAmount(Integer lastSalaryChangeAmount) {
        this.lastSalaryChangeAmount = lastSalaryChangeAmount;
    }

    public Date getLastGradeChangeDate() {
        return lastGradeChangeDate;
    }

    public void setLastGradeChangeDate(Date lastGradeChangeDate) {
        this.lastGradeChangeDate = lastGradeChangeDate;
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

    public Integer getPerformanceCount() {
        return performanceCount;
    }

    public void setPerformanceCount(Integer performanceCount) {
        this.performanceCount = performanceCount;
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

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getWorkGroupName() {
        return workGroupName;
    }

    public void setWorkGroupName(String workGroupName) {
        this.workGroupName = workGroupName;
    }
}
