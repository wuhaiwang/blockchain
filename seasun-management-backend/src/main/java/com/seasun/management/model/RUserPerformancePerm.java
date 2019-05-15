package com.seasun.management.model;

public class RUserPerformancePerm {
    private Long id;

    private Long userId;

    private Long performanceWorkGroupId;

    private Long performanceWorkGroupRoleId;

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

    public Long getPerformanceWorkGroupId() {
        return performanceWorkGroupId;
    }

    public void setPerformanceWorkGroupId(Long performanceWorkGroupId) {
        this.performanceWorkGroupId = performanceWorkGroupId;
    }

    public Long getPerformanceWorkGroupRoleId() {
        return performanceWorkGroupRoleId;
    }

    public void setPerformanceWorkGroupRoleId(Long performanceWorkGroupRoleId) {
        this.performanceWorkGroupRoleId = performanceWorkGroupRoleId;
    }
}