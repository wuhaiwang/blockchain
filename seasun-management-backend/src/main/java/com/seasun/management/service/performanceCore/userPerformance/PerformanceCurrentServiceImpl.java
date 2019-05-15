package com.seasun.management.service.performanceCore.userPerformance;

import com.seasun.management.dto.*;
import com.seasun.management.helper.ReportHelper;
import com.seasun.management.helper.UserPerformanceHelper;
import com.seasun.management.mapper.*;
import com.seasun.management.model.*;
import com.seasun.management.service.performanceCore.historyTrack.PerformanceHistoryTrackService;
import com.seasun.management.util.MySystemParamUtils;
import com.seasun.management.util.MyTokenUtils;
import com.seasun.management.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service(value = "current")
public class PerformanceCurrentServiceImpl implements PerformanceService {

    @Autowired
    UserPerformanceMapper userPerformanceMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    PerformanceWorkGroupMapper performanceWorkGroupMapper;

    @Autowired
    CfgPerfHideManagerCommentMapper cfgPerfHideManagerCommentMapper;

    @Autowired
    FmMemberMapper fmMemberMapper;

    @Autowired
    FmUserRoleMapper fmUserRoleMapper;

    @Autowired
    ProjectMapper projectMapper;

    @Autowired
    FmGroupConfirmInfoMapper fmGroupConfirmInfoMapper;

    @Autowired
    PerformanceHistoryTrackService historyTrackService;

    @Autowired
    private PerfWorkGroupStatusMapper perfWorkGroupStatusMapper;

    @Override
    public SubPerformanceAppVo getSubPerformance(Long userId, Long workGroupId, Integer year, Integer month, String filter) {
        List<UserPerformanceDto> allUserPerformances = userPerformanceMapper.selectAllByYearAndMonth(year, month);
        List<WorkGroupDto> allPerformanceGroups = performanceWorkGroupMapper.selectAllByActive();
        List<PerformanceUserDto> allUsers = userMapper.selectAllEntityWithPerformanceByPerformanceWorkGroup();
        List<PerformanceWorkGroupDto> allPerformanceWorkGroups = performanceWorkGroupMapper.selectAllWithProjectIdByActive();
        List<PerformanceWorkGroupDto> managerGroups;
        if (null != userId) {
            managerGroups = allPerformanceWorkGroups.stream().filter(g -> userId.equals(g.getPerformanceManagerId())).collect(Collectors.toList());
        } else {
            managerGroups = allPerformanceWorkGroups.stream().filter(g -> workGroupId.equals(g.getId())).collect(Collectors.toList());
        }
        List<CfgPerfHideManagerComment> cfgPerfHideManagerComments = cfgPerfHideManagerCommentMapper.selectAll();
        List<FmGroupConfirmInfoVo> allFmGroupConfirmInfoList = fmGroupConfirmInfoMapper.selectAllByYearMonth(year, month);
        List<FmMemberDto> allFmMembers = fmMemberMapper.selectAll();
        List<PerfWorkGroupStatus> perfWorkGroupStatuses = perfWorkGroupStatusMapper.selectByYearAndMonth(year, month);

        // 项目平台负责人
        boolean projectManagerFlag = managerGroups.stream().anyMatch(g -> null != g.getProjectId());

        // 把记录放到map里方便查找
        Map<Long, UserPerformanceDto> userPerformanceDtoMap = allUserPerformances.stream().collect(Collectors.toMap(p -> p.getUserId(), p -> p));
        Map<Long, String> performanceWorkGroupStatusMap = PerformanceDataHelper.getPerformanceWorkGroupStatusMap(allPerformanceWorkGroups, perfWorkGroupStatuses);
        Map<Long, FmMemberDto> fmMemberMap = allFmMembers.stream().collect(Collectors.toMap(m -> m.getUserId(), m -> m));

        // 重设员工绩效组
        PerformanceDataHelper.resetUserPerformanceWorkGroup(allUsers, userPerformanceDtoMap);

        Integer strictType = PerformanceWorkGroup.GroupStrictType.normalNum;
        Boolean projectConfirmFlag = false;
        List<PerformanceUserDto> allMembers = new ArrayList<>();
        List<PerformanceUserDto> directMembers = new ArrayList<>();
        List<WorkGroupDto> directGroups = new ArrayList<>();
        List<Long> managerGroupIds = new ArrayList<>();
        List<Long> fmPlatIds = new ArrayList<>();

        // 查找管理组的strictType list
        List<Integer> groupStrictTypes = new ArrayList<>();

        for (PerformanceWorkGroupDto performanceWorkGroup : managerGroups) {
            managerGroupIds.add(performanceWorkGroup.getId());

            // 找出根目录，初始化树
            WorkGroupDto performanceRootGroup = PerformanceTreeHelper.getWorkGroupTree(performanceWorkGroup.getId(), allPerformanceGroups, allUsers, false);
            // 获取所有下属
            allMembers.addAll(PerformanceTreeHelper.getAllMembersByRootWorkGroup(performanceRootGroup));
            // 获取直接下属
            directMembers.addAll(performanceRootGroup.getMembers());
            // 获取下属团队
            directGroups.addAll(performanceRootGroup.getChildWorkGroups());


            groupStrictTypes.add(performanceWorkGroup.getStrictType());


            if (performanceWorkGroup.getProjectConfirmFlag()) {
                projectConfirmFlag = true;
                if (null != performanceWorkGroup.getProjectId()) {
                    fmPlatIds.add(performanceWorkGroup.getProjectId());
                }
            }
        }

        strictType = UserPerformanceHelper.getStrictType(groupStrictTypes);

        List<MemberPerformanceAppVo> allMemberPerformanceAppVos = getMemberPerformanceList(allMembers, userPerformanceDtoMap, cfgPerfHideManagerComments, allPerformanceWorkGroups, performanceWorkGroupStatusMap, fmMemberMap, projectManagerFlag);
        List<MemberPerformanceAppVo> directMemberPerformanceAppVos = getMemberPerformanceList(directMembers, userPerformanceDtoMap, cfgPerfHideManagerComments, allPerformanceWorkGroups, performanceWorkGroupStatusMap, fmMemberMap, projectManagerFlag);

        // 生成api返回数据
        SubPerformanceAppVo subPerformanceAppVo = new SubPerformanceAppVo();

        boolean showProjectConfirmFlag = false;
        if (projectConfirmFlag) {
            List<ProjectMaxMemberVo> projects = projectMapper.selectAllWithMaxMember();
            showProjectConfirmFlag = getProjectConfirmFlag(allMembers, fmPlatIds, allFmMembers, allPerformanceWorkGroups, projects);
        }
        SubPerformanceAppVo.Profile profile = getSubPerformanceProfile(allMemberPerformanceAppVos, userPerformanceDtoMap, strictType, managerGroupIds, allMembers, allUserPerformances, showProjectConfirmFlag, allFmGroupConfirmInfoList);
        subPerformanceAppVo.setProfile(profile);

        subPerformanceAppVo.setManagerPerformanceList(PerformanceDataHelper.filerMemberPerformances(directMemberPerformanceAppVos, filter));

        SubPerformanceAppVo.SubGroupStatistics subGroupStatistics = new SubPerformanceAppVo.SubGroupStatistics();
        subGroupStatistics.setTotal(directGroups.size());
        int memberCount = 0;
        List<PerformanceWorkGroupInfoAppVo> performanceWorkGroupInfos = new ArrayList<>();

        Integer startDay = MySystemParamUtils.getSystemConfigWithDefaultValue(MySystemParamUtils.Key.PerformanceStartDay, MySystemParamUtils.DefaultValue.PerformanceStartDay);
        for (WorkGroupDto directGroup : directGroups) {
            PerformanceWorkGroupInfoAppVo directGroupInfo = getPerformanceWorkGroupInfo(startDay, year, month, directGroup, filter, allUserPerformances, performanceWorkGroupStatusMap, cfgPerfHideManagerComments, allPerformanceWorkGroups, fmMemberMap, projectManagerFlag);
            memberCount += directGroupInfo.getTotal();
            performanceWorkGroupInfos.add(directGroupInfo);
        }
        if (performanceWorkGroupInfos.isEmpty()) {
            subGroupStatistics.setData(new ArrayList<>());
        } else {
            performanceWorkGroupInfos.sort((x, y) -> {
                int i = x.getSort().compareTo(y.getSort());
                if (i == 0) {
                    i = x.getGroupName().compareTo(y.getGroupName());
                }
                return i;
            });
            subGroupStatistics.setData(performanceWorkGroupInfos);
        }
        subGroupStatistics.setMemberCount(memberCount);
        subPerformanceAppVo.setSubGroup(subGroupStatistics);

        // 历史记录
        List<SubPerformanceAppVo.HistoryInfo> historyInfos = PerformanceDataHelper.getWorkGroupHistory(userId, workGroupId);
        subPerformanceAppVo.setHistory(historyInfos);

        // 筛选项
        subPerformanceAppVo.setFilter(UserPerformanceHelper.getCurrentFilter(projectConfirmFlag));

        return subPerformanceAppVo;
    }

    @Override
    public SubFixMemberPerformanceVo getSubFixMemberPerformance(Long userId, Integer year, Integer month) {
        List<UserPerformanceDto> allUserPerformances = userPerformanceMapper.selectAllByYearAndMonth(year, month);
        List<WorkGroupDto> allPerformanceGroups = performanceWorkGroupMapper.selectAllByActive();
        List<PerformanceUserDto> allUsers = userMapper.selectAllEntityWithPerformanceByPerformanceWorkGroup();
        List<PerformanceWorkGroupDto> allPerformanceWorkGroups = performanceWorkGroupMapper.selectAllWithProjectIdByActive();
        List<PerformanceWorkGroupDto> managerGroups = allPerformanceWorkGroups.stream().filter(g -> userId.equals(g.getPerformanceManagerId())).collect(Collectors.toList());
        List<CfgPerfHideManagerComment> cfgPerfHideManagerComments = cfgPerfHideManagerCommentMapper.selectAll();
        List<FmMemberDto> allFmMembers = fmMemberMapper.selectAll();
        List<FmUserRoleDto> allFmUserRoles = fmUserRoleMapper.selectAllByRoleId(FmRole.Role.projectFixFirstConfirmer);
        List<FmGroupConfirmInfoVo> allFmGroupConfirmInfoList = fmGroupConfirmInfoMapper.selectAllByYearMonth(year, month);
        List<ProjectMaxMemberVo> projects = projectMapper.selectAllWithMaxMember();
        List<PerfWorkGroupStatus> perfWorkGroupStatuses = perfWorkGroupStatusMapper.selectByYearAndMonth(year, month);

        // 项目平台负责人
        boolean projectManagerFlag = managerGroups.stream().anyMatch(g -> null != g.getProjectId());

        // 把记录放到map里方便查找
        Map<Long, UserPerformanceDto> userPerformanceDtoMap = allUserPerformances.stream().collect(Collectors.toMap(p -> p.getUserId(), p -> p));
        Map<Long, String> performanceWorkGroupStatusMap = PerformanceDataHelper.getPerformanceWorkGroupStatusMap(allPerformanceWorkGroups, perfWorkGroupStatuses);
        Map<Long, FmMemberDto> fmMemberMap = allFmMembers.stream().collect(Collectors.toMap(m -> m.getUserId(), m -> m));
        Map<String, FmUserRoleDto> fmUserRoleMap = allFmUserRoles.stream().collect(Collectors.toMap(ur -> ur.getPlatId() + "_" + ur.getProjectId(), ur -> ur));
        Map<Long, ProjectMaxMemberVo> projectMap = projects.stream().collect(Collectors.toMap(p -> p.getId(), p -> p));

        // 重设员工绩效组
        PerformanceDataHelper.resetUserPerformanceWorkGroup(allUsers, userPerformanceDtoMap);

        Integer strictType = PerformanceWorkGroup.GroupStrictType.normalNum;
        Boolean projectConfirmFlag = false;
        List<PerformanceUserDto> allMembers = new ArrayList<>();
        List<Long> managerGroupIds = new ArrayList<>();
        List<Long> fmPlatIds = new ArrayList<>();

        // 所有团队的strict type
        List<Integer> strictTypes = new ArrayList<>();

        for (PerformanceWorkGroupDto performanceWorkGroup : managerGroups) {
            managerGroupIds.add(performanceWorkGroup.getId());

            // 找出根目录，初始化树
            WorkGroupDto performanceRootGroup = PerformanceTreeHelper.getWorkGroupTree(performanceWorkGroup.getId(), allPerformanceGroups, allUsers, false);

            // 获取所有下属
            allMembers.addAll(PerformanceTreeHelper.getAllMembersByRootWorkGroup(performanceRootGroup));

            strictTypes.add(performanceWorkGroup.getStrictType());

            if (performanceWorkGroup.getProjectConfirmFlag()) {
                projectConfirmFlag = true;
                if (null != performanceWorkGroup.getProjectId()) {
                    fmPlatIds.add(performanceWorkGroup.getProjectId());
                }
            }
        }

        strictType = UserPerformanceHelper.getStrictType(strictTypes);

        List<MemberPerformanceAppVo> allMemberPerformanceAppVos = getMemberPerformanceList(allMembers, userPerformanceDtoMap, cfgPerfHideManagerComments, allPerformanceWorkGroups, performanceWorkGroupStatusMap, fmMemberMap, projectManagerFlag);

        Map<String, List<FmGroupConfirmInfoVo>> platProjectFmGroupConfirmInfoListMap = allFmGroupConfirmInfoList.stream().filter(i -> managerGroupIds.contains(i.getPerformanceWorkGroupId())).collect(Collectors.groupingBy(i -> i.getPlatId() + "_" + i.getProjectId()));

        // 筛选固化成员绩效
        List<PerformanceUserDto> fixMembers = allMembers.stream().filter(u -> fmMemberMap.containsKey(u.getUserId())).collect(Collectors.toList());
        List<MemberPerformanceAppVo> fixMemberPerformances = getMemberPerformanceList(fixMembers, userPerformanceDtoMap, cfgPerfHideManagerComments, allPerformanceWorkGroups, performanceWorkGroupStatusMap, fmMemberMap, projectManagerFlag);
        Map<Long, MemberPerformanceAppVo> fixMemberPerformanceMap = fixMemberPerformances.stream().collect(Collectors.toMap(p -> p.getUserId(), p -> p));
        // 筛选当前固化成员
        List<FmMemberDto> currentFmMembers = allFmMembers.stream().filter(m -> fixMemberPerformanceMap.containsKey(m.getUserId())).collect(Collectors.toList());
        // 按平台分组
        Map<Long, List<FmMemberDto>> platFmMembersMap = currentFmMembers.stream().collect(Collectors.groupingBy(m -> m.getPlatId()));
        Map<Long, String> platNameMap = PerformanceDataHelper.getPlatNameMap(currentFmMembers);
        Map<Long, String> projectNameMap = PerformanceDataHelper.getProjectNameMap(currentFmMembers);

        SubFixMemberPerformanceVo subFixMemberPerformanceVo = new SubFixMemberPerformanceVo();

        if (projectConfirmFlag) {
            projectConfirmFlag = getProjectConfirmFlag(allMembers, fmPlatIds, allFmMembers, allPerformanceWorkGroups, projects);
        }
        SubFixMemberPerformanceVo.Profile profile = getSubPerformanceProfile(allMemberPerformanceAppVos, userPerformanceDtoMap, strictType, managerGroupIds, allMembers, allUserPerformances, projectConfirmFlag, allFmGroupConfirmInfoList);
        subFixMemberPerformanceVo.setProfile(profile);

        subFixMemberPerformanceVo.setTotal(currentFmMembers.size());
        subFixMemberPerformanceVo.setPlats(new ArrayList<>());
        for (Map.Entry<Long, List<FmMemberDto>> platEntry : platFmMembersMap.entrySet()) {
            List<FmMemberDto> platFmMembers = platEntry.getValue();
            SubFixMemberPerformanceVo.Plat plat = new SubFixMemberPerformanceVo.Plat();
            plat.setTotal(platFmMembers.size());
            plat.setName(platNameMap.get(platEntry.getKey()));
            plat.setProjects(new ArrayList<>());
            // 按项目分组
            Map<Long, List<FmMemberDto>> projectFmMembersMap = platFmMembers.stream().collect(Collectors.groupingBy(m -> m.getProjectId()));
            for (Map.Entry<Long, List<FmMemberDto>> projectEntry : projectFmMembersMap.entrySet()) {
                List<FmMemberDto> projectFmMembers = projectEntry.getValue();
                Map<Long, FmMemberDto> projectFmMemberMap = projectFmMembers.stream().collect(Collectors.toMap(m -> m.getUserId(), m -> m));
                SubFixMemberPerformanceVo.Project project = new SubFixMemberPerformanceVo.Project();
                project.setName(projectNameMap.get(projectEntry.getKey()));
                project.setTotal(projectFmMembers.size());
                String platProjectKey = platEntry.getKey() + "_" + projectEntry.getKey();
                if (platProjectFmGroupConfirmInfoListMap.containsKey(platProjectKey)) {
                    List<FmGroupConfirmInfoVo> platProjectFmGroupConfirmInfoList = platProjectFmGroupConfirmInfoListMap.get(platProjectKey);
                    for (FmGroupConfirmInfoVo fmGroupConfirmInfo : platProjectFmGroupConfirmInfoList) {
                        project.setConfirmFlag(FmGroupConfirmInfo.Status.confirmed.equals(fmGroupConfirmInfo.getStatus()));
                    }
                } else {
                    project.setConfirmFlag(false);
                }
                if (projectManagerFlag) {
                    if (fmUserRoleMap.containsKey(platEntry.getKey() + "_" + projectEntry.getKey()) && projectMap.containsKey(projectEntry.getKey())) {
                        project.setManager(projectMap.get(projectEntry.getKey()).getManagerName());
                    }
                } else {
                    if (fmUserRoleMap.containsKey(platEntry.getKey() + "_" + projectEntry.getKey())) {
                        project.setManager(fmUserRoleMap.get(platEntry.getKey() + "_" + projectEntry.getKey()).getUserName());
                    }
                }
                project.setPerformances(fixMemberPerformances.stream().filter(p -> projectFmMemberMap.containsKey(p.getUserId())).collect(Collectors.toList()));
                plat.getProjects().add(project);
                plat.getProjects().sort(Comparator.comparing(p -> getSortNumber(p)));
            }
            subFixMemberPerformanceVo.getPlats().add(plat);
        }

        // 历史记录
        List<SubFixMemberPerformanceVo.HistoryInfo> historyInfos = PerformanceDataHelper.getWorkGroupHistory(userId, null);
        subFixMemberPerformanceVo.setHistory(historyInfos);

        // 筛选
        subFixMemberPerformanceVo.setFilter(UserPerformanceHelper.getCurrentFilter(projectConfirmFlag));

        return subFixMemberPerformanceVo;
    }

    @Override
    public WorkGroupMemberPerformanceAppVo getWorkGroupMemberPerformance(Long observerUserId, Long performanceGroupId, Integer year, Integer month) {
        WorkGroupMemberPerformanceAppVo workGroupMemberPerformanceAppVo = new WorkGroupMemberPerformanceAppVo();

        User user;
        if (null == observerUserId) {
            user = MyTokenUtils.getCurrentUser();
        } else {
            user = userMapper.selectByPrimaryKey(observerUserId);
        }
        List<UserPerformanceDto> allUserPerformances = userPerformanceMapper.selectAllByYearAndMonth(year, month);
        List<PerformanceUserDto> allUsers = userMapper.selectAllEntityWithPerformanceByPerformanceWorkGroup();
        List<WorkGroupDto> allPerformanceGroups = performanceWorkGroupMapper.selectAllByActive();
        List<CfgPerfHideManagerComment> cfgPerfHideManagerComments = cfgPerfHideManagerCommentMapper.selectAll();
        List<PerformanceWorkGroupDto> allPerformanceWorkGroups = performanceWorkGroupMapper.selectAllWithProjectIdByActive();
        List<PerfWorkGroupStatus> perfWorkGroupStatuses = perfWorkGroupStatusMapper.selectByYearAndMonth(year, month);
        List<PerformanceWorkGroupDto> managerGroups = allPerformanceWorkGroups.stream().filter(g -> user.getId().equals(g.getPerformanceManagerId())).collect(Collectors.toList());

        // 项目平台负责人
        boolean projectManagerFlag = managerGroups.stream().anyMatch(g -> null != g.getProjectId());

        // 把记录放到map里方便查找
        Map<Long, UserPerformanceDto> userPerformanceMap = allUserPerformances.stream().collect(Collectors.toMap(p -> p.getUserId(), p -> p));
        Map<Long, String> performanceWorkGroupStatusMap = PerformanceDataHelper.getPerformanceWorkGroupStatusMap(allPerformanceWorkGroups, perfWorkGroupStatuses);

        // 重设员工绩效组
        PerformanceDataHelper.resetUserPerformanceWorkGroup(allUsers, userPerformanceMap);

        // 找出根目录，初始化树
        WorkGroupDto rootWorkGroup = PerformanceTreeHelper.getWorkGroupTree(performanceGroupId, allPerformanceGroups, allUsers, true);

        Integer startDay = MySystemParamUtils.getSystemConfigWithDefaultValue(MySystemParamUtils.Key.PerformanceStartDay, MySystemParamUtils.DefaultValue.PerformanceStartDay);

        PerformanceWorkGroupInfoAppVo performanceWorkGroupInfoAppVo = getPerformanceWorkGroupInfo(startDay, year, month, rootWorkGroup, UserPerformanceHelper.SearchFilter.all, allUserPerformances, performanceWorkGroupStatusMap, cfgPerfHideManagerComments, allPerformanceWorkGroups, null, projectManagerFlag);
        workGroupMemberPerformanceAppVo.setManagerPerformanceList(performanceWorkGroupInfoAppVo.getManagerPerformanceList());
        performanceWorkGroupInfoAppVo.setManagerPerformanceList(null);
        workGroupMemberPerformanceAppVo.setGroupInfo(performanceWorkGroupInfoAppVo);

        return workGroupMemberPerformanceAppVo;
    }

    private List<MemberPerformanceAppVo> getMemberPerformanceList(List<PerformanceUserDto> allMembers, Map<Long, UserPerformanceDto> userPerformanceDtoMap,
                                                                  List<CfgPerfHideManagerComment> cfgPerfHideManagerComments, List<PerformanceWorkGroupDto> performanceWorkGroups,
                                                                  Map<Long, String> performanceWorkGroupStatusMap, Map<Long, FmMemberDto> fmMemberMap, Boolean projectManagerFlag) {
        List<MemberPerformanceAppVo> memberPerformanceAppVos = new ArrayList<>();
        for (PerformanceUserDto member : allMembers) {
            MemberPerformanceAppVo memberPerformanceAppVo;
            boolean fixMemberFlag = false;
            if (null != fmMemberMap) {
                fixMemberFlag = fmMemberMap.containsKey(member.getUserId());
            }
            if (userPerformanceDtoMap.containsKey(member.getUserId())) {
                UserPerformanceDto userPerformanceDto = userPerformanceDtoMap.get(member.getUserId());
                boolean isHideManagerComment = UserPerformanceHelper.isHideManagerComment(cfgPerfHideManagerComments, performanceWorkGroups, userPerformanceDto.getWorkGroupId(), userPerformanceDto.getYear(), userPerformanceDto.getMonth());
                memberPerformanceAppVo = PerformanceDataHelper.getMemberPerformanceAppVo(userPerformanceDto, !isHideManagerComment, false, fixMemberFlag, projectManagerFlag);
                memberPerformanceAppVos.add(memberPerformanceAppVo);
            } else {
                // 绩效组未提交才加上没绩效记录的
                String status = PerformanceDataHelper.getPerformanceWorkGroupStatus(member.getWorkGroupId(), performanceWorkGroupStatusMap);
                if (status.equals(SubPerformanceAppVo.HistoryInfo.Status.processing)) {
                    memberPerformanceAppVo = getNewMemberPerformanceAppVo(member);
                    memberPerformanceAppVo.setFixMemberFlag(fixMemberFlag);
                    memberPerformanceAppVos.add(memberPerformanceAppVo);
                }
            }
        }
        return memberPerformanceAppVos;
    }

    private MemberPerformanceAppVo getNewMemberPerformanceAppVo(PerformanceUserDto user) {
        MemberPerformanceAppVo memberPerformanceAppVo = new MemberPerformanceAppVo();
        memberPerformanceAppVo.setUserId(user.getUserId());
        memberPerformanceAppVo.setLoginId(user.getLoginId());
        memberPerformanceAppVo.setEmployeeNo(user.getEmployeeNo());
        memberPerformanceAppVo.setName(user.getName());
        memberPerformanceAppVo.setMonthGoalFlag(false);
        memberPerformanceAppVo.setManagerCommentFlag(false);
        memberPerformanceAppVo.setStatus(MemberPerformanceAppVo.Status.unsubmitted);
        memberPerformanceAppVo.setSort(UserPerformanceHelper.getSubMemberStatusSortMap().get(memberPerformanceAppVo.getStatus()));
        memberPerformanceAppVo.setDirectGroupName(user.getWorkGroupName());
        memberPerformanceAppVo.setOutOfGroupFlag(false);
        return memberPerformanceAppVo;
    }

    private PerformanceWorkGroupInfoAppVo.PerformancePro getPerformancePro(List<Long> managerGroupIds, List<PerformanceUserDto> allMembers, List<UserPerformanceDto> userPerformances) {
        List<UserPerformanceDto> calUserPerformances = new ArrayList<>();
        Map<Long, UserPerformanceDto> userPerformanceMap = userPerformances.stream().collect(Collectors.toMap(p -> p.getUserId(), p -> p));

        for (PerformanceUserDto member : allMembers) {
            if (userPerformanceMap.containsKey(member.getUserId())) {
                UserPerformanceDto userPerformance = userPerformanceMap.get(member.getUserId());
                if (managerGroupIds.contains(userPerformance.getWorkGroupId())) {
                    // 直接下属中的已评定的记录
                    if (null != userPerformance.getFinalPerformance()) {
                        calUserPerformances.add(userPerformance);
                    }
                } else {
                    // 已提交的下属团队的记录
//                    if (UserPerformance.Status.locked.equals(userPerformance.getStatus()) || UserPerformance.Status.complete.equals(userPerformance.getStatus())) {
                    calUserPerformances.add(userPerformance);
//                    }
                }
            }
        }

        PerformanceWorkGroupInfoAppVo.PerformancePro performancePro = PerformanceDataHelper.getPerformancePro(calUserPerformances);

        return performancePro;
    }

    private PerformanceWorkGroupInfoAppVo getPerformanceWorkGroupInfo(int startDay, int curYear, int curMonth, WorkGroupDto performanceRootGroup, String filter,
                                                                      List<UserPerformanceDto> allUserPerformances, Map<Long, String> performanceWorkGroupStatusMap,
                                                                      List<CfgPerfHideManagerComment> cfgPerfHideManagerComments, List<PerformanceWorkGroupDto> performanceWorkGroups,
                                                                      Map<Long, FmMemberDto> fmMemberMap, Boolean projectManagerFlag) {
        List<UserPerformanceDto> allWorkGroupUserPerformances = PerformanceDataHelper.getCurrentSubUserPerformances(performanceRootGroup, allUserPerformances);
        Map<Long, UserPerformanceDto> userPerformanceMap = allWorkGroupUserPerformances.stream().collect(Collectors.toMap(p -> p.getUserId(), p -> p));

        Calendar now = ReportHelper.getDateOnly();
        int nowYear = now.get(Calendar.YEAR);
        int nowMonth = now.get(Calendar.MONTH) + 1;

        // 获取所有下属
        List<PerformanceUserDto> allSubMembers = PerformanceTreeHelper.getAllMembersByRootWorkGroup(performanceRootGroup);

        // 不参与人数
        int memberForInvalided = 0;
        for (UserPerformanceDto userPerformanceDto : allWorkGroupUserPerformances) {
            if (UserPerformance.Performance.invalided.equals(userPerformanceDto.getFinalPerformance())) {
                memberForInvalided++;
            }
        }

        PerformanceWorkGroupInfoAppVo.PerformancePro performancePro = PerformanceDataHelper.getPerformancePro(allWorkGroupUserPerformances);

        // 获取直接下属已存在绩效，计算组状态
        String status = PerformanceDataHelper.getPerformanceWorkGroupStatus(performanceRootGroup.getId(), performanceWorkGroupStatusMap);
        int sort;
        switch (status) {
            case SubPerformanceAppVo.HistoryInfo.Status.complete:
                sort = 2;
                break;
            case SubPerformanceAppVo.HistoryInfo.Status.submitted:
                sort = 1;
                break;
            default:
                sort = 0;
                break;
        }
        if (curYear == nowYear && curMonth == nowMonth && now.get(Calendar.DAY_OF_MONTH) < startDay) {
            status = SubPerformanceAppVo.HistoryInfo.Status.waitingForStart;
        } else if (SubPerformanceAppVo.HistoryInfo.Status.processing.equals(status) && UserPerformanceHelper.isDelay(curYear, curMonth, startDay)) {
            status = SubPerformanceAppVo.HistoryInfo.Status.delay;
        }

        PerformanceWorkGroupInfoAppVo performanceWorkGroupInfo = new PerformanceWorkGroupInfoAppVo();
        performanceWorkGroupInfo.setGroupId(performanceRootGroup.getId());
        performanceWorkGroupInfo.setGroupName(performanceRootGroup.getName());
        if (null != performanceRootGroup.getLeader()) {
            performanceWorkGroupInfo.setManager(performanceRootGroup.getLeader().getName());
        }
        // 人数不包含已提交组没有绩效记录的员工
        int total = 0;
        for (PerformanceUserDto member : allSubMembers) {
            if (userPerformanceMap.containsKey(member.getUserId())) {
                total++;
            } else {
                String memberGroupStatus = PerformanceDataHelper.getPerformanceWorkGroupStatus(member.getWorkGroupId(), performanceWorkGroupStatusMap);
                if (memberGroupStatus.equals(SubPerformanceAppVo.HistoryInfo.Status.processing)) {
                    total++;
                }
            }
        }
        performanceWorkGroupInfo.setTotal(total);
        performanceWorkGroupInfo.setInvalided(memberForInvalided);
        performanceWorkGroupInfo.setPerformancePro(performancePro);
        performanceWorkGroupInfo.setStatus(status);
        performanceWorkGroupInfo.setSort(sort);

        if (null != filter) {
            List<MemberPerformanceAppVo> allSubMemberPerformanceAppVos = getMemberPerformanceList(allSubMembers, userPerformanceMap, cfgPerfHideManagerComments, performanceWorkGroups, performanceWorkGroupStatusMap, fmMemberMap, projectManagerFlag);
            performanceWorkGroupInfo.setManagerPerformanceList(PerformanceDataHelper.filerMemberPerformances(allSubMemberPerformanceAppVos, filter));
        }

        return performanceWorkGroupInfo;
    }

    // 判断是否需要与项目确认
    private boolean getProjectConfirmFlag(List<PerformanceUserDto> allMembers, List<Long> fmPlatIds, List<FmMemberDto> allFmMembers, List<PerformanceWorkGroupDto> allPerformanceWorkGroups, List<ProjectMaxMemberVo> projects) {
        Map<Long, PerformanceUserDto> allMemberMap = allMembers.stream().collect(Collectors.toMap(u -> u.getUserId(), u -> u));
        int confirmProjectCount = 0;
        if (!fmPlatIds.isEmpty()) {
            List<FmUserRoleDto> fmUserRoleDtos = fmUserRoleMapper.selectAllByRoleId(FmRole.Role.projectFixSecondConfirmer);
            Map<Long, Long> confirmProjectIds = fmUserRoleDtos.stream().collect(Collectors.toMap(x -> x.getProjectId(), x -> x.getUserId()));
            Map<Long, List<FmMemberDto>> smProjectFmMembersMap = allFmMembers.stream().filter(m -> allMemberMap.containsKey(m.getUserId()) && fmPlatIds.contains(m.getPlatId())).collect(Collectors.groupingBy(m -> m.getProjectId()));
            Map<Long, PerformanceWorkGroupDto> projectPerformanceWorkGroupMap = allPerformanceWorkGroups.stream().filter(g -> null != g.getProjectId()).collect(Collectors.toMap(g -> g.getProjectId(), g -> g));
            for (Long projectId : smProjectFmMembersMap.keySet()) {
                if (!projectPerformanceWorkGroupMap.containsKey(projectId)) {
                    continue;
                }
                if (!confirmProjectIds.containsKey(projectId)) {
                    continue;
                }
                if (MyTokenUtils.BOSS_ID.equals(confirmProjectIds.get(projectId))) {
                    continue;
                }
                confirmProjectCount++;
            }
        } else {
            Long planId = null;
            loop:
            for (PerformanceUserDto allMember : allMembers) {
                for (FmMemberDto allFmMember : allFmMembers) {
                    if (allFmMember.getUserId().equals(allMember.getUserId())) {
                        planId = allFmMember.getPlatId();
                        break loop;
                    }
                }
            }
            List<FmUserRoleDto> fmUserRoles = fmUserRoleMapper.selectAllByRoleIdAndPlatId(FmRole.Role.projectFixFirstConfirmer, planId);
            Map<Long, List<FmMemberDto>> smProjectFmMembersMap = allFmMembers.stream().filter(m -> allMemberMap.containsKey(m.getUserId())).collect(Collectors.groupingBy(m -> m.getProjectId()));
            for (Long projectId : smProjectFmMembersMap.keySet()) {
                if (!fmUserRoles.stream().anyMatch(p -> p.getProjectId().equals(projectId))) {
                    continue;
                }
                confirmProjectCount++;
            }
        }
        return confirmProjectCount > 0;
    }

    private SubPerformanceBaseVo.Profile getSubPerformanceProfile(List<MemberPerformanceAppVo> allMemberPerformanceAppVos, Map<Long, UserPerformanceDto> userPerformanceDtoMap, Integer strictType,
                                                                  List<Long> managerGroupIds, List<PerformanceUserDto> allMembers, List<UserPerformanceDto> allUserPerformances,
                                                                  Boolean projectConfirmFlag, List<FmGroupConfirmInfoVo> allFmGroupConfirmInfoList) {
        SubPerformanceAppVo.Profile profile = new SubPerformanceAppVo.Profile();
        profile.setTotal(allMemberPerformanceAppVos.size());
        int waitingForCommit = 0, waitingForReview = 0;
        for (MemberPerformanceAppVo memberPerformanceAppVo : allMemberPerformanceAppVos) {
            if (userPerformanceDtoMap.containsKey(memberPerformanceAppVo.getUserId())) {
                String status = userPerformanceDtoMap.get(memberPerformanceAppVo.getUserId()).getStatus();
                if (UserPerformance.Status.unsubmitted.equals(status)) {
                    waitingForCommit++;
                } else if (UserPerformance.Status.submitted.equals(status)) {
                    waitingForReview++;
                }
            } else {
                waitingForCommit++;
            }
        }
        profile.setWaitingForCommit(waitingForCommit);
        profile.setWaitingForReview(waitingForReview);
        profile.setStrictType(strictType);
        profile.setManagerCount(managerGroupIds.size());
        PerformanceWorkGroupInfoAppVo.PerformancePro performancePro = getPerformancePro(managerGroupIds, allMembers, allUserPerformances);
        profile.setPerformancePro(performancePro);
        profile.setProjectConfirmFlag(projectConfirmFlag);
        if (projectConfirmFlag) {
            List<FmGroupConfirmInfoVo> managerFmGroupConfirmInfoList = allFmGroupConfirmInfoList.stream().filter(i -> managerGroupIds.contains(i.getPerformanceWorkGroupId())).collect(Collectors.toList());
            if (managerFmGroupConfirmInfoList.isEmpty()) {
                profile.setFmGroupConfirmedFlag(false);
                profile.setFmGroupConfirmInfoList(new ArrayList<>());
            } else {
                profile.setFmGroupConfirmedFlag(managerFmGroupConfirmInfoList.stream().allMatch(i -> FmGroupConfirmInfo.Status.confirmed.equals(i.getStatus())));
                profile.setFmGroupConfirmInfoList(managerFmGroupConfirmInfoList);
            }
            profile.getFmGroupConfirmInfoList().sort((x, y) -> getSortCode(x.getStatus()) - getSortCode(y.getStatus()));
        } else {
            profile.setFmGroupConfirmedFlag(false);
            profile.setFmGroupConfirmInfoList(new ArrayList<>());
        }
        return profile;
    }

    public static int getSortCode(String status) {
        int i = 0;
        switch (status) {
            case FmGroupConfirmInfo.Status.unsubmitted:
                i = 2;
                break;
            case FmGroupConfirmInfo.Status.unconfirmed:
                i = 1;
                break;
            case FmGroupConfirmInfo.Status.confirmed:
                i = 3;
                break;
            default:
                break;

        }
        return i;
    }

    @Override
    public ProjectFixMemberInfoVo getFmGroupConfirmInfoListByProjectConfirmer(Long userId, Integer year, Integer month, ProjectFixMemberInfoVo.History history) {
        ProjectFixMemberInfoVo projectFixMemberInfoVo = new ProjectFixMemberInfoVo();

        // 历史状态  add missed code
        projectFixMemberInfoVo.setHistoryInfo(history);

        List<IdNameBaseObject> projects = fmUserRoleMapper.selectUserFixSecondProjectsByUserId(userId);
        List<PerformanceWorkGroupDto> performanceWorkGroups = performanceWorkGroupMapper.selectAllByProjectConfirm();
        List<FmMemberDto> allFmMembers = fmMemberMapper.selectAll();
        List<FmGroupConfirmInfoVo> fmGroupConfirmInfoList;
        if (!projects.isEmpty()) {
            fmGroupConfirmInfoList = fmGroupConfirmInfoMapper.selectFixSencondConfirmInfoByProjectConfirmerAndYearAndMonth(userId, year, month);
        } else {
            fmGroupConfirmInfoList = fmGroupConfirmInfoMapper.selectAllByProjectConfirmerAndYearMonth(userId, year, month);
        }
        List<WorkGroupDto> allPerformanceGroups = performanceWorkGroupMapper.selectAllByActive();
        List<PerformanceUserDto> allUsers = userMapper.selectAllEntityWithPerformanceByPerformanceWorkGroup();
        List<UserPerformanceDto> allUserPerformances = userPerformanceMapper.selectAllByYearAndMonth(year, month);

        Map<Long, List<FmMemberDto>> projectFmMembersMap = allFmMembers.stream().collect(Collectors.groupingBy(m -> m.getProjectId()));
        // 平台级别需项目确认绩效组
        Map<Long, PerformanceWorkGroupDto> platWorkGroupMap = performanceWorkGroups.stream().filter(g -> null != g.getProjectId()).collect(Collectors.toMap(g -> g.getProjectId(), g -> g));
        Map<Long, PerformanceWorkGroupDto> parentWorkGroupMap = performanceWorkGroups.stream().filter(g -> null != g.getProjectId()).collect(Collectors.toMap(g -> g.getId(), g -> g));

        Map<String, FmGroupConfirmInfoVo> fmGroupConfirmInfoMap = fmGroupConfirmInfoList.stream().collect(Collectors.toMap(i -> i.getProjectId() + "_" + i.getPlatId() + "_" + i.getPerformanceWorkGroupId(), i -> i));

        // 如果有已提交的固化组绩效重历史里取
        List<UserPerformanceDto> allWorkGroupUserPerformances = new ArrayList<>();
        if (!fmGroupConfirmInfoList.isEmpty()) {
            Map<Long, String> historyGroupNameMap = new HashMap<>();
            for (FmGroupConfirmInfoVo fmGroupConfirmInfo : fmGroupConfirmInfoList) {
                if (!historyGroupNameMap.containsKey(fmGroupConfirmInfo.getPerformanceWorkGroupId())) {
                    historyGroupNameMap.put(fmGroupConfirmInfo.getPerformanceWorkGroupId(), fmGroupConfirmInfo.getPerformanceWorkGroupName());
                }
            }
            List<UserPerformanceDto> allMonthRecords = userPerformanceMapper.selectAllByYearAndMonth(year, month);
            allWorkGroupUserPerformances = historyTrackService.getAllHistoryMembersByWorkGroupIdAndTime(historyGroupNameMap.keySet().stream().collect(Collectors.toList()), year, month, allMonthRecords);
        }

        // 重设员工绩效组
        Map<Long, UserPerformanceDto> userPerformanceDtoMap = allUserPerformances.stream().collect(Collectors.toMap(p -> p.getUserId(), p -> p));
        PerformanceDataHelper.resetUserPerformanceWorkGroup(allUsers, userPerformanceDtoMap);

        projectFixMemberInfoVo.setProjects(new ArrayList<>());

        // 判读是否是项目负责人
        if (!projects.isEmpty()) {
            // 项目负责人
            for (IdNameBaseObject project : projects) {
                if (!projectFmMembersMap.containsKey(project.getId())) {
                    continue;
                }

                Integer total = 0, unsubmittedCount = 0, unconfirmedCount = 0;
                ProjectFixMemberInfoVo.Project fmProject = new ProjectFixMemberInfoVo.Project();
                fmProject.setProjectId(project.getId());
                fmProject.setName(project.getName());
                fmProject.setPlats(new ArrayList<>());

                // 存在固化成员的项目
                List<FmMemberDto> projectFmMembers = projectFmMembersMap.get(project.getId());
                Map<Long, List<FmMemberDto>> platFmMembersMap = projectFmMembers.stream().collect(Collectors.groupingBy(m -> m.getPlatId()));
                Map<Long, String> platNameMap = PerformanceDataHelper.getPlatNameMap(projectFmMembers);
                for (Map.Entry<Long, List<FmMemberDto>> platEntry : platFmMembersMap.entrySet()) {
                    if (!platWorkGroupMap.containsKey(platEntry.getKey())) {
                        continue;
                    }

                    List<FmMemberDto> platFmMembers = platEntry.getValue();

                    // 找出有效固化组
                    PerformanceWorkGroupDto platWorkGroup = platWorkGroupMap.get(platEntry.getKey());

                    // 生成固化组初始化数据
                    FmGroupConfirmInfoParamDto fmGroupConfirmInfoParam = new FmGroupConfirmInfoParamDto();
                    fmGroupConfirmInfoParam.setYear(year);
                    fmGroupConfirmInfoParam.setMonth(month);
                    fmGroupConfirmInfoParam.setPlatId(platEntry.getKey());
                    fmGroupConfirmInfoParam.setPlatName(platNameMap.get(platEntry.getKey()));
                    fmGroupConfirmInfoParam.setProjectId(project.getId());
                    fmGroupConfirmInfoParam.setProjectName(project.getName());

                    List<FmGroupConfirmInfoVo> projectPlatFmGroupConfirmInfoList = new ArrayList<>();

                    FmGroupConfirmInfoVo fmGroupConfirmInfo = getFmGroupConfirmInfo(fmGroupConfirmInfoParam, platWorkGroup, fmGroupConfirmInfoMap, allWorkGroupUserPerformances, allPerformanceGroups, allUsers, platFmMembers);
                    if (null != fmGroupConfirmInfo) {
                        projectPlatFmGroupConfirmInfoList.add(fmGroupConfirmInfo);
                        switch (fmGroupConfirmInfo.getStatus()) {
                            case FmGroupConfirmInfo.Status.unsubmitted:
                                unsubmittedCount++;
                                break;
                            case FmGroupConfirmInfo.Status.unconfirmed:
                                unconfirmedCount++;
                                break;
                            case FmGroupConfirmInfo.Status.confirmed:
                                break;
                            default:
                                break;
                        }
                        total += fmGroupConfirmInfo.getTotal();
                    }

                    if (!projectPlatFmGroupConfirmInfoList.isEmpty()) {
                        ProjectFixMemberInfoVo.Plat fmPlat = new ProjectFixMemberInfoVo.Plat();
                        fmPlat.setName(platNameMap.get(platEntry.getKey()));
                        fmPlat.setFmGroupConfirmInfoList(projectPlatFmGroupConfirmInfoList);
                        fmProject.getPlats().add(fmPlat);
                    }
                }
                if (total > 0) {
                    fmProject.setTotal(total);
                    fmProject.setUnsubmittedCount(unsubmittedCount);
                    fmProject.setUnconfirmedCount(unconfirmedCount);
                    projectFixMemberInfoVo.getProjects().add(fmProject);
                }
            }
        } else {
            // 首次项目审核员
            // 首次需项目确认绩效组
            Map<Long, List<PerformanceWorkGroupDto>> parentWorkGroupsMap = performanceWorkGroups.stream().filter(g -> null != g.getParent() && parentWorkGroupMap.containsKey(g.getParent())).collect(Collectors.groupingBy(g -> g.getParent()));
            List<FmUserRoleDto> fmUserRoles = fmUserRoleMapper.selectAllByUserIdAndRoleId(userId, FmRole.Role.projectFixFirstConfirmer);
            Map<Long, List<FmUserRoleDto>> projectFmUserRolesMap = fmUserRoles.stream().collect(Collectors.groupingBy(ur -> ur.getProjectId()));
            Map<Long, String> projectNameMap = PerformanceDataHelper.getProjectNameMap(fmUserRoles);
            Map<Long, String> platNameMap = PerformanceDataHelper.getPlatNameMap(fmUserRoles);
            for (Map.Entry<Long, List<FmUserRoleDto>> projectEntry : projectFmUserRolesMap.entrySet()) {
                if (!projectFmMembersMap.containsKey(projectEntry.getKey())) {
                    continue;
                }

                Integer total = 0, unsubmittedCount = 0, unconfirmedCount = 0;
                ProjectFixMemberInfoVo.Project fmProject = new ProjectFixMemberInfoVo.Project();
                fmProject.setProjectId(projectEntry.getKey());
                fmProject.setName(projectNameMap.get(projectEntry.getKey()));
                fmProject.setPlats(new ArrayList<>());

                // 存在固化成员的项目
                List<FmMemberDto> projectFmMembers = projectFmMembersMap.get(projectEntry.getKey());
                Map<Long, List<FmMemberDto>> platFmMembersMap = projectFmMembers.stream().collect(Collectors.groupingBy(m -> m.getPlatId()));
                Map<Long, List<FmUserRoleDto>> platFmUserRolesMap = projectEntry.getValue().stream().collect(Collectors.groupingBy(ur -> ur.getPlatId()));
                for (Long platId : platFmUserRolesMap.keySet()) {
                    if (!platFmMembersMap.containsKey(platId)) {
                        continue;
                    }
                    if (!platWorkGroupMap.containsKey(platId)) {
                        continue;
                    }
                    PerformanceWorkGroupDto platWorkGroup = platWorkGroupMap.get(platId);
                    if (!parentWorkGroupsMap.containsKey(platWorkGroup.getId())) {
                        continue;
                    }

                    List<FmMemberDto> platFmMembers = platFmMembersMap.get(platId);

                    // 生成固化组初始化数据
                    FmGroupConfirmInfoParamDto fmGroupConfirmInfoParam = new FmGroupConfirmInfoParamDto();
                    fmGroupConfirmInfoParam.setYear(year);
                    fmGroupConfirmInfoParam.setMonth(month);
                    fmGroupConfirmInfoParam.setPlatId(platId);
                    fmGroupConfirmInfoParam.setPlatName(platNameMap.get(platId));
                    fmGroupConfirmInfoParam.setProjectId(projectEntry.getKey());
                    fmGroupConfirmInfoParam.setProjectName(projectNameMap.get(projectEntry.getKey()));

                    List<FmGroupConfirmInfoVo> projectPlatFmGroupConfirmInfoList = new ArrayList<>();

                    // 有效固化组
                    List<PerformanceWorkGroupDto> fmWorkGroups = parentWorkGroupsMap.get(platWorkGroup.getId());
                    for (PerformanceWorkGroupDto fmWorkGroup : fmWorkGroups) {
                        FmGroupConfirmInfoVo fmGroupConfirmInfo = getFmGroupConfirmInfo(fmGroupConfirmInfoParam, fmWorkGroup, fmGroupConfirmInfoMap, allWorkGroupUserPerformances, allPerformanceGroups, allUsers, platFmMembers);
                        if (null != fmGroupConfirmInfo) {
                            projectPlatFmGroupConfirmInfoList.add(fmGroupConfirmInfo);
                            switch (fmGroupConfirmInfo.getStatus()) {
                                case FmGroupConfirmInfo.Status.unsubmitted:
                                    unsubmittedCount++;
                                    break;
                                case FmGroupConfirmInfo.Status.unconfirmed:
                                    unconfirmedCount++;
                                    break;
                                case FmGroupConfirmInfo.Status.confirmed:
                                    break;
                                default:
                                    break;
                            }
                            total += fmGroupConfirmInfo.getTotal();
                        }
                    }

                    if (!projectPlatFmGroupConfirmInfoList.isEmpty()) {
                        projectPlatFmGroupConfirmInfoList.sort(Comparator.comparing(i -> i.getSort()));
                        ProjectFixMemberInfoVo.Plat fmPlat = new ProjectFixMemberInfoVo.Plat();
                        fmPlat.setName(platNameMap.get(platId));
                        fmPlat.setFmGroupConfirmInfoList(projectPlatFmGroupConfirmInfoList);
                        fmProject.getPlats().add(fmPlat);
                    }
                }
                if (total > 0) {
                    fmProject.setTotal(total);
                    fmProject.setUnsubmittedCount(unsubmittedCount);
                    fmProject.setUnconfirmedCount(unconfirmedCount);
                    projectFixMemberInfoVo.getProjects().add(fmProject);
                }
            }
        }

        return projectFixMemberInfoVo;
    }

    private FmGroupConfirmInfoVo getFmGroupConfirmInfo(FmGroupConfirmInfoParamDto fmGroupConfirmInfoParam, PerformanceWorkGroupDto fmWorkGroup,
                                                       Map<String, FmGroupConfirmInfoVo> fmGroupConfirmInfoMap, List<UserPerformanceDto> allWorkGroupUserPerformances,
                                                       List<WorkGroupDto> allPerformanceGroups, List<PerformanceUserDto> allUsers, List<FmMemberDto> fmMembers) {
        Map<Long, FmMemberDto> fmMemberMap = fmMembers.stream().collect(Collectors.toMap(m -> m.getUserId(), m -> m));
        Map<Long, UserPerformanceDto> userPerfByUserIdMap = allWorkGroupUserPerformances.stream().collect(Collectors.toMap(x -> x.getUserId(), x -> x, (oldValue, newValue) -> newValue));
        FmGroupConfirmInfoVo fmGroupConfirmInfo;
        String key = fmGroupConfirmInfoParam.getProjectId() + "_" + fmGroupConfirmInfoParam.getPlatId() + "_" + fmWorkGroup.getId();
        if (fmGroupConfirmInfoMap.containsKey(key)) {
            fmGroupConfirmInfo = fmGroupConfirmInfoMap.get(key);
            // 未确认的固化组重置成最新负责人
            if (FmGroupConfirmInfo.Status.unconfirmed.equals(fmGroupConfirmInfo.getStatus())) {
                fmGroupConfirmInfo.setPlatConfirmerId(fmWorkGroup.getPerformanceManagerId());
                fmGroupConfirmInfo.setPlatConfirmerName(fmWorkGroup.getManagerName());
            }
            // 固化组人数
            List<UserPerformanceDto> allSubUserPerformances = PerformanceDataHelper.getSubUserPerformances(fmGroupConfirmInfo.getPerformanceWorkGroupId(), allWorkGroupUserPerformances);
            Integer fixMemberCount = allSubUserPerformances.stream().filter(p -> fmGroupConfirmInfo.getProjectId().equals(p.getFmProjectId())).collect(Collectors.toList()).size();
            fmGroupConfirmInfo.setTotal(fixMemberCount);
        } else {
            // 生成当前固化组绩效树
            WorkGroupDto performanceRootGroup = PerformanceTreeHelper.getWorkGroupTree(fmWorkGroup.getId(), allPerformanceGroups, allUsers, false);
            List<PerformanceUserDto> allMembers = PerformanceTreeHelper.getAllMembersByRootWorkGroup(performanceRootGroup);
            List<PerformanceUserDto> fixMembers = allMembers.stream().filter(m -> fmMemberMap.containsKey(m.getUserId())).collect(Collectors.toList());
            Integer fixMemberCount = fixMembers.size();
            // 有固化成员才是有效固化组
            if (fixMemberCount > 0) {
                fmGroupConfirmInfo = new FmGroupConfirmInfoVo();
                fmGroupConfirmInfo.setYear(fmGroupConfirmInfoParam.getYear());
                fmGroupConfirmInfo.setMonth(fmGroupConfirmInfoParam.getMonth());
                fmGroupConfirmInfo.setPerformanceWorkGroupId(fmWorkGroup.getId());
                fmGroupConfirmInfo.setPerformanceWorkGroupName(fmWorkGroup.getName());
                fmGroupConfirmInfo.setPlatId(fmGroupConfirmInfoParam.getPlatId());
                fmGroupConfirmInfo.setPlatName(fmGroupConfirmInfoParam.getPlatName());
                fmGroupConfirmInfo.setProjectId(fmGroupConfirmInfoParam.getProjectId());
                fmGroupConfirmInfo.setProjectName(fmGroupConfirmInfoParam.getProjectName());
                fmGroupConfirmInfo.setPlatConfirmerId(fmWorkGroup.getPerformanceManagerId());
                fmGroupConfirmInfo.setPlatConfirmerName(fmWorkGroup.getManagerName());
                String status = FmGroupConfirmInfo.Status.unsubmitted;

                for (PerformanceUserDto fixMember : fixMembers) {
                    UserPerformanceDto userPerformanceDto = userPerfByUserIdMap.get(fixMember.getUserId());
                    if (userPerformanceDto != null) {
                        switch (userPerformanceDto.getStatus()) {
                            case UserPerformance.Status.complete:
                                status = UserPerformance.Status.complete;
                                break;
                            case UserPerformance.Status.locked:
                                status = UserPerformance.Status.submitted;
                                break;
                        }
                    }
                }
                fmGroupConfirmInfo.setStatus(status);
                fmGroupConfirmInfo.setTotal(fixMemberCount);
            } else {
                fmGroupConfirmInfo = null;
            }
        }
        int sort = 0;
        if (null != fmGroupConfirmInfo) {
            switch (fmGroupConfirmInfo.getStatus()) {
                case FmGroupConfirmInfo.Status.unsubmitted:
                    sort = 2;
                    break;
                case FmGroupConfirmInfo.Status.unconfirmed:
                    sort = 1;
                    break;
                case FmGroupConfirmInfo.Status.confirmed:
                    sort = 3;
                    break;
                default:
                    break;
            }
            fmGroupConfirmInfo.setSort(sort);
        }
        return fmGroupConfirmInfo;
    }

    @Override
    public FmGroupConfirmInfoVo getFmGroupConfirmInfo(FmGroupConfirmInfoVo fmGroupConfirmInfo) {
        Long performanceGroupId = fmGroupConfirmInfo.getPerformanceWorkGroupId();
        Integer year = fmGroupConfirmInfo.getYear();
        Integer month = fmGroupConfirmInfo.getMonth();
        Long projectId = fmGroupConfirmInfo.getProjectId();
        Long platId = fmGroupConfirmInfo.getPlatId();

        List<FmMemberDto> allFmMembers = fmMemberMapper.selectAllByProjectIdAndPlatId(projectId, platId);
        List<UserPerformanceDto> allUserPerformances = userPerformanceMapper.selectAllByYearAndMonth(year, month);
        List<PerformanceUserDto> allUsers = userMapper.selectAllEntityWithPerformanceByPerformanceWorkGroup();
        List<WorkGroupDto> allPerformanceGroups = performanceWorkGroupMapper.selectAllByActive();
        List<CfgPerfHideManagerComment> cfgPerfHideManagerComments = cfgPerfHideManagerCommentMapper.selectAll();
        List<PerformanceWorkGroupDto> allPerformanceWorkGroups = performanceWorkGroupMapper.selectAllWithProjectIdByActive();
        List<PerfWorkGroupStatus> perfWorkGroupStatuses = perfWorkGroupStatusMapper.selectByYearAndMonth(year, month);

        // 把记录放到map里方便查找
        Map<Long, UserPerformanceDto> userPerformanceMap = allUserPerformances.stream().collect(Collectors.toMap(p -> p.getUserId(), p -> p));
        Map<Long, String> performanceWorkGroupStatusMap = PerformanceDataHelper.getPerformanceWorkGroupStatusMap(allPerformanceWorkGroups, perfWorkGroupStatuses);
        Map<Long, FmMemberDto> fmMemberMap = allFmMembers.stream().collect(Collectors.toMap(m -> m.getUserId(), m -> m));

        // 重设员工绩效组
        PerformanceDataHelper.resetUserPerformanceWorkGroup(allUsers, userPerformanceMap);

        // 找出根目录，初始化树
        WorkGroupDto rootWorkGroup = PerformanceTreeHelper.getWorkGroupTree(performanceGroupId, allPerformanceGroups, allUsers, true);

        // 获取所有下属绩效记录
        List<PerformanceUserDto> allMembers = PerformanceTreeHelper.getAllMembersByRootWorkGroup(rootWorkGroup);
        List<MemberPerformanceAppVo> allMemberPerformanceAppVos = getMemberPerformanceList(allMembers, userPerformanceMap, cfgPerfHideManagerComments, allPerformanceWorkGroups, performanceWorkGroupStatusMap, fmMemberMap, null);
        allMemberPerformanceAppVos = allMemberPerformanceAppVos.stream().filter(p -> fmMemberMap.containsKey(p.getUserId())).collect(Collectors.toList());
        allMemberPerformanceAppVos.sort((x, y) -> {
            int i = x.getSort().compareTo(y.getSort());
            if (i == 0) {
                i = x.getLoginId().compareTo(y.getLoginId());
            }
            return i;
        });

        fmGroupConfirmInfo.setMemberPerformances(allMemberPerformanceAppVos);
        fmGroupConfirmInfo.setTotal(allMemberPerformanceAppVos.size());
        fmGroupConfirmInfo.setStatus(FmGroupConfirmInfo.Status.unsubmitted);
        fmGroupConfirmInfo.setPerformanceWorkGroupName(rootWorkGroup.getName());
        if (null != rootWorkGroup.getLeader()) {
            fmGroupConfirmInfo.setPlatConfirmerName(rootWorkGroup.getLeader().getName());
        }
        return fmGroupConfirmInfo;
    }

    private int getSortNumber(SubFixMemberPerformanceVo.Project project) {
        if (project.getManager() == null) {
            return 3;
        } else if (project.getConfirmFlag()) {
            return 2;
        } else {
            return 1;
        }
    }
}
