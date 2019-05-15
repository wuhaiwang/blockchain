package com.seasun.management.dto;

public class PerformanceDto {
    private Long id;
    private Long userId;
    private Integer year;
    private Integer month;
    private String name;
    private String workGroup;
    private String finalPerformance;
    private String status;
    private String manager;
    private String selfPerformance;

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

    public String getWorkGroup() {
        return workGroup;
    }

    public void setWorkGroup(String workGroup) {
        this.workGroup = workGroup;
    }

    public String getFinalPerformance() {
        return finalPerformance;
    }

    public void setFinalPerformance(String finalPerformance) {
        this.finalPerformance = finalPerformance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public String getSelfPerformance() {
        return selfPerformance;
    }

    public void setSelfPerformance(String selfPerformance) {
        this.selfPerformance = selfPerformance;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
