package com.seasun.management.model;

public class UserIdMinVo extends IdNameBaseObject{
    private Long userId;

    private Long performanceWorkGroupRoleId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getPerformanceWorkGroupRoleId() {
        return performanceWorkGroupRoleId;
    }

    public void setPerformanceWorkGroupRoleId(Long performanceWorkGroupRoleId) {
        this.performanceWorkGroupRoleId = performanceWorkGroupRoleId;
    }
}
