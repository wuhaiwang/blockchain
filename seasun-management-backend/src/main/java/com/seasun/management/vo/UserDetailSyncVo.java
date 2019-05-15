package com.seasun.management.vo;

import com.seasun.management.model.UserDetail;

public class UserDetailSyncVo extends BaseSyncVo {
    private UserDetailInfo data;

    public UserDetailInfo getData() {
        return data;
    }

    public void setData(UserDetailInfo data) {
        this.data = data;
    }

    public static class UserDetailInfo extends UserDetail {
    }
}
