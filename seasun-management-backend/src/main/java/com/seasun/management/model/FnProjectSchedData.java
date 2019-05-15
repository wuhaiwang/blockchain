package com.seasun.management.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class FnProjectSchedData {
    private Long id;

    private Long projectId;

    private Integer year;

    private Integer month;

    private Float totalCost;

    private Float totalBudget;

    private Float expectIncome;

    private Float humanNumber;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    //  the following are user defined...


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

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Float getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Float totalCost) {
        this.totalCost = totalCost;
    }

    public Float getTotalBudget() {
        return totalBudget;
    }

    public void setTotalBudget(Float totalBudget) {
        this.totalBudget = totalBudget;
    }

    public Float getExpectIncome() {
        return expectIncome;
    }

    public void setExpectIncome(Float expectIncome) {
        this.expectIncome = expectIncome;
    }

    public Float getHumanNumber() {
        return humanNumber;
    }

    public void setHumanNumber(Float humanNumber) {
        this.humanNumber = humanNumber;
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