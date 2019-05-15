package com.seasun.management.service.impl;

import com.seasun.management.constant.ErrorCode;
import com.seasun.management.controller.response.AddRecordResponse;
import com.seasun.management.dto.RMenuProjectRolePermUsersDto;
import com.seasun.management.mapper.ProjectMapper;
import com.seasun.management.mapper.ProjectRoleMapper;
import com.seasun.management.mapper.RMenuProjectRolePermMapper;
import com.seasun.management.model.Project;
import com.seasun.management.model.ProjectRole;
import com.seasun.management.model.RMenuProjectRolePerm;
import com.seasun.management.service.RMenuProjectRolePermService;
import com.seasun.management.vo.RMenuProjectRolePermVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RMenuProjectRolePermServiceImpl implements RMenuProjectRolePermService {

    @Autowired
    RMenuProjectRolePermMapper rMenuProjectRolePermMapper;

    @Autowired
    ProjectRoleMapper projectRoleMapper;

    @Autowired
    ProjectMapper projectMapper;

    Logger logger = LoggerFactory.getLogger(RMenuProjectRolePermServiceImpl.class);

    @Override
    public RMenuProjectRolePermVo getListByProjectRoleId(final Long roleId) {
        RMenuProjectRolePermVo rMenuProjectRolePermVo = new RMenuProjectRolePermVo();
        rMenuProjectRolePermVo.setMenus(rMenuProjectRolePermMapper.selectMenusByProjectRoleId(roleId));
        if (isProjectFlag(roleId)) rMenuProjectRolePermVo.setProjects(this.mapRoleProjectToProjectRole(roleId));
        else                       rMenuProjectRolePermVo.setProjects(this.mapRoleProjectToSystemRole(roleId));
        return rMenuProjectRolePermVo;
    }

    private List<RMenuProjectRolePermVo.ProjectRoleProjectVo> mapRoleProjectToProjectRole (Long roleId) {

        List<Project> projects = projectMapper.selectAll();

        List<RMenuProjectRolePermUsersDto> rMenuProjectRolePermUsersDtoList = rMenuProjectRolePermMapper.selectMenusUsersByProjectRoleId(roleId);

        Map<Long, RMenuProjectRolePermVo.ProjectRoleProjectVo> projectRoleProjectVoMap = new HashMap<>();

        Map<Long, List<RMenuProjectRolePermVo.ProjectRoleUserVo>> projectRoleUserVoListMap = new HashMap<>();

        List<RMenuProjectRolePermVo.ProjectRoleProjectVo> projectRoleProjectVoList = new LinkedList<>();


        /**
         *
         * projectRoleUserVoListMap 结构:
         * {
         *     "1":[],
         *     "2":[]
         * }
         *
         * projectRoleProjectVoMap 结构:
         *
         * {
         *
         *     "projectId" : {
         *         "projectName":"",
         *         "projectId": ""
         *     },
         *     "projectId" : {
         *           "projectName":"",
         *            "projectId": ""
         *      },
         *
         *
         * }
         *
         *
         *
         * */

        projects.stream().forEach(project -> {
            projectRoleUserVoListMap.put(project.getId(), new LinkedList<>());
            RMenuProjectRolePermVo.ProjectRoleProjectVo projectRoleProjectVo = new RMenuProjectRolePermVo.ProjectRoleProjectVo();
            projectRoleProjectVo.setProjectId(project.getId());
            projectRoleProjectVo.setProjectName(project.getName());
            projectRoleProjectVoMap.put (project.getId(), projectRoleProjectVo);
        });

        /**
         * 所有项目的project_id 为null
         * */
        projectRoleUserVoListMap.put (null, new LinkedList<>());

        RMenuProjectRolePermVo.ProjectRoleProjectVo temp =  new RMenuProjectRolePermVo.ProjectRoleProjectVo();

        temp.setProjectName("所有项目");

        projectRoleProjectVoMap.put (null, temp);

        /**
         *
         * projectRoleUserVoListMap 结构
         * {
         *     "1":[{user}, {user}],
         *     "2";[],
         *     "3":[{user}]
         * }
         *
         *
         * */

        rMenuProjectRolePermUsersDtoList.stream().forEach(dto->{
            List<RMenuProjectRolePermVo.ProjectRoleUserVo> users = projectRoleUserVoListMap.get(dto.getProjectId());
            logger.debug ("users -> {}, dto.getProjectId() -> {}", users, dto.getProjectId());
            users.add (this.buildProjectRoleUserVo(dto));
        });


       for (Map.Entry<Long, List<RMenuProjectRolePermVo.ProjectRoleUserVo>> entry: projectRoleUserVoListMap.entrySet()) {
           RMenuProjectRolePermVo.ProjectRoleProjectVo projectRoleProjectVo =  projectRoleProjectVoMap.get(entry.getKey());
           logger.debug ("entry.getKey() -> {}, projectRoleProjectVo -> {}, entry.getValue() -> {}", entry.getKey(), projectRoleProjectVo,entry.getValue());
           projectRoleProjectVo.setUsers(entry.getValue());
           projectRoleProjectVoList.add (projectRoleProjectVo);
       }

        projectRoleProjectVoList.sort((a ,b )-> {
             if (a.getUsers().size() == b.getUsers().size()) return 0;
             else if (a.getUsers().size() < b.getUsers().size()) return 1;
             return -1;
        });

       return projectRoleProjectVoList;

    }

    private List<RMenuProjectRolePermVo.ProjectRoleProjectVo> mapRoleProjectToSystemRole (Long roleId) {
        List<RMenuProjectRolePermUsersDto> rMenuProjectRolePermUsersDtoList = rMenuProjectRolePermMapper.selectMenusUsersByProjectRoleId(roleId);
        List<RMenuProjectRolePermVo.ProjectRoleProjectVo> projectRoleProjectVoList = new LinkedList<>();
        RMenuProjectRolePermVo.ProjectRoleProjectVo projectRoleProjectVo = new RMenuProjectRolePermVo.ProjectRoleProjectVo();
        projectRoleProjectVo.setProjectName(null);
        projectRoleProjectVo.setProjectId(null);
        projectRoleProjectVo.setUsers(new LinkedList<>());
        /**
         * 用户防重校验
         * */

        Map<String, String> map = new HashMap<>();
        rMenuProjectRolePermUsersDtoList.stream().forEach(dto -> {
            logger.debug ("getLoginId -> {}", dto.getLoginId());
            if (!map.containsKey(dto.getLoginId())) {
                projectRoleProjectVo.getUsers().add (this.buildProjectRoleUserVo(dto));
                map.put(dto.getLoginId(), dto.getLoginId());
            }
        });
        projectRoleProjectVoList.add (projectRoleProjectVo);
        return projectRoleProjectVoList;
    }


    private RMenuProjectRolePermVo.ProjectRoleUserVo buildProjectRoleUserVo (RMenuProjectRolePermUsersDto dto) {
        RMenuProjectRolePermVo.ProjectRoleUserVo user = new RMenuProjectRolePermVo.ProjectRoleUserVo();
        user.setUserId(dto.getUserId());
        user.setId(dto.getId());
        user.setLoginId(dto.getLoginId());
        user.setPhoto(dto.getPhoto());
        user.setUserName(dto.getUserName());
        return  user;
    }

    private Boolean isProjectFlag (Long roleId) {
        ProjectRole projectRole = projectRoleMapper.selectByPrimaryKey(roleId);
        return projectRole.getSystemFlag() == 0 ? Boolean.TRUE : Boolean.FALSE;
    }


    @Override
    public AddRecordResponse insertProjectRoleMenu(Long projectRoleId, List<Long> menuIds) {
        logger.info("addProjectRoleMenu , projectRoleId -> {}, menuId -> {}", projectRoleId, menuIds);
        try{
            rMenuProjectRolePermMapper.insertBatch(projectRoleId, menuIds);
            return new AddRecordResponse(0, "添加成功", null);
        }catch(Exception e) {
            logger.error("insertProjectRoleMenu error -> {} ", e.getMessage());
            return new AddRecordResponse(ErrorCode.PARAM_ERROR, "添加失败", null);
        }
    }

    @Override
    public Boolean deleteProjectRoleMenu(Long projectRoleId, Long menuId) {
        logger.info("deleteProjectRoleMenu , projectRoleId -> {}, menuId -> {}", projectRoleId, menuId);
        int result =  rMenuProjectRolePermMapper.deleteByMenuIdAndProjectRoleId(projectRoleId, menuId) ;
        logger.info("deleteProjectRoleMenu  projectRoleId -> {}, menuId -> {}", projectRoleId, menuId);
        return result == 1 ? Boolean.TRUE : Boolean.FALSE;
    }

}
