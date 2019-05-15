package com.seasun.management.model;

import java.util.Date;

public class PmAttachment {
    private Long id;

    private Long pmFinanceReportId;

    private String name;

    private String url;

    private String size;

    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPmFinanceReportId() {
        return pmFinanceReportId;
    }

    public void setPmFinanceReportId(Long pmFinanceReportId) {
        this.pmFinanceReportId = pmFinanceReportId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size == null ? null : size.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}