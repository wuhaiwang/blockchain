package com.seasun.management.dto;

import com.seasun.management.model.FnPlatWeekShareConfig;

public class FnPlatWeekShareConfigDto extends FnPlatWeekShareConfig {

    private Integer endMonth;
    private Integer startDay;
    private Integer endDay;

    public Integer getEndMonth() {
        return endMonth;
    }

    public void setEndMonth(Integer endMonth) {
        this.endMonth = endMonth;
    }

    public Integer getStartDay() {
        return startDay;
    }

    public void setStartDay(Integer startDay) {
        this.startDay = startDay;
    }

    public Integer getEndDay() {
        return endDay;
    }

    public void setEndDay(Integer endDay) {
        this.endDay = endDay;
    }
}
