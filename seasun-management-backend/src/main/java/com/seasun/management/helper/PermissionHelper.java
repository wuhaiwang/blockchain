package com.seasun.management.helper;


import com.seasun.management.dto.PermissionProjectDto;
import com.seasun.management.dto.RUserProjectPermDto;
import com.seasun.management.mapper.RUserProjectPermMapper;
import com.seasun.management.util.MyBeanUtils;
import com.seasun.management.util.MyTokenUtils;

import java.util.*;

public class PermissionHelper {

    public interface SystemRole {
        Long BACKEND_PROJECT_MANAGEMENT_ID = 1L;
        Long BACKEND_MANAGEMENT_ID = 2L;
        Long BACKEND_HR_ORG_ID = 3L;
        Long BACKEND_FINANCE_DATA_ID = 4L;
        Long BACKEND_FINANCE_SHARE_MANAGER_ID = 5L;
        Long BACKEND_FINANCE_SHARE_PEOPLE_ID = 6L;
    }

    public interface AppRole {
        long APP_MANAGEMENT_ID = 100l;
        long APP_PROJECT_MANAGEMENT_ID = 101l;
        long APP_HR_ID = 102l;
        long APP_FINANCE_ID = 103l;
        long APP_PESIDENT_OFFICE =104L;
        long APP_Plat_OB = 105L;
    }

    public interface AppHomeInfoModuleName {
        String project = "project";
        String platform = "platform";
        String finance = "finance";
        String hr = "hr";
        String outsourcing = "outsourcing";
        String salary = "salary";
        String grade = "grade";
        String projectMaxMemberFlow = "project-max-member-flow";
    }

    public static PermissionProjectDto getPermissionByProject() {
        RUserProjectPermMapper rUserProjectPermMapper = MyBeanUtils.getBean(RUserProjectPermMapper.class);

        Long userId = MyTokenUtils.getCurrentUserId();
        boolean allPerm = false;
        List<Long> permProjectIds = new ArrayList<>();
        List<RUserProjectPermDto> rUserProjectPermVos = rUserProjectPermMapper.selectAppPermByUserId(userId);
        for (RUserProjectPermDto rUserProjectPermDto : rUserProjectPermVos) {
            if (!rUserProjectPermDto.getProjectRoleId().equals(AppRole.APP_MANAGEMENT_ID)
                    && !rUserProjectPermDto.getProjectRoleId().equals(AppRole.APP_PROJECT_MANAGEMENT_ID)
                    && !rUserProjectPermDto.getProjectRoleId().equals(AppRole.APP_PESIDENT_OFFICE)) {
                continue;
            }

            if (rUserProjectPermDto.getProjectRoleId().equals(AppRole.APP_MANAGEMENT_ID)) {
                allPerm = true;
            } else if (rUserProjectPermDto.getProjectRoleId().equals(AppRole.APP_PROJECT_MANAGEMENT_ID)
                    && null == rUserProjectPermDto.getProjectId()) {
                allPerm = true;
            } else if (rUserProjectPermDto.getProjectRoleId().equals(AppRole.APP_PESIDENT_OFFICE)){
                allPerm = true;
            }
            permProjectIds.add(rUserProjectPermDto.getProjectId());
        }

        PermissionProjectDto permissionProjectDto = new PermissionProjectDto();
        permissionProjectDto.setAllPerm(allPerm);
        permissionProjectDto.setPermProjectIds(permProjectIds);

        return permissionProjectDto;
    }

    public static PermissionProjectDto getPermissionByFinance() {
        RUserProjectPermMapper rUserProjectPermMapper = MyBeanUtils.getBean(RUserProjectPermMapper.class);

        Long userId = MyTokenUtils.getCurrentUserId();
        boolean allPerm = false;
        List<Long> permProjectIds = new ArrayList<>();
        List<RUserProjectPermDto> rUserProjectPermVos = rUserProjectPermMapper.selectAppPermByUserId(userId);
        for (RUserProjectPermDto rUserProjectPermDto : rUserProjectPermVos) {
            if (!rUserProjectPermDto.getProjectRoleId().equals(AppRole.APP_MANAGEMENT_ID)
                    && !rUserProjectPermDto.getProjectRoleId().equals(AppRole.APP_FINANCE_ID)
                    && !rUserProjectPermDto.getProjectRoleId().equals(AppRole.APP_PROJECT_MANAGEMENT_ID)) {
                continue;
            }

           if (rUserProjectPermDto.getProjectRoleId().equals(AppRole.APP_MANAGEMENT_ID)) {
                allPerm = true;
            } else if (rUserProjectPermDto.getProjectRoleId().equals(AppRole.APP_FINANCE_ID)
                    && null == rUserProjectPermDto.getProjectId()) {
                allPerm = true;
            }
            permProjectIds.add(rUserProjectPermDto.getProjectId());
        }

        PermissionProjectDto permissionProjectDto = new PermissionProjectDto();
        permissionProjectDto.setAllPerm(allPerm);
        permissionProjectDto.setPermProjectIds(permProjectIds);

        return permissionProjectDto;
    }

}
