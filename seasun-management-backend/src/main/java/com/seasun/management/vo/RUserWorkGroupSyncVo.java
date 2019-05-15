package com.seasun.management.vo;

import com.seasun.management.model.RUserWorkGroupPerm;


public class RUserWorkGroupSyncVo extends BaseSyncVo {
    private RUserWorkGroupPerm data;

    public RUserWorkGroupPerm getData() {
        return data;
    }

    public void setData(RUserWorkGroupPerm data) {
        this.data = data;
    }
}
