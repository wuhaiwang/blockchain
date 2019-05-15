package com.seasun.management.vo;


import com.seasun.management.model.IdNameBaseObject;

import java.util.List;

public class WorkGroupUserVo {

    private Long userId;

    private String userName;

    private String loginId;

    private List<IdNameBaseObject> manageWorkGroups;

    private IdNameBaseObject workGroup;

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

    public List<IdNameBaseObject> getManageWorkGroups() {
        return manageWorkGroups;
    }

    public void setManageWorkGroups(List<IdNameBaseObject> manageWorkGroups) {
        this.manageWorkGroups = manageWorkGroups;
    }

    public IdNameBaseObject getWorkGroup() {
        return workGroup;
    }

    public void setWorkGroup(IdNameBaseObject workGroup) {
        this.workGroup = workGroup;
    }
}
