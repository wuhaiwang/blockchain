package com.seasun.management.helper;

import com.alibaba.fastjson.JSONObject;
import com.seasun.management.constant.SyncTarget;
import com.seasun.management.service.*;
import com.seasun.management.util.MyBeanUtils;
import com.seasun.management.vo.*;

import java.util.HashMap;
import java.util.Map;

public class SyncHelper {

    private static final Map<String, Class> targetServiceMap = new HashMap();
    private static final Map<String, Class> targetVoMap = new HashMap();

    static {
        // all service
        targetServiceMap.put(SyncTarget.user.toString(), UserService.class);
        targetServiceMap.put(SyncTarget.work_group.toString(), WorkGroupService.class);
        targetServiceMap.put(SyncTarget.user_work_group.toString(), RUserWorkGroupService.class);//组员
        targetServiceMap.put(SyncTarget.user_children_info.toString(), UserChildrenInfoService.class);
        targetServiceMap.put(SyncTarget.user_edu_experience.toString(), UserEduExperienceService.class);
        targetServiceMap.put(SyncTarget.user_work_experience.toString(), UserWorkExperienceService.class);
        targetServiceMap.put(SyncTarget.user_certification.toString(), UserCertificationService.class);
        targetServiceMap.put(SyncTarget.user_bank_card.toString(), UserBankCardService.class);
        targetServiceMap.put(SyncTarget.user_favorite.toString(), UserFavoriteService.class);
        targetServiceMap.put(SyncTarget.user_nda.toString(), UserNdaService.class);
        targetServiceMap.put(SyncTarget.department.toString(), DepartmentService.class);
        targetServiceMap.put(SyncTarget.r_user_project_perm.toString(), RUserProjectPermService.class);
        targetServiceMap.put(SyncTarget.r_user_work_group_perm.toString(), RUserWorkGroupService.class);//组长
        targetServiceMap.put(SyncTarget.project.toString(), ProjectService.class);
        targetServiceMap.put(SyncTarget.project_role.toString(), ProjectRoleService.class);
        targetServiceMap.put(SyncTarget.batch.toString(), BatchSyncService.class);
        targetServiceMap.put(SyncTarget.floor.toString(), FloorService.class);
        targetServiceMap.put(SyncTarget.subcompany.toString(), SubCompanyService.class);
        targetServiceMap.put(SyncTarget.user_floor.toString(), RUserFloorService.class);
        targetServiceMap.put(SyncTarget.user_transfer_post.toString(), UserTransferPosService.class);
        targetServiceMap.put(SyncTarget.user_performance_work_group.toString(), PerformanceWorkGroupService.class);

        // all request vo
        targetVoMap.put(SyncTarget.user.toString(), UserSyncVo.class);
        targetVoMap.put(SyncTarget.work_group.toString(), WorkGroupSyncVo.class);
        targetVoMap.put(SyncTarget.user_work_group.toString(), RUserWorkGroupSyncVo.class);
        targetVoMap.put(SyncTarget.user_children_info.toString(), UserChildrenInfoSyncVo.class);
        targetVoMap.put(SyncTarget.user_edu_experience.toString(), UserEduExperienceSyncVo.class);
        targetVoMap.put(SyncTarget.user_work_experience.toString(), UserWorkExperienceSyncVo.class);
        targetVoMap.put(SyncTarget.user_certification.toString(), UserCertificationSyncVo.class);
        targetVoMap.put(SyncTarget.user_bank_card.toString(), UserBankCardSyncVo.class);
        targetVoMap.put(SyncTarget.user_favorite.toString(), UserFavoriteSyncVo.class);
        targetVoMap.put(SyncTarget.user_nda.toString(), UserNdaSyncVo.class);
        targetVoMap.put(SyncTarget.department.toString(), DepartmentSyncVo.class);
        targetVoMap.put(SyncTarget.r_user_project_perm.toString(), RUserProjectPermSyncVo.class);
        targetVoMap.put(SyncTarget.r_user_work_group_perm.toString(), RUserWorkGroupSyncVo.class);
        targetVoMap.put(SyncTarget.project.toString(), ProjectSyncVo.class);
        targetVoMap.put(SyncTarget.project_role.toString(), ProjectRoleSyncVo.class);
        targetVoMap.put(SyncTarget.floor.toString(), FloorSyncVo.class);
        targetVoMap.put(SyncTarget.subcompany.toString(), SubCompanySyncVo.class);
        targetVoMap.put(SyncTarget.user_floor.toString(), RUserFloorSyncVo.class);
        targetVoMap.put(SyncTarget.user_transfer_post.toString(), UserTransferPosSyncVo.class);
        targetVoMap.put(SyncTarget.user_performance_work_group.toString(), RUserPerformanceWorkGroupSyncVo.class);
    }

    public static SyncService getSyncServiceByTargetName(String targetName) {
        Class syncServiceImpl = targetServiceMap.get(targetName);

        SyncService syncService = (SyncService) MyBeanUtils.getBean(syncServiceImpl);
        return syncService;
    }

    public static BaseSyncVo getSyncVoByTargetName(String targetName, JSONObject data) {
        BaseSyncVo syncVo = (BaseSyncVo) data.toJavaObject(targetVoMap.get(targetName));
        return syncVo;
    }
}
