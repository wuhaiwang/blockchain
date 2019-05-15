package com.seasun.management.dto;

import com.seasun.management.model.FnShareData;

public class FnShareDataDto extends FnShareData {

    private Float fixedNumber;

    private String platName;

    private String projectName;

    public Float getFixedNumber() {
        return fixedNumber;
    }

    public void setFixedNumber(Float fixedNumber) {
        this.fixedNumber = fixedNumber;
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
