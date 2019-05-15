package com.seasun.management.dto;

import com.seasun.management.model.FnPlatShareMember;

public class FnPlatShareMemberDto extends FnPlatShareMember {

    private String platName;

    public String getPlatName() {
        return platName;
    }

    public void setPlatName(String platName) {
        this.platName = platName;
    }
}
