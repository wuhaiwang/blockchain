package com.seasun.management.model;

import java.util.Date;

public class UserDormitoryReservation {
    private Long id;

    private String name;

    private String certificateType;

    private String idCode;

    private Long userId;

    private String bedroomType;

    private String berthType;

    private Date expectCheckinDate;

    private Date expectCheckoutDate;

    private String status;

    private Date createDate;

    private String roomNo;
    private String liveNo;
    private Integer type;
    private Long projectId;
    private Long companyId;
    private String mobile;
    private Integer gender;
    private String loginId;

    public String getCertificateType() {
        return certificateType;
    }

    public void setCertificateType(String certificateType) {
        this.certificateType = certificateType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getIdCode() {
        return idCode;
    }

    public void setIdCode(String idCode) {
        this.idCode = idCode == null ? null : idCode.trim();
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getBedroomType() {
        return bedroomType;
    }

    public void setBedroomType(String bedroomType) {
        this.bedroomType = bedroomType == null ? null : bedroomType.trim();
    }

    public String getBerthType() {
        return berthType;
    }

    public void setBerthType(String berthType) {
        this.berthType = berthType == null ? null : berthType.trim();
    }

    public Date getExpectCheckinDate() {
        return expectCheckinDate;
    }

    public void setExpectCheckinDate(Date expectCheckinDate) {
        this.expectCheckinDate = expectCheckinDate;
    }

    public Date getExpectCheckoutDate() {
        return expectCheckoutDate;
    }

    public void setExpectCheckoutDate(Date expectCheckoutDate) {
        this.expectCheckoutDate = expectCheckoutDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public String getLiveNo() {
        return liveNo;
    }

    public void setLiveNo(String liveNo) {
        this.liveNo = liveNo;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }
}