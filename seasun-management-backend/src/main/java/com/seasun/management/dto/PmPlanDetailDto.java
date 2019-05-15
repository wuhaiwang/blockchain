package com.seasun.management.dto;

import com.seasun.management.model.IdNameBaseObject;
import com.seasun.management.model.PmPlanDetail;

import java.util.List;

public class PmPlanDetailDto extends PmPlanDetail {

    private String managerName;

    private List<IdNameBaseObject> managers;

    private Integer year;

    private Long projectId;

    private Boolean newPlanFlag;

    public Boolean getNewPlanFlag() {
        return newPlanFlag;
    }

    public void setNewPlanFlag(Boolean newPlanFlag) {
        this.newPlanFlag = newPlanFlag;
    }

    public List<IdNameBaseObject> getManagers() {
        return managers;
    }

    public void setManagers(List<IdNameBaseObject> managers) {
        this.managers = managers;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }
}
