package com.seasun.management.service;


import com.seasun.management.Application;
import com.seasun.management.dto.PerformanceUserDto;
import com.seasun.management.dto.WorkGroupDto;
import com.seasun.management.dto.WorkGroupUserDto;
import com.seasun.management.exception.ParamException;
import com.seasun.management.mapper.PerformanceWorkGroupMapper;
import com.seasun.management.mapper.RUserWorkGroupPermMapper;
import com.seasun.management.mapper.UserMapper;
import com.seasun.management.mapper.WorkGroupMapper;
import com.seasun.management.model.PerformanceWorkGroup;
import com.seasun.management.model.WorkGroup;
import com.seasun.management.model.WorkGroupRole;
import com.seasun.management.service.performanceCore.groupTrack.GroupTrackService;
import com.seasun.management.vo.RUserWorkGroupPermVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@ActiveProfiles("local")
public class GroupServiceTest {


    private static final Logger logger = LoggerFactory.getLogger(GroupServiceTest.class);

    @Autowired
    WorkGroupService workGroupService;

    @Autowired
    WorkGroupMapper workGroupMapper;

    @Autowired
    RUserWorkGroupPermMapper rUserWorkGroupPermMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    GroupTrackService<PerformanceUserDto> groupTrackService;

    @Autowired
    PerformanceWorkGroupMapper performanceWorkGroupMapper;

    @Test
    public void testChildGroupRelation() {
        WorkGroup workGroup = workGroupMapper.selectByPrimaryKey(83L);
        List<WorkGroup> workGroupList = workGroupService.getWorkGroupRelationByChild(workGroup);
        for (WorkGroup group : workGroupList) {
            logger.debug("group name is:{}", group.getName());
        }
    }

    @Test
    public void testParentGroupRelation() {

        List<WorkGroup> workGroupList = workGroupMapper.selectAllIdParentWorkGroup();
        List<Long> result = workGroupService.getWorkGroupRelationByParent(309L, workGroupList);
        for (Long id : result) {
            logger.debug("child group id is:{}", id);
        }
    }

    @Test
    public void testGroupTree() {
        List<WorkGroup> rootGroups = workGroupService.getWorkGroupTree();
        for (WorkGroup root : rootGroups) {
            String other = genAppendInfo(root);
            logger.debug("parent group:{}，id:{}" + other, root.getName(), root.getId());
            printChild(root, "");
        }
    }


    @Test
    public void testProjectGroupTree() {
        List<WorkGroup> rootGroups = workGroupService.getWorkGroupTreeByProject("剑网3谢云流传");
        for (WorkGroup root : rootGroups) {
            String other = genAppendInfo(root);
            logger.debug("{}" + other, root.getName());
            printChild(root, "");
        }
    }

    private void printChild(WorkGroup group, String prefix) {
        if (group.getChildren() == null) {
            return;
        }
        prefix = prefix + "-  ";
        for (WorkGroup child : group.getChildren()) {
            String other = genAppendInfo(child);
            logger.debug(prefix + "{}" + other, child.getName());
            printChild(child, prefix);
        }
    }

    private void printPerformanceChild(WorkGroup group, List<RUserWorkGroupPermVo> permVos, String prefix) {
        if (group.getChildren() == null) {
            return;
        }
        prefix = prefix + "-  ";
        for (WorkGroup child : group.getChildren()) {
            String other = genAppendInfo(child);
            String performanceManager = "";
            for (RUserWorkGroupPermVo tempPerm : permVos) {
                if (child.getId().equals(tempPerm.getWorkGroupId())) {
                    performanceManager = performanceManager + tempPerm.getLoginId();
                    break;
                }
            }

            if (performanceManager.length() > 0) {
                logger.debug(prefix + "{}" + other, child.getName() + "(" + performanceManager + ")");
            } else {
                logger.debug(prefix + "{}" + other, child.getName());
            }

            printPerformanceChild(child, permVos, prefix);
        }
    }


//    private String genPerformanceInfo()

    private String genAppendInfo(WorkGroup group) {
        String other = ",";
        if (!group.getActiveFlag()) {
            other = other + " (非激活)";
        }
        if (!group.getHasUserFlag()) {
            other = other + " (没人)";
        }
        return other;
    }

    /**
     * 打印当前组和成员结构
     */
    @Test
    public void testGroupPermTreeInfoPrint() {

        Long roleId=WorkGroupRole.Role.performance;
//        Long roleId = WorkGroupRole.Role.salary;
        List<WorkGroup> roots = workGroupService.getWorkGroupTree();
        List<RUserWorkGroupPermVo> workGroupPermVos = rUserWorkGroupPermMapper.selectAllSystemPerm(roleId);
        Map<Long, List<Long>> map = groupIdAndUserIdsMap();

        for (WorkGroup root : roots) {

            // 只打印大西山居(id:267)下的组织结构
            if (!root.getId().equals(267L)) {
                continue;
            }
            String other = genAppendInfo(root);
            String userIdStr = "";
            String performanceManagerStr = "";
            for (RUserWorkGroupPermVo permVo : workGroupPermVos) {
                if (root.getId().equals(permVo.getWorkGroupId())) {
                    userIdStr = userIdStr +"("+permVo.getId() +")";
                    performanceManagerStr = performanceManagerStr + "(" + permVo.getLoginId() + ")";
                    break;
                }
            }

            if (performanceManagerStr.length() > 0) {
                logger.debug("parent group:{}，id:{},------,leaderId:{},manager:{}" + other, root.getName(), root.getId(), userIdStr ,performanceManagerStr);
            } else {
                logger.debug("parent group:{}，id:{}" + other, root.getName(), root.getId());
            }

            printList(map.get(root.getId()), "");

            printAllSystemChild(root, workGroupPermVos, "", map);
        }
    }

    private void printAllSystemChild(WorkGroup group, List<RUserWorkGroupPermVo> permVos, String prefix, Map<Long, List<Long>> map) {
        if (group.getChildren() == null) {
            return;
        }
        prefix = prefix + "-  ";
        for (WorkGroup child : group.getChildren()) {
            String other = genAppendInfo(child);
            String performanceManager = "";
            String userStr = "";
            for (RUserWorkGroupPermVo tempPerm : permVos) {
                if (child.getId().equals(tempPerm.getWorkGroupId())) {
                    performanceManager = performanceManager + tempPerm.getLoginId();
                    userStr =userStr+tempPerm.getUserId();
                    break;
                }
            }

            if (performanceManager.length() > 0) {
                logger.debug(prefix + "{}" + other, child.getName() +"------"+userStr +"(" + performanceManager + ")");
            } else {
                logger.debug(prefix + "{}" + other, child.getName());
            }
            printList(map.get(child.getId()), prefix);
            printAllSystemChild(child, permVos, prefix,map);
        }
    }

    private Map<Long, List<Long>> groupIdAndUserIdsMap() {

        Map<Long, List<Long>> groupIdUsers = new HashMap<>();
        List<WorkGroupUserDto> allUsers = userMapper.selectAllEntityWithWorkGroupSimple(WorkGroupRole.Role.salary);
        for (WorkGroupUserDto user : allUsers) {
            if (groupIdUsers.containsKey(user.getWorkGroupId())) {
                List<Long> userIds = groupIdUsers.get(user.getWorkGroupId());
                userIds.add(user.getUserId());
            } else {
                List<Long> userIds = new ArrayList<>();
                userIds.add(user.getUserId());
                groupIdUsers.put(user.getWorkGroupId(), userIds);
            }
        }
        return groupIdUsers;
    }

    private void printList(List<Long> result, String prefix) {

        String str = "";
        if (result != null && result.size() != 0) {
            for (int i = 0; i < result.size(); i++) {
                //只有一个
                if (i == 0 && i == result.size() - 1) {
                    str += "(" + result.get(0) + ")";
                } else {
                    //排列
                    if (i == 0) {
                        str += "(" + result.get(0) + ",";
                    } else if (i == result.size() - 1) {
                        str += result.get(i) + ")";
                    } else {
                        str += result.get(i) + ",";
                    }
                }
            }
        }
        logger.info(prefix + str);
    }

    @Test
    public void insertPerformanceWorkGroupData() {
        List<WorkGroupDto> allGroups = workGroupMapper.selectAllWithLeaderByGroupRoleId(WorkGroupRole.Role.performance);
        List<PerformanceUserDto> allUsers = userMapper.selectAllEntityWithPerformance(WorkGroupRole.Role.performance);
        WorkGroupDto rootWorkGroup = getWorkGroupTree("西山居", allGroups, allUsers);
        traversalTreeForInsertPerformanceWorkGroup(rootWorkGroup, null);
    }

    private WorkGroupDto getWorkGroupTree(String workGroupName, List<WorkGroupDto> allGroups, List<PerformanceUserDto> allUsers) {
        // 找出根目录，初始化树
        WorkGroupDto rootWorkGroup = null;
        for (WorkGroupDto workGroupDto : allGroups) {
            if (workGroupName.equals(workGroupDto.getName())) {
                rootWorkGroup = workGroupDto;
                break;
            }
        }
        if (null == rootWorkGroup) {
            logger.info("{} work group not found", workGroupName);
            throw new ParamException("当前组不存在");
        }
        groupTrackService.initHrGroupTreeByRootGroup(rootWorkGroup, allGroups, allUsers);

        return rootWorkGroup;
    }

    private void traversalTreeForInsertPerformanceWorkGroup(WorkGroupDto rootWorkGroup, Long parent) {
        if (rootWorkGroup.getMemberNumber() >= 8) {
            PerformanceWorkGroup performanceWorkGroup = new PerformanceWorkGroup();
            performanceWorkGroup.setWorkGroupId(rootWorkGroup.getId());
            performanceWorkGroup.setName(rootWorkGroup.getName());
            performanceWorkGroup.setParent(parent);
            performanceWorkGroup.setPerformanceManagerId(rootWorkGroup.getLeaderId());
            performanceWorkGroup.setStrictType(2);
            performanceWorkGroupMapper.insert(performanceWorkGroup);

            if (null != rootWorkGroup.getChildWorkGroups() && !rootWorkGroup.getChildWorkGroups().isEmpty()) {
                List<WorkGroupDto> childWorkGroups = rootWorkGroup.getChildWorkGroups();
                for (WorkGroupDto childGroup : childWorkGroups) {
                    traversalTreeForInsertPerformanceWorkGroup(childGroup, performanceWorkGroup.getId());
                }
            }
        }
    }
}
