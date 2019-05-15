package com.seasun.management.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.seasun.management.config.DataSourceContextHolder;
import com.seasun.management.dto.PerformanceUserDto;
import com.seasun.management.dto.UserPerformanceDto;
import com.seasun.management.dto.WorkGroupDto;
import com.seasun.management.exception.ParamException;
import com.seasun.management.helper.ExcelHelper;
import com.seasun.management.mapper.*;
import com.seasun.management.mapper.dsp.BankCardMapper;
import com.seasun.management.model.*;
import com.seasun.management.model.dsp.BankCard;
import com.seasun.management.service.OperateLogService;
import com.seasun.management.service.TestService;
import com.seasun.management.service.app.HrReportService;
import com.seasun.management.service.performanceCore.userPerformance.PerformanceTreeHelper;
import com.seasun.management.util.MyEncryptorUtils;
import com.seasun.management.util.MyTokenUtils;
import com.seasun.management.vo.ContactsAppVo;
import com.seasun.management.vo.OrgWorkGroupMemberAppVo;
import com.seasun.management.vo.ProjectVo;
import com.seasun.management.vo.UserCountCheckVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

import static com.seasun.management.vo.UserCountCheckVo.*;

@Service
public class TestServiceImpl implements TestService {

    @Autowired
    UserPerformanceMapper userPerformanceMapper;

    @Autowired
    UserSalaryChangeMapper userSalaryChangeMapper;

    @Autowired
    OperateLogService operateLogService;

    @Autowired
    WorkGroupMapper workGroupMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    PerformanceWorkGroupMapper performanceWorkGroupMapper;

    @Autowired
    FmGroupConfirmInfoMapper fmGroupConfirmInfoMapper;

    @Autowired
    ProjectMapper projectMapper;

    @Autowired
    FnPlatShareConfigMapper fnPlatShareConfigMapper;

    @Autowired
    FnProjectSchedDataMapper fnProjectSchedDataMapper;

    @Autowired
    FnProjectStatDataMapper fnProjectStatDataMapper;

    @Autowired
    AReprProjSchedDataMapper aReprProjSchedDataMapper;

    @Autowired
    AReprShareDataMapper aReprShareDataMapper;

    @Autowired
    AReprProjDataMapper aReprProjDataMapper;

    @Autowired
    HrReportService hrReportService;

    @Autowired
    BankCardMapper bankCardMapper;

    @Autowired
    private PerfWorkGroupStatusMapper perfWorkGroupStatusMapper;

    private static final Logger logger = LoggerFactory.getLogger(TestServiceImpl.class);

    @Override
    public void cleanUserSalaryChange(int year, int quarter) {
        userSalaryChangeMapper.deleteByYearAndQuarter(year, quarter);
        operateLogService.add("test-clean-salary", "清除了" + year + "年-" + quarter +
                "季度所有的调薪数据", MyTokenUtils.getCurrentUserId());
    }

    @Override
    public String testSqlServer() {
        DataSourceContextHolder.useFourth();
        List<BankCard> allBankCards = bankCardMapper.selectAll();
        logger.info("result size :"+allBankCards.size());
        logger.info("first one is :{}", allBankCards.get(0).getName());
        DataSourceContextHolder.usePrimary();
        return allBankCards.get(0).getName();
    }

    @Override
    public void cleanUserPerformance(int year, int month) {
        userPerformanceMapper.deleteByYearAndMonth(year, month);
        fmGroupConfirmInfoMapper.deleteByYearAndMonth(year, month);
        perfWorkGroupStatusMapper.deleteByYearAndMonth(year,month);
        operateLogService.add("test-clean-performance", "清除了" + year + "年-" + month +
                "月所有的绩效数据", MyTokenUtils.getCurrentUserId());
    }

    @Transactional
    @Override
    public void batchCreateUserPerformance(Integer year, Integer month, Integer quarter) {
        Long workGroupId = null;

        List<PerformanceUserDto> allUsers = userMapper.selectAllEntityWithPerformanceByPerformanceWorkGroup();
        List<WorkGroupDto> allPerformanceGroups = performanceWorkGroupMapper.selectAllByActive();
        for (WorkGroupDto workGroup : allPerformanceGroups) {
            if ("西山居".equals(workGroup.getName())) {
                workGroupId = workGroup.getId();
                break;
            }
        }
        if (null == workGroupId) {
            throw new ParamException("西山居根组没有配置");
        }

        // 找出根目录，初始化树
        WorkGroupDto performanceRootGroup = PerformanceTreeHelper.getWorkGroupTree(workGroupId, allPerformanceGroups, allUsers, true);

        // 获取所有下属
        List<PerformanceUserDto> allMembers = PerformanceTreeHelper.getAllMembersByRootWorkGroup(performanceRootGroup);

        if (null != quarter && quarter > 0) {
            List<UserPerformanceDto> allUserPerformanceDtos = new ArrayList<>();
            int qMonth = quarter == 1 ? quarter : (quarter - 1) * 3 + 1;
            List<UserPerformanceDto> userPerformanceDtos = getUserPerformanceList(year, qMonth, allMembers);
            List<PerfWorkGroupStatus> perfWorkGroupStatuses = new ArrayList<>();
            userPerformanceMapper.deleteByYearAndMonth(year, qMonth);
            allUserPerformanceDtos.addAll(userPerformanceDtos);
            qMonth++;
            while (qMonth <= quarter * 3) {
                perfWorkGroupStatusMapper.deleteByYearAndMonth(year, qMonth);
                userPerformanceMapper.deleteByYearAndMonth(year, qMonth);
                for (UserPerformanceDto userPerformanceDto : userPerformanceDtos) {
                    UserPerformanceDto newUserPerformance = new UserPerformanceDto();
                    newUserPerformance.setYear(year);
                    newUserPerformance.setMonth(qMonth);
                    newUserPerformance.setUserId(userPerformanceDto.getUserId());
                    newUserPerformance.setWorkGroupId(userPerformanceDto.getWorkGroupId());
                    newUserPerformance.setSubGroup(userPerformanceDto.getSubGroup());
                    newUserPerformance.setParentGroup(userPerformanceDto.getParentGroup());
                    newUserPerformance.setFinalPerformance(userPerformanceDto.getFinalPerformance());
                    newUserPerformance.setStatus(userPerformanceDto.getStatus());
                    newUserPerformance.setCreateTime(userPerformanceDto.getCreateTime());
                    newUserPerformance.setUpdateTime(userPerformanceDto.getUpdateTime());
                    newUserPerformance.setWorkAge(userPerformanceDto.getWorkAge());
                    newUserPerformance.setWorkAgeInKs(userPerformanceDto.getWorkAgeInKs());
                    newUserPerformance.setPost(userPerformanceDto.getPost());
                    newUserPerformance.setWorkGroupName(userPerformanceDto.getWorkGroupName());
                    newUserPerformance.setWorkStatus(userPerformanceDto.getWorkStatus());
                    allUserPerformanceDtos.add(newUserPerformance);
                }
                for (WorkGroupDto allPerformanceGroup : allPerformanceGroups) {
                    PerfWorkGroupStatus perfWorkGroupStatus = new PerfWorkGroupStatus();
                    perfWorkGroupStatus.setPerfWorkGroupId(allPerformanceGroup.getId());
                    perfWorkGroupStatus.setStatus(UserPerformance.Status.complete);
                    perfWorkGroupStatus.setYear(year);
                    perfWorkGroupStatus.setMonth(qMonth);
                    perfWorkGroupStatus.setCreateTime(new Date());
                    perfWorkGroupStatus.setUpdateTime(new Date());
                    perfWorkGroupStatus.setOperateId(MyTokenUtils.ADMIN_ID);
                    perfWorkGroupStatuses.add(perfWorkGroupStatus);
                }
                qMonth++;
            }
            perfWorkGroupStatusMapper.batchInsert(perfWorkGroupStatuses);
            userPerformanceMapper.batchInsertByLeaderSubmit(allUserPerformanceDtos);
            operateLogService.add("test-create-performance", "生成了" + year + "年-" + quarter +
                    "季度所有的绩效数据", MyTokenUtils.getCurrentUserId());
        } else {
            List<UserPerformanceDto> userPerformanceDtos = getUserPerformanceList(year, month, allMembers);
            List<PerfWorkGroupStatus> perfWorkGroupStatuses = new ArrayList<>();

            for (WorkGroupDto allPerformanceGroup : allPerformanceGroups) {
                PerfWorkGroupStatus perfWorkGroupStatus = new PerfWorkGroupStatus();
                perfWorkGroupStatus.setPerfWorkGroupId(allPerformanceGroup.getId());
                perfWorkGroupStatus.setStatus(UserPerformance.Status.complete);
                perfWorkGroupStatus.setYear(year);
                perfWorkGroupStatus.setMonth(month);
                perfWorkGroupStatus.setCreateTime(new Date());
                perfWorkGroupStatus.setUpdateTime(new Date());
                perfWorkGroupStatus.setOperateId(MyTokenUtils.ADMIN_ID);
                perfWorkGroupStatuses.add(perfWorkGroupStatus);
            }
            userPerformanceMapper.deleteByYearAndMonth(year, month);
            perfWorkGroupStatusMapper.deleteByYearAndMonth(year, month);
            perfWorkGroupStatusMapper.batchInsert(perfWorkGroupStatuses);
            userPerformanceMapper.batchInsertByLeaderSubmit(userPerformanceDtos);
            operateLogService.add("test-create-performance", "生成了" + year + "年-" + month +
                    "月所有的绩效数据", MyTokenUtils.getCurrentUserId());
        }
    }

    @Override
    public void syncFixedOutnumber(JSONObject params) {

        Integer year = params.getInteger("year");
        Integer month = params.getInteger("month");

        // 1. 获取老数据的projectId和platId，准备数据
        List<ProjectVo> activeProjects = projectMapper.selectAllActiveProject();


        // 不传年月，则处理所有
        List<AReprShareData> aReprShareDatas;
        if (year != null && month != null) {
            aReprShareDatas = aReprShareDataMapper.selectByYearAndMonth(year, month);
        } else {
            aReprShareDatas = aReprShareDataMapper.selectAll();
        }

        Map<String, Long> projectNameIdMap = new HashMap<>();
        Map<String, Long> platNameIdMap = new HashMap<>();
        List<String> invalidProjects = new ArrayList<>();
        List<String> invalidPlats = new ArrayList<>();
        List<FnPlatShareConfig> syncData = new ArrayList<>();
        for (AReprShareData aReprShareData : aReprShareDatas) {

            boolean validProjectFlag = false;
            boolean validplatFlag = false;
            for (ProjectVo projectVo : activeProjects) {
                if (!validProjectFlag && ExcelHelper.buildProjectUsedNames(projectVo).contains(aReprShareData.getProjectName())) {
                    projectNameIdMap.put(aReprShareData.getProjectName(), projectVo.getId());
                    validProjectFlag = true;
                }

                // 为对齐分摊项，该“西山居技术中心”需要重置为“珠海技术中心”
                if (aReprShareData.getPlatName().equals("西山居技术中心")) {
                    aReprShareData.setPlatName("技术中心");
                }
                if (!validplatFlag && ExcelHelper.buildProjectUsedNames(projectVo).contains(aReprShareData.getPlatName())) {
                    platNameIdMap.put(aReprShareData.getPlatName(), projectVo.getId());
                    validplatFlag = true;
                }
            }
            if (!validProjectFlag && !invalidProjects.contains(aReprShareData.getProjectName())) {
                invalidProjects.add(aReprShareData.getProjectName());
            }
            if (!validplatFlag && !invalidPlats.contains(aReprShareData.getPlatName())) {
                invalidPlats.add(aReprShareData.getPlatName());
            }
            if (validplatFlag && validProjectFlag) {
                FnPlatShareConfig fnPlatShareConfig = new FnPlatShareConfig();
                fnPlatShareConfig.setProjectId(projectNameIdMap.get(aReprShareData.getProjectName()));
                fnPlatShareConfig.setPlatId(platNameIdMap.get(aReprShareData.getPlatName()));
                fnPlatShareConfig.setCreateTime(aReprShareData.getCreateTime());
                fnPlatShareConfig.setUpdateTime(aReprShareData.getUpdateTime());
                fnPlatShareConfig.setFixedNumber(new BigDecimal(aReprShareData.getFixedOutnumber()));
                fnPlatShareConfig.setCreateBy(1101L);
                fnPlatShareConfig.setMonth(aReprShareData.getMonth());
                fnPlatShareConfig.setYear(aReprShareData.getYear());
                syncData.add(fnPlatShareConfig);
            }
        }
        // 2. 删除fn_plat_share_config表,（打印找不到项目或平台归属的id）
        ExcelHelper.saveErrorFile(invalidProjects, "sync-fixed-number/", "invalid_project");
        ExcelHelper.saveErrorFile(invalidPlats, "sync-fixed-number/", "invalid_plat");

        // 3. 插入fn_plat_share_config表数据
        if (year != null && month != null) {
            fnPlatShareConfigMapper.deleteByYearAndMonth(year, month);
        } else {
            fnPlatShareConfigMapper.deleteAll();
        }
        fnPlatShareConfigMapper.batchInsert(syncData);
    }

    @Override
    public void syncSchedData(JSONObject params) {

        Integer year = params.getInteger("year");
        Integer month = params.getInteger("month");

        // 1. 获取对应的projectId，准备数据
        Map<String, Long> projectNameIdMap = new HashMap<>();
        List<String> invalidProjects = new ArrayList<>();
        List<FnProjectSchedData> syncData = new ArrayList<>();

        // 不传年月，则处理所有
        List<AReprProjSchedData> fnProjectSchedDatas;
        if (year != null && month != null) {
            fnProjectSchedDatas = aReprProjSchedDataMapper.selectByYearAndMonth(year, month);
        } else {
            fnProjectSchedDatas = aReprProjSchedDataMapper.selectAll();
        }

        List<ProjectVo> activeProjects = projectMapper.selectAllActiveProject();

        for (AReprProjSchedData aReprProjSchedData : fnProjectSchedDatas) {
            boolean validProjectFlag = false;
            for (ProjectVo projectVo : activeProjects) {
                if (!validProjectFlag && ExcelHelper.buildProjectUsedNames(projectVo).contains(aReprProjSchedData.getProjectName())) {
                    projectNameIdMap.put(aReprProjSchedData.getProjectName(), projectVo.getId());
                    validProjectFlag = true;
                }
            }
            if (!validProjectFlag && !invalidProjects.contains(aReprProjSchedData.getProjectName())) {
                invalidProjects.add(aReprProjSchedData.getProjectName());
            }
            if (validProjectFlag) {
                FnProjectSchedData fnProjectSchedData = new FnProjectSchedData();
                fnProjectSchedData.setProjectId(projectNameIdMap.get(aReprProjSchedData.getProjectName()));
                fnProjectSchedData.setYear(aReprProjSchedData.getYear());
                fnProjectSchedData.setMonth(aReprProjSchedData.getMonth());
                fnProjectSchedData.setCreateTime(aReprProjSchedData.getCreateTime());
                fnProjectSchedData.setUpdateTime(aReprProjSchedData.getUpdateTime());
                fnProjectSchedData.setExpectIncome(aReprProjSchedData.getAnticipatedRevenue());
                fnProjectSchedData.setHumanNumber(aReprProjSchedData.getHrNumbers());
                fnProjectSchedData.setTotalCost(aReprProjSchedData.getTotalCost());
                syncData.add(fnProjectSchedData);
            }
        }

        // 2. 打印找不到项目归属的id
        ExcelHelper.saveErrorFile(invalidProjects, "sync-sched-data/", "invalid_project");

        // 3. 插入fn_plat_share_config表数据
        if (year != null && month != null) {
            fnProjectSchedDataMapper.deleteByYearAndMonth(year, month);
        } else {
            fnProjectSchedDataMapper.deleteAll();
        }
        fnProjectSchedDataMapper.batchInsert(syncData);
    }

    @Override
    public void syncOutsourcingData(JSONObject params) {
        Integer year = params.getInteger("year");
        Integer month = params.getInteger("month");

        // 1. 获取对应的projectId，准备数据
        Map<String, Long> projectNameIdMap = new HashMap<>();
        List<String> invalidProjects = new ArrayList<>();
        List<FnProjectStatData> syncData = new ArrayList<>();

        // 不传年月，则处理所有
        List<AReprProjData> outsourcingData;
        if (year != null && month != null) {
            outsourcingData = aReprProjDataMapper.selectOutsourcingByYearAndMonth(year, month);
        } else {
            outsourcingData = aReprProjDataMapper.selectAllOutsourcing();
        }

        List<ProjectVo> activeProjects = projectMapper.selectAllActiveProject();

        for (AReprProjData aReprProjData : outsourcingData) {
            boolean validProjectFlag = false;
            for (ProjectVo projectVo : activeProjects) {
                if (!validProjectFlag && ExcelHelper.buildProjectUsedNames(projectVo).contains(aReprProjData.getProjectName())) {
                    validProjectFlag = true;
                    projectNameIdMap.put(aReprProjData.getProjectName(), projectVo.getId());
                }
            }
            if (!validProjectFlag && !invalidProjects.contains(aReprProjData.getProjectName())) {
                invalidProjects.add(aReprProjData.getProjectName());
            }
            if (validProjectFlag) {
                FnProjectStatData fnProjectStatData = new FnProjectStatData();
                fnProjectStatData.setFnStatId(3700L);
                fnProjectStatData.setProjectId(projectNameIdMap.get(aReprProjData.getProjectName()));
                fnProjectStatData.setValue(aReprProjData.getValue());
                fnProjectStatData.setYear(aReprProjData.getYear());
                fnProjectStatData.setMonth(aReprProjData.getMonth());
                fnProjectStatData.setCreateTime(aReprProjData.getCreateTime());
                fnProjectStatData.setUpdateTime(aReprProjData.getUpdateTime());
                syncData.add(fnProjectStatData);
            }
        }

        // 2. 删除fn_proj_stat_data表的外包数据（stat_id=3700）,（打印找不到项目归属的id）
        if (year != null && month != null) {
            fnProjectStatDataMapper.deleteOutsourcingDataByYearAndMonth(year, month);
        } else {
            fnProjectStatDataMapper.deleteAllOutsourcingData();
        }
        ExcelHelper.saveErrorFile(invalidProjects, "sync-outsourcing-data/", "invalid_project");

        // 3. 插入数据
        fnProjectStatDataMapper.batchInsert(syncData);
    }

    @Override
    public UserCountCheckVo checkHrUserCount(String city, Long workGroupId) {
        if (city == null) {
            city = "珠海";
        }

        if (workGroupId == null) {
            workGroupId = 1108L; // 1108为工作组：珠海
        }

        List<User> hrUsers = userMapper.selectHrUserByCity(city); // 需要查询的人力地区
        ContactsAppVo contactsWorkGroupVo = hrReportService.getContacts(workGroupId); // 需要查询的工作组根节点

        List<UserCountCheckVo.CheckUser> userNotInWorkGroupList = new ArrayList<>();
        for (User user : hrUsers) {
            if (!contactsWorkGroupVo.getMembers().stream().anyMatch(m -> m.getLoginId().equals(user.getLoginId()))) {
                UserCountCheckVo.CheckUser tmp = new UserCountCheckVo.CheckUser();
                tmp.setLoginId(user.getLoginId());
                userNotInWorkGroupList.add(tmp);
            }
        }

        List<CheckUser> userNotInHrList = new ArrayList<>();
        for (OrgWorkGroupMemberAppVo user : contactsWorkGroupVo.getMembers()) {
            if (!hrUsers.stream().anyMatch(m -> m.getLoginId().equals(user.getLoginId()))) {
                UserCountCheckVo.CheckUser tmp = new UserCountCheckVo.CheckUser();
                tmp.setLoginId(user.getLoginId());
                userNotInHrList.add(tmp);
            }
        }

        UserCountCheckVo userCountCheckVo = new UserCountCheckVo();
        userCountCheckVo.setCityHrUserCount(hrUsers.size());
        userCountCheckVo.setCityWorkGroupUserCount(contactsWorkGroupVo.getMembers().size());
        userCountCheckVo.setNotInHrList(userNotInHrList);
        userCountCheckVo.setNotInWorkgroupList(userNotInWorkGroupList);
        return userCountCheckVo;
    }

    @Override
    public List<Long> getXimiUsers() {
        List<Long> result = new ArrayList<>(1000);
        List<User> allUser = userMapper.selectAll();
        try {
            for (User user : allUser) {
                String email = user.getEmail();
                if (user.getEmail() != null) {
                    email = MyEncryptorUtils.decryptByAES(email);
                }
                if (email != null && email.endsWith("@ximigame.com")) {
                    result.add(user.getId());
                }

            }
        } catch (Exception e) {
            logger.error("email解密失败");
        }

        return result;
    }

    private List<UserPerformanceDto> getUserPerformanceList(int year, int month, List<PerformanceUserDto> allUsers) {
        int total = allUsers.size();
        int maxS = Math.round(total * 0.2F);
        int minC = Math.round(total * 0.1F);
        int s = 0, a = 0, b = 0, c = 0;

        Collections.shuffle(allUsers);
        List<UserPerformanceDto> userPerformanceDtos = new ArrayList<>();
        for (PerformanceUserDto user : allUsers) {
            UserPerformanceDto userPerformanceDto = new UserPerformanceDto();
            userPerformanceDto.setYear(year);
            userPerformanceDto.setMonth(month);
            userPerformanceDto.setUserId(user.getUserId());
            userPerformanceDto.setWorkGroupId(user.getWorkGroupId());
            userPerformanceDto.setParentGroup(user.getParentWorkGroupId());
            String finalPerformance = "B";
            if (c <= minC) {
                finalPerformance = "C";
                c++;
            } else if (s < maxS) {
                finalPerformance = "S";
                s++;
            } else if (a < maxS) {
                finalPerformance = "A";
                a++;
            }
            userPerformanceDto.setFinalPerformance(finalPerformance);
            userPerformanceDto.setStatus(UserPerformance.Status.complete);
            userPerformanceDto.setCreateTime(new Date());
            userPerformanceDto.setUpdateTime(new Date());

            userPerformanceDto.setWorkAge(user.getWorkAge());
            userPerformanceDto.setWorkAgeInKs(user.getWorkAgeInKs());
            userPerformanceDto.setPost(user.getPost());
            userPerformanceDto.setWorkGroupName(user.getWorkGroupName());
            userPerformanceDto.setWorkStatus(user.getWorkStatus());

            userPerformanceDtos.add(userPerformanceDto);
        }
        return userPerformanceDtos;
    }
}
