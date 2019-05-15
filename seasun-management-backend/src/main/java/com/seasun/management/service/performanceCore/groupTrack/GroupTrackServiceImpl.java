package com.seasun.management.service.performanceCore.groupTrack;

import com.seasun.management.constant.PerformanceServiceType;
import com.seasun.management.dto.WorkGroupDto;
import com.seasun.management.dto.WorkGroupUserDto;
import com.seasun.management.exception.ParamException;
import com.seasun.management.mapper.RUserWorkGroupPermMapper;
import com.seasun.management.mapper.UserMapper;
import com.seasun.management.mapper.UserPerformanceMapper;
import com.seasun.management.mapper.WorkGroupMapper;
import com.seasun.management.model.WorkGroupRole;
import com.seasun.management.service.WorkGroupService;
import com.seasun.management.util.MyListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GroupTrackServiceImpl<U extends WorkGroupUserDto> implements GroupTrackService<U> {

    private static final Logger logger = LoggerFactory.getLogger(GroupTrackServiceImpl.class);

    @Autowired
    WorkGroupService workGroupService;

    @Autowired
    WorkGroupMapper workGroupMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    RUserWorkGroupPermMapper workGroupPermMapper;

    @Autowired
    UserPerformanceMapper userPerformanceMapper;

    /**
     * 预置条件：
     * 先从 allGroups 中 遍历出 rootGroup。
     * List<WorkGroupDto> allGroups = workGroupMapper.selectAllWithLeaderByGroupRoleId(work_group_role_id);// 注意不同的模块，work_group_role_id不一样
     */
    @Override
    public void initHrGroupTreeByRootGroup(WorkGroupDto rootGroup, List<WorkGroupDto> allGroups, List<U> allUsers) {
        Map<Long, List<WorkGroupDto>> allChildGroupsMap = new HashMap<>();
        Map<Long, List<U>> workGroupUsersMap = new HashMap<>();
        Map<Long, U> allUserMap = new HashMap<>();
        for (WorkGroupDto group : allGroups) {
            if (allChildGroupsMap.containsKey(group.getParent())) {
                allChildGroupsMap.get(group.getParent()).add(group);
            } else {
                List<WorkGroupDto> childGroups = new ArrayList<>();
                childGroups.add(group);
                allChildGroupsMap.put(group.getParent(), childGroups);
            }
        }
        for (U user : allUsers) {
            if (workGroupUsersMap.containsKey(user.getWorkGroupId())) {
                workGroupUsersMap.get(user.getWorkGroupId()).add(user);
            } else {
                List<U> workGroupUsers = new ArrayList<>();
                workGroupUsers.add(user);
                workGroupUsersMap.put(user.getWorkGroupId(), workGroupUsers);
            }
            allUserMap.put(user.getUserId(), user);
        }

        trackHrGroupTreeForRootGroup(rootGroup, allChildGroupsMap, workGroupUsersMap, allUserMap);
    }


    private void trackHrGroupTreeForRootGroup(WorkGroupDto rootGroup, Map<Long, List<WorkGroupDto>> allChildGroupsMap, Map<Long, List<U>> workGroupUsersMap, Map<Long, U> allUserMap) {
        // 组下所有下属人数
        int total = 0;

        // 设置负责人
        if (null != rootGroup.getLeaderId() && allUserMap.containsKey(rootGroup.getLeaderId())) {
            rootGroup.setLeader(allUserMap.get(rootGroup.getLeaderId()));
        }

        // 1.设置子组
        if (allChildGroupsMap.containsKey(rootGroup.getId())) {
            List<WorkGroupDto> childGroups = allChildGroupsMap.get(rootGroup.getId());
            for (WorkGroupDto workGroup : childGroups) {
                trackHrGroupTreeForRootGroup(workGroup, allChildGroupsMap, workGroupUsersMap, allUserMap);
                total += workGroup.getMemberNumber();
            }
            rootGroup.setChildWorkGroups(childGroups);
        } else {
            rootGroup.setChildWorkGroups(new ArrayList<>());
        }

        // 2.设置成员
        if (workGroupUsersMap.containsKey(rootGroup.getId())) {
            List<U> members = workGroupUsersMap.get(rootGroup.getId());
            rootGroup.setMembers(members);
            total += members.size();
        } else {
            rootGroup.setMembers(new ArrayList());
        }
        rootGroup.setMemberNumber(total);
    }

    @Override
    public List<U> getAllMembersByRootWorkGroup(WorkGroupDto rootWorkGroup) {
        logger.info("will getAllMembersByRootWorkGroup...");
        List<U> members = new ArrayList<>();

        // 添加所有子组下的所有成员
        addAllMembers(members, rootWorkGroup);

        // 去掉本人
        List<U> result = new ArrayList<>();
        for (U temp : members) {
            if (!temp.getUserId().equals(rootWorkGroup.getLeaderId())) {
                result.add(temp);
            }
        }

        return result;
    }


    @Override
    @Deprecated
    public WorkGroupDto getUserWorkGroupByUserIdAndType(Long userId, String type) {
        logger.info("will getUserWorkGroupByUserIdAndType,userId:{},type:{}", userId, type);
        Long workGroupRoleId = getGroupRoleIdByType(type);
        List<WorkGroupDto> allWorkGroupDto = workGroupMapper.selectAllWithLeaderByGroupRoleId(workGroupRoleId);
        Long currentHrWorkGroupId = userMapper.selectByPrimaryKey(userId).getWorkGroupId();
        if (null == currentHrWorkGroupId) {
            logger.info("workGroupId is null by userId is {}", userId);
            throw new ParamException("当前用户没有配置工作组");
        }

        // 获取当前dto
        WorkGroupDto currentDto = getWorkGroupDtoById(currentHrWorkGroupId, allWorkGroupDto);
        while (true) {
            // 到达根节点,或找到leader，并且leader不是本人，停止。
            if (currentDto.getParent() == null || (currentDto.getLeaderId() != null && !userId.equals(currentDto.getLeaderId()))) {
                break;
            }
            //  否则向上递归
            else {
                currentDto = getWorkGroupDtoById(currentDto.getParent(), allWorkGroupDto);
            }
        }

        return currentDto;
    }

    @Override
    public WorkGroupDto getUserWorkGroupByUserAndAllWorkGroup(WorkGroupUserDto user, List<WorkGroupDto> allWorkGroupDto) {
        Long currentHrWorkGroupId = user.getWorkGroupId();
        if (null == currentHrWorkGroupId) {
            logger.info("workGroupId is null by userId is {}", user.getUserId());
            throw new ParamException("当前用户没有配置工作组");
        }

        // 获取当前dto
        WorkGroupDto currentDto = getWorkGroupDtoById(currentHrWorkGroupId, allWorkGroupDto);
        if (null == currentDto) {
            logger.info("workGroup is null by workGroupId is {}", currentHrWorkGroupId);
            throw new ParamException("当前工作组没找到");
        }

        while (true) {
            // 到达根节点,或找到leader，并且leader不是本人，停止。
            if (currentDto.getParent() == null || (currentDto.getLeaderId() != null && !user.getUserId().equals(currentDto.getLeaderId()))) {
                break;
            }
            //  否则向上递归
            else {
                currentDto = getWorkGroupDtoById(currentDto.getParent(), allWorkGroupDto);
            }
        }

        return currentDto;
    }


    /**
     * 预置条件
     * List<WorkGroupUserDto> allUsers = userMapper.selectAllEntityWithWorkGroupSimple(type);// 注意不同的模块，work_group_role_id不一样
     */
    @Override
    public List<U> getDirectMember(WorkGroupDto rootWorkGroup) {
        logger.info("will getDirectMembersByWorkGroupIdAndType,workGroupId:{}", rootWorkGroup.getId());

        // 1. 开始查找直接下属成员
        List<WorkGroupUserDto> directSubMemberList = getDirectSubMemberList(rootWorkGroup);

        // 2. 去重，并去除绩本人
        List<U> noDupList = MyListUtils.removeDuplicate(directSubMemberList);
        List<U> result = new ArrayList<>();
        for (U temp : noDupList) {
            if (!temp.getUserId().equals(rootWorkGroup.getLeaderId())) {
                result.add(temp);
            }
        }
        return result;
    }


    @Override
    public List<WorkGroupDto> getDirectGroup(List<U> directMemberList, List<WorkGroupDto> allGroups) {

        List<WorkGroupDto> result = new ArrayList<>();

        // 查找直接下属中，是绩效负责人的用户
        Map<Long, U> managerUser = new HashMap<>();
        for (U workGroupUserDto : directMemberList) {
            if (workGroupUserDto.getLeaderFlag()) {
                managerUser.put(workGroupUserDto.getUserId(), workGroupUserDto);
            }
        }

        // 查找绩效负责人所管理的组，并加入结果集
        for (WorkGroupDto workGroupDto : allGroups) {
            if (managerUser.containsKey(workGroupDto.getLeaderId())) {
                workGroupDto.setLeader(managerUser.get(workGroupDto.getLeaderId()));
                result.add(workGroupDto);
            }
        }
        return result;
    }

    private void addAllMembers(List<U> result, WorkGroupDto rootWorkGroup) {
        if (null != rootWorkGroup.getMembers()) {
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

    private WorkGroupDto getWorkGroupDtoById(Long groupId, List<WorkGroupDto> workGroupDtos) {
        WorkGroupDto result = null;
        for (WorkGroupDto workGroupDto : workGroupDtos) {
            if (workGroupDto.getId().equals(groupId)) {
                result = workGroupDto;
                break;
            }
        }
        return result;
    }

    private Long getGroupRoleIdByType(String type) {
        Long workGroupRoleId = 0L;
        if (PerformanceServiceType.performance.equals(type)) {
            workGroupRoleId = WorkGroupRole.Role.performance;
        } else if (PerformanceServiceType.salary.equals(type)) {
            workGroupRoleId = WorkGroupRole.Role.salary;
        } else if (PerformanceServiceType.grade.equals(type)) {
            workGroupRoleId = WorkGroupRole.Role.grade;
        }
        return workGroupRoleId;
    }


    // 获取直接下属
    private List<WorkGroupUserDto> getDirectSubMemberList(WorkGroupDto rootGroup) {
        List<WorkGroupUserDto> result = new ArrayList<>();

        // 加入当前组的所有成员
        List<WorkGroupUserDto> members = rootGroup.getMembers();
        if (members != null) {
            result.addAll(members);
        }

        List<WorkGroupDto> childWorkGroups = rootGroup.getChildWorkGroups();
        for (WorkGroupDto subGroup : childWorkGroups) {

            // 若有绩效负责人，则直接加入到直接下属，继续检查下一个子组。
            if (null != subGroup.getLeader()) {
                result.add(subGroup.getLeader());
                continue;
            }

            // 否则，递归查找子组，并加入到结果集中
            List<WorkGroupUserDto> subMemberList = getDirectSubMemberList(subGroup);
            result.addAll(subMemberList);
        }

        return result;
    }


}
