package com.seasun.management.vo;

import com.seasun.management.model.UserBankCard;

public class UserBankCardSyncVo extends BaseSyncVo {
    private UserBankCard data;

    public UserBankCard getData() {
        return data;
    }

    public void setData(UserBankCard data) {
        this.data = data;
    }
}
