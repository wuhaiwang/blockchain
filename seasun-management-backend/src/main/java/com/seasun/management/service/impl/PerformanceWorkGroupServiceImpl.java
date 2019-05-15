package com.seasun.management.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.seasun.management.constant.ErrorCode;
import com.seasun.management.constant.SyncType;
import com.seasun.management.dto.*;
import com.seasun.management.exception.ParamException;
import com.seasun.management.exception.UserInvalidOperateException;
import com.seasun.management.mapper.*;
import com.seasun.management.model.*;
import com.seasun.management.service.OperateLogService;
import com.seasun.management.service.PerformanceWorkGroupService;
import com.seasun.management.service.WorkGroupService;
import com.seasun.management.service.performanceCore.groupTrack.GroupTrackService;
import com.seasun.management.service.performanceCore.userPerformance.PerformanceDataHelper;
import com.seasun.management.util.MySystemParamUtils;
import com.seasun.management.util.MyTokenUtils;
import com.seasun.management.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PerformanceWorkGroupServiceImpl extends AbstractSyncService implements PerformanceWorkGroupService {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceWorkGroupServiceImpl.class);

    @Autowired
    PerformanceWorkGroupMapper performanceWorkGroupMapper;

    @Autowired
    WorkGroupMapper workGroupMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    OperateLogService operateLogService;

    @Autowired
    GroupTrackService<PerformanceUserDto> groupTrackService;

    @Autowired
    RUserProjectPermMapper rUserProjectPermMapper;

    @Autowired
    RUserPerformancePermMapper rUserPerformancePermMapper;

    @Autowired
    FmMemberMapper fmMemberMapper;

    @Autowired
    private WorkGroupService workGroupService;

    @Autowired
    private UserPerformanceMapper userPerformanceMapper;

    @Autowired
    private PerfUserCheckResultMapper perfUserCheckResultMapper;

    @Autowired
    private CfgSystemParamMapper cfgSystemParamMapper;

    @Override
    public PerformanceWorkGroupNodeVo getPerformanceTree() {
        // 能进入此界面，只有 admin hr (manager) 三类身份
        PerformanceWorkGroupNodeVo root = new PerformanceWorkGroupNodeVo();
        boolean partRoot = false;

        Long userId = MyTokenUtils.getCurrentUserId();
        RUserPerformancePerm perm = new RUserPerformancePerm();
        perm.setPerformanceWorkGroupRoleId(PerformanceWorkGroupRole.Role.performance_human_access_role_id);
        perm.setUserId(userId);
        List<RUserPerformancePerm> perms = rUserPerformancePermMapper.selectSelectiveByRole(perm);

//        List<PerformanceWorkGroup>  workGroups = performanceWorkGroupMapper.selectAllByManagerId(userId);
        if (((perms != null && perms.size() > 0)
//                || (workGroups != null && workGroups.size() > 0)
        ) && !MyTokenUtils.isAdmin()) {
            partRoot = true;
        }


        List<PerformanceWorkGroup> allPerformanceWorkGroups = performanceWorkGroupMapper.selectAllActiveWorkGroup();

        Map<Long, List<PerformanceWorkGroupNodeVo>> parentChildrenMap = new HashMap<>();
        for (PerformanceWorkGroup workGroup : allPerformanceWorkGroups) {

            if (workGroup.getParent() == null) {
                root = copyWorkGroup2Vo(workGroup);
            } else if (parentChildrenMap.containsKey(workGroup.getParent())) {
                parentChildrenMap.get(workGroup.getParent()).add(copyWorkGroup2Vo(workGroup));
            } else {
                List<PerformanceWorkGroupNodeVo> childrenList = new ArrayList<>();
                childrenList.add(copyWorkGroup2Vo(workGroup));
                parentChildrenMap.put(workGroup.getParent(), childrenList);
            }
        }
        // 填充根节点
        Long rootId = root.getId();
        List<PerformanceWorkGroupNodeVo> list = parentChildrenMap.get(rootId);
        root.setNodes(list);

        if (!partRoot) {

            // 根节点下的子节点,将所有权限设置为 true
            filledNode(root, parentChildrenMap, null);
            root.setAvailable(true);
        } else {
            List<Long> perfWorkGroupIds = new ArrayList<>();
            if (perms.size() != 0) {
                for (RUserPerformancePerm tempPerm : perms) {
                    perfWorkGroupIds.add(tempPerm.getPerformanceWorkGroupId());
                }
            }
//            if (workGroups != null && workGroups.size() != 0) {
//                for (PerformanceWorkGroup workGroup : workGroups) {
//                    perfWorkGroupIds.add(workGroup.getId());
//                }
//            }

            filledNode(root, parentChildrenMap, perfWorkGroupIds);
            root.setAvailable(perfWorkGroupIds.contains(rootId));
            if (!root.getAvailable()) {
                filerRoot(root);
            }
        }
        filledAvailableNode(root.getNodes(), root.getAvailable());
        return root;
    }

    // 过滤掉没有 权限节点的组
    private void filerRoot(PerformanceWorkGroupNodeVo root) {
        List<PerformanceWorkGroupNodeVo> children = root.getNodes();
        List<PerformanceWorkGroupNodeVo> resultNode = new ArrayList<>();

        if (children == null || children.size() == 0) {
            return;
        }

        for (PerformanceWorkGroupNodeVo vo : children) {

            //不过滤这个子节点
            boolean flag = getFilterFlag(vo);
            if (!flag) {
                if (!vo.getAvailable()) {
                    filerRoot(vo);
                }
                resultNode.add(vo);
            }
        }
        root.setNodes(resultNode);
    }

    // 判断树中是否包含 权限节点,不包含则过滤掉
    private boolean getFilterFlag(PerformanceWorkGroupNodeVo root) {

        boolean filterFlag = true;
        List<PerformanceWorkGroupNodeVo> children = root.getNodes();

        // 本身为权限节点
        if (root.getAvailable()) {
            return false;
        }

        // 没有叶子节点时
        if (children == null || children.size() == 0) {
            return !root.getAvailable();
        }

        if (children.size() != 0) {
            // 分别遍历子节点
            for (PerformanceWorkGroupNodeVo vo : children) {

                if (vo.getAvailable()) {
                    // 当子节点为权限节点
                    filterFlag = false;
                    break;
                } else {
                    // 当子节点不为权限节点，递归判断子节点是否 包含 权限节点
                    if (!getFilterFlag(vo)) {
                        filterFlag = false;
                        break;
                    }
                }
            }
        }

        return filterFlag;
    }

    private void filledAvailableNode(List<PerformanceWorkGroupNodeVo> children, boolean isAvailable) {

        for (PerformanceWorkGroupNodeVo vo : children) {
            // 父节点是权限节点
            if (isAvailable) {
                vo.setAvailable(true);
            }
            filledAvailableNode(vo.getNodes(), vo.getAvailable());
        }
    }


    private PerformanceWorkGroupNodeVo copyWorkGroup2Vo(PerformanceWorkGroup workGroup) {
        PerformanceWorkGroupNodeVo vo = new PerformanceWorkGroupNodeVo();
        vo.setId(workGroup.getId());
        vo.setTitle(workGroup.getName());
        vo.setWorkGroupId(workGroup.getWorkGroupId());
        return vo;
    }

    private PerformanceWorkGroupNodeVo filledNode(PerformanceWorkGroupNodeVo vo, Map<Long, List<PerformanceWorkGroupNodeVo>> map,
                                                  List<Long> perfWorkGroupIds) {
        List<PerformanceWorkGroupNodeVo> list = vo.getNodes();
        if (list == null || list.size() == 0) {
            return vo;
        } else {
            for (PerformanceWorkGroupNodeVo child : list) {
                child.setAvailable(perfWorkGroupIds == null || perfWorkGroupIds.size() == 0 || perfWorkGroupIds.contains(child.getId()));
                child.setNodes(map.get(child.getId()) == null ? new ArrayList<>() : map.get(child.getId()));
                filledNode(child, map, perfWorkGroupIds);
            }
            return vo;
        }

    }

    @Override
    public PerformanceWorkGroupVo getPerformanceWorkGroupInfoById(Long id) {
        PerformanceWorkGroupVo result = performanceWorkGroupMapper.selectPerformanceWorkGroupVoById(id);
        if (result == null) {
            return null;
        }
        result.setDirectMembers(getWorkGroupMemberById(id));

        //设置当前绩效树下所有成员总数
        List<WorkGroupMemberDto> totalMember = performanceWorkGroupMapper.selectAllActiveWorkGroupMember();
        Map<Long, List<WorkGroupMemberDto>> totalMemberMap = totalMember.stream().filter(g -> null != g.getParent()).collect(Collectors.groupingBy(g -> g.getParent()));
        result.setTotal(result.getDirectMembers().size() + getWorkGroupTotalMemberById(id, totalMemberMap));

        List<UserIdMinVo> performancePerm = rUserPerformancePermMapper.selectPerformancePermsByGroupId(id);
        if (performancePerm != null && performancePerm.size() != 0) {
            List<UserIdMinVo> dataAccessPerm = new ArrayList<>();
            List<UserIdMinVo> observerPerm = new ArrayList<>();
            List<UserIdMinVo> humanPerm = new ArrayList<>();
            for (UserIdMinVo vo : performancePerm) {
                if (PerformanceWorkGroupRole.Role.performance_data_access_role_id.equals(vo.getPerformanceWorkGroupRoleId())) {
                    dataAccessPerm.add(vo);
                } else if (PerformanceWorkGroupRole.Role.performance_observer_role_id.equals(vo.getPerformanceWorkGroupRoleId())) {
                    observerPerm.add(vo);
                } else if (PerformanceWorkGroupRole.Role.performance_human_access_role_id.equals(vo.getPerformanceWorkGroupRoleId())) {
                    humanPerm.add(vo);
                }
            }
            result.setDataManagers(dataAccessPerm);
            result.setObservers(observerPerm);
            result.setHumanConfigurator(humanPerm);
        }
        return result;
    }

    @Override
    public Long getWorkGroupTotalMemberById(Long id, Map<Long, List<WorkGroupMemberDto>> totalMemberMap) {
        Long result = 0L;
        if (totalMemberMap.containsKey(id)) {
            List<WorkGroupMemberDto> workGroupMemberDtos = totalMemberMap.get(id);
            for (WorkGroupMemberDto workGroupMemberDto : workGroupMemberDtos) {
                Long total = workGroupMemberDto.getTotal();
                total = null == total ? 0 : total;
                result += total;
                result += getWorkGroupTotalMemberById(workGroupMemberDto.getId(), totalMemberMap);
            }
        }
        return result;
    }

    private List<PerformanceWorkGroupVo.UserInfo> getWorkGroupMemberById(Long id) {
        List<User> users = userMapper.selectByPerfWorkGroupId(id);
        List<FmMemberDto> allFmMembers = fmMemberMapper.selectAll();

        List<PerformanceWorkGroupVo.UserInfo> userInfos = new ArrayList<>();
        for (User user : users) {
            PerformanceWorkGroupVo.UserInfo tmp = new PerformanceWorkGroupVo.UserInfo();
            tmp.setLoginId(user.getLoginId());
            tmp.setName(user.getLastName() + user.getFirstName());
            tmp.setPhoto(user.getPhoto());
            if (allFmMembers.size() > 0) {
                FmMemberDto dto = allFmMembers.stream().filter(u -> u.getUserId().equals(user.getId())).findFirst().orElse(null);
                if (dto != null) {
                    tmp.setFixedFlag(true);
                    tmp.setFixedWorkGroupName(dto.getPlatName() + "-" + dto.getProjectName() + "-固化组");
                } else {
                    tmp.setFixedFlag(false);
                }
            }
            userInfos.add(tmp);
        }

        return userInfos;
    }

    @Override
    public Long addSubPerformanceWorkGroup(Long performanceWorkGroupId, Long subGroupId, Long managerId, Integer strictType, String newName) {
        PerformanceWorkGroup newWorkGroup = new PerformanceWorkGroup();
        newWorkGroup.setParent(performanceWorkGroupId);
        newWorkGroup.setPerformanceManagerId(managerId);
        newWorkGroup.setStrictType(strictType);
        newWorkGroup.setWorkGroupId(subGroupId);
        if (null != subGroupId) {
            WorkGroup workGroup = workGroupMapper.selectByPrimaryKey(subGroupId);
            if (null == workGroup) {
                throw new ParamException("关联人力组异常，没找到对应人力组");
            }
            newWorkGroup.setName(workGroup.getName());
        } else {
            newWorkGroup.setName(newName);
        }
        newWorkGroup.setActiveFlag(true);
        newWorkGroup.setProjectConfirmFlag(false);
        performanceWorkGroupMapper.insert(newWorkGroup);
        return newWorkGroup.getId();
    }

    @Override
    public void deleteSubPerformanceGroup(Long id) {
        User user = MyTokenUtils.getCurrentUser();
        List<PerformanceWorkGroup> allPerformanceWorkGroups = performanceWorkGroupMapper.selectAllActiveWorkGroup();
        List<Long> deleteList = new ArrayList<>();
        Long resId = null;
        Map<Long, List<PerformanceWorkGroup>> parentChildrenMap = new HashMap<>();
        for (PerformanceWorkGroup workGroup : allPerformanceWorkGroups) {
            if (id.equals(workGroup.getId())) {
                resId = workGroup.getId();
            }
            if (parentChildrenMap.containsKey(workGroup.getParent())) {
                parentChildrenMap.get(workGroup.getParent()).add(workGroup);
            } else {
                List<PerformanceWorkGroup> childrenList = new ArrayList<>();
                childrenList.add(workGroup);
                parentChildrenMap.put(workGroup.getParent(), childrenList);
            }
        }
        deleteList.add(resId);
        deleteList = recursionAddDeleteList(resId, deleteList, parentChildrenMap);
        performanceWorkGroupMapper.updateActiveAllInList(deleteList);
        userMapper.clearUserPerformanceGroupIdInList(deleteList);
        operateLogService.add(OperateLog.Type.performance_group_delete, user.getName() + " 删除绩效组，组ID：" + id, user.getId());
    }

    @Override
    public void updateByCond(PerformanceWorkGroup performanceWorkGroup) {
        PerformanceWorkGroup workGroup = performanceWorkGroupMapper.selectByPrimaryKey(performanceWorkGroup.getId());
        //当workGroupId为-1时，删除关联人力组
        if (performanceWorkGroup.getWorkGroupId() != null && performanceWorkGroup.getWorkGroupId().equals(-1L)) {
            performanceWorkGroupMapper.updateWorkGroupById(performanceWorkGroup.getId());
        } else {
            performanceWorkGroupMapper.updateByPrimaryKeySelective(performanceWorkGroup);
        }

        if (null != performanceWorkGroup.getPerformanceManagerId()) {
            // 修改负责人, 清除之前负责人的角色
            if (null != workGroup.getPerformanceManagerId() && !workGroup.getPerformanceManagerId().equals(performanceWorkGroup.getPerformanceManagerId())) {

                List<PerformanceWorkGroup> manageGroups = performanceWorkGroupMapper.selectAllByManagerId(workGroup.getPerformanceManagerId());
                if (null == manageGroups || manageGroups.isEmpty()) {
                    deletePerformanceMenuPermByManagerId(workGroup.getPerformanceManagerId());
                }
            }

            // 如果没有,则添加负责人角色
            List<RUserProjectPermVo> perms = rUserProjectPermMapper.selectByUserIdAndProjectRoleId(performanceWorkGroup.getPerformanceManagerId(), ProjectRole.Role.performance_manager_id);
            if (perms == null || perms.size() == 0) {
                RUserProjectPerm rUserProjectPerm = new RUserProjectPerm();
                rUserProjectPerm.setProjectRoleId(ProjectRole.Role.performance_manager_id);
                rUserProjectPerm.setUserId(performanceWorkGroup.getPerformanceManagerId());
                rUserProjectPermMapper.insertSelective(rUserProjectPerm);
            }
        }
    }

    private void deletePerformanceMenuPermByManagerId(Long managerId) {
        RUserProjectPerm rUserProjectPerm = new RUserProjectPerm();
        rUserProjectPerm.setProjectRoleId(ProjectRole.Role.performance_manager_id);
        rUserProjectPerm.setUserId(managerId);
        rUserProjectPermMapper.deleteSelective(rUserProjectPerm);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void forceAddPerformanceMember(Long memberId, Long performanceWorkGroupId) {
        User user = userMapper.selectByPrimaryKey(memberId);
        if (null == user) {
            throw new ParamException("该员工不存在");
        }

        if (user.getPerfWorkGroupId() != null && user.getPerfWorkGroupId().equals(performanceWorkGroupId)) {
            throw new UserInvalidOperateException("该员工已在本绩效组，请勿重复添加。");
        }

        User userCond = new User();
        userCond.setId(memberId);
        userCond.setPerfWorkGroupId(performanceWorkGroupId);
        int i = userMapper.updateByPrimaryKeySelective(userCond);
        List<Long> perfWorkGroupIds = new ArrayList<>();
        perfWorkGroupIds.add(performanceWorkGroupId);
        if (user.getPerfWorkGroupId() != null) {
            perfWorkGroupIds.add(user.getPerfWorkGroupId());
        }

        if (i > 0) {
            insertUserPerfGroupWorkModifyOperateLog(perfWorkGroupIds, performanceWorkGroupId, user, null);
        }
    }


    public void insertUserPerfGroupWorkModifyOperateLog(List<Long> perfWorkGroupIds, Long performanceWorkGroupId, User user, String channel) {
        String content;
        User currentUser = new User();
        List<PerformanceWorkGroup> result = performanceWorkGroupMapper.selectByIds(perfWorkGroupIds);

        if (user.getPerfWorkGroupId() != null) {
            PerformanceWorkGroup lastPerformanceWorkGroup = new PerformanceWorkGroup();
            PerformanceWorkGroup nowPerformanceWorkGroup = new PerformanceWorkGroup();
            for (PerformanceWorkGroup performanceWorkGroup : result) {
                if (performanceWorkGroup.getId().equals(performanceWorkGroupId)) {
                    nowPerformanceWorkGroup = performanceWorkGroup;
                } else {
                    lastPerformanceWorkGroup = performanceWorkGroup;
                }
            }
            content = " 从 " + lastPerformanceWorkGroup.getName() + lastPerformanceWorkGroup.getId() + " 调到 " + nowPerformanceWorkGroup.getName() + nowPerformanceWorkGroup.getId();
        } else {
            PerformanceWorkGroup performanceWorkGroup = result.get(0);
            content = " 加入到 " + performanceWorkGroup.getName() + performanceWorkGroup.getId();
        }

        if (MyTokenUtils.Channel.dsp.equals(channel)) {
            currentUser.setLastName(MyTokenUtils.Channel.dsp);
            currentUser.setFirstName("后台用户");
        } else {
            currentUser = MyTokenUtils.getCurrentUser();
        }

        operateLogService.add(OperateLog.Type.user_performance_work_group_modify,
                currentUser.getName() + " 在 " + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()) + " 将 " + user.getName() + content, currentUser.getId());

    }

    @Override
    public JSONObject checkPerformanceMember(Long memberId) {
        JSONObject jsonObject = new JSONObject();
        int code = 0;
        String perfWorkGroupName = "";
        User user = userMapper.selectByPrimaryKey(memberId);
        if (null == user) {
            throw new ParamException("该员工不存在");
        }
        if (null != user.getPerfWorkGroupId()) {
            code = 1;
            UserPerformance userPerformance = userPerformanceMapper.selectUserLastPerformance(memberId);
            if (userPerformance != null) {
                if (!userPerformance.getStatus().equals(UserPerformance.Status.complete)) {
                    code = ErrorCode.USER_PERFORMANCE_WORK_GROUP_MODIFY_ERROR;
                }
            }

            PerformanceWorkGroup performanceWorkGroup = performanceWorkGroupMapper.selectByPrimaryKey(user.getPerfWorkGroupId());
            if (null != performanceWorkGroup) {
                perfWorkGroupName = performanceWorkGroup.getName();
            }
        }
        jsonObject.put("code", code);
        jsonObject.put("name", perfWorkGroupName);
        return jsonObject;
    }

    private List<Long> recursionAddDeleteList(Long id, List<Long> deleteList,
                                              Map<Long, List<PerformanceWorkGroup>> map) {
        if (id == null) {
            return deleteList;
        }
        List<PerformanceWorkGroup> children = map.get(id);
        if (children == null || children.size() == 0) {
            return deleteList;
        }

        children.forEach(x -> deleteList.add(x.getId()));
        for (int i = 0; i < children.size(); i++) {
            recursionAddDeleteList(children.get(i).getId(), deleteList, map);
        }
        return deleteList;
    }

    @Override
    public void syncHrWorkGroupToPerformanceWorkGroup() {
        // 清空绩效树
        performanceWorkGroupMapper.truncateTable();
        userMapper.clearAllPerfWorkGroupId();

        // 从新根据人力树生成
        List<WorkGroupDto> allGroups = workGroupMapper.selectAllWithLeaderByGroupRoleId(WorkGroupRole.Role.performance);
        List<PerformanceUserDto> allUsers = userMapper.selectAllEntityWithPerformance(WorkGroupRole.Role.performance);
        WorkGroupDto rootWorkGroup = getWorkGroupTree(267L, allGroups, allUsers);
        traversalTreeForInsertPerformanceWorkGroup(rootWorkGroup, null, true);
    }

    @Override
    public void syncHrWorkGroupToPerformanceWorkGroupById(Long performanceGroupId) {

        // 找到对应的人力工作组
        PerformanceWorkGroup performanceWorkGroup = performanceWorkGroupMapper.selectByPrimaryKey(performanceGroupId);

        List<PerformanceWorkGroup> allPerformanceWorkGroup = performanceWorkGroupMapper.selectAllPerformanceWorkGroup();
        List<Long> childWorkGroupIds = new ArrayList<>();
        setAllChildPerformanceGroupIds(childWorkGroupIds, allPerformanceWorkGroup, performanceGroupId);
        if (childWorkGroupIds.size() > 0) {
            performanceWorkGroupMapper.batchDeleteByPks(childWorkGroupIds);
            userMapper.clearUserPerformanceGroupIdInList(childWorkGroupIds);
        }

        // 从新根据人力树生成
        List<WorkGroupDto> allGroups = workGroupMapper.selectAllWithLeaderByGroupRoleId(WorkGroupRole.Role.performance);
        List<PerformanceUserDto> allUsers = userMapper.selectAllEntityWithPerformance(WorkGroupRole.Role.performance);

        WorkGroupDto rootWorkGroup = getWorkGroupTree(performanceWorkGroup.getWorkGroupId(), allGroups, allUsers);
        traversalTreeForInsertPerformanceWorkGroup(rootWorkGroup, performanceGroupId, true);
    }

    private void setAllChildPerformanceGroupIds(List<Long> childWorkGroupIds, List<PerformanceWorkGroup> allPerformanceWorkGroup, Long parentId) {
        for (PerformanceWorkGroup performanceWorkGroup : allPerformanceWorkGroup) {
            if (performanceWorkGroup.getParent() != null && performanceWorkGroup.getParent().equals(parentId)) {
                childWorkGroupIds.add(performanceWorkGroup.getId());
                setAllChildPerformanceGroupIds(childWorkGroupIds, allPerformanceWorkGroup, performanceWorkGroup.getId());
            }
        }

    }

    @Override
    public void deleteDirectMember(String memberLoginId) {
        User user = userMapper.selectUserByLoginId(memberLoginId);
        User currentUser = MyTokenUtils.getCurrentUser();
        if (null != user) {
            // 清除绩效组
            int i = userMapper.clearPerfWorkGroupIdByUserId(user.getId());
            PerformanceWorkGroup performanceWorkGroup = performanceWorkGroupMapper.selectByPrimaryKey(user.getPerfWorkGroupId());
            if (i > 0) {
                operateLogService.add(OperateLog.Type.user_performance_work_group_modify,
                        currentUser.getName() + " 在 " + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()) + " 将 " + user.getName() + " 从 " + performanceWorkGroup.getName() + performanceWorkGroup.getId() + " 删除",
                        currentUser.getId());
            }


            // 清除本月未完成的绩效记录 -- 不清记录 2017-8-7 by linjinghua
            //UserPerformanceMapper.deleteUnfinishedRecordByUserId(user.getId());
        }
    }

    @Override
    public CheckResultVo checkGroup() {

        CheckResultVo result = new CheckResultVo();

        int perfWorkGroupMemberCount = 0;
        int workGroupMemberCount = 0;

        //不在绩效组数据
        CheckResultVo.NotInPerfWorkGroupDto notInPerfWorkGroupDto = new CheckResultVo.NotInPerfWorkGroupDto();
        List<PerfUserCheckResultVo> notInPerfWorkGroupUsers = new ArrayList<>();
        List<CheckResultVo.NotInPerfWorkGroupDto.IgnoreWorkGroup> notInPerfWorkGroupIgnoreWorkGroups = new ArrayList<>();

        //不在人力组数据
        List<PerfUserCheckResultVo> notInWorkGroups = new ArrayList<>();

        Map<Long, List<PerfUserCheckResultVo>> ignoreWorkGroupUsersByRootGroupMap = new HashMap<>();
        Map<Long, Integer> ingoreWorkGroupRootMemberCount = new HashMap<>();
        Map<Long, List<Long>> ingoreWorkGroupIdsByRootMap = new HashMap<>();

        PerformanceWorkGroup rootWorkGroup = performanceWorkGroupMapper.selectByRoot();
        List<PerformanceWorkGroupInfo> performanceWorkGroups = performanceWorkGroupMapper.selectAllWorkGroupWithManagerPerfWorkGroupId();
        List<UserDto> activeEntityUsers = userMapper.selectActiveEntity();
        List<PerfUserCheckResult> allPerfUserCheckResults = perfUserCheckResultMapper.selectAll();
        List<WorkGroup> activeWorkGroup = workGroupMapper.selectAllByActive();
        CfgSystemParam cfgSystemParam = cfgSystemParamMapper.selectByName(MySystemParamUtils.Key.NotInPerformanceWorkGroupList);

        String[] ignoreWorkGroups = cfgSystemParam.getValue().split(",");
        for (String workGroupId : ignoreWorkGroups) {
            Long id = Long.valueOf(workGroupId);
            ignoreWorkGroupUsersByRootGroupMap.put(id, new ArrayList<>());
            ingoreWorkGroupRootMemberCount.put(id, 0);
            List<Long> ingoreWorkGroupIds = new ArrayList<>();
            ingoreWorkGroupIds.add(id);
            List<Long> subGroupIds = workGroupService.getWorkGroupRelationByParent(id, activeWorkGroup);
            if (subGroupIds != null) {
                ingoreWorkGroupIds.addAll(subGroupIds);
            }
            ingoreWorkGroupIdsByRootMap.put(id, ingoreWorkGroupIds);
        }

        List<Long> activePerformanceWorkGroupIds = new ArrayList<>();
        Map<Long, PerformanceWorkGroup> perfWorkGroupByIdMap = performanceWorkGroups.stream().collect(Collectors.toMap(x -> x.getId(), x -> x));
        Map<Long, WorkGroup> workGroupByIdMap = activeWorkGroup.stream().collect(Collectors.toMap(x -> x.getId(), x -> x));
        Map<Long, PerfUserCheckResult> perfUserCheckResultByUserIdMap = allPerfUserCheckResults.stream().collect(Collectors.toMap(x -> x.getUserId(), x -> x, (oldValue, newValue) -> newValue));

        if (performanceWorkGroups != null && !performanceWorkGroups.isEmpty()) {

            CheckResultVo.CommonCheckResultDto emptyMember = new CheckResultVo.CommonCheckResultDto();
            emptyMember.setItemName("no_member");
            StringBuilder emptyMemberGroupStr = new StringBuilder();

            CheckResultVo.CommonCheckResultDto emptyItem = new CheckResultVo.CommonCheckResultDto();
            emptyItem.setItemName("no_manager");
            StringBuilder emptyManagerGroupStr = new StringBuilder();

            CheckResultVo.CommonCheckResultDto selfManagerItem = new CheckResultVo.CommonCheckResultDto();
            selfManagerItem.setItemName("self_manager");
            StringBuilder selfManagerGroupStr = new StringBuilder();

            CheckResultVo.CommonCheckResultDto invalidStrictFlagItem = new CheckResultVo.CommonCheckResultDto();
            invalidStrictFlagItem.setItemName("invalid_strict_flag");
            StringBuilder invalidStrictGroupStr = new StringBuilder();

            CheckResultVo.CommonCheckResultDto invalidProjectConfirmFlagItem = new CheckResultVo.CommonCheckResultDto();
            invalidProjectConfirmFlagItem.setItemName("invalid_project_confirm_flag");
            StringBuilder invalidProjectConfirmGroupStr = new StringBuilder();

            for (PerformanceWorkGroupInfo perfWorkGroup : performanceWorkGroups) {

                activePerformanceWorkGroupIds.add(perfWorkGroup.getId());
                // 1. 检查是否存在空负责人的绩效组
                if (perfWorkGroup.getPerformanceManagerId() == null) {
                    emptyManagerGroupStr.append(perfWorkGroup.getName()).append(",");
                }

                // 2. 不允许负责人在本组
                if (perfWorkGroup.getId().equals(perfWorkGroup.getManagerPerformanceGroupId())) {
                    selfManagerGroupStr.append(perfWorkGroup.getName()).append(",");
                }

                // 3. 叶子节点,少于7人，且配置为严格比例的组
                if (perfWorkGroup.getChildGroupCount() == 0 && perfWorkGroup.getMemberCount().intValue() < 7 && perfWorkGroup.getStrictType() == 1) {
                    invalidStrictGroupStr.append(perfWorkGroup.getName()).append(",");
                }

                // 4. 开启了与项目确认，二级绩效组必须关联人力组
                if (perfWorkGroup.getProjectConfirmFlag() && rootWorkGroup.getId().equals(perfWorkGroup.getParent()) && null == perfWorkGroup.getWorkGroupId()) {
                    invalidProjectConfirmGroupStr.append(perfWorkGroup.getName()).append(",");
                }

                //5.检查是否存在无直属下属的绩效组
                if (null == perfWorkGroup.getMemberCount() || perfWorkGroup.getMemberCount() == 0) {
                    emptyMemberGroupStr.append(perfWorkGroup.getName()).append(",");
                }
            }

            loop:
            for (UserDto activeEntityUser : activeEntityUsers) {

                if (activeEntityUser.getWorkGroupId() != null && workGroupByIdMap.containsKey(activeEntityUser.getWorkGroupId())) {
                    workGroupMemberCount++;

                    //统计不参与绩效的人力组总人数
                    subWorkGroupLoop:
                    for (Long ignoreWorkGroupRootId : ingoreWorkGroupIdsByRootMap.keySet()) {
                        for (Long subGroupId : ingoreWorkGroupIdsByRootMap.get(ignoreWorkGroupRootId)) {
                            if (subGroupId.equals(activeEntityUser.getWorkGroupId())) {
                                ingoreWorkGroupRootMemberCount.put(ignoreWorkGroupRootId, ingoreWorkGroupRootMemberCount.get(ignoreWorkGroupRootId) + 1);
                                continue subWorkGroupLoop;
                            }
                        }
                    }

                    //notInPerfWorkGroup
                    if (activeEntityUser.getPerfWorkGroupId() == null) {
                        PerfUserCheckResultVo perfUserCheckResultVo = new PerfUserCheckResultVo();
                        if (UserDetail.WorkStatus.cadet.equals(activeEntityUser.getWorkStatus())) {
                            perfUserCheckResultVo.setCadetFlag(true);
                            perfUserCheckResultVo.setRemark("该员工是实习生，无需加入绩效组");
                        } else {
                            perfUserCheckResultVo.setCadetFlag(false);
                        }
                        perfUserCheckResultVo.setUserName(activeEntityUser.getName());
                        perfUserCheckResultVo.setWorkGroupName(workGroupService.getWorkGroupFullNameById(activeEntityUser.getWorkGroupId(), workGroupByIdMap));
                        perfUserCheckResultVo.setUserId(activeEntityUser.getId());
                        perfUserCheckResultVo.setLoginId(activeEntityUser.getLoginId());

                        if (perfUserCheckResultByUserIdMap.containsKey(activeEntityUser.getId())) {
                            PerfUserCheckResult perfUserCheckResult = perfUserCheckResultByUserIdMap.get(activeEntityUser.getId());
                            if (PerfUserCheckResult.type.notInPerfWorkGroup.equals(perfUserCheckResult.getType())) {
                                BeanUtils.copyProperties(perfUserCheckResult, perfUserCheckResultVo);
                                perfUserCheckResultByUserIdMap.remove(activeEntityUser.getId());
                            } else {
                                perfUserCheckResultVo.setType(PerfUserCheckResult.type.notInPerfWorkGroup);
                            }
                        } else {
                            perfUserCheckResultVo.setType(PerfUserCheckResult.type.notInPerfWorkGroup);
                        }
                        //当前user是否在配置了不参与绩效的人力组
                        for (Long ignoreWorkGroupRootId : ingoreWorkGroupIdsByRootMap.keySet()) {
                            for (Long subGroupId : ingoreWorkGroupIdsByRootMap.get(ignoreWorkGroupRootId)) {
                                if (subGroupId.equals(activeEntityUser.getWorkGroupId())) {
                                    ignoreWorkGroupUsersByRootGroupMap.get(ignoreWorkGroupRootId).add(perfUserCheckResultVo);
                                    continue loop;
                                }
                            }
                        }
                        notInPerfWorkGroupUsers.add(perfUserCheckResultVo);
                        continue;
                    }
                }

                if (activeEntityUser.getPerfWorkGroupId() != null && activePerformanceWorkGroupIds.contains(activeEntityUser.getPerfWorkGroupId())) {
                    perfWorkGroupMemberCount++;

                    //notInWorkGroup
                    if (activeEntityUser.getWorkGroupId() == null) {
                        PerfUserCheckResultVo perfUserCheckResultVo = new PerfUserCheckResultVo();
                        perfUserCheckResultVo.setCadetFlag(UserDetail.WorkStatus.cadet.equals(activeEntityUser.getWorkStatus()) ? true : false);
                        perfUserCheckResultVo.setUserName(activeEntityUser.getName());
                        perfUserCheckResultVo.setPerfWorkGroupName(getPerfWorkGroupName(activeEntityUser.getPerfWorkGroupId(), perfWorkGroupByIdMap));
                        perfUserCheckResultVo.setLoginId(activeEntityUser.getLoginId());

                        if (perfUserCheckResultByUserIdMap.containsKey(activeEntityUser.getId())) {
                            PerfUserCheckResult perfUserCheckResult = perfUserCheckResultByUserIdMap.get(activeEntityUser.getId());
                            if (PerfUserCheckResult.type.notInOrgWorkGroup.equals(perfUserCheckResult.getType())) {
                                BeanUtils.copyProperties(perfUserCheckResult, perfUserCheckResultVo);
                                perfUserCheckResultByUserIdMap.remove(activeEntityUser.getId());
                            } else {
                                perfUserCheckResultVo.setType(PerfUserCheckResult.type.notInOrgWorkGroup);
                            }
                        } else {
                            perfUserCheckResultVo.setUserId(activeEntityUser.getId());
                            perfUserCheckResultVo.setType(PerfUserCheckResult.type.notInOrgWorkGroup);
                        }
                        notInWorkGroups.add(perfUserCheckResultVo);
                    }
                }
            }

            //计算不参与绩效的人力组
            for (Long workGroupId : ignoreWorkGroupUsersByRootGroupMap.keySet()) {
                CheckResultVo.NotInPerfWorkGroupDto.IgnoreWorkGroup ignoreWorkGroup = new CheckResultVo.NotInPerfWorkGroupDto.IgnoreWorkGroup();
                List<PerfUserCheckResultVo> perfUserCheckResults = perfUserCheckResurtSort(ignoreWorkGroupUsersByRootGroupMap.get(workGroupId));
                ignoreWorkGroup.setUsers(perfUserCheckResults);
                ignoreWorkGroup.setWorkGroupName(workGroupByIdMap.get(workGroupId).getName());
                ignoreWorkGroup.setMemberCount(ingoreWorkGroupRootMemberCount.get(workGroupId));
                ignoreWorkGroup.setNotInCount(perfUserCheckResults.size());
                notInPerfWorkGroupIgnoreWorkGroups.add(ignoreWorkGroup);
            }

            //排序
            notInPerfWorkGroupUsers = perfUserCheckResurtSort(notInPerfWorkGroupUsers);
            notInWorkGroups = perfUserCheckResurtSort(notInWorkGroups);

            List<Long> deleteIds = new ArrayList<>(perfUserCheckResultByUserIdMap.keySet());
            if (deleteIds.size() > 0) {
                perfUserCheckResultMapper.deleteByUserIds(deleteIds);
            }
            //设置常规检查
            List<CheckResultVo.CommonCheckResultDto> commonCheckResultDtos = new ArrayList<>();
            commonCheckResultDtos.add(checkErrorMessage(emptyManagerGroupStr, emptyItem));
            commonCheckResultDtos.add(checkErrorMessage(selfManagerGroupStr, selfManagerItem));
            commonCheckResultDtos.add(checkErrorMessage(invalidStrictGroupStr, invalidStrictFlagItem));
            commonCheckResultDtos.add(checkErrorMessage(invalidProjectConfirmGroupStr, invalidProjectConfirmFlagItem));
            commonCheckResultDtos.add(checkErrorMessage(emptyMemberGroupStr, emptyMember));

            notInPerfWorkGroupDto.setIgnoreWorkGroups(notInPerfWorkGroupIgnoreWorkGroups);
            notInPerfWorkGroupDto.setUsers(notInPerfWorkGroupUsers);
            result.setCommonCheckResults(commonCheckResultDtos);
            result.setNotInWorkGroups(notInWorkGroups);
            result.setNotInPerfWorkGroup(notInPerfWorkGroupDto);
            result.setPerfWorkGroupTreeMemberTotal(perfWorkGroupMemberCount);
            result.setWorkGroupTreeMemberTotal(workGroupMemberCount);
        }

        return result;
    }

    private List<PerfUserCheckResultVo> perfUserCheckResurtSort(List<PerfUserCheckResultVo> PerfUserCheckResultVos) {
        List<PerfUserCheckResultVo> result = new ArrayList<>();
        if (PerfUserCheckResultVos != null && PerfUserCheckResultVos.size() > 0) {
            List<PerfUserCheckResultVo> hasRemarks = new ArrayList<>();
            List<PerfUserCheckResultVo> nullRemarks = new ArrayList<>();
            for (PerfUserCheckResultVo item : PerfUserCheckResultVos) {
                if (item.getRemark() != null) {
                    hasRemarks.add(item);
                } else {
                    nullRemarks.add(item);
                }
            }
            Collections.sort(hasRemarks, new Comparator<PerfUserCheckResultVo>() {
                @Override
                public int compare(PerfUserCheckResultVo o1, PerfUserCheckResultVo o2) {
                    return o1.getWorkGroupName().compareTo(o2.getWorkGroupName());
                }
            });
            Collections.sort(nullRemarks, new Comparator<PerfUserCheckResultVo>() {
                @Override
                public int compare(PerfUserCheckResultVo o1, PerfUserCheckResultVo o2) {
                    return o1.getWorkGroupName().compareTo(o2.getWorkGroupName());
                }
            });
            result.addAll(nullRemarks);
            result.addAll(hasRemarks);
        }
        return result;

    }

    private CheckResultVo.CommonCheckResultDto checkErrorMessage(StringBuilder errorMessage, CheckResultVo.CommonCheckResultDto checkResultVo) {
        if (errorMessage.toString().length() > 0) {
            String tempStr = errorMessage.toString();
            checkResultVo.setErrorMessage(tempStr.substring(0, tempStr.length() - 1));
            checkResultVo.setCheckResultFlag(false);
        } else {
            checkResultVo.setCheckResultFlag(true);
        }
        return checkResultVo;

    }

    private WorkGroupDto getWorkGroupTree(Long firstGroupId, List<WorkGroupDto> allGroups, List<PerformanceUserDto> allUsers) {
        // 找出根目录，初始化树
        WorkGroupDto rootWorkGroup = null;
        for (WorkGroupDto workGroupDto : allGroups) {
            if (firstGroupId.equals(workGroupDto.getId())) {
                rootWorkGroup = workGroupDto;
                break;
            }
        }
        if (null == rootWorkGroup) {
            throw new ParamException("当前组不存在");
        }
        groupTrackService.initHrGroupTreeByRootGroup(rootWorkGroup, allGroups, allUsers);

        return rootWorkGroup;
    }

    private void traversalTreeForInsertPerformanceWorkGroup(WorkGroupDto rootWorkGroup, Long parent, Boolean firstFlag) {

        Long workGroupId = parent;
        if (!firstFlag) {
            PerformanceWorkGroup performanceWorkGroup = new PerformanceWorkGroup();
            performanceWorkGroup.setWorkGroupId(rootWorkGroup.getId());
            performanceWorkGroup.setName(rootWorkGroup.getName());
            performanceWorkGroup.setParent(parent);
            performanceWorkGroup.setPerformanceManagerId(rootWorkGroup.getLeaderId());
            performanceWorkGroup.setStrictType(PerformanceWorkGroup.GroupStrictType.normalNum);
            performanceWorkGroup.setActiveFlag(true);
            performanceWorkGroup.setProjectConfirmFlag(false);
            performanceWorkGroupMapper.insert(performanceWorkGroup);
            workGroupId = performanceWorkGroup.getId();
        }


        List<User> updateUsers = new ArrayList<>();
        List<PerformanceUserDto> members = rootWorkGroup.getMembers();
        for (PerformanceUserDto member : members) {
            User updateUser = new User();
            updateUser.setId(member.getUserId());
            updateUser.setPerfWorkGroupId(workGroupId);
            updateUsers.add(updateUser);
        }
        if (!updateUsers.isEmpty()) {
            userMapper.batchUpdatePerfWorkGroupIdByPks(updateUsers);
        }

        if (null != rootWorkGroup.getChildWorkGroups() && !rootWorkGroup.getChildWorkGroups().isEmpty()) {
            List<WorkGroupDto> childWorkGroups = rootWorkGroup.getChildWorkGroups();
            for (WorkGroupDto childGroup : childWorkGroups) {
                traversalTreeForInsertPerformanceWorkGroup(childGroup, workGroupId, false);
            }
        }
    }

    @Override
    public void changePerformanceWorkGroupParent(Long sourceId, Long parentId) {
        PerformanceWorkGroup sourceWorkGroup = performanceWorkGroupMapper.selectByPrimaryKey(sourceId);
        if (null == sourceWorkGroup) {
            throw new ParamException("拖拽id不正确");
        }
        PerformanceWorkGroup parentWorkGroup = performanceWorkGroupMapper.selectByPrimaryKey(parentId);
        if (null == parentWorkGroup) {
            throw new ParamException("父组id不正确");
        }

        sourceWorkGroup.setParent(parentWorkGroup.getId());
        performanceWorkGroupMapper.updateByPrimaryKeySelective(sourceWorkGroup);
    }

    @Override
    public List<PerformanceWorkGroupDto> getPerformanceWorkGroupsByWorkGroup(Long workGroupId) {
        List<PerformanceWorkGroupDto> allGroups = performanceWorkGroupMapper.selectAllWithProjectIdByActive();
        Map<Long, PerformanceWorkGroupDto> allGroupMap = allGroups.stream().collect(Collectors.toMap(g -> g.getId(), g -> g));
        List<PerformanceWorkGroupDto> performanceWorkGroups;
        if (null != workGroupId) {
            performanceWorkGroups = performanceWorkGroupMapper.selectAllWithProjectIdByUserHrWorkGroupId(workGroupId);
        } else {
            performanceWorkGroups = allGroups;
        }
        return PerformanceDataHelper.getGroupsWthFullPathName(performanceWorkGroups, allGroupMap);
    }

    @Override
    public List<PerformanceWorkGroupDto> getPerformanceWorkGroupsByManager(Long managerId) {
        List<PerformanceWorkGroupDto> allGroups = performanceWorkGroupMapper.selectAllWithProjectIdByActive();
        Map<Long, PerformanceWorkGroupDto> allGroupMap = allGroups.stream().collect(Collectors.toMap(g -> g.getId(), g -> g));
        List<PerformanceWorkGroupDto> performanceWorkGroups = performanceWorkGroupMapper.selectAllWithProjectIdByManagerId(managerId);
        return PerformanceDataHelper.getGroupsWthFullPathName(performanceWorkGroups, allGroupMap);
    }

    @Transactional
    @Override
    public void sync(BaseSyncVo baseSyncVo) {

        if (!(baseSyncVo instanceof RUserPerformanceWorkGroupSyncVo)) {
            throw new ClassCastException("baseSynVo对象 不能转换为 RUserPerformanceWorkGroupSyncVo 类");
        }
        RUserPerformanceWorkGroupSyncVo rUserPerformanceWorkGroupSyncVo = (RUserPerformanceWorkGroupSyncVo) baseSyncVo;
        Long userId = rUserPerformanceWorkGroupSyncVo.getData().getUserId();
        if (null == userId) {
            throw new ParamException("userId不能为空");
        }
        User user = userMapper.selectByPrimaryKey(userId);
        if (rUserPerformanceWorkGroupSyncVo.getType().equals(SyncType.update)) {
            if (null != rUserPerformanceWorkGroupSyncVo.getData().getPerfWorkGroupId()) {
                List<Long> perfWorkGroupIds = new ArrayList<>();
                if (user.getPerfWorkGroupId() != null) {
                    perfWorkGroupIds.add(user.getPerfWorkGroupId());
                }
                perfWorkGroupIds.add(rUserPerformanceWorkGroupSyncVo.getData().getPerfWorkGroupId());
                user.setPerfWorkGroupId(rUserPerformanceWorkGroupSyncVo.getData().getPerfWorkGroupId());
                int i = userMapper.updateByPrimaryKeySelective(user);
                if (i > 0) {
                    insertUserPerfGroupWorkModifyOperateLog(perfWorkGroupIds, rUserPerformanceWorkGroupSyncVo.getData().getPerfWorkGroupId(), user, MyTokenUtils.Channel.dsp);
                }
            }
        } else if (rUserPerformanceWorkGroupSyncVo.getType().equals(SyncType.delete)) {

            if (user.getPerfWorkGroupId() != null) {
                PerformanceWorkGroup performanceWorkGroup = performanceWorkGroupMapper.selectByPrimaryKey(user.getPerfWorkGroupId());
                int i = userMapper.clearPerfWorkGroupIdByUserId(userId);
                if (i > 0) {
                    operateLogService.add(OperateLog.Type.user_performance_work_group_modify,
                            "dsp后台用户 在 " + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()) + " 将 " + user.getName() + " 从 " + performanceWorkGroup.getName() + performanceWorkGroup.getId() + " 删除",
                            null);
                }
            }

        }
        // 绩效负责人离职或调岗指定新的绩效负责人
        Long managerId = rUserPerformanceWorkGroupSyncVo.getData().getManagerId();
        if (null != managerId && !userId.equals(managerId)) {
            List<PerformanceWorkGroup> performanceWorkGroups = performanceWorkGroupMapper.selectAllByManagerId(userId);
            for (PerformanceWorkGroup performanceWorkGroup : performanceWorkGroups) {
                PerformanceWorkGroup performanceWorkGroupCond = new PerformanceWorkGroup();
                performanceWorkGroupCond.setId(performanceWorkGroup.getId());
                performanceWorkGroupCond.setPerformanceManagerId(managerId);
                performanceWorkGroupMapper.updateByPrimaryKeySelective(performanceWorkGroupCond);
            }

            // 清除之前负责人的角色
            List<PerformanceWorkGroup> manageGroups = performanceWorkGroupMapper.selectAllByManagerId(userId);
            if (null == manageGroups || manageGroups.isEmpty()) {
                deletePerformanceMenuPermByManagerId(userId);
            }

            // 如果没有,则添加负责人角色
            List<RUserProjectPermVo> perms = rUserProjectPermMapper.selectByUserIdAndProjectRoleId(managerId, ProjectRole.Role.performance_manager_id);
            if (perms == null || perms.isEmpty()) {
                RUserProjectPerm rUserProjectPerm = new RUserProjectPerm();
                rUserProjectPerm.setProjectRoleId(ProjectRole.Role.performance_manager_id);
                rUserProjectPerm.setUserId(managerId);
                rUserProjectPermMapper.insertSelective(rUserProjectPerm);
            }
        }
    }

    @Override
    public List<PerformanceFixMemberSimpleInfoVo> getPerformanceGroupMember(Long projectId) {
        List<PerformanceFixMemberSimpleInfoVo> fixMemberSimpleInfoVoList = new ArrayList<>();
        List<OrgWorkGroupMemberAppVo> memberAppVoList;
        if (projectId != null) {
            memberAppVoList = userMapper.selectAllEntityWithOrgWorkGroupSimpleByProjectId(projectId);
        } else {
            memberAppVoList = userMapper.selectAllEntityWithOrgWorkGroupSimple();
        }
        for (OrgWorkGroupMemberAppVo vo : memberAppVoList) {
            PerformanceFixMemberSimpleInfoVo fixMember = new PerformanceFixMemberSimpleInfoVo();
            fixMember.setId(vo.getUserId());
            fixMember.setName(vo.getName());
            fixMember.setLoginId(vo.getLoginId());
            fixMember.setPhotoAddress(vo.getPhoto());
            fixMemberSimpleInfoVoList.add(fixMember);
        }
        return fixMemberSimpleInfoVoList;
    }

    @Override
    public List<UserPerfWorkGroupVo> getUsers(String keyword) {

        // 关键字匹配 login_id 和 姓名
        List<UserPerfWorkGroupDto> userDtoList = userMapper.selectByUserNameKeyword(keyword);
        Map<Long, List<UserPerfWorkGroupDto>> userIdDtoMap = userDtoList.stream().collect(Collectors.groupingBy(UserPerfWorkGroupDto::getUserId));
        List<UserPerfWorkGroupVo> result = userPerfDtoMap2Vo(userIdDtoMap);
        result.sort((x, y) -> x.getLoginId().compareTo(y.getLoginId()));
        return result;
    }


    // map 转 vos
    private List<UserPerfWorkGroupVo> userPerfDtoMap2Vo(Map<Long, List<UserPerfWorkGroupDto>> userIdDtoMap) {
        List<UserPerfWorkGroupVo> result = new ArrayList<>();
        if (userIdDtoMap == null || userIdDtoMap.size() == 0) {
            return result;
        }

        List<PerformanceWorkGroup> perWGs = performanceWorkGroupMapper.selectAllActiveRecords();
        Map<Long, PerformanceWorkGroup> idPWGMap = perWGs.stream().collect(Collectors.toMap(PerformanceWorkGroup::getId, x -> x));
        for (Map.Entry<Long, List<UserPerfWorkGroupDto>> entry : userIdDtoMap.entrySet()) {

            UserPerfWorkGroupDto dto = entry.getValue().get(0);
            UserPerfWorkGroupVo vo = new UserPerfWorkGroupVo();

            BeanUtils.copyProperties(dto, vo);

            // 匹配绩效组信息
            if (dto.getPerformanceWorkGroupId() != null && dto.getPerformanceWorkGroupId() != 0) {
                UserPerfWorkGroupVo.MinGroup performanceWorkGroup = new UserPerfWorkGroupVo.MinGroup();
                performanceWorkGroup.setId(dto.getPerformanceWorkGroupId());
                performanceWorkGroup.setName(getPerfWorkGroupName(dto.getPerformanceWorkGroupId(), idPWGMap));
                vo.setPerformanceWorkGroup(performanceWorkGroup);
            }
            //匹配 管理组信息
            vo.setInChargeGroups(getManagePerfWG(entry.getValue(), idPWGMap));
            result.add(vo);
        }
        return result;
    }

    // 迭代查找绩效工作组名(西山居下属工作组默认去除'西山居')
    @Override
    public String getPerfWorkGroupName(Long perWorkGroupId, Map<Long, PerformanceWorkGroup> idPWGMap) {
        StringBuilder resultBuilder = new StringBuilder();
        if (idPWGMap == null || idPWGMap.size() == 0 || idPWGMap.get(perWorkGroupId) == null) {
            return "";
        }
        Long parent = idPWGMap.get(perWorkGroupId).getParent();
        String name = idPWGMap.get(perWorkGroupId).getName();
        resultBuilder.append(name);
        while (parent != null && !parent.equals(0L)) {
            String tempName = idPWGMap.get(parent).getName();
            resultBuilder.insert(0, tempName + "/");
            parent = idPWGMap.get(parent).getParent();
        }
        return resultBuilder.length() > 3 ? resultBuilder.substring(4) : resultBuilder.toString();
    }

    @Override
    public Long insertPerfUserCheckResult(PerfUserCheckResult insert) {
        Long currentUserId = MyTokenUtils.getCurrentUserId();
        insert.setCreateBy(currentUserId);
        insert.setCreateTime(new Date());
        perfUserCheckResultMapper.insertSelective(insert);
        return insert.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdatePerfUserCheckResult(List<PerfUserCheckResult> perfUserCheckResult) {

        User currentUser = MyTokenUtils.getCurrentUser();

        List<PerfUserCheckResult> batchInsert = new ArrayList<>();
        List<PerfUserCheckResult> batchUpdate = new ArrayList<>();

        for (PerfUserCheckResult userCheckResult : perfUserCheckResult) {
            userCheckResult.setCreateBy(currentUser.getId());
            userCheckResult.setCreateTime(new Date());
            if (userCheckResult.getId() == null) {
                batchInsert.add(userCheckResult);
                continue;
            }
            batchUpdate.add(userCheckResult);
        }
        if (batchInsert.size() > 0) {
            perfUserCheckResultMapper.batchInsert(batchInsert);
        }
        if (batchUpdate.size() > 0) {
            perfUserCheckResultMapper.batchUpdate(batchUpdate);
        }
    }

    @Override
    public int updateUserCheckResult(Long id, PerfUserCheckResult perfUserCheckResult) {
        User currentUser = MyTokenUtils.getCurrentUser();
        perfUserCheckResult.setCreateBy(currentUser.getId());
        perfUserCheckResult.setCreateTime(new Date());
        perfUserCheckResult.setId(id);
        int result = perfUserCheckResultMapper.updateByPrimaryKeySelective(perfUserCheckResult);
        return result;
    }

    private List<UserPerfWorkGroupVo.MinGroup> getManagePerfWG(List<UserPerfWorkGroupDto> dtos, Map<Long, PerformanceWorkGroup> idPWGMap) {
        List<UserPerfWorkGroupVo.MinGroup> resultGroup = new ArrayList<>();

        for (UserPerfWorkGroupDto dto : dtos) {
            if (dto.getManagePerfWGId() != null && dto.getManagePerfWGId() != 0) {
                UserPerfWorkGroupVo.MinGroup inCharge = new UserPerfWorkGroupVo.MinGroup();
                inCharge.setId(dto.getManagePerfWGId());
                inCharge.setName(getPerfWorkGroupName(dto.getManagePerfWGId(), idPWGMap));
                resultGroup.add(inCharge);
            }
        }
        return resultGroup;
    }

    @Override
    public WorkGroupCompVo comparePerfHr(Long perfWGId, Long hrWGId) {
        if (perfWGId == null || hrWGId == null) {
            throw new ParamException("参数值为空");
        }

        WorkGroupCompVo workGroupCompVo = new WorkGroupCompVo();
        List<WorkGroupCompVo.UserSimVo> perfRedundantMember = new ArrayList<>();
        List<WorkGroupCompVo.UserSimVo> hrRedundantMember = new ArrayList<>();
        List<WorkGroupCompVo.UserSimVo> hrMembers = getGroupMembers(User.GroupType.HrWorkGroup, hrWGId);
        List<WorkGroupCompVo.UserSimVo> perfMembers = getGroupMembers(User.GroupType.perfWorkGroup, perfWGId);
        Map<Long, WorkGroupCompVo.UserSimVo> hrIdMemberMap = matchIdUserMap(hrMembers);
        Map<Long, WorkGroupCompVo.UserSimVo> perfMemberMap = matchIdUserMap(perfMembers);

        Set<WorkGroupCompVo.UserSimVo> collectMembers = new HashSet<>();
        collectMembers.addAll(hrMembers);
        collectMembers.addAll(perfMembers);
        for (WorkGroupCompVo.UserSimVo member : collectMembers) {
            if (hrIdMemberMap.containsKey(member.getId()) && (!perfMemberMap.containsKey(member.getId()))) {
                hrRedundantMember.add(member);
            } else if ((!hrIdMemberMap.containsKey(member.getId())) && perfMemberMap.containsKey(member.getId())) {
                perfRedundantMember.add(member);
            }
        }
        workGroupCompVo.setHrRedundantMember(hrRedundantMember);
        workGroupCompVo.setPerfRedundantMember(perfRedundantMember);

        logger.info("人力组下的多余的人数为：" + hrRedundantMember.size());
        logger.info("绩效组下的多余的人数为：" + perfRedundantMember.size());
        return workGroupCompVo;
    }

    private List<WorkGroupCompVo.UserSimVo> getGroupMembers(String type, Long groupId) {
        logger.info("获取" + type + "下人员");
        List<WorkGroup> groups = null;
        List<Long> groupIds;
        String tableColumn = "";
        if (type.equals(User.GroupType.HrWorkGroup)) {
            tableColumn = User.GroupType.HrColumn;
            groups = workGroupService.getAllIdParentWorkGroup();
        } else if (type.equals(User.GroupType.perfWorkGroup)) {
            tableColumn = User.GroupType.perfColumn;
            groups = performanceWorkGroupMapper.selectAllIdParentWorkGroup();
        }
        if (groups == null || tableColumn.equals("")) {
            throw new ParamException("系统中不存在 id 为" + groupId + "的" + type + "组");
        }
        groupIds = workGroupService.getWorkGroupRelationByParent(groupId, groups);

        // 将自己id 添加入list
        groupIds.add(groupId);
        List<WorkGroupCompVo.UserSimVo> hrGroupUsers = userMapper.selectAllActiveUserInWorkGroup(tableColumn, groupIds);
        return hrGroupUsers == null ? new ArrayList<>() : hrGroupUsers;
    }

    private Map<Long, WorkGroupCompVo.UserSimVo> matchIdUserMap(List<WorkGroupCompVo.UserSimVo> list) {
        Map<Long, WorkGroupCompVo.UserSimVo> idUserMap = new HashMap<>();
        for (WorkGroupCompVo.UserSimVo vo : list) {
            if (!idUserMap.containsKey(vo.getId())) {
                idUserMap.put(vo.getId(), vo);
            }
        }
        return idUserMap;
    }
}
