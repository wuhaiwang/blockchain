package com.seasun.management.vo;

import java.util.List;

public class FnPlatShareConfigLockVo {
    private Boolean lockFlag;

    private List<WeekConfirmInfo> weekConfirmInfo;

    private List<FnPlatShareConfigVo> platProcessList;

    public List<WeekConfirmInfo> getWeekConfirmInfo() {
        return weekConfirmInfo;
    }

    public void setWeekConfirmInfo(List<WeekConfirmInfo> weekConfirmInfo) {
        this.weekConfirmInfo = weekConfirmInfo;
    }

    public Boolean getLockFlag() {
        return lockFlag;
    }

    public void setLockFlag(Boolean lockFlag) {
        this.lockFlag = lockFlag;
    }

    public List<FnPlatShareConfigVo> getPlatProcessList() {
        return platProcessList;
    }

    public void setPlatProcessList(List<FnPlatShareConfigVo> platProcessList) {
        this.platProcessList = platProcessList;
    }


    public static class WeekConfirmInfo {
        private int year;
        private int week;

        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }

        public int getWeek() {
            return week;
        }

        public void setWeek(int week) {
            this.week = week;
        }
    }
}
