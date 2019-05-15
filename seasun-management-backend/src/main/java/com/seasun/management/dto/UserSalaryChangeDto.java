package com.seasun.management.dto;

import com.seasun.management.model.UserSalaryChange;

public class UserSalaryChangeDto extends UserSalaryChange {

    private String name;
    private String manager;
    private String workGroup;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public String getWorkGroup() {
        return workGroup;
    }

    public void setWorkGroup(String workGroup) {
        this.workGroup = workGroup;
    }

}
