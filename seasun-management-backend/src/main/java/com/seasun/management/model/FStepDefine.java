package com.seasun.management.model;

public class FStepDefine {
    private Long id;

    private Long flowId;

    private String name;

    private Long managerId;

    private Long previousStep;

    private Boolean endFlag;

    private String description;

    // private String notifyUserList; //该字段是通知人的配置项，F_step_define 和 F_instance_detail中都需要增加该字段。
                                      //目前先走系统配置参数表，后续后走该字段来获取通知的对象。

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFlowId() {
        return flowId;
    }

    public void setFlowId(Long flowId) {
        this.flowId = flowId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }

    public Long getPreviousStep() {
        return previousStep;
    }

    public void setPreviousStep(Long previousStep) {
        this.previousStep = previousStep;
    }

    public Boolean getEndFlag() {
        return endFlag;
    }

    public void setEndFlag(Boolean endFlag) {
        this.endFlag = endFlag;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }
}