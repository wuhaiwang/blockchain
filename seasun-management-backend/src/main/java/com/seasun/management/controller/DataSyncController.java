package com.seasun.management.controller;

import com.seasun.management.controller.response.CommonResponse;
import com.seasun.management.dto.PerformanceWorkGroupDto;
import com.seasun.management.exception.ParamException;
import com.seasun.management.helper.SyncHelper;
import com.seasun.management.model.PerformanceWorkGroup;
import com.seasun.management.service.PerformanceWorkGroupService;
import com.seasun.management.service.SyncService;
import com.seasun.management.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/apis/auth/dsp-data-sync")
public class DataSyncController {

    private static final Logger logger = LoggerFactory.getLogger(DataSyncController.class);

    @Autowired
    PerformanceWorkGroupService performanceWorkGroupService;

    @RequestMapping(value = "/department", method = RequestMethod.POST)
    public ResponseEntity<?> syncDepartment(@Valid @RequestBody DepartmentSyncVo departmentSyncVo, Errors errors) {
        logger.info("begin syncDepartment...");
        checkRequest(errors);

        SyncService syncService = SyncHelper.getSyncServiceByTargetName(departmentSyncVo.getTargetName());
        CommonResponse result = syncService.run(departmentSyncVo);
        logger.info("end syncDepartment...");
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public ResponseEntity<?> syncUser(@RequestBody UserSyncVo baseSyncVo, Errors errors) {
        logger.info("begin syncUser...");
        checkRequest(errors);

        SyncService syncService = SyncHelper.getSyncServiceByTargetName(baseSyncVo.getTargetName());
        CommonResponse result = syncService.run(baseSyncVo);
        logger.info("end syncUser...");
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/user-children-info", method = RequestMethod.POST)
    public ResponseEntity<?> syncUserChildrenInfo(@RequestBody UserChildrenInfoSyncVo baseSyncVo, Errors errors) {
        logger.info("begin syncUserChildrenInfo...");
        checkRequest(errors);

        SyncService syncService = SyncHelper.getSyncServiceByTargetName(baseSyncVo.getTargetName());
        CommonResponse result = syncService.run(baseSyncVo);
        logger.info("end syncUserChildrenInfo...");
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/user-edu-experience", method = RequestMethod.POST)
    public ResponseEntity<?> syncUserEduExperience(@RequestBody UserEduExperienceSyncVo baseSyncVo, Errors errors) {
        logger.info("begin syncUserEduExperience...");
        checkRequest(errors);

        SyncService syncService = SyncHelper.getSyncServiceByTargetName(baseSyncVo.getTargetName());
        CommonResponse result = syncService.run(baseSyncVo);
        logger.info("end syncUserEduExperience...");
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/user-work-experience", method = RequestMethod.POST)
    public ResponseEntity<?> syncUserWorkExperience(@RequestBody UserWorkExperienceSyncVo baseSyncVo, Errors errors) {
        logger.info("begin syncUserWorkExperience...");
        checkRequest(errors);

        SyncService syncService = SyncHelper.getSyncServiceByTargetName(baseSyncVo.getTargetName());
        CommonResponse result = syncService.run(baseSyncVo);
        logger.info("end syncUserWorkExperience...");
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/user-certification", method = RequestMethod.POST)
    public ResponseEntity<?> syncUserCertification(@RequestBody UserCertificationSyncVo baseSyncVo, Errors errors) {
        logger.info("begin syncUserCertification...");
        checkRequest(errors);

        SyncService syncService = SyncHelper.getSyncServiceByTargetName(baseSyncVo.getTargetName());
        CommonResponse result = syncService.run(baseSyncVo);
        logger.info("end syncUserCertification...");
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/user-bank-card", method = RequestMethod.POST)
    public ResponseEntity<?> syncUserBankCard(@RequestBody UserBankCardSyncVo baseSyncVo, Errors errors) {
        logger.info("begin syncUserBankCard...");
        checkRequest(errors);

        SyncService syncService = SyncHelper.getSyncServiceByTargetName(baseSyncVo.getTargetName());
        CommonResponse result = syncService.run(baseSyncVo);
        logger.info("end syncUserBankCard...");
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/user-favorite", method = RequestMethod.POST)
    public ResponseEntity<?> syncUserFavorite(@RequestBody UserFavoriteSyncVo baseSyncVo, Errors errors) {
        logger.info("begin syncUserFavorite...");
        checkRequest(errors);

        SyncService syncService = SyncHelper.getSyncServiceByTargetName(baseSyncVo.getTargetName());
        CommonResponse result = syncService.run(baseSyncVo);
        logger.info("end syncUserFavorite...");
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/user-floor", method = RequestMethod.POST)
    public ResponseEntity<?> syncRUserFloor(@RequestBody RUserFloorSyncVo baseSyncVo, Errors errors) {
        logger.info("begin syncRUserFloor...");
        checkRequest(errors);

        SyncService syncService = SyncHelper.getSyncServiceByTargetName(baseSyncVo.getTargetName());
        CommonResponse result = syncService.run(baseSyncVo);
        logger.info("end syncRUserFloor...");
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/user-nda", method = RequestMethod.POST)
    public ResponseEntity<?> syncUserNda(@RequestBody UserNdaSyncVo baseSyncVo, Errors errors) {
        logger.info("begin syncUserNda...");
        checkRequest(errors);

        SyncService syncService = SyncHelper.getSyncServiceByTargetName(baseSyncVo.getTargetName());
        CommonResponse result = syncService.run(baseSyncVo);
        logger.info("end syncUserNda...");
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/work-group", method = RequestMethod.POST)
    public ResponseEntity<?> syncWorkGroup(@RequestBody WorkGroupSyncVo workGroupSyncVo, Errors errors) {
        logger.info("begin syncWorkGroup...");
        checkRequest(errors);

        SyncService syncService = SyncHelper.getSyncServiceByTargetName(workGroupSyncVo.getTargetName());
        CommonResponse result = syncService.run(workGroupSyncVo);
        logger.info("end syncWorkGroup...");
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/user-work-group", method = RequestMethod.POST)
    public ResponseEntity<?> syncUserGroup(@RequestBody RUserWorkGroupSyncVo userGroupSyncVo, Errors errors) {
        logger.info("begin syncUserGroup...");
        checkRequest(errors);

        SyncService syncService = SyncHelper.getSyncServiceByTargetName(userGroupSyncVo.getTargetName());
        CommonResponse result = syncService.run(userGroupSyncVo);
        logger.info("end syncUserGroup...");
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/r-user-work-group-perm", method = RequestMethod.POST)
    public ResponseEntity<?> syncGroupLeader(@RequestBody RUserWorkGroupSyncVo rUserWorkGroupSyncVo, Errors errors) {
        logger.info("begin syncGroupLeader...");
        checkRequest(errors);

        SyncService syncService = SyncHelper.getSyncServiceByTargetName(rUserWorkGroupSyncVo.getTargetName());
        CommonResponse result = syncService.run(rUserWorkGroupSyncVo);
        logger.info("end syncGroupLeader...");
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/r-user-project-perm", method = RequestMethod.POST)
    public ResponseEntity<?> syncRUserProjectPerm(@RequestBody RUserProjectPermSyncVo rUserProjectPermSyncVo, Errors errors) {
        logger.info("begin syncRUserProjectPerm...");
        checkRequest(errors);

        SyncService syncService = SyncHelper.getSyncServiceByTargetName(rUserProjectPermSyncVo.getTargetName());
        CommonResponse result = syncService.run(rUserProjectPermSyncVo);
        logger.info("end syncRUserProjectPerm...");
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/project", method = RequestMethod.POST)
    public ResponseEntity<?> syncProject(@RequestBody ProjectSyncVo projectSyncVo, Errors errors) {
        logger.info("begin syncProject...");
        checkRequest(errors);

        SyncService syncService = SyncHelper.getSyncServiceByTargetName(projectSyncVo.getTargetName());
        CommonResponse result = syncService.run(projectSyncVo);
        logger.info("end syncProject...");
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/project-role", method = RequestMethod.POST)
    public ResponseEntity<?> syncProjectRole(@RequestBody ProjectRoleSyncVo projectRoleSyncVo, Errors errors) {
        logger.info("begin syncProjectRole...");
        checkRequest(errors);

        SyncService syncService = SyncHelper.getSyncServiceByTargetName(projectRoleSyncVo.getTargetName());
        CommonResponse result = syncService.run(projectRoleSyncVo);
        logger.info("end syncProjectRole...");
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/floor", method = RequestMethod.POST)
    public ResponseEntity<?> syncFloor(@RequestBody FloorSyncVo floorSyncVo, Errors errors) {
        logger.info("begin syncFloor...");
        checkRequest(errors);

        SyncService syncService = SyncHelper.getSyncServiceByTargetName(floorSyncVo.getTargetName());
        CommonResponse result = syncService.run(floorSyncVo);
        logger.info("end syncFloor...");
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/subcompany", method = RequestMethod.POST)
    public ResponseEntity<?> syncSubCompany(@RequestBody SubCompanySyncVo subCompanySyncVo, Errors errors) {
        logger.info("begin syncSubCompany...");
        checkRequest(errors);

        SyncService syncService = SyncHelper.getSyncServiceByTargetName(subCompanySyncVo.getTargetName());
        CommonResponse result = syncService.run(subCompanySyncVo);
        logger.info("end syncSubCompany...");
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/user-transfer-post", method = RequestMethod.POST)
    public ResponseEntity<?> syncUserTransferPost(@RequestBody UserTransferPosSyncVo userTransferPosSyncVo, Errors errors) {
        logger.info("begin syncUserTransferPost...");
        checkRequest(errors);

        SyncService syncService = SyncHelper.getSyncServiceByTargetName(userTransferPosSyncVo.getTargetName());
        CommonResponse result = syncService.run(userTransferPosSyncVo);
        logger.info("end syncUserTransferPost...");
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/batch")
    public ResponseEntity<?> batchSync(@RequestBody BatchSyncVo batchSyncVo, Errors errors) {
        logger.info("begin batchSync...");
        checkRequest(errors);

        if (batchSyncVo.getRequests().size() > 50) {
            logger.info("batchSync failed...");
            throw new ParamException("参数requests超过批量同步的单次最大处理记录数50");
        }

        SyncService syncService = SyncHelper.getSyncServiceByTargetName(batchSyncVo.getTargetName());
        CommonResponse result = syncService.run(batchSyncVo);
        logger.info("end batchSync...");
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/user-performance-work-group", method = RequestMethod.POST)
    public ResponseEntity<?> syncUserPerformanceWorkGroup(@RequestBody RUserPerformanceWorkGroupSyncVo rUserPerformanceWorkGroupSyncVo, Errors errors) {
        logger.info("begin syncUserPerformanceWorkGroup...");
        checkRequest(errors);

        SyncService syncService = SyncHelper.getSyncServiceByTargetName(rUserPerformanceWorkGroupSyncVo.getTargetName());
        CommonResponse result = syncService.run(rUserPerformanceWorkGroupSyncVo);
        logger.info("end syncUserPerformanceWorkGroup...");
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/performance-work-groups", method = RequestMethod.GET)
    public ResponseEntity<?> getSyncPerformanceWorkGroups(@RequestParam(required = false) Long workGroupId, @RequestParam(required = false) Long managerId) {
        logger.info("begin getSyncPerformanceWorkGroups...");
        List<PerformanceWorkGroupDto> performanceWorkGroups;
        if (null == managerId) {
            performanceWorkGroups = performanceWorkGroupService.getPerformanceWorkGroupsByWorkGroup(workGroupId);
        } else {
            performanceWorkGroups = performanceWorkGroupService.getPerformanceWorkGroupsByManager(managerId);
        }
        logger.info("end getSyncPerformanceWorkGroups...");
        return ResponseEntity.ok(performanceWorkGroups);
    }

    private void checkRequest(Errors errors) {
        if (errors.hasErrors()) {
            throw new ParamException(errors.getFieldError().getDefaultMessage());
        }
    }
}
