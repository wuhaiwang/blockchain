package com.seasun.management.vo;

import com.seasun.management.dto.SimpleSharePlatWeekDto;

import java.math.BigDecimal;

public class SimpleSharePlatWeekVo extends SimpleSharePlatWeekDto {
    private Long platId;
    private Integer year;
    private Integer week;
    private BigDecimal sharePro;
    private String createUserName;

    public Long getPlatId() {
        return platId;
    }

    public void setPlatId(Long platId) {
        this.platId = platId;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
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

    public BigDecimal getSharePro() {
        return sharePro;
    }

    public void setSharePro(BigDecimal sharePro) {
        this.sharePro = sharePro;
    }
}
