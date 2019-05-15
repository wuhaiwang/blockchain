package com.seasun.management.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class OperateLog {

    /* the flowing are user defined ... */

    public interface channel {
        String mobile = "mobile";
        String pc = "pc";
    }

    public interface Type {
        String user_performance_insert = "user_performance_insert";
        String user_performance_modify = "user_performance_modify";
        String group_performance_submit = "group_performance_submit";
        String all_performance_confirm = "all_performance_confirm";
        String user_salary_modify = "user_salary_modify";
        String group_salary_reject = "group_salary_reject";
        String group_salary_submit = "group_salary_submit";
        String all_salary_confirm = "all_salary_confirm";
        String user_grade_modify = "user_grade_modify";
        String user_evaluate_type_modify = "user_evaluate_type_modify";
        String performance_group_delete = "performance_group_delete";
        String user_performance_work_group_modify ="user_performance_work_group_modify";
    }

    public OperateLog() {
    }

    public OperateLog(String type, String desc, Long userId) {
        this.type = type;
        this.description = desc;
        this.operateId = userId;
    }

    private String operateName;

    private Long id;

    private String type;

    private Long operateId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss", timezone = "GMT+8")
    private Date createTime;

    private String description;

    private String channel;

    private String system;

    private String version;

    private String model;

    private String appVersion;

    private String imei;

    private String codePushLabel;

    private String codePushEnvironment;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public Long getOperateId() {
        return operateId;
    }

    public void setOperateId(Long operateId) {
        this.operateId = operateId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel == null ? null : channel.trim();
    }

    public String getOperateName() {
        return operateName;
    }

    public void setOperateName(String operateName) {
        this.operateName = operateName;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getCodePushLabel() {
        return codePushLabel;
    }

    public void setCodePushLabel(String codePushLabel) {
        this.codePushLabel = codePushLabel == null ? null : codePushLabel.trim();
    }

    public String getCodePushEnvironment() {
        return codePushEnvironment;
    }

    public void setCodePushEnvironment(String codePushEnvironment) {
        this.codePushEnvironment = codePushEnvironment == null ? null : codePushEnvironment.trim();
    }
}