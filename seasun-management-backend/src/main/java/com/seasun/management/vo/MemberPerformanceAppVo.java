package com.seasun.management.vo;

public class MemberPerformanceAppVo {

    public interface Status {
        String unsubmitted = "未提交";
        String submitted = "待评定";
        String assessed = "已评定";
        String complete = "已完成";
        String confirmed = "已锁定";
    }

    private Long id;

    private Long userId;

    private String loginId;

    private Long employeeNo;

    private String name;

    private Boolean monthGoalFlag;

    private String managerComment;

    private Boolean managerCommentFlag;

    private String selfPerformance;

    private String finalPerformance;

    private String status;

    private Integer sort;

    private String directGroupName;

    private Boolean outOfGroupFlag;

    private Boolean fixMemberFlag;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public Long getEmployeeNo() {
        return employeeNo;
    }

    public void setEmployeeNo(Long employeeNo) {
        this.employeeNo = employeeNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getMonthGoalFlag() {
        return monthGoalFlag;
    }

    public void setMonthGoalFlag(Boolean monthGoalFlag) {
        this.monthGoalFlag = monthGoalFlag;
    }

    public Boolean getManagerCommentFlag() {
        return managerCommentFlag;
    }

    public void setManagerCommentFlag(Boolean managerCommentFlag) {
        this.managerCommentFlag = managerCommentFlag;
    }

    public String getSelfPerformance() {
        return selfPerformance;
    }

    public String getFinalPerformance() {
        return finalPerformance;
    }

    public void setFinalPerformance(String finalPerformance) {
        this.finalPerformance = finalPerformance;
    }

    public void setSelfPerformance(String selfPerformance) {
        this.selfPerformance = selfPerformance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getManagerComment() {
        return managerComment;
    }

    public void setManagerComment(String managerComment) {
        this.managerComment = managerComment;
    }

    public String getDirectGroupName() {
        return directGroupName;
    }

    public void setDirectGroupName(String directGroupName) {
        this.directGroupName = directGroupName;
    }

    public Boolean getOutOfGroupFlag() {
        return outOfGroupFlag;
    }

    public void setOutOfGroupFlag(Boolean outOfGroupFlag) {
        this.outOfGroupFlag = outOfGroupFlag;
    }

    public Boolean getFixMemberFlag() {
        return fixMemberFlag;
    }

    public void setFixMemberFlag(Boolean fixMemberFlag) {
        this.fixMemberFlag = fixMemberFlag;
    }
}
