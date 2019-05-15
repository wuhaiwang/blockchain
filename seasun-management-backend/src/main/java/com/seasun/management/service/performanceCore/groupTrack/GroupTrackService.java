package com.seasun.management.service.performanceCore.groupTrack;

import com.seasun.management.dto.WorkGroupDto;
import com.seasun.management.dto.WorkGroupUserDto;


import java.util.List;

public interface GroupTrackService<U extends WorkGroupUserDto> {


    /**
     * 初始化树
     *
     * @param rootGroup
     * @param allGroups
     * @param allUsers
     */
    void initHrGroupTreeByRootGroup(WorkGroupDto rootGroup, List<WorkGroupDto> allGroups, List<U> allUsers);


    /**
     * 获取该组下所有人力子组的成员。（不包含该组的 绩效/调薪/调级 负责人）
     *
     * @param rootWorkGroup: 根节点组
     * @return 注：暂时不区分组是否激活
     */
    List<U> getAllMembersByRootWorkGroup(WorkGroupDto rootWorkGroup);


    /**
     * 获取用户在 绩效/调薪/调级 上的所属工作组
     *
     * @param userId
     * @param type:  performance/salary/grade
     * @return
     */
    WorkGroupDto getUserWorkGroupByUserIdAndType(Long userId, String type);

    /**
     * 获取用户在 绩效/调薪/调级 上的所属工作组
     *
     * @param user
     * @param allWorkGroupDto
     * @return
     */
    WorkGroupDto getUserWorkGroupByUserAndAllWorkGroup(WorkGroupUserDto user, List<WorkGroupDto> allWorkGroupDto);


    /**
     * 获取该组下的直接下属，包括：当前组人力上的所有员工 - 本组的绩效负责人 +  子组的绩效负责人 + 子组下没有绩效负责人的员工
     *
     * @param rootWorkGroup 树
     * @return
     */
    List<U> getDirectMember(WorkGroupDto rootWorkGroup);


    /**
     * 通过直接下属，查找并返回 下属团队。
     *
     * @param directMemberList 直接下属用户
     * @param allGroups        所有组
     * @return
     */
    List<WorkGroupDto> getDirectGroup(List<U> directMemberList, List<WorkGroupDto> allGroups);

}