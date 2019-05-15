package com.seasun.management.model;

import java.util.Date;

public class FnShareData {
    private Long id;

    private Long platId;

    private Long projectId;

    private Integer year;

    private Integer month;

    private Float sharePro;

    private Float shareAmount;

    private Float shareNumber;

    private Date createTime;

    private Date updateTime;

    //  the following are user defined...


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPlatId() {
        return platId;
    }

    public void setPlatId(Long platId) {
        this.platId = platId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Float getSharePro() {
        return sharePro;
    }

    public void setSharePro(Float sharePro) {
        this.sharePro = sharePro;
    }

    public Float getShareAmount() {
        return shareAmount;
    }

    public void setShareAmount(Float shareAmount) {
        this.shareAmount = shareAmount;
    }

    public Float getShareNumber() {
        return shareNumber;
    }

    public void setShareNumber(Float shareNumber) {
        this.shareNumber = shareNumber;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}