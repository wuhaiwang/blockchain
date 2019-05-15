package com.seasun.management.vo;

public class WeekShareConfigVo extends SimpleSharePlatWeekVo {

    private String projectName;
    private String projectUsedNames;
    private String city;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectUsedNames() {
        return projectUsedNames;
    }

    public void setProjectUsedNames(String projectUsedNames) {
        this.projectUsedNames = projectUsedNames;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

}
