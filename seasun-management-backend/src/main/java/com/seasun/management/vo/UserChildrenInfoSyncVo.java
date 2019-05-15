package com.seasun.management.vo;

import com.seasun.management.model.UserChildrenInfo;

public class UserChildrenInfoSyncVo extends BaseSyncVo {
    private UserChildrenInfo data;

    public UserChildrenInfo getData() {
        return data;
    }

    public void setData(UserChildrenInfo data) {
        this.data = data;
    }
}
