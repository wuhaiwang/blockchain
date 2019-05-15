package com.seasun.management.dto;

import java.util.Date;

public class UserSalaryChangeDetailDto {
    private Long userId;
    private Integer year;
    private Integer quarter;
    private Long employeeNo;
    private Integer workAge;
    private Integer workAgeInKs;
    private String post;
    private Integer lastSalaryChangeAmount;
    private Date lastSalaryChangeDate;
    private Date lastGradeChangeDate;

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

    public Integer getQuarter() {
        return quarter;
    }

    public void setQuarter(Integer quarter) {
        this.quarter = quarter;
    }

    public Long getEmployeeNo() {
        return employeeNo;
    }

    public void setEmployeeNo(Long employeeNo) {
        this.employeeNo = employeeNo;
    }

    public Integer getWorkAge() {
        return workAge;
    }

    public void setWorkAge(Integer workAge) {
        this.workAge = workAge;
    }

    public Integer getWorkAgeInKs() {
        return workAgeInKs;
    }

    public void setWorkAgeInKs(Integer workAgeInKs) {
        this.workAgeInKs = workAgeInKs;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public Integer getLastSalaryChangeAmount() {
        return lastSalaryChangeAmount;
    }

    public void setLastSalaryChangeAmount(Integer lastSalaryChangeAmount) {
        this.lastSalaryChangeAmount = lastSalaryChangeAmount;
    }

    public Date getLastSalaryChangeDate() {
        return lastSalaryChangeDate;
    }

    public void setLastSalaryChangeDate(Date lastSalaryChangeDate) {
        this.lastSalaryChangeDate = lastSalaryChangeDate;
    }

    public Date getLastGradeChangeDate() {
        return lastGradeChangeDate;
    }

    public void setLastGradeChangeDate(Date lastGradeChangeDate) {
        this.lastGradeChangeDate = lastGradeChangeDate;
    }
}
