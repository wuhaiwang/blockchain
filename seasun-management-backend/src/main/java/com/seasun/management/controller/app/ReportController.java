package com.seasun.management.controller.app;

import com.seasun.management.annotation.AccessLog;
import com.seasun.management.controller.response.CommonAppResponse;
import com.seasun.management.dto.AppGuiCategoryDto;
import com.seasun.management.model.IdNameBaseObject;
import com.seasun.management.service.FnProjectStatDataDetailService;
import com.seasun.management.service.CfgSystemService;
import com.seasun.management.service.PmPlanService;
import com.seasun.management.service.UserService;
import com.seasun.management.service.app.HrReportService;
import com.seasun.management.service.app.PermissionService;
import com.seasun.management.service.app.ProjectReportService;
import com.seasun.management.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/apis/auth/app")
public class ReportController {

    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);

    @Autowired
    private ProjectReportService projectReportService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private HrReportService hrReportService;

    @Autowired
    private UserService userService;

    @Autowired
    private FnProjectStatDataDetailService fnProjectStatDataDetailService;

    @Autowired
    private PmPlanService pmPlanService;

    @Autowired
    private CfgSystemService cfgSystemService;

    @RequestMapping(value = "/projects", method = RequestMethod.GET)
    public ResponseEntity<?> getAppProjects(@RequestParam String status) {
        logger.info("begin getAppProjects...");
        List<ProjectAppVo> projectAppVos = projectReportService.getAppProjects(status);
        logger.info("end getAppProjects...");
        return ResponseEntity.ok(new CommonAppResponse(0, projectAppVos));
    }

    @RequestMapping(value = "/fn-project-info", method = RequestMethod.GET)
    public ResponseEntity<?> getAppFnProjectInfo(@RequestParam Long id, @RequestParam Integer year, @RequestParam Integer month) {
        logger.info("begin getAppFnProjectInfo...");
        FnProjectInfoAppVo fnProjectInfoAppVo = projectReportService.getAppFnProjectInfo(id, year, month);
        logger.info("end getAppFnProjectInfo...");
        return ResponseEntity.ok(new CommonAppResponse(0, fnProjectInfoAppVo));
    }

    /**
     * 全年视图
     */
    @RequestMapping(value = "/fn-project-stat-yoy", method = RequestMethod.GET)
    public ResponseEntity<?> getAppFnProjectStatYoY(@RequestParam Long projectId, @RequestParam Integer year) {
        logger.info("begin getAppFnProjectStatYoY...");
        FnProjectStatYoYVO fnProjectInfoAppVo = projectReportService.getAppFnProjectStatYoY(projectId, year);
        logger.info("end getAppFnProjectStatYoY...");
        return ResponseEntity.ok(new CommonAppResponse(0, fnProjectInfoAppVo));
    }

    @RequestMapping(value = "/fn-project-stat-data-details", method = RequestMethod.GET)
    public ResponseEntity<?> getFnProjectStatDataDetails(@RequestParam Long projectStatDataId) {
        logger.info("begin getFnProjectStatDataDetails...");
        List<FnProjectStatDataDetailVo> fnProjectStatDataDetailList = fnProjectStatDataDetailService.getDetailById(projectStatDataId);
        logger.info("end getFnProjectStatDataDetails...");
        return ResponseEntity.ok(new CommonAppResponse(0, fnProjectStatDataDetailList));
    }

    @RequestMapping(value = "/project-share", method = RequestMethod.GET)
    public ResponseEntity<?> getAppProjectShare(@RequestParam Long id, @RequestParam Integer year, @RequestParam Integer month) {
        logger.info("begin getAppProjectShare...");
        FnProjectShareAppVo fnProjectShareAppVo = projectReportService.getAppProjectShare(id, year, month);
        logger.info("end getAppProjectShare...");
        return ResponseEntity.ok(new CommonAppResponse(0, fnProjectShareAppVo));
    }

    @RequestMapping(value = "/project-share-detail", method = RequestMethod.GET)
    public ResponseEntity<?> getAppProjectShareDetail(@RequestParam Long id, @RequestParam Integer year, @RequestParam Integer month) {
        logger.info("begin getAppProjectShareDetail...");
        List<FnProjectShareDetailVo> detailVos = projectReportService.getAppProjectShareDetail(id, year, month);
        logger.info("end getAppProjectShareDetail...");
        return ResponseEntity.ok(new CommonAppResponse(0, detailVos));
    }

    @RequestMapping(value = "/plats", method = RequestMethod.GET)
    public ResponseEntity<?> getAppPlats() {
        logger.info("begin getAppPlats...");
        List<PlatAppVo> platAppVos = projectReportService.getAppPlats();
        logger.info("end getAppPlats...");
        return ResponseEntity.ok(new CommonAppResponse(0, platAppVos));
    }

    @RequestMapping(value = "/plat-cost", method = RequestMethod.GET)
    public ResponseEntity<?> getAppPlatCost(@RequestParam Long id, @RequestParam Integer year, @RequestParam Integer month) {
        logger.info("begin getAppPlatCost...");
        FnPlatCostAppVo fnPlatCostAppVo = projectReportService.getAppPlatCost(id, year, month);
        logger.info("end getAppPlatCost...");
        return ResponseEntity.ok(new CommonAppResponse(0, fnPlatCostAppVo));
    }

    @RequestMapping(value = "/plat-share", method = RequestMethod.GET)
    public ResponseEntity<?> getAppPlatShare(@RequestParam Long id, @RequestParam Integer year, @RequestParam Integer month) {
        logger.info("begin getAppPlatShare...");
        FnPlatShareAppVo fnShareAppVo = projectReportService.getAppPlatShare(id, year, month);
        logger.info("end getAppPlatShare...");
        return ResponseEntity.ok(new CommonAppResponse(0, fnShareAppVo));
    }

    @Deprecated
    @RequestMapping(value = "/org-project-info", method = RequestMethod.GET)
    public ResponseEntity<?> getAppOrgProjectInfo(@RequestParam Long id) {
        logger.info("begin getAppOrgProjectInfo...");
        OrgProjectInfoAppVo orgProjectInfoAppVo = projectReportService.getAppOrgProjectInfo(id);
        logger.info("end getAppOrgProjectInfo...");
        return ResponseEntity.ok(new CommonAppResponse(0, orgProjectInfoAppVo));
    }

    @Deprecated
    @RequestMapping(value = "/org-project-work-group-info", method = RequestMethod.GET)
    public ResponseEntity<?> getAppOrgProjectWorkGroupInfo(@RequestParam Long projectId, @RequestParam Long groupId) {
        logger.info("begin getAppOrgProjectWorkGroupInfo...");
        OrgProjectWorkGroupInfoAppVo orgProjectWorkGroupInfoAppVo = projectReportService.getAppOrgProjectWorkGroupInfo(projectId, groupId);
        logger.info("end getAppOrgProjectWorkGroupInfo...");
        return ResponseEntity.ok(new CommonAppResponse(0, orgProjectWorkGroupInfoAppVo));
    }

    @RequestMapping(value = "/home-info", method = RequestMethod.GET)
    public ResponseEntity<?> getAppHomeInfo() {
        logger.info("begin getAppHomeInfo...");
        HomeInfoAppVo homeInfoAppVo = permissionService.getAppHomeInfo();
        logger.info("end getAppHomeInfo...");
        return ResponseEntity.ok(new CommonAppResponse(0, homeInfoAppVo));
    }

    @RequestMapping(value = "/permissions/{module}", method = RequestMethod.GET)
    public ResponseEntity<?> getAppPermissions(@PathVariable String module) {
        logger.info("begin getAppPermissions...");
        List<String> permList = permissionService.getAppPermissionList(module);
        logger.info("end getAppPermissions...");
        return ResponseEntity.ok(new CommonAppResponse(0, permList));
    }

    @RequestMapping(value = "/hr-overview", method = RequestMethod.GET)
    public ResponseEntity<?> getAppHrOverview() {
        logger.info("begin getAppHrOverview...");
        List<HrOverviewAppVo> hrOverviewAppVos = hrReportService.getAppHrOverview();
        logger.info("end getAppHrOverview...");
        return ResponseEntity.ok(new CommonAppResponse(0, hrOverviewAppVos));
    }

    @RequestMapping(value = "/hr-project", method = RequestMethod.GET)
    public ResponseEntity<?> getAppHrProject() {
        logger.info("begin getAppHrProject...");
        List<HrOverviewAppVo> hrOverviewAppVos = hrReportService.getAppHrProject();
        logger.info("end getAppHrProject...");
        return ResponseEntity.ok(new CommonAppResponse(0, hrOverviewAppVos));
    }

    @RequestMapping(value = "/hr-plat", method = RequestMethod.GET)
    public ResponseEntity<?> getAppHrPlat() {
        logger.info("begin getAppHrPlat...");
        List<HrOverviewAppVo> hrOverviewAppVos = hrReportService.getAppHrPlat();
        logger.info("end getAppHrPlat...");
        return ResponseEntity.ok(new CommonAppResponse(0, hrOverviewAppVos));
    }

    @RequestMapping(value = "/hr-user-transaction-location", method = RequestMethod.GET)
    public ResponseEntity<?> getAppHrUserTransactionByLocation(@RequestParam String location, @RequestParam Integer year, @RequestParam Integer month) {
        logger.info("begin getAppHrUserTransactionByLocation...");
        HrUserTransactionAppVo hrUserTransactionAppVo = hrReportService.getHrUserTransactionByLocation(location, year, month);
        logger.info("end getAppHrUserTransactionByLocation...");
        return ResponseEntity.ok(new CommonAppResponse(0, hrUserTransactionAppVo));
    }

    @RequestMapping(value = "/hr-user-transaction-project", method = RequestMethod.GET)
    public ResponseEntity<?> getAppHrUserTransactionByProject(@RequestParam Long projectId, @RequestParam Integer year, @RequestParam Integer month) {
        logger.info("begin getAppHrUserTransactionByProject...");
        HrUserTransactionAppVo hrUserTransactionAppVo = hrReportService.getHrUserTransactionByProject(projectId, year, month);
        logger.info("end getAppHrUserTransactionByProject...");
        return ResponseEntity.ok(new CommonAppResponse(0, hrUserTransactionAppVo));
    }

    @RequestMapping(value = "/hr-user-analysis-location", method = RequestMethod.GET)
    public ResponseEntity<?> getAppHrUserAnalysisByLocation(@RequestParam String location, @RequestParam Integer year, @RequestParam Integer month) {
        logger.info("begin getAppHrUserAnalysisByLocation...");
        HrPersonnelAnalysisAppVo hrPersonnelAnalysisAppVo = hrReportService.getHrPersonnelAnalysisByLocation(location, year, month);
        logger.info("end getAppHrUserAnalysisByLocation...");
        return ResponseEntity.ok(new CommonAppResponse(0, hrPersonnelAnalysisAppVo));
    }

    @RequestMapping(value = "/hr-user-analysis-project", method = RequestMethod.GET)
    public ResponseEntity<?> getAppHrUserAnalysisByProject(@RequestParam Long projectId, @RequestParam Integer year, @RequestParam Integer month) {
        logger.info("begin getAppHrUserAnalysisByProject...");
        HrPersonnelAnalysisAppVo hrPersonnelAnalysisAppVo = hrReportService.getHrPersonnelAnalysisByProject(projectId, year, month);
        logger.info("end getAppHrUserAnalysisByProject...");
        return ResponseEntity.ok(new CommonAppResponse(0, hrPersonnelAnalysisAppVo));
    }

    @RequestMapping(value = "/fn-summary-project", method = RequestMethod.GET)
    public ResponseEntity<?> getAppFnSummaryProject() {
        logger.info("begin getAppFnSummaryProject...");
        List<FnSummaryProjectAppVo> fnSummaryProjectAppVos = projectReportService.getAppSummaryProjects();
        logger.info("end getAppFnSummaryProject...");
        return ResponseEntity.ok(new CommonAppResponse(0, fnSummaryProjectAppVos));
    }

    @RequestMapping(value = "/hr-user-info", method = RequestMethod.GET)
    public ResponseEntity<?> getAppUserInfo(@RequestParam(required = false) Long userId, @RequestParam(required = false) Boolean getSeatFlag) {
        logger.info("begin getAppUserInfo...");
        UserInfoAppVo userInfoAppVo = userService.getAppUserInfo(userId, getSeatFlag);
        logger.info("end getAppUserInfo...");
        return ResponseEntity.ok(new CommonAppResponse(0, userInfoAppVo));
    }

    @RequestMapping(value = "/hr-contacts", method = RequestMethod.GET)
    public ResponseEntity<?> getAppContacts(@RequestParam(required = false) Long groupId) {
        logger.info("begin getAppContacts...");
        ContactsAppVo contactsAppVo = hrReportService.getContacts(groupId);
        logger.info("end getAppContacts...");
        return ResponseEntity.ok(new CommonAppResponse(0, contactsAppVo));
    }

    @RequestMapping(value = "/hr-contacts-project", method = RequestMethod.GET)
    public ResponseEntity<?> getAppContactsByProject(@RequestParam(required = true) Long projectId, @RequestParam(required = false) Long groupId) {
        logger.info("begin getAppContacts...");
        ContactsAppVo contactsAppVo = hrReportService.getContactsByProjectId(projectId, groupId);
        logger.info("end getAppContacts...");
        return ResponseEntity.ok(new CommonAppResponse(0, contactsAppVo));
    }

    @RequestMapping(value = "/pm-projects", method = RequestMethod.GET)
    public ResponseEntity<?> getAppPmProjectList() {
        logger.info("begin getAppPmPlanList...");
        List<IdNameBaseObject> contactsAppVo = pmPlanService.getAppPmPlanList();
        logger.info("end getAppPmPlanList...");
        return ResponseEntity.ok(new CommonAppResponse(0, contactsAppVo));
    }

    @RequestMapping(value = "/pm-project", method = RequestMethod.GET)
    public ResponseEntity<?> getAppPmPlanDetail(Long projectId) {
        logger.info("begin getAppPmPlanDetail...");
        PmPlanDetailAppVo contactsAppVo = pmPlanService.getProjectPublishedPlanDetails(projectId);
        logger.info("end getAppPmPlanDetail...");
        return ResponseEntity.ok(new CommonAppResponse(0, contactsAppVo));
    }

    @RequestMapping(value = "/discovery-info", method = RequestMethod.GET)
    public ResponseEntity<?> getAppDiscoveryInfo() {
        logger.info("begin getAppDiscoveryInfo...");
        AppGuiCategoryDto result = cfgSystemService.getAppDiscoveryInfo();
        logger.info("end getAppDiscoveryInfo...");
        return ResponseEntity.ok(new CommonAppResponse(0, result));
    }

    @RequestMapping(value = "/simple-user-info", method = RequestMethod.GET)
    public ResponseEntity<?> getAppSimpleUserInfo() {
        logger.info("begin getAppSimpleUserInfo...");
        SimpleUserInfoVo result = userService.getSimpleUserInfo();
        logger.info("end getAppSimpleUserInfo...");
        return ResponseEntity.ok(new CommonAppResponse(0, result));
    }

}
