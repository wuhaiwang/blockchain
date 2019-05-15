package com.seasun.management.vo;

import com.seasun.management.model.UserCertification;

public class UserCertificationSyncVo extends BaseSyncVo {
    private UserCertification data;

    public UserCertification getData() {
        return data;
    }

    public void setData(UserCertification data) {
        this.data = data;
    }
}
