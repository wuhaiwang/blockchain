package com.seasun.management.service.performanceCore.userSalaryChange;

import com.seasun.management.dto.WorkGroupDto;
import com.seasun.management.dto.WorkGroupUserDto;
import com.seasun.management.model.UserSalaryChange;
import com.seasun.management.service.performanceCore.groupTrack.GroupTrackService;
import com.seasun.management.util.MyBeanUtils;
import com.seasun.management.vo.OrdinateSalaryChangeAppVo;
import com.seasun.management.vo.SubordinateSalaryChangeAppVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class UserSalaryChangeHelper {

    private static final Logger logger = LoggerFactory.getLogger(UserSalaryChangeHelper.class);

    /**
     * 对成员分组排序，按照可调薪--待淘汰--已完成分块，名称排序
     *
     * @param list 要排序的vo 的list
     * @return 排序之后的集合
     */
    static List<OrdinateSalaryChangeAppVo> performanceResultListClassifySort(List<OrdinateSalaryChangeAppVo> list) {

        if (list == null || list.size() == 0) {
            return null;
        }
        List<OrdinateSalaryChangeAppVo> resultList = new ArrayList<>();
        List<OrdinateSalaryChangeAppVo> goodList = new LinkedList<>();
        List<OrdinateSalaryChangeAppVo> badList = new LinkedList<>();
        List<OrdinateSalaryChangeAppVo> normalList = new LinkedList<>();
        for (OrdinateSalaryChangeAppVo vo : list) {
            if (UserSalaryChange.PerformanceResult.good.equals(vo.getPerformanceResult())) {
                goodList.add(vo);
            } else if (UserSalaryChange.PerformanceResult.bad.equals(vo.getPerformanceResult())) {
                badList.add(vo);
            } else {
                normalList.add(vo);
            }
        }

        goodList.sort(Comparator.comparing(u -> u.getName()));
        badList.sort(Comparator.comparing(u -> u.getName()));
        normalList.sort(Comparator.comparing(u -> u.getName()));

        resultList.addAll(goodList);
        resultList.addAll(badList);
        resultList.addAll(normalList);
        return resultList;
    }

    /**
     * 根据三个月的绩效得分计算 performanceResult
     *
     * @param vo 传入的对象
     * @return 完成计算的对象
     */
    static OrdinateSalaryChangeAppVo performanceResultFilled(OrdinateSalaryChangeAppVo vo) {
        String evaluateType = vo.getEvaluateType();
        Integer score = vo.getScore();
        Integer performanceCount = vo.getPerformanceCount();
        if (performanceCount == null || score == null)
            return vo;

        //根据分数规则计算 performanceResult
        if ((UserSalaryChange.EvaluateType.low.equals(evaluateType) && (score >= 8)) || (!(UserSalaryChange.EvaluateType.high).equals(evaluateType) && (score >= 10))) {
            vo.setPerformanceResult(UserSalaryChange.PerformanceResult.good);
        } else if (performanceCount == 3 && score < 4) {
            vo.setPerformanceResult(UserSalaryChange.PerformanceResult.bad);
        } else {
            vo.setPerformanceResult(UserSalaryChange.PerformanceResult.normal);
        }
        return vo;
    }

    /**
     * 批量计算performanceResult 的方法
     *
     * @param ordinateSalaryChangeAppVoList
     * @return
     */
    static List<OrdinateSalaryChangeAppVo> performanceResultFilled(List<OrdinateSalaryChangeAppVo> ordinateSalaryChangeAppVoList) {
        if (ordinateSalaryChangeAppVoList == null || ordinateSalaryChangeAppVoList.size() == 0)
            return ordinateSalaryChangeAppVoList;
        for (OrdinateSalaryChangeAppVo vo : ordinateSalaryChangeAppVoList) {
            performanceResultFilled(vo);
        }
        return ordinateSalaryChangeAppVoList;
    }

    /**
     * 根据直接下属调薪列表和下属团队的调薪列表，计算出总的Profile:可调薪人数、待淘汰人数等
     *
     * @param ordinateSalaryChangeAppVoList 直接下属的调薪列表
     * @param subGroup                      下属团队的调薪列表
     * @return profile
     */
    public static SubordinateSalaryChangeAppVo.Profile getProfile(List<OrdinateSalaryChangeAppVo> ordinateSalaryChangeAppVoList, SubordinateSalaryChangeAppVo.SubGroup subGroup) {
        SubordinateSalaryChangeAppVo.Profile profile = new SubordinateSalaryChangeAppVo.Profile();
        Integer total = ordinateSalaryChangeAppVoList.size() + subGroup.getMemberCount();
        Integer salaryIncreaseNumber = 0;
        Long salaryIncreaseAmount = 0L;
        Integer waitingForCutNumber = 0;

        for (int i = 0; i < ordinateSalaryChangeAppVoList.size(); i++) {
            OrdinateSalaryChangeAppVo dto = ordinateSalaryChangeAppVoList.get(i);
            if (UserSalaryChange.PerformanceResult.good.equals(dto.getPerformanceResult())) {
                if (dto.getIncreaseSalary() != null) {
                    salaryIncreaseAmount += dto.getIncreaseSalary();
                }
                salaryIncreaseNumber++;
            } else if (UserSalaryChange.PerformanceResult.bad.equals(dto.getPerformanceResult())) {
                waitingForCutNumber++;
            }
        }

        List<SubordinateSalaryChangeAppVo.SubGroup.GroupSalarySituation> subData = subGroup.getData();
        for (int i = 0; i < subData.size(); i++) {
            SubordinateSalaryChangeAppVo.SubGroup.GroupSalarySituation group = subData.get(i);
            if (group.getSalaryIncreaseNumber() != null) {
                salaryIncreaseNumber += group.getSalaryIncreaseNumber();
            }
            if (group.getSalaryIncreaseAmount() != null) {
                salaryIncreaseAmount += group.getSalaryIncreaseAmount();
            }
            if (group.getWaitingForCutNumber() != null) {
                waitingForCutNumber += group.getWaitingForCutNumber();
            }
        }
        profile.setTotal(total);
        profile.setSalaryIncreaseAmount(salaryIncreaseAmount);
        profile.setWaitingForCutNumber(waitingForCutNumber);
        profile.setSalaryIncreaseNumber(salaryIncreaseNumber);
        return profile;
    }

    static void getWorkGroupIdAndSubGroupId(List<OrdinateSalaryChangeAppVo> result, WorkGroupDto workGroupDto) {
        Map<Long, Long> userGroupMap = userGroupMapGenerate(workGroupDto);//用户和所属绩效组对应map
        Map<Long, List<Long>> userSubGroupMap = new HashMap<>();//用户和下属组对应map
        getUserSubGroupMap(workGroupDto, userSubGroupMap);
        for (OrdinateSalaryChangeAppVo user : result) {
            user.setWorkGroupId(userGroupMap.get(user.getUserId()));
            List<Long> subGroupIds = userSubGroupMap.get(user.getUserId());
            user.setSubGroup(getSubGroupString(subGroupIds));
        }
    }

    //生成 Key：userId,Value:workGroupId 的 map
    private static Map<Long, Long> userGroupMapGenerate(WorkGroupDto workGroupDto) {
        GroupTrackService groupTrackService = MyBeanUtils.getBean(GroupTrackService.class);
        Map<Long, Long> resultMap = new HashMap<>();

        //如果组为调薪节点
        if (workGroupDto.getLeaderId() != null) {
            //是调薪节点的将其所有成员Id以及workGroupId 插入UserId-GroupId Map中
            List<WorkGroupUserDto> userExceptLeaders = groupTrackService.getDirectMember(workGroupDto);

            for (WorkGroupUserDto user : userExceptLeaders) {
                resultMap.put(user.getUserId(), workGroupDto.getId());
            }
        }

        //继续递归子组
        List<WorkGroupDto> workGroupDtoList = workGroupDto.getChildWorkGroups();
        if (workGroupDtoList == null || workGroupDtoList.size() == 0) {
            return resultMap;
        }

        for (WorkGroupDto workGroup : workGroupDtoList) {
            resultMap.putAll(userGroupMapGenerate(workGroup));
        }
        return resultMap;

    }

    //转换subGroupIds 的格式：List to String 方便存储
    private static String getSubGroupString(List<Long> workGroupList) {
        StringBuffer subGroupString = new StringBuffer();
        if (workGroupList == null || workGroupList.size() == 0)
            return subGroupString.toString();

        //将list<Long> 转换为字符串，用逗号相隔
        for (int i = 0; i < workGroupList.size(); i++) {
            Long workGroupDto = workGroupList.get(i);
            if (i == workGroupList.size() - 1) {
                subGroupString.append(workGroupDto);
            } else {
                subGroupString.append(workGroupDto + ",");
            }
        }
        return subGroupString.toString();
    }

    //生成 Key：userId,Value:subGroupIds 集合的 map
    private static Map<Long, List<Long>> getUserSubGroupMap(WorkGroupDto workGroupDto, Map<Long, List<Long>> userSubGroupMap) {

        if (workGroupDto.getLeaderId() != null) {//这个组存在Leader
            List<Long> subGroupIds = userSubGroupMap.get(workGroupDto.getLeaderId());//取出subGroupId的List
            if (subGroupIds != null && subGroupIds.size() != 0) {//list不为空,将此组id加入进list
                subGroupIds.add(workGroupDto.getId());
            } else {//new list，插入本组的id
                List<Long> subGroupIdList = new ArrayList<>();
                subGroupIdList.add(workGroupDto.getId());
                userSubGroupMap.put(workGroupDto.getLeaderId(), subGroupIdList);
            }
        }
        //继续对子组递归
        List<WorkGroupDto> workGroups = workGroupDto.getChildWorkGroups();
        if (workGroups == null || workGroups.size() == 0) {
            return userSubGroupMap;
        }
        for (WorkGroupDto workGroup : workGroups) {
            getUserSubGroupMap(workGroup, userSubGroupMap);
        }
        return userSubGroupMap;
    }

    //将小组的状态改成规定模式，1.补齐开头年的季度 2.目前最近季度为进行中，其他未完成季度为延误中
    static List<SubordinateSalaryChangeAppVo.GroupStatus> fixAllGroupStatus(List<SubordinateSalaryChangeAppVo.GroupStatus> list) {

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int currentQuarter = (int) Math.ceil((Calendar.getInstance().get(Calendar.MONTH) + 1) / 3f);

        List<SubordinateSalaryChangeAppVo.GroupStatus> groupStatusList = new ArrayList<>();
        int beginYear = list.get(0).getYear();
        int beginQuarter = list.get(0).getQuarter();
        //补齐开头年的季度
        for (int i = 1; i < beginQuarter; i++) {
            groupStatusList.add(new SubordinateSalaryChangeAppVo.GroupStatus(beginYear, i, null));
        }

        for (SubordinateSalaryChangeAppVo.GroupStatus groupStatus : list) {

            if (groupStatus.getStatus().equals(SubordinateSalaryChangeAppVo.GroupStatus.Status.waitingForCommit)) {//只有待提交状态才会出现延误中状态
                if (groupStatus.getYear() == currentYear && groupStatus.getQuarter() == currentQuarter) {
                    groupStatusList.add(new SubordinateSalaryChangeAppVo.GroupStatus(groupStatus.getYear(), groupStatus.getQuarter(), SubordinateSalaryChangeAppVo.GroupStatus.StatusVo.underway));
                    break;
                }
                groupStatusList.add(new SubordinateSalaryChangeAppVo.GroupStatus(groupStatus.getYear(), groupStatus.getQuarter(), SubordinateSalaryChangeAppVo.GroupStatus.StatusVo.delayed));
            } else {//小组已完成/已提交 保持不变
                groupStatusList.add(new SubordinateSalaryChangeAppVo.GroupStatus(groupStatus.getYear(), groupStatus.getQuarter(), groupStatus.getStatus()));
            }

        }

        return groupStatusList;
    }

}
