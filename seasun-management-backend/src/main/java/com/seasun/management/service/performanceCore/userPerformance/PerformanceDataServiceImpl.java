package com.seasun.management.service.performanceCore.userPerformance;

import com.seasun.management.constant.ErrorCode;
import com.seasun.management.dto.*;
import com.seasun.management.exception.DataImportException;
import com.seasun.management.exception.ParamException;
import com.seasun.management.helper.ExcelHelper;
import com.seasun.management.helper.UserPerformanceHelper;
import com.seasun.management.mapper.PerformanceWorkGroupMapper;
import com.seasun.management.mapper.RUserPerformancePermMapper;
import com.seasun.management.mapper.UserMapper;
import com.seasun.management.mapper.UserPerformanceMapper;
import com.seasun.management.model.*;
import com.seasun.management.service.PerformanceDataService;
import com.seasun.management.service.PerformanceWorkGroupService;
import com.seasun.management.service.UserPerformanceService;
import com.seasun.management.service.performanceCore.historyTrack.PerformanceHistoryTrackService;
import com.seasun.management.util.MyCellUtils;
import com.seasun.management.util.MyObjectTrackUtils;
import com.seasun.management.util.MyTokenUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.seasun.management.model.PerformanceWorkGroupRole.Type.*;

@Service
public class PerformanceDataServiceImpl implements PerformanceDataService {
    private static final Logger logger = LoggerFactory.getLogger(PerformanceDataServiceImpl.class);

    @Value("${file.sys.prefix}")
    private String filePathPrefix;

    @Value("${backup.excel.url}")
    private String backupExcelUrl;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PerformanceWorkGroupMapper performanceWorkGroupMapper;

    @Autowired
    private UserPerformanceMapper userPerformanceMapper;

    @Autowired
    private UserPerformanceService userPerformanceService;

    @Autowired
    private PerformanceHistoryTrackService historyTrackService;

    @Autowired
    private RUserPerformancePermMapper rUserPerformancePermMapper;

    @Autowired
    private PerformanceWorkGroupService performanceWorkGroupService;

    @Override
    public void importUserPerformance(MultipartFile file, Long performanceWorkGroupId, Boolean insertFlag) {
        List<PerformanceUserDto> users = userMapper.selectAllEntityWithPerformanceByPerformanceWorkGroup();
        List<PerformanceWorkGroup> allPerformanceGroups = performanceWorkGroupMapper.selectAllActiveWorkGroup();

        UserPerformanceImportDataDto userPerformanceImportDataDto = getImportUserPerformanceDataFromExcel(file, users, allPerformanceGroups);
        List<UserPerformanceDto> userPerformanceList = userPerformanceImportDataDto.getUserPerformances();
        int year = userPerformanceImportDataDto.getYear();
        int month = userPerformanceImportDataDto.getMonth();

        if (userPerformanceList.size() == 0) {
            throw new DataImportException(ErrorCode.FILE_IMPORT_FILE_READ_ERROR, "没有可导入的绩效数据");
        }

        List<Long> perfWorkGroupIds = getAllSubPerfWorkGroupIds(performanceWorkGroupId, allPerformanceGroups);
        if (userPerformanceList.stream().anyMatch(p -> perfWorkGroupIds.contains(p.getWorkGroupId()))) {
            if (insertFlag) {
                this.execute(perfWorkGroupIds, userPerformanceList, year, month);
            } else {
                List<UserPerformance> userPerformances = userPerformanceMapper.selectByWorkGroupIdAndYearAndMonth(year, month, perfWorkGroupIds);
                for (UserPerformance userPerformance : userPerformances) {
                    for (UserPerformanceDto u : userPerformanceList) {
                        if (userPerformance.getYear().equals(u.getYear()) && userPerformance.getMonth().equals(u.getMonth())
                                && userPerformance.getUserId().equals(u.getUserId())) {
                            u.setId(userPerformance.getId());
                        }
                    }
                }
                userPerformanceMapper.updateFPAndMCById(userPerformanceList);
            }
        }
    }

    private static boolean checkoutUserPerformance(UserPerformance userPerformance) {
        String finalPerformance = userPerformance.getFinalPerformance();
        String upperFinPer = convertFinPerUpperCase(finalPerformance);
        if (UserPerformance.performanceDataDictionary.contains(upperFinPer)) {
            userPerformance.setFinalPerformance(upperFinPer);
            return true;
        }
        return false;
    }

    private static String convertFinPerUpperCase(String input) {
        if (input == null) {
            return "";
        }
        byte[] items = input.getBytes();
        byte[] result = new byte[items.length];
        for (int i = 0; i < items.length; i++) {
            if (items[i] != '-') {
                result[i] = (byte) ((char) items[0] - 'a' + 'A');
            } else {
                result[i] = items[i];
            }
        }
        return new String(result);
    }

    @Transactional
    private void execute(List<Long> perfWorkGroupIds, List<UserPerformanceDto> userPerformanceList, int year, int month) {
        userPerformanceMapper.deleteByWorkGroupIdAndYearAndMonth(year, month, perfWorkGroupIds);
        userPerformanceMapper.batchInsertByLeaderSubmit(userPerformanceList);
    }


    @Override
    public List<String> checkImportUserPerformanceData(MultipartFile file, Long performanceWorkGroupId) {
        List<PerformanceUserDto> users = userMapper.selectAllEntityWithPerformanceByPerformanceWorkGroup();
        List<PerformanceWorkGroup> allPerformanceGroups = performanceWorkGroupMapper.selectAllActiveWorkGroup();

        UserPerformanceImportDataDto userPerformanceImportDataDto = getImportUserPerformanceDataFromExcel(file, users, allPerformanceGroups);
        List<UserPerformanceDto> userPerformanceList = userPerformanceImportDataDto.getUserPerformances();

        if (userPerformanceList.size() > 0) {
            List<Long> perfWorkGroupIds = getAllSubPerfWorkGroupIds(performanceWorkGroupId, allPerformanceGroups);
            for (UserPerformanceDto userPerformanceDto : userPerformanceList) {
                if (!perfWorkGroupIds.contains(userPerformanceDto.getWorkGroupId())) {
                    // 记录不在绩效组的员工
                    userPerformanceImportDataDto.getErrorList().add(userPerformanceDto.getUserName() + " 工号" + userPerformanceDto.getEmployeeNo() + " 不在目标绩效组里");
                }
            }
        }

        return userPerformanceImportDataDto.getErrorList();
    }

    @Override
    public Map<Long, String> getSubWorkGroupIdAndFullNames(Long workGroupId, Map<Long, List<PerformanceWorkGroup>> workGroupMap, Map<Long, PerformanceWorkGroup> idPWGMap) {

        Map<Long, String> result = new LinkedHashMap<>();
        result.put(workGroupId, performanceWorkGroupService.getPerfWorkGroupName(workGroupId, idPWGMap));

        if (workGroupMap.containsKey(workGroupId)) {
            List<PerformanceWorkGroup> performanceWorkGroups = workGroupMap.get(workGroupId);
            for (PerformanceWorkGroup performanceWorkGroup : performanceWorkGroups) {
                result.putAll(getSubWorkGroupIdAndFullNames(performanceWorkGroup.getId(), workGroupMap, idPWGMap));
            }
        }
        return result;
    }

    @Override
    public List<IdNameBaseObject> getPerformanceDataPermission(Long userId) {
        if (userId == null) {
            userId = MyTokenUtils.getCurrentUserId();
        }
        return performanceWorkGroupMapper.selectUserDataPermission(userId);
    }

    @Override
    public List<YearMonthDto> getPerformanceDate(Long performanceWorkGroupId) {
        List<PerformanceWorkGroup> all = performanceWorkGroupMapper.selectAllActiveRecords();
        List<BaseParentDto> allChildren = new MyObjectTrackUtils().getAllChildrenByParentId(performanceWorkGroupId, all);
        List<Long> ids = new ArrayList<>();
        allChildren.forEach(r -> ids.add(r.getId()));
        return userPerformanceMapper.selectYearMonthByPerformanceWorkGroupIds(ids);
    }

    @Override
    public boolean changeDataManager(Long userId, Long workGroupPerformanceId, String type) {
        RUserPerformancePerm perm = new RUserPerformancePerm();
        perm.setUserId(userId);
        perm.setPerformanceWorkGroupId(workGroupPerformanceId);

        // 数据专员
        if (type.equals(dataManager)) {
            perm.setPerformanceWorkGroupRoleId(PerformanceWorkGroupRole.Role.performance_data_access_role_id);
        }
        // 观察者
        else if (type.equals(observer)) {
            perm.setPerformanceWorkGroupRoleId(PerformanceWorkGroupRole.Role.performance_observer_role_id);
        } else if (type.equals(humanConfigurator)) {
            perm.setPerformanceWorkGroupRoleId(PerformanceWorkGroupRole.Role.performance_human_access_role_id);
        }
        List<RUserPerformancePerm> rUserPerformancePerm = rUserPerformancePermMapper.selectSelectiveByRole(perm);
        if (rUserPerformancePerm == null || rUserPerformancePerm.size() == 0) {
            rUserPerformancePermMapper.insert(perm);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void deleteDataManager(Long id) {
        rUserPerformancePermMapper.deleteByPrimaryKey(id);
    }

    private UserPerformanceImportDataDto getImportUserPerformanceDataFromExcel(MultipartFile file, List<PerformanceUserDto> users, List<PerformanceWorkGroup> allPerformanceGroups) {

        UserPerformanceImportDataDto userPerformanceImportDataDto = new UserPerformanceImportDataDto();
        List<String> errorList = new ArrayList<>();
        List<UserPerformanceDto> userPerformanceList = new ArrayList<>();

        //从excel 文件中获取 sheet
        Sheet sheet = getSheetFromFile(file);
        String fileName = file.getOriginalFilename();

        YearMonthDto time = getTimeFromFileName(fileName);

        Map<Long, List<PerformanceUserDto>> userMap = users.stream().filter(u -> null != u.getEmployeeNo())
                .collect(Collectors.groupingBy(u -> u.getEmployeeNo()));
        Map<Long, PerformanceWorkGroup> groupMap = allPerformanceGroups.stream()
                .collect(Collectors.toMap(g -> g.getId(), g -> g));

        // 得到 绩效列名 -- 列数  ==> Map
        HashMap<String, Integer> headerIndexMap = getHeaderRuleNameIndexMap(sheet);

        //拿到主要绩效数据 ,错误列表 并保存文件
        setPerformanceListAndErrorListGetInvalidList(userPerformanceList, errorList, userMap, groupMap, headerIndexMap, time, sheet);

        userPerformanceImportDataDto.setYear(time.getYear());
        userPerformanceImportDataDto.setMonth(time.getMonth());
        userPerformanceImportDataDto.setUserPerformances(userPerformanceList);
        userPerformanceImportDataDto.setErrorList(errorList);
        return userPerformanceImportDataDto;
    }

    private UserPerformanceDto getNewUserPerformance(PerformanceUserDto user, int year, int month, PerformanceWorkGroup performanceGroup) {
        UserPerformanceDto userPerformanceDto = new UserPerformanceDto();
        userPerformanceDto.setYear(year);
        userPerformanceDto.setMonth(month);
        userPerformanceDto.setUserId(user.getUserId());
        userPerformanceDto.setWorkGroupId(performanceGroup.getId());
        userPerformanceDto.setParentGroup(performanceGroup.getParent());
        userPerformanceDto.setStatus(UserPerformance.Status.complete);
        userPerformanceDto.setCreateTime(new Date());
        userPerformanceDto.setUpdateTime(new Date());

        userPerformanceDto.setWorkAge(user.getWorkAge());
        userPerformanceDto.setWorkAgeInKs(user.getWorkAgeInKs());
        userPerformanceDto.setPost(user.getPost());
        userPerformanceDto.setWorkGroupName(user.getWorkGroupName());
        userPerformanceDto.setWorkStatus(user.getWorkStatus());

        return userPerformanceDto;
    }

    private static List<Long> getAllSubPerfWorkGroupIds(Long perfWorkGroupId, List<PerformanceWorkGroup> performanceWorkGroups) {
        Map<Long, List<PerformanceWorkGroup>> subPerfWorkGroupsMap = performanceWorkGroups.stream().filter(g -> null != g.getParent()).collect(Collectors.groupingBy(g -> g.getParent()));
        List<Long> ids = new ArrayList<>();
        ids.add(perfWorkGroupId);
        ids.addAll(getSubPerfWorkGroupIds(perfWorkGroupId, subPerfWorkGroupsMap));
        return ids;
    }

    private static List<Long> getSubPerfWorkGroupIds(Long parentId, Map<Long, List<PerformanceWorkGroup>> subPerfWorkGroupsMap) {
        List<Long> ids = new ArrayList<>();
        if (subPerfWorkGroupsMap.containsKey(parentId)) {
            List<PerformanceWorkGroup> subPerfWorkGroups = subPerfWorkGroupsMap.get(parentId);
            if (null != subPerfWorkGroups && !subPerfWorkGroups.isEmpty()) {
                for (PerformanceWorkGroup performanceWorkGroup : subPerfWorkGroups) {
                    ids.add(performanceWorkGroup.getId());
                    ids.addAll(getSubPerfWorkGroupIds(performanceWorkGroup.getId(), subPerfWorkGroupsMap));
                }
            }
        }
        return ids;
    }

    private Sheet getSheetFromFile(MultipartFile file) {

        // 获取 sheet
        Workbook wb;
        try {
            ExcelHelper.saveExcelBackup(file, filePathPrefix + backupExcelUrl);
        } catch (IOException e) {
            logger.error("invalid file");
            e.printStackTrace();
        }

        try {
            wb = WorkbookFactory.create(file.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
            throw new DataImportException(ErrorCode.FILE_IMPORT_FILE_READ_ERROR, "文件读取失败");
        }

        return wb.getSheetAt(0);
    }

    private YearMonthDto getTimeFromFileName(String fileName) {
        YearMonthDto yearMonthDto = new YearMonthDto();
        int year;
        int month;
        try {
            if (fileName.contains("年") && fileName.contains("月") && fileName.indexOf("年") < fileName.indexOf("月")) {
                year = Integer.parseInt(fileName.substring(fileName.indexOf("年") - 4, fileName.indexOf("年")));
                month = Integer.parseInt(fileName.substring(fileName.indexOf("年") + 1, fileName.indexOf("月")));
            } else {
                throw new DataImportException(ErrorCode.FILE_IMPORT_FILE_READ_ERROR, "文件名命名错误必须带有xxxx年xx月");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new DataImportException(ErrorCode.FILE_IMPORT_FILE_READ_ERROR, "文件名命名错误必须带有xxxx年xx月");
        }
        yearMonthDto.setMonth(month);
        yearMonthDto.setYear(year);
        return yearMonthDto;
    }

    private void setPerformanceListAndErrorListGetInvalidList(List<UserPerformanceDto> userPerformanceList, List<String> errorList,
                                                              Map<Long, List<PerformanceUserDto>> userMap,
                                                              Map<Long, PerformanceWorkGroup> groupMap,
                                                              HashMap<String, Integer> headerIndexMap,
                                                              YearMonthDto yearMonthDto, Sheet sheet) {
        int physicalNumberOfRows = sheet.getPhysicalNumberOfRows();
        if (physicalNumberOfRows == 0) {
            throw new DataImportException(ErrorCode.FILE_IMPORT_FILE_READ_ERROR, "模板内容为空");
        }
        List<String> invalidEmployeeNoUsers = new ArrayList<>();
        List<Long> dupEmployeeNoUsers = new ArrayList<>();
        HashMap<String, UserPerformanceDto> currentDataMap = new HashMap<>();

        for (int i = 1; i < physicalNumberOfRows; i++) {
            Row row = sheet.getRow(i);

            Long employeeNo = (long) NumberUtils.toDouble(MyCellUtils.getCellValue(row.getCell(headerIndexMap.get(UserPerformanceHelper.ExcelTableHeader.employeeNo))), -1d);
            if (employeeNo == -1L) {
                logger.error("empty employeeNo...,row:{}", i);
                errorList.add("行号：" + (i + 1) + " 的工号为空");
                continue;
            }
            String finalPerformance = MyCellUtils.getCellValue(row.getCell(headerIndexMap.get(UserPerformanceHelper.ExcelTableHeader.finalPerformance)));
            String monthGoal = MyCellUtils.getCellValue(row.getCell(headerIndexMap.get(UserPerformanceHelper.ExcelTableHeader.monthGoal)));
            String managerComment = MyCellUtils.getCellValue(row.getCell(headerIndexMap.get(UserPerformanceHelper.ExcelTableHeader.managerComment)));

            if (userMap.containsKey(employeeNo)) {
                PerformanceUserDto user = userMap.get(employeeNo).get(0);
                String uniqueKey = "" + user.getUserId() + yearMonthDto.getYear() + yearMonthDto.getMonth();
                if (groupMap.containsKey(user.getWorkGroupId())) {
                    UserPerformanceDto userPerformance;

                    // 已存在，则直接获取
                    if (currentDataMap.keySet().contains(uniqueKey)) {
                        // 记录重复员工编号的用户
                        if (!dupEmployeeNoUsers.contains(user.getEmployeeNo())) {
                            dupEmployeeNoUsers.add(user.getEmployeeNo());
                            invalidEmployeeNoUsers.add("dup employNO:" + user.getEmployeeNo() + ", and row is:" + i);
                            errorList.add("行号：" + (i + 1) + " 重复的员工编号");
                        }
                        userPerformance = currentDataMap.get(uniqueKey);
                        userPerformance.setFinalPerformance(finalPerformance);
                        userPerformance.setMonthGoal(monthGoal);
                        userPerformance.setManagerComment(managerComment);
                        continue;
                    }
                    // 否则，重新计算
                    else {
                        PerformanceWorkGroup group = groupMap.get(user.getWorkGroupId());
                        userPerformance = getNewUserPerformance(user, yearMonthDto.getYear(), yearMonthDto.getMonth(), group);
                        currentDataMap.put("" + user.getUserId() + yearMonthDto.getYear() + yearMonthDto.getMonth(), userPerformance);
                        userPerformance.setFinalPerformance(finalPerformance);
                        userPerformance.setMonthGoal(monthGoal);
                        userPerformance.setManagerComment(managerComment);
                    }

                    userPerformance.setUserName(user.getName());
                    userPerformance.setEmployeeNo(user.getEmployeeNo());
                    if (checkoutUserPerformance(userPerformance)) {
                        userPerformanceList.add(userPerformance);
                    } else {
                        errorList.add("行号：" + (i + 1) + " 主管评级不合法，请修改");
                    }

                }
            } else {
                invalidEmployeeNoUsers.add("invalid employNO:" + employeeNo + ", and row is:" + i);
                errorList.add("行号：" + (i + 1) + " 工号无效");
            }
        }
        ExcelHelper.saveErrorFile(invalidEmployeeNoUsers, "perf-import/", "invalid-employee" + yearMonthDto.getYear() + "-" + yearMonthDto.getMonth());
    }

    private HashMap<String, Integer> getHeaderRuleNameIndexMap(Sheet sheet) {

        HashMap<String, Integer> headerIndexMap = new HashMap<String, Integer>() {{
            put(UserPerformanceHelper.ExcelTableHeader.employeeNo, 0);
            put(UserPerformanceHelper.ExcelTableHeader.finalPerformance, 0);
            put(UserPerformanceHelper.ExcelTableHeader.monthGoal, 0);
            put(UserPerformanceHelper.ExcelTableHeader.managerComment, 0);
        }};
        Row header = sheet.getRow(0);
        for (String key : headerIndexMap.keySet()) {
            boolean hasKey = false;
            for (int i = 0; i < header.getPhysicalNumberOfCells(); i++) {
                boolean isColumnHidden = sheet.isColumnHidden(i);
                if (isColumnHidden) {
                    continue;
                }
                String name = MyCellUtils.getCellValue(header.getCell(i));
                if (name.contains(key)) {
                    headerIndexMap.replace(key, i);
                    hasKey = true;
                    break;
                }
            }
            if (!hasKey) {
                throw new DataImportException(ErrorCode.FILE_IMPORT_FILE_READ_ERROR, "表头没有“" + key + "”列，模板格式不正确");
            }
        }
        return headerIndexMap;
    }

}
