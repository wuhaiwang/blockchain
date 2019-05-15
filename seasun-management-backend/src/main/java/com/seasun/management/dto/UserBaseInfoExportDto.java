package com.seasun.management.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

public class UserBaseInfoExportDto extends UserDetailBaseInfoDto implements Serializable {

    private Integer age;

    private String department;

    private String workGroup;

    private String subcompany;

    private String costCenterCode;

    private String orderCenterCode;

    private String project;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss", timezone = "GMT+8")
    private Date ndaBeginDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss", timezone = "GMT+8")
    private Date ndaEndDate;

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getWorkGroup() {
        return workGroup;
    }

    public void setWorkGroup(String workGroup) {
        this.workGroup = workGroup;
    }

    public String getCostCenterCode() {
        return costCenterCode;
    }

    public void setCostCenterCode(String costCenterCode) {
        this.costCenterCode = costCenterCode;
    }

    public String getOrderCenterCode() {
        return orderCenterCode;
    }

    public void setOrderCenterCode(String orderCenterCode) {
        this.orderCenterCode = orderCenterCode;
    }

    public Date getNdaBeginDate() {
        return ndaBeginDate;
    }

    public void setNdaBeginDate(Date ndaBeginDate) {
        this.ndaBeginDate = ndaBeginDate;
    }

    public Date getNdaEndDate() {
        return ndaEndDate;
    }

    public void setNdaEndDate(Date ndaEndDate) {
        this.ndaEndDate = ndaEndDate;
    }

    public String getSubcompany() {
        return subcompany;
    }

    public void setSubcompany(String subcompany) {
        this.subcompany = subcompany;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }
}
