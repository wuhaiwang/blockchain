package com.seasun.management.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class UserMessage {
    public interface Type {
        String system = "system";
        String user = "user";
    }

    public interface Location {
        String performanceMyPerformance = "performance-myPerformance";
        String performanceSubordinate = "performance-subordinate";
        String performanceFixMember = "performance-fix-member";
        String project = "project";
        String platform = "platform";
        String finance = "finance";
        String hr = "hr";
        String projectMaxMemberFlow = "project-max-member-flow";
        String dailyMessage = "dailyMessage";
        String anuualDraw = "annual-draw";
    }

    public interface Content {
        String user_perf_write_notify = "您在%s年%s月的绩效未填写，请尽快填写。";
        String user_perf_submit_notify = "您在%s年%s月的绩效填写后未提交，请尽快提交。";
        String perf_work_group_submit_notify = "您所管理的绩效组在%s年%s月的绩效记录中尚未提交，请尽快提交。";
    }

    private Long id;

    private Long sender;

    private Long receiver;

    private String type;

    private String location;

    private String params;

    private String content;

    private Long versionId;

    private Boolean readFlag;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss", timezone = "GMT+8")
    private Date readTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSender() {
        return sender;
    }

    public void setSender(Long sender) {
        this.sender = sender;
    }

    public Long getReceiver() {
        return receiver;
    }

    public void setReceiver(Long receiver) {
        this.receiver = receiver;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location == null ? null : location.trim();
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params == null ? null : params.trim();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public Long getVersionId() {
        return versionId;
    }

    public void setVersionId(Long versionId) {
        this.versionId = versionId;
    }

    public Boolean getReadFlag() {
        return readFlag;
    }

    public void setReadFlag(Boolean readFlag) {
        this.readFlag = readFlag;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getReadTime() {
        return readTime;
    }

    public void setReadTime(Date readTime) {
        this.readTime = readTime;
    }
}