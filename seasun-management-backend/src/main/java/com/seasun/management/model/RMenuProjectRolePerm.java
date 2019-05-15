package com.seasun.management.model;

import com.seasun.management.dto.MenuPermDto;

public class RMenuProjectRolePerm extends MenuPermDto {

    private Long projectRoleId;

    private String remark;

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getProjectRoleId() {
        return projectRoleId;
    }

    public void setProjectRoleId(Long projectRoleId) {
        this.projectRoleId = projectRoleId;
    }
}