package com.seasun.management.service.performanceCore.userPerformance;

import com.seasun.management.dto.*;
import com.seasun.management.exception.ParamException;
import com.seasun.management.mapper.UserMapper;
import com.seasun.management.model.User;
import com.seasun.management.util.MyBeanUtils;
import com.seasun.management.util.MyListUtils;
import com.seasun.management.vo.WorkGroupCompVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PerformanceTreeHelper {
    private static final Logger logger = LoggerFactory.getLogger(PerformanceTreeHelper.class);

    public static WorkGroupDto getWorkGroupTree(Long performanceGroupId, List<WorkGroupDto> allPerformanceGroups, List<PerformanceUserDto> allUsers, Boolean all) {
        // 找出根目录，初始化树
        WorkGroupDto performanceRootGroup = null;
        for (WorkGroupDto performanceGroup : allPerformanceGroups) {
            if (performanceGroupId.equals(performanceGroup.getId())) {
                performanceRootGroup = performanceGroup;
                break;
            }
        }
        if (null == performanceRootGroup) {
            logger.error("当前组不存在,performanceGroupId:{}", performanceGroupId);
            throw new ParamException("当前组不存在");
        }

        initPerformanceGroupTreeByRootGroup(performanceRootGroup, allPerformanceGroups, allUsers, all);

        return performanceRootGroup;
    }

    //只初始化树结构，不包含下属成员
    public static WorkGroupDto getNoMemberWorkGroupTree(Long performanceGroupId, List<PerformanceWorkGroupDto> allPerformanceGrou, Boolean all) {
        // 找出根目录，初始化树
        WorkGroupDto performanceRootGroup = null;
        for (PerformanceWorkGroupDto performanceGroup : allPerformanceGrou) {
            if (performanceGroupId.equals(performanceGroup.getId())) {
                performanceRootGroup = performanceWorkGroupDtoToWorkGroupDto(performanceGroup);
                break;
            }
        }

        if (null == performanceRootGroup) {
            logger.error("当前组不存在,performanceGroupId:{}", performanceGroupId);
            throw new ParamException("当前组不存在");
        }

        Map<Long, List<PerformanceWorkGroupDto>> parentGroupMap = allPerformanceGrou.stream().filter(x -> null != x.getParent()).collect(Collectors.groupingBy(x -> x.getParent()));

        initNoMemberPerformanceGroupTreeByRootGroup(performanceRootGroup, performanceRootGroup.getLeaderId(), parentGroupMap, all);

        return performanceRootGroup;
    }

    public static WorkGroupDto performanceWorkGroupDtoToWorkGroupDto(PerformanceWorkGroupDto param) {
        WorkGroupDto result = new WorkGroupDto();
        result.setId(param.getId());
        result.setLeaderId(param.getPerformanceManagerId());
        result.setWorkGroupId(param.getWorkGroupId());
        result.setProjectConfirmFlag(param.getProjectConfirmFlag());
        result.setStrictType(param.getStrictType());
        result.setName(param.getName());
        result.setParent(param.getParent());
        if (param.getPerformanceManagerId() != null) {
            WorkGroupUserDto leader = new WorkGroupUserDto();
            leader.setUserId(param.getPerformanceManagerId());
            leader.setWorkGroupId(param.getWorkGroupId());
            leader.setName(param.getManagerName());
            leader.setLoginId(param.getManagerLoginId());
            result.setLeader(leader);
        }
        return result;
    }

    private static void initNoMemberPerformanceGroupTreeByRootGroup(WorkGroupDto performanceRootGroup, Long leaderId, Map<Long, List<PerformanceWorkGroupDto>> parentGroupMap, Boolean all) {

        performanceRootGroup.setChildWorkGroups(new ArrayList<>());
        if (parentGroupMap.containsKey(performanceRootGroup.getId())) {
            for (PerformanceWorkGroupDto performanceWorkGroupDto : parentGroupMap.get(performanceRootGroup.getId())) {
                if (!all && performanceWorkGroupDto.getPerformanceManagerId().equals(leaderId)) {
                    continue;
                }
                WorkGroupDto workGroupDto = performanceWorkGroupDtoToWorkGroupDto(performanceWorkGroupDto);
                if (parentGroupMap.containsKey(workGroupDto.getId())) {
                    initNoMemberPerformanceGroupTreeByRootGroup(workGroupDto, leaderId, parentGroupMap, all);
                }
                performanceRootGroup.getChildWorkGroups().add(workGroupDto);
            }
        }
    }

    public static List<WorkGroupCompVo.UserSimVo> getAllUsersByRootWorkGroupId(List<Long> perfWorkGroupIds, List<WorkGroupDto> performanceWorkGroups) {
        List<Long> subGroups = new ArrayList<>();
        subGroups.addAll(perfWorkGroupIds);
        for (Long perfWorkGroupId : perfWorkGroupIds) {
            fillSubGroups(subGroups, perfWorkGroupId, performanceWorkGroups);
        }

        // 去重（同时管理父子组的情况）
        subGroups = MyListUtils.removeDuplicate(subGroups);

        // 查询所有子组下的所有成员
        UserMapper userMapper = MyBeanUtils.getBean(UserMapper.class);
        List<WorkGroupCompVo.UserSimVo> result = userMapper.selectAllActiveUserInWorkGroup(User.GroupType.perfColumn, subGroups);
        return result;
    }

    private static void fillSubGroups(List<Long> result, Long perfWorkGroupId, List<WorkGroupDto> performanceWorkGroups) {
        for (WorkGroupDto temp : performanceWorkGroups) {
            if (perfWorkGroupId.equals(temp.getParent())) {
                result.add(temp.getId());
                fillSubGroups(result, temp.getId(), performanceWorkGroups);
            }
        }
    }

    private static void initPerformanceGroupTreeByRootGroup(WorkGroupDto performanceRootGroup, List<WorkGroupDto> allPerformanceWorkGroups, List<PerformanceUserDto> allUsers, Boolean all) {
        Map<Long, List<WorkGroupDto>> allPerformanceChildGroupsMap = allPerformanceWorkGroups.stream().filter(w -> null != w.getParent()).collect(Collectors.groupingBy(w -> w.getParent()));
        Map<Long, List<PerformanceUserDto>> workGroupUsersMap = allUsers.stream().filter(u -> null != u.getWorkGroupId()).collect(Collectors.groupingBy(u -> u.getWorkGroupId()));
        Map<Long, PerformanceUserDto> allUserMap = allUsers.stream().collect(Collectors.toMap(u -> u.getUserId(), u -> u));

        trackPerformanceGroupTreeForRootGroup(performanceRootGroup.getLeaderId(), all, performanceRootGroup, workGroupUsersMap, allUserMap, allPerformanceChildGroupsMap);
    }

    private static void trackPerformanceGroupTreeForRootGroup(Long managerId, Boolean all, WorkGroupDto performanceRootGroup, Map<Long, List<PerformanceUserDto>> workGroupUsersMap,
                                                              Map<Long, PerformanceUserDto> allUserMap, Map<Long, List<WorkGroupDto>> allPerformanceChildGroupsMap) {
        if (null != performanceRootGroup.getLeaderId() && allUserMap.containsKey(performanceRootGroup.getLeaderId())) {
         // todo 下面这个逻辑进不去
            if (!allUserMap.containsKey(performanceRootGroup.getLeaderId())) {
                logger.error("{}的绩效负责人配置异常", performanceRootGroup.getName());
                throw new ParamException(performanceRootGroup.getName() + "的绩效负责人配置异常");
            }
            performanceRootGroup.setLeader(allUserMap.get(performanceRootGroup.getLeaderId()));
        }

        // 1.设置子组
        if (allPerformanceChildGroupsMap.containsKey(performanceRootGroup.getId())) {
            List<WorkGroupDto> performanceChildGroups = allPerformanceChildGroupsMap.get(performanceRootGroup.getId());
            List<WorkGroupDto> childWorkGroups = new ArrayList<>();
            for (WorkGroupDto performanceChildGroup : performanceChildGroups) {
                if (!all && managerId.equals(performanceChildGroup.getLeaderId())) {
                    continue;
                }
                childWorkGroups.add(performanceChildGroup);
                trackPerformanceGroupTreeForRootGroup(managerId, all, performanceChildGroup, workGroupUsersMap, allUserMap, allPerformanceChildGroupsMap);
            }
            performanceRootGroup.setChildWorkGroups(childWorkGroups);
        } else {
            performanceRootGroup.setChildWorkGroups(new ArrayList<>());
        }

        // 2.设置成员
        if (workGroupUsersMap.containsKey(performanceRootGroup.getId())) {
            List<PerformanceUserDto> members = workGroupUsersMap.get(performanceRootGroup.getId());
            performanceRootGroup.setMembers(members);
        } else {
            performanceRootGroup.setMembers(new ArrayList());
        }
    }

    public static List<PerformanceUserDto> getAllMembersByRootWorkGroup(WorkGroupDto rootWorkGroup) {
        List<PerformanceUserDto> members = new ArrayList<>();
        // 添加所有子组下的所有成员
        addAllMembers(members, rootWorkGroup);
        return members;
    }

    private static void addAllMembers(List<PerformanceUserDto> result, WorkGroupDto rootWorkGroup) {
        if (null != rootWorkGroup.getMembers() && !rootWorkGroup.getMembers().isEmpty()) {
            result.addAll(rootWorkGroup.getMembers());
        }
        List<WorkGroupDto> childWorkGroups = rootWorkGroup.getChildWorkGroups();
        if (null == childWorkGroups || childWorkGroups.size() == 0) {
            return;
        }

        for (WorkGroupDto workGroupDto : childWorkGroups) {
            // 递归
            addAllMembers(result, workGroupDto);
        }
    }

    public static void getAllSubPerfWorkGroupIdByRootTree(List<Long> result, WorkGroupDto rootWorkGroup) {
        List<WorkGroupDto> childWorkGroups = rootWorkGroup.getChildWorkGroups();
        if (childWorkGroups != null && rootWorkGroup.getChildWorkGroups().size() > 0) {
            for (WorkGroupDto childWorkGroup : childWorkGroups) {
                result.add(childWorkGroup.getId());
                if (childWorkGroup.getChildWorkGroups() != null && childWorkGroup.getChildWorkGroups().size() > 0) {
                    getAllSubPerfWorkGroupIdByRootTree(result, childWorkGroup);
                }
            }
        }
    }

}
