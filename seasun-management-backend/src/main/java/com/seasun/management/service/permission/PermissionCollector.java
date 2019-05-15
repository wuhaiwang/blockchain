package com.seasun.management.service.permission;


import com.seasun.management.constant.WebModule;
import com.seasun.management.dto.MenuPermDto;
import com.seasun.management.model.User;
import com.seasun.management.vo.UserPermissionsVo;

import java.util.ArrayList;
import java.util.List;

abstract class PermissionCollector {

    abstract List<? extends MenuPermDto> getMenuPermsByUserId(Long userId);

    public boolean hasPermission(Long userId, String state) {
        List<? extends MenuPermDto> perms = getMenuPermsByUserId(userId);
        return perms.stream().anyMatch(p -> p.getKey().equals(state));
    }

    public UserPermissionsVo collectPermission(User currentUser) {
        List<? extends MenuPermDto> menuPermDtos = getMenuPermsByUserId(currentUser.getId());

        // 前端控制子系统是否显示，需要划分多个模块
        UserPermissionsVo vo = new UserPermissionsVo();
        List<String> orgPerms = new ArrayList<>();
        List<String> finPerms = new ArrayList<>();
        List<String> perfPerms = new ArrayList<>();
        List<String> commPerms = new ArrayList<>();
        List<String> pmPerms = new ArrayList<>();
        List<String> cpPerms = new ArrayList<>();
        for (MenuPermDto rMenuProjectRolePermDto : menuPermDtos) {
            switch (rMenuProjectRolePermDto.getModule()) {
                case WebModule.org: {
                    if (!orgPerms.contains(rMenuProjectRolePermDto.getKey())) {
                        orgPerms.add(rMenuProjectRolePermDto.getKey());
                    }
                    break;
                }
                case WebModule.finance: {
                    if (!finPerms.contains(rMenuProjectRolePermDto.getKey())) {
                        finPerms.add(rMenuProjectRolePermDto.getKey());
                    }
                    break;
                }
                case WebModule.common: {
                    if (!commPerms.contains(rMenuProjectRolePermDto.getKey())) {
                        commPerms.add(rMenuProjectRolePermDto.getKey());
                    }
                    break;
                }
                case WebModule.perf: {
                    if (!perfPerms.contains(rMenuProjectRolePermDto.getKey())) {
                        perfPerms.add(rMenuProjectRolePermDto.getKey());
                    }
                    break;
                }
                case WebModule.pm: {
                    if (!pmPerms.contains(rMenuProjectRolePermDto.getKey())) {
                        pmPerms.add(rMenuProjectRolePermDto.getKey());
                    }
                    break;
                }
                case WebModule.cp: {
                    if (!cpPerms.contains(rMenuProjectRolePermDto.getKey())) {
                        cpPerms.add(rMenuProjectRolePermDto.getKey());
                    }
                    break;
                }
                default:
                    break;
            }
        }
        vo.setFinPerms(finPerms);
        vo.setOrgPerms(orgPerms);
        vo.setPerfPerms(perfPerms);
        vo.setCommPerms(commPerms);
        vo.setPmPerms(pmPerms);
        vo.setCpPerms(cpPerms);
        return vo;

    }

}

