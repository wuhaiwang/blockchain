package com.seasun.management.model;

import java.util.Date;

public class PerfWorkGroupStatus {
    private Long id;

    private Long perfWorkGroupId;

    private String status;

    private Integer year;

    private Integer month;

    private Date createTime;

    private Date updateTime;

    private Long operateId;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
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

    public Long getOperateId() {
        return operateId;
    }

    public void setOperateId(Long operateId) {
        this.operateId = operateId;
    }
}