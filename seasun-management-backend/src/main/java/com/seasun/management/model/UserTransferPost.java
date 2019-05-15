package com.seasun.management.model;

import java.util.Date;

public class UserTransferPost {
    private Long id;

    private Long userId;

    private String transferType;

    private Date transferTime;

    private String preDepartment;

    private String preProject;

    private String preCompany;

    private String prePost;

    private Long preCostCenterId;

    private Long preOrderCenterId;

    private String newDepartment;

    private String newProject;

    private String newCompany;

    private String newPost;

    private String newPostType;

    private Long newCostCenterId;

    private Long newOrderCenterId;

    //  the following are user defined...
    private String preWorkPlace;

    private String preOrderCenterCode;

    private String newWorkPlace;


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

    public String getTransferType() {
        return transferType;
    }

    public void setTransferType(String transferType) {
        this.transferType = transferType == null ? null : transferType.trim();
    }

    public Date getTransferTime() {
        return transferTime;
    }

    public void setTransferTime(Date transferTime) {
        this.transferTime = transferTime;
    }

    public String getPreDepartment() {
        return preDepartment;
    }

    public void setPreDepartment(String preDepartment) {
        this.preDepartment = preDepartment == null ? null : preDepartment.trim();
    }

    public String getPreProject() {
        return preProject;
    }

    public void setPreProject(String preProject) {
        this.preProject = preProject == null ? null : preProject.trim();
    }

    public String getPreCompany() {
        return preCompany;
    }

    public void setPreCompany(String preCompany) {
        this.preCompany = preCompany == null ? null : preCompany.trim();
    }

    public String getPrePost() {
        return prePost;
    }

    public void setPrePost(String prePost) {
        this.prePost = prePost == null ? null : prePost.trim();
    }

    public Long getPreCostCenterId() {
        return preCostCenterId;
    }

    public void setPreCostCenterId(Long preCostCenterId) {
        this.preCostCenterId = preCostCenterId;
    }

    public Long getPreOrderCenterId() {
        return preOrderCenterId;
    }

    public void setPreOrderCenterId(Long preOrderCenterId) {
        this.preOrderCenterId = preOrderCenterId;
    }

    public String getNewDepartment() {
        return newDepartment;
    }

    public void setNewDepartment(String newDepartment) {
        this.newDepartment = newDepartment == null ? null : newDepartment.trim();
    }

    public String getNewProject() {
        return newProject;
    }

    public void setNewProject(String newProject) {
        this.newProject = newProject == null ? null : newProject.trim();
    }

    public String getNewCompany() {
        return newCompany;
    }

    public void setNewCompany(String newCompany) {
        this.newCompany = newCompany == null ? null : newCompany.trim();
    }

    public String getNewPost() {
        return newPost;
    }

    public void setNewPost(String newPost) {
        this.newPost = newPost == null ? null : newPost.trim();
    }

    public String getNewPostType() {
        return newPostType;
    }

    public void setNewPostType(String newPostType) {
        this.newPostType = newPostType == null ? null : newPostType.trim();
    }

    public Long getNewCostCenterId() {
        return newCostCenterId;
    }

    public void setNewCostCenterId(Long newCostCenterId) {
        this.newCostCenterId = newCostCenterId;
    }

    public Long getNewOrderCenterId() {
        return newOrderCenterId;
    }

    public void setNewOrderCenterId(Long newOrderCenterId) {
        this.newOrderCenterId = newOrderCenterId;
    }

    public String getPreWorkPlace() {
        return preWorkPlace;
    }

    public void setPreWorkPlace(String preWorkPlace) {
        this.preWorkPlace = preWorkPlace;
    }

    public String getPreOrderCenterCode() {
        return preOrderCenterCode;
    }

    public void setPreOrderCenterCode(String preOrderCenterCode) {
        this.preOrderCenterCode = preOrderCenterCode;
    }

    public String getNewWorkPlace() {
        return newWorkPlace;
    }

    public void setNewWorkPlace(String newWorkPlace) {
        this.newWorkPlace = newWorkPlace;
    }
}