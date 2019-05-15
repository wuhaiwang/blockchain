package com.seasun.management.vo;

import com.seasun.management.model.Department;
import com.seasun.management.model.IdNameBaseObject;

import java.util.List;

public class DepartmentVo extends Department {

    private String managerIds;

    private List<IdNameBaseObject> manageUserList;

    private String costCenterCodesStr;

    public String getCostCenterCodesStr() {
        return costCenterCodesStr;
    }

    public void setCostCenterCodesStr(String costCenterCodesStr) {
        this.costCenterCodesStr = costCenterCodesStr;
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
}
