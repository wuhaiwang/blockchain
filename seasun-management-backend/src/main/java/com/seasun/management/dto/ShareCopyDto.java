package com.seasun.management.dto;

public class ShareCopyDto extends  FnPlatWeekShareConfigDto{
    private String userType;

    private Integer lastWeek;

    public Integer getLastWeek() {
        return lastWeek;
    }

    public void setLastWeek(Integer lastWeek) {
        this.lastWeek = lastWeek;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
