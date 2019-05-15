package com.seasun.management.vo;

import com.seasun.management.dto.WorkGroupUserDto;

import java.util.Date;

public class OrdinateSalaryChangeAppVo extends WorkGroupUserDto {

    private Long id;
    private Integer year;
    private Integer quarter;
    private Integer score;
    private String grade;
    private String evaluateType;
    private String manager;
    private String workGroup;//模型中workGroupName
    private String performanceResult;//模型中没有
    private Integer performanceCount;
    private Integer oldSalary;
    private Integer increaseSalary;
    private String status;
    private Date lastSalaryChangeDate;
    private Integer lastSalaryChangeAmount;
    private Date lastGradeChangeDate;

    private Integer workAge;
    private Integer workAgeInKs;
    private String post;
    private String employeeNo;
    private Long parentGroup;

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

    public Integer getQuarter() {
        return quarter;
    }

    public void setQuarter(Integer quarter) {
        this.quarter = quarter;
    }


    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getEvaluateType() {
        return evaluateType;
    }

    public void setEvaluateType(String evaluateType) {
        this.evaluateType = evaluateType;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public String getWorkGroup() {
        return workGroup;
    }

    public void setWorkGroup(String workGroup) {
        this.workGroup = workGroup;
    }

    public String getPerformanceResult() {
        return performanceResult;
    }

    public void setPerformanceResult(String performanceResult) {
        this.performanceResult = performanceResult;
    }

    public Integer getOldSalary() {
        return oldSalary;
    }

    public void setOldSalary(Integer oldSalary) {
        this.oldSalary = oldSalary;
    }

    public Integer getIncreaseSalary() {
        return increaseSalary;
    }

    public void setIncreaseSalary(Integer increaseSalary) {
        this.increaseSalary = increaseSalary;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getPerformanceCount() {
        return performanceCount;
    }

    public void setPerformanceCount(Integer performanceCount) {
        this.performanceCount = performanceCount;
    }

    public Integer getLastSalaryChangeAmount() {
        return lastSalaryChangeAmount;
    }

    public void setLastSalaryChangeAmount(Integer lastSalaryChangeAmount) {
        this.lastSalaryChangeAmount = lastSalaryChangeAmount;
    }

    public Date getLastGradeChangeDate() {
        return lastGradeChangeDate;
    }

    public void setLastGradeChangeDate(Date lastGradeChangeDate) {
        this.lastGradeChangeDate = lastGradeChangeDate;
    }

    public Date getLastSalaryChangeDate() {
        return lastSalaryChangeDate;
    }

    public void setLastSalaryChangeDate(Date lastSalaryChangeDate) {
        this.lastSalaryChangeDate = lastSalaryChangeDate;
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

    public String getEmployeeNo() {
        return employeeNo;
    }

    public void setEmployeeNo(String employeeNo) {
        this.employeeNo = employeeNo;
    }

    public Long getParentGroup() {
        return parentGroup;
    }

    public void setParentGroup(Long parentGroup) {
        this.parentGroup = parentGroup;
    }

    @Override
    public String toString() {
        return "OrdinateSalaryChangeAppVo{" +
                "id=" + id +
                ", year=" + year +
                ", quarter=" + quarter +
                ", score=" + score +
                ", grade='" + grade + '\'' +
                ", evaluateType='" + evaluateType + '\'' +
                ", manager='" + manager + '\'' +
                ", workGroup='" + workGroup + '\'' +
                ", performanceResult='" + performanceResult + '\'' +
                ", performanceCount=" + performanceCount +
                ", oldSalary=" + oldSalary +
                ", increaseSalary=" + increaseSalary +
                ", status='" + status + '\'' +
                ", lastSalaryChangeDate=" + lastSalaryChangeDate +
                ", lastSalaryChangeAmount=" + lastSalaryChangeAmount +
                ", lastGradeChangeDate=" + lastGradeChangeDate +
                ", workAge=" + workAge +
                ", workAgeInKs=" + workAgeInKs +
                ", post='" + post + '\'' +
                '}';
    }
}
