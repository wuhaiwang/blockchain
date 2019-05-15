package com.seasun.management.vo;

import com.seasun.management.model.School;

public class SchoolSyncVo extends BaseSyncVo {
    private School data;

    public School getData() {
        return data;
    }

    public void setData(School data) {
        this.data = data;
    }
}
