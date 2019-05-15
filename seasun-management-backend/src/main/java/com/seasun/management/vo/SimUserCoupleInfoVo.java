package com.seasun.management.vo;

import java.util.Date;

public class SimUserCoupleInfoVo extends SimpleUserInfoVo {

    private Long userId;
    private Integer gender;
    private String city;
    private String workGroup;
    private Integer workAgeInKS;
    private String post;
    private Long coupleUserId;
    private String workStatus;
    private String inDate;

    public String getInDate() {
        return inDate;
    }

    public void setInDate(String inDate) {
        this.inDate = inDate;
    }

    public String getWorkStatus() {
        return workStatus;
    }

    public void setWorkStatus(String workStatus) {
        this.workStatus = workStatus;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getWorkGroup() {
        return workGroup;
    }

    public void setWorkGroup(String workGroup) {
        this.workGroup = workGroup;
    }

    public Integer getWorkAgeInKS() {
        return workAgeInKS;
    }

    public void setWorkAgeInKS(Integer workAgeInKS) {
        this.workAgeInKS = workAgeInKS;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public Long getCoupleUserId() {
        return coupleUserId;
    }

    public void setCoupleUserId(Long coupleUserId) {
        this.coupleUserId = coupleUserId;
    }
}
