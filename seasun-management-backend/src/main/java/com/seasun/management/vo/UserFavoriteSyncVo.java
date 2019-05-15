package com.seasun.management.vo;

import com.seasun.management.model.UserFavorite;

public class UserFavoriteSyncVo extends BaseSyncVo {
    private UserFavorite data;

    public UserFavorite getData() {
        return data;
    }

    public void setData(UserFavorite data) {
        this.data = data;
    }
}
