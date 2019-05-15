package com.seasun.management.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class OrgMaxMemberChangeLog {
    private Long id;

    private Long projectId;

    private Integer oldMaxMember;

    private Integer newMaxMember;

    private String reason;

    private String attachmentUrl;

    private Long operatorId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

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

    public Integer getOldMaxMember() {
        return oldMaxMember;
    }

    public void setOldMaxMember(Integer oldMaxMember) {
        this.oldMaxMember = oldMaxMember;
    }

    public Integer getNewMaxMember() {
        return newMaxMember;
    }

    public void setNewMaxMember(Integer newMaxMember) {
        this.newMaxMember = newMaxMember;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason == null ? null : reason.trim();
    }

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl == null ? null : attachmentUrl.trim();
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}