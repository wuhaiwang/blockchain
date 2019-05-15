package com.seasun.management.controller.app;

import com.alibaba.fastjson.JSONObject;
import com.seasun.management.controller.response.CommonAppResponse;
import com.seasun.management.controller.response.CommonResponse;
import com.seasun.management.model.FProjectMaxMember;
import com.seasun.management.service.FProjectMaxMemberService;
import com.seasun.management.vo.ApprovalFlowVo;
import com.seasun.management.vo.FProjectMaxMemberVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/apis/auth/app/project-max-member-flow")
public class AppProjectMaxMemberFlowController {

    @Autowired
    FProjectMaxMemberService fProjectMaxMemberService;

    private static final Logger logger = LoggerFactory.getLogger(AppProjectMaxMemberFlowController.class);

    @RequestMapping(value = "/project-flow", method = RequestMethod.GET)
    public ResponseEntity<?> getAppFlowListByProject(@RequestParam Long projectId) {
        logger.info("begin getAppFlowListByProject...");
        JSONObject jsonObject = fProjectMaxMemberService.getFlowListByProject(projectId);
        logger.info("end getAppFlowListByProject...");
        return ResponseEntity.ok(new CommonAppResponse(0, jsonObject));
    }

    @RequestMapping(value = "/projects", method = RequestMethod.GET)
    public ResponseEntity<?> getAppManageProjects() {
        logger.info("begin getAppManageProjects...");
        JSONObject jsonObject = fProjectMaxMemberService.getManageProjects();
        logger.info("end getAppManageProjects...");
        return ResponseEntity.ok(new CommonAppResponse(0, jsonObject));
    }

    @RequestMapping(value = "/flow-approval", method = RequestMethod.GET)
    public ResponseEntity<?> getAppFlowListByApproval() {
        logger.info("begin getAppFlowListByApproval...");
        List<FProjectMaxMemberVo> FProjectMaxMemberVos = fProjectMaxMemberService.getFlowListByApproval();
        logger.info("end getAppFlowListByApproval...");
        return ResponseEntity.ok(new CommonAppResponse(0, FProjectMaxMemberVos));
    }

    @RequestMapping(value = "/flow/{instanceId}", method = RequestMethod.GET)
    public ResponseEntity<?> getAppFlowByInstanceId(@PathVariable Long instanceId) {
        logger.info("begin getAppFlowByInstanceId...");
        FProjectMaxMemberVo FProjectMaxMemberVo = fProjectMaxMemberService.getFlowByInstanceId(instanceId);
        logger.info("end getAppFlowByInstanceId...");
        return ResponseEntity.ok(new CommonAppResponse(0, FProjectMaxMemberVo));
    }

    @RequestMapping(value = "/flow", method = RequestMethod.POST)
    public ResponseEntity<?> createAppApprovalFlow(@RequestBody FProjectMaxMember fProjectMaxMemberProcess) {
        logger.info("begin createAppApprovalFlow...");
        fProjectMaxMemberService.createApprovalFlow(fProjectMaxMemberProcess);
        logger.info("end createAppApprovalFlow...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

    @RequestMapping(value = "/flow-approval", method = RequestMethod.POST)
    public ResponseEntity<?> approvalAppFlow(@RequestBody ApprovalFlowVo approvalFlowVo) {
        logger.info("begin approvalAppFlow...");
        if (null == approvalFlowVo.getAllPass()) {
            fProjectMaxMemberService.approvalFlow(approvalFlowVo);
        } else {
            fProjectMaxMemberService.batchApprovalFlow();
        }
        logger.info("end approvalAppFlow...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

    @RequestMapping(value = "/flow-deploy", method = RequestMethod.POST)
    public ResponseEntity<?> createAppDeployFlow(@RequestBody FProjectMaxMember fProjectMaxMemberProcess) {
        logger.info("begin createAppDeployFlow...");
        fProjectMaxMemberService.createDeployFlow(fProjectMaxMemberProcess);
        logger.info("end createAppDeployFlow...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }
}
