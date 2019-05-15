package com.seasun.management.model;

import java.util.Date;

public class PmPlan {
    private Long id;

    private Long projectId;

    private Integer year;

    private Integer version;

    private Integer status;

    private Date createTime;

    private Date updateTime;

    private Date projectEstimateDay;

    public Date getProjectEstimateDay() {
        return projectEstimateDay;
    }

    public void setProjectEstimateDay(Date projectEstimateDay) {
        this.projectEstimateDay = projectEstimateDay;
    }

    public interface Status {
        Integer INITIALIZED = 0;
        Integer SUBMITTED = 1;
        Integer PUBLISHED = 2;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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
}