package com.seasun.management.model;

import com.seasun.management.dto.MenuPermDto;

public class RMenuPerformanceWorkGroupPerm extends MenuPermDto {

    private Long performanceWorkGroupRoleId;

    public Long getPerformanceWorkGroupRoleId() {
        return performanceWorkGroupRoleId;
    }

    public void setPerformanceWorkGroupRoleId(Long performanceWorkGroupRoleId) {
        this.performanceWorkGroupRoleId = performanceWorkGroupRoleId;
    }

}