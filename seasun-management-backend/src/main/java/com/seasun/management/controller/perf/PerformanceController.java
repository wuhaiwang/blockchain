package com.seasun.management.controller.perf;

import com.alibaba.fastjson.JSONObject;
import com.seasun.management.annotation.AccessLog;
import com.seasun.management.controller.response.AddRecordResponse;
import com.seasun.management.controller.response.CommonAppResponse;
import com.seasun.management.controller.response.CommonResponse;
import com.seasun.management.controller.response.FileResponse;
import com.seasun.management.dto.YearMonthDto;
import com.seasun.management.helper.UserPerformanceHelper;
import com.seasun.management.model.UserPerformance;
import com.seasun.management.service.FixMemberService;
import com.seasun.management.service.PerformanceDataService;
import com.seasun.management.service.UserPerformanceService;
import com.seasun.management.service.WorkGroupService;
import com.seasun.management.util.MyTokenUtils;
import com.seasun.management.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/apis/auth/user-performance")
public class PerformanceController {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceController.class);

    @Autowired
    WorkGroupService workGroupService;

    @Autowired
    private UserPerformanceService userPerformanceService;

    @Autowired
    private FixMemberService fixMemberService;

    @Autowired
    private PerformanceDataService performanceDataService;

    @RequestMapping(value = "/identity", method = RequestMethod.GET)
    public ResponseEntity<?> getUserPerformanceIdentity(@RequestParam long userType) {
        logger.info("begin getUserPerformanceIdentity...");
        UserPerformanceIdentityAppVo userPerformanceIdentityAppVo = workGroupService.getUserPerformanceIdentity(userType);
        logger.info("end getUserPerformanceIdentity...");
        return ResponseEntity.ok(userPerformanceIdentityAppVo);
    }


    @RequestMapping(value = "/sub-performance", method = RequestMethod.GET)
    public ResponseEntity<?> getSubPerformance(@RequestParam(required = false) Integer year, @RequestParam(required = false) Integer month, @RequestParam(required = false) String filter) {
        logger.info("begin getSubPerformance...");
        if (UserPerformanceHelper.SearchFilter.fixmember.equals(filter)) {
            SubFixMemberPerformanceVo fmResult = fixMemberService.getSubFixMemberPerformance(year, month);
            int selectIndex = UserPerformanceHelper.getSelectIndex(fmResult.getHistory(), year, month);
            fmResult.setSelectIndex(selectIndex);

            // 补充historyInfo
            ProjectFixMemberInfoVo.History historyInfo = new ProjectFixMemberInfoVo.History();
            historyInfo.setSelectIndex(selectIndex);
            historyInfo.setAll(fmResult.getHistory());
            fmResult.setHistoryInfo(historyInfo);

            logger.info("end getFmSubPerformance...");
            return ResponseEntity.ok(fmResult);
        } else {
            SubPerformanceAppVo performanceResult = userPerformanceService.getSubPerformance(year, month, filter);
            int selectIndex = UserPerformanceHelper.getSelectIndex(performanceResult.getHistory(), year, month);
            performanceResult.setSelectIndex(selectIndex);

            // 补充historyInfo
            ProjectFixMemberInfoVo.History historyInfo = new ProjectFixMemberInfoVo.History();
            historyInfo.setSelectIndex(selectIndex);
            historyInfo.setAll(performanceResult.getHistory());
            performanceResult.setHistoryInfo(historyInfo);

            logger.info("end getSubPerformance...");
            return ResponseEntity.ok(performanceResult);
        }
    }

    @AccessLog(tag = "pc-performance-access", message = "访问了下属绩效界面的历史信息")
    @RequestMapping(value = "/sub-performance/history", method = RequestMethod.GET)
    public ResponseEntity<?> getSubPerformanceHistory() {
        logger.info("begin getSubPerformanceHistory...");
        List<SubPerformanceAppVo.HistoryInfo> historyInfos = userPerformanceService.getWorkGroupHistory();
        logger.info("end getSubPerformanceHistory...");
        return ResponseEntity.ok(historyInfos);
    }

    @RequestMapping(value = "/member-performance", method = RequestMethod.GET)
    public ResponseEntity<?> getMemberPerformance(@RequestParam Long workGroupId, @RequestParam Integer year, @RequestParam Integer month) {
        logger.info("begin getMemberPerformance...");
        WorkGroupMemberPerformanceAppVo workGroupMemberPerformanceAppVo = userPerformanceService.getWorkGroupMemberPerformance(workGroupId, year, month);
        logger.info("end getMemberPerformance...");
        return ResponseEntity.ok(workGroupMemberPerformanceAppVo);
    }

    // todo: 改版时间线
    @RequestMapping(value = "/performance-detail", method = RequestMethod.GET)
    public ResponseEntity<?> getPerformanceDetail(@RequestParam(required = false) Long userId, @RequestParam(required = false) Integer year, @RequestParam(required = false) Integer month) {
        logger.info("begin getPerformanceDetail...");
        UserPerformanceDetailAppVo userPerformanceDetailAppVo = userPerformanceService.getUserPerformanceDetail(userId, year, month);
        List<SubPerformanceBaseVo.HistoryInfo> originalHistoryList = new ArrayList<>();
        for (SubPerformanceBaseVo.HistoryInfo historyInfo : userPerformanceDetailAppVo.getHistory()) {
            SubPerformanceBaseVo.HistoryInfo temp = new SubPerformanceBaseVo.HistoryInfo();
            temp.setYear(historyInfo.getYear());
            temp.setMonth(historyInfo.getMonth());
            temp.setStatus(historyInfo.getStatus());
            originalHistoryList.add(temp);
        }
        int selectIndex = UserPerformanceHelper.getSelectIndex(originalHistoryList, year, month);
        userPerformanceDetailAppVo.setSelectIndex(selectIndex);

        // 补充historyInfo
        ProjectFixMemberInfoVo.History historyInfo = new ProjectFixMemberInfoVo.History();
        historyInfo.setSelectIndex(selectIndex);
        historyInfo.setAll(userPerformanceDetailAppVo.getHistory());
        userPerformanceDetailAppVo.setHistoryInfo(historyInfo);

        logger.info("end getPerformanceDetail...");
        return ResponseEntity.ok(userPerformanceDetailAppVo);
    }

    @RequestMapping(value = "/performance-detail/history", method = RequestMethod.GET)
    public ResponseEntity<?> getPerformanceDetail(@RequestParam(required = false) Long userId) {
        logger.info("begin getPerformanceDetail...");
        boolean isMyself = userId == null;
        List<SubPerformanceBaseVo.HistoryInfo> historyInfos = userPerformanceService.getUserPerformanceHistory(userId, isMyself);
        logger.info("end getPerformanceDetail...");
        return ResponseEntity.ok(historyInfos);
    }

    @RequestMapping(value = "/performance-detail", method = RequestMethod.POST)
    public ResponseEntity<?> addPerformanceDetail(@RequestBody UserPerformance userPerformance) {
        logger.info("begin addPerformanceDetail...");
        userPerformanceService.addPerformanceDetail(userPerformance);
        logger.info("end addPerformanceDetail...");
        return ResponseEntity.ok(new AddRecordResponse(0, "success", userPerformance.getId()));
    }

    @RequestMapping(value = "/performance-detail/{performanceId}", method = RequestMethod.PUT)
    public ResponseEntity<?> updatePerformanceDetail(@PathVariable Long performanceId, @RequestBody UserPerformance userPerformance) {
        logger.info("begin updatePerformanceDetail...");
        userPerformanceService.updatePerformanceDetail(performanceId, userPerformance);
        logger.info("end updatePerformanceDetail...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

    @RequestMapping(value = "/performance-detail-batch", method = RequestMethod.POST)
    public ResponseEntity<?> batchUpdatePerformanceDetail(@RequestBody List<UserPerformance> userPerformanceList, @RequestParam int year, @RequestParam int month) {
        logger.info("begin batchHandlePerformanceDetail ...");
        userPerformanceService.batchHandlePerformanceDetail(userPerformanceList, MyTokenUtils.getCurrentUser(), year, month);
        logger.info("end batchHandlePerformanceDetail ...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

    @RequestMapping(value = "/submit-self-performance/{performanceId}", method = RequestMethod.POST)
    public ResponseEntity<?> submitSelfPerformance(@PathVariable Long performanceId) {
        logger.info("begin submitSelfPerformance...");
        userPerformanceService.submitSelfPerformance(performanceId);
        logger.info("end submitSelfPerformance...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

    @RequestMapping(value = "/submit-work-group", method = RequestMethod.POST)
    public ResponseEntity<?> submitWorkGroup(@RequestBody JSONObject jsonObject) {
        logger.info("begin submitWorkGroup...");
        Long workGroupId = jsonObject.getLong("workGroupId");
        Integer year = jsonObject.getInteger("year");
        Integer month = jsonObject.getInteger("month");
        userPerformanceService.submitWorkGroup(workGroupId, year, month);
        logger.info("end submitWorkGroup...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

    @RequestMapping(value = "/confirm-performance", method = RequestMethod.POST)
    public ResponseEntity<?> confirmPerformance(@RequestBody JSONObject jsonObject) {
        logger.info("begin confirmPerformance...");
        Integer year = jsonObject.getInteger("year");
        Integer month = jsonObject.getInteger("month");
        userPerformanceService.confirmPerformance(year, month);
        logger.info("end confirmPerformance...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

    @RequestMapping(value = "/my-performance", method = RequestMethod.GET)
    public ResponseEntity<?> getUserPerformance(@RequestParam(required = false) Long userId) {
        logger.info("begin getUserPerformance...,userId:{}", userId);
        List<UserPerformance> result = userPerformanceService.getAllPerformanceByUserId(userId);
        logger.info("end getUserPerformance...");
        return ResponseEntity.ok(result);
    }

    @AccessLog(tag = "pc-performance-access", message = "执行了导出绩效操作")
    @RequestMapping(value = "/sub-performance-export", method = RequestMethod.GET)
    public ResponseEntity<?> downloadSubPerformance(@RequestParam Integer year, @RequestParam(required = false) Integer month,
                                                    @RequestParam(required = false) Long workGroupId, @RequestParam(required = false, defaultValue = UserPerformance.Performance.All) String grade) {
        logger.info("begin download sub performances...");
        String url = userPerformanceService.downloadUserPerformanceData(workGroupId, year, month, grade);
        logger.info("end download sub performances...");
        return ResponseEntity.ok(new FileResponse(0, url));
    }

    @RequestMapping(value = "/all-performance-date", method = RequestMethod.GET)
    public ResponseEntity<?> getAllYearMonth() {
        logger.info("begin getAllYearMonth...");
        List<YearMonthDto> yearMonthDtos = userPerformanceService.getAllYearMonth();
        logger.info("end getAllYearMonth...");
        return ResponseEntity.ok(yearMonthDtos);
    }

    // app 端的固化绩效，发起与项目确认接口
    @RequestMapping(value = "/submit-fix-member", method = RequestMethod.POST)
    public ResponseEntity<?> submitFmGroup(@RequestBody JSONObject jsonObject) {
        logger.info("begin submitFmGroup...");
        Integer year = jsonObject.getInteger("year");
        Integer month = jsonObject.getInteger("month");
        fixMemberService.submitFmGroup(year, month);
        logger.info("end submitFmGroup...");
        return ResponseEntity.ok(new CommonAppResponse(0, "success"));
    }

    // app 端的固化绩效接口
    @RequestMapping(value = "/fix-member-performance", method = RequestMethod.GET)
    public ResponseEntity<?> getAppFixMemberPerformance(@RequestParam(required = false) Long id, @RequestParam(required = false) Integer year,
                                                        @RequestParam(required = false) Integer month, @RequestParam(required = false) Long performanceWorkGroupId,
                                                        @RequestParam(required = false) Long projectId, @RequestParam(required = false) Long platId) {
        logger.info("begin getWebFixMemberPerformance...");
        FmGroupConfirmInfoVo fmGroupConfirmInfoParam = new FmGroupConfirmInfoVo();
        fmGroupConfirmInfoParam.setId(id);
        fmGroupConfirmInfoParam.setYear(year);
        fmGroupConfirmInfoParam.setMonth(month);
        fmGroupConfirmInfoParam.setPerformanceWorkGroupId(performanceWorkGroupId);
        fmGroupConfirmInfoParam.setProjectId(projectId);
        fmGroupConfirmInfoParam.setPlatId(platId);
        FmGroupConfirmInfoVo fmGroupConfirmInfoVo = fixMemberService.getFmGroupConfirmInfo(fmGroupConfirmInfoParam);
        logger.info("end getWebFixMemberPerformance...");
        return ResponseEntity.ok(new CommonAppResponse(0, fmGroupConfirmInfoVo));
    }

    // app 端的固化绩效接口，项目单个组确认
    @RequestMapping(value = "/confirm-fix-member/{id}", method = RequestMethod.POST)
    public ResponseEntity<?> confirmFmGroup(@PathVariable Long id) {
        logger.info("begin confirmFmGroup...");
        fixMemberService.confirmFmGroup(id);
        logger.info("end confirmFmGroup...");
        return ResponseEntity.ok(new CommonAppResponse(0, "success"));
    }

    // app 端的固化绩效接口，项目批量确认
    @RequestMapping(value = "/confirm-fix-member", method = RequestMethod.POST)
    public ResponseEntity<?> confirmAllFmGroup(@RequestBody JSONObject jsonObject) {
        logger.info("begin confirmAllFmGroup...");
        Long projectId = jsonObject.getLong("projectId");
        Integer year = jsonObject.getInteger("year");
        Integer month = jsonObject.getInteger("month");
        fixMemberService.confirmAllFmGroup(year, month, projectId);
        logger.info("end confirmAllFmGroup...");
        return ResponseEntity.ok(new CommonAppResponse(0, "success"));
    }

    // app 端的固化绩效接口 todo: 改版时间线
    @RequestMapping(value = "/fix-member", method = RequestMethod.GET)
    public ResponseEntity<?> getAppProjectFixMember(@RequestParam(required = false) Integer year, @RequestParam(required = false) Integer month) {
        logger.info("begin getAppProjectFixMember...");
        ProjectFixMemberInfoVo projectFixMemberInfoVo = fixMemberService.getFmGroupConfirmInfoListByProjectConfirmer(year, month);
        int selectIndex = UserPerformanceHelper.getSelectIndex(projectFixMemberInfoVo.getHistoryInfo().getAll(), projectFixMemberInfoVo.getHistoryInfo().getYear(), projectFixMemberInfoVo.getHistoryInfo().getMonth());
        projectFixMemberInfoVo.setSelectIndex(selectIndex);

        // 补充historyInfo
        projectFixMemberInfoVo.getHistoryInfo().setSelectIndex(selectIndex);

        logger.info("end getAppProjectFixMember...");
        return ResponseEntity.ok(new CommonAppResponse(0, projectFixMemberInfoVo));
    }

    // 查看下属工作组绩效填写进度
    @RequestMapping(value = "/sub-performance/progress", method = RequestMethod.GET)
    public ResponseEntity<?> getSubGroupPerformance(@RequestParam(required = false) Long userId, @RequestParam Integer year, @RequestParam Integer month) {
        List<SubGroupPerformanceVo> result = null;
        logger.info("begin getSubGroupPerformance...");
        result = userPerformanceService.getSubGroupPerformanceByDate(userId, year, month);
        logger.info("end getSubGroupPerformance...");
        return ResponseEntity.ok(new CommonAppResponse(0, result));
    }

    @RequestMapping(value = "/automatic-confirm", method = RequestMethod.POST)
    public ResponseEntity<?> updateUserPerformanceStatus() {
        logger.info("begin updateUserPerformanceStatus...");
        userPerformanceService.updateUserPerformanceStatus();
        logger.info("end updateUserPerformanceStatus...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }
}
