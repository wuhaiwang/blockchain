package com.seasun.management.service.permission;

import com.seasun.management.constant.MenuConstant;
import com.seasun.management.dto.MenuPermDto;
import com.seasun.management.dto.RMenuProjectRolePermDto;
import com.seasun.management.mapper.CfgPlatAttrMapper;
import com.seasun.management.mapper.RMenuProjectRolePermMapper;
import com.seasun.management.mapper.RUserProjectPermMapper;
import com.seasun.management.model.Menu;
import com.seasun.management.model.ProjectRole;
import com.seasun.management.model.RUserProjectPerm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class RUserProjectPermCollector extends PermissionCollector {

    @Autowired
    private RMenuProjectRolePermMapper rMenuProjectRolePermMapper;

    @Autowired
    private CfgPlatAttrMapper cfgPlatAttrMapper;

    @Autowired
    private RUserProjectPermMapper rUserProjectPermMapper;

    @Override
    public List<? extends MenuPermDto> getMenuPermsByUserId(Long userId) {
        List<RMenuProjectRolePermDto> result = rMenuProjectRolePermMapper.selectAllByUserId(Menu.Type.pc, userId);

        // 排除未开启精确分摊的平台分摊负责人获取到周分摊模块
        boolean weekShareFlag = false;
        for (RMenuProjectRolePermDto item : result) {
            if (MenuConstant.weekly_plat_share.equals(item.getKey())) {
                weekShareFlag = true;
                break;
            }
        }
        if (weekShareFlag) {
            List<RUserProjectPerm> rUserProjectPerms = rUserProjectPermMapper.selectByUserId(userId);
            boolean allPlatManagerFlag = false;
            Set<Long> platIdSet = new HashSet<>();
            for (RUserProjectPerm item : rUserProjectPerms) {
               if(ProjectRole.Role.plat_share_manager_id.equals(item.getProjectRoleId()) || ProjectRole.Role.pm_manager_id.equals(item.getProjectRoleId())){
                   if (item.getProjectId() == null) {
                       allPlatManagerFlag = true;
                       break;
                   } else {
                       platIdSet.add(item.getProjectId());
                   }
               }
            }

            Integer platCount;
            if (allPlatManagerFlag) {
                platCount = cfgPlatAttrMapper.selectCountDetailPlat();
            } else {
                platCount = cfgPlatAttrMapper.selectDetailPlatCountByPlatIds(new ArrayList<>(platIdSet));
            }
            if (platCount == null || platCount == 0) {
                Iterator<RMenuProjectRolePermDto> iterator = result.iterator();
                while (iterator.hasNext()) {
                    RMenuProjectRolePermDto next = iterator.next();
                    if (MenuConstant.weekly_plat_share.equals(next.getKey())) {
                        iterator.remove();
                    }
                }
            }

        }
        return result == null ? new ArrayList<>() : result;
    }
}
