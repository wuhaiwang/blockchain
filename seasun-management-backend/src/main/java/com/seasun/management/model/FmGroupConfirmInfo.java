package com.seasun.management.model;

public class FmGroupConfirmInfo {
    public interface Status {
        String unsubmitted = "未提交";
        String unconfirmed = "未确认";
        String confirmed = "已确认";
    }

    private Long id;

    private Integer year;

    private Integer month;

    private Long performanceWorkGroupId;

    private Long platId;

    private Long projectId;

    private Long platConfirmerId;

    private Long projectConfirmerId;

    private String status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getPerformanceWorkGroupId() {
        return performanceWorkGroupId;
    }

    public void setPerformanceWorkGroupId(Long performanceWorkGroupId) {
        this.performanceWorkGroupId = performanceWorkGroupId;
    }

    public Long getPlatId() {
        return platId;
    }

    public void setPlatId(Long platId) {
        this.platId = platId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getPlatConfirmerId() {
        return platConfirmerId;
    }

    public void setPlatConfirmerId(Long platConfirmerId) {
        this.platConfirmerId = platConfirmerId;
    }

    public Long getProjectConfirmerId() {
        return projectConfirmerId;
    }

    public void setProjectConfirmerId(Long projectConfirmerId) {
        this.projectConfirmerId = projectConfirmerId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }
}