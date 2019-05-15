package com.seasun.management.model;

import java.math.BigDecimal;
import java.util.Date;

public class FnWeekShareWorkdayStatus {
    private Long id;

    private Integer year;

    private Integer week;

    private BigDecimal workday;

    private Boolean status;

    private Long operatorId;

    private Date createTime;

    private Date updateTime;

    private Integer month;

    private Integer day;

    private Integer endMonth;

    private Integer endDay;

    public interface StatusVo {
        String locked = "已锁定";
        String unstart = "未开启";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getWeek() {
        return week;
    }

    public void setWeek(Integer week) {
        this.week = week;
    }

    public BigDecimal getWorkday() {
        return workday;
    }

    public void setWorkday(BigDecimal workday) {
        this.workday = workday;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Integer getEndMonth() {
        return endMonth;
    }

    public void setEndMonth(Integer endMonth) {
        this.endMonth = endMonth;
    }

    public Integer getEndDay() {
        return endDay;
    }

    public void setEndDay(Integer endDay) {
        this.endDay = endDay;
    }
}