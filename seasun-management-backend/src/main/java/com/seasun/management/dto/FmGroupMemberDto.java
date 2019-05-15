package com.seasun.management.dto;

import com.seasun.management.vo.PerformanceFMGroupsVo;

public class FmGroupMemberDto extends PerformanceFMGroupsVo.Group.Member {
    private Long platId;
    private Long projectId;

    public Long getPlatId() {
        return platId;
    }

    public void setPlatId(Long platId) {
        this.platId = platId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
}
