package com.seasun.management.model;

public class RUserWorkGroupPerm {
    private Long id;

    private Long userId;

    private Long workGroupId;

    private Long workGroupRoleId;

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

    public Long getWorkGroupId() {
        return workGroupId;
    }

    public void setWorkGroupId(Long workGroupId) {
        this.workGroupId = workGroupId;
    }

    public Long getWorkGroupRoleId() {
        return workGroupRoleId;
    }

    public void setWorkGroupRoleId(Long workGroupRoleId) {
        this.workGroupRoleId = workGroupRoleId;
    }
}