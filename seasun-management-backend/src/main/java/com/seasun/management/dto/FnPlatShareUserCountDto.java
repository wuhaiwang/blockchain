package com.seasun.management.dto;

import java.util.HashMap;

/**
 * Created by hedahai on 2017/8/7.
 */
public class FnPlatShareUserCountDto {
    private Integer year;
    private Integer month;
    private Integer totalNumber;
    private Integer shareNumber;
    private HashMap<Long, Integer> userCountOfProjectMap;

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getTotalNumber() {
        return totalNumber;
    }

    public void setTotalNumber(Integer totalNumber) {
        this.totalNumber = totalNumber;
    }

    public Integer getShareNumber() {
        return shareNumber;
    }

    public void setShareNumber(Integer shareNumber) {
        this.shareNumber = shareNumber;
    }

    public HashMap<Long, Integer> getUserCountOfProjectMap() {
        return userCountOfProjectMap;
    }

    public void setUserCountOfProjectMap(HashMap<Long, Integer> userCountOfProjectMap) {
        this.userCountOfProjectMap = userCountOfProjectMap;
    }
}
