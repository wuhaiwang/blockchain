package com.seasun.management.vo;

public class ApprovalFlowVo {
    private Long instanceId;

    private String approvalComment;

    private Boolean pass;

    private Boolean allPass;

    public Long getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
    }

    public String getApprovalComment() {
        return approvalComment;
    }

    public void setApprovalComment(String approvalComment) {
        this.approvalComment = approvalComment;
    }

    public Boolean getPass() {
        return pass;
    }

    public void setPass(Boolean pass) {
        this.pass = pass;
    }

    public Boolean getAllPass() {
        return allPass;
    }

    public void setAllPass(Boolean allPass) {
        this.allPass = allPass;
    }
}
