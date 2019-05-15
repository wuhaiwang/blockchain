package com.seasun.management.dto;

import com.seasun.management.model.PmAttachment;

public class PmAttachmentDateDto extends PmAttachment {

    private Integer year;

    private Integer month;

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
}
