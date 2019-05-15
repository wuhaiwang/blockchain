package com.seasun.management.dto;

import com.seasun.management.vo.UserPerformanceDetailVo;

import java.util.Date;

public class UserPerformanceDetailDto {
    private String name;
    private Long employeeNo;
    private Date joinTime;
    private Integer workAge;
    private String post;
    private String workGroup;
    private String performance;
    private String monthGoal;
    private String managerComment;
    private String selfComment;
    private String status;
    private String manager;
    private String lastPerformance;

    public String getPerformance() {
        return performance;
    }

    public void setPerformance(String performance) {
        this.performance = performance;
    }

    public String getMonthGoal() {
        return monthGoal;
    }

    public void setMonthGoal(String monthGoal) {
        this.monthGoal = monthGoal;
    }

    public String getManagerComment() {
        return managerComment;
    }

    public void setManagerComment(String managerComment) {
        this.managerComment = managerComment;
    }

    public String getSelfComment() {
        return selfComment;
    }

    public void setSelfComment(String selfComment) {
        this.selfComment = selfComment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getEmployeeNo() {
        return employeeNo;
    }

    public void setEmployeeNo(Long employeeNo) {
        this.employeeNo = employeeNo;
    }

    public Date getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(Date joinTime) {
        this.joinTime = joinTime;
    }

    public Integer getWorkAge() {
        return workAge;
    }

    public void setWorkAge(Integer workAge) {
        this.workAge = workAge;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getWorkGroup() {
        return workGroup;
    }

    public void setWorkGroup(String workGroup) {
        this.workGroup = workGroup;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public String getLastPerformance() {
        return lastPerformance;
    }

    public void setLastPerformance(String lastPerformance) {
        this.lastPerformance = lastPerformance;
    }

    public UserPerformanceDetailVo dto2vo(UserPerformanceDetailVo userPerformanceDetailVo,
                                          UserPerformanceDetailVo.PerformanceInfo performanceInfo,
                                          UserPerformanceDetailVo.UserInfo userInfo) {
        userInfo.setName(this.getName());
        userInfo.setEmployeeNo(this.getEmployeeNo());
        userInfo.setJoinTime(this.getJoinTime());
        userInfo.setWorkAge(this.getWorkAge());
        userInfo.setPost(this.getPost());
        userInfo.setWorkGroup(this.getWorkGroup());
        userInfo.setStatus(this.getStatus());
        userInfo.setManager(this.getManager());
        performanceInfo.setPerformance(this.getPerformance());
        performanceInfo.setMonthGoal(this.getMonthGoal());
        performanceInfo.setManagerComment(this.getManagerComment());
        performanceInfo.setSelfComment(this.getSelfComment());
        performanceInfo.setLastPerformance(this.getLastPerformance());
        userPerformanceDetailVo.setPerformanceInfo(performanceInfo);
        userPerformanceDetailVo.setUserInfo(userInfo);
        return userPerformanceDetailVo;
    }

    public int getNum() {
        int num = 0;
        if (name != null)
            num++;
        if (employeeNo != null)
            num++;
        if (joinTime != null)
            num++;
        if (workAge != null)
            num++;
        if (post != null)
            num++;
        if (workGroup != null)
            num++;
        if (performance != null)
            num++;
        if (monthGoal != null)
            num++;
        if (managerComment != null)
            num++;
        if (selfComment != null)
            num++;
        if (status != null)
            num++;
        if (manager != null)
            num++;
        if (lastPerformance != null)
            num++;
        return num;
    }
}
