package com.seasun.management.service.impl;

import com.seasun.management.constant.BaseConstant;
import com.seasun.management.dto.*;
import com.seasun.management.exception.ParamException;
import com.seasun.management.exception.UserInvalidOperateException;
import com.seasun.management.helper.ReportHelper;
import com.seasun.management.helper.UserMessageHelper;
import com.seasun.management.helper.UserPerformanceHelper;
import com.seasun.management.mapper.*;
import com.seasun.management.model.*;
import com.seasun.management.service.FixMemberService;
import com.seasun.management.service.OperateLogService;
import com.seasun.management.service.UserMessageService;
import com.seasun.management.service.UserPerformanceService;
import com.seasun.management.service.performanceCore.historyTrack.PerformanceHistoryTrackService;
import com.seasun.management.service.performanceCore.userPerformance.PerformanceDataHelper;
import com.seasun.management.service.performanceCore.userPerformance.PerformanceService;
import com.seasun.management.service.performanceCore.userPerformance.PerformanceTreeHelper;
import com.seasun.management.util.MySystemParamUtils;
import com.seasun.management.util.MyTokenUtils;
import com.seasun.management.vo.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FixMemberServiceImpl implements FixMemberService {

    @Autowired
    PerformanceWorkGroupMapper performanceWorkGroupMapper;

    @Autowired
    FmPerfSubmitInfoMapper perfSubmitInfoMapper;

    @Autowired
    UserPerformanceMapper userPerformanceMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    FmMemberMapper fmMemberMapper;

    @Autowired
    ProjectMapper projectMapper;

    @Autowired
    FmUserRoleMapper fmUserRoleMapper;

    @Resource(name = "current")
    PerformanceService performanceCurrentService;

    @Resource(name = "history")
    PerformanceService performanceHistoryService;

    @Autowired
    UserMessageService userMessageService;

    @Autowired
    OperateLogService operateLogService;

    @Autowired
    private UserPerformanceService userPerformanceService;

    @Autowired
    PerformanceHistoryTrackService historyTrackService;

    @Autowired
    private PerfWorkGroupStatusMapper perfWorkGroupStatusMapper;

    @Autowired
    FmGroupConfirmInfoMapper fmGroupConfirmInfoMapper;


    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(FixMemberServiceImpl.class);

    @Override
    public List<FmGroupConfirmInfoVo> getFmGroupConfirmInfoListByWorkGroupManager(Integer year, Integer month) {
        Long userId = MyTokenUtils.getCurrentUserId();
        List<PerformanceWorkGroup> managerGroups = performanceWorkGroupMapper.selectAllByManagerId(userId);
        List<FmGroupConfirmInfoVo> allFmGroupConfirmInfoList = fmGroupConfirmInfoMapper.selectAllByYearMonth(year, month);

        Map<Long, PerformanceWorkGroup> fmGroupMap = managerGroups.stream().filter(g -> g.getProjectConfirmFlag()).collect(Collectors.toMap(p -> p.getId(), p -> p));
        List<FmGroupConfirmInfoVo> managerFmGroupConfirmInfoList = allFmGroupConfirmInfoList.stream().filter(i -> fmGroupMap.containsKey(i.getPerformanceWorkGroupId())).collect(Collectors.toList());

        return managerFmGroupConfirmInfoList;
    }

    @Override
    public SubFixMemberPerformanceVo getSubFixMemberPerformance(Integer year, Integer month) {
        Calendar now = ReportHelper.getDateOnly();
        int nowYear = now.get(Calendar.YEAR);
        int nowMonth = now.get(Calendar.MONTH) + 1;


        if (year == null || month == null) {
            year = nowYear;
            month = nowMonth;
        }

        Long userId = MyTokenUtils.getCurrentUserId();
        SubFixMemberPerformanceVo subFixMemberPerformanceVo;
        if (perfWorkGroupStatusMapper.selectCountByManagerAndLockedOrComplete(userId, year, month) == 0) {
            subFixMemberPerformanceVo = performanceCurrentService.getSubFixMemberPerformance(userId, year, month);
        } else {
            subFixMemberPerformanceVo = performanceHistoryService.getSubFixMemberPerformance(userId, year, month);
        }
        return subFixMemberPerformanceVo;
    }

    @Override
    public ProjectFixMemberInfoVo getFmGroupConfirmInfoListByProjectConfirmer(Integer year, Integer month) {
        Long userId = MyTokenUtils.getCurrentUserId();
        Boolean defaultFlag = false;
        if (null == year && null == month) {
            Calendar now = ReportHelper.getDateOnly();
            year = now.get(Calendar.YEAR);
            month = now.get(Calendar.MONTH) + 1;
            defaultFlag = true;
        }

        // add missed code
        ProjectFixMemberInfoVo.History history = PerformanceDataHelper.getProjectFixMemberHistory(userId, year, month, defaultFlag);
        year = history.getYear();
        month = history.getMonth();

        ProjectFixMemberInfoVo projectFixMemberInfoVo;
        if (userPerformanceMapper.selectCountByComplete(year, month) == 0) {
            projectFixMemberInfoVo = performanceCurrentService.getFmGroupConfirmInfoListByProjectConfirmer(userId, year, month, history);
        } else {
            projectFixMemberInfoVo = performanceHistoryService.getFmGroupConfirmInfoListByProjectConfirmer(userId, year, month, history);
        }
        return projectFixMemberInfoVo;
    }

    @Override
    public FmGroupConfirmInfoVo getFmGroupConfirmInfo(FmGroupConfirmInfoVo fmGroupConfirmInfoParam) {
        FmGroupConfirmInfoVo fmGroupConfirmInfo;
        if (null == fmGroupConfirmInfoParam.getId()) {
            fmGroupConfirmInfo = performanceCurrentService.getFmGroupConfirmInfo(fmGroupConfirmInfoParam);
        } else {
            fmGroupConfirmInfo = fmGroupConfirmInfoMapper.selectById(fmGroupConfirmInfoParam.getId());
            fmGroupConfirmInfo = performanceHistoryService.getFmGroupConfirmInfo(fmGroupConfirmInfo);
        }
        return fmGroupConfirmInfo;
    }

    private void checkCurrentMonthSubmitSupport(Integer year, Integer month) {
        Calendar now = Calendar.getInstance();
        Integer nowYear = now.get(Calendar.YEAR);
        Integer nowMonth = now.get(Calendar.MONTH) + 1;
        Integer nowDay = now.get(Calendar.DATE);
        if (nowYear.equals(year) && nowMonth.equals(month)) {
            Integer startDay = MySystemParamUtils.getSystemConfigWithDefaultValue(MySystemParamUtils.Key.PerformanceStartDay, MySystemParamUtils.DefaultValue.PerformanceStartDay);
            if (nowDay < startDay) {
                logger.info("current performance is not support submit");
                throw new ParamException("当月绩效不允许在" + startDay + "号前提交");
            }
        }
        Date startDate = MySystemParamUtils.getSystemConfigWithDefaultValue(MySystemParamUtils.Key.PerformanceSubmitStartDate, MySystemParamUtils.DefaultValue.PerformanceSubmitStartDate);
        if (now.getTime().before(startDate)) {
            DateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
            logger.info("current performance is not support submit");
            throw new ParamException("绩效不允许在" + format.format(startDate) + "前提交");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitFmGroup(Integer year, Integer month) {
        checkCurrentMonthSubmitSupport(year, month);

        // todo 跟下面db连接重复
        User logonUser = MyTokenUtils.getCurrentUser();
        List<Long> workGroupIds = performanceWorkGroupMapper.selectWorkGroupIdByUserId(logonUser.getId());
        checkFmPerfSubmit(year, month, workGroupIds);

        User user = MyTokenUtils.getCurrentUser();

        // todo: project-work-group-id-bug-tag 因为只有美术中心和运营中心，故先不处理。
        List<PerformanceWorkGroupDto> allGroups = performanceWorkGroupMapper.selectAllWithProjectIdByActive();
        Map<Long, PerformanceWorkGroupDto> groupByIdMap = allGroups.stream().collect(Collectors.toMap(x -> x.getId(), x -> x));
        List<PerformanceWorkGroupDto> managerGroups = allGroups.stream().filter(g -> user.getId().equals(g.getPerformanceManagerId())).collect(Collectors.toList());
        List<UserPerformanceDto> allUserPerformances = userPerformanceMapper.selectAllByYearAndMonth(year, month);
        List<PerformanceUserDto> allUsers = userMapper.selectAllEntityWithPerformanceByPerformanceWorkGroup();
        List<WorkGroupDto> allPerformanceGroups = performanceWorkGroupMapper.selectAllByActive();
        List<FmMemberDto> allFmMembers = fmMemberMapper.selectAll();
        List<ProjectMaxMemberVo> projects = projectMapper.selectAllWithMaxMember();
        Map<Long, ProjectMaxMemberVo> projectByIdMap = projects.stream().collect(Collectors.toMap(x -> x.getId(), x -> x));
        List<PerfWorkGroupStatus> perfWorkGroupStatuses = perfWorkGroupStatusMapper.selectByYearAndMonth(year, month);

        // 把记录放到map里方便查找
        Map<Long, UserPerformanceDto> userPerformanceMap = allUserPerformances.stream().collect(Collectors.toMap(p -> p.getUserId(), p -> p));
        Map<Long, String> performanceWorkGroupStatusMap = PerformanceDataHelper.getPerformanceWorkGroupStatusMap(allGroups, perfWorkGroupStatuses);
        Map<Long, FmMemberDto> fmMemberMap = allFmMembers.stream().collect((Collectors.toMap(m -> m.getUserId(), m -> m)));
        Map<Long, PerformanceUserDto> userDtoByIdMap = allUsers.stream().collect(Collectors.toMap(x -> x.getUserId(), x -> x));
        // 重设员工绩效组
        PerformanceDataHelper.resetUserPerformanceWorkGroup(allUsers, userPerformanceMap);

        // 需要与项目确认固化组
        List<FmGroupConfirmInfo> fmGroups = new ArrayList<>();
        Set<Long> projectIds = new HashSet<>();

        // 待数据库处理记录
        List<UserPerformanceDto> waittingForUpdateRecords = new ArrayList<>();
        List<UserPerformanceDto> waittingFroInsertRecords = new ArrayList<>();
        List<UserPerformanceDto> validRecords = new ArrayList<>(); //有效记录，计算比例用
        Integer strictType = PerformanceWorkGroup.GroupStrictType.normalNum;

        // 查找管理组的strictType list
        List<Integer> groupStrictTypes = new ArrayList<>();
        List<Long> needInsertWorkGroupIds = new ArrayList<>();

        for (PerformanceWorkGroupDto performanceWorkGroup : managerGroups) {
            String fmConfirmedStatus = UserPerformance.FmConfirmedStatus.firstUnconfirmed;

            // 重要！！！ 若用户管理的绩效组中，有项目级别的，则状态设置为等待二次确认。
            // todo: project-work-group-id-bug-tag 所有参与固化绩效的平台，work_group_id都要配置正确！！！（因为该逻辑目前有问题，performanceWorkGroup中的work_group_id和project的work_group_id已经无法对应）
            // todo: project-work-group-id-bug-tag 因为只有美术中心和运营中心，故暂时先不处理。
            if (null != performanceWorkGroup.getProjectId()) {
                fmConfirmedStatus = UserPerformance.FmConfirmedStatus.secondUnconfirmed;
            }

            // 找出根目录，初始化树
            WorkGroupDto performanceRootGroup = PerformanceTreeHelper.getWorkGroupTree(performanceWorkGroup.getId(), allPerformanceGroups, allUsers, false);

            // 本组负责人提交逻辑
            // 获取直接下属
            List<PerformanceUserDto> directMembers = performanceRootGroup.getMembers();
            // 获取下属团队
            List<WorkGroupDto> directGroups = performanceRootGroup.getChildWorkGroups();

            // 找出未提交和已提交的子团队
            List<WorkGroupDto> unSubmittedWorkGroups = new ArrayList<>();
//            List<WorkGroupDto> submittedWorkGroups = new ArrayList<>();
            for (WorkGroupDto workGroupDto : directGroups) {
                needInsertWorkGroupIds.add(workGroupDto.getId());
                PerformanceTreeHelper.getAllSubPerfWorkGroupIdByRootTree(needInsertWorkGroupIds, workGroupDto);
                String status = performanceWorkGroupStatusMap.get(workGroupDto.getId());
                if (SubPerformanceAppVo.HistoryInfo.Status.processing.equals(status)) {
                    unSubmittedWorkGroups.add(workGroupDto);
                }
//                else {
//                    submittedWorkGroups.add(workGroupDto);
//                }
            }

            // 处理直接下属绩效
            for (PerformanceUserDto member : directMembers) {
                if (userPerformanceMap.containsKey(member.getUserId())) {
                    UserPerformanceDto userPerformance = userPerformanceMap.get(member.getUserId());

                    // 只处理固化成员
                    if (performanceWorkGroup.getProjectConfirmFlag() && fmMemberMap.containsKey(member.getUserId())) {
                        if (UserPerformance.Status.unsubmitted.equals(userPerformance.getStatus()) || UserPerformance.Status.submitted.equals(userPerformance.getStatus())) {
                            // 未提交，已提交的记录变为作废，放入待更新列表
                            userPerformance.setFinalPerformance(UserPerformance.Performance.invalided);
                            userPerformance.setStatus(UserPerformance.Status.assessed);
                        }
                        PerformanceDataHelper.setUserPerformanceFmGroupInfo(userPerformance, fmMemberMap, fmConfirmedStatus, projectIds);
                        waittingForUpdateRecords.add(userPerformance);
                    }

                    validRecords.add(userPerformance);
                } else {
                    // 只处理固化成员
                    if (performanceWorkGroup.getProjectConfirmFlag() && fmMemberMap.containsKey(member.getUserId())) {
                        // 空记录变为作废，放入待新建列表
                        UserPerformanceDto userPerformance = PerformanceDataHelper.getNewInvalidedUserPerformance(member, year, month, UserPerformance.Status.assessed);

                        PerformanceDataHelper.setUserPerformanceFmGroupInfo(userPerformance, fmMemberMap, fmConfirmedStatus, projectIds);
                        waittingFroInsertRecords.add(userPerformance);
                    }
                }
            }
            // 处理未提交子团队绩效
            for (WorkGroupDto workGroupDto : unSubmittedWorkGroups) {
                List<PerformanceUserDto> allSubGroupMembers = PerformanceTreeHelper.getAllMembersByRootWorkGroup(workGroupDto);
                for (PerformanceUserDto member : allSubGroupMembers) {
                    if (userPerformanceMap.containsKey(member.getUserId())) {
                        // 作废已有记录，放入待更新列表
                        UserPerformanceDto userPerformance = userPerformanceMap.get(member.getUserId());
                        if (UserPerformance.Status.unsubmitted.equals(userPerformance.getStatus()) || UserPerformance.Status.submitted.equals(userPerformance.getStatus())) {
                            userPerformance.setFinalPerformance(UserPerformance.Performance.invalided);
                        }
                        userPerformance.setStatus(UserPerformance.Status.locked);
                        if (performanceWorkGroup.getProjectConfirmFlag()) {
                            PerformanceDataHelper.setUserPerformanceFmGroupInfo(userPerformance, fmMemberMap, fmConfirmedStatus, projectIds);
                        }
                        waittingForUpdateRecords.add(userPerformance);
                    } else {
                        // 空记录变为作废，放入待新建列表，如果当前员工所在绩效组已提交则不做处理
                        String status = PerformanceDataHelper.getPerformanceWorkGroupStatus(member.getWorkGroupId(), performanceWorkGroupStatusMap);
                        if (status.equals(SubPerformanceAppVo.HistoryInfo.Status.processing)) {
                            UserPerformanceDto userPerformance = PerformanceDataHelper.getNewInvalidedUserPerformance(member, year, month, UserPerformance.Status.locked);
                            if (performanceWorkGroup.getProjectConfirmFlag()) {
                                PerformanceDataHelper.setUserPerformanceFmGroupInfo(userPerformance, fmMemberMap, fmConfirmedStatus, projectIds);
                            }
                            waittingFroInsertRecords.add(userPerformance);
                        }
                    }
                }
            }
            // 不处理已提交团队，放入有效记录
            for (WorkGroupDto workGroupDto : directGroups) {
                List<UserPerformanceDto> allSubWorkGroupUserPerformances = PerformanceDataHelper.getSubUserPerformances(workGroupDto.getId(), allUserPerformances);
                if (performanceWorkGroup.getProjectConfirmFlag()) {
                    for (UserPerformanceDto userPerformance : allSubWorkGroupUserPerformances) {
                        PerformanceDataHelper.setUserPerformanceFmGroupInfo(userPerformance, fmMemberMap, fmConfirmedStatus, projectIds);
                        waittingForUpdateRecords.add(userPerformance);
                    }
                }

                validRecords.addAll(allSubWorkGroupUserPerformances);
            }


            // 固化组
            if (performanceWorkGroup.getProjectConfirmFlag()) {
                List<FmGroupConfirmInfo> curFmGroups = PerformanceDataHelper.getFmGroups(year, month, performanceWorkGroup, performanceRootGroup, projectIds, allFmMembers, projects, false);
                if (!curFmGroups.isEmpty()) {
                    fmGroups.addAll(curFmGroups);
                }
            }

            // 去除没有项目固化审核员的记录
            if (UserPerformance.FmConfirmedStatus.firstUnconfirmed.equals(fmConfirmedStatus)) {
                List<FmUserRole> allFmProjectConfirmer = fmUserRoleMapper.selectAllRecordsByRoleIdAndPlatId(FmRole.Role.projectFixFirstConfirmer, fmGroups.get(0).getPlatId());
                fmGroups = fmGroups.stream().filter(g -> {
                    return allFmProjectConfirmer.stream().anyMatch(a -> a.getProjectId().equals(g.getProjectId()));
                }).collect(Collectors.toList());
            } else {
                List<FmUserRole> allFmProjectConfirmer = fmUserRoleMapper.selectByRoleId(FmRole.Role.projectFixSecondConfirmer);
                fmGroups = fmGroups.stream().filter(g -> {
                    return allFmProjectConfirmer.stream().anyMatch(a -> a.getProjectId().equals(g.getProjectId()));
                }).collect(Collectors.toList());
            }
            groupStrictTypes.add(performanceRootGroup.getStrictType());
        }

        if (fmGroups.isEmpty()) {
            logger.info("FmGroupConfirmInfo is empty");
            throw new ParamException("数据异常，没有固化组");
        }

        strictType = UserPerformanceHelper.getStrictType(groupStrictTypes);

        // 检查比例，非严格组不检查比例
        // 老k不检查比例：检查温和比例 和 严格比例
        if (strictType.equals(PerformanceWorkGroup.GroupStrictType.normalNum)) {
            PerformanceWorkGroupInfoAppVo.PerformancePro performancePro = PerformanceDataHelper.getPerformancePro(validRecords);
            float total = performancePro.getSCount() + performancePro.getACount() + performancePro.getBCount() + performancePro.getCCount();
            if (total == 0.0 || validRecords.size() > 0 && (performancePro.getS() > 20 || (performancePro.getSa()) > 50 || performancePro.getCCount() < Math.floor(total * 0.1))) {
                logger.info("performance proportion error");
                throw new ParamException("S比例不能超过20%，S+A比例不能超过50%，C比例不能少于10%");
            }
        } else if (strictType.equals(PerformanceWorkGroup.GroupStrictType.strictNum)) {
            PerformanceWorkGroupInfoAppVo.PerformancePro performancePro = PerformanceDataHelper.getPerformancePro(validRecords);
            if (validRecords.size() > 0 && (performancePro.getS() > 20 || (performancePro.getSa()) > 50 || performancePro.getC() < 10)) {
                logger.info("performance proportion error");
                throw new ParamException("S比例不能超过20%，S+A比例不能超过50%，C比例不能少于10%");
            }
        }

        // 批量更新和批量插入记录
        if (!waittingFroInsertRecords.isEmpty()) {
            userPerformanceMapper.batchInsertByLeaderSubmit(waittingFroInsertRecords);
        }
        if (!waittingForUpdateRecords.isEmpty()) {
            userPerformanceMapper.batchUpdateStatusByPks(waittingForUpdateRecords);
        }

        // 固化组记录
        fmGroupConfirmInfoMapper.batchInsertByPlatConfirmerSubmit(fmGroups);

        //保存提交绩效组
        userPerformanceService.insertPerfWorkGroupStatus(needInsertWorkGroupIds, performanceWorkGroupStatusMap, year, month, user);

        // 消息通知
        List<UserMessage> userMessages = new ArrayList<>();
        Map<Long, List<FmGroupConfirmInfo>> projectConfirmerFmGroupsMap = fmGroups.stream().filter(i -> FmGroupConfirmInfo.Status.unconfirmed.equals(i.getStatus())).collect(Collectors.groupingBy(i -> i.getProjectConfirmerId()));
        Map<Long, List<FmGroupConfirmInfo>> confirmerFmGroupsByPWGIdMap = fmGroups.stream().filter(i -> FmGroupConfirmInfo.Status.unconfirmed.equals(i.getStatus())).collect(Collectors.groupingBy(i -> i.getPerformanceWorkGroupId()));
        String content = user.getName() + "发起了" + year + "年" + month + "月的固化成员绩效确认";
        for (Long projectConfirmerId : projectConfirmerFmGroupsMap.keySet()) {
            UserMessage userMessage = UserMessageHelper.getSubmitFixMemberUserMessage(projectConfirmerId, content, year, month);
            userMessages.add(userMessage);
        }
        // rtx消息通知
        for (Map.Entry<Long, List<FmGroupConfirmInfo>> entry : confirmerFmGroupsByPWGIdMap.entrySet()) {
            FmGroupConfirmInfo item = entry.getValue().get(0);
            String message = user.getName() + "(" + projectByIdMap.get(item.getPlatId()).getName() + "-" + groupByIdMap.get(item.getPerformanceWorkGroupId()).getName() + ")向您发起了" + year + "年" + month + "月的固化成员绩效确认，请尽快完成审核。";
            List<String> receivers = new ArrayList<>();
            for (FmGroupConfirmInfo fmGroupConfirmInfo : entry.getValue()) {
                receivers.add(userDtoByIdMap.get(fmGroupConfirmInfo.getProjectConfirmerId()).getLoginId());
            }
            userMessageService.sendSeasunMessages(BaseConstant.APP_NAME, message, BaseConstant.SendMessageType.rtx, receivers);

        }

        userMessageService.batchAdd(userMessages);

        // 操作记录
        operateLogService.add(OperateLog.Type.group_performance_submit, String.format("%s 发起了与项目确认固化成员的绩效", user.getName()), user.getId());
    }

    @Transactional
    @Override
    public void confirmFmGroup(Long id) {
        User user = MyTokenUtils.getCurrentUser();
        FmGroupConfirmInfoVo fmGroupConfirmInfo = fmGroupConfirmInfoMapper.selectById(id);
        if (null == fmGroupConfirmInfo) {
            throw new ParamException("固化组信息不存在");
        }

        Long receiverId;
        // plat_confirmer_id project_confirmer_id更新成最新
        FmGroupConfirmInfo fmGroupConfirmInfoCon = new FmGroupConfirmInfo();
        fmGroupConfirmInfoCon.setId(fmGroupConfirmInfo.getId());
        PerformanceWorkGroup performanceWorkGroup = performanceWorkGroupMapper.selectByPrimaryKey(fmGroupConfirmInfo.getPerformanceWorkGroupId());
        if (null != performanceWorkGroup) {
            fmGroupConfirmInfoCon.setPlatConfirmerId(performanceWorkGroup.getPerformanceManagerId());
            receiverId = fmGroupConfirmInfoCon.getPlatConfirmerId();
        } else {
            receiverId = fmGroupConfirmInfo.getPlatConfirmerId();
        }
        fmGroupConfirmInfoCon.setProjectConfirmerId(user.getId());
        fmGroupConfirmInfoCon.setStatus(FmGroupConfirmInfo.Status.confirmed);

        fmGroupConfirmInfoMapper.updateByPrimaryKeySelective(fmGroupConfirmInfoCon);

        // 处理固化成员绩效
        int year = fmGroupConfirmInfo.getYear();
        int month = fmGroupConfirmInfo.getMonth();
        List<Long> managerGroupId = new ArrayList<>();
        managerGroupId.add(fmGroupConfirmInfo.getPerformanceWorkGroupId());
        List<UserPerformanceDto> allMonthRecords = userPerformanceMapper.selectAllByYearAndMonth(year, month);
        List<UserPerformanceDto> userPerformances = historyTrackService.getAllHistoryMembersByWorkGroupIdAndTime(managerGroupId, year, month, allMonthRecords);
        userPerformances = userPerformances.stream().filter(p -> fmGroupConfirmInfo.getProjectId().equals(p.getFmProjectId())).collect(Collectors.toList());
        for (UserPerformanceDto userPerformance : userPerformances) {
            switch (userPerformance.getFmConfirmedStatus()) {
                case UserPerformance.FmConfirmedStatus.firstUnconfirmed:
                    userPerformance.setFmConfirmedStatus(UserPerformance.FmConfirmedStatus.firstConfirmed);
                    break;
                case UserPerformance.FmConfirmedStatus.secondUnconfirmed:
                    userPerformance.setFmConfirmedStatus(UserPerformance.FmConfirmedStatus.secondConfirmed);
                    break;
                default:
                    break;
            }
        }
        userPerformanceMapper.batchUpdateStatusByPks(userPerformances);

        // 消息通知
        String content = user.getName() + "确认了" + fmGroupConfirmInfo.getProjectName() + "-" + fmGroupConfirmInfo.getPlatName() + "-" + fmGroupConfirmInfo.getPerformanceWorkGroupName() + "的绩效";
        UserMessage userMessage = UserMessageHelper.getConfirmFixMemberUserMessage(receiverId, fmGroupConfirmInfo, content);
        userMessageService.add(userMessage);

        // rtx消息通知
        User receiver = userMapper.selectByPrimaryKey(receiverId);
        List<FmGroupConfirmInfoVo> fmGroupConfirmInfoVos = fmGroupConfirmInfoMapper.selectAllByPerformanceWorkGroupIdAndYearMonth(fmGroupConfirmInfo.getPerformanceWorkGroupId(), year, month);
        boolean allConfirmFlag = true;
        for (FmGroupConfirmInfoVo fmGroupConfirmInfoVo : fmGroupConfirmInfoVos) {
            if (!FmGroupConfirmInfo.Status.confirmed.equals(fmGroupConfirmInfoVo.getStatus())) {
                allConfirmFlag = false;
                break;
            }
        }
        if (allConfirmFlag) {
            userMessageService.sendSeasunMessage(BaseConstant.APP_NAME, "您管理的绩效组（" + fmGroupConfirmInfo.getPlatName() + "-" + fmGroupConfirmInfo.getPerformanceWorkGroupName() + "），固化绩效已全部审核通过，请尽快提交本组绩效。", BaseConstant.SendMessageType.rtx, receiver.getLoginId());
        } else {
            userMessageService.sendSeasunMessage(BaseConstant.APP_NAME, user.getName() +"("+fmGroupConfirmInfo.getProjectName() +"项目固化审核员)通过了您的固化绩效审核申请。", BaseConstant.SendMessageType.rtx, receiver.getLoginId());
        }

        // 操作记录
        operateLogService.add(OperateLog.Type.group_performance_submit, content, user.getId());
    }

    @Transactional
    @Override
    public void confirmAllFmGroup(Integer year, Integer month, Long projectId) {
        User user = MyTokenUtils.getCurrentUser();

        List<IdNameBaseObject> projects = fmUserRoleMapper.selectUserFixSecondProjectsByUserId(user.getId());
        List<FmGroupConfirmInfoVo> fmGroupConfirmInfoList;
        if (!projects.isEmpty()) {
            fmGroupConfirmInfoList = fmGroupConfirmInfoMapper.selectFixSencondConfirmInfoByProjectConfirmerAndYearAndMonth(user.getId(), year, month);
        } else {
            fmGroupConfirmInfoList = fmGroupConfirmInfoMapper.selectAllByProjectConfirmerAndYearMonth(user.getId(), year, month);
        }
        List<UserPerformanceDto> allMonthRecords = userPerformanceMapper.selectAllByYearAndMonth(year, month);

        List<FmGroupConfirmInfo> fmGroupConfirmInfoCons = new ArrayList<>();
        List<UserMessage> userMessages = new ArrayList<>();
        List<UserPerformanceDto> userPerformancesForUpdate = new ArrayList<>();
        Iterator<FmGroupConfirmInfoVo> fmGroupConfirmInfoIterator = fmGroupConfirmInfoList.iterator();
        while (fmGroupConfirmInfoIterator.hasNext()) {
            FmGroupConfirmInfoVo fmGroupConfirmInfo = fmGroupConfirmInfoIterator.next();
            if (FmGroupConfirmInfo.Status.confirmed.equals(fmGroupConfirmInfo.getStatus())) {
                fmGroupConfirmInfoIterator.remove();
                continue;
            }
            if (!projectId.equals(fmGroupConfirmInfo.getProjectId())) {
                fmGroupConfirmInfoIterator.remove();
                continue;
            }

            Long receiverId;
            // plat_confirmer_id project_confirmer_id更新成最新
            FmGroupConfirmInfo fmGroupConfirmInfoCon = new FmGroupConfirmInfo();
            fmGroupConfirmInfoCon.setId(fmGroupConfirmInfo.getId());
            // todo : 循环里面不建议写sql连接
            PerformanceWorkGroup performanceWorkGroup = performanceWorkGroupMapper.selectByPrimaryKey(fmGroupConfirmInfo.getPerformanceWorkGroupId());
            if (null != performanceWorkGroup) {
                fmGroupConfirmInfoCon.setPlatConfirmerId(performanceWorkGroup.getPerformanceManagerId());
                fmGroupConfirmInfo.setPlatConfirmerId(performanceWorkGroup.getPerformanceManagerId());
                receiverId = fmGroupConfirmInfoCon.getPlatConfirmerId();
            } else {
                receiverId = fmGroupConfirmInfo.getPlatConfirmerId();
            }
            fmGroupConfirmInfoCon.setProjectConfirmerId(user.getId());
            fmGroupConfirmInfoCon.setStatus(FmGroupConfirmInfo.Status.confirmed);
            fmGroupConfirmInfoCons.add(fmGroupConfirmInfoCon);

            // 处理固化成员绩效
            List<Long> managerGroupId = new ArrayList<>();
            managerGroupId.add(fmGroupConfirmInfo.getPerformanceWorkGroupId());
            List<UserPerformanceDto> userPerformances = historyTrackService.getAllHistoryMembersByWorkGroupIdAndTime(managerGroupId, year, month, allMonthRecords);
            userPerformances = userPerformances.stream().filter(p -> fmGroupConfirmInfo.getProjectId().equals(p.getFmProjectId())).collect(Collectors.toList());
            for (UserPerformanceDto userPerformance : userPerformances) {
                switch (userPerformance.getFmConfirmedStatus()) {
                    case UserPerformance.FmConfirmedStatus.firstUnconfirmed:
                        userPerformance.setFmConfirmedStatus(UserPerformance.FmConfirmedStatus.firstConfirmed);
                        break;
                    case UserPerformance.FmConfirmedStatus.secondUnconfirmed:
                        userPerformance.setFmConfirmedStatus(UserPerformance.FmConfirmedStatus.secondConfirmed);
                        break;
                    default:
                        break;
                }
            }
            userPerformancesForUpdate.addAll(userPerformances);

            // 消息通知
            String content = user.getName() + "确认了" + fmGroupConfirmInfo.getProjectName() + "-" + fmGroupConfirmInfo.getPlatName() + "-" + fmGroupConfirmInfo.getPerformanceWorkGroupName() + "的绩效";
            UserMessage userMessage = UserMessageHelper.getConfirmFixMemberUserMessage(receiverId, fmGroupConfirmInfo, content);
            userMessages.add(userMessage);
        }
        if (!fmGroupConfirmInfoCons.isEmpty()) {
            fmGroupConfirmInfoMapper.batchUpdateConfirmerAndStatusByPks(fmGroupConfirmInfoCons);
            // rtx消息通知
            Set<Long> fmGroupPlatConfirmerIdSet = fmGroupConfirmInfoCons.stream().filter(x -> null != x.getPlatConfirmerId()).map(x -> x.getPlatConfirmerId()).collect(Collectors.toSet());
            List<FmGroupConfirmInfoVo> fmGroupConfirmInfoVos = fmGroupConfirmInfoMapper.selectAllByYearMonth(year, month);
            Map<Long, List<FmGroupConfirmInfoVo>> fmGroupConfirmInfoVosByPerfWrokGroupIdMap = fmGroupConfirmInfoVos.stream().collect(Collectors.groupingBy(x -> x.getPerformanceWorkGroupId()));
            List<SimUserCoupleInfoVo> users = userMapper.selectSimUserCoupleInfoVoBYUserIds(new ArrayList<>(fmGroupPlatConfirmerIdSet));
            Map<Long, String> userLoginIdByIdMap = users.stream().collect(Collectors.toMap(x -> x.getUserId(), x -> x.getLoginId()));
            List<String> recevers = new ArrayList<>();
            String projectName=fmGroupConfirmInfoList.get(0).getProjectName();
            for (FmGroupConfirmInfoVo vo : fmGroupConfirmInfoList) {
                String loginId = userLoginIdByIdMap.get(vo.getPlatConfirmerId());
                boolean allconfirmFlag = true;
                for (FmGroupConfirmInfoVo item : fmGroupConfirmInfoVosByPerfWrokGroupIdMap.get(vo.getPerformanceWorkGroupId())) {
                    if (!FmGroupConfirmInfo.Status.confirmed.equals(item.getStatus())) {
                        allconfirmFlag = false;
                        break;
                    }
                }
                if (allconfirmFlag) {
                    userMessageService.sendSeasunMessage(BaseConstant.APP_NAME, "您管理的绩效组（" + vo.getPlatName() + "-" + vo.getPerformanceWorkGroupName() + "），固化绩效已全部审核通过，请尽快提交本组绩效。", BaseConstant.SendMessageType.rtx, loginId);
                } else {
                    recevers.add(loginId);
                }
            }
            if(!recevers.isEmpty()){
                userMessageService.sendSeasunMessages(BaseConstant.APP_NAME, user.getName() +"("+projectName+ "项目固化审核员)通过了您的固化绩效审核申请。", BaseConstant.SendMessageType.rtx, recevers);
            }
        }
        if (!userPerformancesForUpdate.isEmpty()) {
            userPerformanceMapper.batchUpdateStatusByPks(userPerformancesForUpdate);
        }
        if (!userMessages.isEmpty()) {
            userMessageService.batchAdd(userMessages);
        }

        if (!fmGroupConfirmInfoCons.isEmpty() || !userPerformancesForUpdate.isEmpty() || !userMessages.isEmpty()) {
            // 操作记录
            operateLogService.add(OperateLog.Type.group_performance_submit, String.format("%s 执行了项目固化成员绩效全部确认操作", user.getName()), user.getId());
        }
    }


    private void checkFmPerfSubmit(int year, int month, List<Long> workGroupIds) {
        if (workGroupIds == null || workGroupIds.size() == 0) {
            return;
        }
        List<Long> perfUserIds = userMapper.selectActiveUserIdByPerfWorkGroup(workGroupIds);

        if (perfUserIds == null || perfUserIds.size() == 0) {
            return;
        }
        List<Long> platIds = fmMemberMapper.selectPlatIdsByUserId(perfUserIds);

        if (platIds == null || platIds.size() == 0) {
            return;
        }

        String plats = MySystemParamUtils.getSystemConfigWithDefaultValue(MySystemParamUtils.Key.FmPerfSubmitBarrierPlats, MySystemParamUtils.DefaultValue.FmPerfSubmitBarrierPlats);
        List<FmPerfSubmitInfo> fmPerfSubmitInfo = perfSubmitInfoMapper.selectByPlatIds(platIds, year, month);

        // 有记录情况
        if (fmPerfSubmitInfo != null && fmPerfSubmitInfo.size() != 0) {
            for (FmPerfSubmitInfo fm : fmPerfSubmitInfo) {
                if (!fm.getPerfSubmitFlag()) {
                    throw new UserInvalidOperateException("固化配置负责人还未打开提交通道");
                }
            }
        } else {
            for (Long plat : platIds) {
                if (plats.contains(String.valueOf(plat))) {
                    throw new UserInvalidOperateException("固化配置负责人还未打开提交通道");
                }
            }
        }

    }
}