package com.seasun.management.dto;

import com.seasun.management.model.WorkGroup;

public class WorkGroupHrDto extends WorkGroup {

    private Long hrId;

    private Long projectId;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getHrId() {
        return hrId;
    }

    public void setHrId(Long hrId) {
        this.hrId = hrId;
    }
}

