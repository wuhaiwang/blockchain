package com.seasun.management.dto;

public class ContactGroupUserDto {
    private Long userId;

    private String loginId;

    private String name;

    private String photo;

    private Long employeeNo;

    private String post;

    private Long userContactGroupId;

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

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getUserContactGroupId() {
        return userContactGroupId;
    }

    public void setUserContactGroupId(Long userContactGroupId) {
        this.userContactGroupId = userContactGroupId;
    }
}
