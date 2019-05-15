package com.seasun.management.dto;

import java.util.List;

public class PermissionProjectDto {

    private boolean allPerm;

    private List<Long> permProjectIds;

    public boolean isAllPerm() {
        return allPerm;
    }

    public void setAllPerm(boolean allPerm) {
        this.allPerm = allPerm;
    }

    public List<Long> getPermProjectIds() {
        return permProjectIds;
    }

    public void setPermProjectIds(List<Long> permProjectIds) {
        this.permProjectIds = permProjectIds;
    }
}
