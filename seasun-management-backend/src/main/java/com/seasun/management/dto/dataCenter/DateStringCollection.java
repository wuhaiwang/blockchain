package com.seasun.management.dto.dataCenter;

public class DateStringCollection {

    //这个月
    private String currentDate;
    private String yesterdayDate;
    private String beforeYesterdayDate;
    private String tomorrowDate;
    private String beginOfMonthDate;

    //上个月
    private String lastMonthDate;
    private String lastYesterdayDate;
    private String lastBeforeYesterdayDate;
    private String lastBeginOfMonthDate;


    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public String getBeginOfMonthDate() {
        return beginOfMonthDate;
    }

    public void setBeginOfMonthDate(String beginOfMonthDate) {
        this.beginOfMonthDate = beginOfMonthDate;
    }

    public String getLastMonthDate() {
        return lastMonthDate;
    }

    public void setLastMonthDate(String lastMonthDate) {
        this.lastMonthDate = lastMonthDate;
    }

    public String getLastBeginOfMonthDate() {
        return lastBeginOfMonthDate;
    }

    public void setLastBeginOfMonthDate(String lastBeginOfMonthDate) {
        this.lastBeginOfMonthDate = lastBeginOfMonthDate;
    }

    public String getYesterdayDate() {
        return yesterdayDate;
    }

    public void setYesterdayDate(String yesterdayDate) {
        this.yesterdayDate = yesterdayDate;
    }

    public String getBeforeYesterdayDate() {
        return beforeYesterdayDate;
    }

    public void setBeforeYesterdayDate(String beforeYesterdayDate) {
        this.beforeYesterdayDate = beforeYesterdayDate;
    }

    public String getTomorrowDate() {
        return tomorrowDate;
    }

    public void setTomorrowDate(String tomorrowDate) {
        this.tomorrowDate = tomorrowDate;
    }

    public String getLastYesterdayDate() {
        return lastYesterdayDate;
    }

    public void setLastYesterdayDate(String lastYesterdayDate) {
        this.lastYesterdayDate = lastYesterdayDate;
    }

    public String getLastBeforeYesterdayDate() {
        return lastBeforeYesterdayDate;
    }

    public void setLastBeforeYesterdayDate(String lastBeforeYesterdayDate) {
        this.lastBeforeYesterdayDate = lastBeforeYesterdayDate;
    }
}