package com.seasun.management.service.performanceCore.userSalaryChange;

import com.seasun.management.dto.UserSalaryChangeDto;
import com.seasun.management.dto.WorkGroupDto;
import com.seasun.management.dto.WorkGroupUserDto;
import com.seasun.management.exception.UserSalaryChangeException;
import com.seasun.management.helper.UserSalaryHelper;
import com.seasun.management.mapper.*;
import com.seasun.management.model.*;
import com.seasun.management.service.OperateLogService;
import com.seasun.management.service.UserSalaryChangeService;
import com.seasun.management.service.UserSalaryService;
import com.seasun.management.service.WorkGroupService;
import com.seasun.management.service.performanceCore.groupTrack.GroupTrackService;
import com.seasun.management.service.performanceCore.historyTrack.HistoryTrackService;
import com.seasun.management.util.*;
import com.seasun.management.vo.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.seasun.management.service.performanceCore.userSalaryChange.UserSalaryChangeHelper.*;

@Service
public class UserSalaryChangeServiceImpl implements UserSalaryChangeService {

    private static final Logger logger = LoggerFactory.getLogger(UserSalaryChangeServiceImpl.class);

    @Autowired
    private UserSalaryService userSalaryService;

    @Value("${export.excel.path}")
    private String exportExcelPath;

    @Value("${file.sys.prefix}")
    private String filePathPrefix;

    @Autowired
    private UserSalaryChangeMapper userSalaryChangeMapper;

    @Autowired
    private WorkGroupService workGroupService;

    @Autowired
    private WorkGroupMapper workGroupMapper;

    @Autowired
    private HistoryTrackService<OrdinateSalaryChangeAppVo> salaryHistoryTrackService;

    @Autowired
    private GroupTrackService groupTrackService;


    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OperateLogService operateLogService;

    @Autowired
    private UserGradeChangeMapper userGradeChangeMapper;

    @Autowired
    PerformanceWorkGroupMapper performanceWorkGroupMapper;

    @Autowired
    UserSalaryChangeDao userSalaryChangeDao;


    @Override
    public String downloadSalaryFile(int year, int quarter, String[] columns) {

        List<UserSalaryChangeDto> userSalaryChangeList;

        //1. 从数据库获取导出列表

        //如果为系统管理员登陆
        if (MyTokenUtils.isAdmin()) {
            userSalaryChangeList = userSalaryChangeMapper.selectAllSalaryChangeList(year, quarter);
        }

        //普通可视组长登陆情况
        else {
            Long userId = MyTokenUtils.getCurrentUserId();
            List<Long> allGroupIdList = getAllGroupIdList(userId);
            userSalaryChangeList = userSalaryChangeMapper.selectSalaryChangeList(year, allGroupIdList, quarter);
        }

        //2. 生成excel文件
        String exportFilePath = exportExcelPath + File.separator + year + "_" + quarter + "_人员工资数据.xls";
        String exportFileDiskPath = filePathPrefix + exportFilePath;

        Workbook wb = new HSSFWorkbook();
        Sheet sheet = wb.createSheet();
        Map<String, String> columnHeaderMap = UserSalaryHelper.getColumnHeader();
        //初始化首行
        Row firstRow = sheet.createRow(0);
        for (int i = 0; i < columns.length; i++) {
            Cell cell = firstRow.createCell(i);
            cell.setCellValue(columnHeaderMap.get(columns[i]));
        }
        //填充剩余行
        for (int i = 0; i < userSalaryChangeList.size(); i++) {
            UserSalaryChangeDto userSalaryChangeVo = userSalaryChangeList.get(i);
            Row row = sheet.createRow(i + 1);
            for (int j = 0; j < columns.length; j++) {
                Cell cell = row.createCell(j);
                Object val = null;
                try {
                    Method m = userSalaryChangeVo.getClass().getMethod(
                            "get" + MyStringUtils.convertFirstCharToUppercase(columns[j]));
                    val = m.invoke(userSalaryChangeVo);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String valStr = val == null ? "" : val.toString();
                cell.setCellValue(valStr);
            }
        }
        //写文件
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(exportFileDiskPath);
            wb.write(fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return exportFilePath;
    }

    @Override
    public UserSalary getUserCurrentSalaryInfo(Long userId) {

        // 生产环境 && 测试模式 && 测试管理员, 则会读真实数据
        Boolean performanceTestModeFlag = MySystemParamUtils.getSystemConfigWithDefaultValue(MySystemParamUtils.Key.TestModeFlag, MySystemParamUtils.DefaultValue.TestModeFlag);
        if (MyEnvUtils.isProdEnv() && performanceTestModeFlag && userId.equals(1000L)) {
            UserSalary result = userSalaryService.getUserSalaryByUserId(userId);
            try {
                result.setLastSalaryChangeAmount(MyEncryptorUtils.decryptByAES(result.getLastSalaryChangeAmount()));
                result.setLastSalaryChangeDate(MyEncryptorUtils.decryptByAES(result.getLastSalaryChangeDate()));
                result.setSalary(MyEncryptorUtils.decryptByAES(result.getSalary()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        // 否则,根据userId模拟薪资系统的返回值
        UserSalary salary = new UserSalary();
        salary.setUserId(userId);
        salary.setSalary(String.valueOf(userId.intValue() * 10));
        salary.setLastSalaryChangeAmount(String.valueOf(userId.intValue() + 100));
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = dateFormat.parse("2015-04-09");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DATE, userId.intValue() / 10);
            salary.setLastSalaryChangeDate(String.valueOf(calendar.getTime()));
            salary.setCreateTime(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return salary;
    }

    private List<Long> getAllGroupIdList(Long userId) {
        Set<Long> allGroupIdSet = new HashSet<>();
        List<Long> groupIdList = workGroupMapper.selectDirectSalaryLegalWorkGroup(userId);
        allGroupIdSet.addAll(groupIdList);//加入所有一级WorkGroup

        List<WorkGroup> workGroup = workGroupMapper.selectAllIdParentWorkGroup();
        for (int i = 0; i < groupIdList.size(); i++) {
            List<Long> partGroupIdList = workGroupService.getWorkGroupRelationByParent(groupIdList.get(i), workGroup);
            if (partGroupIdList != null)
                allGroupIdSet.addAll(partGroupIdList);
        }
        List<Long> allGroupIdList = new ArrayList(allGroupIdSet);
        return allGroupIdList;
    }

    @Override
    public SubordinateSalaryChangeAppVo getSubSalaryChangeWeb(Long workGroupId, Integer year, Integer quarter) {
        SubordinateSalaryChangeAppVo subordinateSalaryChangeWebVo = new SubordinateSalaryChangeAppVo();
        List<SubordinateSalaryChangeAppVo.GroupStatus> existGroupStatus = userSalaryChangeDao.getAllGroupStatus(workGroupId);
        subordinateSalaryChangeWebVo.setHistory(existGroupStatus);
        boolean isHistoryFlag = false;
        for (SubordinateSalaryChangeAppVo.GroupStatus groupStatus : existGroupStatus) {
            if (groupStatus.getYear().equals(year)) {
                if (groupStatus.getQuarter().equals(quarter)) {
                    subordinateSalaryChangeWebVo.setGroupStatus(groupStatus.getStatus());
                    if (SubordinateSalaryChangeAppVo.GroupStatus.StatusVo.finished.equals(groupStatus.getStatus())) {
                        isHistoryFlag = true;
                    }
                }
            }
        }
        if (!isHistoryFlag) {
            logger.info("year and quarter group status is underway...web");
            WorkGroupDto rootGroup = new WorkGroupDto();
            rootGroup.setId(workGroupId);
            List<WorkGroupDto> allGroups = workGroupMapper.selectAllWithLeaderByGroupRoleId(WorkGroupRole.Role.salary);
            if (allGroups == null || allGroups.size() == 0) {
                subordinateSalaryChangeWebVo.setHistory(new ArrayList<>());
                subordinateSalaryChangeWebVo.setProfile(null);
                subordinateSalaryChangeWebVo.setLeaderSalaryChangeList(new ArrayList<>());
                subordinateSalaryChangeWebVo.setSubGroup(null);
                logger.info("extreme case ,all groups is null !...web");
                return subordinateSalaryChangeWebVo;
            }
            for (WorkGroupDto workGroup : allGroups) {
                if (workGroup.getId().equals(workGroupId)) {
                    rootGroup.setLeaderId(workGroup.getLeaderId());
                }
            }
            List<OrdinateSalaryChangeAppVo> ordinateSalaryChangeList = userSalaryChangeMapper.getQuarterRangePerformance(year, quarter);
            performanceResultFilled(ordinateSalaryChangeList);
            groupTrackService.initHrGroupTreeByRootGroup(rootGroup, allGroups, ordinateSalaryChangeList);
            List<OrdinateSalaryChangeAppVo> directList = groupTrackService.getDirectMember(rootGroup);
            userSalaryChangeDao.getRealTimeSalaryChangeList(directList, subordinateSalaryChangeWebVo, allGroups, year, quarter, true);
        } else {
            logger.info("choose old year and quarter's user salary information...");
            List<OrdinateSalaryChangeAppVo> historyDirectOrdinate = userSalaryChangeDao.getHistoryDirectOrdinate(workGroupId, year, quarter);
            performanceResultFilled(historyDirectOrdinate);
            SubordinateSalaryChangeAppVo.SubGroup subGroup = userSalaryChangeDao.getHistorySubGroup(historyDirectOrdinate, year, quarter, true);
            SubordinateSalaryChangeAppVo.Profile profile = getProfile(historyDirectOrdinate, subGroup);
            subordinateSalaryChangeWebVo.setLeaderSalaryChangeList(performanceResultListClassifySort(historyDirectOrdinate));
            //直接下属块
            subordinateSalaryChangeWebVo.setSubGroup(subGroup);
            subordinateSalaryChangeWebVo.setProfile(profile);
        }
        return subordinateSalaryChangeWebVo;
    }

    //app impl

    @Override
    public SubordinateSalaryChangeAppVo getSubSalaryChange(Long workGroupId, Integer year, Integer quarter) {
        SubordinateSalaryChangeAppVo subordinateSalaryChangeAppVo = new SubordinateSalaryChangeAppVo();

        List<SubordinateSalaryChangeAppVo.GroupStatus> existGroupStatus = userSalaryChangeDao.getAllGroupStatus(workGroupId);
        subordinateSalaryChangeAppVo.setHistory(existGroupStatus);
        boolean isHistoryFlag = false;
        boolean submitFlag = true;
        for (SubordinateSalaryChangeAppVo.GroupStatus groupStatus : existGroupStatus) {
            if (groupStatus.getYear().equals(year)) {
                if (groupStatus.getQuarter().equals(quarter)) {
                    if (SubordinateSalaryChangeAppVo.GroupStatus.StatusVo.finished.equals(groupStatus.getStatus())) {
                        isHistoryFlag = true;
                        submitFlag = false;
                    }
                }
            }
        }
        if (!isHistoryFlag) {
            logger.info("year and quarter group status is underway...");
            WorkGroupDto rootGroup = new WorkGroupDto();
            rootGroup.setId(workGroupId);
            List<WorkGroupDto> allGroups = workGroupMapper.selectAllWithLeaderByGroupRoleId(WorkGroupRole.Role.salary);
            if (allGroups == null || allGroups.size() == 0) {
                subordinateSalaryChangeAppVo.setHistory(new ArrayList<>());
                subordinateSalaryChangeAppVo.setProfile(null);
                subordinateSalaryChangeAppVo.setLeaderSalaryChangeList(new ArrayList<>());
                subordinateSalaryChangeAppVo.setSubGroup(null);
                logger.info("extreme case ,all groups is null !");
                return subordinateSalaryChangeAppVo;
            }
            for (WorkGroupDto workGroup : allGroups) {
                if (workGroup.getId().equals(workGroupId)) {
                    rootGroup.setLeaderId(workGroup.getLeaderId());
                }
            }
            List<OrdinateSalaryChangeAppVo> ordinateSalaryChangeList = userSalaryChangeMapper.getQuarterRangePerformance(year, quarter);
            performanceResultFilled(ordinateSalaryChangeList);
            groupTrackService.initHrGroupTreeByRootGroup(rootGroup, allGroups, ordinateSalaryChangeList);
//            initSalaryModelTree(rootGroup,year,quarter,workGroupId);

            List<OrdinateSalaryChangeAppVo> directList = groupTrackService.getDirectMember(rootGroup);
//            List<WorkGroupDto> allGroups = workGroupMapper.selectAllWithLeaderByGroupRoleId(WorkGroupRole.Role.salary);

            userSalaryChangeDao.getRealTimeSalaryChangeList(directList, subordinateSalaryChangeAppVo, allGroups, year, quarter, false);
            subordinateSalaryChangeAppVo.setSubmitFlag(submitFlag);
        } else {
            logger.info("choose old year and quarter's user salary information...");
            List<OrdinateSalaryChangeAppVo> historyDirectOrdinate = userSalaryChangeDao.getHistoryDirectOrdinate(workGroupId, year, quarter);
            performanceResultFilled(historyDirectOrdinate);
            SubordinateSalaryChangeAppVo.SubGroup subGroup = userSalaryChangeDao.getHistorySubGroup(historyDirectOrdinate, year, quarter, false);
            SubordinateSalaryChangeAppVo.Profile profile = getProfile(historyDirectOrdinate, subGroup);
            subordinateSalaryChangeAppVo.setLeaderSalaryChangeList(performanceResultListClassifySort(historyDirectOrdinate));
            //直接下属块
            subordinateSalaryChangeAppVo.setSubGroup(subGroup);
            subordinateSalaryChangeAppVo.setProfile(profile);
            subordinateSalaryChangeAppVo.setSubmitFlag(false);
        }
        return subordinateSalaryChangeAppVo;
    }

    @Override
    public List<OrdinateSalaryChangeAppVo> getMemberSalaryChange(Long workGroupId, Integer year, Integer quarter, Boolean isFinish) {
        if (isFinish) {
            List<OrdinateSalaryChangeAppVo> list = salaryHistoryTrackService.getAllHistoryMembersByWorkGroupIdAndTime(workGroupId, year, 0, quarter);
            performanceResultFilled(list);
            return performanceResultListClassifySort(list);
        } else {
            List<WorkGroupDto> allGroups = workGroupMapper.selectAllWithLeaderByGroupRoleId(WorkGroupRole.Role.salary);
            List<OrdinateSalaryChangeAppVo> ordinateSalaryChangeList = userSalaryChangeMapper.getQuarterRangePerformance(year, quarter);

            WorkGroupDto rootWorkGroup = null;
            for (WorkGroupDto workGroupDto : allGroups) {
                if (workGroupId.equals(workGroupDto.getId())) {
                    rootWorkGroup = workGroupDto;
                    break;
                }
            }
            performanceResultFilled(ordinateSalaryChangeList);
            groupTrackService.initHrGroupTreeByRootGroup(rootWorkGroup, allGroups, ordinateSalaryChangeList);
            List<OrdinateSalaryChangeAppVo> resultList = groupTrackService.getAllMembersByRootWorkGroup(rootWorkGroup);
            return performanceResultListClassifySort(resultList);
        }
    }

    @Override
    public IndividualSalaryChangeVo getIndividualDetail(Boolean isExist, Long userId, Integer year, Integer quarter) {
        IndividualSalaryChangeVo result = new IndividualSalaryChangeVo();
        IndividualSalaryChangeVo.UserInfo userInfo = new IndividualSalaryChangeVo.UserInfo();
        IndividualSalaryChangeVo.SalaryInfo salaryInfo = new IndividualSalaryChangeVo.SalaryInfo();

        //数据库所有已存的个人信息
        OrdinateSalaryChangeAppVo vo = userSalaryChangeMapper.getIndividualSalaryDetail(userId, year, quarter);
        UserGradeChange userGradeChange = userGradeChangeMapper.getUserGradeChangeByUserId(userId);

        if (isExist) {//已经完成了
            BeanUtils.copyProperties(vo, salaryInfo);
            salaryInfo.setSalary(vo.getOldSalary());
            BeanUtils.copyProperties(vo, userInfo);
            userInfo.setWorkGroupName(vo.getWorkGroup());
        } else if (vo != null) {//未完成但部分信息已经被保存

            OrdinateSalaryChangeAppVo temp = userSalaryChangeMapper.combineSalaryInfo(userId, year, quarter);
            //拼接出与绩效有关的信息+当前evaluate_type ,grade 保持最新信息
            vo.setGrade(temp.getGrade());
            vo.setEvaluateType(temp.getEvaluateType());
            BeanUtils.copyProperties(vo, salaryInfo);
            salaryInfo.setSalary(vo.getOldSalary());
            BeanUtils.copyProperties(vo, userInfo);
            userInfo.setWorkGroupName(vo.getWorkGroup());

        } else {
            OrdinateSalaryChangeAppVo temp = userSalaryChangeMapper.combineSalaryInfo(userId, year, quarter);
            if (temp == null)
                throw new UserSalaryChangeException(-1, "该用户此季度还未有绩效记录所以调薪记录为空");
            vo = temp;
            //用户的其他信息例如:employee_no ,post ，workAgeInKs 等（UserInfo中）
            userInfo = userMapper.selectUserPositionInfoByUserId(userId);

            //剩余信息（SalaryInfo中）
            UserSalary infoDtoTemp = getUserCurrentSalaryInfo(userId);
            String salary = infoDtoTemp.getSalary();
            String lastSalaryChangeDate = infoDtoTemp.getLastSalaryChangeDate();
            String lastSalaryChangeAmount = infoDtoTemp.getLastSalaryChangeAmount();
            logger.info(infoDtoTemp.toString());
            // 转换salary Info格式
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.US);
                Date lastSalaryChangeDateT = sdf.parse(lastSalaryChangeDate);
                salaryInfo.setLastSalaryChangeDate(lastSalaryChangeDateT);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            salaryInfo.setSalary(Integer.parseInt(salary));
            salaryInfo.setLastSalaryChangeAmount(Integer.parseInt(lastSalaryChangeAmount));
            if (userGradeChange != null) {
                salaryInfo.setLastGradeChangeDate(userGradeChange.getCreateTime());
            }
        }
        if (userGradeChange != null) {
            salaryInfo.setLastGrade(userGradeChange.getOldGrade());
        }
        performanceResultFilled(vo);
        BeanUtils.copyProperties(vo, result);
        result.setSalaryInfo(salaryInfo);
        result.setUserInfo(userInfo);
        return result;
    }

    @Override
    public List<List<OrdinateSalaryChangeAppVo>> getSubGroupMembers(Boolean isFinish, Long workGroupId, String status, List<Long> groupIds, Integer year, Integer quarter) {

        List<List<OrdinateSalaryChangeAppVo>> resultList = new ArrayList<>();

        Long userId = MyTokenUtils.getCurrentUser().getId();
        WorkGroupDto rootGroup = new WorkGroupDto();
        rootGroup.setId(workGroupId);
        rootGroup.setLeaderId(userId);
        List<WorkGroupDto> allGroups = workGroupMapper.selectAllWithLeaderByGroupRoleId(WorkGroupRole.Role.salary);
        List<OrdinateSalaryChangeAppVo> allUsers = userSalaryChangeMapper.getQuarterRangePerformance(year, quarter);
        performanceResultFilled(allUsers);
        groupTrackService.initHrGroupTreeByRootGroup(rootGroup, allGroups, allUsers);
        if (isFinish) {
            for (int j = 0; j < groupIds.size(); j++) {
                List<OrdinateSalaryChangeAppVo> simpleVos = new ArrayList<>();
                List<OrdinateSalaryChangeAppVo> list = salaryHistoryTrackService.getAllHistoryMembersByWorkGroupIdAndTime(groupIds.get(j), year, 0, quarter);
                if (list == null || list.size() == 0) {
                    return resultList;
                }
                performanceResultFilled(list);
                for (OrdinateSalaryChangeAppVo vo : list) {
                    if (status.equals(vo.getPerformanceResult())) {
                        OrdinateSalaryChangeAppVo simpleVo = new OrdinateSalaryChangeAppVo();
                        BeanUtils.copyProperties(vo, simpleVo);
                        simpleVos.add(simpleVo);
                    }
                }
                performanceResultListClassifySort(simpleVos);
                resultList.add(simpleVos);
            }
        } else {
            List<WorkGroupDto> childrenWorkGroup = groupTrackService.getDirectGroup(groupTrackService.getDirectMember(rootGroup), allGroups);
            for (int i = 0; i < childrenWorkGroup.size(); i++) {
                List<OrdinateSalaryChangeAppVo> simpleVos = new ArrayList<>();
                List<OrdinateSalaryChangeAppVo> dtoList = groupTrackService.getAllMembersByRootWorkGroup(childrenWorkGroup.get(i));
                performanceResultFilled(dtoList);
                for (int j = 0; j < dtoList.size(); j++) {
                    OrdinateSalaryChangeAppVo vo = dtoList.get(j);
                    if (status.equals(vo.getPerformanceResult())) {
                        OrdinateSalaryChangeAppVo simpleVo = new OrdinateSalaryChangeAppVo();
                        BeanUtils.copyProperties(vo, simpleVo);
                        simpleVos.add(simpleVo);
                    }
                }
                performanceResultListClassifySort(simpleVos);
                resultList.add(simpleVos);
            }
        }
        return resultList;
    }

    @Transactional
    @Override
    public void modifyOrdinateSalaryChange(IndividualSalaryChangeVo vo, Long workGroupId) {
        Long userId = MyTokenUtils.getCurrentUser().getId();
        String channel = MyTokenUtils.getChannel();
        String newGrade = vo.getGrade();
        String oldGrade = "";
        String newEvaluateType = vo.getEvaluateType();
        String oldEvaluateType = "";
        Integer newIncreaseSalary = vo.getIncreaseSalary();
        Integer oldIncreaseSalary = 0;

        UserSalaryChange info = userSalaryChangeMapper.selectPersonalInfo(vo.getUserId(), vo.getYear(), vo.getQuarter());
        if (info != null) {
            oldEvaluateType = info.getEvaluateType();
            oldGrade = info.getGrade();
        }
        if (vo.getEvaluateType() == null) newEvaluateType = oldEvaluateType;
        if (vo.getGrade() == null) newGrade = oldGrade;

        if (vo.getId() != null) {
            //数据库已经有记录
            UserSalaryChange userSalaryChange = userSalaryChangeMapper.selectByUserIdYearAndQuarter(vo.getUserId(), vo.getYear(), vo.getQuarter());
            oldIncreaseSalary = userSalaryChange.getIncreaseSalary();
            userSalaryChange.setGrade(vo.getGrade());
            userSalaryChange.setEvaluateType(vo.getEvaluateType());
            userSalaryChange.setIncreaseSalary(vo.getIncreaseSalary());
            userSalaryChange.setUpdateTime(new Date());
            userSalaryChange.setWorkGroupName(vo.getWorkGroup());
            userSalaryChange.setPost(vo.getPost());
            userSalaryChange.setWorkAgeInKs(vo.getWorkAgeInKs());
            userSalaryChange.setWorkAge(vo.getWorkAge());

            OrdinateSalaryChangeAppVo voTemp = new OrdinateSalaryChangeAppVo();
            voTemp.setScore(vo.getScore());
            voTemp.setPerformanceCount(vo.getPerformanceCount());
            voTemp.setEvaluateType(newEvaluateType);

            if (!UserSalaryChange.PerformanceResult.good.equals(performanceResultFilled(voTemp).getPerformanceResult()) && newIncreaseSalary > 0) {
                throw new UserSalaryChangeException(-1, "不满足调薪条件");
            }
            userSalaryChangeMapper.updateByPrimaryKeySelective(userSalaryChange);
        } else {

            //生成一条记录
            OrdinateSalaryChangeAppVo salaryChange = new OrdinateSalaryChangeAppVo();
            BeanUtils.copyProperties(vo, salaryChange);

            BeanUtils.copyProperties(vo.getSalaryInfo(), salaryChange);
            BeanUtils.copyProperties(vo.getUserInfo(), salaryChange);

            salaryChange.setOldSalary(vo.getSalaryInfo().getSalary());
            salaryChange.setStatus(UserSalaryChange.Status.waitingForCommit);

            List<OrdinateSalaryChangeAppVo> list = new ArrayList<>();
            list.add(salaryChange);
            WorkGroupDto rootGroup = new WorkGroupDto();
            List<WorkGroupDto> allGroups = workGroupMapper.selectAllWithLeaderByGroupRoleId(WorkGroupRole.Role.salary);
            List<WorkGroupUserDto> allUsers = userMapper.selectAllEntityWithWorkGroupSimple(WorkGroupRole.Role.salary);
            for (WorkGroupDto workGroup : allGroups) {
                if (workGroup.getId().equals(workGroupId)) {
                    rootGroup = workGroup;
                }
            }
            groupTrackService.initHrGroupTreeByRootGroup(rootGroup, allGroups, allUsers);
            getWorkGroupIdAndSubGroupId(list, rootGroup);
            OrdinateSalaryChangeAppVo changeVo = list.get(0);
            UserSalaryChange userSalaryChangeResult = new UserSalaryChange();
            BeanUtils.copyProperties(changeVo, userSalaryChangeResult);
            userSalaryChangeResult.setCreateTime(new Date());
            userSalaryChangeResult.setUpdateTime(new Date());
            userSalaryChangeMapper.insertSelective(userSalaryChangeResult);
        }
        //插入改动记录的log:三个地方：user表/user_grade_change表/log表
        String userName = vo.getName() == null ? "" : vo.getName();
        List<OperateLog> logList = new ArrayList<>();
        if (newEvaluateType != null && (!newEvaluateType.equals(oldEvaluateType))) {
            String str = " 调整了 " + userName + " 预估高估状态 from " + oldEvaluateType + " to " + newEvaluateType + ". ";
            OperateLog operateLog = new OperateLog();
            operateLog.setType(OperateLog.Type.user_evaluate_type_modify);
            operateLog.setDescription(str);
            operateLog.setChannel(channel);
            logList.add(operateLog);
        }
        if (newGrade != null && (!newGrade.equals(oldGrade))) {
            String desc = "调整了 " + userName + " 职级 from " + oldGrade + " to " + newGrade + ".";
            OperateLog operateLog = new OperateLog();
            operateLog.setType(OperateLog.Type.user_grade_modify);
            operateLog.setDescription(desc);
            operateLog.setChannel(channel);
            logList.add(operateLog);
            UserGradeChange userGradeChange = new UserGradeChange();
            userGradeChange.setUserId(vo.getUserId());
            userGradeChange.setYear(Calendar.getInstance().get(Calendar.YEAR));
            userGradeChange.setMonth(Calendar.getInstance().get(Calendar.MONTH) + 1);
            userGradeChange.setOldGrade(oldGrade);
            userGradeChange.setNewGrade(newGrade);
            userGradeChange.setOldEvaluateType(oldEvaluateType);
            userGradeChange.setNewEvaluateType(newEvaluateType);
            userGradeChange.setCreateTime(new Date());
            userGradeChangeMapper.insertSelective(userGradeChange);
        }
        if (newIncreaseSalary != null && (!newIncreaseSalary.equals(oldIncreaseSalary))) {
            String desc="";
            OperateLog operateLog = new OperateLog();
            operateLog.setType(OperateLog.Type.user_salary_modify);
            operateLog.setDescription(desc);
            operateLog.setChannel(channel);
            logList.add(operateLog);
        }
        if (logList.size() != 0) {
            operateLogService.batchInsert(logList, userId);
        }
        //更新用户的grade和evaluateType
        if ((newEvaluateType != null) && (newGrade != null) && (!newEvaluateType.equals(oldEvaluateType) || !newGrade.equals(oldGrade))) {
            User user = new User();
            user.setId(vo.getUserId());
            user.setEvaluateType(newEvaluateType);
            user.setGrade(newGrade);
            userMapper.updateByPrimaryKeySelective(user);
        }
    }

    @Transactional
    @Override
    public void requestReject(Integer year, Integer quarter, Long workGroupId) {
        User user = MyTokenUtils.getCurrentUser();
        WorkGroupDto rootGroup = new WorkGroupDto();
        rootGroup.setId(workGroupId);
        List<WorkGroupDto> allGroups = workGroupMapper.selectAllWithLeaderByGroupRoleId(WorkGroupRole.Role.salary);
        for (WorkGroupDto workGroupDto : allGroups) {
            if (workGroupId.equals(workGroupDto.getId())) {
                rootGroup = workGroupDto;
                break;
            }
        }

        List<OrdinateSalaryChangeAppVo> allUsers = userSalaryChangeMapper.getQuarterRangePerformance(year, quarter);

        groupTrackService.initHrGroupTreeByRootGroup(rootGroup, allGroups, allUsers);

        List<OrdinateSalaryChangeAppVo> directMember = groupTrackService.getDirectMember(rootGroup);
        List<UserSalaryChange> list = new ArrayList<>();
        for (int i = 0; i < directMember.size(); i++) {
            OrdinateSalaryChangeAppVo rejectPerson = directMember.get(i);
            UserSalaryChange userSalaryChange = new UserSalaryChange();
            BeanUtils.copyProperties(rejectPerson, userSalaryChange);
            userSalaryChange.setStatus(UserSalaryChange.Status.waitingForCommit);
            userSalaryChange.setUpdateTime(new Date());
            list.add(userSalaryChange);
        }
        if (list.size() != 0) {
            userSalaryChangeMapper.batchUpdateSelective(list);
        }
        operateLogService.add(OperateLog.Type.group_salary_reject, user.getName() + " 打回了 " + rootGroup.getName() + " 组的调薪", user.getId());
    }

    @Transactional
    @Override
    public void groupCommit(Long workGroupId, Integer year, Integer quarter) {
        User user = MyTokenUtils.getCurrentUser();
        WorkGroupDto workGroupDto = new WorkGroupDto();
        workGroupDto.setId(workGroupId);
        List<WorkGroupDto> allGroups = workGroupMapper.selectAllWithLeaderByGroupRoleId(WorkGroupRole.Role.salary);
        List<OrdinateSalaryChangeAppVo> ordinateSalaryChangeList = userSalaryChangeMapper.getQuarterRangePerformance(year, quarter);
        for (WorkGroupDto workGroup : allGroups) {
            if (workGroupId.equals(workGroup.getId())) {
                workGroupDto = workGroup;
                break;
            }
        }
        groupTrackService.initHrGroupTreeByRootGroup(workGroupDto, allGroups, ordinateSalaryChangeList);

        //更新直接下属状态
        List<OrdinateSalaryChangeAppVo> directList = groupTrackService.getDirectMember(workGroupDto);
        getWorkGroupIdAndSubGroupId(directList, workGroupDto);
        insertAndUpdate(directList);

        //处理下属组
        processAllSubGroup(directList, allGroups, workGroupDto);

        operateLogService.add(OperateLog.Type.group_salary_submit, user.getName() + "提交了 " + workGroupDto.getName() + "组的调薪", user.getId());
    }

    @Transactional
    @Override
    public void assistSubmit(Long workGroupId, Integer year, Integer quarter) {

        User user = MyTokenUtils.getCurrentUser();
        WorkGroupDto workGroupDto = new WorkGroupDto();
        workGroupDto.setId(workGroupId);
        List<WorkGroupDto> allGroups = workGroupMapper.selectAllWithLeaderByGroupRoleId(WorkGroupRole.Role.salary);
        List<OrdinateSalaryChangeAppVo> ordinateSalaryChangeList = userSalaryChangeMapper.getQuarterRangePerformance(year, quarter);
        for (WorkGroupDto workGroup : allGroups) {
            if (workGroup.getId().equals(workGroupId)) {
                workGroupDto = workGroup;
                break;
            }
        }
        groupTrackService.initHrGroupTreeByRootGroup(workGroupDto, allGroups, ordinateSalaryChangeList);

        //判断此下属组是否已经提交
        boolean subGroupIsFinish;
        List<OrdinateSalaryChangeAppVo> secondSubDirect = groupTrackService.getDirectMember(workGroupDto);
        if (secondSubDirect == null || secondSubDirect.size() == 0) {
            subGroupIsFinish = true;
        } else {
            OrdinateSalaryChangeAppVo vo = secondSubDirect.get(0);
            if (UserSalaryChange.Status.waitingForCommit.equals(vo.getStatus())) {
                subGroupIsFinish = false;
            } else {
                subGroupIsFinish = true;
            }
        }
        List<OrdinateSalaryChangeAppVo> voList = groupTrackService.getAllMembersByRootWorkGroup(workGroupDto);

        if (voList != null && voList.size() != 0) {
            getWorkGroupIdAndSubGroupId(voList, workGroupDto);
            if (subGroupIsFinish) {
                //下属组已经提交，更新下属组所有成员状态
                insertAndUpdate(voList);
            } else {
                //下属组未提交，下属组全部归零+提交
                salaryChangeReturnToZero(voList);
            }
        }
        operateLogService.add(OperateLog.Type.group_salary_submit, user.getName() + " 代提交了 " + workGroupDto.getName() + "组的调薪", user.getId());
    }

    @Transactional
    @Override
    public void leaderFinish(Integer year, Integer quarter) {
        Long userId = MyTokenUtils.getCurrentUser().getId();
//        Long userId = 1086L;
        WorkGroupDto workGroup = new WorkGroupDto();
        if (userId.equals(1086L)) {
            List<WorkGroupDto> allGroups = workGroupMapper.selectAllWithLeaderByGroupRoleId(WorkGroupRole.Role.salary);
            List<OrdinateSalaryChangeAppVo> ordinateSalaryChangeList = userSalaryChangeMapper.getQuarterRangePerformance(year, quarter);
            for (WorkGroupDto workGroupDto : allGroups) {
                if (userId.equals(workGroupDto.getLeaderId())) {
                    workGroup = workGroupDto;
                }
            }
            groupTrackService.initHrGroupTreeByRootGroup(workGroup, allGroups, ordinateSalaryChangeList);
            List<OrdinateSalaryChangeAppVo> directMembers = groupTrackService.getDirectMember(workGroup);
            getWorkGroupIdAndSubGroupId(directMembers, workGroup);
            insertAndUpdate(directMembers);

            processAllSubGroup(directMembers, allGroups, workGroup);
            userSalaryChangeMapper.updateAllEmployee(year, quarter);
        }
        operateLogService.add(OperateLog.Type.all_salary_confirm, "郭炜炜 确认了" + year + "年" + quarter + "季度" + "全员的调薪", 1086L);

    }


    @Override
    public int performanceMonthCount(Integer year, Integer quarter) {
        return userSalaryChangeMapper.performanceMonthCount(year, quarter);
    }

    @Override
    public List<SubordinateSalaryChangeAppVo.GroupStatus> getAllTimeGroupStatus(Long workGroupId) {
        return userSalaryChangeDao.getAllGroupStatus(workGroupId);
    }

    @Override
    public void receiveSalaryKey(MySalaryKey mySalaryKey) {
        logger.info("收到的 salaryKey version为:"+mySalaryKey.getVersion());
    }

    //更新所有下属状态为待评定
    private void insertAndUpdate(List<OrdinateSalaryChangeAppVo> ordinateSalaryChangeAppList) {
        List<UserSalaryChange> insertList = new ArrayList<>();
        List<UserSalaryChange> updateList = new ArrayList<>();

        //查找所有下属的 GradeChangeTime
        List<UserGradeChange> allUserGradeChange = userGradeChangeMapper.getAllUserLastChangeTime();
        Map<Long, Date> userGradeChangeTimeDateMap = new HashMap<>();
        if (allUserGradeChange != null && allUserGradeChange.size() != 0) {
            for (UserGradeChange userGradeChange : allUserGradeChange) {
                userGradeChangeTimeDateMap.put(userGradeChange.getUserId(), userGradeChange.getCreateTime());
            }
        }
        //查找所有下属的其他基本信息
        List<IndividualSalaryChangeVo.UserInfo> userInfoList = userMapper.selectAllUserPositionInfo();
        Map<Long, IndividualSalaryChangeVo.UserInfo> userInfoMap = new HashMap<>();
        if (userInfoList != null && userInfoList.size() != 0) {
            for (IndividualSalaryChangeVo.UserInfo userInfo : userInfoList) {
                userInfoMap.put(userInfo.getUserId(), userInfo);
            }
        }
        //将要更新和插入的用户分组
        for (OrdinateSalaryChangeAppVo vo : ordinateSalaryChangeAppList) {
            UserSalaryChange userSalaryChange = new UserSalaryChange();
            BeanUtils.copyProperties(vo, userSalaryChange);
            userSalaryChange.setStatus(UserSalaryChange.Status.waitingForConfirm);
            Date lastGradeChange = userGradeChangeTimeDateMap.get(vo.getUserId());
            UserSalary infoDtoTemp = getUserCurrentSalaryInfo(vo.getUserId());
            IndividualSalaryChangeVo.UserInfo userInfo = userInfoMap.get(vo.getUserId());

            // 转换日期格式
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.US);
                Date lastSalaryChangeDate = sdf.parse(infoDtoTemp.getLastSalaryChangeDate());
                userSalaryChange.setLastSalaryChangeDate(lastSalaryChangeDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            userSalaryChange.setWorkAge(userInfo.getWorkAge());
            userSalaryChange.setWorkAgeInKs(userInfo.getWorkAgeInKs());
            userSalaryChange.setWorkGroupName(userInfo.getWorkGroupName());
            if (userSalaryChange.getWorkGroupName() == null) {
                userSalaryChange.setWorkGroupName(vo.getWorkGroup());
            }
            userSalaryChange.setPost(userInfo.getPost());
            userSalaryChange.setLastSalaryChangeAmount(Integer.valueOf(infoDtoTemp.getLastSalaryChangeAmount()));
            userSalaryChange.setOldSalary(Integer.valueOf(infoDtoTemp.getSalary()));
            userSalaryChange.setLastGradeChangeDate(lastGradeChange);
            userSalaryChange.setCreateTime(new Date());
            userSalaryChange.setUpdateTime(new Date());
            if (vo.getId() == null || vo.getId().equals(0L)) {
                insertList.add(userSalaryChange);
            } else {
                updateList.add(userSalaryChange);
            }
        }

        //插入 & 更新
        if (insertList.size() != 0) {
            userSalaryChangeMapper.batchInsertSelective(insertList);
        }
        if (updateList.size() != 0) {
            userSalaryChangeMapper.batchUpdateSelective(updateList);//此处插入只包括状态改变,不用记录日志
        }
    }

    //将所有子下属调薪纪录归零方法
    private void salaryChangeReturnToZero(List<OrdinateSalaryChangeAppVo> voList) {
        for (OrdinateSalaryChangeAppVo vo : voList) {
            vo.setIncreaseSalary(0);
        }
        insertAndUpdate(voList);
    }

    //处理所有的下属组成员:组提交-->不用改变(调薪记录与数据已经存在)，未提交-->归零
    private void processAllSubGroup(List<OrdinateSalaryChangeAppVo> directMemberList, List<WorkGroupDto> allGroups, WorkGroupDto workGroupDto) {

        //未提交下属组全部归零
        List<WorkGroupDto> workGroupDtoList = groupTrackService.getDirectGroup(directMemberList, allGroups);
        List<OrdinateSalaryChangeAppVo> returnToZeroVoList = new ArrayList<>();

        //整理出未提交组的下属
        for (WorkGroupDto workGroupDtoTemp : workGroupDtoList) {
            List<OrdinateSalaryChangeAppVo> subDirect = groupTrackService.getDirectMember(workGroupDtoTemp);
            if (subDirect != null && subDirect.size() != 0) {
                OrdinateSalaryChangeAppVo vo = subDirect.get(0);
                if (vo.getStatus() == null || (vo.getStatus() != null && vo.getStatus().equals(UserSalaryChange.Status.waitingForCommit))) {
                    returnToZeroVoList.addAll(groupTrackService.getAllMembersByRootWorkGroup(workGroupDtoTemp));
                }
            }
        }
        getWorkGroupIdAndSubGroupId(returnToZeroVoList, workGroupDto);
        salaryChangeReturnToZero(returnToZeroVoList);
    }
//    public List<UserSalaryChange> cloneDateObjectPerformanceTest() {
//        return userSalaryChangeMapper.selectAll();
//    }


}
