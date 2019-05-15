package com.seasun.management.vo;

import org.springframework.web.bind.annotation.RequestParam;

public class FnPlatLockList {
    private Long[] plats;
    private Integer year;
    private Integer month;
    private String type;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long[] getPlats() {
        return plats.clone();
    }

    public void setPlats(Long[] plats) {
        this.plats = plats.clone();
    }
}
