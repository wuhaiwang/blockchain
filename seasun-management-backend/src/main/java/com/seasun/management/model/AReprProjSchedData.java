package com.seasun.management.model;

import java.util.Date;

public class AReprProjSchedData {
    private Long id;

    private String projectName;

    private String projectId;

    private Integer month;

    private Integer year;

    private Float totalCost;

    private Float restBudget;

    private Date createTime;

    private Date updateTime;

    private Float anticipatedRevenue;

    private Float hrNumbers;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName == null ? null : projectName.trim();
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId == null ? null : projectId.trim();
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Float getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Float totalCost) {
        this.totalCost = totalCost;
    }

    public Float getRestBudget() {
        return restBudget;
    }

    public void setRestBudget(Float restBudget) {
        this.restBudget = restBudget;
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

    public Float getAnticipatedRevenue() {
        return anticipatedRevenue;
    }

    public void setAnticipatedRevenue(Float anticipatedRevenue) {
        this.anticipatedRevenue = anticipatedRevenue;
    }

    public Float getHrNumbers() {
        return hrNumbers;
    }

    public void setHrNumbers(Float hrNumbers) {
        this.hrNumbers = hrNumbers;
    }
}