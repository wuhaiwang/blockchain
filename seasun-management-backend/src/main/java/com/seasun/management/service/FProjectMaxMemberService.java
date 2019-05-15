package com.seasun.management.service;

import com.alibaba.fastjson.JSONObject;
import com.seasun.management.model.FProjectMaxMember;
import com.seasun.management.vo.ApprovalFlowVo;
import com.seasun.management.vo.FProjectMaxMemberVo;

import java.util.List;

public interface FProjectMaxMemberService {
    JSONObject getManageProjects();

    JSONObject getFlowListByProject(Long projectId);

    void createApprovalFlow(FProjectMaxMember fProjectMaxMemberProcess);

    List<FProjectMaxMemberVo> getFlowListByApproval();

    FProjectMaxMemberVo getFlowByInstanceId(Long instanceId);

    void batchApprovalFlow();

    void approvalFlow(ApprovalFlowVo approvalFlowVo);

    void createDeployFlow(FProjectMaxMember fProjectMaxMemberProcess);
}
