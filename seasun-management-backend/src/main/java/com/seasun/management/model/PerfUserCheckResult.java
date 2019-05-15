package com.seasun.management.model;

import java.util.Date;

public class PerfUserCheckResult {
    private Long id;

    private String type;

    private Long userId;

    private String remark;

    private Long createBy;

    private Date createTime;


    public interface type {
        String notInOrgWorkGroup = "not_in_org_work_group";
        String notInPerfWorkGroup = "not_in_perf_work_group";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Long getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Long createBy) {
        this.createBy = createBy;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}