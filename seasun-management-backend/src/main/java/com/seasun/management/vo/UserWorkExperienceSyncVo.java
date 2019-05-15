package com.seasun.management.vo;

import com.seasun.management.model.UserWorkExperience;

public class UserWorkExperienceSyncVo extends BaseSyncVo {
    private UserWorkExperience data;

    public UserWorkExperience getData() {
        return data;
    }

    public void setData(UserWorkExperience data) {
        this.data = data;
    }
}
