package com.seasun.management.model;

import java.util.Date;

public class UserGradeChange {
    private Long id;

    private Long userId;

    private Integer year;

    private Integer month;

    private String oldGrade;

    private String oldEvaluateType;

    private String newGrade;

    private String newEvaluateType;

    private String status;

    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public String getOldGrade() {
        return oldGrade;
    }

    public void setOldGrade(String oldGrade) {
        this.oldGrade = oldGrade;
    }

    public String getOldEvaluateType() {
        return oldEvaluateType;
    }

    public void setOldEvaluateType(String oldEvaluateType) {
        this.oldEvaluateType = oldEvaluateType == null ? null : oldEvaluateType.trim();
    }

    public String getNewGrade() {
        return newGrade;
    }

    public void setNewGrade(String newGrade) {
        this.newGrade = newGrade == null ? null : newGrade.trim();
    }

    public String getNewEvaluateType() {
        return newEvaluateType;
    }

    public void setNewEvaluateType(String newEvaluateType) {
        this.newEvaluateType = newEvaluateType == null ? null : newEvaluateType.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}