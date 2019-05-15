package com.seasun.management.service.impl;

import com.seasun.management.constant.ErrorMessage;
import com.seasun.management.constant.SyncType;
import com.seasun.management.dto.SubProjectInfo;
import com.seasun.management.exception.ParamException;
import com.seasun.management.mapper.*;
import com.seasun.management.model.*;
import com.seasun.management.service.OrgMaxMemberChangeLogService;
import com.seasun.management.service.ProjectService;
import com.seasun.management.util.MyTokenUtils;
import com.seasun.management.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl extends AbstractSyncService implements ProjectService {

    private static final Logger logger = LoggerFactory.getLogger(DepartmentServiceImpl.class);

    @Autowired
    ProjectMapper projectMapper;

    @Autowired
    OrderCenterMapper orderCenterMapper;

    @Autowired
    ProjectUsedNameMapper projectUsedNameMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    RUserProjectPermMapper rUserProjectPermMapper;

    @Value("${project.icon.url}")
    private String projectLogoUrl;

    @Value("${file.sys.prefix}")
    private String fileSysPrefix;

    @Autowired
    OrgMaxMemberChangeLogService orgMaxMemberChangeLogService;

    private static final Long PROJECT_MANAGER_ROLE_ID = 1L;

    private void setProjectUserList(ProjectVo projectVo, String userIdsString, List<UserSelectVo> userList, String keyName) {
        if (StringUtils.hasText(userIdsString)) {
            String[] userIds = userIdsString.split(",");

            for (String userId : userIds) {
                for (IdNameBaseObject user : userList) {
                    if (user.getId().toString().equals(userId)) {
                        switch (keyName) {
                            case "ManageUser":
                                projectVo.getManageUserList().add(user);
                                break;
                            case "HrUser":
                                projectVo.getHrUserList().add(user);
                                break;
                            default:
                                //do nothing
                                break;
                        }
                        break;
                    }
                }
            }
        }
    }

    private void setProjectUserList(ProjectVo projectVo, List<UserSelectVo> allActiveUsers) {
        setProjectUserList(projectVo, projectVo.getManagerIds(), allActiveUsers, "ManageUser");

        setProjectUserList(projectVo, projectVo.getHrList(), allActiveUsers, "HrUser");
    }

    @Override
    public List<ProjectVo> getAllProject() {
        List<ProjectVo> projectVos = projectMapper.selectAllWithOrderCodesStrAndUsedNamesStr();

        List<UserSelectVo> userList = userMapper.selectAllActiveUserSelectVo();

        for (ProjectVo project : projectVos) {
            setProjectUserList(project, userList);
        }

        return projectVos;
    }

    @Override
    public List<MiniProjectVo> getAllPermProject() {
        return projectMapper.selectAllMiniProject();
    }

    @Override
    public List<Project> getProjectByCond(Project projectCond) {
        return projectMapper.selectByCondition(projectCond);
    }

    @Override
    public ProjectVo getProjectById(Long projectId) {
        ProjectVo projectVo = projectMapper.selectWithOrderCodesStrAndUsedNamesStrByPrimaryKey(projectId);

        List<UserSelectVo> userList = userMapper.selectAllActiveUserSelectVo();

        setProjectUserList(projectVo, userList);

        return projectVo;
    }

    private void addOrUpdateProjectManager(Long projectId, String[] managerIds) {
        if (managerIds.length > 0) {
            Map<String, Object> params = new HashMap<>();
            params.put("projectId", projectId);
            params.put("userIds", managerIds);

            rUserProjectPermMapper.deleteByProjectIdAndUserIdsNotIn(params);
        }

        List<RUserProjectPerm> rUserProjectPermList = rUserProjectPermMapper.selectManagersByProjectId(projectId);
        List<RUserProjectPerm> rUserProjectPermListNeedInsert = new ArrayList<>();
        for (String userId : managerIds) {
            Boolean aExists = false;
            for (RUserProjectPerm rUserProjectPerm : rUserProjectPermList) {
                if (rUserProjectPerm.getUserId().toString().equals(userId)) {
                    aExists = true;
                    break;
                }
            }

            if (!aExists) {
                RUserProjectPerm rUserProjectPerm = new RUserProjectPerm();
                rUserProjectPerm.setProjectId(projectId);
                rUserProjectPerm.setUserId(Long.parseLong(userId));
                rUserProjectPerm.setProjectRoleId(PROJECT_MANAGER_ROLE_ID);

                rUserProjectPermListNeedInsert.add(rUserProjectPerm);

            }
        }

        if (rUserProjectPermListNeedInsert.size() > 0) {
            rUserProjectPermMapper.batchInsert(rUserProjectPermListNeedInsert);
        }
    }

    private void addOrUpdateProjectManagerByName(Long projectId, String[] managers) {
        List<UserSelectVo> allUsers = userMapper.selectAllActiveUserSelectVo();
        List<String> userIds = new ArrayList<>();
        for (String userObj : managers) {
            for (UserSelectVo userVo : allUsers) {
                if (userObj.equals(userVo.getName()) || userObj.equals(userVo.getLoginId())) {
                    userIds.add(userVo.getId().toString());
                    break;
                }
            }
        }

        addOrUpdateProjectManager(projectId, userIds.toArray(new String[userIds.size()]));
    }

    private void addOrUpdateOrderCenterCode(Long projectId, String orderCenterCodes) {
        if (StringUtils.hasText(orderCenterCodes)) {
            String[] codes = orderCenterCodes.toUpperCase().split(",");
            Map<String, Object> params = new HashMap<>();
            params.put("projectId", projectId);
            params.put("codes", codes);

            //根据projectId查询该项目order_code数据,把code不在codes中的数据projectId置为null
            orderCenterMapper.removeByProjectIdAndCodesNotIn(params);

            List<OrderCenter> orderCenterList = orderCenterMapper.selectAll();
            List<OrderCenter> orderCenterListNeedInsert = new ArrayList<>();
            List<String> orderCenterListNeedUpdate = new ArrayList<>();
            for (String code : codes) {
                Boolean aExists = false;
                for (OrderCenter orderCenter : orderCenterList) {
                    if (code.equals(orderCenter.getCode())) {
                        aExists = true;

                        //订单号一样，项目不一样的做更新处理
                        if (!projectId.equals(orderCenter.getProjectId())) {
                            orderCenterListNeedUpdate.add(code);
                        }

                        break;
                    }
                }

                if (!aExists) {
                    OrderCenter newOrderCenter = new OrderCenter();
                    newOrderCenter.setCode(code);
                    newOrderCenter.setProjectId(projectId);
                    orderCenterListNeedInsert.add(newOrderCenter);
                }
            }

            if (orderCenterListNeedInsert.size() > 0) {
                orderCenterMapper.batchInsert(orderCenterListNeedInsert);
            }
            if (orderCenterListNeedUpdate.size() > 0) {
                Map<String, Object> paramsUpdate = new HashMap<>();
                paramsUpdate.put("projectId", projectId);
                paramsUpdate.put("codes", orderCenterListNeedUpdate);

                orderCenterMapper.updateProjectByCodesIn(paramsUpdate);
            }
        } else {
            orderCenterMapper.removeByProjectId(projectId);
        }
    }

    private void addOrUpdateProjectUsedNames(Long projectId, String usedNamesString) {
        if (StringUtils.hasText(usedNamesString)) {
            String[] usedNames = usedNamesString.split(",");
            Map<String, Object> params = new HashMap<>();
            params.put("projectId", projectId);
            params.put("usedNames", usedNames);

            projectUsedNameMapper.deleteByProjectIdAndUsedNamesNotIn(params);

            List<ProjectUsedName> projectUsedNames = projectUsedNameMapper.selectByProject(projectId);
            List<ProjectUsedName> projectUsedNamesNeedInsert = new ArrayList<>();
            for (String usedName : usedNames) {
                Boolean aExists = false;
                for (ProjectUsedName projectUsedName : projectUsedNames) {
                    if (projectUsedName.getName().equals(usedName)) {
                        aExists = true;
                        break;
                    }
                }
                if (!aExists) {
                    ProjectUsedName projectUsedName = new ProjectUsedName();
                    projectUsedName.setName(usedName);
                    projectUsedName.setProjectId(projectId);

                    projectUsedNamesNeedInsert.add(projectUsedName);
                }
            }
            if (!projectUsedNamesNeedInsert.isEmpty()) {
                projectUsedNameMapper.batchInsert(projectUsedNamesNeedInsert);
            }
        } else {
            projectUsedNameMapper.deleteByProjectId(projectId);
        }
    }

    @Override
    public void addProject(ProjectVo projectVo) {
        //处理基础字段
        if (StringUtils.hasText(projectVo.getParentShareName())) {
            Long parentShareId = projectMapper.selectByUsedName(projectVo.getParentShareName()).getId();
            projectVo.setParentShareId(parentShareId);
        }
        projectMapper.insertSelective(projectVo);

        //处理负责人
        if (StringUtils.hasText(projectVo.getManagerIds())) {
            addOrUpdateProjectManager(projectVo.getId(), projectVo.getManagerIds().split(","));
        }

        //处理订单编码
        addOrUpdateOrderCenterCode(projectVo.getId(), projectVo.getOrderCenterCodesStr());

        //处理曾用名
        addOrUpdateProjectUsedNames(projectVo.getId(), projectVo.getUsedNamesStr());
    }

    @Override
    public void updateProject(ProjectVo projectVo) {
        //处理基础字段
        if (StringUtils.hasText(projectVo.getParentShareName())) {
            Project projectShare = projectMapper.selectByUsedName(projectVo.getParentShareName());
            if (null != projectShare) {
                projectVo.setParentShareId(projectShare.getId());
            }
        } else {
            projectVo.setParentShareId(null);
        }
        projectVo.setUpdateTime(new Date());


        if (!projectVo.getActiveFlag() && !projectVo.getName().endsWith("-废弃")) {
            projectVo.setName(projectVo.getName() + "-废弃");
        } else if (projectVo.getActiveFlag() && projectVo.getName().endsWith("-废弃")) {
            projectVo.setName(projectVo.getName().substring(0, projectVo.getName().length() - 3));
        }
        projectMapper.updateByPrimaryKey(projectVo);

        //处理负责人
        if (StringUtils.hasText(projectVo.getManagerIds())) {
            addOrUpdateProjectManager(projectVo.getId(), projectVo.getManagerIds().split(","));
        } else {
            rUserProjectPermMapper.deleteByProjectIdAndProjectRoleId(projectVo.getId(), ProjectRole.Role.project_manager_id);
        }

        //处理订单编码
        addOrUpdateOrderCenterCode(projectVo.getId(), projectVo.getOrderCenterCodesStr());

        //处理曾用名
        addOrUpdateProjectUsedNames(projectVo.getId(), projectVo.getUsedNamesStr());
    }

    @Override
    public void disableProject(Long projectId) {
        projectMapper.disableByPrimaryKey(projectId);

        //处理订单编码
        //orderCenterMapper.removeByProjectId(projectId);//2017-7-10 何大海 因为统计人时会过滤非激活的项目，所以不把订单编号关联的项目置为NULL也不会有问题，所以就把这个逻辑去掉算了
    }

    @Override
    public void activeProject(Long projectId) {
        Project project = projectMapper.selectByPrimaryKey(projectId);
        if (!project.getActiveFlag()) {
            project.setActiveFlag(true);

            String projectName = project.getName();
            if (projectName.endsWith("-废弃")) {
                project.setName(projectName.substring(0, projectName.length() - 3));
            }

            projectMapper.updateByPrimaryKeySelective(project);
        }
    }

    @Override
    public List<ProjectVo> getSharePlats() {
        List<ProjectVo> projectVos = projectMapper.selectAllSharePlat();
        return projectVos;
    }

    @Override
    public String uploadProjectLogo(MultipartFile file, Long projectId) {
        if (file.isEmpty()) {
            throw new ParamException("文件为空");
        }
        try {
            String fileName = file.getOriginalFilename();
            String prefix = fileName.substring(fileName.lastIndexOf("."));
            String newLogoName = getNewLogoName(projectId);
            String url = projectLogoUrl + "/" + newLogoName + prefix;
            String filePath = fileSysPrefix + url;
            file.transferTo(new File(filePath));
            return url;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ParamException("文件保存失败");
        }
    }

    @Override
    public SubProjectInfo getSubProjectById(Long projectId) {
        SubProjectInfo subProjectInfo = new SubProjectInfo();
        List<Project> allProject = projectMapper.selectAllProject();
        Project project = allProject.stream().filter(p -> p.getId().equals(projectId)).collect(Collectors.toList()).get(0);
        if (project.getId().equals(5080L) || project.getName().equals("汇总-大西山居")) { // 汇总大西山居的id,写死为5080或汇总-大西山居
            subProjectInfo.setType(SubProjectInfo.Type.all);
        } else if ("西米".equals(project.getName())) {
            subProjectInfo.setType(SubProjectInfo.Type.project);
            subProjectInfo.getSubProjects().add(project);
        } else if (Project.ServiceLine.summary.equals(project.getServiceLine()) || Project.Id.SEASUN_WORLD_GAME.equals(project.getId())) {
            subProjectInfo.setType(SubProjectInfo.Type.summary);
            setSubProject(subProjectInfo.getSubProjects(), allProject, project);
        } else {
            subProjectInfo.setType(SubProjectInfo.Type.project);
            subProjectInfo.getSubProjects().add(project);
        }
        return subProjectInfo;
    }

    private void setSubProject(List<Project> subProjects, List<Project> allProject, Project parentProject) {
        for (Project project : allProject) {
            if (parentProject.getId().equals(project.getParentFnSumId())) {
                if (Project.ServiceLine.summary.equals(project.getServiceLine()) || Project.Id.SEASUN_WORLD_GAME.equals(project.getId())) {
                    setSubProject(subProjects, allProject, project);
                } else {
                    subProjects.add(project);

                }
            }
        }
    }


    private String getNewLogoName(Long projectId) {
        Project oldProject = projectMapper.selectByPrimaryKey(projectId);
        String oldLogoName = oldProject.getLogo();
        String splitString = "project_icon";
        if (StringUtils.isEmpty(oldLogoName)) {
            return projectId + "v1";
        }

        if (oldLogoName.split("\\.")[0].split(splitString)[1].split("v").length == 1) {
            return projectId + "v1";
        }

        int versionId = Integer.parseInt(oldLogoName.split("\\.")[0].split(splitString)[1].split("v")[1]);
        versionId = versionId + 1;
        return projectId + "v" + versionId;
    }

    @Override
    public void sync(BaseSyncVo baseSyncVo) {

        if (!(baseSyncVo instanceof ProjectSyncVo)) {
            throw new ClassCastException("baseSynVo对象 不能转换为 ProjectSyncVo 类");
        }
        ProjectSyncVo projectSyncVo = (ProjectSyncVo) baseSyncVo;
        ProjectSyncVo.ProjectInfo projectNew = projectSyncVo.getData();
        projectNew.setHrFlag(true);
        if (null == projectNew.getId()) {
            throw new ParamException("id can not be null...");
        }

        //同步project
        //DSP那边的项目负责人为存在项目上的联系人，所以不需要单独处理了
        if (projectSyncVo.getType().equals(SyncType.add)) {
            projectMapper.insertSelectiveWithId(projectNew);

            //处理订单编码
            addOrUpdateOrderCenterCode(projectNew.getId(), projectNew.getOrderCenterCode());

            //处理负责人
            if (StringUtils.hasText(projectNew.getContactName())) {
                String contactName = projectNew.getContactName().replace("，", ",");
                addOrUpdateProjectManagerByName(projectNew.getId(), contactName.split(","));
            }
        } else if (projectSyncVo.getType().equals(SyncType.update)) {
            if (!projectNew.getActiveFlag() && !projectNew.getName().endsWith("-废弃")) {
                projectNew.setName(projectNew.getName() + "-废弃");
            }

            projectMapper.updateByPrimaryKeySelective(projectNew);

            //处理订单编码
            addOrUpdateOrderCenterCode(projectNew.getId(), projectNew.getOrderCenterCode());

            //处理负责人
            if (StringUtils.hasText(projectNew.getContactName())) {
                String contactName = projectNew.getContactName().replace("，", ",");
                addOrUpdateProjectManagerByName(projectNew.getId(), contactName.split(","));
            }
        } else if (projectSyncVo.getType().equals(SyncType.delete)) {
            projectMapper.diableHrProjectByPrimaryKey(projectNew.getId());

            //处理订单编码
            orderCenterMapper.removeByProjectId(projectNew.getId());

            //处理负责人
            rUserProjectPermMapper.deleteByProjectId(projectNew.getId());
        }
    }

    public List<ProjectVo> selectItProject(String name) {
        return projectMapper.selectItProject(name);
    }

    @Override
    public List<IdNameBaseObject> getSimpleProjects(Long companyId) {
        return projectMapper.selectIdNameByCompanyId(companyId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void simplifyUpdateProject(ProjectSimplifyVo projectSimplifyVo) {

        if (projectSimplifyVo.getId() == null) throw new ParamException("缺少Id");

        projectSimplifyVo.setUpdateTime(new Date());

        Integer maxMember = projectMapper.getProjectDateByProjectId(projectSimplifyVo.getId()).getMaxMember();

        if (projectSimplifyVo.getMaxMember() != null && projectSimplifyVo.getMaxMember() == maxMember) {
            throw new ParamException("上限人数没有变化");
        }

        if (StringUtils.hasText(projectSimplifyVo.getName())) {
            if (projectSimplifyVo.getActiveFlag() != null && !projectSimplifyVo.getActiveFlag() && !projectSimplifyVo.getName().endsWith("-废弃")) {
                projectSimplifyVo.setName(projectSimplifyVo.getName() + "-废弃");
            } else if (projectSimplifyVo.getActiveFlag() != null && projectSimplifyVo.getActiveFlag() && projectSimplifyVo.getName().endsWith("-废弃")) {
                projectSimplifyVo.setName(projectSimplifyVo.getName().substring(0, projectSimplifyVo.getName().length() - 3));
            }
        }


        projectMapper.simpleUpdate(projectSimplifyVo);

        //处理新增的订单
        if (StringUtils.hasText(projectSimplifyVo.getOrderCenterAddList())) {

            String orders[] = projectSimplifyVo.getOrderCenterAddList().split(",");
            List<OrderCenter> orderCenters = new ArrayList<>();
            Arrays.stream(orders).forEach(order -> {
                String orderStrs[] = order.split(":");
                String city = null;
                if (orderStrs.length > 0) {
                    String code = orderStrs[0];
                    if (orderStrs.length > 1) city = orderStrs[1];
                    OrderCenter orderCenter = new OrderCenter();
                    orderCenter.setCode(code);
                    orderCenter.setCity(city);
                    orderCenters.add(orderCenter);
                }
            });

            //过滤 orderCenterCodesStr中不存在的orderCenterAddList
            List<OrderCenter> newOrderCenter = new ArrayList<>();
            if (StringUtils.hasText(projectSimplifyVo.getOrderCenterCodesStr())) {
                List<String> orderCodes = Arrays.asList(projectSimplifyVo.getOrderCenterCodesStr().split(","));
                newOrderCenter = orderCenters.stream().filter(orderCenter -> {
                    return orderCodes.contains(orderCenter.getCode());
                }).collect(Collectors.toList());
            }

            if (!newOrderCenter.isEmpty()) orderCenterMapper.batchInsert(newOrderCenter);

        }

        //处理负责人
        if (StringUtils.hasText(projectSimplifyVo.getManagerIds())) {
            addOrUpdateProjectManager(projectSimplifyVo.getId(), projectSimplifyVo.getManagerIds().split(","));
        } else {
            rUserProjectPermMapper.deleteByProjectIdAndProjectRoleId(projectSimplifyVo.getId(), ProjectRole.Role.project_manager_id);
        }

        //处理订单编码
        addOrUpdateOrderCenterCode(projectSimplifyVo.getId(), projectSimplifyVo.getOrderCenterCodesStr());

        //如果有附件，需要处理附件
        if (projectSimplifyVo.getMaxMember() != null) {

            if (!StringUtils.hasText(projectSimplifyVo.getReason())) {
                throw new ParamException("修改最大上限人数需要填写备注");
            }

            String attachmentUrl = orgMaxMemberChangeLogService.importAttachment(projectSimplifyVo.getAttachment());
            // 当attachment为null
            if (attachmentUrl != null) {
                orgMaxMemberChangeLogService.addLog(projectSimplifyVo.getId(), projectSimplifyVo.getMaxMember(), projectSimplifyVo.getReason(), attachmentUrl, MyTokenUtils.getCurrentUserId());
            }

        }

    }
    
    public List<OrderCenterVo> getOrderCenters(Boolean usedFlag) {
        return orderCenterMapper.selectOrderCenterVoByUsedFlag(usedFlag);
    }

    @Override
    public void updateOrderCenter(OrderCenter orderCenter) {
        int i = orderCenterMapper.updateByPrimaryKeySelective(orderCenter);
        if (i < 1) {
            throw new ParamException(ErrorMessage.PARAM_ERROR_MESSAGE);
        }
    }

}
