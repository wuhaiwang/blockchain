package com.seasun.management.dto;

public class PerformanceUserDto extends WorkGroupUserDto {
    private String post;

    private Integer workAge;

    private String workStatus;

    private Integer workAgeInKs;

    private String workGroupName;

    private Long employeeNo;

    private Long parentWorkGroupId;

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
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

    public String getWorkGroupName() {
        return workGroupName;
    }

    public void setWorkGroupName(String workGroupName) {
        this.workGroupName = workGroupName;
    }

    public Long getEmployeeNo() {
        return employeeNo;
    }

    public void setEmployeeNo(Long employeeNo) {
        this.employeeNo = employeeNo;
    }

    public Long getParentWorkGroupId() {
        return parentWorkGroupId;
    }

    public void setParentWorkGroupId(Long parentWorkGroupId) {
        this.parentWorkGroupId = parentWorkGroupId;
    }

    public String getWorkStatus() {
        return workStatus;
    }

    public void setWorkStatus(String workStatus) {
        this.workStatus = workStatus;
    }
}
