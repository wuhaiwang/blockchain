package com.seasun.management.model;

public class RUserContactGroup {
    private Long id;

    private Long userId;

    private Long userContactGroupId;

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

    public Long getUserContactGroupId() {
        return userContactGroupId;
    }

    public void setUserContactGroupId(Long userContactGroupId) {
        this.userContactGroupId = userContactGroupId;
    }
}