package com.seasun.management.vo;

import java.util.List;

public class UserCountCheckVo {

    private int cityWorkGroupUserCount;

    private int cityHrUserCount;

    private List<CheckUser> notInHrList;

    private List<CheckUser> notInWorkgroupList;

    public int getCityWorkGroupUserCount() {
        return cityWorkGroupUserCount;
    }

    public void setCityWorkGroupUserCount(int cityWorkGroupUserCount) {
        this.cityWorkGroupUserCount = cityWorkGroupUserCount;
    }

    public int getCityHrUserCount() {
        return cityHrUserCount;
    }

    public void setCityHrUserCount(int cityHrUserCount) {
        this.cityHrUserCount = cityHrUserCount;
    }

    public List<CheckUser> getNotInHrList() {
        return notInHrList;
    }

    public void setNotInHrList(List<CheckUser> notInHrList) {
        this.notInHrList = notInHrList;
    }

    public List<CheckUser> getNotInWorkgroupList() {
        return notInWorkgroupList;
    }

    public void setNotInWorkgroupList(List<CheckUser> notInWorkgroupList) {
        this.notInWorkgroupList = notInWorkgroupList;
    }

    public static class CheckUser {
        String loginId;

        public String getLoginId() {
            return loginId;
        }

        public void setLoginId(String loginId) {
            this.loginId = loginId;
        }
    }
}
