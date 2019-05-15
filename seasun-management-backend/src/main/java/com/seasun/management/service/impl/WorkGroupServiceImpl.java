package com.seasun.management.service.impl;

import com.seasun.management.constant.ErrorCode;
import com.seasun.management.constant.ErrorMessage;
import com.seasun.management.constant.PerformanceServiceType;
import com.seasun.management.constant.SyncType;
import com.seasun.management.dto.HrWorkGroupDto;
import com.seasun.management.dto.*;
import com.seasun.management.exception.GroupPermissionException;
import com.seasun.management.exception.ParamException;
import com.seasun.management.helper.ReportHelper;
import com.seasun.management.mapper.*;
import com.seasun.management.model.*;
import com.seasun.management.service.PerformanceWorkGroupService;
import com.seasun.management.service.WorkGroupService;
import com.seasun.management.service.cache.CacheService;
import com.seasun.management.service.permission.FixMemberPermCollector;
import com.seasun.management.util.MyTokenUtils;
import com.seasun.management.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class WorkGroupServiceImpl extends AbstractSyncService implements WorkGroupService {

    private static final Logger logger = LoggerFactory.getLogger(WorkGroupServiceImpl.class);

    @Autowired
    WorkGroupMapper workGroupMapper;

    @Autowired
    RUserWorkGroupPermMapper rUserWorkGroupPermMapper;

    @Autowired
    RUserProjectPermMapper rUserProjectPermMapper;

    @Autowired
    UserPerformanceMapper userPerformanceMapper;

    @Autowired
    UserSalaryChangeMapper userSalaryChangeMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    PerformanceWorkGroupMapper performanceWorkGroupMapper;

    @Autowired
    FmMemberMapper fmMemberMapper;

    @Autowired
    FmUserRoleMapper fmUserRoleMapper;

    @Autowired
    FmGroupConfirmInfoMapper fmGroupConfirmInfoMapper;

    @Autowired
    ProjectMapper projectMapper;

    @Autowired
    PerformanceWorkGroupService performanceWorkGroupService;

    @Autowired
    private FixMemberPermCollector fixMemberPermCollector;

    @Autowired
    private PerfWorkGroupStatusMapper perfWorkGroupStatusMapper;

    @Autowired
    private CacheService cacheService;


    private static Map<String, Long> typeWorkGroupRoleMap = new HashMap<>();
    private static Map<String, Long> typeProjectRoleMap = new HashMap<>();

    static {
        typeWorkGroupRoleMap.put(PerformanceServiceType.performance, WorkGroupRole.Role.performance);
        typeWorkGroupRoleMap.put(PerformanceServiceType.salary, WorkGroupRole.Role.salary);
        typeWorkGroupRoleMap.put(PerformanceServiceType.grade, WorkGroupRole.Role.grade);

        typeProjectRoleMap.put(PerformanceServiceType.performance, ProjectRole.Role.performance_manager_id);
        typeProjectRoleMap.put(PerformanceServiceType.salary, ProjectRole.Role.salary_manager_id);
        typeProjectRoleMap.put(PerformanceServiceType.grade, ProjectRole.Role.grade_manager_id);
    }

    @Override
    public void sync(BaseSyncVo baseSyncVo) {
        if (!(baseSyncVo instanceof WorkGroupSyncVo)) {
            throw new ClassCastException("baseSynVo对象 不能转换为 WorkGroupSyncVo 类");
        }
        WorkGroupSyncVo workGroupSyncVo = (WorkGroupSyncVo) baseSyncVo;
        if (null == workGroupSyncVo.getData().getId()) {
            throw new ParamException("id can not be empty");
        }

        if (workGroupSyncVo.getType().equals(SyncType.add)) {
            workGroupMapper.insertWithId(workGroupSyncVo.getData());
        } else if (workGroupSyncVo.getType().equals(SyncType.update)) {
            workGroupMapper.updateByPrimaryKey(workGroupSyncVo.getData());
        } else if (workGroupSyncVo.getType().equals(SyncType.delete)) {
            workGroupMapper.deleteByPrimaryKey(workGroupSyncVo.getData().getId());
        }
        String cacheResult = cacheService.refreshCacheByCacheNameAndStoreKey(ITCache.HashKey.HRCONTACTROOTTREE, ITCache.EntryKey.HRCONTACTROOTTREE);
        if (cacheResult != null) {
            logger.error(String.format(ErrorMessage.CACHE_REFRESH_ERROR, ITCache.HashKey.HRCONTACTROOTTREE, ITCache.EntryKey.HRCONTACTROOTTREE, cacheResult));
        }

    }

    @Override
    // 获取rootId下面所有groupId
    public <T extends WorkGroup> Set<Long> getSubGroupIds(Long rootId, List<T> workGroups) {
        Map<Long, List<WorkGroup>> workGroupByParentIdMap = workGroups.stream().collect(Collectors.groupingBy(x -> x.getParent()));
        return getSubGroupIds(rootId, workGroupByParentIdMap);
    }

    @Override
    public <T extends WorkGroup> Set<Long> getSubGroupIds(Long groupId, Map<Long, List<T>> workGroupByParentIdMap) {
        Set<Long> result = new HashSet<>();
        if (workGroupByParentIdMap.containsKey(groupId)) {
            List<? extends WorkGroup> workGroups = workGroupByParentIdMap.get(groupId);
            for (WorkGroup workGroup : workGroups) {
                result.add(workGroup.getId());
                if (workGroupByParentIdMap.containsKey(workGroup.getId())) {
                    result.addAll(getSubGroupIds(workGroup.getId(), workGroupByParentIdMap));
                }
            }
        }
        return result;
    }

    @Override
    public List<WorkGroup> getWorkGroupRelationByChild(WorkGroup workGroup) {
        List<WorkGroup> result = new ArrayList<>();
        checkGroupRelation(workGroup, result);
        Collections.reverse(result);
        return result;
    }

    @Override
    public List<Long> getWorkGroupRelationByParent(Long parent, List<WorkGroup> allWorkGroup) {
        if (allWorkGroup == null || allWorkGroup.size() == 0) {
            return new ArrayList<>();
        }
        Map<Long, List<Long>> map = new HashMap<>();
        for (WorkGroup w : allWorkGroup) {
            Long parentTemp = w.getParent();
            Long id = w.getId();
            if (parentTemp != null && map.containsKey(parentTemp)) {
                map.get(parentTemp).add(id);
            } else if (parentTemp != null) {
                List<Long> idList = new ArrayList<>();
                idList.add(id);
                map.put(parentTemp, idList);
            }
        }
        List<Long> workGroupIdList = new ArrayList<>();
        List<Long> workGroups = map.get(parent);
        if (workGroups == null) {
            return null;
        }

        workGroupIdList.addAll(workGroups);
        while (true) {
            if (workGroups.size() == 0) {
                break;
            } else {
                List<Long> workGroupTemp = new ArrayList<>();
                workGroupTemp.addAll(workGroups);
                workGroups.clear();
                for (Long g : workGroupTemp) {
                    List<Long> wg = map.get(g);
                    if (wg != null) {
                        workGroups.addAll(wg);
                    }
                }
                workGroupIdList.addAll(workGroups);
            }
        }
        return workGroupIdList;
    }

    @Override
    public List<WorkGroup> getWorkGroupTree() {
        List<WorkGroup> allGroups = workGroupMapper.selectValidGroup();// 若查看所有组：workGroupMapper.selectAllByActive()
        return genGroupTree(allGroups);
    }


    @Override
    public List<WorkGroup> getWorkGroupTreeByProject(String projectName) {
        List<WorkGroup> groups = workGroupMapper.getGroupListByProject(projectName);
        return genGroupTree(groups);
    }


    private List<WorkGroup> genGroupTree(List<WorkGroup> inputGroups) {
        List<WorkGroup> rootGroups = new ArrayList<>();
        List<WorkGroup> noUserGroups = workGroupMapper.selectNoUserGroup();

        for (WorkGroup workGroup : inputGroups) {

            // 检查是否是子组
            boolean isSubGroup = false;
            for (WorkGroup g : inputGroups) {
                // 父组为空，则肯定不是子组
                if (null == workGroup.getParent()) {
                    break;
                }

                // 父组不为空，且父组在组群里，则为子组
                if (null != workGroup.getParent() && workGroup.getParent().equals(g.getId())) {
                    isSubGroup = true;
                    break;
                }
            }
            if (!isSubGroup) {
                rootGroups.add(workGroup);
            }

            for (WorkGroup g : inputGroups) {

                // 检查是否有激活的用户在此组
                g.setHasUserFlag(true);
                for (WorkGroup nouserGroup : noUserGroups) {
                    if (nouserGroup.getId().equals(g.getId())) {
                        g.setHasUserFlag(false);
                        break;
                    }
                }

                if (g.getParent() != null && g.getParent().equals(workGroup.getId())) {
                    if (workGroup.getChildren() == null) {
                        List<WorkGroup> myChildrens = new ArrayList<WorkGroup>();
                        myChildrens.add(g);
                        workGroup.setChildren(myChildrens);
                    } else {
                        workGroup.getChildren().add(g);
                    }
                }
            }
        }
        return rootGroups;
    }

    private void checkGroupRelation(WorkGroup workGroup, List<WorkGroup> result) {
        if (workGroup.getParent() == 0) {
            result.add(workGroup);
        } else {
            result.add(workGroup);
            WorkGroup parentWorkGroup = workGroupMapper.selectByPrimaryKey(workGroup.getParent());
            checkGroupRelation(parentWorkGroup, result);
        }
    }

    @Override
    public List<WorkGroup> getWorkGroupTreeByProjectId(Long projectId) {
        List<WorkGroup> allGroups = workGroupMapper.selectAllGroupByProjectId(projectId);
        return initWorkGroupTree(allGroups);
    }

    @Override
    public List<WorkGroup> getActiveWorkGroupTreeByProjectId(Long projectId) {
        List<WorkGroup> allGroups = workGroupMapper.selectAllActiveGroupByProjectId(projectId);
        return initWorkGroupTree(allGroups);
    }

    private List<WorkGroup> initWorkGroupTree(List<WorkGroup> allGroups){
        Map<Long, WorkGroup> groupMap = new HashMap<>();
        for (WorkGroup workGroup : allGroups) {
            groupMap.put(workGroup.getId(), workGroup);
        }

        List<WorkGroup> result = new ArrayList<>();
        // todo : 这里有个坑，当中间的某个人力组节点没有下属时，这条分支会断，他的有成员的子工作组节点会成为根组
        for (WorkGroup workGroup : allGroups) {
            //父组为空 或者 父组不在项目的所有组里面 就是根组
            if (null == workGroup.getParent() || !groupMap.containsKey(workGroup.getParent())) {
                setChildGroups(workGroup, allGroups);
                result.add(workGroup);
            }
        }
        return result;
    }

    @Override
    public WorkGroup getWorkGroupTreeByProjectIdAndGroupId(Long projectId, Long groupId) {
        List<WorkGroup> allGroups = workGroupMapper.selectAllGroupByProjectId(projectId);

        WorkGroup rootGroup = null;
        for (WorkGroup workGroup : allGroups) {
            if (groupId.equals(workGroup.getId())) {
                setChildGroups(workGroup, allGroups);
                rootGroup = workGroup;
                break;
            }
        }

        return rootGroup;
    }

    private void setChildGroups(WorkGroup parentGroup, List<WorkGroup> allGroups) {
        List<WorkGroup> childGroups = new ArrayList<>();
        for (WorkGroup workGroup : allGroups) {
            if (parentGroup.getId().equals(workGroup.getParent())) {
                setChildGroups(workGroup, allGroups);
                //子组人数加到父组人数里
                parentGroup.setMemberNumber(parentGroup.getMemberNumber() + workGroup.getMemberNumber());
                childGroups.add(workGroup);
            }
        }
        if (!childGroups.isEmpty()) {
            parentGroup.setChildren(childGroups);
        }
    }

    @Override
    public UserPerformanceIdentityAppVo getUserPerformanceIdentity(long userType) {
        UserPerformanceIdentityAppVo userPerformanceIdentityAppVo = new UserPerformanceIdentityAppVo();
        userPerformanceIdentityAppVo.setLeaderFlag(false);

        User user = MyTokenUtils.getCurrentUser();

        // 我的绩效权限
        userPerformanceIdentityAppVo.setMyPerformanceFlag(null != user.getPerfWorkGroupId());

        boolean isBoss = MyTokenUtils.isBoss(user);
        // 老K权限
        if (isBoss) {
            userPerformanceIdentityAppVo.setLeaderFlag(true);
            userPerformanceIdentityAppVo.setMyPerformanceFlag(false);
        }

        if (WorkGroupRole.Role.performance.equals(userType)) {
            List<PerformanceWorkGroup> workGroups = performanceWorkGroupMapper.selectAllByManagerId(user.getId());
            if (!workGroups.isEmpty()) {
                userPerformanceIdentityAppVo.setWorkGroups(new ArrayList<>());
            }
            for (PerformanceWorkGroup workGroup : workGroups) {
                UserPerformanceIdentityAppVo.WorkGroup identityWorkGroup = new UserPerformanceIdentityAppVo.WorkGroup();
                identityWorkGroup.setWorkGroupId(workGroup.getId());
                identityWorkGroup.setWorkGroupName(workGroup.getName());
                userPerformanceIdentityAppVo.getWorkGroups().add(identityWorkGroup);
            }
        } else {
            List<WorkGroup> workGroups = workGroupMapper.selectAllByLeaderAndGroupRoleId(user.getId(), userType);
            if (!workGroups.isEmpty()) {
                userPerformanceIdentityAppVo.setWorkGroups(new ArrayList<>());
            }
            for (WorkGroup workGroup : workGroups) {
                UserPerformanceIdentityAppVo.WorkGroup identityWorkGroup = new UserPerformanceIdentityAppVo.WorkGroup();
                identityWorkGroup.setWorkGroupId(workGroup.getId());
                identityWorkGroup.setWorkGroupName(workGroup.getName());
                userPerformanceIdentityAppVo.getWorkGroups().add(identityWorkGroup);
            }
        }
        userPerformanceIdentityAppVo.setFixMemberFlag(fixMemberPermCollector.hasPermission(user.getId()));
        return userPerformanceIdentityAppVo;
    }

    @Override
    public WorkGroupOrgVo getWorkGroupOrgMap() {
        List<WorkGroupOrgVo> workGroups = workGroupMapper.selectWithGroupInfo();

        // 获取根节点
        WorkGroupOrgVo rootGroup = null;
        for (WorkGroupOrgVo temp : workGroups) {
            if (temp.getName().equals("西山居")) {
                rootGroup = temp;
                break;
            }
        }

        buildGroupTree(rootGroup, workGroups);
        return rootGroup;
    }

    @Override
    public WorkGroupPerformanceVo getWorkGroupDetailById(Long id) {
        List<WorkGroupPerformanceVo> managerInfos = workGroupMapper.selectAllGroupWithManagerInfoById(id);
        WorkGroupPerformanceVo result = new WorkGroupPerformanceVo();
        result.setId(managerInfos.get(0).getId());
        result.setName(managerInfos.get(0).getName());
        result.setParent(managerInfos.get(0).getParent());
        result.setParentName(managerInfos.get(0).getParentName());
        result.setActiveFlag(managerInfos.get(0).getActiveFlag());

        //设置下属人力组总人数(不包含直接下属)
        List<WorkGroupMemberDto> totalMember = workGroupMapper.selectAllActiveWorkGroupMember();
        Map<Long, List<WorkGroupMemberDto>> totalMemberMap = totalMember.stream().filter(g -> null != g.getParent()).collect(Collectors.groupingBy(g -> g.getParent()));
        result.setMemberNumber(performanceWorkGroupService.getWorkGroupTotalMemberById(id, totalMemberMap).intValue());


        // 分组
        List<WorkGroupPerformanceVo.UserInfo> hrInfoList = new ArrayList<>();
        for (WorkGroupPerformanceVo temp : managerInfos) {
            if (WorkGroupRole.Role.hr.equals(temp.getWorkGroupRoleId())) {
                WorkGroupPerformanceVo.UserInfo hrInfo = new WorkGroupPerformanceVo.UserInfo();
                hrInfo.setLoginId(temp.getLoginId());
                hrInfo.setName(temp.getUserName());
                hrInfoList.add(hrInfo);
            }
            if (WorkGroupRole.Role.performance.equals(temp.getWorkGroupRoleId())) {
                WorkGroupPerformanceVo.UserInfo hrInfo = new WorkGroupPerformanceVo.UserInfo();
                hrInfo.setLoginId(temp.getLoginId());
                hrInfo.setName(temp.getUserName());
                result.setPerformanceManager(hrInfo);
            }
            if (WorkGroupRole.Role.salary.equals(temp.getWorkGroupRoleId())) {
                WorkGroupPerformanceVo.UserInfo hrInfo = new WorkGroupPerformanceVo.UserInfo();
                hrInfo.setLoginId(temp.getLoginId());
                hrInfo.setName(temp.getUserName());
                result.setSalaryChangeManager(hrInfo);
            }
            if (WorkGroupRole.Role.grade.equals(temp.getWorkGroupRoleId())) {
                WorkGroupPerformanceVo.UserInfo hrInfo = new WorkGroupPerformanceVo.UserInfo();
                hrInfo.setLoginId(temp.getLoginId());
                hrInfo.setName(temp.getUserName());
                result.setGradeChangeManager(hrInfo);
            }
        }
        result.setHrList(hrInfoList);
        return result;
    }

    @Override
    public List<WorkGroupPerformanceVo.UserInfo> getWorkGroupMemberById(Long id) {
        List<User> users = userMapper.selectByWorkGroupId(id);
        List<WorkGroupPerformanceVo.UserInfo> userInfos = new ArrayList<>();
        for (User user : users) {
            if (!user.getActiveFlag()) {
                continue;
            }

            WorkGroupPerformanceVo.UserInfo tmp = new WorkGroupPerformanceVo.UserInfo();
            tmp.setLoginId(user.getLoginId());
            tmp.setName(user.getLastName() + user.getFirstName());
            tmp.setPhoto(user.getPhoto());
            userInfos.add(tmp);
        }
        return userInfos;
    }

    @Override
    public int updateWorkGroupPermByType(Long groupId, Long userId, String type) {
        int result = 0;

        if (PerformanceServiceType.performance.equals(type)) {
            UserPerformance performanceRecord = userPerformanceMapper.selectUnfinishedRecordByWorkGroupId(groupId);
            if (performanceRecord != null) {
                throw new GroupPermissionException(ErrorCode.UNFINISHED_PERFORMANCE_RECORD_ERROR, "user performance is processing ,can not update group permission");
            }
        }

        if (PerformanceServiceType.salary.equals(type)) {
            UserSalaryChange salaryChangeRecord = userSalaryChangeMapper.selectUnfinishedRecordByWorkGroupId(groupId);
            if (salaryChangeRecord != null) {
                throw new GroupPermissionException(ErrorCode.UNFINISHED_SALARY_RECORD_ERROR, "user salary change is processing ,can not update group permission");
            }
        }


        RUserWorkGroupPerm rUserWorkGroupPerm = rUserWorkGroupPermMapper.selectByUserIdAndWorkGroupIdAndRoleId(
                groupId, typeWorkGroupRoleMap.get(type));

        // 存在则替换,不存在则插入
        if (rUserWorkGroupPerm == null) {
            RUserWorkGroupPerm temp = new RUserWorkGroupPerm();
            temp.setUserId(userId);
            temp.setWorkGroupId(groupId);
            temp.setWorkGroupRoleId(typeWorkGroupRoleMap.get(type));
            result = rUserWorkGroupPermMapper.insertSelective(temp);

        } else {
            rUserWorkGroupPerm.setUserId(userId);
            rUserWorkGroupPerm.setWorkGroupId(groupId);
            rUserWorkGroupPerm.setWorkGroupRoleId(typeWorkGroupRoleMap.get(type));
            result = rUserWorkGroupPermMapper.updateByPrimaryKeySelective(rUserWorkGroupPerm);
        }

        // 如果没有,则添加负责人角色
        List<RUserProjectPermVo> perms = rUserProjectPermMapper.selectByUserIdAndProjectRoleId(userId, typeProjectRoleMap.get(type));
        if (perms == null || perms.size() == 0) {
            RUserProjectPerm rUserProjectPerm = new RUserProjectPerm();
            rUserProjectPerm.setProjectRoleId(typeProjectRoleMap.get(type));
            rUserProjectPerm.setUserId(userId);
            rUserProjectPermMapper.insertSelective(rUserProjectPerm);
        }

        return result;
    }

    @Override
    public void deleteWorkGroupPermByType(Long groupId, String loginId, String type) {
        User user = userMapper.selectActiveUserByLoginId(loginId);
        rUserWorkGroupPermMapper.deleteByUserIdAndWorkGroupIdAndWorkGroupRoleId(user.getId(),
                groupId, typeWorkGroupRoleMap.get(type));
    }

    @Override
    public List<WorkGroup> getAllIdParentWorkGroup() {
        return workGroupMapper.selectAllIdParentWorkGroup();
    }

    @Override
    public List<WorkGroup> getAllActiveWorkGroup() {
        List<WorkGroup> workGroups = workGroupMapper.selectAllByActive();

        // 设置全路径名称
        Map<Long, WorkGroup> allGroupMap = workGroups.stream().collect(Collectors.toMap(g -> g.getId(), g -> g));
        for (WorkGroup workGroup : workGroups) {
            workGroup.setFullPathName(ReportHelper.getWorkGroupFullPathName(workGroup, allGroupMap));
        }

        workGroups.sort(Comparator.comparing(g -> g.getFullPathName()));

        return workGroups;
    }


    private void buildGroupTree(WorkGroupOrgVo parentGroup, List<WorkGroupOrgVo> allGroups) {
        List<WorkGroupOrgVo> childGroups = new ArrayList<>();
        for (WorkGroupOrgVo workGroup : allGroups) {
            if (parentGroup.getId().equals(workGroup.getParent())) {
                buildGroupTree(workGroup, allGroups);
                childGroups.add(workGroup);
            }
        }
        if (!childGroups.isEmpty()) {
            parentGroup.setChildren(childGroups);
        }
    }

    // @Cacheable(value = "cacheManager", key = "methodName")
    @Override
    public HrWorkGroupDto buildHrWorkGroupView() {
        List<HrWorkGroupDto> allGroups = workGroupMapper.selectAllActiveWithLeaderByHR();
        List<OrgWorkGroupMemberAppVo> allUsers = userMapper.selectAllEntityWithOrgWorkGroupSimpleWithIntern();

        HrWorkGroupDto rootWorkGroup = null;
        for (HrWorkGroupDto workGroupDto : allGroups) {
            if ("西山居".equals(workGroupDto.getName())) {
                rootWorkGroup = workGroupDto;
                break;
            }
        }
        initHrGroupTreeByRootGroup(rootWorkGroup, allGroups, allUsers, true);
        return rootWorkGroup;
    }

    @Override
    public void initHrGroupTreeByRootGroup(HrWorkGroupDto rootGroup, List<HrWorkGroupDto> allGroups, List<OrgWorkGroupMemberAppVo> allUsers, boolean allFlag) {
        Map<Long, List<HrWorkGroupDto>> allChildGroupsMap = new HashMap<>();
        Map<Long, List<OrgWorkGroupMemberAppVo>> workGroupUsersMap = new HashMap<>();
        Map<Long, OrgWorkGroupMemberAppVo> allUserMap = new HashMap<>();
        List<Long> leaderIds = new ArrayList<>();
        for (HrWorkGroupDto group : allGroups) {
            if (allChildGroupsMap.containsKey(group.getParent())) {
                allChildGroupsMap.get(group.getParent()).add(group);
            } else {
                List<HrWorkGroupDto> childGroups = new ArrayList<>();
                childGroups.add(group);
                allChildGroupsMap.put(group.getParent(), childGroups);
            }
        }
        for (OrgWorkGroupMemberAppVo user : allUsers) {
            if (workGroupUsersMap.containsKey(user.getWorkGroupId())) {
                workGroupUsersMap.get(user.getWorkGroupId()).add(user);
            } else {
                List<OrgWorkGroupMemberAppVo> workGroupUsers = new ArrayList<>();
                workGroupUsers.add(user);
                workGroupUsersMap.put(user.getWorkGroupId(), workGroupUsers);
            }
            allUserMap.put(user.getUserId(), user);
        }
        if(!allFlag){
            if (rootGroup.getLeaderIds() != null) {
                String[] tempUserIds = rootGroup.getLeaderIds().split(",");
                for (String tempUserId : tempUserIds) {
                    leaderIds.add(Long.parseLong(tempUserId));
                }
            }
        }
        trackHrGroupTreeForRootGroup(leaderIds, rootGroup, allChildGroupsMap, workGroupUsersMap, allUserMap, allFlag);
    }

    private List<Long> getLeaderIds(String idStr) {
        List<Long> result = new ArrayList<>();
        if (idStr == null) {
            return result;
        }
        String[] tempUserIds = idStr.split(",");
        for (String tempUserId : tempUserIds) {
            result.add(Long.parseLong(tempUserId));
        }
        return result;
    }

    @Override
    public void trackHrGroupTreeForRootGroup(List<Long> leaderIds, HrWorkGroupDto rootGroup, Map<Long, List<HrWorkGroupDto>> allChildGroupsMap,
                                              Map<Long, List<OrgWorkGroupMemberAppVo>> workGroupUsersMap, Map<Long, OrgWorkGroupMemberAppVo> allUserMap, boolean allFlag) {
        // 组下所有下属人数
        int totalRegular = 0;
        int totalIntern = 0;

        // 设置负责人
        if (null != rootGroup.getLeaderIds()) {
            rootGroup.setLeaders(new ArrayList<>());
            String[] tempUserIds = rootGroup.getLeaderIds().split(",");
            for (String tempUserId : tempUserIds) {
                if (allUserMap.containsKey(Long.valueOf(tempUserId))) {
                    OrgWorkGroupMemberAppVo leader = allUserMap.get(Long.valueOf(tempUserId));
                    rootGroup.getLeaders().add(leader);
                }
            }
        }

        // 1.设置子组
        if (allChildGroupsMap.containsKey(rootGroup.getId())) {
            List<HrWorkGroupDto> childGroups = allChildGroupsMap.get(rootGroup.getId());
            for (HrWorkGroupDto workGroup : childGroups) {
                boolean stopFlag = false;
                if (!allFlag && workGroup.getLeaderIds() != null) {
                    for (Long id : getLeaderIds(workGroup.getLeaderIds())) {
                        for (Long leaderId : leaderIds) {
                            if (leaderId.equals(id)) {
                                stopFlag = true;
                                break;
                            }
                        }
                        if (stopFlag) {
                            break;
                        }
                    }
                }
                if (stopFlag) {
                    continue;
                }
                trackHrGroupTreeForRootGroup(leaderIds, workGroup, allChildGroupsMap, workGroupUsersMap, allUserMap, allFlag);
                totalIntern += workGroup.getInternNumber();
                totalRegular += workGroup.getMemberNumber();
            }
            rootGroup.setChildWorkGroups(childGroups);
        } else {
            rootGroup.setChildWorkGroups(new ArrayList<>());
        }

        // 2.设置成员
        if (workGroupUsersMap.containsKey(rootGroup.getId())) {
            List<OrgWorkGroupMemberAppVo> members = workGroupUsersMap.get(rootGroup.getId());
            rootGroup.setMembers(members);
            long internCount = members.stream().filter(member -> "实习".equals(member.getWorkStatus())).count();
            long regularCount = members.size() - internCount;
            totalIntern += internCount;
            totalRegular += regularCount;
        } else {
            rootGroup.setMembers(new ArrayList<>());
        }
        rootGroup.setMemberNumber((int) totalRegular);
        rootGroup.setInternNumber((int) totalIntern);
    }

    @Override
    public List<WorkGroupUserVo> getWorkGroupUserByKeyword(String keyword) {
        List<User> users = userMapper.getWorkGroupUserByUserNameKeyword(keyword);
        List<WorkGroupHrDto> workGroupHrDtos = workGroupMapper.getActiveWorkGroupByRole(WorkGroupRole.Role.hr);
        Map<Long, WorkGroup> workGroupByIdMap = workGroupHrDtos.stream().collect(Collectors.toMap(g -> g.getId(), g -> g, (oleValue, newValue) -> oleValue));
        Map<Long, List<WorkGroupHrDto>> workGroupsByHrIdMap = workGroupHrDtos.stream().filter(g -> null != g.getHrId()).collect(Collectors.groupingBy(g -> g.getHrId()));
        List<WorkGroupUserVo> result = new ArrayList<>();
        for (User user : users) {
            WorkGroupUserVo workGroupUserVo = new WorkGroupUserVo();
            workGroupUserVo.setUserId(user.getId());
            workGroupUserVo.setLoginId(user.getLoginId());
            workGroupUserVo.setUserName(user.getLastName() + user.getFirstName());
            if (null != user.getWorkGroupId()) {
                IdNameBaseObject workGroup = new IdNameBaseObject();
                workGroup.setId(user.getWorkGroupId());
                workGroup.setName(getWorkGroupFullNameById(user.getWorkGroupId(), workGroupByIdMap));
                workGroupUserVo.setWorkGroup(workGroup);
            }
            if (workGroupsByHrIdMap.containsKey(user.getId())) {
                List<IdNameBaseObject> managerWorkGroups = new ArrayList<>();
                List<WorkGroupHrDto> manageWorkgroups = workGroupsByHrIdMap.get(user.getId());
                for (WorkGroupHrDto workGroupHrDto : manageWorkgroups) {
                    IdNameBaseObject managerWorkGroup = new IdNameBaseObject();
                    managerWorkGroup.setId(workGroupHrDto.getId());
                    managerWorkGroup.setName(getWorkGroupFullNameById(workGroupHrDto.getId(), workGroupByIdMap));
                    managerWorkGroups.add(managerWorkGroup);
                }
                workGroupUserVo.setManageWorkGroups(managerWorkGroups);
            }
            result.add(workGroupUserVo);
        }
        return result;
    }

    //获取人力工作组全路径名称
    @Override
    public String getWorkGroupFullNameById(Long id, Map<Long, WorkGroup> workGroupByIdMap) {
        StringBuilder resultBuilder = new StringBuilder();
        WorkGroup workGroup = workGroupByIdMap.get(id);
        if (workGroup == null) {
            return "";
        }
        Long parent = workGroup.getParent();
        resultBuilder.append(workGroup.getName());
        while (parent != null && !parent.equals(0L)) {
            String tempName = workGroupByIdMap.get(parent).getName();
            resultBuilder.insert(0, tempName + "/");
            parent = workGroupByIdMap.get(parent).getParent();
        }
        return resultBuilder.length() > 3 ? resultBuilder.substring(4) : resultBuilder.toString();
    }


    @Override
    public Map<Long, String> getWorkGroupsIdNamePairFromCache () {
        return getWorkGroupsIdNamePair();
    }

    @Override
    public void addWorkGroupManager(RUserWorkGroupPerm rUserWorkGroupPerm) {
        rUserWorkGroupPerm.setWorkGroupRoleId(WorkGroupRole.Role.hr);
        rUserWorkGroupPermMapper.insert(rUserWorkGroupPerm);
    }


    @Override
    public Map<Long, String> refreshWorkGroupsIdNamePairIntoCache() {
        return getWorkGroupsIdNamePair();
    }

    public Map<Long, String> getWorkGroupsIdNamePair () {

        List<WorkGroup> workGroupList =  workGroupMapper.selectAllGroup();

        Map<Long, WorkGroup> groups = new LinkedHashMap<>();

        Map<Long, String> groupIdNamePair = new LinkedHashMap<>();

        /*
         * {
         *   91:  301,
         *   301: 101,
         *   101: 505,
         *   505: 0
         * }
         * */
        workGroupList.stream().forEach(workGroup -> {
            groups.put (workGroup.getId(), workGroup);
        });

        /**
         * {
         *     "91":[80,70,55,0]
         * }
         * */
        workGroupList.stream().forEach(workGroup -> {

            if (workGroup.getActiveFlag()) {

                Long id =  workGroup.getId(); // 91
                Long parent =  groups.get(id).getParent(); //301
                List<Long> parents = new ArrayList<>();
                if (parent!=0 ) parents.add (parent);
                while(parent != 0) {
                    parent = groups.get(parent).getParent();
                    if (parent!=0 ) parents.add (parent);
                }
                StringBuilder stringBuilder = new StringBuilder();
                Collections.reverse(parents);

                parents.stream().forEach(item -> {
                    // 名字太长，过滤西山居
                    if (item != 267)
                        stringBuilder.append(groups.get(item).getName()).append("/");
                });

                stringBuilder.append(workGroup.getName());

                groupIdNamePair.put(id, stringBuilder.toString());

            }

        });


        Map<Long,String> sort = groupIdNamePair.entrySet()
                .stream()
                .sorted(Map.Entry.<Long, String>comparingByValue())
                .collect(Collectors.toMap(k -> k.getKey(), v -> v.getValue(),
                        (k, v) -> k, LinkedHashMap::new));
        return sort;

    }


}
