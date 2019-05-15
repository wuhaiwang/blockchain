package com.seasun.management.vo;

import com.seasun.management.model.UserContactGroup;

import java.util.List;

public class UserInfoAppVo {

    private Long userId;

    private String name;

    private Boolean gender;

    private String employeeNo;

    private String loginId;

    private String phone;

    private String email;

    private Long workGroupId;

    private String workGroup;

    private String post;

    private String photo;

    private List<UserContactGroup> userContactGroups;

    private Boolean subMemberFlag;

    private Long perfWorkGroupId;

    private String seatNo;

    private Boolean psyStartFlag;

    public Boolean getPsyStartFlag() {
        return psyStartFlag;
    }

    public void setPsyStartFlag(Boolean psyStartFlag) {
        this.psyStartFlag = psyStartFlag;
    }

    public String getSeatNo() {
        return seatNo;
    }

    public void setSeatNo(String seatNo) {
        this.seatNo = seatNo;
    }

    public Long getPerfWorkGroupId() {
        return perfWorkGroupId;
    }

    public void setPerfWorkGroupId(Long perfWorkGroupId) {
        this.perfWorkGroupId = perfWorkGroupId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Boolean getSubMemberFlag() {
        return subMemberFlag;
    }

    public void setSubMemberFlag(Boolean subMemberFlag) {
        this.subMemberFlag = subMemberFlag;
    }

    public List<UserContactGroup> getUserContactGroups() {
        return userContactGroups;
    }

    public void setUserContactGroups(List<UserContactGroup> userContactGroups) {
        this.userContactGroups = userContactGroups;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getGender() {
        return gender;
    }

    public void setGender(Boolean gender) {
        this.gender = gender;
    }

    public String getEmployeeNo() {
        return employeeNo;
    }

    public void setEmployeeNo(String employeeNo) {
        this.employeeNo = employeeNo;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getWorkGroupId() {
        return workGroupId;
    }

    public void setWorkGroupId(Long workGroupId) {
        this.workGroupId = workGroupId;
    }

    public String getWorkGroup() {
        return workGroup;
    }

    public void setWorkGroup(String workGroup) {
        this.workGroup = workGroup;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
