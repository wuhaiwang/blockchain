package com.seasun.management.service.performanceCore.userSalaryChange;

import com.seasun.management.dto.WorkGroupDto;
import com.seasun.management.mapper.PerformanceWorkGroupMapper;
import com.seasun.management.mapper.UserSalaryChangeMapper;
import com.seasun.management.model.UserSalaryChange;
import com.seasun.management.service.performanceCore.groupTrack.GroupTrackService;
import com.seasun.management.service.performanceCore.historyTrack.HistoryTrackService;
import com.seasun.management.service.performanceCore.userPerformance.PerformanceDataHelper;
import com.seasun.management.vo.OrdinateSalaryChangeAppVo;
import com.seasun.management.vo.SubPerformanceAppVo;
import com.seasun.management.vo.SubordinateSalaryChangeAppVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

import static com.seasun.management.service.performanceCore.userSalaryChange.UserSalaryChangeHelper.*;
import static com.seasun.management.service.performanceCore.userSalaryChange.UserSalaryChangeHelper.performanceResultListClassifySort;

@Repository
public class UserSalaryChangeDao {

    private static final Logger logger = LoggerFactory.getLogger(UserSalaryChangeDao.class);

    @Autowired
    private GroupTrackService groupTrackService;

    @Autowired
    private UserSalaryChangeMapper userSalaryChangeMapper;

    @Autowired
    private HistoryTrackService<OrdinateSalaryChangeAppVo> salaryHistoryTrackService;

    @Autowired
    PerformanceWorkGroupMapper performanceWorkGroupMapper;


    public List<OrdinateSalaryChangeAppVo> getHistoryDirectOrdinate(Long workGroupId, Integer year, Integer quarter) {
        List<OrdinateSalaryChangeAppVo> voList = userSalaryChangeMapper.getHistorySubLeader(workGroupId, year, quarter);
        performanceResultFilled(voList);
        return voList;
    }

    /**
     * 查询出对应的workGroupId 和subGroup信息，加入后返回
     *
     * @param children:         几个子组的id
     * @param salarySituations: 几个子组的详情，但是缺少GroupName 和Manager
     * @return 将GroupName 和Manger 的信息填充后返回
     */
    public List<SubordinateSalaryChangeAppVo.SubGroup.GroupSalarySituation> matchSubGroupSimpleInfo(List<Long> children, List<SubordinateSalaryChangeAppVo.SubGroup.GroupSalarySituation> salarySituations) {
        if (children == null || children.size() == 0 || salarySituations == null || salarySituations.size() == 0) {
            return salarySituations;
        }
        List<OrdinateSalaryChangeAppVo> groupSimpleInfoList = getGroupSimpleInfoList(children);
        Map<Long, Integer> groupIdAndPositionMap = new HashMap<>();
        for (int i = 0; i < groupSimpleInfoList.size(); i++) {
            groupIdAndPositionMap.put(groupSimpleInfoList.get(i).getId(), i);
        }
        for (int i = 0; i < salarySituations.size(); i++) {
            SubordinateSalaryChangeAppVo.SubGroup.GroupSalarySituation salarySituation = salarySituations.get(i);
            Long workGroupId = salarySituation.getGroupId();
            Integer position = groupIdAndPositionMap.get(workGroupId);
            if (position != null) {
                salarySituation.setGroupName(groupSimpleInfoList.get(position).getWorkGroup());
                salarySituation.setManager(groupSimpleInfoList.get(position).getManager());
            }
        }
        return salarySituations;
    }

    /**
     * 根据历史时间下直接下属的记录，得到调薪子团队的数据
     *
     * @param ordinateSalaryChangeVo 历史情况下直接下属的记录
     * @param year                   历史的年份
     * @param quarter                历史的季度
     * @param isWeb                  是否web端请求，如果为web端的请求，还要将下属团队中的所有人都返回
     * @return SubGroup：子团队模块所有数据
     */
    public SubordinateSalaryChangeAppVo.SubGroup getHistorySubGroup(List<OrdinateSalaryChangeAppVo> ordinateSalaryChangeVo, Integer year, Integer quarter, Boolean isWeb) {
        SubordinateSalaryChangeAppVo.SubGroup subGroup = new SubordinateSalaryChangeAppVo.SubGroup();
        Integer memberCount = 0;
        List<Long> allSubGroupId = new ArrayList<>();
        List<SubordinateSalaryChangeAppVo.SubGroup.GroupSalarySituation> data = new ArrayList<>();
        for (OrdinateSalaryChangeAppVo vo : ordinateSalaryChangeVo) {
            //每一个直接下属包含的子组
            String subGroupString = vo.getSubGroup();
            if (subGroupString != null && !("").equals(subGroupString)) {

                //如果直接下属有子组，查询出子组
                String[] tempSubGroupIds = subGroupString.split(",");
                for (String subGroupId : tempSubGroupIds) {

                    //记录每一个子组的salaryIncreaseNumber，salaryIncreaseAmount，waitingForCutNumber
                    int groupTotal = 0;
                    long subGroupIdLong = Long.parseLong(subGroupId.trim());
                    allSubGroupId.add(subGroupIdLong);
                    SubordinateSalaryChangeAppVo.SubGroup.GroupSalarySituation groupSalarySituation = new SubordinateSalaryChangeAppVo.SubGroup.GroupSalarySituation();
                    Integer salaryIncreaseNumber = 0;
                    Long salaryIncreaseAmount = 0L;
                    Integer waitingForCutNumber = 0;

                    //查询出子团队的所有员工，计算员工人数、待淘汰、可调薪、调薪值等调薪信息
                    List<OrdinateSalaryChangeAppVo> ordinateSalaryChangeList = salaryHistoryTrackService.getAllHistoryMembersByWorkGroupIdAndTime(subGroupIdLong, year, 0, quarter);
                    performanceResultFilled(ordinateSalaryChangeList);
                    for (OrdinateSalaryChangeAppVo salaryChange : ordinateSalaryChangeList) {
                        if (UserSalaryChange.PerformanceResult.good.equals(salaryChange.getPerformanceResult())) {
                            if (salaryChange.getIncreaseSalary() != null && salaryChange.getIncreaseSalary() > 0) {
                                salaryIncreaseAmount += salaryChange.getIncreaseSalary();
                            }
                            salaryIncreaseNumber++;
                        } else if (UserSalaryChange.PerformanceResult.bad.equals(salaryChange.getPerformanceResult())) {
                            waitingForCutNumber++;
                        }
                        memberCount++;
                        groupTotal++;
                    }
                    if (isWeb) groupSalarySituation.setSubSalaryChangeList(ordinateSalaryChangeList);
                    groupSalarySituation.setGroupId(subGroupIdLong);
                    groupSalarySituation.setTotal(groupTotal);
                    groupSalarySituation.setSalaryIncreaseNumber(salaryIncreaseNumber);
                    groupSalarySituation.setSalaryIncreaseAmount(salaryIncreaseAmount);
                    groupSalarySituation.setWaitingForCutNumber(waitingForCutNumber);
                    groupSalarySituation.setStatus(SubordinateSalaryChangeAppVo.GroupStatus.StatusVo.finished);
                    data.add(groupSalarySituation);
                }
            }
        }
        matchSubGroupSimpleInfo(allSubGroupId, data);
        subGroup.setTotal(data.size());
        subGroup.setMemberCount(memberCount);
        subGroup.setData(data);
        return subGroup;
    }

    public List<OrdinateSalaryChangeAppVo> getGroupSimpleInfoList(List<Long> list) {
        return userSalaryChangeMapper.getGroupSimpleInfoList(list);
    }

    //get all group user's status from origin
    public List<SubordinateSalaryChangeAppVo.GroupStatus> getAllGroupStatus(Long workGroupId) {

        List<SubordinateSalaryChangeAppVo.GroupStatus> groupStatusList = new ArrayList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int currentQuarter = (int) Math.ceil((Calendar.getInstance().get(Calendar.MONTH) + 1) / 3f);
        int year = 0;
        int quarter = 0;
        int performanceBeginYear = 0;
        int performanceBeginQuarter = 0;

        //本组绩效的组状态
        Long perfWorkGroupId = performanceWorkGroupMapper.selectByRoot().getId();
        List<SubPerformanceAppVo.HistoryInfo> perfHistory = PerformanceDataHelper.getWorkGroupHistory(null, perfWorkGroupId);
        for (SubPerformanceAppVo.HistoryInfo info : perfHistory) {//查询绩效开始的起始时间
            if (info.getStatus() != null) {
                performanceBeginYear = info.getYear();
                performanceBeginQuarter = (int) Math.ceil(info.getMonth() / 3f);
                break;
            }
        }

        //数据库中关于本组成员的所有调薪记录，人员状态可能为：（待提交，待确认，已完成）或者直接没有这个组成员的调薪记录
        List<UserSalaryChange> userSalaryChangeList = userSalaryChangeMapper.getAllExistByWorkGroupId(workGroupId);
        Quarter performanceQuarter = new Quarter(performanceBeginYear, performanceBeginQuarter);

        logger.info("performance info given begin performance quarter: year" + performanceQuarter.year + ";quarter" + performanceQuarter.quarter);

        if (userSalaryChangeList != null && userSalaryChangeList.size() != 0) {//数据库中有这个组成员的调薪记录

            Quarter salaryFirstQuarter = new Quarter(userSalaryChangeList.get(0).getYear(), userSalaryChangeList.get(0).getQuarter());
            Quarter salaryLastQuarter = new Quarter(userSalaryChangeList.get(userSalaryChangeList.size() - 1).getYear(), userSalaryChangeList.get(userSalaryChangeList.size() - 1).getQuarter());

            logger.info("data base begin quarter: year:" + salaryFirstQuarter.year + ";quarter:" + salaryFirstQuarter.quarter);
            logger.info("data base end quarter: year:" + salaryLastQuarter.year + ";quarter:" + salaryLastQuarter.quarter);

            while (performanceQuarter.isEarly(salaryFirstQuarter)) {//将绩效有，调薪没有的季度加入进去
                groupStatusList.add(new SubordinateSalaryChangeAppVo.GroupStatus(performanceQuarter.year, performanceQuarter.quarter, SubordinateSalaryChangeAppVo.GroupStatus.Status.waitingForCommit));
                performanceQuarter = performanceQuarter.getNextQuarter();
            }

            // 将数据库中成员的调薪记录转化为本组的调薪记录状态：
            // 组员已完成状态转为小组“已完成”
            // 组员待确认状态转为小组“已提交”
            // 组员待提交转为小组“待提交”
            int index = 0;
            logger.info((performanceQuarter.isEarly(salaryLastQuarter) + "||" + performanceQuarter.isEqual(salaryLastQuarter) + "||" + (index <= userSalaryChangeList.size() - 1)));
            while ((performanceQuarter.isEarly(salaryLastQuarter) || performanceQuarter.isEqual(salaryLastQuarter)) && (index <= userSalaryChangeList.size() - 1)) {
                UserSalaryChange userSalaryChange = userSalaryChangeList.get(index);
                String groupStatus;
                if (performanceQuarter.equal(userSalaryChange.getYear(), userSalaryChange.getQuarter())) {// 数据库刚好有这个季度这个小组人员的记录
                    if (UserSalaryChange.Status.waitingForCommit.equals(userSalaryChange.getStatus())) {// 小组人员待提交 转为小组状态“待提交”
                        groupStatus = SubordinateSalaryChangeAppVo.GroupStatus.Status.waitingForCommit;
                    } else if (UserSalaryChange.Status.waitingForConfirm.equals(userSalaryChange.getStatus())) {//小组人员状态为 待确认的改为小组状态“已提交”
                        groupStatus = SubordinateSalaryChangeAppVo.GroupStatus.Status.submitted;
                    } else {
                        groupStatus = SubordinateSalaryChangeAppVo.GroupStatus.Status.finished;
                    }
                    index++;
                } else {//数据库里没有这个季度小组成员的调薪记录，但这个季度又在有调戏记录的季度区间中
                    groupStatus = SubordinateSalaryChangeAppVo.GroupStatus.Status.waitingForCommit;
                }
                groupStatusList.add(new SubordinateSalaryChangeAppVo.GroupStatus(performanceQuarter.year, performanceQuarter.quarter, groupStatus));
                performanceQuarter = performanceQuarter.getNextQuarter();
            }
            year = groupStatusList.get(groupStatusList.size() - 1).getYear();
            quarter = groupStatusList.get(groupStatusList.size() - 1).getQuarter();
        } else {

            //数据库中没有本组人员的调薪记录
            year = performanceBeginYear;
            quarter = performanceBeginQuarter;
            groupStatusList.add(new SubordinateSalaryChangeAppVo.GroupStatus(year, quarter, SubordinateSalaryChangeAppVo.GroupStatus.Status.waitingForCommit));
        }
        logger.info("after this stamp ,all status is waiting for commit year:" + year + ";quarter" + quarter);

        //从开始到现在都 设置为 waitingForCommit
        Quarter tempQ = new Quarter(year, quarter);
        Quarter currentQ = new Quarter(currentYear, currentQuarter);
        while (tempQ.isEarly(currentQ)) {
            tempQ = tempQ.getNextQuarter();
            groupStatusList.add(new SubordinateSalaryChangeAppVo.GroupStatus(tempQ.year, tempQ.quarter, SubordinateSalaryChangeAppVo.GroupStatus.Status.waitingForCommit));
        }
        return fixAllGroupStatus(groupStatusList);
    }

    /**
     * 根据传入的直接下属,计算查询出主要信息
     *
     * @param directList
     * @param result
     * @param year
     * @param quarter
     * @param isWeb
     * @return
     */
    public SubordinateSalaryChangeAppVo getRealTimeSalaryChangeList(List<OrdinateSalaryChangeAppVo> directList, SubordinateSalaryChangeAppVo result,
                                                                    List<WorkGroupDto> allGroups, int year, int quarter, boolean isWeb) {
        List<WorkGroupDto> subWorkGroupDtoList = groupTrackService.getDirectGroup(directList, allGroups);
        SubordinateSalaryChangeAppVo.SubGroup subGroup = getSubGroup(subWorkGroupDtoList, year, quarter, isWeb);
        SubordinateSalaryChangeAppVo.Profile profile = getProfile(directList, subGroup);
        result.setProfile(profile);
        result.setSubGroup(subGroup);
        result.setLeaderSalaryChangeList(performanceResultListClassifySort(directList));
        return result;
    }

    public SubordinateSalaryChangeAppVo.SubGroup getSubGroup(List<WorkGroupDto> workGroupDtoList, int year, int quarter, boolean isWeb) {

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int currentQuarter = (int) Math.ceil((Calendar.getInstance().get(Calendar.MONTH) + 1) / 3f);

        SubordinateSalaryChangeAppVo.SubGroup subGroup = new SubordinateSalaryChangeAppVo.SubGroup();
        List<SubordinateSalaryChangeAppVo.SubGroup.GroupSalarySituation> data = new ArrayList<>();
        int total = workGroupDtoList.size();
        int member = 0;
        List<Long> subGroupIds = new ArrayList<>();

        //遍历每一个下属组，计算需要信息
        for (int i = 0; i < workGroupDtoList.size(); i++) {
            int groupTotal = 0;
            WorkGroupDto workGroup = workGroupDtoList.get(i);
            SubordinateSalaryChangeAppVo.SubGroup.GroupSalarySituation groupSalarySituation = new SubordinateSalaryChangeAppVo.SubGroup.GroupSalarySituation();
            int salaryIncreaseNumber = 0;
            long salaryIncreaseAmount = 0;
            int waitingForCutNumber = 0;
            subGroupIds.add(workGroup.getId());

            //查找出直接下属，根据直接下属才能得出，本组的提交状态
            List<OrdinateSalaryChangeAppVo> secondSubDirect = groupTrackService.getDirectMember(workGroup);

            //组名和负责人可以直接得到
            String manager = workGroup.getLeader().getName();
            String groupName = workGroup.getName();

            //没有直接下属
            if (secondSubDirect == null || secondSubDirect.size() == 0) {
                String status = SubordinateSalaryChangeAppVo.GroupStatus.Status.finished;
                groupSalarySituation.setManager(manager);
                groupSalarySituation.setStatus(status);
                groupSalarySituation.setGroupName(groupName);
                groupSalarySituation.setTotal(groupTotal);
                groupSalarySituation.setGroupId(workGroup.getId());
                data.add(groupSalarySituation);
            } else {//直接下属存在

                OrdinateSalaryChangeAppVo vo = secondSubDirect.get(0);

                //根据任意一个直接下属判断大组的状态
                String status;
                if (vo.getStatus() == null || (vo.getStatus() != null && vo.getStatus().equals(UserSalaryChange.Status.waitingForCommit))) {
                    if (year == currentYear && quarter == currentQuarter) {
                        status = SubordinateSalaryChangeAppVo.GroupStatus.StatusVo.underway;
                    } else {
                        status = SubordinateSalaryChangeAppVo.GroupStatus.StatusVo.delayed;
                    }
                } else if (UserSalaryChange.Status.waitingForConfirm.equals(vo.getStatus())) {
                    status = SubordinateSalaryChangeAppVo.GroupStatus.StatusVo.submitted;
                } else {
                    status = SubordinateSalaryChangeAppVo.GroupStatus.StatusVo.finished;
                }

                List<OrdinateSalaryChangeAppVo> workGroupUsers = groupTrackService.getAllMembersByRootWorkGroup(workGroup);
                for (int j = 0; j < workGroupUsers.size(); j++) {
                    OrdinateSalaryChangeAppVo salaryUser = workGroupUsers.get(j);
                    Integer salaryIncreaseAmountTemp = salaryUser.getIncreaseSalary();
                    if (salaryIncreaseAmountTemp != null && salaryIncreaseAmountTemp > 0) {
                        salaryIncreaseAmount += salaryIncreaseAmountTemp;
                    }
                    if (UserSalaryChange.PerformanceResult.good.equals(salaryUser.getPerformanceResult())) {
                        salaryIncreaseNumber++;
                    }
                    if (UserSalaryChange.PerformanceResult.bad.equals(salaryUser.getPerformanceResult())) {
                        waitingForCutNumber++;
                    }
                    groupTotal++;
                    member++;
                }
                if (isWeb) {
                    groupSalarySituation.setSubSalaryChangeList(performanceResultListClassifySort(workGroupUsers));
                }
                groupSalarySituation.setSalaryIncreaseNumber(salaryIncreaseNumber);
                groupSalarySituation.setSalaryIncreaseAmount(salaryIncreaseAmount);
                groupSalarySituation.setWaitingForCutNumber(waitingForCutNumber);
                groupSalarySituation.setManager(manager);
                groupSalarySituation.setStatus(status);
                groupSalarySituation.setTotal(groupTotal);
                groupSalarySituation.setGroupName(groupName);
                groupSalarySituation.setGroupId(workGroup.getId());
                data.add(groupSalarySituation);
            }

            data.sort((x, y) -> {
                String delayed = SubordinateSalaryChangeAppVo.GroupStatus.StatusVo.delayed;
                String underway = SubordinateSalaryChangeAppVo.GroupStatus.StatusVo.underway;
                String submitted = SubordinateSalaryChangeAppVo.GroupStatus.StatusVo.submitted;

                if (((x.getStatus().equals(delayed) || x.getStatus().equals(underway)) && (y.getStatus().equals(submitted)))//未提交（延误中/进行中）排在已提交前
                        || (x.getStatus().equals(delayed) && y.getStatus().equals(underway)))//延误中排在进行中前
                    return -1;
                else return 0;//其他情况不需要排序
            });
        }
        matchSubGroupSimpleInfo(subGroupIds, data);
        subGroup.setData(data);
        subGroup.setMemberCount(member);
        subGroup.setTotal(total);
        return subGroup;
    }

}
