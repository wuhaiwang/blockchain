package com.seasun.management.dto;

import com.seasun.management.model.FmUserRole;

public class FmUserRoleDto extends FmUserRole {
    private String userName;

    private String platName;

    private String projectName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPlatName() {
        return platName;
    }

    public void setPlatName(String platName) {
        this.platName = platName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}
