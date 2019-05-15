package com.seasun.management.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.seasun.management.model.IdNameBaseObject;
import com.seasun.management.model.Project;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProjectVo extends Project {

    @JsonIgnore
    private Date updateTime;

    private String parentShareName;

    private String orderCenterCodesStr;

    private String usedNamesStr;

    private String managerIds;

    private List<IdNameBaseObject> manageUserList;

    private List<IdNameBaseObject> hrUserList;

    private String WorkGroupName;

    public ProjectVo() {
        super();

        this.manageUserList = new ArrayList<>();
        this.hrUserList = new ArrayList<>();
    }

    public String getParentShareName() {
        return parentShareName;
    }

    public void setParentShareName(String parentShareName) {
        this.parentShareName = parentShareName;
    }

    public String getOrderCenterCodesStr() {
        return orderCenterCodesStr;
    }

    public void setOrderCenterCodesStr(String orderCenterCodesStr) {
        this.orderCenterCodesStr = orderCenterCodesStr;
    }

    public String getUsedNamesStr() {
        return usedNamesStr;
    }

    public void setUsedNamesStr(String usedNamesStr) {
        this.usedNamesStr = usedNamesStr;
    }

    @Override
    public Date getUpdateTime() {
        return updateTime;
    }

    @Override
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public List<IdNameBaseObject> getHrUserList() {
        return hrUserList;
    }

    public void setHrUserList(List<IdNameBaseObject> hrUserList) {
        this.hrUserList = hrUserList;
    }

    public String getManagerIds() {
        return managerIds;
    }

    public void setManagerIds(String managerIds) {
        this.managerIds = managerIds;
    }

    public List<IdNameBaseObject> getManageUserList() {
        return manageUserList;
    }

    public void setManageUserList(List<IdNameBaseObject> manageUserList) {
        this.manageUserList = manageUserList;
    }

    public String getWorkGroupName() {
        return WorkGroupName;
    }

    public void setWorkGroupName(String workGroupName) {
        WorkGroupName = workGroupName;
    }
}
