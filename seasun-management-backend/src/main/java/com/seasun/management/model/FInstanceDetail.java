package com.seasun.management.model;

import java.util.Date;

public class FInstanceDetail {

    public interface ProcessResult {
        int processing = 0;
        int success = 1;
        int failed = 2;
    }

    private Long id;

    private Long instanceId;

    private Long fStepDefineId;

    private Long previousDetail;

    private Boolean endFlag;

    private Integer processResult;

    private Date startTime;

    private Date endTime;

    // private String notifyUserList; //该字段是通知人的配置项，F_step_define 和 F_instance_detail中都需要增加该字段。
    //目前先走系统配置参数表，后续后走该字段来获取通知的对象。

    private Long managerId;

    private String remark;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
    }

    public Long getfStepDefineId() {
        return fStepDefineId;
    }

    public void setfStepDefineId(Long fStepDefineId) {
        this.fStepDefineId = fStepDefineId;
    }

    public Long getPreviousDetail() {
        return previousDetail;
    }

    public void setPreviousDetail(Long previousDetail) {
        this.previousDetail = previousDetail;
    }

    public Boolean getEndFlag() {
        return endFlag;
    }

    public void setEndFlag(Boolean endFlag) {
        this.endFlag = endFlag;
    }

    public Integer getProcessResult() {
        return processResult;
    }

    public void setProcessResult(Integer processResult) {
        this.processResult = processResult;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }
}