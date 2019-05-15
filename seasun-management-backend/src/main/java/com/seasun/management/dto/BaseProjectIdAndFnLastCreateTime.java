package com.seasun.management.dto;

import java.util.Date;

public class BaseProjectIdAndFnLastCreateTime {

    private Long projectId;

    private Date fnLastCreateTime;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Date getFnLastCreateTime() {
        return fnLastCreateTime;
    }

    public void setFnLastCreateTime(Date fnLastCreateTime) {
        this.fnLastCreateTime = fnLastCreateTime;
    }
}
