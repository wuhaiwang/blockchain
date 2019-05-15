package com.seasun.management.service.impl;

import com.seasun.management.constant.ErrorCode;
import com.seasun.management.controller.response.CommonResponse;
import com.seasun.management.dto.FixRoleInfoDto;
import com.seasun.management.dto.FmGroupMemberDto;
import com.seasun.management.dto.FmMemberDto;
import com.seasun.management.dto.YearMonthDto;
import com.seasun.management.exception.DataImportException;
import com.seasun.management.helper.ExcelHelper;
import com.seasun.management.helper.ReportHelper;
import com.seasun.management.mapper.*;
import com.seasun.management.model.*;
import com.seasun.management.service.PerformanceFMService;
import com.seasun.management.util.MyCellUtils;
import com.seasun.management.util.MyFileUtils;
import com.seasun.management.util.MySystemParamUtils;
import com.seasun.management.util.MyTokenUtils;
import com.seasun.management.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PerformanceFMServiceImpl implements PerformanceFMService {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceFMServiceImpl.class);

    @Value("${file.sys.prefix}")
    private String filePathPrefix;

    @Value("${backup.excel.url}")
    private String backupExcelUrl;

    @Autowired
    FmMemberMapper fmMemberMapper;

    @Autowired
    ProjectMapper projectMapper;

    @Autowired
    FmUserRoleMapper fmUserRoleMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    RUserProjectPermMapper rUserProjectPermMapper;

    @Autowired
    FmGroupConfirmInfoMapper fmGroupConfirmInfoMapper;

    @Autowired
    UserPerformanceMapper userPerformanceMapper;

    @Autowired
    FmPerfSubmitInfoMapper fmPerfSubmitInfoMapper;

    @Override
    public List<PerformanceFMStageVo> getUserIdentityInfo(Long userId) {
        List<PerformanceFMStageVo> result = new ArrayList<>();
        if (userId == null) {
            userId = MyTokenUtils.getCurrentUserId();
        }
        List<FixRoleInfoDto> fixRoleInfoDtoList;
        if (MyTokenUtils.isAdmin()) {
            fixRoleInfoDtoList = fmUserRoleMapper.getAdminUserIdentityInfo();
        } else {
            fixRoleInfoDtoList = fmUserRoleMapper.getUserIdentityInfo(userId);
        }
        if (fixRoleInfoDtoList == null || fixRoleInfoDtoList.size() == 0) {
            //该用户没有固化角色，返回
            return result;
        }

        for (FixRoleInfoDto fixRoleInfoDto : fixRoleInfoDtoList) {
            PerformanceFMStageVo vo = new PerformanceFMStageVo();
            vo.setId(fixRoleInfoDto.getpId());
            vo.setName(fixRoleInfoDto.getName());

            if (fixRoleInfoDto.getRoleId().equals(FmUserRole.Role.platFixRole)) {
                vo.setType("plat");
            } else if (fixRoleInfoDto.getRoleId().equals(FmUserRole.Role.projectFixRole)) {
                vo.setType("project");
            }
            result.add(vo);
        }
        return result;
    }

    @Override
    public PerformanceFMGroupsVo getFixMemberGroups(String type, Long id) {
        PerformanceFMGroupsVo performanceFMGroupsVo = new PerformanceFMGroupsVo();
        int totalNum;
        List<PerformanceFMGroupsVo.Group> groups = new ArrayList<>();

        //默认顺序确认，最后一次已完成情况(之前都为已完成)
        YearMonthDto yearMonthDto = userPerformanceMapper.selectWithYearMonthByLastComplete();

        List<MiniProjectVo> projectList = projectMapper.selectAllMiniProject();
        if (projectList == null) { //没有任何项目
            return performanceFMGroupsVo;
        }
        Map<Long, String> idNameMap = new HashMap<>();
        List<String> existProject = new ArrayList<>();
        for (IdNameBaseObject p : projectList) {
            idNameMap.put(p.getId(), p.getName());
        }
        List<FmGroupMemberDto> members;
        List<FixRoleInfoDto> fmUserRoles;
        List<FmGroupConfirmInfo> confirmInfoList;
        List<Long> allApplyGroupId = new ArrayList<>();

        //1. 查询出fm_member 表中所有的平台/项目 下的组
        if (type.equals("plat")) {
            members = fmMemberMapper.selectFixMembersByPlatId(id);
            fmUserRoles = fmUserRoleMapper.selectAllUserRoleByPlatId(id);
            confirmInfoList = fmGroupConfirmInfoMapper.selectAllConfirmInfoByPlatId(id);

            //已经提交审核 的 --- 记录
            if (confirmInfoList != null && confirmInfoList.size() != 0) {
                // 不存在已完成的月份
                if (yearMonthDto == null) {
                    for (FmGroupConfirmInfo fmConfirm : confirmInfoList) {
                        allApplyGroupId.add(fmConfirm.getProjectId());
                    }
                } else {
                    for (FmGroupConfirmInfo fmConfirm : confirmInfoList) {
                        int confirmYear = fmConfirm.getYear();
                        int confirmMonth = fmConfirm.getMonth();
                        int completeYear = yearMonthDto.getYear();
                        int completeMonth = yearMonthDto.getMonth();
                        Calendar confirm = Calendar.getInstance();
                        confirm.set(confirmYear, confirmMonth, 1);
                        Calendar complete = Calendar.getInstance();
                        complete.set(completeYear, completeMonth, 1);
                        if (complete.getTime().compareTo(confirm.getTime()) < 0) {
                            allApplyGroupId.add(fmConfirm.getProjectId());
                        }
                    }
                }
            }
        } else {
            members = fmMemberMapper.selectFixMembersByProjectId(id);
            fmUserRoles = fmUserRoleMapper.selectAllUserRoleByProjectId(id);
            confirmInfoList = fmGroupConfirmInfoMapper.selectAllConfirmInfoByProjectId(id);
            //已经提交审核 的 --- 记录
            if (confirmInfoList != null && confirmInfoList.size() != 0) {

                // 不存在已完成的月份
                if (yearMonthDto == null) {
                    for (FmGroupConfirmInfo fmConfirm : confirmInfoList) {
                        allApplyGroupId.add(fmConfirm.getPlatId());
                    }
                } else {
                    for (FmGroupConfirmInfo fmConfirm : confirmInfoList) {
                        int confirmYear = fmConfirm.getYear();
                        int confirmMonth = fmConfirm.getMonth();
                        int completeYear = yearMonthDto.getYear();
                        int completeMonth = yearMonthDto.getMonth();
                        Calendar confirm = Calendar.getInstance();
                        confirm.set(confirmYear, confirmMonth);
                        Calendar complete = Calendar.getInstance();
                        complete.set(completeYear, completeMonth);
                        if (complete.getTime().compareTo(confirm.getTime()) < 0) {
                            allApplyGroupId.add(fmConfirm.getPlatId());
                        }
                    }
                }
            }
        }
        if (members == null) {
            members = new ArrayList<>();
        }
        if (fmUserRoles == null) {
            fmUserRoles = new ArrayList<>();
        }

        //当前平台/项目下存在成员被固化
        totalNum = members.size();
        for (FmGroupMemberDto dto : members) {
            Long platId = dto.getPlatId();
            Long projectId = dto.getProjectId();
            String platName = idNameMap.get(platId);
            String projectName = idNameMap.get(projectId);
            String name = projectName + "-" + platName + "-固化组";
            //先查询这个平台下有多少相关的项目组
            if (!existProject.contains(name)) {
                PerformanceFMGroupsVo.Group group = new PerformanceFMGroupsVo.Group();
                List<PerformanceFMGroupsVo.Group.Member> groupMember = new ArrayList<>();
                group.setPlatId(platId);
                group.setProjectId(projectId);
                group.setName(name);
                group.setMembers(groupMember);
                existProject.add(name);
                groups.add(group);
            }
        }

        //将查出的成员按照组分类
        for (PerformanceFMGroupsVo.Group group : groups) {
            for (FmGroupMemberDto dto : members) {
                Long platId = dto.getPlatId();
                Long projectId = dto.getProjectId();
                String platName = idNameMap.get(platId);
                String projectName = idNameMap.get(projectId);
                String name = projectName + "-" + platName + "-固化组";
                if (group.getName().equals(name)) {
                    group.getMembers().add(dto);
                }
            }
            sortMembers(group.getMembers());
        }

        //2.查询出fm_user_role 存在的项目-平台固化组组合，以及项目负责人信息
        List<PerformanceFMGroupsVo.Group> unSavedGroups = new ArrayList<>();
        for (FixRoleInfoDto roleInfoDto : fmUserRoles) {
            Long platId = roleInfoDto.getPlatId();
            Long projectId = roleInfoDto.getProjectId();
            String platName = idNameMap.get(platId);
            String projectName = idNameMap.get(projectId);
            String name = projectName + "-" + platName + "-固化组";
            boolean memberExist = false;

            //为已经存在成员的组匹配负责人信息
            for (PerformanceFMGroupsVo.Group group : groups) {
                if (group.getName().equals(name)) {
                    memberExist = true;
                    PerformanceFMGroupsVo.Group.ProjectManager projectManager = new PerformanceFMGroupsVo.Group.ProjectManager();
                    projectManager.setId(roleInfoDto.getUserId());
                    projectManager.setName(roleInfoDto.getUserName());
                    group.setProjectManager(projectManager);
                }
            }
            if (!memberExist) {
                //为不存在人员的组匹配信息
                PerformanceFMGroupsVo.Group unSaveGroup = new PerformanceFMGroupsVo.Group();
                List<PerformanceFMGroupsVo.Group.Member> groupMember = new ArrayList<>();
                unSaveGroup.setPlatId(platId);
                unSaveGroup.setProjectId(projectId);
                unSaveGroup.setName(name);
                unSaveGroup.setMembers(groupMember);
                PerformanceFMGroupsVo.Group.ProjectManager projectManager = new PerformanceFMGroupsVo.Group.ProjectManager();
                projectManager.setId(roleInfoDto.getUserId());
                projectManager.setName(roleInfoDto.getUserName());
                unSaveGroup.setProjectManager(projectManager);
                unSavedGroups.add(unSaveGroup);
            }
        }
        //将不在固化人员表中存储的表添加进显示列表
        groups.addAll(unSavedGroups);

        //为该平台下所有的固化组匹配是否已经提交审核的Flag
        if (type.equals("plat")) {
            for (PerformanceFMGroupsVo.Group group : groups) {
                group.setApplyFlag(allApplyGroupId.contains(group.getProjectId()));
            }
        } else {
            for (PerformanceFMGroupsVo.Group group : groups) {
                group.setApplyFlag(allApplyGroupId.contains(group.getPlatId()));
            }
        }
        performanceFMGroupsVo.setMemberCount(totalNum);
        performanceFMGroupsVo.setGroupCount(groups.size());
        performanceFMGroupsVo.setGroups(groups);

        // 匹配月信息
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int date = calendar.get(Calendar.DATE);
        Integer startDay = MySystemParamUtils.getSystemConfigWithDefaultValue(MySystemParamUtils.Key.PerformanceStartDay, MySystemParamUtils.DefaultValue.PerformanceStartDay);

        // 当月绩效还未开始
        if (date < startDay) {
            calendar.add(Calendar.MONTH, -1);
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH) + 1;
        }
        performanceFMGroupsVo.setPerfSubmitFlag(getPerfSubmitStatus(id, year, month));
        performanceFMGroupsVo.setYear(year);
        performanceFMGroupsVo.setMonth(month);
        return performanceFMGroupsVo;
    }

    @Override
    public ImportFixMemberDataResultVo verifyFixMemberData(MultipartFile file, Long platId) {

        ImportFixMemberDataResultVo vo = new ImportFixMemberDataResultVo();
        ImportFixMemberDataResultVo.Conflict conflict = new ImportFixMemberDataResultVo.Conflict();
        List<ImportFixMemberDataResultVo.Conflict.ConflictMember> removedMembers = new ArrayList<>();
        List<ImportFixMemberDataResultVo.Conflict.ConflictMember> changedMembers = new ArrayList<>();
        List<UserSelectVo> userInfoList = userMapper.selectAllActiveUserSelectVo();
        List<ProjectVo> projectList = projectMapper.selectAllProjectAndPlat();

        //用户信息整理便于返回
        Map<Long, UserSelectVo> userSelectVoMap = new HashMap<>();
        for (UserSelectVo userSelectVo : userInfoList) {
            userSelectVoMap.put(userSelectVo.getId(), userSelectVo);
        }

        // 永久固化 UserId ，固化的项目Id -> Map
        List<FmMember> permanentFixedMember = fmMemberMapper.selectAllPermanentFixedMember(platId);
        Map<Long, Long> oldFixedUserIdProjectIdMap = new HashMap<>();
        for (FmMember fmMember : permanentFixedMember) {
            oldFixedUserIdProjectIdMap.put(fmMember.getUserId(), fmMember.getProjectId());
        }


        Map<Long, Long> extractByExcelMap = new HashMap<>();//解析excel文件生成的 将要固化 UserId ，固化的项目Id -> Map
        List<Long> allUserIdByExcelList = new ArrayList<>();//excel文件中所有涉及的成员

        String backupFilePath = backupExcel(file);
        String message = analysisExcelDataByFront(file, platId, extractByExcelMap, allUserIdByExcelList);
        Set<Long> extractByExcelSet = extractByExcelMap.keySet();
        Set<Map.Entry<Long, Long>> fixedUserIdProjectEntry = oldFixedUserIdProjectIdMap.entrySet();

        for (Map.Entry<Long, Long> fixedUserIdProjectId : fixedUserIdProjectEntry) {//依次检验之前永久固化人员，是不是被新一轮永久固化
            Long oldProjectId = fixedUserIdProjectId.getValue();
            if (extractByExcelSet.contains(fixedUserIdProjectId.getKey())) {
                Long newProjectId = extractByExcelMap.get(fixedUserIdProjectId.getKey());
                if (!newProjectId.equals(oldProjectId)) {
                    //转移到了别的组
                    ImportFixMemberDataResultVo.Conflict.ConflictMember changeMember = new ImportFixMemberDataResultVo.Conflict.ConflictMember();

                    //转移的组信息填充
                    ImportFixMemberDataResultVo.Conflict.ConflictMember.Group originFixGroup = new ImportFixMemberDataResultVo.Conflict.ConflictMember.Group();
                    ImportFixMemberDataResultVo.Conflict.ConflictMember.Group changeToGroup = new ImportFixMemberDataResultVo.Conflict.ConflictMember.Group();
                    String originFixGroupName = getFixGroupName(platId, oldProjectId, projectList);
                    String changeToName = getFixGroupName(platId, newProjectId, projectList);

                    originFixGroup.setId(oldProjectId);
                    originFixGroup.setName(originFixGroupName);
                    changeToGroup.setId(newProjectId);
                    changeToGroup.setName(changeToName);

                    String name = userSelectVoMap.get(fixedUserIdProjectId.getKey()).getName();
                    String loginId = userSelectVoMap.get(fixedUserIdProjectId.getKey()).getLoginId();
                    changeMember.setName(name);
                    changeMember.setId(fixedUserIdProjectId.getKey());
                    changeMember.setLoginId(loginId);
                    changeMember.setChangeTo(changeToGroup);
                    changeMember.setOriginFixGroup(originFixGroup);
                    changedMembers.add(changeMember);
                }
            } else {
                //不被固化
                ImportFixMemberDataResultVo.Conflict.ConflictMember removeMember = new ImportFixMemberDataResultVo.Conflict.ConflictMember();

                ImportFixMemberDataResultVo.Conflict.ConflictMember.Group originFixGroup = new ImportFixMemberDataResultVo.Conflict.ConflictMember.Group();
                String originFixName = getFixGroupName(platId, oldProjectId, projectList);
                originFixGroup.setName(originFixName);
                originFixGroup.setId(oldProjectId);

                String name = userSelectVoMap.get(fixedUserIdProjectId.getKey()).getName();
                String loginId = userSelectVoMap.get(fixedUserIdProjectId.getKey()).getLoginId();
                removeMember.setName(name);
                removeMember.setId(fixedUserIdProjectId.getKey());
                removeMember.setLoginId(loginId);
                removeMember.setOriginFixGroup(originFixGroup);
                if (allUserIdByExcelList.contains(fixedUserIdProjectId.getKey())) {
                    removeMember.setReason("分摊表中包含这个员工，但是其分摊比例不足够固化到其之前永久固化的项目中");
                } else {
                    removeMember.setReason("分摊表中没有包含此员工");
                }
                removedMembers.add(removeMember);
            }
        }

        conflict.setChangedMembers(changedMembers);
        conflict.setRemovedMembers(removedMembers);
        if (message.equals("")) {
            vo.setCode(0);
            vo.setMessage("success");
        } else {
            vo.setCode(1);
            vo.setUrl(message);
            vo.setMessage("文件解析可能出现一些问题。");
        }
        vo.setFileBackup(backupFilePath);
        vo.setConflict(conflict);
        return vo;
    }

    @Transactional
    @Override
    public void confirmPermanentFixedMember(List<PerformanceFMConfirmVo.PermanentConfirmSolution> removedMembers,
                                            List<PerformanceFMConfirmVo.PermanentConfirmSolution> changedMembers,
                                            Long platId, String fileName) {
        // 解析解决冲突方案：
        Map<Long, PerformanceFMConfirmVo.PermanentConfirmSolution> changedMemberMap = new HashMap<>();
        if (changedMembers != null) {
            for (PerformanceFMConfirmVo.PermanentConfirmSolution solution : changedMembers) {
                changedMemberMap.put(solution.getUserId(), solution);
            }
        }

        // 先删除之前所有所有的非固化记录
        List<FmMember> permanentFixedMember = fmMemberMapper.selectAllPermanentFixedMember(platId);
        fmMemberMapper.deleteAllFmMemberByPlatId(platId);
        Map<Long, Long> userIdProjectIdMap = new HashMap<>();
        List<Long> allUserList = new ArrayList<>();

        // 从备份地址读取文件,然后开始解析文件并进行固化操作
        analysisExcelDataByLocal(fileName, platId, userIdProjectIdMap, allUserList);
        Set<Map.Entry<Long, Long>> userIdProjectIdEntry = userIdProjectIdMap.entrySet();

        List<FmMember> fmMemberList = new ArrayList<>();
        // 通过文件与消除冲突信息添加固化成员，并且处理移除的成员
        if (removedMembers != null) {
            for (PerformanceFMConfirmVo.PermanentConfirmSolution solution : removedMembers) {
                int solutionCode = solution.getSolution();
                switch (solutionCode) {
                    case 1: {//保留以前的永久固化
                        FmMember fmMember = new FmMember();
                        fmMember.setPlatId(platId);
                        fmMember.setFixedFlag(true);
                        fmMember.setUserId(solution.getUserId());
                        fmMember.setProjectId(solution.getFromProject());
                        fmMemberList.add(fmMember);
                        break;
                    }
                    default:// 删除此人的固化
                        break;
                }
            }
        }

        // 通过文件与消除冲突信息添加固化成员，并且处理转移组的成员
        for (Map.Entry<Long, Long> userIdProjectId : userIdProjectIdEntry) {
            if (!changedMemberMap.containsKey(userIdProjectId.getKey())) {//不是有冲突情况
                FmMember fmMember = new FmMember();
                fmMember.setUserId(userIdProjectId.getKey());
                FmMember tempFmMember = permanentFixedMember.stream().filter(item -> item.getUserId().equals(userIdProjectId.getKey())).findFirst().orElse(null);
                if (tempFmMember != null) {
                    fmMember.setFixedFlag(true);
                } else {
                    fmMember.setFixedFlag(false);
                }
                fmMember.setPlatId(platId);
                fmMember.setProjectId(userIdProjectId.getValue());
                fmMemberList.add(fmMember);
            } else {//根据冲突解决方案解决冲突
                PerformanceFMConfirmVo.PermanentConfirmSolution solution = changedMemberMap.get(userIdProjectId.getKey());

                int solutionCode = solution.getSolution();
                switch (solutionCode) {
                    case 1: {//永久固化到以前项目
                        FmMember fmMember = new FmMember();
                        fmMember.setPlatId(platId);
                        fmMember.setProjectId(solution.getFromProject());
                        fmMember.setFixedFlag(true);
                        fmMember.setUserId(userIdProjectId.getKey());
                        fmMemberList.add(fmMember);
                        break;
                    }
                    case 2: {//永久固化到当前项目
                        FmMember fmMember = new FmMember();
                        fmMember.setPlatId(platId);
                        fmMember.setProjectId(solution.getToProject());
                        fmMember.setFixedFlag(true);
                        fmMember.setUserId(userIdProjectId.getKey());
                        fmMemberList.add(fmMember);
                        break;
                    }
                    case 3: {//普通固化到当前项目
                        FmMember fmMember = new FmMember();
                        fmMember.setPlatId(platId);
                        fmMember.setProjectId(solution.getToProject());
                        fmMember.setFixedFlag(false);
                        fmMember.setUserId(userIdProjectId.getKey());
                        fmMemberList.add(fmMember);
                        break;
                    }
                    default:
                        break;
                }
            }
        }
        if (fmMemberList.size() != 0) {
            //插入处理结果
            try {
                fmMemberMapper.batchInsert(fmMemberList);
            } catch (Exception e) {
                throw new DataImportException(ErrorCode.FILE_IMPORT_FILE_READ_ERROR, "文件中成员不正确，已经被固化到了其他平台下");
            }
        }
    }

    @Override
    public CommonResponse addFixMember(Long platId, Long projectId, Long userId) {
        CommonResponse response;
        FmMemberDto fmMemberDto = fmMemberMapper.selectByUserId(userId);
        if (fmMemberDto == null) {
            insertFmMember(platId, projectId, userId);
            response = new CommonResponse(0, "success");
        } else {
            String message = "该成员已经存在于" + fmMemberDto.getProjectName() + "-" + fmMemberDto.getPlatName() + "-固化组" +
                    "中。";
            response = new CommonResponse(1, message);
        }
        return response;
    }

    @Override
    public void deleteFixMember(Long platId, Long projectId, Long userId) {
        FmMember fmMember = fmMemberMapper.selectByProjectIdAndPlatId(platId, projectId, userId);
        if (fmMember == null) {
            return;
        }
        Long id = fmMember.getId();
        fmMemberMapper.deleteByPrimaryKey(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean changeProjectConfirmer(Long platId, Long projectId, Long projectManagerId) {
        List<Long> excludedMembers = fmUserRoleMapper.selectAllProjectManagers();
        if (excludedMembers.contains(projectManagerId)) {
            return false;
        }
        FmUserRole fmUserRoleRecord = new FmUserRole();

        //删除固化绩效-项目审核员
        if (projectManagerId.equals(-1L)) {
            fmUserRoleRecord.setProjectId(projectId);
            fmUserRoleRecord.setPlatId(platId);
            fmUserRoleRecord.setRoleId(FmUserRole.Role.managerRole);
            fmUserRoleMapper.deleteSelective(fmUserRoleRecord);
            RUserProjectPerm condition = new RUserProjectPerm();
            condition.setProjectId(projectId);
            condition.setProjectRoleId(ProjectRole.Role.fm_project_confirmer_id);
            rUserProjectPermMapper.deleteSelective(condition);
            return true;
        }

        FmUserRole fmUserRole = fmUserRoleMapper.selectUserRoleByPlatAndProject(platId, projectId);

        fmUserRoleRecord.setPlatId(platId);
        fmUserRoleRecord.setProjectId(projectId);
        fmUserRoleRecord.setUserId(projectManagerId);
        fmUserRoleRecord.setRoleId(FmUserRole.Role.managerRole);

        if (fmUserRole != null) {
            //存在审核员，则更新
            fmUserRoleMapper.updateByPlatAndProject(fmUserRoleRecord);
        } else {
            //不存在审核员，新加入
            fmUserRoleMapper.insertSelective(fmUserRoleRecord);
        }

        List<RUserProjectPerm> rUserProjectPerms = rUserProjectPermMapper.selectByProjectIdAndProjectRoleId(projectId, ProjectRole.Role.fm_project_confirmer_id);
        if (rUserProjectPerms != null && rUserProjectPerms.size() > 0) {
            rUserProjectPermMapper.deleteByProjectIdAndProjectRoleId(projectId, ProjectRole.Role.fm_project_confirmer_id);
        }
        RUserProjectPerm record = new RUserProjectPerm();
        record.setUserId(projectManagerId);
        record.setProjectId(projectId);
        record.setProjectRoleId(ProjectRole.Role.fm_project_confirmer_id);
        rUserProjectPermMapper.insert(record);

        return true;
    }

    @Override
    public boolean createFixGroup(Long projectId, Long platId, Long projectManagerId) {
        List<Long> excludedMembers = fmUserRoleMapper.selectAllProjectManagers();
        if (excludedMembers.contains(projectManagerId)) {
            return false;
        }
        FmUserRole fmUserRole = new FmUserRole();
        fmUserRole.setUserId(projectManagerId);
        fmUserRole.setPlatId(platId);
        fmUserRole.setProjectId(projectId);
        fmUserRole.setRoleId(FmUserRole.Role.managerRole);//项目的审核员
        fmUserRoleMapper.insertSelective(fmUserRole);
        return true;
    }

    @Override
    public List<PerformanceFixPlatVo> getAllPlats(Long projectId) {
        List<PerformanceFixPlatVo> allPlatList = new ArrayList<>();
        List<ProjectVo> allPlats = projectMapper.selectAllPlatWithOrderCodesStrAndUsedNamesStr();
        List<Long> fixedPlatIds = fmUserRoleMapper.selectAllFixGroupPlatIdByProjectId(projectId);
        for (ProjectVo vo : allPlats) {
            PerformanceFixPlatVo plat = new PerformanceFixPlatVo();
            plat.setId(vo.getId());
            plat.setName(vo.getName());
            if (fixedPlatIds.contains(vo.getId())) {
                plat.setRelationFlag(true);
            }
            allPlatList.add(plat);
        }
        return allPlatList;
    }

    @Override
    public void permanentFixMember(Long id) {
        fmMemberMapper.updateFixFlag(id);
    }

    @Override
    public String deleteFixGroup(Long platId, Long projectId) {
        List fmMembers = fmMemberMapper.selectFixMembersByPlatIdAndProjectId(platId, projectId);
        if (fmMembers == null || fmMembers.size() == 0) {
            FmUserRole fmUserRole = new FmUserRole();
            fmUserRole.setProjectId(projectId);
            fmUserRole.setPlatId(platId);
            fmUserRoleMapper.deleteSelective(fmUserRole);
            return "success";
        } else {
            return "固化组删除失败，因为在该固化组下固化了成员";
        }
    }

    @Override
    @Transactional
    public CommonResponse addFixProjectPerm(Long groupId, Long userId, String type) {
        CommonResponse commonResponse;
        FmUserRole recordUser = new FmUserRole();
        switch (type) {
            case "plat": {
                recordUser.setPlatId(groupId);
                recordUser.setRoleId(FmRole.Role.platFixManager);
                break;
            }
            case "project": {
                recordUser.setProjectId(groupId);
                recordUser.setRoleId(FmRole.Role.projectFixManager);
                break;
            }
            default: {
                commonResponse = new CommonResponse(-1, "该组既不是平台也不是项目，请核对");
                return commonResponse;
            }
        }
        recordUser.setUserId(userId);
        FmUserRole selectRole = fmUserRoleMapper.selectSelective(recordUser);
        if (selectRole != null) {
            commonResponse = new CommonResponse(-1, "该记录已存在");
            return commonResponse;
        }

        fmUserRoleMapper.insert(recordUser);

        RUserProjectPerm record = new RUserProjectPerm();
        record.setUserId(userId);
        record.setProjectId(groupId);
        record.setProjectRoleId(ProjectRole.Role.fm_config_manager_id);

        //默认与fmUserRole保持一致
        rUserProjectPermMapper.insert(record);
        commonResponse = new CommonResponse(0, "success");
        return commonResponse;
    }

    @Override
    public void deleteFixProjectPerm(Long id, String type) {
        RUserProjectPerm rUserProjectPerm = rUserProjectPermMapper.selectByPrimaryKey(id);
        rUserProjectPermMapper.deleteByPrimaryKey(id);
        FmUserRole fmUserRole = new FmUserRole();
        fmUserRole.setUserId(rUserProjectPerm.getUserId());

        switch (type) {
            case "plat": {
                fmUserRole.setRoleId(FmUserRole.Role.platFixRole);
                fmUserRole.setPlatId(rUserProjectPerm.getProjectId());
                break;
            }
            case "project": {
                fmUserRole.setRoleId(FmUserRole.Role.projectFixRole);
                fmUserRole.setProjectId(rUserProjectPerm.getProjectId());
                break;
            }
            default:
                return;
        }
        fmUserRoleMapper.deleteSelective(fmUserRole);
    }

    @Override
    public int modifyPerfSubmit(long platId, boolean nowStatus, int year, int month) {
        FmPerfSubmitInfo fmPerfSubmitInfo = fmPerfSubmitInfoMapper.selectByPlatIdAndTime(platId, year, month);
        if (fmPerfSubmitInfo == null) {
            FmPerfSubmitInfo fm = new FmPerfSubmitInfo();
            fm.setYear(year);
            fm.setMonth(month);
            fm.setPerfSubmitFlag(!nowStatus);
            fm.setPlatId(platId);
            fm.setCreateTime(new Date());
            fm.setUpdateTime(new Date());
            return fmPerfSubmitInfoMapper.insertSelective(fm);
        } else {
            return fmPerfSubmitInfoMapper.updatePerfSubmitFlag(platId, year, month, nowStatus);
        }
    }


    private String getFixGroupName(Long platId, Long projectId, List<ProjectVo> projectList) {
        Map<Long, String> idNameMap = new HashMap<>();

        for (Project p : projectList) {
            idNameMap.put(p.getId(), p.getName());
        }
        String platName = idNameMap.get(platId);
        String projectName = idNameMap.get(projectId);
        return projectName + "-" + platName + "-固化组";
    }

    private void insertFmMember(Long platId, Long projectId, Long userId) {
        FmMember fmMember = new FmMember();
        fmMember.setProjectId(projectId);
        fmMember.setPlatId(platId);
        fmMember.setFixedFlag(false);
        fmMember.setUserId(userId);
        fmMemberMapper.insert(fmMember);
    }

    private String backupExcel(MultipartFile file) {
        String path;
        try {
            path = ExcelHelper.saveExcelBackup(file, filePathPrefix + backupExcelUrl);
        } catch (Exception e) {
            throw new DataImportException(ErrorCode.FILE_IMPORT_FILE_READ_ERROR, "文件备份失败");
        }
        return path;
    }

    private String analysisExcelDataByFront(MultipartFile file, Long platId, Map<Long, Long> resultUserIdProjectIdMap, List<Long> allUserList) {

        //前序操作：读取文件
        Workbook wb;
        try {
            wb = WorkbookFactory.create(file.getInputStream());
        } catch (Exception e) {
            throw new DataImportException(ErrorCode.FILE_IMPORT_FILE_READ_ERROR, "文件读取失败");
        }
        return analysisExcelData(wb, platId, resultUserIdProjectIdMap, allUserList);
    }

    private String analysisExcelDataByLocal(String filePath, Long platId, Map<Long, Long> resultUserIdProjectIdMap, List<Long> allUserList) {

        //前序操作：读取文件
        Workbook wb;
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(filePath);
            wb = WorkbookFactory.create(inputStream);
        } catch (Exception e) {
            throw new DataImportException(ErrorCode.FILE_IMPORT_FILE_READ_ERROR, "文件读取失败");
        }
        return analysisExcelData(wb, platId, resultUserIdProjectIdMap, allUserList);
    }

    private String analysisExcelData(Workbook wb, Long platId, Map<Long, Long> resultUserIdProjectIdMap, List<Long> allUserList) {

        //用于计算工作日期

        //将成员和将要固化的组id（解析结果） 存入Map中

        List<String> errorLogs = new ArrayList<>();
        Sheet sheet = wb.getSheetAt(0);
        //建立列和项目ID的映射关系
        List<ProjectVo> projectList = projectMapper.selectAllShareProject();
        Map<Integer, Long> mapColumnProject = new HashMap<>();
        int colIndexEnd = 7;
        Row rowProject = sheet.getRow(0);
        while (true) {
            Cell cell = rowProject.getCell(colIndexEnd);
            String cellText = cell.getStringCellValue();
            //以这一列作为结束
            if ("工作日".equals(cellText)) {
                break;
            }
            if ("平台-公司".equals(cellText)) {
                Long platShareProjectId = ReportHelper.PlatInShareProjectMap.get(platId);
                if (platShareProjectId != null) {
                    mapColumnProject.put(colIndexEnd, platShareProjectId);
                }
                colIndexEnd++;
                continue;
            }

            ProjectVo project = projectList.stream().filter(item -> {
                List<String> projectUsedNames = ExcelHelper.buildProjectUsedNames(item);
                return projectUsedNames.contains(ExcelHelper.trimSpaceAndSpecialSymbol(cellText));
            }).findFirst().orElse(null);
            if (null == project) {
                errorLogs.add("项目名不规范，找不到此项目 ProjectName:" + cellText);
            } else {
                mapColumnProject.put(colIndexEnd, project.getId());
            }
            colIndexEnd++;
        }
        //开始读取每个人的分摊数据
        List<OrgWorkGroupMemberAppVo> userList = userMapper.selectAllEntityWithOrgWorkGroupSimpleByProjectId(platId);
        int rowIndexEnd = 1;
        Double rate = MySystemParamUtils.getSystemConfigWithDefaultValue(MySystemParamUtils.Key.FixMemberRate, MySystemParamUtils.DefaultValue.fixMemberRate);
        while (true) {
            Row rowUserShareData = sheet.getRow(rowIndexEnd++);
            if (rowUserShareData == null) {
                break;
            }
            logger.info("current row is:{}", rowUserShareData.getRowNum());
//            Cell cellEndOfUserShare = rowUserShareData.getCell(5);
//            String cellTextEnd = cellEndOfUserShare.toString();
//            //以这一行做结束行
//            if ("各项目工作日".equals(cellTextEnd)) {
//                break;
//            }


            Cell cellEmployeeNo = rowUserShareData.getCell(0);
            if(cellEmployeeNo ==null || cellEmployeeNo.toString().isEmpty()){
                break;

            }

            Long employeeNo =  new BigDecimal(MyCellUtils.getCellValue(cellEmployeeNo)).longValue();
            Cell cellUserName = rowUserShareData.getCell(1);
            String cellTextUserName = cellUserName.toString();
            if (employeeNo.equals(0L) || StringUtils.isEmpty(cellTextUserName)) {
                errorLogs.add("row: " + (rowUserShareData.getRowNum() + 1) + " 员工编号不存在本平台下 EmployeeNo:" + cellEmployeeNo.toString());
                continue;
            }

            OrgWorkGroupMemberAppVo userInfo = userList.stream().filter(item -> item.getEmployeeNo().equals(employeeNo)).findFirst().orElse(null);
            if (null == userInfo) {
                errorLogs.add("row: " + (rowUserShareData.getRowNum() + 1) + " 员工编号不存在本平台下 EmployeeNo:" + cellEmployeeNo.toString());
            } else {

                allUserList.add(userInfo.getUserId());//将这张excel的所有可以识别的用户加入list中

                float maxDays = 0;
                Long maxProjectId = 0L;
                float totalShareDay = 0f;
                for (int columnIndex = 7; columnIndex <= colIndexEnd; columnIndex++) {
                    if (!mapColumnProject.containsKey(columnIndex)) {//项目名不识别与分配给自己都不计入
                        continue;
                    }
                    Cell cellProjectOfUser = rowUserShareData.getCell(columnIndex);
                    if (cellProjectOfUser == null) {
                        continue;
                    }

                    String cellText = cellProjectOfUser.toString();
                    if (null == cellText || cellText.isEmpty() || cellText.trim().isEmpty() || cellText.equals("-")) {
                        continue;
                    }

                    String regEx = "([0-9]\\d*(\\.\\d*)*)";
                    Pattern pattern = Pattern.compile(regEx);
                    Matcher matcher = pattern.matcher(cellText);
                    if (matcher.matches()) {
                        String firstMatch = matcher.group(0);
                        if (firstMatch.equals(cellText)) {
                            Float shareDay = Float.parseFloat(cellText);
                            totalShareDay = totalShareDay + shareDay;
                            if (shareDay > maxDays) {
                                maxDays = shareDay;
                                maxProjectId = mapColumnProject.get(columnIndex);
                            }
                        }
                    } else {
                        errorLogs.add("row: " + (rowUserShareData.getRowNum() + 1) + "column: " + (columnIndex + 1) + " 是一个特殊字符，被服务器忽略");
                    }
                }
                if (totalShareDay != 0f) {
                    if (maxDays / totalShareDay > rate) {
                        resultUserIdProjectIdMap.put(userInfo.getUserId(), maxProjectId);
                    }
                }
            }
        }

        //保存错误日志
        String fileName = "import-fix-member-error-" + platId + "-" + "-";
        ExcelHelper.saveErrorFile(errorLogs, "fix-import/", fileName);
        if (errorLogs.size() > 0) {
            String downloadURL = backupExcelUrl + "/" + fileName + ".txt";
            MyFileUtils.copyFile(ExcelHelper.ERR_LOG_DIR + "fix-import/" + fileName + ".log", filePathPrefix + downloadURL);
            return downloadURL;
        }
        return "";
    }

    private List<PerformanceFMGroupsVo.Group.Member> sortMembers(List<PerformanceFMGroupsVo.Group.Member> memberList) {
        memberList.sort((x, y) -> {
            if (x.getPermanentFlag().equals(y.getPermanentFlag())) {
                return x.getLoginId().compareTo(y.getLoginId());
            }
            return x.getPermanentFlag() ? -1 : 1;
        });
        return memberList;
    }

    private Boolean getPerfSubmitStatus(long platId, int year, int month) {

        String plats = MySystemParamUtils.getSystemConfigWithDefaultValue(MySystemParamUtils.Key.FmPerfSubmitBarrierPlats, MySystemParamUtils.DefaultValue.FmPerfSubmitBarrierPlats);

        // 系统参数未配置，不使用固化绩效通道功能
        if (!plats.contains(String.valueOf(platId))) {
            return null;
        }

        // 默认是通道关闭的
        Boolean perfSubmitFlag = fmPerfSubmitInfoMapper.getPerfSubmitFlag(platId, year, month);
        if (perfSubmitFlag == null) {
            return false;
        }
        return perfSubmitFlag;
    }
}
