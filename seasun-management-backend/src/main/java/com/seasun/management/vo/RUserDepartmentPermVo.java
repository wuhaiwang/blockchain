package com.seasun.management.vo;

import com.seasun.management.model.RUserDepartmentPerm;

public class RUserDepartmentPermVo extends RUserDepartmentPerm {
    private String departmentName;

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
}
