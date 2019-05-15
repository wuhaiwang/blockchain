package com.seasun.management.vo;

import com.seasun.management.model.Floor;

public class FloorSyncVo extends BaseSyncVo {
    private Floor data;

    public Floor getData() {
        return data;
    }

    public void setData(Floor data) {
        this.data = data;
    }
}
