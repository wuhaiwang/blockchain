package com.seasun.management.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class UserWorkInfoDto {

    private String employeeNo;
    private String workStatus;
    private String post;
    private String belongToCenter;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private String firstJoinDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date inDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date joinPostDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date becomeValidDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date leaveDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date intershipEndDate;
    private String subcompany;
    private String email;
    private Integer workAgeInKs; //司龄
    private Integer workAge; //工作年限
    private String major;
    private String costCenterCode;
    private String orderCenterCode;
    private String workPlace;
    private String insurancePlace;
    private String joinType;
    private String encouragement;

    public String getEmployeeNo() {
        return employeeNo;
    }

    public void setEmployeeNo(String employeeNo) {
        this.employeeNo = employeeNo;
    }

    public String getWorkStatus() {
        return workStatus;
    }

    public void setWorkStatus(String workStatus) {
        this.workStatus = workStatus;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getBelongToCenter() {
        return belongToCenter;
    }

    public void setBelongToCenter(String belongToCenter) {
        this.belongToCenter = belongToCenter;
    }

    public String getFirstJoinDate() {
        return firstJoinDate;
    }

    public void setFirstJoinDate(String firstJoinDate) {
        this.firstJoinDate = firstJoinDate;
    }

    public Date getInDate() {
        return inDate;
    }

    public void setInDate(Date inDate) {
        this.inDate = inDate;
    }

    public Date getJoinPostDate() {
        return joinPostDate;
    }

    public void setJoinPostDate(Date joinPostDate) {
        this.joinPostDate = joinPostDate;
    }

    public Date getBecomeValidDate() {
        return becomeValidDate;
    }

    public void setBecomeValidDate(Date becomeValidDate) {
        this.becomeValidDate = becomeValidDate;
    }

    public Date getLeaveDate() {
        return leaveDate;
    }

    public void setLeaveDate(Date leaveDate) {
        this.leaveDate = leaveDate;
    }

    public Date getIntershipEndDate() {
        return intershipEndDate;
    }

    public void setIntershipEndDate(Date intershipEndDate) {
        this.intershipEndDate = intershipEndDate;
    }

    public String getSubcompany() {
        return subcompany;
    }

    public void setSubcompany(String subcompany) {
        this.subcompany = subcompany;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getCostCenterCode() {
        return costCenterCode;
    }

    public void setCostCenterCode(String costCenterCode) {
        this.costCenterCode = costCenterCode;
    }

    public String getOrderCenterCode() {
        return orderCenterCode;
    }

    public void setOrderCenterCode(String orderCenterCode) {
        this.orderCenterCode = orderCenterCode;
    }

    public String getWorkPlace() {
        return workPlace;
    }

    public void setWorkPlace(String workPlace) {
        this.workPlace = workPlace;
    }

    public String getInsurancePlace() {
        return insurancePlace;
    }

    public void setInsurancePlace(String insurancePlace) {
        this.insurancePlace = insurancePlace;
    }

    public String getJoinType() {
        return joinType;
    }

    public void setJoinType(String joinType) {
        this.joinType = joinType;
    }

    public String getEncouragement() {
        return encouragement;
    }

    public void setEncouragement(String encouragement) {
        this.encouragement = encouragement;
    }

    public Integer getWorkAgeInKs() {
        return workAgeInKs;
    }

    public void setWorkAgeInKs(Integer workAgeInKs) {
        this.workAgeInKs = workAgeInKs;
    }

    public Integer getWorkAge() {
        return workAge;
    }

    public void setWorkAge(Integer workAge) {
        this.workAge = workAge;
    }
}
