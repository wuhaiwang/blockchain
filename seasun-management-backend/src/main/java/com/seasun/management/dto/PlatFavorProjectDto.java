package com.seasun.management.dto;

import java.util.List;

public class PlatFavorProjectDto {

    private Long platId;
    private List<Long> projectIds;

    public Long getPlatId() {
        return platId;
    }

    public void setPlatId(Long platId) {
        this.platId = platId;
    }

    public List<Long> getProjectIds() {
        return projectIds;
    }

    public void setProjectIds(List<Long> projectIds) {
        this.projectIds = projectIds;
    }
}
