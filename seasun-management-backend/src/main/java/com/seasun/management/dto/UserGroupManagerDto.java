package com.seasun.management.dto;

public class UserGroupManagerDto {

    private Long id;

    private String loginId;

    private String name;

    private String email;

    private String phone;

    private Long groupId;

    private Long groupManagerId;

    private String groupManagerName;

    private String groupManagerLoginId;

    private Long groupRoleId;

    private String groupRoleName;

    private String groupManagerPhone;

    private String groupManagerEmail;

    public String getGroupManagerPhone() {
        return groupManagerPhone;
    }

    public void setGroupManagerPhone(String groupManagerPhone) {
        this.groupManagerPhone = groupManagerPhone;
    }

    public String getGroupManagerEmail() {
        return groupManagerEmail;
    }

    public void setGroupManagerEmail(String groupManagerEmail) {
        this.groupManagerEmail = groupManagerEmail;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getGroupManagerId() {
        return groupManagerId;
    }

    public void setGroupManagerId(Long groupManagerId) {
        this.groupManagerId = groupManagerId;
    }

    public String getGroupManagerName() {
        return groupManagerName;
    }

    public void setGroupManagerName(String groupManagerName) {
        this.groupManagerName = groupManagerName;
    }

    public String getGroupManagerLoginId() {
        return groupManagerLoginId;
    }

    public void setGroupManagerLoginId(String groupManagerLoginId) {
        this.groupManagerLoginId = groupManagerLoginId;
    }

    public Long getGroupRoleId() {
        return groupRoleId;
    }

    public void setGroupRoleId(Long groupRoleId) {
        this.groupRoleId = groupRoleId;
    }

    public String getGroupRoleName() {
        return groupRoleName;
    }

    public void setGroupRoleName(String groupRoleName) {
        this.groupRoleName = groupRoleName;
    }

}
