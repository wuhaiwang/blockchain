package com.seasun.management.dto;

import com.seasun.management.model.OrderCenter;

public class OrderCenterWithParentHrIdDto extends OrderCenter {
    private Long parentHrId;

    public Long getParentHrId() {
        return parentHrId;
    }

    public void setParentHrId(Long parentHrId) {
        this.parentHrId = parentHrId;
    }
}
