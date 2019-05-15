package com.seasun.management.dto;

import com.seasun.management.model.PerformanceWorkGroup;

public class PerformanceWorkGroupDto extends PerformanceWorkGroup {
    private Long projectId;

    private String managerName;

    private String fullPathName;

    private String managerLoginId;

    public Long getProjectId() {
        return projectId;
    }

    public String getManagerLoginId() {
        return managerLoginId;
    }

    public void setManagerLoginId(String managerLoginId) {
        this.managerLoginId = managerLoginId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getFullPathName() {
        return fullPathName;
    }

    public void setFullPathName(String fullPathName) {
        this.fullPathName = fullPathName;
    }
}
