package com.seasun.management.service;

import com.seasun.management.dto.HrWorkGroupDto;
import com.seasun.management.dto.WorkGroupHrDto;
import com.seasun.management.model.IdNameBaseObject;
import com.seasun.management.model.RUserWorkGroupPerm;
import com.seasun.management.model.WorkGroup;
import com.seasun.management.vo.OrgWorkGroupMemberAppVo;
import com.seasun.management.vo.UserPerformanceIdentityAppVo;
import com.seasun.management.vo.WorkGroupOrgVo;
import com.seasun.management.vo.WorkGroupPerformanceVo;
import com.seasun.management.vo.SubGroupPerformanceVo;
import com.seasun.management.vo.WorkGroupUserVo;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface WorkGroupService {

    /**
     * 以该工作组为子节点，查找并返回该子节点之上所有的父节点。
     * 输出顺序示例： 当前节点，子节点1，子节点1-1，子节点1-1-1
     *
     * @param workGroup
     * @return
     */
    List<WorkGroup> getWorkGroupRelationByChild(WorkGroup workGroup);

    /**
     * 以该工作组为父节点，查找并返回该父节点下的所有子节点
     *
     * @param workGroupId
     * @return
     */
    List<Long> getWorkGroupRelationByParent(Long workGroupId, List<WorkGroup> allWorkGroup);

    /**
     * 生成工作组树
     *
     * @return
     */
    List<WorkGroup> getWorkGroupTree();


    /**
     * 获取部门下的所有工作组
     *
     * @param projectName
     * @return
     */
    List<WorkGroup> getWorkGroupTreeByProject(String projectName);

    /**
     * 获取项目下的所有工作组树
     *
     * @param projectId
     * @return
     */
    List<WorkGroup> getWorkGroupTreeByProjectId(Long projectId);

    /**
     * 获取父组下的所有工作组树
     *
     * @param projectId
     * @param groupId
     * @return
     */
    WorkGroup getWorkGroupTreeByProjectIdAndGroupId(Long projectId, Long groupId);

    /**
     * 返回当前用户的多重身份
     *
     * @return
     */
    UserPerformanceIdentityAppVo getUserPerformanceIdentity(long userType);

    /**
     * 获取人力组织架构图
     *
     * @return
     */
    WorkGroupOrgVo getWorkGroupOrgMap();


    /**
     * 获取工作组详情
     *
     * @return
     */
    WorkGroupPerformanceVo getWorkGroupDetailById(Long id);

    List<WorkGroupPerformanceVo.UserInfo> getWorkGroupMemberById(Long id);


    /**
     * 更新组负责人
     *
     * @return
     */
    int updateWorkGroupPermByType(Long groupId, Long userId, String type);

    /**
     * 删除组负责人
     *
     * @param groupId
     * @param loginId
     * @param type
     */
    void deleteWorkGroupPermByType(Long groupId, String loginId, String type);

    List<WorkGroup> getAllIdParentWorkGroup();

    List<WorkGroup> getAllActiveWorkGroup();

    void initHrGroupTreeByRootGroup(HrWorkGroupDto rootGroup, List<HrWorkGroupDto> allGroups, List<OrgWorkGroupMemberAppVo> allUsers,boolean allFlag);

    void trackHrGroupTreeForRootGroup(List<Long> leaderIds, HrWorkGroupDto rootGroup, Map<Long, List<HrWorkGroupDto>> allChildGroupsMap,
                                      Map<Long, List<OrgWorkGroupMemberAppVo>> workGroupUsersMap, Map<Long, OrgWorkGroupMemberAppVo> allUserMap, boolean allFlag);
    List<WorkGroupUserVo> getWorkGroupUserByKeyword(String keyword);

    String getWorkGroupFullNameById(Long id, Map<Long, WorkGroup> workGroupByIdMap);

    HrWorkGroupDto buildHrWorkGroupView();

    <T extends WorkGroup>  Set<Long>  getSubGroupIds(Long rootId, List<T> workGroups);

    <T extends WorkGroup> Set<Long> getSubGroupIds(Long groupId, Map<Long, List<T>> workGroupByParentIdMap);

	List<WorkGroup> getActiveWorkGroupTreeByProjectId(Long platId);

    Map<Long, String> refreshWorkGroupsIdNamePairIntoCache();

    /**
     * 获取所有工作组, 格式是这样的
     * id: 301
     * name: 珠海/珠海1/珠海2
     * */
    Map<Long, String> getWorkGroupsIdNamePairFromCache ();

    void addWorkGroupManager(RUserWorkGroupPerm rUserWorkGroupPerm);
}
