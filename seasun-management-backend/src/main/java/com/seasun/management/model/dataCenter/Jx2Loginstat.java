package com.seasun.management.model.dataCenter;

import java.util.Date;

public class Jx2Loginstat {
    private Date statDate;

    private Long loginTotalAccNum;

    private Long firstLoginAccNum;

    private Long payPlayerNum;

    private Long weekPlayerNum;

    private Long monthPlayerNum;

    private Long pointPlayerNum;

    private Long onlinePayPlayerMax;

    private String type;

    private Long monActiveAccNum;

    public Date getStatDate() {
        return statDate;
    }

    public void setStatDate(Date statDate) {
        this.statDate = statDate;
    }

    public Long getLoginTotalAccNum() {
        return loginTotalAccNum;
    }

    public void setLoginTotalAccNum(Long loginTotalAccNum) {
        this.loginTotalAccNum = loginTotalAccNum;
    }

    public Long getFirstLoginAccNum() {
        return firstLoginAccNum;
    }

    public void setFirstLoginAccNum(Long firstLoginAccNum) {
        this.firstLoginAccNum = firstLoginAccNum;
    }

    public Long getPayPlayerNum() {
        return payPlayerNum;
    }

    public void setPayPlayerNum(Long payPlayerNum) {
        this.payPlayerNum = payPlayerNum;
    }

    public Long getWeekPlayerNum() {
        return weekPlayerNum;
    }

    public void setWeekPlayerNum(Long weekPlayerNum) {
        this.weekPlayerNum = weekPlayerNum;
    }

    public Long getMonthPlayerNum() {
        return monthPlayerNum;
    }

    public void setMonthPlayerNum(Long monthPlayerNum) {
        this.monthPlayerNum = monthPlayerNum;
    }

    public Long getPointPlayerNum() {
        return pointPlayerNum;
    }

    public void setPointPlayerNum(Long pointPlayerNum) {
        this.pointPlayerNum = pointPlayerNum;
    }

    public Long getOnlinePayPlayerMax() {
        return onlinePayPlayerMax;
    }

    public void setOnlinePayPlayerMax(Long onlinePayPlayerMax) {
        this.onlinePayPlayerMax = onlinePayPlayerMax;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public Long getMonActiveAccNum() {
        return monActiveAccNum;
    }

    public void setMonActiveAccNum(Long monActiveAccNum) {
        this.monActiveAccNum = monActiveAccNum;
    }
}