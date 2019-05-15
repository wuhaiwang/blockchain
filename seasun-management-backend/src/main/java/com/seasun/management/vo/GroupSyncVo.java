package com.seasun.management.vo;

import com.seasun.management.model.WorkGroup;

public class GroupSyncVo extends BaseSyncVo {
    private WorkGroup data;

    public WorkGroup getData() {
        return data;
    }

    public void setData(WorkGroup data) {
        this.data = data;
    }
}
