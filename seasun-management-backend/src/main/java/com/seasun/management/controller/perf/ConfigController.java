package com.seasun.management.controller.perf;

import com.alibaba.fastjson.JSONObject;
import com.seasun.management.annotation.AccessLog;
import com.seasun.management.constant.ErrorCode;
import com.seasun.management.constant.ErrorMessage;
import com.seasun.management.controller.response.AddRecordResponse;
import com.seasun.management.controller.response.CommonResponse;
import com.seasun.management.model.PerfUserCheckResult;
import com.seasun.management.model.PerformanceWorkGroup;
import com.seasun.management.model.UserPerfWorkGroupVo;
import com.seasun.management.service.PerformanceWorkGroupService;
import com.seasun.management.service.UserSalaryService;
import com.seasun.management.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;


@RestController
public class ConfigController {

    private static final Logger logger = LoggerFactory.getLogger(ConfigController.class);

    @Autowired
    UserSalaryService userSalaryService;

    @Autowired
    PerformanceWorkGroupService performanceWorkGroupService;


    @AccessLog(tag = "pc-config-salary-access", message = "访问了薪资导入界面")
    @RequestMapping(value = "/apis/auth/user-performance/salary-import", method = RequestMethod.POST)
    public ResponseEntity<?> importUserSalary(@RequestParam MultipartFile file) {
        logger.info("begin importUserSalary...");
        userSalaryService.importUserSalary(file);
        logger.info("end importUserSalary...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

    @RequestMapping(value = "/apis/auth/performance-work-group/all", method = RequestMethod.GET)
    public ResponseEntity<?> getPerformanceWorkGroupTree() {
        logger.info("begin getPerformanceWorkGroupTree...");
        PerformanceWorkGroupNodeVo tree = performanceWorkGroupService.getPerformanceTree();
        logger.info("end getPerformanceWorkGroupTree...");
        List<PerformanceWorkGroupNodeVo> result = new ArrayList<>();
        result.add(tree);
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/apis/auth/performance-work-group/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getPerformanceWorkGroupInfo(@PathVariable Long id) {
        logger.info("begin getPerformanceWorkGroupInfo...");
        PerformanceWorkGroupVo result = performanceWorkGroupService.getPerformanceWorkGroupInfoById(id);
        logger.info("end getPerformanceWorkGroupInfo...");
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/apis/auth/performance-work-group", method = RequestMethod.POST)
    public ResponseEntity<?> addSubPerformanceGroup(@RequestBody JSONObject jsonObject) {
        logger.info("begin addSubPerformanceGroup...");
        Long performanceWorkGroupId = jsonObject.getLong("performanceWorkGroupId");
        Long subGroupId = jsonObject.getLong("subGroupId");
        Long managerId = jsonObject.getLong("managerId");
        Integer strictType = jsonObject.getInteger("strictType");
        String newName = jsonObject.getString("newName");
        Long id = performanceWorkGroupService.addSubPerformanceWorkGroup(performanceWorkGroupId, subGroupId, managerId, strictType, newName);
        logger.info("end addSubPerformanceGroup...");
        JSONObject result = new JSONObject();
        result.put("id", id);
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/apis/auth/performance-work-group/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteSubPerformanceGroup(@PathVariable Long id) {
        logger.info("begin deleteSubPerformanceGroup...");
        performanceWorkGroupService.deleteSubPerformanceGroup(id);
        logger.info("end deleteSubPerformanceGroup...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

    @RequestMapping(value = "/apis/auth/performance-work-group/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateBycond(@RequestBody PerformanceWorkGroup performanceWorkGroup) {
        logger.info("begin updatePerformanceWorkGroupBycond...");
        performanceWorkGroupService.updateByCond(performanceWorkGroup);
        logger.info("end updatePerformanceWorkGroupBycond...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

    @RequestMapping(value = "/apis/auth/performance-work-group/direct-member", method = RequestMethod.POST)
    public ResponseEntity<?> addDirectMember(@RequestBody JSONObject jsonObject) {
        logger.info("begin addDirectMember...");
        Long memberId = jsonObject.getLong("memberId");
        Long performanceGroupId = jsonObject.getLong("performanceGroupId");
        performanceWorkGroupService.forceAddPerformanceMember(memberId, performanceGroupId);
        logger.info("end addDirectMember...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

    @RequestMapping(value = "/apis/auth/performance-work-group/check-direct-member", method = RequestMethod.GET)
    public ResponseEntity<?> checkDirectMember(@RequestParam Long memberId) {
        logger.info("begin checkDirectMember...");
        JSONObject response = performanceWorkGroupService.checkPerformanceMember(memberId);
        logger.info("end checkDirectMember...");
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "/apis/auth/performance-work-group/sync-hr-work-group", method = RequestMethod.POST)
    public ResponseEntity<?> syncHrWorkGroup() {
        logger.info("begin syncHrWorkGroup...");
        performanceWorkGroupService.syncHrWorkGroupToPerformanceWorkGroup();
        logger.info("end syncHrWorkGroup...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

    @RequestMapping(value = "/apis/auth/performance-work-group/sync-hr-work-group/{id}", method = RequestMethod.POST)
    public ResponseEntity<?> syncSingleHrWorkGroup(@PathVariable Long id) {
        logger.info("begin syncSingleHrWorkGroup...");
        performanceWorkGroupService.syncHrWorkGroupToPerformanceWorkGroupById(id);
        logger.info("end syncSingleHrWorkGroup...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

    @RequestMapping(value = "/apis/auth/performance-work-group/check", method = RequestMethod.GET)
    public ResponseEntity<?> checkPerformanceWorkGroup() {
        logger.info("begin checkPerformanceWorkGroup...");
        CheckResultVo result = performanceWorkGroupService.checkGroup();
        logger.info("end checkPerformanceWorkGroup...");
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/apis/auth/performance-work-group/direct-member/{loginId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteDirectMember(@PathVariable String loginId) {
        logger.info("begin deleteDirectMember...");
        performanceWorkGroupService.deleteDirectMember(loginId);
        logger.info("end deleteDirectMember...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

    @RequestMapping(value = "/apis/auth/performance-work-group/batch-move", method = RequestMethod.POST)
    public ResponseEntity<?> batchMoveWorkGroup(@RequestBody JSONObject js) {
        logger.info("begin batchMoveWorkGroup...");
        Long sourceId = js.getLong("sourceID");
        Long parentId = js.getLong("parentID");
        performanceWorkGroupService.changePerformanceWorkGroupParent(sourceId, parentId);
        logger.info("end batchMoveWorkGroup...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

    @RequestMapping(value = "/apis/auth/performance-work-group/members", method = RequestMethod.GET)
    public ResponseEntity<?> getPerformanceGroupMember(@RequestParam(required = false) Long groupId) {
        logger.info("begin getPerformanceGroupMember...");
        List<PerformanceFixMemberSimpleInfoVo> vos = performanceWorkGroupService.getPerformanceGroupMember(groupId);
        logger.info("end getPerformanceGroupMember...");
        return ResponseEntity.ok(vos);
    }

    @RequestMapping(value = "/apis/auth/performance-work-group/user", method = RequestMethod.GET)
    public ResponseEntity<?> getUserInfo(String keyword) {
        logger.info("begin getUserInfo...");
        List<UserPerfWorkGroupVo> result = performanceWorkGroupService.getUsers(keyword);
        logger.info("end getUserInfo...");
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/apis/auth/performance-work-group/perf-user-check-result", method = RequestMethod.POST)
    public CommonResponse insertPerfUserCheckResult(@RequestBody PerfUserCheckResult perfUserCheckResult) {
        logger.info("begin insertPerfUserCheckResult...");
        Long result = performanceWorkGroupService.insertPerfUserCheckResult(perfUserCheckResult);
        logger.info("end insertPerfUserCheckResult...");
        if (result != null) {
            return new AddRecordResponse(result);
        } else {
            return new CommonResponse(ErrorCode.PARAM_ERROR, ErrorMessage.PARAM_ERROR_MESSAGE);
        }
    }

    @RequestMapping(value = "/apis/auth/performance-work-group/perf-user-check-result/{id}", method = RequestMethod.PUT)
    public CommonResponse updatePerfUserCheckResult(@PathVariable("id") Long id, @RequestBody PerfUserCheckResult perfUserCheckResult) {
        logger.info("begin updatePerfUserCheckResult...");
        int result = performanceWorkGroupService.updateUserCheckResult(id, perfUserCheckResult);
        logger.info("end updatePerfUserCheckResult...");
        if (result > 0) {
            return new CommonResponse();
        } else {
            return new CommonResponse(ErrorCode.PARAM_ERROR, ErrorMessage.PARAM_ERROR_MESSAGE);
        }
    }

    //批量处理(暂未使用)
    @RequestMapping(value = "/apis/auth/performance-work-group/perf-user-check-results", method = RequestMethod.POST)
    public CommonResponse batchUpdatePerfUserCheckResult(@RequestBody List<PerfUserCheckResult> perfUserCheckResult) {
        logger.info("begin batchUpdatePerfUserCheckResult...");
        performanceWorkGroupService.batchUpdatePerfUserCheckResult(perfUserCheckResult);
        logger.info("end batchUpdatePerfUserCheckResult...");
        return new CommonResponse();
    }

    @RequestMapping(value = "/apis/auth/performance-work-group/compared-user", method = RequestMethod.GET)
    public ResponseEntity<?> getUserInfo(@RequestParam Long perfWGId, @RequestParam Long hrWGId) {
        logger.info("begin getUserInfo...");
        WorkGroupCompVo result = performanceWorkGroupService.comparePerfHr(perfWGId, hrWGId);
        logger.info("end getUserInfo...");
        return ResponseEntity.ok(result);
    }
}
