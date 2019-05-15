package com.seasun.management.vo;

import com.seasun.management.model.FnProjectStatData;

import java.util.ArrayList;
import java.util.List;

public class FnProjectStatDataVo {
    private Long projectId;
    private String projectName;

    List<FnProjectStatData> data;

    public FnProjectStatDataVo() {
        this.data = new ArrayList<>();
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public List<FnProjectStatData> getData() {
        return data;
    }

    public void setData(List<FnProjectStatData> data) {
        this.data = data;
    }
}
