package com.seasun.management.vo;

import com.seasun.management.model.RUserWorkGroupPerm;

public class RUserWorkGroupPermVo extends RUserWorkGroupPerm {

    private String loginId;

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }
}
