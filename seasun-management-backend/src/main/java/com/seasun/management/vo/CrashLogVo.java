package com.seasun.management.vo;

import com.seasun.management.model.CrashLog;

public class CrashLogVo extends CrashLog {
    private String loginId;

    private String userName;

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
