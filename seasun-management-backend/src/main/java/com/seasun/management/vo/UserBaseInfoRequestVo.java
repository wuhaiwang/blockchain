package com.seasun.management.vo;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

public class UserBaseInfoRequestVo {

    private String loginId;

    private String employeeNo;

    private String workGroupId;

    private String  projectId;

    private String orderCenterCode;

    private String costCenterCode;

    private String workStatus;

    private String activeFlag;

    private String virtualFlag;

    private String subCompanyIds;

    private Integer currentPage = 1;

    private Integer pageSize = 30;

    private String[] subcompanys;

    private Integer offset;

    private Integer limit;

    private Boolean needDepartment = false;

    private LinkedHashSet<String> exports;

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getEmployeeNo() {
        return employeeNo;
    }

    public void setEmployeeNo(String employeeNo) {
        this.employeeNo = employeeNo;
    }

    public String getWorkGroupId() {
        return workGroupId;
    }

    public void setWorkGroupId(String workGroupId) {
        this.workGroupId = workGroupId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getOrderCenterCode() {
        return orderCenterCode;
    }

    public void setOrderCenterCode(String orderCenterCode) {
        this.orderCenterCode = orderCenterCode;
    }

    public String getCostCenterCode() {
        return costCenterCode;
    }

    public void setCostCenterCode(String costCenterCode) {
        this.costCenterCode = costCenterCode;
    }

    public String getWorkStatus() {
        return workStatus;
    }

    public void setWorkStatus(String workStatus) {
        this.workStatus = workStatus;
    }

    public String getActiveFlag() {
        return activeFlag;
    }

    public void setActiveFlag(String activeFlag) {
        this.activeFlag = activeFlag;
    }

    public String getVirtualFlag() {
        return virtualFlag;
    }

    public void setVirtualFlag(String virtualFlag) {
        this.virtualFlag = virtualFlag;
    }

    public String getSubCompanyIds() {
        return subCompanyIds;
    }

    public void setSubCompanyIds(String subCompanyIds) {
        this.subCompanyIds = subCompanyIds;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public String[] getSubcompanys() {
        return subcompanys;
    }

    public void setSubcompanys(String[] subcompanys) {
        this.subcompanys = subcompanys;
    }

    public Boolean getNeedDepartment() {
        return needDepartment;
    }

    public void setNeedDepartment(Boolean needDepartment) {
        this.needDepartment = needDepartment;
    }

    public LinkedHashSet<String> getExports() {
        return exports;
    }

    public void setExports(LinkedHashSet<String> exports) {
        this.exports = exports;
    }

    @Override
    public String toString() {
        return "UserBaseInfoRequestVo{" +
                "loginId='" + loginId + '\'' +
                ", employeeNo='" + employeeNo + '\'' +
                ", workGroupId=" + workGroupId +
                ", projectId=" + projectId +
                ", orderCenterCode='" + orderCenterCode + '\'' +
                ", costCenterCode='" + costCenterCode + '\'' +
                ", workStatus='" + workStatus + '\'' +
                ", activeFlag=" + activeFlag +
                ", virtualFlag=" + virtualFlag +
                ", subCompanyIds='" + subCompanyIds + '\'' +
                ", currentPage=" + currentPage +
                ", pageSize=" + pageSize +
                ", subcompanys=" + Arrays.toString(subcompanys) +
                ", offset=" + offset +
                ", limit=" + limit +
                ", needDepartment=" + needDepartment +
                ", exports=" + exports +
                '}';
    }
}
