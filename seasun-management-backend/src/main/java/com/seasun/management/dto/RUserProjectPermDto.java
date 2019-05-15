package com.seasun.management.dto;

import com.seasun.management.model.RUserProjectPerm;

public class RUserProjectPermDto extends RUserProjectPerm {

    private String roleName;

    private String projectStatus;

    private String serviceLine;

    private Integer appShowMode;

    public Integer getAppShowMode() {
        return appShowMode;
    }

    public void setAppShowMode(Integer appShowMode) {
        this.appShowMode = appShowMode;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getProjectStatus() {
        return projectStatus;
    }

    public void setProjectStatus(String projectStatus) {
        this.projectStatus = projectStatus;
    }

    public String getServiceLine() {
        return serviceLine;
    }

    public void setServiceLine(String serviceLine) {
        this.serviceLine = serviceLine;
    }
}
