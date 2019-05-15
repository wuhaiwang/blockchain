package com.seasun.management.dto;

import com.seasun.management.model.UserFeedback;

public class UserFeedbackDto extends UserFeedback {
    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}