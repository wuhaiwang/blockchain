package com.seasun.management.model;

public class CfgPerfHideManagerComment {
    private Long id;

    private Long perfWorkGroupId;

    private Integer year;

    private Integer month;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPerfWorkGroupId() {
        return perfWorkGroupId;
    }

    public void setPerfWorkGroupId(Long perfWorkGroupId) {
        this.perfWorkGroupId = perfWorkGroupId;
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
}