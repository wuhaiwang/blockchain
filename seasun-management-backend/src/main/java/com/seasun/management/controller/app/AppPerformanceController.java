package com.seasun.management.controller.app;

import com.alibaba.fastjson.JSONObject;
import com.seasun.management.controller.response.AddRecordResponse;
import com.seasun.management.controller.response.CommonAppResponse;
import com.seasun.management.dto.PerformanceHistoryDto;
import com.seasun.management.helper.UserPerformanceHelper;
import com.seasun.management.model.UserPerformance;
import com.seasun.management.service.FixMemberService;
import com.seasun.management.service.PerformanceObserverService;
import com.seasun.management.service.UserPerformanceService;
import com.seasun.management.service.WorkGroupService;
import com.seasun.management.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/apis/auth/app/user-performance")
public class AppPerformanceController {

    private static final Logger logger = LoggerFactory.getLogger(AppPerformanceController.class);

    @Autowired
    WorkGroupService workGroupService;

    @Autowired
    PerformanceObserverService performanceObserverService;

    @Autowired
    FixMemberService fixMemberService;

    @Autowired
    private UserPerformanceService userPerformanceService;

    @RequestMapping(value = "/identity", method = RequestMethod.GET)
    public ResponseEntity<?> getAppUserPerformanceIdentity(@RequestParam long userType) {
        logger.info("begin getAppUserPerformanceIdentity...");
        UserPerformanceIdentityAppVo userPerformanceIdentityAppVo = workGroupService.getUserPerformanceIdentity(userType);
        logger.info("end getAppUserPerformanceIdentity...");
        return ResponseEntity.ok(new CommonAppResponse(0, userPerformanceIdentityAppVo));
    }


    // todo: 改版时间线
    @RequestMapping(value = "/sub-performance", method = RequestMethod.GET)
    public ResponseEntity<?> getAppSubPerformance(@RequestParam(required = false) Integer year, @RequestParam(required = false) Integer month, @RequestParam(required = false) String filter,
                                                  @RequestParam(required = false) Integer startYear,
                                                  @RequestParam(required = false) Integer startMonth,
                                                  @RequestParam(required = false) Integer endYear,
                                                  @RequestParam(required = false) Integer endMonth) {
        logger.info("begin getAppSubPerformance...");
        if (UserPerformanceHelper.SearchFilter.fixmember.equals(filter)) {
            SubFixMemberPerformanceVo fixMemberResult = fixMemberService.getSubFixMemberPerformance(year, month);
            PerformanceHistoryDto historyTimeLine = UserPerformanceHelper.buildHistoryTimeLineIndex(year, month, fixMemberResult.getHistory(), startYear, startMonth, endYear, endMonth);
            fixMemberResult.setStartIndex(historyTimeLine.getStartIndex());
            fixMemberResult.setEndIndex(historyTimeLine.getEndIndex());
            fixMemberResult.setSelectIndex(historyTimeLine.getSelectIndex());

            // 补充historyInfo
            ProjectFixMemberInfoVo.History historyInfo = new ProjectFixMemberInfoVo.History();
            historyInfo.setAll(fixMemberResult.getHistory());
            historyInfo.setSelectIndex(fixMemberResult.getSelectIndex());
            historyInfo.setStartIndex(fixMemberResult.getStartIndex());
            historyInfo.setEndIndex(fixMemberResult.getEndIndex());
            fixMemberResult.setHistoryInfo(historyInfo);
            logger.info("end getAppFixMemberSubPerformance...");
            return ResponseEntity.ok(new CommonAppResponse(0, fixMemberResult));
        } else {
            SubPerformanceAppVo performanceResult = userPerformanceService.getSubPerformance(year, month, filter);
            PerformanceHistoryDto historyTimeLine = UserPerformanceHelper.buildHistoryTimeLineIndex(year, month, performanceResult.getHistory(), startYear, startMonth, endYear, endMonth);
            performanceResult.setStartIndex(historyTimeLine.getStartIndex());
            performanceResult.setEndIndex(historyTimeLine.getEndIndex());
            performanceResult.setSelectIndex(historyTimeLine.getSelectIndex());

            // 补充historyInfo
            ProjectFixMemberInfoVo.History historyInfo = new ProjectFixMemberInfoVo.History();
            historyInfo.setAll(performanceResult.getHistory());
            historyInfo.setSelectIndex(performanceResult.getSelectIndex());
            historyInfo.setStartIndex(performanceResult.getStartIndex());
            historyInfo.setEndIndex(performanceResult.getEndIndex());
            performanceResult.setHistoryInfo(historyInfo);
            logger.info("end getAppSubPerformance...");
            return ResponseEntity.ok(new CommonAppResponse(0, performanceResult));
        }
    }


    // todo: 改版后，没有调用过
    @RequestMapping(value = "/sub-performance/history", method = RequestMethod.GET)
    public ResponseEntity<?> getAppSubPerformanceHistory() {
        logger.info("begin getAppSubPerformance...");
        List<SubPerformanceAppVo.HistoryInfo> historyInfos = userPerformanceService.getWorkGroupHistory();
        logger.info("end getAppSubPerformance...");
        return ResponseEntity.ok(new CommonAppResponse(0, historyInfos));
    }

    @RequestMapping(value = "/member-performance", method = RequestMethod.GET)
    public ResponseEntity<?> getAppMemberPerformance(@RequestParam Long workGroupId, @RequestParam Integer year, @RequestParam Integer month) {
        logger.info("begin getAppMemberPerformance...");
        WorkGroupMemberPerformanceAppVo workGroupMemberPerformanceAppVo = userPerformanceService.getWorkGroupMemberPerformance(workGroupId, year, month);
        logger.info("end getAppMemberPerformance...");
        return ResponseEntity.ok(new CommonAppResponse(0, workGroupMemberPerformanceAppVo));
    }

    // todo: 改版时间线
    @RequestMapping(value = "/performance-detail", method = RequestMethod.GET)
    public ResponseEntity<?> getAppPerformanceDetail(@RequestParam(required = false) Long userId,
                                                     @RequestParam(required = false) Integer year,
                                                     @RequestParam(required = false) Integer month,
                                                     @RequestParam(required = false) Integer startYear,
                                                     @RequestParam(required = false) Integer startMonth,
                                                     @RequestParam(required = false) Integer endYear,
                                                     @RequestParam(required = false) Integer endMonth) {
        logger.info("begin getAppPerformanceDetail...");
        UserPerformanceDetailAppVo userPerformanceDetailAppVo = userPerformanceService.getUserPerformanceDetail(userId, year, month);
        List<SubPerformanceBaseVo.HistoryInfo> originalHistoryList = new ArrayList<>();
        for (SubPerformanceBaseVo.HistoryInfo historyInfo : userPerformanceDetailAppVo.getHistory()) {
            SubPerformanceBaseVo.HistoryInfo temp = new SubPerformanceBaseVo.HistoryInfo();
            temp.setYear(historyInfo.getYear());
            temp.setMonth(historyInfo.getMonth());
            temp.setStatus(historyInfo.getStatus());
            originalHistoryList.add(temp);
        }
        PerformanceHistoryDto historyTimeLine = UserPerformanceHelper.buildHistoryTimeLineIndex(year, month, originalHistoryList, startYear, startMonth, endYear, endMonth);
        userPerformanceDetailAppVo.setStartIndex(historyTimeLine.getStartIndex());
        userPerformanceDetailAppVo.setEndIndex(historyTimeLine.getEndIndex());
        userPerformanceDetailAppVo.setSelectIndex(historyTimeLine.getSelectIndex());

        // 补充historyInfo
        ProjectFixMemberInfoVo.History historyInfo = new ProjectFixMemberInfoVo.History();
        historyInfo.setAll(userPerformanceDetailAppVo.getHistory());
        historyInfo.setSelectIndex(userPerformanceDetailAppVo.getSelectIndex());
        historyInfo.setStartIndex(userPerformanceDetailAppVo.getStartIndex());
        historyInfo.setEndIndex(userPerformanceDetailAppVo.getEndIndex());
        userPerformanceDetailAppVo.setHistoryInfo(historyInfo);

        logger.info("end getAppPerformanceDetail...");
        return ResponseEntity.ok(new CommonAppResponse(0, userPerformanceDetailAppVo));
    }

    @RequestMapping(value = "/performance-detail/history", method = RequestMethod.GET)
    public ResponseEntity<?> getAppPerformanceDetail(@RequestParam(required = false) Long userId) {
        logger.info("begin getAppPerformanceDetail...");
        boolean isMyself = userId == null;
        List<SubPerformanceBaseVo.HistoryInfo> historyInfos = userPerformanceService.getUserPerformanceHistory(userId, isMyself);
        logger.info("end getAppPerformanceDetail...");
        return ResponseEntity.ok(new CommonAppResponse(0, historyInfos));
    }

    @RequestMapping(value = "/performance-detail", method = RequestMethod.POST)
    public ResponseEntity<?> addAppPerformanceDetail(@RequestBody UserPerformance userPerformance) {
        logger.info("begin addAppPerformanceDetail...");
        userPerformanceService.addPerformanceDetail(userPerformance);
        logger.info("end addAppPerformanceDetail...");
        return ResponseEntity.ok(new AddRecordResponse(0, "success", userPerformance.getId()));
    }

    @RequestMapping(value = "/performance-detail/{performanceId}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateAppPerformanceDetail(@PathVariable Long performanceId, @RequestBody UserPerformance userPerformance) {
        logger.info("begin updateAppPerformanceDetail...");
        userPerformanceService.updatePerformanceDetail(performanceId, userPerformance);
        logger.info("end updateAppPerformanceDetail...");
        return ResponseEntity.ok(new CommonAppResponse(0, "success"));
    }

    @RequestMapping(value = "/submit-self-performance/{performanceId}", method = RequestMethod.POST)
    public ResponseEntity<?> submitSelfPerformance(@PathVariable Long performanceId) {
        logger.info("begin submitSelfPerformance...");
        userPerformanceService.submitSelfPerformance(performanceId);
        logger.info("end submitSelfPerformance...");
        return ResponseEntity.ok(new CommonAppResponse(0, "success"));
    }

    @RequestMapping(value = "/submit-work-group", method = RequestMethod.POST)
    public ResponseEntity<?> submitWorkGroup(@RequestBody JSONObject jsonObject) {
        logger.info("begin submitWorkGroup...");
        Long workGroupId = null;
        if (jsonObject.containsKey("workGroupId")) {
            workGroupId = jsonObject.getLong("workGroupId");
        }
        Integer year = jsonObject.getInteger("year");
        Integer month = jsonObject.getInteger("month");
        userPerformanceService.submitWorkGroup(workGroupId, year, month);
        logger.info("end submitWorkGroup...");
        return ResponseEntity.ok(new CommonAppResponse(0, "success"));
    }

    @RequestMapping(value = "/confirm-performance", method = RequestMethod.POST)
    public ResponseEntity<?> confirmPerformance(@RequestBody JSONObject jsonObject) {
        logger.info("begin confirmPerformance...");
        Integer year = jsonObject.getInteger("year");
        Integer month = jsonObject.getInteger("month");
        userPerformanceService.confirmPerformance(year, month);
        logger.info("end confirmPerformance...");
        return ResponseEntity.ok(new CommonAppResponse(0, "success"));
    }

    @RequestMapping(value = "/observer-performance", method = RequestMethod.GET)
    public ResponseEntity<?> getAppObserverPerformance(@RequestParam(required = false) Long userId,
                                                       @RequestParam(required = false) Long performanceWorkGroupId,
                                                       @RequestParam(required = false) Integer year, @RequestParam(required = false) Integer month,
                                                       @RequestParam(required = false) Integer startYear, @RequestParam(required = false) Integer startMonth,
                                                       @RequestParam(required = false) Integer endYear, @RequestParam(required = false) Integer endMonth,
                                                       @RequestParam(required = false) String filter) {
        logger.info("begin getAppObserverPerformance...");
        SubPerformanceAppVo subPerformanceAppVo = userPerformanceService.getObserverPerformance(userId, performanceWorkGroupId, year, month, filter);

        PerformanceHistoryDto historyTimeLine = UserPerformanceHelper.buildHistoryTimeLineIndex(year, month, subPerformanceAppVo.getHistory(), startYear, startMonth, endYear, endMonth);

        // 补充historyInfo
        ProjectFixMemberInfoVo.History historyInfo = new ProjectFixMemberInfoVo.History();
        historyInfo.setAll(subPerformanceAppVo.getHistory());
        historyInfo.setSelectIndex(historyTimeLine.getSelectIndex());
        historyInfo.setStartIndex(historyTimeLine.getStartIndex());
        historyInfo.setEndIndex(historyTimeLine.getEndIndex());
        subPerformanceAppVo.setHistoryInfo(historyInfo);

        logger.info("end getAppObserverPerformance...");
        return ResponseEntity.ok(new CommonAppResponse(0, subPerformanceAppVo));
    }

    @RequestMapping(value = "/observer-member-performance", method = RequestMethod.GET)
    public ResponseEntity<?> getAppMemberPerformance(@RequestParam(required = false) Long observerUserId, @RequestParam(required = false) Long observerWorkGroupId, @RequestParam Long workGroupId, @RequestParam Integer year, @RequestParam Integer month) {
        logger.info("begin getAppMemberPerformance...");
        WorkGroupMemberPerformanceAppVo workGroupMemberPerformanceAppVo = userPerformanceService.getObserverWorkGroupMemberPerformance(observerUserId, observerWorkGroupId, workGroupId, year, month);
        logger.info("end getAppMemberPerformance...");
        return ResponseEntity.ok(new CommonAppResponse(0, workGroupMemberPerformanceAppVo));
    }

    @RequestMapping(value = "/observer", method = RequestMethod.GET)
    public ResponseEntity<?> getAppObserverList() {
        logger.info("begin getAppObserverList...");
        List<PerformanceObserverVo> performanceObserverVos = performanceObserverService.getObserverList();
        logger.info("end getAppObserverList...");
        return ResponseEntity.ok(new CommonAppResponse(0, performanceObserverVos));
    }

    // todo： 改版时间线
    @RequestMapping(value = "/fix-member", method = RequestMethod.GET)
    public ResponseEntity<?> getAppProjectFixMember(@RequestParam(required = false) Integer year, @RequestParam(required = false) Integer month,
                                                    @RequestParam(required = false) Integer startYear,
                                                    @RequestParam(required = false) Integer startMonth,
                                                    @RequestParam(required = false) Integer endYear,
                                                    @RequestParam(required = false) Integer endMonth) {
        logger.info("begin getAppProjectFixMember...");
        ProjectFixMemberInfoVo projectFixMemberInfoVo = fixMemberService.getFmGroupConfirmInfoListByProjectConfirmer(year, month);
        PerformanceHistoryDto historyTimeLine = UserPerformanceHelper.buildHistoryTimeLineIndex(year, month, projectFixMemberInfoVo.getHistoryInfo().getAll(),
                startYear, startMonth, endYear, endMonth);
        projectFixMemberInfoVo.setStartIndex(historyTimeLine.getStartIndex());
        projectFixMemberInfoVo.setEndIndex(historyTimeLine.getEndIndex());
        projectFixMemberInfoVo.setSelectIndex(historyTimeLine.getSelectIndex());

        // 补充historyInfo
        projectFixMemberInfoVo.getHistoryInfo().setStartIndex(historyTimeLine.getStartIndex());
        projectFixMemberInfoVo.getHistoryInfo().setSelectIndex(historyTimeLine.getSelectIndex());
        projectFixMemberInfoVo.getHistoryInfo().setEndIndex(historyTimeLine.getEndIndex());

        logger.info("end getAppProjectFixMember...");
        return ResponseEntity.ok(new CommonAppResponse(0, projectFixMemberInfoVo));
    }

    @RequestMapping(value = "/fix-member-performance", method = RequestMethod.GET)
    public ResponseEntity<?> getAppFixMemberPerformance(@RequestParam(required = false) Long id, @RequestParam(required = false) Integer year,
                                                        @RequestParam(required = false) Integer month, @RequestParam(required = false) Long performanceWorkGroupId,
                                                        @RequestParam(required = false) Long projectId, @RequestParam(required = false) Long platId) {
        logger.info("begin getAppFixMemberPerformance...");
        FmGroupConfirmInfoVo fmGroupConfirmInfoParam = new FmGroupConfirmInfoVo();
        fmGroupConfirmInfoParam.setId(id);
        fmGroupConfirmInfoParam.setYear(year);
        fmGroupConfirmInfoParam.setMonth(month);
        fmGroupConfirmInfoParam.setPerformanceWorkGroupId(performanceWorkGroupId);
        fmGroupConfirmInfoParam.setProjectId(projectId);
        fmGroupConfirmInfoParam.setPlatId(platId);
        FmGroupConfirmInfoVo fmGroupConfirmInfoVo = fixMemberService.getFmGroupConfirmInfo(fmGroupConfirmInfoParam);
        logger.info("end getAppFixMemberPerformance...");
        return ResponseEntity.ok(new CommonAppResponse(0, fmGroupConfirmInfoVo));
    }

    @RequestMapping(value = "/submit-fix-member", method = RequestMethod.POST)
    public ResponseEntity<?> submitFmGroup(@RequestBody JSONObject jsonObject) {
        logger.info("begin submitFmGroup...");
        Integer year = jsonObject.getInteger("year");
        Integer month = jsonObject.getInteger("month");
        fixMemberService.submitFmGroup(year, month);
        logger.info("end submitFmGroup...");
        return ResponseEntity.ok(new CommonAppResponse(0, "success"));
    }

    // 固化绩效组确认
    @RequestMapping(value = "/confirm-fix-member/{id}", method = RequestMethod.POST)
    public ResponseEntity<?> confirmFmGroup(@PathVariable Long id) {
        logger.info("begin confirmFmGroup...");
        fixMemberService.confirmFmGroup(id);
        logger.info("end confirmFmGroup...");
        return ResponseEntity.ok(new CommonAppResponse(0, "success"));
    }

    // 固化绩效组全部确认
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
}
