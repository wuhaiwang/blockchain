package com.seasun.management.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.seasun.management.model.FnProjectSchedData;

import java.util.*;

public class FnProjectSchedDataVo {

    private Long projectId;

    private String projectName;

    private String city;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date setupDate;

    private List<FnProjectSchedData> data;

    public FnProjectSchedDataVo() {
        this.data = new ArrayList<>();
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Date getSetupDate() {
        return setupDate;
    }

    public void setSetupDate(Date setupDate) {
        this.setupDate = setupDate;
    }

    public List<FnProjectSchedData> getData() {
        return data;
    }

    public void setData(List<FnProjectSchedData> data) {
        this.data = data;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}
