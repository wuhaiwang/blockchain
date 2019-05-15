package com.seasun.management.vo;

import com.seasun.management.model.FnPlatShareConfig;

public class FnPlatShareConfigOfUserVo extends FnPlatShareConfig {
    private String userName;
    private String projectName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}
