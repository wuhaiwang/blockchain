package com.seasun.management.controller.perf;

import com.seasun.management.annotation.AccessLog;
import com.seasun.management.controller.response.CommonAppResponse;
import com.seasun.management.controller.response.CommonResponse;
import com.seasun.management.controller.response.FileResponse;
import com.seasun.management.model.MySalaryKey;
import com.seasun.management.model.UserSalaryChange;
import com.seasun.management.service.UserSalaryChangeService;
import com.seasun.management.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/apis/auth/user-salary-change")
public class UserSalaryChangeController {

    private static final Logger logger = LoggerFactory.getLogger(UserSalaryChangeController.class);

    @Autowired
    UserSalaryChangeService userSalaryChangeService;

    @AccessLog(tag = "pc-salary-access", message = "访问了调薪界面")
    @RequestMapping(value = "/sub-salary-change", method = RequestMethod.GET)
    public ResponseEntity<?> getSubSalaryChange(@RequestParam Long workGroupId,
                                                @RequestParam Integer year, @RequestParam Integer quarter) {
        logger.info("begin getSubSalaryChange...year:{},quarter:{},workGroupId:{}", year, quarter, workGroupId);
        SubordinateSalaryChangeAppVo subordinateSalaryChangeWebVo = userSalaryChangeService.getSubSalaryChangeWeb(workGroupId, year, quarter);
        logger.info("end getSubSalaryChange");
        return ResponseEntity.ok(new CommonAppResponse(0, subordinateSalaryChangeWebVo));
    }

    @RequestMapping(value = "/member-salary-change", method = RequestMethod.GET)
    public ResponseEntity<?> getMemberSalaryChange(@RequestParam Long groupId, @RequestParam Integer year, @RequestParam Integer quarter, @RequestParam Boolean isHistory) {
        logger.info("begin getMemberSalaryChange...groupId:{},year:{},quarter:{},isHistory:{}", groupId, year, quarter, isHistory);
        List<OrdinateSalaryChangeAppVo> list = userSalaryChangeService.getMemberSalaryChange(groupId, year, quarter, isHistory);
        logger.info("end get MemberSalaryChange...");
        return ResponseEntity.ok(new CommonAppResponse(0, list));
    }

    @RequestMapping(value = "/salary-change-detail", method = RequestMethod.GET)
    public ResponseEntity<?> getIndividualDetail(@RequestParam Boolean isHistory, @RequestParam Long userId, @RequestParam Integer year, @RequestParam Integer quarter) {
        logger.info("begin getIndividualDetail...isHistory:{}");
        IndividualSalaryChangeVo vo = userSalaryChangeService.getIndividualDetail(isHistory, userId, year, quarter);
        logger.info("end getIndividualDetail...");
        return ResponseEntity.ok(new CommonAppResponse(0, vo));
    }

    @RequestMapping(value = "/status-search", method = RequestMethod.POST)
    public ResponseEntity<?> getSubGroupMembers(@RequestBody UserSalaryConditionVo userSalaryConditionVo) {
        logger.info("begin getSubGroupMembers...");
        List<List<OrdinateSalaryChangeAppVo>> result = userSalaryChangeService.getSubGroupMembers(userSalaryConditionVo.getHistory(), userSalaryConditionVo.getWorkGroupId()
                , userSalaryConditionVo.getStatus(), userSalaryConditionVo.getGroupIds(), userSalaryConditionVo.getYear(), userSalaryConditionVo.getQuarter());
        logger.info("end getSubGroupMembers");
        return ResponseEntity.ok(new CommonAppResponse(0, result));
    }

    @RequestMapping(value = "/modify-person/{workGroupId}", method = RequestMethod.PUT)
    public ResponseEntity<?> modifyOrdinateSalaryChange(@RequestBody IndividualSalaryChangeVo vo, @PathVariable("workGroupId") Long workGroupId) {
        logger.info("begin modifyOrdinateSalaryChange...");
        userSalaryChangeService.modifyOrdinateSalaryChange(vo, workGroupId);
        logger.info("end modifyOrdinateSalaryChange...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

    @RequestMapping(value = "/request-reject", method = RequestMethod.POST)
    public ResponseEntity<?> requestReject(@RequestBody UserSalaryConditionVo userSalaryConditionVo) {
        logger.info("begin requestReject...");
        userSalaryChangeService.requestReject(userSalaryConditionVo.getYear(), userSalaryConditionVo.getQuarter(), userSalaryConditionVo.getWorkGroupId());
        logger.info("end requestReject...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

    @RequestMapping(value = "/group-commit", method = RequestMethod.POST)
    public ResponseEntity<?> groupCommit(@RequestBody UserSalaryConditionVo userSalaryConditionVo) {
        logger.info("begin groupCommit...");
        userSalaryChangeService.groupCommit(userSalaryConditionVo.getWorkGroupId(), userSalaryConditionVo.getYear(), userSalaryConditionVo.getQuarter());
        logger.info("end groupCommit...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

    @RequestMapping(value = "/submit-assist", method = RequestMethod.POST)
    public ResponseEntity<?> assistSubmit(@RequestBody UserSalaryConditionVo userSalaryConditionVo) {
        logger.info("begin assistSubmit...");
        userSalaryChangeService.assistSubmit(userSalaryConditionVo.getWorkGroupId(), userSalaryConditionVo.getYear(), userSalaryConditionVo.getQuarter());
        logger.info("end assistSubmit...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

    @RequestMapping(value = "/leader-finish", method = RequestMethod.POST)
    public ResponseEntity<?> leaderFinish(@RequestBody UserSalaryConditionVo userSalaryConditionVo) {
        logger.info("begin leaderFinish...");
        userSalaryChangeService.leaderFinish(userSalaryConditionVo.getYear(), userSalaryConditionVo.getQuarter());
        logger.info("end leaderFinish...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

    @RequestMapping(value = "/month-process", method = RequestMethod.GET)
    public ResponseEntity<?> performanceMonthCount(@RequestParam Integer year, @RequestParam Integer quarter) {
        logger.info("begin get performance month counts...");
        int result = userSalaryChangeService.performanceMonthCount(year, quarter);
        logger.info("end get performance month counts");
        return ResponseEntity.ok(new CommonResponse(0, result + ""));
    }

    @RequestMapping(value = "/all-group-status", method = RequestMethod.GET)
    public ResponseEntity<?> getAllTimeGroupStatus(@RequestParam Long workGroupId) {
        logger.info("begin getAllTimeGroupStatus...");
        List<SubordinateSalaryChangeAppVo.GroupStatus> list = userSalaryChangeService.getAllTimeGroupStatus(workGroupId);
        logger.info("end getAllTimeGroupStatus");
        return ResponseEntity.ok(list);
    }

    @RequestMapping(value = "/file-export", method = RequestMethod.POST)
    public ResponseEntity<?> downloadSalaryFile(@RequestParam Integer year, @RequestParam Integer quarter, @RequestBody UserFileInfoVo userFileInfoVo) {
        logger.info("begin downloadSalaryFile...year:{},quarter:{}", year, quarter);
        String filePath = userSalaryChangeService.downloadSalaryFile(year, quarter, userFileInfoVo.getColumns());
        logger.info("end downloadSalaryFile...");
        return ResponseEntity.ok(new FileResponse(0, filePath));
    }

    // todo: 增加查看季度绩效详情的接口
//
//    @RequestMapping(value ="/all",method=RequestMethod.GET)
//    public ResponseEntity<?> getAllSalaryChange(){
//        logger.info("begin getAllSalaryChange...");
//        long startTime =System.currentTimeMillis();
//        List<UserSalaryChange> userSalaryChanges = userSalaryChangeService.cloneDateObjectPerformanceTest();
//        long endTime =System.currentTimeMillis();
//        logger.info("Time..................."+(endTime-startTime));
//        logger.info("begin getAllSalaryChange...");
//        return  ResponseEntity.ok(userSalaryChanges);
//    }

    // 接收薪资库同步接口
    @RequestMapping(value = "salary-key",method = RequestMethod.POST)
    public ResponseEntity<?> receiveSalaryKey(@RequestBody MySalaryKey mySalaryKey){
        logger.info("begin receiveSalaryKey ...");
         userSalaryChangeService.receiveSalaryKey(mySalaryKey);
        logger.info("end receiveSalaryKey...");
        return ResponseEntity.ok(new FileResponse(0, "success"));
    }
}
