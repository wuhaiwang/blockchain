package com.seasun.management.model;

import java.util.Date;

public class FnProjectStatData {
    private Long id;

    private Long fnStatId;

    private Long projectId;

    private Integer year;

    private Integer month;

    private Float value;

    private Date createTime;

    private Date updateTime;

    //  the following are user defined...

    private Boolean detailFlag;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFnStatId() {
        return fnStatId;
    }

    public void setFnStatId(Long fnStatId) {
        this.fnStatId = fnStatId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
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

    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
        this.value = value;
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

    public Boolean getDetailFlag() {
        return detailFlag;
    }

    public void setDetailFlag(Boolean detailFlag) {
        this.detailFlag = detailFlag;
    }
}