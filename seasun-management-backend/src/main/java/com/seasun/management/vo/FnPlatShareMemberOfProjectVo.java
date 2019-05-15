package com.seasun.management.vo;

import java.util.List;

public class FnPlatShareMemberOfProjectVo {
    private Long projectId;
    private String projectName;
    private List<FnPlatShareMemberVo> fnPlatShareMemberVoList;

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

    public List<FnPlatShareMemberVo> getFnPlatShareMemberVoList() {
        return fnPlatShareMemberVoList;
    }

    public void setFnPlatShareMemberVoList(List<FnPlatShareMemberVo> fnPlatShareMemberVoList) {
        this.fnPlatShareMemberVoList = fnPlatShareMemberVoList;
    }
}
