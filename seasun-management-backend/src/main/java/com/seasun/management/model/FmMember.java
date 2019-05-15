package com.seasun.management.model;

public class FmMember {
    private Long id;

    private Long userId;

    private Long platId;

    private Long projectId;

    private Boolean fixedFlag;

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

    public Long getPlatId() {
        return platId;
    }

    public void setPlatId(Long platId) {
        this.platId = platId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Boolean getFixedFlag() {
        return fixedFlag;
    }

    public void setFixedFlag(Boolean fixedFlag) {
        this.fixedFlag = fixedFlag;
    }
}