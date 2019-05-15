package com.seasun.management.flow;

import java.util.Map;

public class FlowInfo {

    private Long flowId;

    private Long instanceId;

    private Long instanceDetailId;

    private Long businessKey;

    Map<Object, Object> ext;

    public Long getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(Long instanceId) {
        this.instanceId = instanceId;
    }

    public Long getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(Long businessKey) {
        this.businessKey = businessKey;
    }

    public Map<Object, Object> getExt() {
        return ext;
    }

    public void setExt(Map<Object, Object> ext) {
        this.ext = ext;
    }

    public Long getFlowId() {
        return flowId;
    }

    public void setFlowId(Long flowId) {
        this.flowId = flowId;
    }

    public Long getInstanceDetailId() {
        return instanceDetailId;
    }

    public void setInstanceDetailId(Long instanceDetailId) {
        this.instanceDetailId = instanceDetailId;
    }
}
