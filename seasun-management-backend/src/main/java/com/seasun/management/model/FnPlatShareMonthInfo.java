package com.seasun.management.model;

import java.math.BigDecimal;
import java.util.Date;

public class FnPlatShareMonthInfo {
    private Long id;

    private Integer year;

    private Integer month;

    private Long platId;

    private BigDecimal workDay;

    private String workdayPeriod;

    private Date createTime;

    public String getWorkdayPeriod() {
        return workdayPeriod;
    }

    public void setWorkdayPeriod(String workdayPeriod) {
        this.workdayPeriod = workdayPeriod;
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

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Long getPlatId() {
        return platId;
    }

    public void setPlatId(Long platId) {
        this.platId = platId;
    }

    public BigDecimal getWorkDay() {
        return workDay;
    }

    public void setWorkDay(BigDecimal workDay) {
        this.workDay = workDay;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}