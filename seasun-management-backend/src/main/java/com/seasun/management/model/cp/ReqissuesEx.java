package com.seasun.management.model.cp;

import java.util.Date;

public class ReqissuesEx {
    private Integer id;

    private Integer issueId;

    private Integer cPId;

    private String gameProject;

    private String resType;

    private String description;

    private String location;

    private Integer amount;

    private Boolean schemeReady;

    private Boolean standardReady;

    private Boolean preReady;

    private Integer interfaceMan;

    private Integer testStatus;

    private Integer artLeader;

    private Boolean artConfirm;

    private Date artConfirmTime;

    private String orderNo;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIssueId() {
        return issueId;
    }

    public void setIssueId(Integer issueId) {
        this.issueId = issueId;
    }

    public Integer getcPId() {
        return cPId;
    }

    public void setcPId(Integer cPId) {
        this.cPId = cPId;
    }

    public String getGameProject() {
        return gameProject;
    }

    public void setGameProject(String gameProject) {
        this.gameProject = gameProject == null ? null : gameProject.trim();
    }

    public String getResType() {
        return resType;
    }

    public void setResType(String resType) {
        this.resType = resType == null ? null : resType.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location == null ? null : location.trim();
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Boolean getSchemeReady() {
        return schemeReady;
    }

    public void setSchemeReady(Boolean schemeReady) {
        this.schemeReady = schemeReady;
    }

    public Boolean getStandardReady() {
        return standardReady;
    }

    public void setStandardReady(Boolean standardReady) {
        this.standardReady = standardReady;
    }

    public Boolean getPreReady() {
        return preReady;
    }

    public void setPreReady(Boolean preReady) {
        this.preReady = preReady;
    }

    public Integer getInterfaceMan() {
        return interfaceMan;
    }

    public void setInterfaceMan(Integer interfaceMan) {
        this.interfaceMan = interfaceMan;
    }

    public Integer getTestStatus() {
        return testStatus;
    }

    public void setTestStatus(Integer testStatus) {
        this.testStatus = testStatus;
    }

    public Integer getArtLeader() {
        return artLeader;
    }

    public void setArtLeader(Integer artLeader) {
        this.artLeader = artLeader;
    }

    public Boolean getArtConfirm() {
        return artConfirm;
    }

    public void setArtConfirm(Boolean artConfirm) {
        this.artConfirm = artConfirm;
    }

    public Date getArtConfirmTime() {
        return artConfirmTime;
    }

    public void setArtConfirmTime(Date artConfirmTime) {
        this.artConfirmTime = artConfirmTime;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo == null ? null : orderNo.trim();
    }
}