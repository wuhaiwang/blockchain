package com.seasun.management.service.impl;

import com.seasun.management.constant.SyncType;
import com.seasun.management.exception.ParamException;
import com.seasun.management.helper.PermissionHelper;
import com.seasun.management.mapper.ProjectRoleMapper;
import com.seasun.management.model.ProjectRole;
import com.seasun.management.service.ProjectRoleService;
import com.seasun.management.vo.BaseSyncVo;
import com.seasun.management.vo.ProjectRoleSyncVo;
import com.seasun.management.vo.ProjectRoleVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectRoleServiceImpl extends AbstractSyncService implements ProjectRoleService {

    @Autowired
    ProjectRoleMapper projectRoleMapper;

    @Override
    public void sync(BaseSyncVo baseSyncVo) {

        if (!(baseSyncVo instanceof ProjectRoleSyncVo)) {
            throw new ClassCastException("baseSynVo对象 不能转换为 ProjectRoleSyncVo 类");
        }
        ProjectRoleSyncVo projectRoleSyncVo = (ProjectRoleSyncVo) baseSyncVo;
        if (null == projectRoleSyncVo.getData().getId()) {
            throw new ParamException("id can not be null");
        }

        if (projectRoleSyncVo.getType().equals(SyncType.add)) {
            projectRoleMapper.insertWithId(projectRoleSyncVo.getData());
        } else if (projectRoleSyncVo.getType().equals(SyncType.update)) {
            projectRoleMapper.updateByPrimaryKeySelective(projectRoleSyncVo.getData());
        } else if (projectRoleSyncVo.getType().equals(SyncType.delete)) {
            projectRoleMapper.deleteByPrimaryKey(projectRoleSyncVo.getData().getId());
        }
    }

    @Override
    public ProjectRoleVo getAllActiveProjectRoles() {
        ProjectRoleVo result = new ProjectRoleVo();
        List<ProjectRole> allProjectRoles = projectRoleMapper.selectAllProjectRoles();
        List<ProjectRoleVo.ProjectRoleInfo> systemRoles = new ArrayList<>();
        List<ProjectRoleVo.ProjectRoleInfo> projectRoles = new ArrayList<>();
        List<ProjectRoleVo.ProjectRoleInfo> fixRoles = new ArrayList<>();
        for (ProjectRole projectRole : allProjectRoles) {

            // 系统角色
            if (projectRole.getSystemFlag().equals(1)) {

                // 忽略项目负责人,该角色在人力资源配置界面维护
                if (projectRole.getName().equals("项目负责人")) {
                    continue;
                }

                ProjectRoleVo.ProjectRoleInfo projectRoleInfo = new ProjectRoleVo.ProjectRoleInfo();
                BeanUtils.copyProperties(projectRole, projectRoleInfo);

                // 系统角色中某个项目特有的角色
                if (projectRole.getName().equals("后台财务分摊管理") || projectRole.getName().equals("APP项目负责人")) {
                    projectRoleInfo.setProjectUseFlag(true);
                } else {
                    projectRoleInfo.setProjectUseFlag(false);
                }
                systemRoles.add(projectRoleInfo);
            }
            // 项目角色
            else if (projectRole.getSystemFlag().equals(0)) {
                ProjectRoleVo.ProjectRoleInfo projectRoleInfo = new ProjectRoleVo.ProjectRoleInfo();
                BeanUtils.copyProperties(projectRole, projectRoleInfo);
                projectRoles.add(projectRoleInfo);
            } else {
                ProjectRoleVo.ProjectRoleInfo projectRoleInfo = new ProjectRoleVo.ProjectRoleInfo();
                BeanUtils.copyProperties(projectRole, projectRoleInfo);
                fixRoles.add(projectRoleInfo);
            }
        }
        result.setProjectRoles(projectRoles);
        result.setSystemRoles(systemRoles);
        result.setFixRoles(fixRoles);
        return result;
    }
}
