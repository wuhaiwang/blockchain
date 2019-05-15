package com.seasun.management.service.impl;

import com.seasun.management.dto.WorkGroupDto;
import com.seasun.management.dto.UserGradeDetailDto;
import com.seasun.management.dto.UserGradeDto;
import com.seasun.management.mapper.UserGradeChangeMapper;
import com.seasun.management.mapper.UserMapper;
import com.seasun.management.mapper.WorkGroupMapper;
import com.seasun.management.model.UserGradeChange;
import com.seasun.management.model.WorkGroup;
import com.seasun.management.model.WorkGroupRole;
import com.seasun.management.service.UserGradeService;
import com.seasun.management.service.performanceCore.groupTrack.GroupTrackService;
import com.seasun.management.vo.UserGradeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserGradeServiceImpl implements UserGradeService {

    @FunctionalInterface
    public interface GradeCountService {
        void count(UserGradeVo.Profile profile);
    }

    private static Map<String, GradeCountService> gradeCountServiceMap = new HashMap<>();

    static {
        gradeCountServiceMap.put("T1", gradeCount -> gradeCount.setT1Count(gradeCount.getT1Count() + 1));
        gradeCountServiceMap.put("T2", gradeCount -> gradeCount.setT2Count(gradeCount.getT2Count() + 1));
        gradeCountServiceMap.put("T3", gradeCount -> gradeCount.setT3Count(gradeCount.getT3Count() + 1));
        gradeCountServiceMap.put("T4", gradeCount -> gradeCount.setT4Count(gradeCount.getT4Count() + 1));
        gradeCountServiceMap.put("T5", gradeCount -> gradeCount.setT5Count(gradeCount.getT5Count() + 1));
        gradeCountServiceMap.put("T6", gradeCount -> gradeCount.setT6Count(gradeCount.getT6Count() + 1));
        gradeCountServiceMap.put("T7", gradeCount -> gradeCount.setT7Count(gradeCount.getT7Count() + 1));
    }

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WorkGroupMapper workGroupMapper;

    @Autowired
    private UserGradeChangeMapper userGradeChangeMapper;

    @Autowired
    private GroupTrackService groupTrackService;


    public UserGradeVo getSubGradeChange(Long workGroupId) {
        List<WorkGroupDto> allWorkGroup = workGroupMapper.selectAllWithLeaderByGroupRoleId(WorkGroupRole.Role.grade);
        WorkGroupDto workGroupOfManager = allWorkGroup.stream().filter(workGroupDto -> workGroupDto.getId().equals(workGroupId)).findFirst().orElse(null);

        if (null == workGroupOfManager) {
            return null;
        }

        List<UserGradeDto> allUserGradeInfo = userMapper.selectAllUserGradeInfo(WorkGroupRole.Role.grade);

        groupTrackService.initHrGroupTreeByRootGroup(workGroupOfManager, allWorkGroup, allUserGradeInfo);

        UserGradeVo userGradeVo = new UserGradeVo();

        userGradeVo.setProfile(new UserGradeVo.Profile());
        getUserGradeProfile(userGradeVo, workGroupOfManager);
        userGradeVo.getProfile().calculate();

        userGradeVo.setManagerMemberList(new ArrayList<>());
        userGradeVo.setSubGroup(new UserGradeVo.SubWorkGroupGrade());
        userGradeVo.getSubGroup().setData(new ArrayList<>());

        getWorkGroupMemberAndSubWorkGroup(userGradeVo, workGroupOfManager);
        userGradeVo.getProfile().setTotal(userGradeVo.getManagerMemberList().size() + userGradeVo.getSubGroup().getMemberCount());
        return userGradeVo;
    }

    public List<UserGradeDto> getMemberGradeChange(Long workGroupId) {
        List<WorkGroupDto> allWorkGroup = workGroupMapper.selectAllWithLeaderByGroupRoleId(WorkGroupRole.Role.grade);
        WorkGroupDto workGroupOfManager = allWorkGroup.stream().filter(workGroupDto -> workGroupDto.getId().equals(workGroupId)).findFirst().orElse(null);

        if (null == workGroupOfManager) {
            return null;
        }

        List<UserGradeDto> allUserGradeInfo = userMapper.selectAllUserGradeInfo(WorkGroupRole.Role.grade);

        groupTrackService.initHrGroupTreeByRootGroup(workGroupOfManager, allWorkGroup, allUserGradeInfo);

        return getAllMemberGradeDtoList(workGroupOfManager);
    }

    private List<UserGradeDto> getAllMemberGradeDtoList(WorkGroupDto<UserGradeDto> workGroup) {
        List<UserGradeDto> userGradeInfoList = new ArrayList<>();
        userGradeInfoList.addAll(workGroup.getMembers());

        workGroup.getChildWorkGroups().stream().forEach(subWorkGroup -> userGradeInfoList.addAll(getAllMemberGradeDtoList(subWorkGroup)));

        return userGradeInfoList;
    }

    public UserGradeDetailDto getGradeChangeDetail(Long userId) {
        UserGradeDetailDto userGradeDetailInfo = userMapper.selectUserGradeDetailInfo(userId, WorkGroupRole.Role.grade);
        if (null != userGradeDetailInfo) {
            userGradeDetailInfo.setHistoryGrade(userGradeChangeMapper.selectUserGradeHistory(userId));
        }
        return userGradeDetailInfo;
    }

    public Boolean changeUserGradeInfo(Long userId, Integer year, Integer month, String grade, String evaluateType) {
        UserGradeDetailDto userGradeDetailInfo = userMapper.selectUserGradeDetailInfo(userId, WorkGroupRole.Role.grade);
        if (null != userGradeDetailInfo && (grade != null || evaluateType != null)) {
            UserGradeChange userGradeChange = new UserGradeChange();
            userGradeChange.setUserId(userId);
            userGradeChange.setYear(year);
            userGradeChange.setMonth(month);
            userGradeChange.setOldGrade(userGradeDetailInfo.getGrade());
            userGradeChange.setOldEvaluateType(userGradeDetailInfo.getEvaluateType());
            userGradeChange.setNewGrade(grade);
            userGradeChange.setNewEvaluateType(evaluateType);

            userGradeChangeMapper.insertSelective(userGradeChange);

            userMapper.updateUserGradeInfo(userId, grade, evaluateType);

            return true;
        } else {
            return false;
        }
    }


    private void getUserGradeProfile(UserGradeVo userGradeVo, WorkGroupDto<UserGradeDto> workGroup) {
        for (UserGradeDto userGradeDto : workGroup.getMembers()) {
            userGradeVo.getProfile().setTotal(userGradeVo.getProfile().getTotal() + 1);
            if (userGradeDto.getGrade() == null) {
                continue;
            }
            gradeCountServiceMap.get(userGradeDto.getGrade().split("-")[0]).count(userGradeVo.getProfile());
        }

        workGroup.getChildWorkGroups().stream().forEach(workGroupDto -> getUserGradeProfile(userGradeVo, workGroupDto));
    }

    private void getWorkGroupMemberAndSubWorkGroup(UserGradeVo userGradeVo, WorkGroupDto<UserGradeDto> workGroup) {
        //首先把这个组里面的人放到直接下属里
        workGroup.getMembers().stream().filter(userGradeDto -> userGradeDto.getUserId().equals(workGroup.getLeaderId())).forEach(userGradeDto -> addMemberUserToUserGradeVo(userGradeVo, userGradeDto));

        //再去检查子组里面有没有配置调级负责人
        for (WorkGroupDto workGroupChild : workGroup.getChildWorkGroups()) {
            if (null == workGroupChild.getLeaderId()) {
                //没有配调级负责人,去递归下一级组，会把组里面的人列为直接下属
                getWorkGroupMemberAndSubWorkGroup(userGradeVo, workGroupChild);
            } else {
                //有配调级负责人，就把调级负责人列为直接下属
                addMemberUserToUserGradeVo(userGradeVo, (UserGradeDto) workGroupChild.getLeader());

                //有配调级负责人的，还要把该组列为子组
                userGradeVo.getSubGroup().setTotal(userGradeVo.getSubGroup().getTotal() + 1);

                UserGradeVo.WorkGroupGrade workGroupGrade = new UserGradeVo.WorkGroupGrade();
                workGroupGrade.setGroupId(workGroupChild.getId());
                workGroupGrade.setGroupName(workGroupChild.getName());

                getWorkGroupGrade(workGroupChild, workGroupGrade);

                workGroupGrade.calculate();
                userGradeVo.getSubGroup().setMemberCount(userGradeVo.getSubGroup().getMemberCount() + workGroupGrade.getMemberCount());
                userGradeVo.getSubGroup().getData().add(workGroupGrade);
            }
        }
    }

    private void getWorkGroupGrade(WorkGroupDto<UserGradeDto> workGroup, UserGradeVo.WorkGroupGrade workGroupGrade) {
        for (UserGradeDto userGradeInfo : workGroup.getMembers()) {
            workGroupGrade.setMemberCount(workGroupGrade.getMemberCount() + 1);
            workGroupGrade.setTotal(workGroupGrade.getTotal() + 1);
            if (null == userGradeInfo.getGrade()) {
                continue;
            }
            gradeCountServiceMap.get(userGradeInfo.getGrade().split("-")[0]).count(workGroupGrade);
        }

        workGroup.getChildWorkGroups().stream().forEach(workGroupDto -> getWorkGroupGrade(workGroupDto, workGroupGrade));
    }

    private void addMemberUserToUserGradeVo(UserGradeVo userGradeVo, UserGradeDto userGradeInfo) {
        Boolean blExists = userGradeVo.getManagerMemberList().stream().anyMatch(userGradeDto -> userGradeDto.getUserId().equals(userGradeInfo.getUserId()));

        if (!blExists) {
            userGradeVo.getManagerMemberList().add(userGradeInfo);
        }
    }
}
