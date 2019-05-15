package com.seasun.management.vo;


public class OrgWorkGroupMemberAppVo {
    private Long id;

    private Long userId;

    private String name;

    private String loginId;

    private String post;

    private Boolean leaderFlag;

    private Long workGroupId;

    private String photo;

    private Long employeeNo;

    private String workStatus;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public Boolean getLeaderFlag() {
        return leaderFlag;
    }

    public void setLeaderFlag(Boolean leaderFlag) {
        this.leaderFlag = leaderFlag;
    }

    public Long getWorkGroupId() {
        return workGroupId;
    }

    public void setWorkGroupId(Long workGroupId) {
        this.workGroupId = workGroupId;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Long getEmployeeNo() {
        return employeeNo;
    }

    public void setEmployeeNo(Long employeeNo) {
        this.employeeNo = employeeNo;
    }
    
    public String getWorkStatus() {
		return workStatus;
	}
    
    public void setWorkStatus(String workStatus) {
		this.workStatus = workStatus;
	}
}
