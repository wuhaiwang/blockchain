package com.seasun.management.controller.org;


import com.alibaba.fastjson.JSONObject;
import com.seasun.management.controller.response.CommonResponse;
import com.seasun.management.model.RUserWorkGroupPerm;
import com.seasun.management.model.WorkGroup;
import com.seasun.management.service.WorkGroupService;
import com.seasun.management.vo.WorkGroupOrgVo;
import com.seasun.management.vo.WorkGroupPerformanceVo;
import com.seasun.management.vo.WorkGroupUserVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class WorkGroupController {

    private static final Logger logger = LoggerFactory.getLogger(WorkGroupController.class);

    @Autowired
    WorkGroupService workGroupService;

    @RequestMapping(value = "/apis/auth/org/work-group-list", method = RequestMethod.GET)
    public ResponseEntity<?> getAllOrgWorkGroupInfoList() {
        logger.info("begin getAllOrgWorkGroupInfoList...");
        WorkGroupOrgVo workGroupOrgMap = workGroupService.getWorkGroupOrgMap();
        logger.info("end getAllOrgWorkGroupInfoList...");
        return ResponseEntity.ok(workGroupOrgMap);
    }

    @RequestMapping(value = "/apis/auth/org/work-group/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getOrgWorkGroupDetail(@PathVariable Long id) {
        logger.info("begin getOrgWorkGroupDetail,id:{}", id);
        WorkGroupPerformanceVo workGroupPerformanceVo = workGroupService.getWorkGroupDetailById(id);
        workGroupPerformanceVo.setMemberList(workGroupService.getWorkGroupMemberById(id));
        workGroupPerformanceVo.setMemberNumber(workGroupPerformanceVo.getMemberNumber() + workGroupPerformanceVo.getMemberList().size());
        logger.info("end getOrgWorkGroupDetail...");
        return ResponseEntity.ok(workGroupPerformanceVo);
    }

    @RequestMapping(value = "/apis/auth/org/work-group/user", method = RequestMethod.GET)
    public ResponseEntity<?> getWorkGroupUserByKeyword(String keyword) {
        logger.info("begin getWorkGroupUserByKeyword,keyword:{}", keyword);
        List<WorkGroupUserVo> result = workGroupService.getWorkGroupUserByKeyword(keyword);
        logger.info("end getWorkGroupUserByKeyword...");
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/apis/auth/org/work-group-base-list", method = RequestMethod.GET)
    public ResponseEntity<?> getAllActiveWorkGroup() {
        logger.info("begin getAllActiveWorkGroup...");
        List<WorkGroup> workGroups = workGroupService.getAllActiveWorkGroup();
        logger.info("end getAllActiveWorkGroup...");
        return ResponseEntity.ok(workGroups);
    }

    @RequestMapping(value = "/apis/auth/user-performance/work-group/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getWorkGroupDetail(@PathVariable Long id) {
        logger.info("begin getWorkGroupDetail,id:{}", id);
        WorkGroupPerformanceVo workGroupPerformanceVo = workGroupService.getWorkGroupDetailById(id);
        logger.info("end getWorkGroupDetail...");
        return ResponseEntity.ok(workGroupPerformanceVo);
    }

    @RequestMapping(value = "/apis/auth/user-performance/r-user-work-group-perm", method = RequestMethod.POST)
    public ResponseEntity<?> updateWorkGroupPerformanceManager(@RequestBody JSONObject js) {
        logger.info("begin updateWorkGroupPerformanceManager");
        workGroupService.updateWorkGroupPermByType(
                js.getLong("groupId"), js.getLong("userId"), js.getString("type"));
        logger.info("end updateWorkGroupPerformanceManager...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }


    @RequestMapping(value = "/apis/auth/user-performance/r-user-work-group-perm/perm-rm", method = RequestMethod.POST)
    public ResponseEntity<?> deleteWorkGroupPerformanceManager(@RequestBody JSONObject js) {
        logger.info("begin deleteWorkGroupPerformanceManager");
        workGroupService.deleteWorkGroupPermByType(
                js.getLong("groupId"), js.getString("loginId"), js.getString("type"));
        logger.info("end deleteWorkGroupPerformanceManager...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }


    @RequestMapping(value = "/apis/auth/org/work-group/hr", method = RequestMethod.POST)
    public ResponseEntity<?> addWorkGroupManager(@RequestBody RUserWorkGroupPerm rUserWorkGroupPerm) {
        workGroupService.addWorkGroupManager(rUserWorkGroupPerm);
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

}
