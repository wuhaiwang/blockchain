package com.seasun.management.vo;

import com.seasun.management.model.RUserProjectPerm;


public class RUserProjectPermSyncVo extends BaseSyncVo {
    private RUserProjectPerm data;

    public RUserProjectPerm getData() {
        return data;
    }

    public void setData(RUserProjectPerm data) {
        this.data = data;
    }
}
