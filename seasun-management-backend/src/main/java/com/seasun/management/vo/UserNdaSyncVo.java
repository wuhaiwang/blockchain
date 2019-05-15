package com.seasun.management.vo;

import com.seasun.management.model.UserNda;

public class UserNdaSyncVo extends BaseSyncVo {
    private UserNda data;

    public UserNda getData() {
        return data;
    }

    public void setData(UserNda data) {
        this.data = data;
    }
}
