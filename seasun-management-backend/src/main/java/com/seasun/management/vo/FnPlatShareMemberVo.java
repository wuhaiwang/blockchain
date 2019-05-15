package com.seasun.management.vo;

import com.seasun.management.model.FnPlatShareMember;

public class FnPlatShareMemberVo extends FnPlatShareMember {

    private Long projectId;

    private String loginId;

    private Long projectName;

    private String userName;

    private String userPhoto;

    private String platName;

    private Float totalPro;

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getProjectName() {
        return projectName;
    }

    public void setProjectName(Long projectName) {
        this.projectName = projectName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPlatName() {
        return platName;
    }

    public void setPlatName(String platName) {
        this.platName = platName;
    }

    public Float getTotalPro() {
        return totalPro;
    }

    public void setTotalPro(Float totalPro) {
        this.totalPro = totalPro;
    }
}
