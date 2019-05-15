package com.seasun.management.vo;

import com.seasun.management.model.IdNameBaseObject;

public class UserSelectVo extends IdNameBaseObject {

    private Long userId;

    private String loginId;

    private String userPhoto;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }
}
