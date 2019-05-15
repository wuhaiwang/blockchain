package com.seasun.management.dto;

import com.seasun.management.model.PmPlan;

public class PmPlanDto extends PmPlan {

    private String projectName;

    private String projectStatus;

    public String getProjectStatus() {
        return projectStatus;
    }

    public void setProjectStatus(String projectStatus) {
        this.projectStatus = projectStatus;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}
