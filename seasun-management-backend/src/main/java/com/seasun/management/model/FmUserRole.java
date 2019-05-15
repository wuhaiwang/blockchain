package com.seasun.management.model;

public class FmUserRole {
    private Long id;

    private Long userId;

    private Long roleId;

    private Long projectId;

    private Long platId;

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

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getPlatId() {
        return platId;
    }

    public void setPlatId(Long platId) {
        this.platId = platId;
    }

    public interface Role {
        Long projectFixRole = 1L;
        Long managerRole = 2L;
        Long platFixRole = 3L;
        Long secondManagerRole = 4L;
    }
}