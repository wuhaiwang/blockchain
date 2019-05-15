package com.seasun.management.dto;

import com.seasun.management.model.UserDormitoryReservation;

public class UserDormitoryReservationDto extends UserDormitoryReservation {

    private String projectName;
    private String companyName;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
}
