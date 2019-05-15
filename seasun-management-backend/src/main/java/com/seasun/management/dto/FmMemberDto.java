package com.seasun.management.dto;


import com.seasun.management.model.FmMember;

public class FmMemberDto extends FmMember {
    private String projectName;

    private String platName;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getPlatName() {
        return platName;
    }

    public void setPlatName(String platName) {
        this.platName = platName;
    }
}
