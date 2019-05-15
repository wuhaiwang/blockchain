package com.seasun.management.dto;

public class UserPerfWorkGroupDto {
    private Long userId;
    private String userName;
    private String loginId;
    private String performanceWorkGroupName;
    private Long performanceWorkGroupId;

    private Long managePerfWGId;
    private String managePerfWGName;

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

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getPerformanceWorkGroupName() {
        return performanceWorkGroupName;
    }

    public void setPerformanceWorkGroupName(String performanceWorkGroupName) {
        this.performanceWorkGroupName = performanceWorkGroupName;
    }

    public Long getPerformanceWorkGroupId() {
        return performanceWorkGroupId;
    }

    public void setPerformanceWorkGroupId(Long performanceWorkGroupId) {
        this.performanceWorkGroupId = performanceWorkGroupId;
    }

    public Long getManagePerfWGId() {
        return managePerfWGId;
    }

    public void setManagePerfWGId(Long managePerfWGId) {
        this.managePerfWGId = managePerfWGId;
    }

    public String getManagePerfWGName() {
        return managePerfWGName;
    }

    public void setManagePerfWGName(String managePerfWGName) {
        this.managePerfWGName = managePerfWGName;
    }
}
