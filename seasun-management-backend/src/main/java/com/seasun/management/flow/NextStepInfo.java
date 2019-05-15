package com.seasun.management.flow;

import java.util.List;

public class NextStepInfo {

    private Long flowId;

    private String nextStepName; // 暂时不考虑同时激活多个后续子节点的情况

    private Long managerId;

    public Long getFlowId() {
        return flowId;
    }

    public void setFlowId(Long flowId) {
        this.flowId = flowId;
    }

    public String getNextStepName() {
        return nextStepName;
    }

    public void setNextStepName(String nextStepName) {
        this.nextStepName = nextStepName;
    }

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }
}
