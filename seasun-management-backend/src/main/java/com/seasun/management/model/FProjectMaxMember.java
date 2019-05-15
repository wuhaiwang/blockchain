package com.seasun.management.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FProjectMaxMember {
    private Long id;

    private Long projectId;

    private Integer currentMember;

    private Integer maxMember;

    private Integer applyMaxMember;

    private String reason;

    private String approvalComment;

    @JsonProperty("deploy")
    private Boolean deployFlag;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Integer getCurrentMember() {
        return currentMember;
    }

    public void setCurrentMember(Integer currentMember) {
        this.currentMember = currentMember;
    }

    public Integer getMaxMember() {
        return maxMember;
    }

    public void setMaxMember(Integer maxMember) {
        this.maxMember = maxMember;
    }

    public Integer getApplyMaxMember() {
        return applyMaxMember;
    }

    public void setApplyMaxMember(Integer applyMaxMember) {
        this.applyMaxMember = applyMaxMember;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason == null ? null : reason.trim();
    }

    public String getApprovalComment() {
        return approvalComment;
    }

    public void setApprovalComment(String approvalComment) {
        this.approvalComment = approvalComment == null ? null : approvalComment.trim();
    }

    public Boolean getDeployFlag() {
        return deployFlag;
    }

    public void setDeployFlag(Boolean deployFlag) {
        this.deployFlag = deployFlag;
    }
}