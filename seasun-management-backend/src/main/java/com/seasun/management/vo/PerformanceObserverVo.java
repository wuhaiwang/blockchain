package com.seasun.management.vo;

public class PerformanceObserverVo{
    private Long userId;

    private String userName;

    private Long performanceWorkGroupId;

    private String performanceWorkGroupName;

    private String performanceWorkGroupManageName;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getPerformanceWorkGroupId() {
        return performanceWorkGroupId;
    }

    public void setPerformanceWorkGroupId(Long performanceWorkGroupId) {
        this.performanceWorkGroupId = performanceWorkGroupId;
    }

    public String getPerformanceWorkGroupName() {
        return performanceWorkGroupName;
    }

    public void setPerformanceWorkGroupName(String performanceWorkGroupName) {
        this.performanceWorkGroupName = performanceWorkGroupName;
    }

    public String getPerformanceWorkGroupManageName() {
        return performanceWorkGroupManageName;
    }

    public void setPerformanceWorkGroupManageName(String performanceWorkGroupManageName) {
        this.performanceWorkGroupManageName = performanceWorkGroupManageName;
    }
}
