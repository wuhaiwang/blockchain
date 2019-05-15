package com.seasun.management.service.performanceCore.userPerformance;

import com.seasun.management.dto.*;
import com.seasun.management.helper.ReportHelper;
import com.seasun.management.helper.UserPerformanceHelper;
import com.seasun.management.mapper.*;
import com.seasun.management.model.*;
import com.seasun.management.util.*;
import com.seasun.management.vo.*;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class PerformanceDataHelper {

    public static boolean checkComplete(int year, int month, List<YearMonthDto> completeYearMonth) {
        boolean isComplete = false;
        for (YearMonthDto yearMonthDto : completeYearMonth) {
            isComplete = yearMonthDto.getYear().equals(year) && yearMonthDto.getMonth().equals(month);
            if (isComplete) {
                break;
            }
        }
        return isComplete;
    }

    public static List<SubPerformanceAppVo.HistoryInfo> getWorkGroupHistory(Long managerId, Long observerWorkGroupId) {
        UserPerformanceMapper userPerformanceMapper = MyBeanUtils.getBean(UserPerformanceMapper.class);
        PerfWorkGroupStatusMapper perfWorkGroupStatusMapper = MyBeanUtils.getBean(PerfWorkGroupStatusMapper.class);

        Integer startDay = MySystemParamUtils.getSystemConfigWithDefaultValue(MySystemParamUtils.Key.PerformanceStartDay, MySystemParamUtils.DefaultValue.PerformanceStartDay);

        // 当前时间
        Calendar now = ReportHelper.getDateOnly();
        int nowYear = now.get(Calendar.YEAR);
        int nowMonth = now.get(Calendar.MONTH) + 1;

        List<SubPerformanceAppVo.HistoryInfo> historyInfos = new ArrayList<>();

        Calendar startDate = ReportHelper.getDateOnly();

        Map<String, List<HistoryStatusDto>> historyStatusMap = new HashMap<>();
        List<YearMonthDto> completeYearMonth = perfWorkGroupStatusMapper.selectAllWithYearMonthByComplete();
        List<HistoryStatusDto> historyStatusDtos;

        // 若是普通情况，则进入以下分支
        if (null != managerId) {
            historyStatusDtos = perfWorkGroupStatusMapper.selectHistoryStatusByManagerId(managerId);
        }
        // 若是观察者中的观察组，则进入以下分支
        else {
            historyStatusDtos = perfWorkGroupStatusMapper.selectHistoryStatusByWorkGroupId(observerWorkGroupId);
        }

        // 若存在直接下属的绩效记录
        if (!historyStatusDtos.isEmpty()) {
            startDate.set(historyStatusDtos.get(0).getYear(), historyStatusDtos.get(0).getMonth() - 1, 1);
            historyStatusMap = historyStatusDtos.stream().collect(Collectors.groupingBy(hs -> hs.getYear() + "-" + hs.getMonth()));
        }
        // 若不存在直接下属 todo: 该情况基本不存在，这个分支进不去
        else {
            YearMonthDto yearMonthDto = perfWorkGroupStatusMapper.selectLastCompleteYearMonth();
            if (null != yearMonthDto) {
                startDate.set(yearMonthDto.getYear(), yearMonthDto.getMonth() - 1, 1);
                startDate.add(Calendar.MONTH, 1);
            }
        }
        int curYear = startDate.get(Calendar.YEAR);
        int curMonth = 1;
        // 补全月份
        while (curMonth < startDate.get(Calendar.MONTH) + 1) {
            SubPerformanceAppVo.HistoryInfo historyInfo = new SubPerformanceAppVo.HistoryInfo();
            historyInfo.setYear(curYear);
            historyInfo.setMonth(curMonth);
            historyInfos.add(historyInfo);
            curMonth++;
        }
        while (curYear < nowYear || (curYear == nowYear && curMonth <= nowMonth)) {
            String status = SubPerformanceAppVo.HistoryInfo.Status.processing;
            boolean isComplete = checkComplete(curYear, curMonth, completeYearMonth);


            // 已完成
            if (isComplete) {
                status = SubPerformanceAppVo.HistoryInfo.Status.complete;
            }
            // 未开始
            if (curYear == nowYear && curMonth == nowMonth && now.get(Calendar.DAY_OF_MONTH) < startDay) {
                status = SubPerformanceAppVo.HistoryInfo.Status.waitingForStart;
            } else {
                // 已提交
                String key = curYear + "-" + curMonth;
                if (historyStatusMap.containsKey(key)) {
                    List<HistoryStatusDto> monthHistoryStatus = historyStatusMap.get(key);
                    for (HistoryStatusDto historyStatusDto : monthHistoryStatus) {
                        if (UserPerformance.Status.submitted.equals(historyStatusDto.getStatus())) {
                            status = SubPerformanceAppVo.HistoryInfo.Status.submitted;
                            break;
                        }
                    }
                }
                if (SubPerformanceAppVo.HistoryInfo.Status.processing.equals(status)
                        && UserPerformanceHelper.isDelay(curYear, curMonth, startDay)) {
                    status = SubPerformanceAppVo.HistoryInfo.Status.delay;
                }
            }

            SubPerformanceAppVo.HistoryInfo historyInfo = new SubPerformanceAppVo.HistoryInfo();
            historyInfo.setYear(curYear);
            historyInfo.setMonth(curMonth);
            historyInfo.setStatus(status);
            historyInfos.add(historyInfo);

            startDate.add(Calendar.MONTH, 1);
            curYear = startDate.get(Calendar.YEAR);
            curMonth = startDate.get(Calendar.MONTH) + 1;
        }
        // 补全月份
        while (curMonth <= 12) {
            SubPerformanceAppVo.HistoryInfo historyInfo = new SubPerformanceAppVo.HistoryInfo();
            historyInfo.setYear(curYear);
            historyInfo.setMonth(curMonth);
            historyInfos.add(historyInfo);
            curMonth++;
        }

        historyInfos.sort((x, y) -> {
            int i = x.getYear().compareTo(y.getYear());
            if (i == 0) {
                i = x.getMonth().compareTo(y.getMonth());
            }
            return i;
        });
        return historyInfos;
    }

    public static ProjectFixMemberInfoVo.History getProjectFixMemberHistory(Long userId, Integer year, Integer month, Boolean defaultFlag) {
        UserPerformanceMapper userPerformanceMapper = MyBeanUtils.getBean(UserPerformanceMapper.class);
        FmGroupConfirmInfoMapper fmGroupConfirmInfoMapper = MyBeanUtils.getBean(FmGroupConfirmInfoMapper.class);
        FmUserRoleMapper fmUserRoleMapper = MyBeanUtils.getBean(FmUserRoleMapper.class);
        PerformanceWorkGroupMapper performanceWorkGroupMapper = MyBeanUtils.getBean(PerformanceWorkGroupMapper.class);
        ProjectMapper projectMapper = MyBeanUtils.getBean(ProjectMapper.class);
        FmMemberMapper fmMemberMapper = MyBeanUtils.getBean(FmMemberMapper.class);

        // 判读是否是项目负责人
        List<IdNameBaseObject> fmUserRoleDtos = fmUserRoleMapper.selectUserFixSecondProjectsByUserId(userId);
        List<FmGroupConfirmInfo> fmGroupConfirmInfoList;
        if (!fmUserRoleDtos.isEmpty()) {
            fmGroupConfirmInfoList = fmGroupConfirmInfoMapper.selectAllByUserIdAndFmRoleId(userId,FmRole.Role.projectFixSecondConfirmer);
        } else {
            fmGroupConfirmInfoList = fmGroupConfirmInfoMapper.selectAllByUserIdAndFmRoleId(userId,FmRole.Role.projectFixFirstConfirmer);
        }
        List<PerformanceWorkGroupDto> performanceWorkGroups = performanceWorkGroupMapper.selectAllByProjectConfirm();
        List<FmMemberDto> allFmMembers = fmMemberMapper.selectAll();

        Map<String, List<FmGroupConfirmInfo>> fmGroupConfirmInfoListMap = fmGroupConfirmInfoList.stream().collect(Collectors.groupingBy(i -> i.getYear() + "-" + i.getMonth()));

        // 平台级别需项目确认绩效组
        Map<Long, PerformanceWorkGroupDto> platWorkGroupMap = performanceWorkGroups.stream().filter(g -> null != g.getProjectId()).collect(Collectors.toMap(g -> g.getProjectId(), g -> g));
        //普通固化组
        Map<Long, PerformanceWorkGroupDto> parentWorkGroupMap = performanceWorkGroups.stream().filter(g -> null != g.getProjectId()).collect(Collectors.toMap(g -> g.getId(), g -> g));
        Map<Long, List<FmMemberDto>> projectFmMembersMap = allFmMembers.stream().collect(Collectors.groupingBy(m -> m.getProjectId()));

        // 历史记录
        Calendar now = ReportHelper.getDateOnly();
        int nowYear = now.get(Calendar.YEAR);
        int nowMonth = now.get(Calendar.MONTH) + 1;

        ProjectFixMemberInfoVo.History history = new ProjectFixMemberInfoVo.History();
        history.setAll(new ArrayList<>());

        Calendar startDate = ReportHelper.getDateOnly();

        Integer startDay = MySystemParamUtils.getSystemConfigWithDefaultValue(MySystemParamUtils.Key.PerformanceStartDay, MySystemParamUtils.DefaultValue.PerformanceStartDay);

        List<YearMonthDto> completeYearMonth = userPerformanceMapper.selectAllWithYearMonthByComplete();
        if (!completeYearMonth.isEmpty()) {
            startDate.set(completeYearMonth.get(0).getYear(), completeYearMonth.get(0).getMonth() - 1, 1);
        }
        int curYear = startDate.get(Calendar.YEAR);
        int curMonth = 1;
        // 补全月份
        while (curMonth < startDate.get(Calendar.MONTH) + 1) {
            SubPerformanceBaseVo.HistoryInfo historyInfo = new SubPerformanceBaseVo.HistoryInfo();
            historyInfo.setYear(curYear);
            historyInfo.setMonth(curMonth);
            history.getAll().add(historyInfo);
            curMonth++;
        }
        while (curYear < nowYear || (curYear == nowYear && curMonth <= nowMonth)) {
            String key = curYear + "-" + curMonth;
            String status = SubPerformanceBaseVo.HistoryInfo.Status.processing;
            boolean isComplete = checkComplete(curYear, curMonth, completeYearMonth);
            if (isComplete) {
                if (fmGroupConfirmInfoListMap.containsKey(key)) {
                    status = SubPerformanceBaseVo.HistoryInfo.Status.complete;
                } else {
                    status = null;
                }
            } else {
                if (fmGroupConfirmInfoListMap.containsKey(key)) {
                    boolean isConfirmed = true;
                    List<FmGroupConfirmInfo> curFmGroupConfirmInfoList = fmGroupConfirmInfoListMap.get(key);
                    Map<Long, List<FmGroupConfirmInfo>> groupFmGroupConfirmInfoListMap = curFmGroupConfirmInfoList.stream().collect(Collectors.groupingBy(i -> i.getPerformanceWorkGroupId()));

                    // 若管理了项目，则走二次审核的逻辑
                    if (!fmUserRoleDtos.isEmpty()) {
                        for (IdNameBaseObject project : fmUserRoleDtos) {
                            //该项目无固化成员，则跳过
                            if (!projectFmMembersMap.containsKey(project.getId())) {
                                continue;
                            }
                            List<FmMemberDto> projectFmMembers = projectFmMembersMap.get(project.getId());
                            Map<Long, List<FmMemberDto>> platFmMembersMap = projectFmMembers.stream().collect(Collectors.groupingBy(m -> m.getPlatId()));
                            for (Long platId : platFmMembersMap.keySet()) {
                                if (!platWorkGroupMap.containsKey(platId)) {
                                    continue;
                                }

                                // 找出有效固化组
                                PerformanceWorkGroupDto platWorkGroup = platWorkGroupMap.get(platId);
                                if (groupFmGroupConfirmInfoListMap.containsKey(platWorkGroup.getId())) {
                                    List<FmGroupConfirmInfo> groupFmGroupConfirmInfoList = groupFmGroupConfirmInfoListMap.get(platWorkGroup.getId());
                                    for (FmGroupConfirmInfo fmGroupConfirmInfo : groupFmGroupConfirmInfoList) {
                                        if (FmGroupConfirmInfo.Status.unconfirmed.equals(fmGroupConfirmInfo.getStatus())) {
                                            isConfirmed = false;
                                            break;
                                        }
                                    }
                                    if (!isConfirmed) {
                                        break;
                                    }
                                } else {
                                    isConfirmed = false;
                                    break;
                                }
                            }
                        }
                    }

                    // 若没管理项目，则走首次审核员的逻辑
                    else {
                        Map<Long, List<PerformanceWorkGroupDto>> parentWorkGroupsMap = performanceWorkGroups.stream().filter(g -> null != g.getParent() && parentWorkGroupMap.containsKey(g.getParent())).collect(Collectors.groupingBy(g -> g.getParent()));
                        List<FmUserRoleDto> fmUserRoles = fmUserRoleMapper.selectAllByUserIdAndRoleId(userId, FmRole.Role.projectFixFirstConfirmer);
                        Map<Long, List<FmUserRoleDto>> projectFmUserRolesMap = fmUserRoles.stream().collect(Collectors.groupingBy(ur -> ur.getProjectId()));
                        for (Map.Entry<Long, List<FmUserRoleDto>> projectEntry : projectFmUserRolesMap.entrySet()) {
                            if (!projectFmMembersMap.containsKey(projectEntry.getKey())) {
                                continue;
                            }
                            // 存在固化成员的项目
                            List<FmMemberDto> projectFmMembers = projectFmMembersMap.get(projectEntry.getKey());
                            Map<Long, List<FmMemberDto>> platFmMembersMap = projectFmMembers.stream().collect(Collectors.groupingBy(m -> m.getPlatId()));
                            Map<Long, List<FmUserRoleDto>> platFmUserRolesMap = projectEntry.getValue().stream().collect(Collectors.groupingBy(ur -> ur.getPlatId()));
                            for (Map.Entry<Long, List<FmUserRoleDto>> platEntry : platFmUserRolesMap.entrySet()) {
                                if (!platFmMembersMap.containsKey(platEntry.getKey())) {
                                    continue;
                                }
                                if (!platWorkGroupMap.containsKey(platEntry.getKey())) {
                                    continue;
                                }
                                PerformanceWorkGroupDto platWorkGroup = platWorkGroupMap.get(platEntry.getKey());
                                if (!parentWorkGroupsMap.containsKey(platWorkGroup.getId())) {
                                    continue;
                                }
                                // 有效固化组
                                List<PerformanceWorkGroupDto> fmWorkGroups = parentWorkGroupsMap.get(platWorkGroup.getId());
                                for (PerformanceWorkGroupDto fmWorkGroup : fmWorkGroups) {
                                    if (groupFmGroupConfirmInfoListMap.containsKey(fmWorkGroup.getId())) {
                                        List<FmGroupConfirmInfo> groupFmGroupConfirmInfoList = groupFmGroupConfirmInfoListMap.get(fmWorkGroup.getId());
                                        for (FmGroupConfirmInfo fmGroupConfirmInfo : groupFmGroupConfirmInfoList) {
                                            if (FmGroupConfirmInfo.Status.unconfirmed.equals(fmGroupConfirmInfo.getStatus())) {
                                                isConfirmed = false;
                                                break;
                                            }
                                        }
                                        if (!isConfirmed) {
                                            break;
                                        }
                                    } else {
                                        isConfirmed = false;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    if (isConfirmed) {
                        status = SubPerformanceBaseVo.HistoryInfo.Status.confirmed;
                    }
                }
                if (curYear == nowYear & curMonth == nowMonth & now.get(Calendar.DAY_OF_MONTH) < startDay) {
                    status = SubPerformanceBaseVo.HistoryInfo.Status.waitingForStart;
                } else if (SubPerformanceAppVo.HistoryInfo.Status.processing.equals(status) && UserPerformanceHelper.isDelay(curYear, curMonth, startDay)) {
                    status = SubPerformanceBaseVo.HistoryInfo.Status.delay;
                }
            }

            SubPerformanceBaseVo.HistoryInfo historyInfo = new SubPerformanceBaseVo.HistoryInfo();
            historyInfo.setYear(curYear);
            historyInfo.setMonth(curMonth);
            historyInfo.setStatus(status);
            history.getAll().add(historyInfo);

            startDate.add(Calendar.MONTH, 1);
            curYear = startDate.get(Calendar.YEAR);
            curMonth = startDate.get(Calendar.MONTH) + 1;
        }
        // 补全月份
        while (curMonth <= 12) {
            SubPerformanceBaseVo.HistoryInfo historyInfo = new SubPerformanceBaseVo.HistoryInfo();
            historyInfo.setYear(curYear);
            historyInfo.setMonth(curMonth);
            history.getAll().add(historyInfo);
            curMonth++;
        }

        history.getAll().sort((x, y) -> {
            int i = x.getYear().compareTo(y.getYear());
            if (i == 0) {
                i = x.getMonth().compareTo(y.getMonth());
            }
            return i;
        });

        int index = 0;
        history.setStartIndex(index);

        // add missed code
        history.setYear(year);
        history.setMonth(month);

        for (SubPerformanceBaseVo.HistoryInfo historyInfo : history.getAll()) {
            if (null == history.getSelectIndex() && (year.equals(historyInfo.getYear()) && month.equals(historyInfo.getMonth()))) {
                history.setSelectIndex(index);
            }
            if (history.getStartIndex() == 0 && (SubPerformanceBaseVo.HistoryInfo.Status.delay.equals(historyInfo.getStatus()) || SubPerformanceBaseVo.HistoryInfo.Status.processing.equals(historyInfo.getStatus()))) {
                history.setStartIndex(index);
                if (defaultFlag) {
                    history.setSelectIndex(index);
                    history.setYear(historyInfo.getYear());
                    history.setMonth(historyInfo.getMonth());
                }
            }
            index++;
        }

        return history;
    }

    public static MemberPerformanceAppVo getMemberPerformanceAppVo(UserPerformanceDto userPerformanceDto, Boolean showManagerCommentFlag,
                                                                   Boolean notInTheGroupFlag, Boolean fixMemberFlag, Boolean projectManagerFlag) {
        MemberPerformanceAppVo memberPerformanceAppVo = new MemberPerformanceAppVo();
        memberPerformanceAppVo.setUserId(userPerformanceDto.getUserId());
        memberPerformanceAppVo.setLoginId(userPerformanceDto.getLoginId());
        memberPerformanceAppVo.setEmployeeNo(userPerformanceDto.getEmployeeNo());
        memberPerformanceAppVo.setName(userPerformanceDto.getUserName());
        memberPerformanceAppVo.setId(userPerformanceDto.getId());

        // web端需要设置评论详情
        if (MyTokenUtils.getChannel().equals(MyTokenUtils.Channel.pc)) {
            if (!UserPerformance.Status.complete.equals(userPerformanceDto.getStatus()) || showManagerCommentFlag) {
                memberPerformanceAppVo.setManagerComment(userPerformanceDto.getManagerComment() == null ? null : MyEmojiUtil.resolveToEmojiFromByte(userPerformanceDto.getManagerComment()));
            }
        }

        memberPerformanceAppVo.setMonthGoalFlag(null != userPerformanceDto.getMonthGoal());
        memberPerformanceAppVo.setManagerCommentFlag(null != userPerformanceDto.getManagerComment());
        memberPerformanceAppVo.setSelfPerformance(userPerformanceDto.getSelfPerformance());
        memberPerformanceAppVo.setFinalPerformance(userPerformanceDto.getFinalPerformance());
        if (null != projectManagerFlag && projectManagerFlag) {
            if (UserPerformance.FmConfirmedStatus.secondConfirmed.equals(userPerformanceDto.getFmConfirmedStatus())) {
                memberPerformanceAppVo.setStatus(MemberPerformanceAppVo.Status.confirmed);
            } else {
                memberPerformanceAppVo.setStatus(UserPerformanceHelper.getSubMemberStatusChangeMap().get(userPerformanceDto.getStatus()));
            }
        } else {
            if (UserPerformance.FmConfirmedStatus.firstConfirmed.equals(userPerformanceDto.getFmConfirmedStatus()) || UserPerformance.FmConfirmedStatus.secondConfirmed.equals(userPerformanceDto.getFmConfirmedStatus())) {
                memberPerformanceAppVo.setStatus(MemberPerformanceAppVo.Status.confirmed);
            } else {
                memberPerformanceAppVo.setStatus(UserPerformanceHelper.getSubMemberStatusChangeMap().get(userPerformanceDto.getStatus()));
            }
        }
        memberPerformanceAppVo.setSort(UserPerformanceHelper.getSubMemberStatusSortMap().get(memberPerformanceAppVo.getStatus()));
        memberPerformanceAppVo.setDirectGroupName(userPerformanceDto.getWorkGroupName());
        memberPerformanceAppVo.setOutOfGroupFlag(notInTheGroupFlag);
        memberPerformanceAppVo.setFixMemberFlag(fixMemberFlag);

        return memberPerformanceAppVo;
    }

    public static PerformanceWorkGroupInfoAppVo.PerformancePro getPerformancePro(List<UserPerformanceDto> userPerformanceDtos) {
        int sCount = 0, aCount = 0, bCount = 0, cCount = 0, totalCount;
        for (UserPerformanceDto userPerformanceDto : userPerformanceDtos) {
            if (null == userPerformanceDto.getFinalPerformance()) {
                continue;
            }
            switch (userPerformanceDto.getFinalPerformance()) {
                case UserPerformance.Performance.S:
                    sCount++;
                    break;
                case UserPerformance.Performance.A:
                    aCount++;
                    break;
                case UserPerformance.Performance.B:
                    bCount++;
                    break;
                case UserPerformance.Performance.C:
                    cCount++;
                    break;
                default:
                    break;
            }
        }
        totalCount = sCount + aCount + bCount + cCount;
        PerformanceWorkGroupInfoAppVo.PerformancePro performancePro = new PerformanceWorkGroupInfoAppVo.PerformancePro();
        if (totalCount > 0) {
            performancePro.setS(ReportHelper.formatNumberByScale(((float) sCount * 100 / totalCount), 1));
            performancePro.setA(ReportHelper.formatNumberByScale(((float) aCount * 100 / totalCount), 1));
            performancePro.setB(ReportHelper.formatNumberByScale(((float) bCount * 100 / totalCount), 1));
            performancePro.setC(ReportHelper.formatNumberByScale(((float) cCount * 100 / totalCount), 1));
            performancePro.setSa(ReportHelper.formatNumberByScale(((float) (sCount + aCount) * 100 / totalCount), 1));
        } else {
            performancePro.setS(0F);
            performancePro.setA(0F);
            performancePro.setB(0F);
            performancePro.setC(0F);
            performancePro.setSa(0F);
        }
        performancePro.setSCount(sCount);
        performancePro.setACount(aCount);
        performancePro.setBCount(bCount);
        performancePro.setCCount(cCount);
        return performancePro;
    }

    private static List<Long> getAllGroupIds(WorkGroupDto performanceGroup) {
        List<Long> allGroupIds = new ArrayList<>();
        allGroupIds.add(performanceGroup.getId());
        if (null != performanceGroup.getChildWorkGroups() && !performanceGroup.getChildWorkGroups().isEmpty()) {
            List<WorkGroupDto> childWorkGroups = performanceGroup.getChildWorkGroups();
            for (WorkGroupDto childGroup : childWorkGroups) {
                allGroupIds.addAll(getAllGroupIds(childGroup));
            }
        }
        return allGroupIds;
    }

    public static List<UserPerformanceDto> getCurrentSubUserPerformances(WorkGroupDto performanceGroup, List<UserPerformanceDto> allUserPerformances) {
        List<UserPerformanceDto> userPerformances = new ArrayList<>();
        List<Long> allGroupIds = getAllGroupIds(performanceGroup);
        for (UserPerformanceDto userPerformance : allUserPerformances) {
            if (allGroupIds.contains(userPerformance.getWorkGroupId())) {
                userPerformances.add(userPerformance);
            }
        }
        return userPerformances;
    }

    public static List<UserPerformanceDto> getSubUserPerformances(Long performanceGroupId, List<UserPerformanceDto> allUserPerformances) {
        List<UserPerformanceDto> userPerformances = new ArrayList<>();
        List<Long> subGroupIds = new ArrayList<>();
        for (UserPerformanceDto userPerformance : allUserPerformances) {
            if (!performanceGroupId.equals(userPerformance.getWorkGroupId())) {
                if (performanceGroupId.equals(userPerformance.getParentGroup()) && !subGroupIds.contains(userPerformance.getWorkGroupId())) {
                    subGroupIds.add(userPerformance.getWorkGroupId());
                }
                continue;
            }
            userPerformances.add(userPerformance);
        }
        for (Long subGroupId : subGroupIds) {
            List<UserPerformanceDto> subUserPerformances = getSubUserPerformances(subGroupId, allUserPerformances);
            userPerformances.addAll(subUserPerformances);
        }
        return userPerformances;
    }

    public static void resetUserPerformanceWorkGroup(List<PerformanceUserDto> allUsers, Map<Long, UserPerformanceDto> userPerformanceMap) {
        for (PerformanceUserDto user : allUsers) {
            if (userPerformanceMap.containsKey(user.getUserId())) {
                UserPerformanceDto userPerformance = userPerformanceMap.get(user.getUserId());
                user.setWorkGroupId(userPerformance.getWorkGroupId());
                user.setParentWorkGroupId(userPerformance.getParentGroup());
            }
        }
    }

    //此方法已无用...
    public static String getPerformanceWorkGroupStatus(Long perfWorkGroupId, Map<Long, String> performanceWorkGroupStatusMap) {
        String status = SubPerformanceAppVo.HistoryInfo.Status.processing;
        if (performanceWorkGroupStatusMap.containsKey(perfWorkGroupId)) {
            status = performanceWorkGroupStatusMap.get(perfWorkGroupId);
        }
        return status;
    }

    public static Map<Long, String> getPerformanceWorkGroupStatusMap(List<UserPerformanceDto> allUserPerformances) {
        Map<Long, String> performanceWorkGroupStatusMap = new HashMap<>();
        Map<Long, List<UserPerformanceDto>> workGroupUserPerformancesMap = allUserPerformances.stream().collect(Collectors.groupingBy(p -> p.getWorkGroupId()));
        for (Map.Entry<Long, List<UserPerformanceDto>> entry : workGroupUserPerformancesMap.entrySet()) {
            String status = SubPerformanceAppVo.HistoryInfo.Status.processing;
            List<UserPerformanceDto> workGroupUserPerformances = entry.getValue();
            for (UserPerformanceDto userPerformanceDto : workGroupUserPerformances) {
                if (UserPerformance.Status.complete.equals(userPerformanceDto.getStatus())) {
                    status = SubPerformanceAppVo.HistoryInfo.Status.complete;
                    break;
                } else if (UserPerformance.Status.locked.equals(userPerformanceDto.getStatus())) {
                    status = SubPerformanceAppVo.HistoryInfo.Status.submitted;
                    break;
                }
            }
            performanceWorkGroupStatusMap.put(entry.getKey(), status);
        }
        return performanceWorkGroupStatusMap;
    }

    public static Map<Long, String> getPerformanceWorkGroupStatusMap(List<PerformanceWorkGroupDto> allGroups, List<PerfWorkGroupStatus> allGroupStatus) {
        Map<Long, String> performanceWorkGroupStatusMap = new HashMap<>();
        Map<Long, PerfWorkGroupStatus> groupStatusByGroupId = allGroupStatus.stream().collect(Collectors.toMap(x -> x.getPerfWorkGroupId(), x -> x));
        for (PerformanceWorkGroupDto group : allGroups) {
            String status = SubPerformanceAppVo.HistoryInfo.Status.processing;
            PerfWorkGroupStatus perfWorkGroupStatus = groupStatusByGroupId.get(group.getId());
            if (perfWorkGroupStatus != null) {
                if (UserPerformance.Status.complete.equals(perfWorkGroupStatus.getStatus())) {
                    status = UserPerformance.Status.complete;
                }
                if (UserPerformance.Status.submitted.equals(perfWorkGroupStatus.getStatus())) {
                    status = UserPerformance.Status.submitted;
                }
            }
            performanceWorkGroupStatusMap.put(group.getId(), status);
        }
        return performanceWorkGroupStatusMap;
    }

    public static List<MemberPerformanceAppVo> filerMemberPerformances(List<MemberPerformanceAppVo> allMemberPerformances, String filter) {
        List<MemberPerformanceAppVo> memberPerformances;
        if (null == filter) {
            memberPerformances = allMemberPerformances;
        } else {
            switch (filter) {
                case UserPerformanceHelper.SearchFilter.s:
                    memberPerformances = allMemberPerformances.stream().filter(p -> UserPerformance.Performance.S.equals(p.getFinalPerformance())).collect(Collectors.toList());
                    break;
                case UserPerformanceHelper.SearchFilter.a:
                    memberPerformances = allMemberPerformances.stream().filter(p -> UserPerformance.Performance.A.equals(p.getFinalPerformance())).collect(Collectors.toList());
                    break;
                case UserPerformanceHelper.SearchFilter.b:
                    memberPerformances = allMemberPerformances.stream().filter(p -> UserPerformance.Performance.B.equals(p.getFinalPerformance())).collect(Collectors.toList());
                    break;
                case UserPerformanceHelper.SearchFilter.c:
                    memberPerformances = allMemberPerformances.stream().filter(p -> UserPerformance.Performance.C.equals(p.getFinalPerformance())).collect(Collectors.toList());
                    break;
                case UserPerformanceHelper.SearchFilter.unsubmitted:
                    memberPerformances = allMemberPerformances.stream().filter(p -> MemberPerformanceAppVo.Status.unsubmitted.equals(p.getStatus())).collect(Collectors.toList());
                    break;
                case UserPerformanceHelper.SearchFilter.submitted:
                    memberPerformances = allMemberPerformances.stream().filter(p -> MemberPerformanceAppVo.Status.submitted.equals(p.getStatus())).collect(Collectors.toList());
                    break;
                case UserPerformanceHelper.SearchFilter.assessed:
                    memberPerformances = allMemberPerformances.stream().filter(p -> MemberPerformanceAppVo.Status.assessed.equals(p.getStatus())).collect(Collectors.toList());
                    break;
                case UserPerformanceHelper.SearchFilter.invalided:
                    memberPerformances = allMemberPerformances.stream().filter(p -> UserPerformance.Performance.invalided.equals(p.getFinalPerformance())).collect(Collectors.toList());
                    break;
                default:
                    memberPerformances = allMemberPerformances;
                    break;
            }
        }
        memberPerformances.sort((x, y) -> {
            int i = x.getDirectGroupName().compareTo(y.getDirectGroupName());
            if (i == 0) {
                i = x.getSort().compareTo(y.getSort());
                if (i == 0) {
                    i = x.getLoginId().compareTo(y.getLoginId());
                }
            }
            return i;
        });
        return memberPerformances;
    }

    public static UserPerformanceDto getNewInvalidedUserPerformance(PerformanceUserDto user, int year, int month, String status) {
        UserPerformanceDto userPerformanceDto = new UserPerformanceDto();
        userPerformanceDto.setYear(year);
        userPerformanceDto.setMonth(month);
        userPerformanceDto.setUserId(user.getUserId());
        userPerformanceDto.setWorkGroupId(user.getWorkGroupId());
        userPerformanceDto.setParentGroup(user.getParentWorkGroupId());
        userPerformanceDto.setFinalPerformance(UserPerformance.Performance.invalided);
        userPerformanceDto.setStatus(status);
        userPerformanceDto.setCreateTime(new Date());
        userPerformanceDto.setUpdateTime(new Date());

        userPerformanceDto.setWorkAge(user.getWorkAge());
        userPerformanceDto.setWorkAgeInKs(user.getWorkAgeInKs());
        userPerformanceDto.setPost(user.getPost());
        userPerformanceDto.setWorkGroupName(user.getWorkGroupName());
        userPerformanceDto.setWorkStatus(user.getWorkStatus());

        return userPerformanceDto;
    }

    public static <T> Map<Long, String> getPlatNameMap(List<T> list) {
        Map<Long, String> platNameMap = new HashMap<>();
        for (T obj : list) {
            try {
                Method m = obj.getClass().getMethod(
                        "get" + MyStringUtils.convertFirstCharToUppercase("platId"));
                Long platId = (Long) m.invoke(obj);
                m = obj.getClass().getMethod(
                        "get" + MyStringUtils.convertFirstCharToUppercase("platName"));
                String platName = (String) m.invoke(obj);

                if (platNameMap.containsKey(platId)) {
                    continue;
                }
                platNameMap.put(platId, platName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return platNameMap;
    }

    public static <T> Map<Long, String> getProjectNameMap(List<T> list) {
        Map<Long, String> projectNameMap = new HashMap<>();
        for (T obj : list) {
            try {
                Method m = obj.getClass().getMethod(
                        "get" + MyStringUtils.convertFirstCharToUppercase("projectId"));
                Long projectId = (Long) m.invoke(obj);
                m = obj.getClass().getMethod(
                        "get" + MyStringUtils.convertFirstCharToUppercase("projectName"));
                String projectName = (String) m.invoke(obj);

                if (projectNameMap.containsKey(projectId)) {
                    continue;
                }
                projectNameMap.put(projectId, projectName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return projectNameMap;
    }

    public static void setUserPerformanceFmGroupInfo(UserPerformanceDto userPerformance, Map<Long, FmMemberDto> fmMemberMap, String fmConfirmedStatus, Set<Long> projectIds) {
        if (fmMemberMap.containsKey(userPerformance.getUserId())) {
            FmMemberDto fmMember = fmMemberMap.get(userPerformance.getUserId());
            userPerformance.setFmProjectId(fmMember.getProjectId());
            if (null != fmConfirmedStatus) {
                userPerformance.setFmConfirmedStatus(fmConfirmedStatus);
            }
            if (null != projectIds) {
                projectIds.add(fmMember.getProjectId());
            }
        }
    }

    public static List<FmGroupConfirmInfo> getFmGroups(Integer year, Integer month, PerformanceWorkGroupDto performanceWorkGroup, WorkGroupDto performanceRootGroup,
                                                       Set<Long> projectIds, List<FmMemberDto> allFmMembers, List<ProjectMaxMemberVo> projects, boolean confirmFlag) {
        FmUserRoleMapper fmUserRoleMapper = MyBeanUtils.getBean(FmUserRoleMapper.class);

        List<FmGroupConfirmInfo> fmGroups = new ArrayList<>();
        Long platId = null;
        Map<Long, Long> projectManagers;
        if (null != performanceWorkGroup.getProjectId()) {
            projectManagers = new HashMap<>();
            platId = performanceWorkGroup.getProjectId();
            List<FmUserRole> fmUserRoles = fmUserRoleMapper.selectByRoleId(FmRole.Role.projectFixSecondConfirmer);
            Map<Long, Long> fmUserRolesByProjectIdMap = fmUserRoles.stream().collect(Collectors.toMap(x -> x.getProjectId(), x -> x.getUserId()));
            for (Long projectId : projectIds) {
                projectManagers.put(projectId, fmUserRolesByProjectIdMap.get(projectId));
            }
        } else {
            // 获取所有下属
            List<PerformanceUserDto> allMembers = PerformanceTreeHelper.getAllMembersByRootWorkGroup(performanceRootGroup);
            Map<Long, PerformanceUserDto> allMemberMap = allMembers.stream().collect(Collectors.toMap(u -> u.getUserId(), u -> u));
            List<FmMemberDto> curFmMembers = allFmMembers.stream().filter(m -> allMemberMap.containsKey(m.getUserId())).collect(Collectors.toList());
            Map<Long, List<FmMemberDto>> platFmMembersMap = curFmMembers.stream().collect(Collectors.groupingBy(m -> m.getPlatId()));
            int maxMember = 0;
            for (Map.Entry<Long, List<FmMemberDto>> platEntry : platFmMembersMap.entrySet()) {
                if (platEntry.getValue().size() > maxMember) {
                    platId = platEntry.getKey();
                    maxMember = platEntry.getValue().size();
                }
            }
            List<FmUserRoleDto> allFmUserRoles = fmUserRoleMapper.selectAllByRoleIdAndPlatId(FmRole.Role.projectFixFirstConfirmer, platId);
            projectManagers = allFmUserRoles.stream().collect(Collectors.toMap(ur -> ur.getProjectId(), ur -> ur.getUserId()));
        }

        for (Long projectId : projectIds) {
            if (!projectManagers.containsKey(projectId)) {
                continue;
            }
            FmGroupConfirmInfo fmGroupConfirmInfo = new FmGroupConfirmInfo();
            fmGroupConfirmInfo.setYear(year);
            fmGroupConfirmInfo.setMonth(month);
            fmGroupConfirmInfo.setPerformanceWorkGroupId(performanceWorkGroup.getId());
            fmGroupConfirmInfo.setPlatId(platId);
            fmGroupConfirmInfo.setProjectId(projectId);
            fmGroupConfirmInfo.setPlatConfirmerId(performanceWorkGroup.getPerformanceManagerId());
            fmGroupConfirmInfo.setProjectConfirmerId(projectManagers.get(projectId));
            if (confirmFlag) {
                fmGroupConfirmInfo.setStatus(FmGroupConfirmInfo.Status.confirmed);
            } else {
                if (MyTokenUtils.BOSS_ID.equals(fmGroupConfirmInfo.getProjectConfirmerId())) {
                    fmGroupConfirmInfo.setStatus(FmGroupConfirmInfo.Status.confirmed);
                } else {
                    fmGroupConfirmInfo.setStatus(FmGroupConfirmInfo.Status.unconfirmed);
                }
            }
            fmGroups.add(fmGroupConfirmInfo);
        }
        return fmGroups;
    }

    public static List<PerformanceWorkGroupDto> getGroupsWthFullPathName(List<PerformanceWorkGroupDto> performanceWorkGroups, Map<Long, PerformanceWorkGroupDto> allGroupMap) {
        for (PerformanceWorkGroupDto performanceWorkGroup : performanceWorkGroups) {
            performanceWorkGroup.setFullPathName(getFullPathName(performanceWorkGroup, allGroupMap));
        }
        return performanceWorkGroups;
    }

    private static String getFullPathName(PerformanceWorkGroupDto performanceWorkGroup, Map<Long, PerformanceWorkGroupDto> allGroupMap) {
        String fullPathName = performanceWorkGroup.getName();
        if (null != performanceWorkGroup.getParent() && allGroupMap.containsKey(performanceWorkGroup.getParent())) {
            PerformanceWorkGroupDto parent = allGroupMap.get(performanceWorkGroup.getParent());
            fullPathName = getFullPathName(parent, allGroupMap) + "/" + fullPathName;
        }
        return fullPathName;
    }

    public static List<Long> getNeedInsertPerfWorkGoupIds(List<Long> workGroups, Map<Long, String> workGroupStatusMap) {
        List<Long> result = new ArrayList<>();
        for (Long workGroupId : workGroups) {
            String status = workGroupStatusMap.get(workGroupId);
            if (!UserPerformance.Status.complete.equals(status) && !UserPerformance.Status.submitted.equals(status)) {
                result.add(workGroupId);
            }
        }
        return result;
    }
}
