package com.seasun.management.dto;

import com.seasun.management.model.FnPlatShareConfig;

public class FnPlatShareConfigUserDTO extends FnPlatShareConfig {

    private Long employeeNo;
    private String userName;
    private String gender;
    private String workStatus;
    private String post;
    private String inDate;
    private String workGroupName;
    private Long workGroupId;

    public Long getWorkGroupId() {
        return workGroupId;
    }

    public void setWorkGroupId(Long workGroupId) {
        this.workGroupId = workGroupId;
    }

    public Long getEmployeeNo() {
        return employeeNo;
    }

    public void setEmployeeNo(Long employeeNo) {
        this.employeeNo = employeeNo;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public String getWorkGroupName() {
        return workGroupName;
    }

    public void setWorkGroupName(String workGroupName) {
        this.workGroupName = workGroupName;
    }

    public String getInDate() {
        return inDate;
    }

    public void setInDate(String inDate) {
        this.inDate = inDate;
    }

    @Override
    public String toString() {
        return "FnPlatShareConfigUserDTO{" +
                "employeeNo=" + employeeNo +
                ", userName='" + userName + '\'' +
                ", gender='" + gender + '\'' +
                ", workStatus='" + workStatus + '\'' +
                ", post='" + post + '\'' +
                ", inDate=" + inDate +
                ", workGroupName='" + workGroupName + '\'' +
                '}';
    }
}
