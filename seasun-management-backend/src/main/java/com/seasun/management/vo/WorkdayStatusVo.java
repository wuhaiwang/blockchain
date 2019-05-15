package com.seasun.management.vo;

import com.seasun.management.dto.BaseYearWeekDto;

public class WorkdayStatusVo extends BaseYearWeekDto {
    private Long id;
    private Float workday;
    private boolean status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Float getWorkday() {
        return workday;
    }

    public void setWorkday(Float workday) {
        this.workday = workday;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
