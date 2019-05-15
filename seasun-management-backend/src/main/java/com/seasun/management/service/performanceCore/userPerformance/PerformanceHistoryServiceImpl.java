package com.seasun.management.service.performanceCore.userPerformance;

import com.seasun.management.dto.*;
import com.seasun.management.exception.ParamException;
import com.seasun.management.helper.UserPerformanceHelper;
import com.seasun.management.mapper.*;
import com.seasun.management.model.*;
import com.seasun.management.service.performanceCore.historyTrack.PerformanceHistoryTrackService;
import com.seasun.management.util.MyTokenUtils;
import com.seasun.management.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service(value = "history")
public class PerformanceHistoryServiceImpl implements PerformanceService {

    @Autowired
    PerformanceHistoryTrackService historyTrackService;

    @Autowired
    PerformanceWorkGroupMapper performanceWorkGroupMapper;

    @Autowired
    CfgPerfHideManagerCommentMapper cfgPerfHideManagerCommentMapper;

    @Autowired
    UserPerformanceMapper userPerformanceMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    ProjectMapper projectMapper;

    @Autowired
    FmGroupConfirmInfoMapper fmGroupConfirmInfoMapper;

    @Autowired
    private FmUserRoleMapper fmUserRoleMapper;

    @Autowired
    private PerfWorkGroupStatusMapper perfWorkGroupStatusMapper;

    @Override
    public SubPerformanceAppVo getSubPerformance(Long userId, Long workGroupId, Integer year, Integer month, String filter) {
        List<PerformanceUserDto> allUsers = userMapper.selectAllEntityWithPerformanceByPerformanceWorkGroup();
        List<WorkGroupDto> allPerformanceGroups = performanceWorkGroupMapper.selectAll();
        List<WorkGroupDto> allActivePerformanceGroups = allPerformanceGroups.stream().filter(g -> g.getActiveFlag()).collect(Collectors.toList());

        Map<Long, WorkGroupDto> allWorkGroupMap = allPerformanceGroups.stream().collect(Collectors.toMap(g -> g.getId(), g -> g));
        Map<Long, WorkGroupDto> activeWorkGroupMap = allActivePerformanceGroups.stream().collect(Collectors.toMap(g -> g.getId(), g -> g));

        // 直接管理组
        List<Long> managerGroupIds = new ArrayList<>();
        if (null != userId) {
            for (WorkGroupDto workGroupDto : allActivePerformanceGroups) {
                if (userId.equals(workGroupDto.getLeaderId())) {
                    managerGroupIds.add(workGroupDto.getId());
                }
            }
        } else {
            managerGroupIds.add(workGroupId);
        }

        // 获取当前管理的所有下属成员userId，以便后面计算 “已离组”。
        List<WorkGroupCompVo.UserSimVo> allUserSimVo = PerformanceTreeHelper.getAllUsersByRootWorkGroupId(managerGroupIds, allPerformanceGroups);
        List<Long> allMemberUserIds = new ArrayList<>();
        for (WorkGroupCompVo.UserSimVo simVo : allUserSimVo) {
            allMemberUserIds.add(simVo.getId());
        }

        List<UserPerformanceDto> allMonthRecords = userPerformanceMapper.selectAllByYearAndMonth(year, month);
        // fixme 这里出来数量对不上了
        List<UserPerformanceDto> allWorkGroupUserPerformances = historyTrackService.getAllHistoryMembersByWorkGroupIdAndTime(managerGroupIds, year, month, allMonthRecords);
        List<CfgPerfHideManagerComment> cfgPerfHideManagerComments = cfgPerfHideManagerCommentMapper.selectAll();
        List<PerformanceWorkGroupDto> allPerformanceWorkGroups = performanceWorkGroupMapper.selectAllWithProjectIdByActive();
        List<PerformanceWorkGroupDto> managerGroups;
        if (null != userId) {
            managerGroups = allPerformanceWorkGroups.stream().filter(g -> userId.equals(g.getPerformanceManagerId())).collect(Collectors.toList());
        } else {
            managerGroups = allPerformanceWorkGroups.stream().filter(g -> workGroupId.equals(g.getId())).collect(Collectors.toList());
        }
        List<YearMonthDto> completeYearMonth = userPerformanceMapper.selectAllWithYearMonthByComplete();

        List<PerfWorkGroupStatus> perfWorkGroupStatuses = perfWorkGroupStatusMapper.selectByYearAndMonth(year, month);
        Map<Long, String> performanceWorkGroupStatusMap = PerformanceDataHelper.getPerformanceWorkGroupStatusMap(allPerformanceWorkGroups, perfWorkGroupStatuses);

        // 获取所有绩效成员
        boolean projectConfirmFlag = false;
        Map<Long, PerformanceUserDto> allMemberMap = new HashMap<>();
        for (PerformanceWorkGroupDto managerGroup : managerGroups) {
            WorkGroupDto rootWorkGroup = PerformanceTreeHelper.getWorkGroupTree(managerGroup.getId(), allActivePerformanceGroups, allUsers, true);
            List<PerformanceUserDto> allMembers = PerformanceTreeHelper.getAllMembersByRootWorkGroup(rootWorkGroup);
            for (PerformanceUserDto member : allMembers) {
                if (!allMemberMap.containsKey(member.getUserId())) {
                    allMemberMap.put(member.getUserId(), member);
                }
            }

            if (managerGroup.getProjectConfirmFlag()) {
                projectConfirmFlag = true;
            }
        }

        // 判断当前月绩效是否已完成
        boolean isComplete = PerformanceDataHelper.checkComplete(year, month, completeYearMonth);

        // 按work_group_id分组
        Map<Long, List<UserPerformanceDto>> workGroupUserPerformancesMap = allWorkGroupUserPerformances.stream().collect(Collectors.groupingBy(p -> p.getWorkGroupId()));

        // 生成api返回数据
        SubPerformanceAppVo subPerformanceAppVo = new SubPerformanceAppVo();

        SubPerformanceAppVo.Profile profile = getSubPerformanceProfile(allWorkGroupUserPerformances, managerGroupIds);
        subPerformanceAppVo.setProfile(profile);

        List<MemberPerformanceAppVo> directMemberPerformanceAppVos = new ArrayList<>();
        List<Long> subWorkGroupIds = new ArrayList<>();
        for (UserPerformanceDto userPerformanceDto : allWorkGroupUserPerformances) {
            // 直接下属
            if (managerGroupIds.contains(userPerformanceDto.getWorkGroupId())) {
                boolean isHideManagerComment = UserPerformanceHelper.isHideManagerComment(cfgPerfHideManagerComments, allPerformanceWorkGroups, userPerformanceDto.getWorkGroupId(), userPerformanceDto.getYear(), userPerformanceDto.getMonth());
                directMemberPerformanceAppVos.add(PerformanceDataHelper.getMemberPerformanceAppVo(userPerformanceDto, !isHideManagerComment, !allMemberUserIds.contains(userPerformanceDto.getUserId()), null != userPerformanceDto.getFmProjectId(), null));
            }

            // 下属团队
            if (managerGroupIds.contains(userPerformanceDto.getParentGroup()) && !managerGroupIds.contains(userPerformanceDto.getWorkGroupId())) {
                if (!subWorkGroupIds.contains(userPerformanceDto.getWorkGroupId())) {
                    subWorkGroupIds.add(userPerformanceDto.getWorkGroupId());
                }
            }
        }

        subPerformanceAppVo.setManagerPerformanceList(PerformanceDataHelper.filerMemberPerformances(directMemberPerformanceAppVos, filter));

        SubPerformanceAppVo.SubGroupStatistics subGroupStatistics = new SubPerformanceAppVo.SubGroupStatistics();
        subGroupStatistics.setTotal(subWorkGroupIds.size());
        int memberCount = 0;
        subGroupStatistics.setData(new ArrayList<>());
        for (Long subWorkGroupId : subWorkGroupIds) {
            List<UserPerformanceDto> allSubUserPerformances = PerformanceDataHelper.getSubUserPerformances(subWorkGroupId, allWorkGroupUserPerformances);

            WorkGroupDto subWorkGroup = getRootWorkGroup(subWorkGroupId, activeWorkGroupMap, allWorkGroupMap, allActivePerformanceGroups, allUsers);

            PerformanceWorkGroupInfoAppVo performanceWorkGroupInfoAppVo = getPerformanceWorkGroupInfo(subWorkGroup, allMemberMap, allSubUserPerformances, workGroupUserPerformancesMap, isComplete, filter, cfgPerfHideManagerComments, allPerformanceWorkGroups, performanceWorkGroupStatusMap);
            memberCount += performanceWorkGroupInfoAppVo.getTotal();

            subGroupStatistics.getData().add(performanceWorkGroupInfoAppVo);
        }
        subGroupStatistics.getData().sort(Comparator.comparing(x -> x.getGroupName()));
        subGroupStatistics.setMemberCount(memberCount);
        subPerformanceAppVo.setSubGroup(subGroupStatistics);

        // 历史记录
        List<SubPerformanceAppVo.HistoryInfo> historyInfos = PerformanceDataHelper.getWorkGroupHistory(userId, workGroupId);
        subPerformanceAppVo.setHistory(historyInfos);

        // 筛选项
        subPerformanceAppVo.setFilter(UserPerformanceHelper.getHistoryFilter(projectConfirmFlag));

        return subPerformanceAppVo;
    }

    @Override
    public SubFixMemberPerformanceVo getSubFixMemberPerformance(Long userId, Integer year, Integer month) {
        List<PerformanceWorkGroupDto> allPerformanceWorkGroups = performanceWorkGroupMapper.selectAllWithProjectIdByActive();
        List<PerformanceWorkGroupDto> managerGroups = allPerformanceWorkGroups.stream().filter(g -> userId.equals(g.getPerformanceManagerId())).collect(Collectors.toList());
        List<Long> managerGroupIds = new ArrayList<>();
        boolean projectConfirmFlag = false;
        for (PerformanceWorkGroup performanceWorkGroup : managerGroups) {
            managerGroupIds.add(performanceWorkGroup.getId());
            if (performanceWorkGroup.getProjectConfirmFlag()) {
                projectConfirmFlag = true;
            }
        }

        List<UserPerformanceDto> allMonthRecords = userPerformanceMapper.selectAllByYearAndMonth(year, month);
        List<UserPerformanceDto> allWorkGroupUserPerformances = historyTrackService.getAllHistoryMembersByWorkGroupIdAndTime(managerGroupIds, year, month, allMonthRecords);
        List<CfgPerfHideManagerComment> cfgPerfHideManagerComments = cfgPerfHideManagerCommentMapper.selectAll();
        List<PerformanceUserDto> allUsers = userMapper.selectAllEntityWithPerformanceByPerformanceWorkGroup();
        List<WorkGroupDto> allPerformanceGroups = performanceWorkGroupMapper.selectAllByActive();
        List<FmGroupConfirmInfoVo> allFmGroupConfirmInfoList = fmGroupConfirmInfoMapper.selectAllByYearMonth(year, month);

        // 找出所有下属
        Map<Long, PerformanceUserDto> memberMap = new HashMap<>();
        for (Long managerGroupId : managerGroupIds) {
            WorkGroupDto rootWorkGroup = PerformanceTreeHelper.getWorkGroupTree(managerGroupId, allPerformanceGroups, allUsers, true);
            List<PerformanceUserDto> managerMembers = PerformanceTreeHelper.getAllMembersByRootWorkGroup(rootWorkGroup);
            for (PerformanceUserDto performanceUser : managerMembers) {
                if (!memberMap.containsKey(performanceUser.getUserId())) {
                    memberMap.put(performanceUser.getUserId(), performanceUser);
                }
            }
        }

        // 筛选当前固化组
        List<FmGroupConfirmInfoVo> currentFmGroupConfirmInfoList = allFmGroupConfirmInfoList.stream().filter(i -> managerGroupIds.contains(i.getPerformanceWorkGroupId())).collect(Collectors.toList());
        // 按平台划分
        Map<Long, List<FmGroupConfirmInfoVo>> platFmGroupConfirmInfoListMap = currentFmGroupConfirmInfoList.stream().collect(Collectors.groupingBy(i -> i.getPlatId()));
        Map<Long, String> platNameMap = PerformanceDataHelper.getPlatNameMap(currentFmGroupConfirmInfoList);
        Map<Long, String> projectNameMap = PerformanceDataHelper.getProjectNameMap(currentFmGroupConfirmInfoList);

        SubFixMemberPerformanceVo subFixMemberPerformanceVo = new SubFixMemberPerformanceVo();

        SubPerformanceAppVo.Profile profile = getSubPerformanceProfile(allWorkGroupUserPerformances, managerGroupIds);
        subFixMemberPerformanceVo.setProfile(profile);

        subFixMemberPerformanceVo.setPlats(new ArrayList<>());
        int total = 0;
        for (Map.Entry<Long, List<FmGroupConfirmInfoVo>> platEntry : platFmGroupConfirmInfoListMap.entrySet()) {
            int platTotal = 0;
            SubFixMemberPerformanceVo.Plat plat = new SubFixMemberPerformanceVo.Plat();
            plat.setName(platNameMap.get(platEntry.getKey()));
            plat.setProjects(new ArrayList<>());
            List<FmGroupConfirmInfoVo> platFmGroupConfirmInfoList = platEntry.getValue();
            for (FmGroupConfirmInfoVo fmGroupConfirmInfo : platFmGroupConfirmInfoList) {
                List<UserPerformanceDto> projectWorkGroupUserPerformances = allWorkGroupUserPerformances.stream().filter(p -> fmGroupConfirmInfo.getProjectId().equals(p.getFmProjectId())).collect(Collectors.toList());
                List<MemberPerformanceAppVo> projectMemberPerformances = new ArrayList<>();
                for (UserPerformanceDto userPerformance : projectWorkGroupUserPerformances) {
                    boolean isHideManagerComment = UserPerformanceHelper.isHideManagerComment(cfgPerfHideManagerComments, allPerformanceWorkGroups, userPerformance.getWorkGroupId(), userPerformance.getYear(), userPerformance.getMonth());
                    projectMemberPerformances.add(PerformanceDataHelper.getMemberPerformanceAppVo(userPerformance, !isHideManagerComment, !memberMap.containsKey(userPerformance.getUserId()), null != userPerformance.getFmProjectId(), null));
                }
                projectMemberPerformances.sort((x, y) -> {
                    int i = x.getSort().compareTo(y.getSort());
                    if (i == 0) {
                        i = x.getLoginId().compareTo(y.getLoginId());
                    }
                    return i;
                });

                SubFixMemberPerformanceVo.Project project = new SubFixMemberPerformanceVo.Project();
                project.setName(projectNameMap.get(fmGroupConfirmInfo.getProjectId()));
                project.setTotal(projectMemberPerformances.size());
                project.setConfirmFlag(FmGroupConfirmInfo.Status.confirmed.equals(fmGroupConfirmInfo.getStatus()));
                project.setManager(fmGroupConfirmInfo.getProjectConfirmerName());
                project.setPerformances(projectMemberPerformances);
                plat.getProjects().add(project);
                platTotal += projectMemberPerformances.size();
            }
            plat.setTotal(platTotal);
            subFixMemberPerformanceVo.getPlats().add(plat);
            total += platTotal;
        }
        subFixMemberPerformanceVo.setTotal(total);

        // 历史记录
        List<SubPerformanceAppVo.HistoryInfo> historyInfos = PerformanceDataHelper.getWorkGroupHistory(userId, null);
        subFixMemberPerformanceVo.setHistory(historyInfos);

        // 筛选
        subFixMemberPerformanceVo.setFilter(UserPerformanceHelper.getHistoryFilter(projectConfirmFlag));

        return subFixMemberPerformanceVo;
    }

    @Override
    public WorkGroupMemberPerformanceAppVo getWorkGroupMemberPerformance(Long observerUserId, Long performanceGroupId, Integer year, Integer month) {
        Long userId;
        if (null == observerUserId) {
            userId = MyTokenUtils.getCurrentUserId();
        } else {
            userId = observerUserId;
        }
        WorkGroupMemberPerformanceAppVo workGroupMemberPerformanceAppVo = new WorkGroupMemberPerformanceAppVo();

        List<Long> managerGroupId = new ArrayList<>();
        managerGroupId.add(performanceGroupId);
        List<UserPerformanceDto> allMonthRecords = userPerformanceMapper.selectAllByYearAndMonth(year, month);
        List<UserPerformanceDto> userPerformances = historyTrackService.getAllHistoryMembersByWorkGroupIdAndTime(managerGroupId, year, month, allMonthRecords);
        List<CfgPerfHideManagerComment> cfgPerfHideManagerComments = cfgPerfHideManagerCommentMapper.selectAll();
        List<PerformanceWorkGroupDto> allPerformanceWorkGroups = performanceWorkGroupMapper.selectAllWithProjectIdByActive();
        List<PerformanceWorkGroupDto> managerGroups = allPerformanceWorkGroups.stream().filter(g -> userId.equals(g.getPerformanceManagerId())).collect(Collectors.toList());
        List<YearMonthDto> completeYearMonth = userPerformanceMapper.selectAllWithYearMonthByComplete();
        List<PerformanceUserDto> allUsers = userMapper.selectAllEntityWithPerformanceByPerformanceWorkGroup();
        List<WorkGroupDto> allPerformanceGroups = performanceWorkGroupMapper.selectAll();
        List<PerfWorkGroupStatus> perfWorkGroupStatuses = perfWorkGroupStatusMapper.selectByYearAndMonth(year, month);
        Map<Long, String> performanceWorkGroupStatusMap = PerformanceDataHelper.getPerformanceWorkGroupStatusMap(allPerformanceWorkGroups, perfWorkGroupStatuses);
        List<WorkGroupDto> allActivePerformanceGroups = allPerformanceGroups.stream().filter(g -> g.getActiveFlag()).collect(Collectors.toList());

        Map<Long, WorkGroupDto> allWorkGroupMap = allPerformanceGroups.stream().collect(Collectors.toMap(g -> g.getId(), g -> g));
        Map<Long, WorkGroupDto> activeWorkGroupMap = allActivePerformanceGroups.stream().collect(Collectors.toMap(g -> g.getId(), g -> g));
        // 获取所有绩效成员
        Map<Long, PerformanceUserDto> allMemberMap = new HashMap<>();
        for (PerformanceWorkGroupDto managerGroup : managerGroups) {
            WorkGroupDto rootWorkGroup = PerformanceTreeHelper.getWorkGroupTree(managerGroup.getId(), allActivePerformanceGroups, allUsers, true);
            List<PerformanceUserDto> allMembers = PerformanceTreeHelper.getAllMembersByRootWorkGroup(rootWorkGroup);
            for (PerformanceUserDto member : allMembers) {
                if (!allMemberMap.containsKey(member.getUserId())) {
                    allMemberMap.put(member.getUserId(), member);
                }
            }
        }

        // 按work_group_id分组
        Map<Long, List<UserPerformanceDto>> workGroupUserPerformancesMap = userPerformances.stream().collect(Collectors.groupingBy(p -> p.getWorkGroupId()));

        WorkGroupDto rootWorkGroup = getRootWorkGroup(performanceGroupId, activeWorkGroupMap, allWorkGroupMap, allActivePerformanceGroups, allUsers);

        // 判断当前月绩效是否已完成
        boolean isComplete = PerformanceDataHelper.checkComplete(year, month, completeYearMonth);
        PerformanceWorkGroupInfoAppVo performanceWorkGroupInfoAppVo = getPerformanceWorkGroupInfo(rootWorkGroup, allMemberMap, userPerformances, workGroupUserPerformancesMap, isComplete, UserPerformanceHelper.SearchFilter.all, cfgPerfHideManagerComments, allPerformanceWorkGroups, performanceWorkGroupStatusMap);

        workGroupMemberPerformanceAppVo.setManagerPerformanceList(performanceWorkGroupInfoAppVo.getManagerPerformanceList());
        performanceWorkGroupInfoAppVo.setManagerPerformanceList(null);
        workGroupMemberPerformanceAppVo.setGroupInfo(performanceWorkGroupInfoAppVo);

        return workGroupMemberPerformanceAppVo;
    }

    private PerformanceWorkGroupInfoAppVo getPerformanceWorkGroupInfo(WorkGroupDto performanceWorkGroup, Map<Long, PerformanceUserDto> allMemberMap, List<UserPerformanceDto> allWorkGroupUserPerformances,
                                                                      Map<Long, List<UserPerformanceDto>> workGroupUserPerformancesMap, Boolean isComplete, String filter,
                                                                      List<CfgPerfHideManagerComment> cfgPerfHideManagerComments, List<PerformanceWorkGroupDto> performanceWorkGroups, Map<Long, String> perfWorkGroupStatusMap) {
        // 不参与人数
        int memberForInvalided = 0;
        for (UserPerformanceDto userPerformanceDto : allWorkGroupUserPerformances) {
            if (UserPerformance.Performance.invalided.equals(userPerformanceDto.getFinalPerformance())) {
                memberForInvalided++;
            }
        }

        PerformanceWorkGroupInfoAppVo.PerformancePro subPerformancePro = PerformanceDataHelper.getPerformancePro(allWorkGroupUserPerformances);

        // 获取直接下属已存在绩效，计算组状态
        String status = SubPerformanceAppVo.HistoryInfo.Status.processing;
        if (null != isComplete && isComplete) {
            status = SubPerformanceAppVo.HistoryInfo.Status.complete;
        } else {
            if (perfWorkGroupStatusMap.containsKey(performanceWorkGroup.getId())) {
                status = perfWorkGroupStatusMap.get(performanceWorkGroup.getId());
            } /*{
                for (UserPerformanceDto userPerformanceDto : workGroupUserPerformancesMap.get(performanceWorkGroup.getId())) {
                    if (UserPerformance.Status.complete.equals(userPerformanceDto.getStatus())) {
                        status = SubPerformanceAppVo.HistoryInfo.Status.complete;
                        break;
                    } else if (UserPerformance.Status.locked.equals(userPerformanceDto.getStatus())) {
                        status = SubPerformanceAppVo.HistoryInfo.Status.submitted;
                        break;
                    }
                }
            }*/
        }

        PerformanceWorkGroupInfoAppVo performanceWorkGroupInfo = new PerformanceWorkGroupInfoAppVo();
        performanceWorkGroupInfo.setGroupId(performanceWorkGroup.getId());
        performanceWorkGroupInfo.setGroupName(performanceWorkGroup.getName());
        if (null != performanceWorkGroup.getLeader()) {
            performanceWorkGroupInfo.setManager(performanceWorkGroup.getLeader().getName());
        }
        performanceWorkGroupInfo.setTotal(allWorkGroupUserPerformances.size());
        performanceWorkGroupInfo.setInvalided(memberForInvalided);
        performanceWorkGroupInfo.setPerformancePro(subPerformancePro);
        performanceWorkGroupInfo.setStatus(status);

        if (null != filter) {
            List<MemberPerformanceAppVo> allSubMemberPerformanceAppVos = getMemberPerformanceList(allMemberMap, allWorkGroupUserPerformances, cfgPerfHideManagerComments, performanceWorkGroups);
            performanceWorkGroupInfo.setManagerPerformanceList(PerformanceDataHelper.filerMemberPerformances(allSubMemberPerformanceAppVos, filter));
        }

        return performanceWorkGroupInfo;
    }

    private List<MemberPerformanceAppVo> getMemberPerformanceList(Map<Long, PerformanceUserDto> allMemberMap, List<UserPerformanceDto> userPerformances,
                                                                  List<CfgPerfHideManagerComment> cfgPerfHideManagerComments, List<PerformanceWorkGroupDto> performanceWorkGroups) {
        List<MemberPerformanceAppVo> allMemberPerformanceAppVos = new ArrayList<>();
        for (UserPerformanceDto userPerformance : userPerformances) {
            boolean isHideManagerComment = UserPerformanceHelper.isHideManagerComment(cfgPerfHideManagerComments, performanceWorkGroups, userPerformance.getWorkGroupId(), userPerformance.getYear(), userPerformance.getMonth());
            allMemberPerformanceAppVos.add(PerformanceDataHelper.getMemberPerformanceAppVo(userPerformance, !isHideManagerComment, !allMemberMap.containsKey(userPerformance.getUserId()), false, null));
        }
        return allMemberPerformanceAppVos;
    }

    private WorkGroupDto getRootWorkGroup(Long performanceGroupId, Map<Long, WorkGroupDto> activeWorkGroupMap, Map<Long, WorkGroupDto> allWorkGroupMap,
                                          List<WorkGroupDto> allActivePerformanceGroups, List<PerformanceUserDto> allUsers) {
        WorkGroupDto rootWorkGroup;
        if (activeWorkGroupMap.containsKey(performanceGroupId)) {
            // 找出根目录，初始化树
            rootWorkGroup = PerformanceTreeHelper.getWorkGroupTree(performanceGroupId, allActivePerformanceGroups, allUsers, true);
        } else if (allWorkGroupMap.containsKey(performanceGroupId)) {
            rootWorkGroup = allWorkGroupMap.get(performanceGroupId);
        } else {
            rootWorkGroup = new WorkGroupDto();
            rootWorkGroup.setId(performanceGroupId);
            rootWorkGroup.setActiveFlag(false);
        }
        return rootWorkGroup;
    }

    private SubPerformanceBaseVo.Profile getSubPerformanceProfile(List<UserPerformanceDto> allWorkGroupUserPerformances, List<Long> managerGroupIds) {
        // 不参与人数
        int memberForInvalided = 0;
        for (UserPerformanceDto userPerformanceDto : allWorkGroupUserPerformances) {
            if (UserPerformance.Performance.invalided.equals(userPerformanceDto.getFinalPerformance())) {
                memberForInvalided++;
            }
        }

        SubPerformanceAppVo.Profile profile = new SubPerformanceAppVo.Profile();
        profile.setTotal(allWorkGroupUserPerformances.size());
        profile.setWaitingForCommit(0);
        profile.setWaitingForReview(0);
        profile.setInvalided(memberForInvalided);
        profile.setManagerCount(managerGroupIds.size());
        PerformanceWorkGroupInfoAppVo.PerformancePro performancePro = PerformanceDataHelper.getPerformancePro(allWorkGroupUserPerformances);
        profile.setPerformancePro(performancePro);
        profile.setProjectConfirmFlag(false);
        profile.setFmGroupConfirmedFlag(true);
        profile.setFmGroupConfirmInfoList(new ArrayList<>());
        return profile;
    }

    @Override
    public ProjectFixMemberInfoVo getFmGroupConfirmInfoListByProjectConfirmer(Long userId, Integer year, Integer month, ProjectFixMemberInfoVo.History history) {
        ProjectFixMemberInfoVo projectFixMemberInfoVo = new ProjectFixMemberInfoVo();

        // 历史状态
        projectFixMemberInfoVo.setHistoryInfo(history);

        List<IdNameBaseObject> projects = fmUserRoleMapper.selectUserFixSecondProjectsByUserId(userId);
        List<PerformanceWorkGroupDto> performanceWorkGroups = performanceWorkGroupMapper.selectAllWithProjectIdByActive();
        // 平台级别需项目确认绩效组
        Map<Long, PerformanceWorkGroupDto> parentWorkGroupMap = performanceWorkGroups.stream().filter(g -> null != g.getProjectId()).collect(Collectors.toMap(g -> g.getId(), g -> g));

        // 历史固化组
        List<FmGroupConfirmInfoVo> fmGroupConfirmInfoList;
        if (!projects.isEmpty()) {
            fmGroupConfirmInfoList = fmGroupConfirmInfoMapper.selectFixSencondConfirmInfoByProjectConfirmerAndYearAndMonth(userId, year, month);
        } else {
            fmGroupConfirmInfoList = fmGroupConfirmInfoMapper.selectAllByProjectConfirmerAndYearMonth(userId, year, month);
        }
        Map<Long, List<FmGroupConfirmInfoVo>> fmGroupConfirmInfoListMap = fmGroupConfirmInfoList.stream().collect(Collectors.groupingBy(i -> i.getPerformanceWorkGroupId()));
        Map<Long, List<FmGroupConfirmInfoVo>> projectFmGroupConfirmInfoListMap = fmGroupConfirmInfoList.stream().collect(Collectors.groupingBy(i -> i.getProjectId()));
        Map<Long, String> projectNameMap = PerformanceDataHelper.getProjectNameMap(fmGroupConfirmInfoList);
        Map<Long, String> platNameMap = PerformanceDataHelper.getPlatNameMap(fmGroupConfirmInfoList);
        // 历史绩效
        List<UserPerformanceDto> allMonthRecords = userPerformanceMapper.selectAllByYearAndMonth(year, month);
        List<UserPerformanceDto> allWorkGroupUserPerformances = historyTrackService.getAllHistoryMembersByWorkGroupIdAndTime(fmGroupConfirmInfoListMap.keySet().stream().collect(Collectors.toList()), year, month, allMonthRecords);

        projectFixMemberInfoVo.setProjects(new ArrayList<>());
        // 项目分组
        for (Map.Entry<Long, List<FmGroupConfirmInfoVo>> projectEntry : projectFmGroupConfirmInfoListMap.entrySet()) {
            Integer total = 0;
            ProjectFixMemberInfoVo.Project fmProject = new ProjectFixMemberInfoVo.Project();
            fmProject.setPlats(new ArrayList<>());
            Map<Long, List<FmGroupConfirmInfoVo>> platFmGroupConfirmInfoListMap = projectEntry.getValue().stream().collect(Collectors.groupingBy(i -> i.getPlatId()));
            // 平台分组
            for (Map.Entry<Long, List<FmGroupConfirmInfoVo>> platEntry : platFmGroupConfirmInfoListMap.entrySet()) {
                ProjectFixMemberInfoVo.Plat fmPlat = new ProjectFixMemberInfoVo.Plat();
                fmPlat.setName(platNameMap.get(platEntry.getKey()));
                fmPlat.setFmGroupConfirmInfoList(new ArrayList<>());
                List<FmGroupConfirmInfoVo> projectPlatFmGroupConfirmInfoList = platEntry.getValue();
                for (FmGroupConfirmInfoVo fmGroupConfirmInfo : projectPlatFmGroupConfirmInfoList) {
                    if (projects.isEmpty() && parentWorkGroupMap.containsKey(fmGroupConfirmInfo.getPerformanceWorkGroupId())) {
                        continue;
                    }
                    if (!projects.isEmpty() && !parentWorkGroupMap.containsKey(fmGroupConfirmInfo.getPerformanceWorkGroupId())) {
                        continue;
                    }
                    // 固化组人数
                    List<UserPerformanceDto> allSubUserPerformances = PerformanceDataHelper.getSubUserPerformances(fmGroupConfirmInfo.getPerformanceWorkGroupId(), allWorkGroupUserPerformances);
                    fmGroupConfirmInfo.setTotal(allSubUserPerformances.stream().filter(p -> fmGroupConfirmInfo.getProjectId().equals(p.getFmProjectId())).collect(Collectors.toList()).size());
                    total += fmGroupConfirmInfo.getTotal();

                    int sort = PerformanceCurrentServiceImpl.getSortCode(fmGroupConfirmInfo.getStatus());
                    fmGroupConfirmInfo.setSort(sort);

                    fmPlat.getFmGroupConfirmInfoList().add(fmGroupConfirmInfo);
                }
                if (!fmPlat.getFmGroupConfirmInfoList().isEmpty()) {
                    fmPlat.getFmGroupConfirmInfoList().sort(Comparator.comparing(i -> i.getSort()));
                    fmProject.getPlats().add(fmPlat);
                }
            }
            fmProject.setTotal(total);
            fmProject.setProjectId(projectEntry.getKey());
            fmProject.setName(projectNameMap.get(projectEntry.getKey()));
            fmProject.setUnsubmittedCount(0);
            fmProject.setUnconfirmedCount(0);
            if (!fmProject.getPlats().isEmpty()) {
                projectFixMemberInfoVo.getProjects().add(fmProject);
            }
        }

        return projectFixMemberInfoVo;
    }


    @Override
    public FmGroupConfirmInfoVo getFmGroupConfirmInfo(FmGroupConfirmInfoVo fmGroupConfirmInfo) {
        if (null == fmGroupConfirmInfo) {
            throw new ParamException("固化组信息不存在");
        }

        int year = fmGroupConfirmInfo.getYear();
        int month = fmGroupConfirmInfo.getMonth();

        // 历史绩效
        List<Long> managerGroupId = new ArrayList<>();
        managerGroupId.add(fmGroupConfirmInfo.getPerformanceWorkGroupId());
        List<UserPerformanceDto> allMonthRecords = userPerformanceMapper.selectAllByYearAndMonth(year, month);
        List<UserPerformanceDto> userPerformances = historyTrackService.getAllHistoryMembersByWorkGroupIdAndTime(managerGroupId, year, month, allMonthRecords);

        // 是否隐藏主管评语配置
        List<CfgPerfHideManagerComment> cfgPerfHideManagerComments = cfgPerfHideManagerCommentMapper.selectAll();
        List<PerformanceWorkGroupDto> performanceWorkGroups = performanceWorkGroupMapper.selectAllWithProjectIdByActive();

        setFmGroupConfirmInfo(fmGroupConfirmInfo, userPerformances, cfgPerfHideManagerComments, performanceWorkGroups);

        return fmGroupConfirmInfo;
    }


    private void setFmGroupConfirmInfo(FmGroupConfirmInfoVo fmGroupConfirmInfo, List<UserPerformanceDto> allWorkGroupUserPerformances, List<CfgPerfHideManagerComment> cfgPerfHideManagerComments, List<PerformanceWorkGroupDto> performanceWorkGroups) {
        List<MemberPerformanceAppVo> allMemberPerformanceAppVos = new ArrayList<>();
        List<UserPerformanceDto> allSubUserPerformances = PerformanceDataHelper.getSubUserPerformances(fmGroupConfirmInfo.getPerformanceWorkGroupId(), allWorkGroupUserPerformances);
        for (UserPerformanceDto userPerformance : allSubUserPerformances) {
            if (!fmGroupConfirmInfo.getProjectId().equals(userPerformance.getFmProjectId())) {
                continue;
            }

            boolean isHideManagerComment = UserPerformanceHelper.isHideManagerComment(cfgPerfHideManagerComments, performanceWorkGroups, userPerformance.getWorkGroupId(), userPerformance.getYear(), userPerformance.getMonth());
            allMemberPerformanceAppVos.add(PerformanceDataHelper.getMemberPerformanceAppVo(userPerformance, !isHideManagerComment, false, null != userPerformance.getFmProjectId(), null));
        }
        allMemberPerformanceAppVos.sort((x, y) -> {
            int i = x.getSort().compareTo(y.getSort());
            if (i == 0) {
                i = x.getLoginId().compareTo(y.getLoginId());
            }
            return i;
        });
        fmGroupConfirmInfo.setMemberPerformances(allMemberPerformanceAppVos);
        fmGroupConfirmInfo.setTotal(allMemberPerformanceAppVos.size());
    }
}
