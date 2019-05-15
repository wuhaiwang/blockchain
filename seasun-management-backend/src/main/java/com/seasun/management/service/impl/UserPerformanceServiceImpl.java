package com.seasun.management.service.impl;

import com.seasun.management.dto.*;
import com.seasun.management.exception.ParamException;
import com.seasun.management.helper.ReportHelper;
import com.seasun.management.helper.UserMessageHelper;
import com.seasun.management.helper.UserPerformanceHelper;
import com.seasun.management.mapper.*;
import com.seasun.management.model.*;
import com.seasun.management.service.OperateLogService;
import com.seasun.management.service.PerformanceDataService;
import com.seasun.management.service.UserMessageService;
import com.seasun.management.service.UserPerformanceService;
import com.seasun.management.service.kingsoftLife.KsUserService;
import com.seasun.management.service.performanceCore.historyTrack.PerformanceHistoryTrackService;
import com.seasun.management.service.performanceCore.userPerformance.PerformanceDataHelper;
import com.seasun.management.service.performanceCore.userPerformance.PerformanceService;
import com.seasun.management.service.performanceCore.userPerformance.PerformanceTreeHelper;
import com.seasun.management.util.*;
import com.seasun.management.vo.*;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class UserPerformanceServiceImpl implements UserPerformanceService {

    @Autowired
    UserPerformanceMapper userPerformanceMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    OperateLogService operateLogService;

    @Autowired
    PerformanceWorkGroupMapper performanceWorkGroupMapper;

    @Autowired
    PerformanceObserverMapper performanceObserverMapper;

    @Resource(name = "current")
    PerformanceService performanceCurrentService;

    @Resource(name = "history")
    PerformanceService performanceHistoryService;

    @Autowired
    PerformanceHistoryTrackService historyTrackService;

    @Autowired
    CfgPerfHideManagerCommentMapper cfgPerfHideManagerCommentMapper;

    @Autowired
    FmUserRoleMapper fmUserRoleMapper;

    @Autowired
    UserMessageService userMessageService;

    @Autowired
    FmMemberMapper fmMemberMapper;

    @Autowired
    PerformanceDataService performanceDataService;

    @Autowired
    private PerfWorkGroupStatusMapper perfWorkGroupStatusMapper;

    @Autowired
    ProjectMapper projectMapper;

    @Autowired
    FmGroupConfirmInfoMapper fmGroupConfirmInfoMapper;

    @Autowired
    private KsUserService ksUserService;


    @Value("${export.excel.path}")
    private String exportExcelPath;

    @Value("${file.sys.prefix}")
    private String filePathPrefix;

    @Value("${file.sys.prefix}")
    private String fileSystemDiskPath;

    @Value("${user.icon.url}")
    private String userPhotoUrl;

    @Value("${user.koa.icon.url}")
    private String userKoaIcon;

    @Value("${user.koa.icon.cirde.url}")
    private String userKoaIconCirde;

    @Value("${s.user.photo.url}")
    private String sUserPhoto;

    @Autowired
    private RUserPerformancePermMapper rUserPerformancePermMapper;

    @Autowired
    private UserMessageMapper userMessageMapper;
    @Autowired
    private CfgSystemParamMapper cfgSystemParamMapper;

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(UserPerformanceServiceImpl.class);

    @Override
    public List<UserPerformance> getAllPerformanceByUserId(Long userId) {
        boolean isMyself = false;
        Calendar calendar = Calendar.getInstance();
        if (userId == null) {
            userId = MyTokenUtils.getCurrentUserId();
            isMyself = true;
        }

        List<UserPerformance> performances = userPerformanceMapper.selectAllByUserId(userId);

        if (performances.isEmpty()) {
            return performances;
        }
        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;

        Integer startDay = MySystemParamUtils.getSystemConfigWithDefaultValue(MySystemParamUtils.Key.PerformanceStartDay, MySystemParamUtils.DefaultValue.PerformanceStartDay);
        List<UserPerformance> result = new ArrayList<>();

        int recordFirstYear = performances.get(0).getYear();
        int recordFirstMonth = performances.get(0).getMonth();

        List<CfgPerfHideManagerComment> cfgPerfHideManagerComments = cfgPerfHideManagerCommentMapper.selectAll();
        List<PerformanceWorkGroupDto> performanceWorkGroups = performanceWorkGroupMapper.selectAllWithProjectIdByActive();

        // 补充其他月份数据
        for (int i = recordFirstYear; i < currentYear + 1; i++) {

            // 1. 确定循环开始的月份（跨年的补充的月份需要为写死为12）
            int loopStartMonth;
            if (recordFirstYear == i) {
                loopStartMonth = recordFirstMonth;
            } else {
                loopStartMonth = 0 + 1;
            }

            // 2. 确定循环结束的月份（跨年的补充的月份需要为写死为12）
            int loopEndMonth = currentMonth + 1;
            if (i < currentYear) {
                loopEndMonth = 12 + 1;
            }

            for (int j = loopStartMonth; j < loopEndMonth; j++) {
                UserPerformance matchedPerformance = null;
                for (UserPerformance performance : performances) {
                    if (performance.getYear() == i && performance.getMonth() == j) {
                        matchedPerformance = performance;
                        boolean isHideManagerComment = UserPerformanceHelper.isHideManagerComment(cfgPerfHideManagerComments, performanceWorkGroups, performance.getWorkGroupId(), performance.getYear(), performance.getMonth());
                        if (isHideManagerComment) {
                            matchedPerformance.setManagerComment(null);
                        }
                        // 没完成的绩效本人不能看到主管评分跟评语
                        if (isMyself && !UserPerformance.Status.complete.equals(performance.getStatus())) {
                            matchedPerformance.setFinalPerformance(null);
                            matchedPerformance.setManagerComment(null);
                        }
                        break;
                    }
                }
                if (matchedPerformance == null) {
                    // 若中间某个月份的数据不存在,则补充空数据
                    UserPerformance currentPerformance = new UserPerformance();
                    currentPerformance.setUserId(userId);
                    currentPerformance.setYear(i);
                    currentPerformance.setMonth(j);
                    currentPerformance.setStatus(UserPerformance.Status.unsubmitted);
                    currentPerformance.setSubBFlag(false);
                    result.add(currentPerformance);
                } else {
                    result.add(matchedPerformance);
                }
            }
        }


        // 转换状态
        for (UserPerformance performance : result) {
            // 将后台的状态转换为前台显示用
            String status = SubPerformanceAppVo.HistoryInfo.Status.processing;
            if (performance.getStatus().equals(UserPerformance.Status.assessed) ||
                    performance.getStatus().equals(UserPerformance.Status.locked) ||
                    performance.getStatus().equals(UserPerformance.Status.submitted)) {
                status = SubPerformanceAppVo.HistoryInfo.Status.submitted;
            } else if (performance.getStatus().equals(UserPerformance.Status.complete)) {
                status = SubPerformanceAppVo.HistoryInfo.Status.complete;
            } else if (performance.getYear() == currentYear && performance.getMonth() == currentMonth && calendar.get(Calendar.DAY_OF_MONTH) < startDay) {
                status = SubPerformanceAppVo.HistoryInfo.Status.waitingForStart;
            } else if (UserPerformanceHelper.isDelay(performance.getYear(), performance.getMonth(), startDay)) {
                status = SubPerformanceBaseVo.HistoryInfo.Status.delay;
            }

            // B-需要处理
            if (isMyself && performance.getSubBFlag()) {
                performance.setFinalPerformance(UserPerformance.Performance.B);
            }

            performance.setStatus(status);
            convertDBFormatToEmoji(performance);
        }

        return result;
    }

    @Override
    public String downloadYearlyToExcel(List<UserPerformanceDto> userPerformances, Map<Long, String> subWorkGoupIdAndFullNameMap, String fileName, List<UserPerformanceDto> leavedUserPerformanceDatas) {
        String exportFilePath = exportExcelPath + File.separator + fileName;
        String exportFileDiskPath = filePathPrefix + exportFilePath;
        FileOutputStream fileoutputStream = null;
        Workbook wb = null;

        Map<Long, List<UserPerformanceDto>> userPerfByUserIdMap = userPerformances.stream().collect(Collectors.groupingBy(g -> g.getUserId()));
        Map<Long, UserPerformanceDto> leavedUserPerformanceDatasByUserIdMap = leavedUserPerformanceDatas.stream().collect(Collectors.toMap(x -> x.getUserId(), x -> x, (oldValue, newValue) -> oldValue));
        Map<Long, Map<Long, Long>> userIdAndEmployeeNoByPerfWGIdMap = new HashMap<>();

        //遍历user的performance记录
        for (Long userId : userPerfByUserIdMap.keySet()) {
            //user最后一个月的perfWorkGroupID，
            UserPerformanceDto userPerformanceDto = userPerfByUserIdMap.get(userId).get(userPerfByUserIdMap.get(userId).size() - 1);
            Long finalPerfWorkGroupId = userPerformanceDto.getWorkGroupId();
            if (userIdAndEmployeeNoByPerfWGIdMap.containsKey(finalPerfWorkGroupId)) {
                userIdAndEmployeeNoByPerfWGIdMap.get(finalPerfWorkGroupId).put(userPerformanceDto.getEmployeeNo(), userId);
                continue;
            }
            Map<Long, Long> idAndEmployeeNoMap = new HashMap<>();
            idAndEmployeeNoMap.put(userPerformanceDto.getEmployeeNo(), userId);
            userIdAndEmployeeNoByPerfWGIdMap.put(finalPerfWorkGroupId, idAndEmployeeNoMap);
        }

        try {
            //创建工作表
            wb = new HSSFWorkbook();
            Sheet sheet = wb.createSheet();
            //设置前景填充样式
            CellStyle cellStyle = wb.createCellStyle();
            cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            cellStyle.setFillForegroundColor(HSSFColor.RED.index);

            //首行数据
            Row firstRow = sheet.createRow(0);
            int firstRowCellIndex = 0;
            for (String key : UserPerformanceHelper.getColumns().subList(0, 6)) {
                firstRow.createCell(firstRowCellIndex).setCellValue(UserPerformanceHelper.getColumnHeaderMap().get(key));
                firstRowCellIndex++;
            }
            for (int i = 1; i <= 12; i++) {
                firstRow.createCell(firstRowCellIndex).setCellValue(i + "月绩效");
                firstRowCellIndex++;
            }

            //遍历下属所有绩效组
            for (Long perfWorkGroupId : subWorkGoupIdAndFullNameMap.keySet()) {
                //判断绩效组是否有绩效记录
                Map<Long, Long> employeeNoAndIdMap = userIdAndEmployeeNoByPerfWGIdMap.get(perfWorkGroupId);
                if (employeeNoAndIdMap == null) {
                    continue;
                }
                List<Long> employeeNos = new ArrayList<>(employeeNoAndIdMap.keySet());

                Collections.sort(employeeNos);
                for (Long employeeNo : employeeNos) {
                    Long userId = employeeNoAndIdMap.get(employeeNo);
                    Row currentRow = sheet.createRow(sheet.getPhysicalNumberOfRows());
                    UserPerformanceDto userPerformanceDto = userPerfByUserIdMap.get(userId).get(userPerfByUserIdMap.get(userId).size() - 1);
                    int currentRowCellIndex = 0;
                    for (String key : UserPerformanceHelper.getColumns().subList(0, 6)) {
                        if ("projectName".equals(key)) {
                            currentRow.createCell(currentRowCellIndex).setCellValue(subWorkGoupIdAndFullNameMap.get(userPerformanceDto.getWorkGroupId()));
                            continue;
                        }
                        Object invoke = userPerformanceDto.getClass().getMethod("get" + MyStringUtils.convertFirstCharToUppercase(key)).invoke(userPerformanceDto);
                        if (invoke != null) {
                            if ("inDate".equals(key)) {
                                currentRow.createCell(currentRowCellIndex).setCellValue(new SimpleDateFormat("yyyy-MM-dd").format(invoke));
                            } else if ("employeeNo".equals(key) || "workAge".equals(key)) {
                                currentRow.createCell(currentRowCellIndex).setCellValue(Double.parseDouble(invoke.toString()));
                            } else {
                                currentRow.createCell(currentRowCellIndex).setCellValue(invoke.toString());
                            }
                        }
                        currentRowCellIndex++;
                    }

                    for (UserPerformanceDto performanceDto : userPerfByUserIdMap.get(userPerformanceDto.getUserId())) {
                        currentRow.createCell(5 + performanceDto.getMonth()).setCellValue(performanceDto.getFinalPerformance());
                    }
                }
            }

            //已离组成员
            for (Long userId : leavedUserPerformanceDatasByUserIdMap.keySet()) {
                UserPerformanceDto userPerformanceDto = leavedUserPerformanceDatasByUserIdMap.get(userId);
                Row currentRow = sheet.createRow(sheet.getPhysicalNumberOfRows());
                int currentRowCellIndex = 0;
                for (String key : UserPerformanceHelper.getColumns().subList(0, 6)) {
                    if ("projectName".equals(key)) {
                        Cell cell = currentRow.createCell(currentRowCellIndex);
                        cell.setCellStyle(cellStyle);
                        cell.setCellValue("该成员已离组");
                        continue;
                    }
                    Object invoke = userPerformanceDto.getClass().getMethod("get" + MyStringUtils.convertFirstCharToUppercase(key)).invoke(userPerformanceDto);
                    if (invoke != null) {
                        if ("inDate".equals(key)) {
                            currentRow.createCell(currentRowCellIndex).setCellValue(new SimpleDateFormat("yyyy-MM-dd").format(invoke));
                        } else if ("employeeNo".equals(key) || "workAge".equals(key)) {
                            currentRow.createCell(currentRowCellIndex).setCellValue(Double.parseDouble(invoke.toString()));
                        } else {
                            currentRow.createCell(currentRowCellIndex).setCellValue(invoke.toString());
                        }
                    }
                    currentRowCellIndex++;
                }
            }

            //设置列自动调整宽度
            sheet.autoSizeColumn(2, true);
            sheet.autoSizeColumn(4, true);
            sheet.autoSizeColumn(5, true);

            fileoutputStream = new FileOutputStream(exportFileDiskPath);
            wb.write(fileoutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (wb != null) {
                    wb.close();
                }
                if (fileoutputStream != null) {
                    fileoutputStream.close();
                }
            } catch (IOException e) {
            }
        }

        return exportFilePath;
    }

    @Override
    public String downloadToExcel(List<UserPerformanceDto> userPerformances, String fileName, List<PerformanceWorkGroupDto> performanceWorkGroups, List<Long> subUserIds, Map<Long, String> subWorkGoupIdAndFullNameMap) {
        List<CfgPerfHideManagerComment> cfgPerfHideManagerComments = cfgPerfHideManagerCommentMapper.selectAll();
        Map<Long, Map<Long, UserPerformanceDto>> userPerformanceByWorkGroupIdMap = new HashMap<>();
        //将-1作为已离组成员的key,排序用
        userPerformanceByWorkGroupIdMap.put(-1L, new HashMap<>());
        subWorkGoupIdAndFullNameMap.put(-1L, null);

        for (UserPerformanceDto userPerformance : userPerformances) {
            if (!subUserIds.contains(userPerformance.getUserId())) {
                userPerformanceByWorkGroupIdMap.get(-1L).put(userPerformance.getEmployeeNo(), userPerformance);
                continue;
            }

            if (userPerformanceByWorkGroupIdMap.containsKey(userPerformance.getWorkGroupId())) {
                userPerformanceByWorkGroupIdMap.get(userPerformance.getWorkGroupId()).put(userPerformance.getEmployeeNo(), userPerformance);
                continue;
            }
            Map<Long, UserPerformanceDto> userPerformanceByEmployeeNoMap = new HashMap<>();
            userPerformanceByEmployeeNoMap.put(userPerformance.getEmployeeNo(), userPerformance);
            userPerformanceByWorkGroupIdMap.put(userPerformance.getWorkGroupId(), userPerformanceByEmployeeNoMap);
        }

        String exportFilePath = exportExcelPath + File.separator + fileName;
        String exportFileDiskPath = filePathPrefix + exportFilePath;

        FileOutputStream fileOutputStream = null;
        Workbook wb = null;
        try {
            wb = new HSSFWorkbook();
            //设置前景填充样式
            CellStyle cellStyle = wb.createCellStyle();
            cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            cellStyle.setFillForegroundColor(HSSFColor.RED.index);

            //创建工作表
            Sheet sheet = wb.createSheet();
            Row firstRow = sheet.createRow(0);

            // 初始化首行
            int cellIndex = 0;
            for (String key : UserPerformanceHelper.getColumns()) {
                firstRow.createCell(cellIndex).setCellValue(UserPerformanceHelper.getColumnHeaderMap().get(key));
                cellIndex++;
            }

            // 填充内容
            int rowIndex = 1;
            for (Long workGroupId : subWorkGoupIdAndFullNameMap.keySet()) {
                Map<Long, UserPerformanceDto> longUserPerformanceDtoHashMap = userPerformanceByWorkGroupIdMap.get(workGroupId);
                if (longUserPerformanceDtoHashMap == null) {
                    continue;
                }
                List<Long> employeeNos = new ArrayList<>(longUserPerformanceDtoHashMap.keySet());
                //工号排序
                Collections.sort(employeeNos);
                for (Long employeeNo : employeeNos) {
                    UserPerformanceDto userPerformance = longUserPerformanceDtoHashMap.get(employeeNo);
                    boolean isHideManagerComment = UserPerformanceHelper.isHideManagerComment(cfgPerfHideManagerComments, performanceWorkGroups, userPerformance.getWorkGroupId(), userPerformance.getYear(), userPerformance.getMonth());
                    if (isHideManagerComment) {
                        userPerformance.setManagerComment(null);
                    }

                    Row row = sheet.createRow(rowIndex);
                    cellIndex = 0;
                    for (String key : UserPerformanceHelper.getColumns()) {
                        Cell cell = row.createCell(cellIndex);
                        Object val = null;
                        String valStr = "";
                        if ("projectName".equals(key)) {
                            if (!subUserIds.contains(userPerformance.getUserId())) {
                                cell.setCellStyle(cellStyle);
                                cell.setCellValue("该成员已离组");
                                break;
                            }
                            valStr = subWorkGoupIdAndFullNameMap.get(userPerformance.getWorkGroupId());
                            cell.setCellValue(valStr);
                            cellIndex++;
                            continue;
                        } else if ("restTime".equals(key) || "unReasonRestTime".equals(key)) {
                            cell.setCellValue(valStr);
                            cellIndex++;
                            continue;
                        } else if ("workContent".equals(key)) {
                            String selfComment = userPerformance.getSelfComment() == null ? "" : "; \n" +
                                    "自      评：" + userPerformance.getSelfComment();
                            String monthGoal = userPerformance.getMonthGoal() == null ? "" : "工作重点： " + userPerformance.getMonthGoal();
                            String workContent = monthGoal + selfComment;
                            cell.setCellValue(workContent);
                            cellIndex++;
                            continue;
                        } else if ("employeeNo".equals(key)) {
                            cell.setCellValue((double) userPerformance.getEmployeeNo());
                            cellIndex++;
                            continue;
                        } else if ("workAge".equals(key)) {
                            if (userPerformance.getWorkAge() != null) {
                                cell.setCellValue((double) userPerformance.getWorkAge());
                            }
                            cellIndex++;
                            continue;
                        }

                        Method m = userPerformance.getClass().getMethod(
                                "get" + MyStringUtils.convertFirstCharToUppercase(key));
                        val = m.invoke(userPerformance);

                        if (null != val) {
                            valStr = String.valueOf(val);
                            if ("inDate".equals(key)) {
                                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                valStr = dateFormat.format(val);
                            } else if ("finalPerformance".equals(key)) {
                                valStr = val.toString();
                            }
                        }
                        cell.setCellValue(valStr);
                        cellIndex++;
                    }
                    rowIndex++;
                }
            }


            //sheet.setAutoFilter(CellRangeAddress.valueOf("A1:G1"));
            //设置列自动调整宽度
            sheet.autoSizeColumn(2, true);
            sheet.autoSizeColumn(4, true);
            sheet.autoSizeColumn(5, true);


            fileOutputStream = new FileOutputStream(exportFileDiskPath);
            wb.write(fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (wb != null) {
                    wb.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return exportFilePath;
    }

    @Override
    public List<YearMonthDto> getAllYearMonth() {
        return userPerformanceMapper.selectAllWithYearMonth();
    }

    @Override
    public SubPerformanceAppVo getObserverPerformance(Long managerId, Long workGroupId, Integer year, Integer month, String filter) {
        SubPerformanceAppVo subPerformanceAppVo;

        Calendar now = Calendar.getInstance();
        if (year == null || month == null) {
            year = now.get(Calendar.YEAR);
            month = now.get(Calendar.MONTH);
        }
        boolean currentFlag;
        if (null != managerId) {
            currentFlag = perfWorkGroupStatusMapper.selectCountByManagerAndLockedOrComplete(managerId, year, month) == 0;
        } else {
            currentFlag = perfWorkGroupStatusMapper.selectCountByWorkGroupAndLockedOrComplete(workGroupId, year, month) == 0;
        }
        if (currentFlag) {
            subPerformanceAppVo = performanceCurrentService.getSubPerformance(managerId, workGroupId, year, month, filter);
        } else {
            subPerformanceAppVo = performanceHistoryService.getSubPerformance(managerId, workGroupId, year, month, filter);
        }

        convertToEmojiFromDbFormat(subPerformanceAppVo);
        return subPerformanceAppVo;
    }

    @Override
    public WorkGroupMemberPerformanceAppVo getObserverWorkGroupMemberPerformance(Long observerUserId, Long observerWorkGroupId, Long performanceGroupId, Integer year, Integer month) {
        WorkGroupMemberPerformanceAppVo workGroupMemberPerformanceAppVo;
        if (perfWorkGroupStatusMapper.selectCountByWorkGroupAndLockedOrComplete(performanceGroupId, year, month) == 0) {
            if (null == observerUserId) {
                PerformanceWorkGroup performanceWorkGroup = performanceWorkGroupMapper.selectByPrimaryKey(observerWorkGroupId);
                if (null == performanceWorkGroup) {
                    logger.info("performance work group not found. id is {}", observerWorkGroupId);
                    throw new ParamException("观察的绩效组不存在");
                }
                observerUserId = performanceWorkGroup.getPerformanceManagerId();
            }
            workGroupMemberPerformanceAppVo = performanceCurrentService.getWorkGroupMemberPerformance(observerUserId, performanceGroupId, year, month);
        } else {
            workGroupMemberPerformanceAppVo = performanceHistoryService.getWorkGroupMemberPerformance(observerUserId, performanceGroupId, year, month);
        }
        return workGroupMemberPerformanceAppVo;
    }

    @Override
    public SubPerformanceAppVo getSubPerformance(Integer year, Integer month, String filter) {

        Calendar now = ReportHelper.getDateOnly();
        int nowYear = now.get(Calendar.YEAR);
        int nowMonth = now.get(Calendar.MONTH) + 1;

        // 改版时间线，需要处理年月为空的情况
        if (year == null || month == null) {
            year = nowYear;
            month = nowMonth;
        }

        Long userId = MyTokenUtils.getCurrentUserId();
        SubPerformanceAppVo subPerformanceAppVo;
        if (perfWorkGroupStatusMapper.selectCountByManagerAndLockedOrComplete(userId, year, month) == 0) {
            subPerformanceAppVo = performanceCurrentService.getSubPerformance(userId, null, year, month, filter);
        } else {
            subPerformanceAppVo = performanceHistoryService.getSubPerformance(userId, null, year, month, filter);
        }

        convertToEmojiFromDbFormat(subPerformanceAppVo);
        return subPerformanceAppVo;
    }

    // 将db存在的格式,转换为emoj字符,供前端显示
    private void convertToEmojiFromDbFormat(SubPerformanceAppVo subPerformanceAppVo) {
        subPerformanceAppVo.getManagerPerformanceList().stream().forEach(m -> {
            if (m.getManagerComment() != null) {
                m.setManagerComment(MyEmojiUtil.resolveToEmojiFromByte(m.getManagerComment()));
            }
        });
    }

    @Override
    public List<SubPerformanceAppVo.HistoryInfo> getWorkGroupHistory() {
        Long userId = MyTokenUtils.getCurrentUserId();
        return PerformanceDataHelper.getWorkGroupHistory(userId, null);
    }

    @Override
    public WorkGroupMemberPerformanceAppVo getWorkGroupMemberPerformance(Long performanceGroupId, Integer year, Integer month) {
        WorkGroupMemberPerformanceAppVo workGroupMemberPerformanceAppVo;
        if (perfWorkGroupStatusMapper.selectCountByWorkGroupAndLockedOrComplete(performanceGroupId, year, month) == 0) {
            workGroupMemberPerformanceAppVo = performanceCurrentService.getWorkGroupMemberPerformance(null, performanceGroupId, year, month);
        } else {
            workGroupMemberPerformanceAppVo = performanceHistoryService.getWorkGroupMemberPerformance(null, performanceGroupId, year, month);
        }
        return workGroupMemberPerformanceAppVo;
    }

    @Override
    public UserPerformanceDetailAppVo getUserPerformanceDetail(Long userId, Integer year, Integer month) {
        boolean isMyself = false;
        if (null == userId) {
            userId = MyTokenUtils.getCurrentUserId();
            isMyself = true;
        }

        // 改版时间线，需要考虑年月为空的情况
        Calendar now = ReportHelper.getDateOnly();
        int nowYear = now.get(Calendar.YEAR);
        int nowMonth = now.get(Calendar.MONTH) + 1;
        if (year == null || month == null) {
            year = nowYear;
            month = nowMonth;
        }

        PerformanceUserDto performanceUserDto = userMapper.selectWithPerformanceByPerformanceWorkGroup(userId);
        UserPerformance curUserPerformance = userPerformanceMapper.selectByUserIdAndYearAndMonth(userId, year, month);
        List<CfgPerfHideManagerComment> cfgPerfHideManagerComments = cfgPerfHideManagerCommentMapper.selectAll();
        List<PerformanceWorkGroupDto> performanceWorkGroups = performanceWorkGroupMapper.selectAllWithProjectIdByActive();

        if (null == performanceUserDto) {
            logger.info("user not found. userId is {}", userId);
            throw new ParamException("用户不存在");
        }

        // 员工信息
        UserPerformanceDetailAppVo.UserInfo userInfo = new UserPerformanceDetailAppVo.UserInfo();
        userInfo.setName(performanceUserDto.getName());
        userInfo.setEmployeeNo(performanceUserDto.getEmployeeNo());

        // 绩效信息
        UserPerformanceDetailAppVo.PerformanceInfo performanceInfo = new UserPerformanceDetailAppVo.PerformanceInfo();
        if (null != curUserPerformance) {
            performanceInfo.setSelfPerformance(curUserPerformance.getSelfPerformance());
            performanceInfo.setMonthGoal(curUserPerformance.getMonthGoal());
            performanceInfo.setMonthGoalLastModifyTime(curUserPerformance.getMonthGoalLastModifyTime());
            performanceInfo.setSelfComment(curUserPerformance.getSelfComment());
            performanceInfo.setFinalPerformance(curUserPerformance.getFinalPerformance());

            if (!isMyself) {
                performanceInfo.setDirectManagerComment(curUserPerformance.getDirectManagerComment() == null ? null : MyEmojiUtil.resolveToEmojiFromByte(curUserPerformance.getDirectManagerComment()));
                Long lastModifyUser = curUserPerformance.getLastModifyUser();
                if (null != lastModifyUser) {
                    User user = userMapper.selectByPrimaryKey(lastModifyUser);
                    performanceInfo.setLastModifyUser(user.getName());
                }
            }

            boolean isHideManagerComment = UserPerformanceHelper.isHideManagerComment(cfgPerfHideManagerComments, performanceWorkGroups, curUserPerformance.getWorkGroupId(), curUserPerformance.getYear(), curUserPerformance.getMonth());
            if (!isHideManagerComment) {
                performanceInfo.setManagerComment(curUserPerformance.getManagerComment());
            }
            performanceInfo.setStatus(curUserPerformance.getStatus());

            // 没完成的绩效本人不能看到主管评分跟评语
            if (isMyself && !UserPerformance.Status.complete.equals(curUserPerformance.getStatus())) {
                performanceInfo.setFinalPerformance(null);
                performanceInfo.setManagerComment(null);
            }

            // B-的数据，返回到前台，恢复一次。
            if (isMyself && curUserPerformance.getSubBFlag()) {
                performanceInfo.setFinalPerformance(UserPerformance.Performance.B);
            }

            // 绩效已存在员工信息读取绩效数据
            userInfo.setWorkAge(curUserPerformance.getWorkAge() + "年");
            userInfo.setWorkAgeInKs(curUserPerformance.getWorkAgeInKs() + "年");
            userInfo.setPost(curUserPerformance.getPost());
            userInfo.setWorkGroup(curUserPerformance.getWorkGroupName());
            userInfo.setWorkStatus(curUserPerformance.getWorkStatus());
        } else {
            userInfo.setWorkAge(performanceUserDto.getWorkAge() + "年");
            userInfo.setWorkAgeInKs(performanceUserDto.getWorkAgeInKs() + "年");
            userInfo.setPost(performanceUserDto.getPost());
            userInfo.setWorkGroup(performanceUserDto.getWorkGroupName());
            userInfo.setWorkStatus(performanceUserDto.getWorkStatus());
        }

        // 历史
        List<SubPerformanceBaseVo.HistoryInfo> historyInfos = getUserPerformanceHistory(userId, isMyself);

        UserPerformanceDetailAppVo userPerformanceDetailAppVo = new UserPerformanceDetailAppVo();
        if (null != curUserPerformance) {
            userPerformanceDetailAppVo.setPerformanceId(curUserPerformance.getId());
        }
        userPerformanceDetailAppVo.setHistory(historyInfos);
        userPerformanceDetailAppVo.setUserInfo(userInfo);
        userPerformanceDetailAppVo.setPerformanceInfo(performanceInfo);

        // 将db存在的格式,转换为emoj字符,供前端显示
        String managementComment = userPerformanceDetailAppVo.getPerformanceInfo().getManagerComment();
        if (userPerformanceDetailAppVo.getPerformanceInfo().getManagerComment() != null) {
            userPerformanceDetailAppVo.getPerformanceInfo().setManagerComment(MyEmojiUtil.resolveToEmojiFromByte(managementComment));
        }
        String monthGoal = userPerformanceDetailAppVo.getPerformanceInfo().getMonthGoal();
        if (userPerformanceDetailAppVo.getPerformanceInfo().getMonthGoal() != null) {
            userPerformanceDetailAppVo.getPerformanceInfo().setMonthGoal(MyEmojiUtil.resolveToEmojiFromByte(monthGoal));
        }
        String selfComment = userPerformanceDetailAppVo.getPerformanceInfo().getSelfComment();
        if (userPerformanceDetailAppVo.getPerformanceInfo().getSelfComment() != null) {
            userPerformanceDetailAppVo.getPerformanceInfo().setSelfComment(MyEmojiUtil.resolveToEmojiFromByte(selfComment));
        }
        return userPerformanceDetailAppVo;
    }

    @Override
    public List<SubPerformanceBaseVo.HistoryInfo> getUserPerformanceHistory(Long userId, boolean isMyself) {
        if (null == userId) {
            userId = MyTokenUtils.getCurrentUserId();
        }

        Integer startDay = MySystemParamUtils.getSystemConfigWithDefaultValue(MySystemParamUtils.Key.PerformanceStartDay, MySystemParamUtils.DefaultValue.PerformanceStartDay);

        // 历史
        List<UserPerformance> userPerformances = userPerformanceMapper.selectAllByUserId(userId);
        Calendar startDate = ReportHelper.getDateOnly();
        Map<String, UserPerformance> userPerformanceMap = new HashMap<>();
        List<SubPerformanceBaseVo.HistoryInfo> historyInfos = new ArrayList<>();
        if (!userPerformances.isEmpty()) {
            startDate.set(userPerformances.get(0).getYear(), userPerformances.get(0).getMonth() - 1, 1);
            for (UserPerformance userPerformance : userPerformances) {
                String key = userPerformance.getYear() + "-" + userPerformance.getMonth();
                if (!userPerformanceMap.containsKey(key)) {
                    userPerformanceMap.put(key, userPerformance);
                }
            }
        } else {
            YearMonthDto yearMonthDto = userPerformanceMapper.selectWithYearMonthByLastComplete();
            if (null != yearMonthDto) {
                startDate.set(yearMonthDto.getYear(), yearMonthDto.getMonth() - 1, 1);
                startDate.add(Calendar.MONTH, 1);
            }
        }
        Calendar now = ReportHelper.getDateOnly();
        int nowYear = now.get(Calendar.YEAR);
        int nowMonth = now.get(Calendar.MONTH) + 1;
        int nowDay = now.get(Calendar.DATE);
        int curYear = startDate.get(Calendar.YEAR);
        int curMonth = 1;
        while (curMonth < startDate.get(Calendar.MONTH) + 1) {
            SubPerformanceBaseVo.HistoryInfo historyInfo = new SubPerformanceBaseVo.HistoryInfo();
            historyInfo.setYear(curYear);
            historyInfo.setMonth(curMonth);
            historyInfos.add(historyInfo);

            curMonth++;
        }
        while (curYear < nowYear || (curYear == nowYear && curMonth <= nowMonth)) {
            // 默认：进行中
            String status = SubPerformanceBaseVo.HistoryInfo.Status.processing;
            String key = curYear + "-" + curMonth;
            // 未开始
            if (curYear == nowYear && curMonth == nowMonth && now.get(Calendar.DAY_OF_MONTH) < startDay) {
                status = SubPerformanceBaseVo.HistoryInfo.Status.waitingForStart;
            } else if (userPerformanceMap.containsKey(key)) {
                UserPerformance userPerformance = userPerformanceMap.get(key);
                switch (userPerformance.getStatus()) {
                    // 已提交
                    case UserPerformance.Status.submitted:
                    case UserPerformance.Status.assessed:
                    case UserPerformance.Status.locked:
                        status = SubPerformanceBaseVo.HistoryInfo.Status.submitted;
                        break;
                    // 已完成
                    case UserPerformance.Status.complete:
                        status = isMyself && userPerformance.getSubBFlag() ? UserPerformance.Performance.B : userPerformance.getFinalPerformance();
                        break;
                    default:
                        break;
                }
            }

            // 延误中
            if (SubPerformanceBaseVo.HistoryInfo.Status.processing.equals(status) && UserPerformanceHelper.isDelay(curYear, curMonth, startDay)) {
                status = SubPerformanceBaseVo.HistoryInfo.Status.delay;
            }

            SubPerformanceBaseVo.HistoryInfo historyInfo = new SubPerformanceBaseVo.HistoryInfo();
            historyInfo.setYear(curYear);
            historyInfo.setMonth(curMonth);
            historyInfo.setStatus(status);
            if (curYear == nowYear && curMonth == nowMonth) {
                if (nowDay < startDay) {
                    historyInfo.setStartDay(startDay);
                }
            }
            historyInfos.add(historyInfo);

            startDate.add(Calendar.MONTH, 1);
            curYear = startDate.get(Calendar.YEAR);
            curMonth = startDate.get(Calendar.MONTH) + 1;
        }
        while (curMonth <= 12) {
            SubPerformanceBaseVo.HistoryInfo historyInfo = new SubPerformanceBaseVo.HistoryInfo();
            historyInfo.setYear(curYear);
            historyInfo.setMonth(curMonth);
            historyInfos.add(historyInfo);

            curMonth++;
        }

        return historyInfos;
    }

    private void checkCurrentMonthSubmitSupport(Integer year, Integer month, boolean checkStartDateFlag) {
        Calendar now = Calendar.getInstance();
        Integer nowYear = now.get(Calendar.YEAR);
        Integer nowMonth = now.get(Calendar.MONTH) + 1;
        Integer nowDay = now.get(Calendar.DATE);
        if (nowYear.equals(year) && nowMonth.equals(month)) {
            Integer startDay = MySystemParamUtils.getSystemConfigWithDefaultValue(MySystemParamUtils.Key.PerformanceStartDay, MySystemParamUtils.DefaultValue.PerformanceStartDay);
            if (nowDay < startDay) {
                logger.info("current performance is not support submit");
                throw new ParamException("当月绩效不允许在" + startDay + "号前提交");
            }
        }
        if (checkStartDateFlag) {
            Date startDate = MySystemParamUtils.getSystemConfigWithDefaultValue(MySystemParamUtils.Key.PerformanceSubmitStartDate, MySystemParamUtils.DefaultValue.PerformanceSubmitStartDate);
            if (now.getTime().before(startDate)) {
                DateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
                logger.info("current performance is not support submit");
                throw new ParamException("绩效不允许在" + format.format(startDate) + "前提交");
            }
        }
    }

    @Override
    public void addPerformanceDetail(UserPerformance userPerformance) {
        User logonUser = MyTokenUtils.getCurrentUser();

        if (null == userPerformance.getYear() || null == userPerformance.getMonth()) {
            logger.info("year or month is null");
            throw new ParamException("年月不能为空");
        }

        checkCurrentMonthSubmitSupport(userPerformance.getYear(), userPerformance.getMonth(), false);

        if (null == userPerformance.getUserId()) {
            userPerformance.setUserId(logonUser.getId());
        }

        PerformanceUserDto performanceUser = userMapper.selectWithPerformanceByPerformanceWorkGroup(userPerformance.getUserId());
        if (null == performanceUser) {
            logger.info("user not found. userId is {}", userPerformance.getUserId());
            throw new ParamException("用户不存在");
        }
        if (null == performanceUser.getWorkGroupId()) {
            logger.info("user performance work group not found. userId is {}", userPerformance.getUserId());
            throw new ParamException("用户没有配置绩效组");
        }

        UserPerformance checkUserPerformance = userPerformanceMapper.selectByUserIdAndYearAndMonth(userPerformance.getUserId(), userPerformance.getYear(), userPerformance.getMonth());
        if (null != checkUserPerformance) {
            logger.info("record is exists");
            throw new ParamException("当前用户" + userPerformance.getYear() + "年" + userPerformance.getMonth() + "月的绩效记录已存在");
        }

        Boolean workGroupIsLocked = perfWorkGroupStatusMapper.selectCountByWorkGroupAndLockedOrComplete(performanceUser.getWorkGroupId(), userPerformance.getYear(), userPerformance.getMonth()) > 0;
        if (workGroupIsLocked) {
            logger.info("work group is submitted, can not add new performance");
            throw new ParamException("您所在的组绩效已完成，不允许再提交" + userPerformance.getYear() + "年" + userPerformance.getMonth() + "月的绩效");
        }

        userPerformance.setWorkGroupId(performanceUser.getWorkGroupId());
        userPerformance.setParentGroup(performanceUser.getParentWorkGroupId());
        userPerformance.setWorkAge(performanceUser.getWorkAge());
        userPerformance.setWorkAgeInKs(performanceUser.getWorkAgeInKs());
        userPerformance.setPost(performanceUser.getPost());
        userPerformance.setWorkGroupName(performanceUser.getWorkGroupName());
        userPerformance.setWorkStatus(performanceUser.getWorkStatus());

        if (null != userPerformance.getFinalPerformance()) {
            userPerformance.setStatus(UserPerformance.Status.assessed);
        } else {
            userPerformance.setStatus(UserPerformance.Status.unsubmitted);
        }

        if (null != userPerformance.getMonthGoal()) {
            userPerformance.setMonthGoalLastModifyTime(new Date());
        }

        userPerformance.setCreateTime(new Date());
        userPerformance.setUpdateTime(new Date());

        convertEmojiToDBFormat(userPerformance);

        if (null != userPerformance.getManagerComment()) {
            userPerformance.setLastModifyUser(logonUser.getId());
            if (performanceUser.getLeaderId().equals(logonUser.getId())) {
                userPerformance.setDirectManagerComment(userPerformance.getManagerComment());
            }
        }
        userPerformanceMapper.insertSelective(userPerformance);

        if (!logonUser.getId().equals(userPerformance.getUserId())) {
            operateLogService.add(OperateLog.Type.user_performance_insert, String.format("%s 创建了 %s %s年%s月的绩效",
                    logonUser.getName(), performanceUser.getName(), userPerformance.getYear(), userPerformance.getMonth()), logonUser.getId());
        }
    }

    //对工作重点和自评进行字符长度判断
    private void subUserPerformanceComment(UserPerformance userPerformance) {
        if (userPerformance.getSelfComment() != null && userPerformance.getSelfComment().length() > 512) {
            userPerformance.setSelfComment(userPerformance.getSelfComment().substring(0, 512));
        }
        if (userPerformance.getMonthGoal() != null && userPerformance.getMonthGoal().length() > 512) {
            userPerformance.setMonthGoal(userPerformance.getMonthGoal().substring(0, 512));
        }
        if (userPerformance.getManagerComment() != null && userPerformance.getManagerComment().length() > 512) {
            userPerformance.setManagerComment(userPerformance.getManagerComment().substring(0, 512));
        }
    }

    // 处理emoji字符,转换为db可写入的格式
    private void convertEmojiToDBFormat(UserPerformance userPerformance) {
        if (userPerformance.getSelfComment() != null) {
            userPerformance.setSelfComment(MyEmojiUtil.resolveToByteFromEmoji(userPerformance.getSelfComment()));
        }
        if (userPerformance.getMonthGoal() != null) {
            userPerformance.setMonthGoal(MyEmojiUtil.resolveToByteFromEmoji(userPerformance.getMonthGoal()));
        }
        if (userPerformance.getManagerComment() != null) {
            userPerformance.setManagerComment(MyEmojiUtil.resolveToByteFromEmoji(userPerformance.getManagerComment()));
        }
    }

    // 处理emoji字符,转换为前端可读的格式
    private void convertDBFormatToEmoji(UserPerformance userPerformance) {
        if (userPerformance.getSelfComment() != null) {
            userPerformance.setSelfComment(MyEmojiUtil.resolveToEmojiFromByte(userPerformance.getSelfComment()));
        }
        if (userPerformance.getMonthGoal() != null) {
            userPerformance.setMonthGoal(MyEmojiUtil.resolveToEmojiFromByte(userPerformance.getMonthGoal()));
        }
        if (userPerformance.getManagerComment() != null) {
            userPerformance.setManagerComment(MyEmojiUtil.resolveToEmojiFromByte(userPerformance.getManagerComment()));
        }
    }

    @Transactional
    @Override
    public void updatePerformanceDetail(Long id, UserPerformance userPerformance) {

        if (null == id) {
            logger.info("id is null...");
            throw new ParamException("id不能为空");
        }

        User logonUser = MyTokenUtils.getCurrentUser();

        userPerformance.setId(id);

        UserPerformanceDto preUserPerformance = userPerformanceMapper.selectWithUserNameByPrimaryKey(id);
        if (null == preUserPerformance) {
            logger.info("record is not exists...");
            throw new ParamException("当前绩效记录不存在");
        }

        // 只有未提交状态下个人才能修改绩效
        if (!UserPerformance.Status.unsubmitted.equals(preUserPerformance.getStatus())) {
            if (preUserPerformance.getUserId().equals(logonUser.getId())) {
                logger.info("record is submitted...");
                throw new ParamException("当前绩效" + preUserPerformance.getStatus() + "，不允许修改");
            }
        }

        if (UserPerformance.Status.locked.equals(preUserPerformance.getStatus())) {
            Long leaderId = preUserPerformance.getLeaderId();
            if (preUserPerformance.getUserId().equals(logonUser.getId()) || logonUser.getId().equals(leaderId)) {
                logger.info("record is submitted... recodeUserId:{} logonUserId:{} leader:{}", preUserPerformance.getUserId(), logonUser.getId(), leaderId);
                throw new ParamException("当前绩效已锁定，没有权限修改");
            }
        } else if (UserPerformance.Status.complete.equals(preUserPerformance.getStatus())) {
            logger.info("record is complete...");
            throw new ParamException("当前绩效已完成，不允许修改");
        }

        if (!MyTokenUtils.isBoss(logonUser)) {
            if (UserPerformance.FmConfirmedStatus.firstConfirmed.equals(preUserPerformance.getFmConfirmedStatus())) {
                List<PerformanceWorkGroupDto> managerGroups = performanceWorkGroupMapper.selectAllWithProjectIdByManagerId(logonUser.getId());
                if (!managerGroups.stream().anyMatch(g -> null != g.getProjectId())) {
                    logger.info("record is confirm...");
                    throw new ParamException("固化成员绩效已确认，不允许修改");
                }
            } else if (UserPerformance.FmConfirmedStatus.secondConfirmed.equals(preUserPerformance.getFmConfirmedStatus())) {
                logger.info("record is confirm...");
                throw new ParamException("固化成员绩效已确认，不允许修改");
            }
        }

        // 月度目标有变化记录修改时间
        if (null != userPerformance.getMonthGoal() && !userPerformance.getMonthGoal().equals(preUserPerformance.getMonthGoal())) {
            userPerformance.setMonthGoalLastModifyTime(new Date());
        }

        // 处理emoji
        convertEmojiToDBFormat(userPerformance);
        if (null != userPerformance.getFinalPerformance()) {
            PerformanceUserDto performanceUserDto = userMapper.selectWithPerformanceByPerformanceWorkGroup(preUserPerformance.getUserId());
            userPerformance.setWorkAge(performanceUserDto.getWorkAge());
            userPerformance.setWorkAgeInKs(performanceUserDto.getWorkAgeInKs());
            userPerformance.setPost(performanceUserDto.getPost());
            userPerformance.setWorkGroupName(performanceUserDto.getWorkGroupName());
            userPerformance.setWorkStatus(performanceUserDto.getWorkStatus());

            if (UserPerformance.Status.unsubmitted.equals(preUserPerformance.getStatus()) || UserPerformance.Status.submitted.equals(preUserPerformance.getStatus())) {
                userPerformance.setStatus(UserPerformance.Status.assessed);
            }

            operateLogService.add(OperateLog.Type.user_performance_modify, String.format("%s 修改了 %s 的绩效(%d)，final_performance从 %s到%s。",
                    logonUser.getName(), preUserPerformance.getUserName(), id,
                    preUserPerformance.getFinalPerformance(), userPerformance.getFinalPerformance()), logonUser.getId());
        }

        // 修改待确认固化成员绩效发送消息通知
        if (UserPerformance.FmConfirmedStatus.firstUnconfirmed.equals(preUserPerformance.getFmConfirmedStatus())) {
            FmUserRoleDto fmUserRole = fmUserRoleMapper.selectUserFMConfirmerId(preUserPerformance.getUserId());
            if (null != fmUserRole) {
                String content = logonUser.getName() + "修改了固化成员" + preUserPerformance.getUserName() + "的绩效";
                UserMessage userMessage = UserMessageHelper.getSubmitFixMemberUserMessage(fmUserRole.getUserId(), content, preUserPerformance.getYear(), preUserPerformance.getMonth());
                userMessageService.add(userMessage);
            }
        } else if (UserPerformance.FmConfirmedStatus.secondUnconfirmed.equals(userPerformance.getFmConfirmedStatus())) {
            PerformanceWorkGroupDto performanceWorkGroup = performanceWorkGroupMapper.selectWithProjectIdByProjectId(preUserPerformance.getFmProjectId());
            if (null != performanceWorkGroup) {
                String content = logonUser.getName() + "修改了固化成员" + preUserPerformance.getUserName() + "的绩效";
                UserMessage userMessage = UserMessageHelper.getSubmitFixMemberUserMessage(performanceWorkGroup.getPerformanceManagerId(), content, preUserPerformance.getYear(), preUserPerformance.getMonth());
                userMessageService.add(userMessage);
            }
        }

        //设置直接主管评语
        if (null != userPerformance.getManagerComment()) {
            if (logonUser.getId().equals(preUserPerformance.getLeaderId())) {
                userPerformance.setDirectManagerComment(userPerformance.getManagerComment());
            }
            if (!logonUser.getId().equals(preUserPerformance.getUserId()) && !userPerformance.getManagerComment().equals(preUserPerformance.getManagerComment())) {
                userPerformance.setLastModifyUser(logonUser.getId());
            }
        }

        userPerformance.setUpdateTime(new Date());
        userPerformanceMapper.updateByPrimaryKeySelective(userPerformance);
    }

    @Override
    public void batchHandlePerformanceDetail(List<UserPerformance> userPerformanceList, User logonUser, int year, int month) {
        checkCurrentMonthSubmitSupport(year, month, false);

        if (userPerformanceList == null || userPerformanceList.size() == 0) {
            return;
        }
        //将 insert和 update Performances 分类
        List<UserPerformance> insertPerformanceList = new ArrayList<>();
        List<UserPerformance> updatePerformanceList = new ArrayList<>();
        List<Long> updateIds = new ArrayList<>();
        List<Long> allUsers = new ArrayList<>();

        // 当前组绩效还未被提交，开始处理
        for (UserPerformance userPerformance : userPerformanceList) {
            if (userPerformance.getId() == null) {
                insertPerformanceList.add(userPerformance);
            } else {
                updateIds.add(userPerformance.getId());
                updatePerformanceList.add(userPerformance);
            }
            allUsers.add(userPerformance.getUserId());
        }

        Map<Long, PerformanceUserDto> userIdPerformanceUserMap = getUserIdPerformanceUserMap(allUsers);

        //操作日志记录
        List<OperateLog> operateLogList = new ArrayList<>();

        // 处理：批量更新
        if (updatePerformanceList.size() != 0) {

            List<UserPerformanceDto> prePerformanceList = userPerformanceMapper.selectPerformancesByPKs(updateIds);
            //查找平台负责人的id
            Long platLeaderId = userPerformanceMapper.selectPlatLeaderIdByUserId(updatePerformanceList.get(0).getUserId());

            //批量发送消息
            List<UserMessage> userMessageList = new ArrayList<>();

            Map<Long, FmUserRoleDto> userIdFmUserRoleDtoMap = getUserIdFmUserRoleMap(updatePerformanceList);

            Map<Long, PerformanceWorkGroupDto> projectIdPerformanceWorkGroupDtoMap = getProjectIdPerformanceWorkGroupMap(userIdFmUserRoleDtoMap);
            Map<UserPerformance, UserPerformanceDto> recordMap = matchRecordById(updatePerformanceList, prePerformanceList);

            for (Map.Entry<UserPerformance, UserPerformanceDto> recordEntry : recordMap.entrySet()) {

                // 直接上级
                estimateLeaderCapability(recordEntry.getValue(), logonUser);
                // 已经被项目确认，不能修改
                estimatePLAndLeaderCapability(recordEntry.getValue(), logonUser, platLeaderId);

                // 拷贝以前信息
                String finalPerformance = recordEntry.getKey().getFinalPerformance();
                String managerComment = MyEmojiUtil.resolveToByteFromEmoji(recordEntry.getKey().getManagerComment());
                BeanUtils.copyProperties(recordEntry.getValue(), recordEntry.getKey());
                recordEntry.getKey().setFinalPerformance(finalPerformance);
                recordEntry.getKey().setManagerComment(managerComment);
                recordEntry.getKey().setYear(year);
                recordEntry.getKey().setMonth(month);

                if (managerComment != null && !managerComment.equals(recordEntry.getValue().getManagerComment())) {
                    recordEntry.getKey().setLastModifyUser(logonUser.getId());
                }

                if (recordEntry.getValue().getLeaderId().equals(logonUser.getId())) {
                    recordEntry.getKey().setDirectManagerComment(managerComment);
                }

                // 月度目标有变化记录修改时间
                if (null != recordEntry.getKey().getMonthGoal() && !recordEntry.getKey().getMonthGoal().equals(recordEntry.getValue().getMonthGoal())) {
                    recordEntry.getKey().setMonthGoalLastModifyTime(new Date());
                }
                convertEmojiToDBFormat(recordEntry.getKey());

                if (recordEntry.getKey().getFinalPerformance() != null) {
                    PerformanceUserDto performanceUserDto = userIdPerformanceUserMap.get(recordEntry.getKey().getUserId());
                    if (performanceUserDto != null) {
                        recordEntry.getKey().setWorkAge(performanceUserDto.getWorkAge());
                        recordEntry.getKey().setWorkAgeInKs(performanceUserDto.getWorkAgeInKs());
                        recordEntry.getKey().setPost(performanceUserDto.getPost());
                        recordEntry.getKey().setWorkGroupName(performanceUserDto.getWorkGroupName());
                        recordEntry.getKey().setWorkStatus(performanceUserDto.getWorkStatus());
                    }
                    if (UserPerformance.Status.unsubmitted.equals(recordEntry.getValue().getStatus()) || UserPerformance.Status.submitted.equals(recordEntry.getValue().getStatus())) {
                        recordEntry.getKey().setStatus(UserPerformance.Status.assessed);
                    }
                } else if (null != recordEntry.getValue().getManagerComment()) {
                    recordEntry.getKey().setStatus(UserPerformance.Status.unsubmitted);
                }

                // 添加操作日志
                OperateLog operateLog = new OperateLog(OperateLog.Type.user_performance_modify, String.format("%s 修改了 %s 的绩效(%d)，final_performance从 %s到%s，manager_comment从 %s到%s",
                        logonUser.getName(), recordEntry.getValue().getUserName(), recordEntry.getKey().getId(),
                        recordEntry.getValue().getFinalPerformance(), recordEntry.getKey().getFinalPerformance(),
                        recordEntry.getValue().getManagerComment(), recordEntry.getKey().getManagerComment()), logonUser.getId());
                operateLogList.add(operateLog);

                // 添加消息通知
                if (UserPerformance.FmConfirmedStatus.firstUnconfirmed.equals(recordEntry.getValue().getFmConfirmedStatus())) {
                    FmUserRoleDto fmUserRole = userIdFmUserRoleDtoMap.get(recordEntry.getKey().getUserId());
                    if (null != fmUserRole) {
                        String content = logonUser.getName() + "修改了固化成员" + recordEntry.getValue().getUserName() + "的绩效";
                        UserMessage userMessage = UserMessageHelper.getSubmitFixMemberUserMessage(fmUserRole.getUserId(), content, recordEntry.getValue().getYear(), recordEntry.getValue().getMonth());
                        userMessageList.add(userMessage);
                    }
                } else if (UserPerformance.FmConfirmedStatus.secondUnconfirmed.equals(recordEntry.getKey().getFmConfirmedStatus())) {
                    Long projectId = userIdFmUserRoleDtoMap.get(recordEntry.getKey().getUserId()).getProjectId();
                    PerformanceWorkGroupDto performanceWorkGroup = projectIdPerformanceWorkGroupDtoMap.get(projectId);
                    if (null != performanceWorkGroup) {
                        String content = logonUser.getName() + "修改了固化成员" + recordEntry.getValue().getUserName() + "的绩效";
                        UserMessage userMessage = UserMessageHelper.getSubmitFixMemberUserMessage(performanceWorkGroup.getPerformanceManagerId(), content, recordEntry.getValue().getYear(), recordEntry.getValue().getMonth());
                        userMessageList.add(userMessage);
                    }
                }
                recordEntry.getKey().setUpdateTime(new Date());
            }
            List<UserPerformance> resultUpdateList = new ArrayList<>(recordMap.keySet());
            userPerformanceMapper.batchUpdateUserPerformanceInfoByPks(resultUpdateList);
            userMessageService.batchAdd(userMessageList);
        }

        //处理：批量插入

        //确定数据库中不存在 用户记录
        checkPerformance(insertPerformanceList, year, month);

        for (UserPerformance insert : insertPerformanceList) {
            if (null == insert.getUserId()) {
                insert.setUserId(MyTokenUtils.getCurrentUserId());
            }

            if (null == userIdPerformanceUserMap.get(insert.getUserId())) {
                logger.info("user not found. userId is {}", insert.getUserId());
                throw new ParamException("用户不存在");
            }
            PerformanceUserDto performanceUser = userIdPerformanceUserMap.get(insert.getUserId());
            if (null == performanceUser.getWorkGroupId()) {
                logger.info("user performance work group not found. userId is {}", insert.getUserId());
                throw new ParamException("用户没有配置绩效组");
            }
            OperateLog operateLog = new OperateLog(OperateLog.Type.user_performance_insert, String.format("%s 创建了 %s %s年%s月的绩效",
                    logonUser.getName(), userIdPerformanceUserMap.get(insert.getUserId()).getName(), insert.getYear(), insert.getMonth()), logonUser.getId());
            operateLogList.add(operateLog);

            insert.setYear(year);
            insert.setMonth(month);
            insert.setWorkGroupId(performanceUser.getWorkGroupId());
            insert.setParentGroup(performanceUser.getParentWorkGroupId());

            insert.setWorkAge(performanceUser.getWorkAge());
            insert.setWorkAgeInKs(performanceUser.getWorkAgeInKs());
            insert.setPost(performanceUser.getPost());
            insert.setWorkGroupName(performanceUser.getWorkGroupName());
            insert.setWorkStatus(performanceUser.getWorkStatus());

            if (null != insert.getFinalPerformance()) {
                insert.setStatus(UserPerformance.Status.assessed);
            } else if (null != insert.getManagerComment()) {
                insert.setStatus(UserPerformance.Status.unsubmitted);
            }

            insert.setCreateTime(new Date());
            insert.setUpdateTime(new Date());

            convertEmojiToDBFormat(insert);

            if (null != insert.getManagerComment()) {
                if (performanceUser.getLeaderId().equals(logonUser.getId())) {
                    insert.setDirectManagerComment(insert.getManagerComment());
                }
                insert.setLastModifyUser(logonUser.getId());
            }
        }
        if (insertPerformanceList.size() != 0) {
            userPerformanceMapper.batchInsertUserPerformance(insertPerformanceList);
        }
        operateLogService.batchInsert(operateLogList, logonUser.getId());
    }

    private void estimateLeaderCapability(UserPerformanceDto member, User logonUser) {

        // 直接上级 不能修改绩效
        if (logonUser.getId().equals(member.getLeaderId())) {
            if (UserPerformance.Status.locked.equals(member.getStatus())) {
                logger.info("record was submitted... logonUserId:{} leader:{}", logonUser.getId(), logonUser.getId());
                throw new ParamException("当前组的绩效已锁定，没有权限修改");
            } else if (UserPerformance.Status.complete.equals(member.getStatus())) {
                logger.info("record is completed...");
                throw new ParamException("当前绩效已完成，不允许修改");
            }
        }

    }

    // 未被项目确认的固化成员才能修改
    private boolean estimatePLAndLeaderCapability(UserPerformanceDto userPerformanceDto, User logonUser, Long platLeaderId) {
        // 非老k 非平台负责人
        if (!logonUser.getId().equals(platLeaderId) || !MyTokenUtils.isBoss(logonUser)) {
            // String firstComplete,secondUnconfirmed,secondConfirmed,secondComplete 已被 lock 过滤
            if (UserPerformance.FmConfirmedStatus.firstConfirmed.equals(userPerformanceDto.getFmConfirmedStatus())) {
                logger.info("record was confirm by confirmer");
                throw new ParamException("当前绩效已被项目审核");
            }
        }
        return true;
    }

    // 获取绩效用户 userId -> performanceUserDto ==> Map
    private Map<Long, PerformanceUserDto> getUserIdPerformanceUserMap(List<Long> userIds) {
        Map<Long, PerformanceUserDto> result = new HashMap<>();
        if (userIds.size() == 0) {
            return result;
        }
        List<PerformanceUserDto> performanceUserDtoList = userMapper.selectWithPerformanceByPWGUserIdInList(userIds);
        for (PerformanceUserDto performanceUserDto : performanceUserDtoList) {
            result.put(performanceUserDto.getUserId(), performanceUserDto);
        }
        return result;
    }

    private Map<Long, FmUserRoleDto> getUserIdFmUserRoleMap(List<UserPerformance> userPerformanceList) {
        Map<Long, FmUserRoleDto> userIdFmUserRoleMap = new HashMap<>();

        List<FmUserRoleDto> roleDtoList = fmUserRoleMapper.selectManagerByMemberIds(userPerformanceList, FmRole.Role.projectFixFirstConfirmer);
        for (FmUserRoleDto fmUserRole : roleDtoList) {
            userIdFmUserRoleMap.put(fmUserRole.getUserId(), fmUserRole);
        }

        return userIdFmUserRoleMap;
    }

    private Map<Long, PerformanceWorkGroupDto> getProjectIdPerformanceWorkGroupMap(Map<Long, FmUserRoleDto> userIdFmUserRoleMap) {
        Map<Long, PerformanceWorkGroupDto> result = new HashMap<>();
        List<Long> projectIds = new ArrayList<>();
        for (Map.Entry<Long, FmUserRoleDto> temp : userIdFmUserRoleMap.entrySet()) {
            projectIds.add(temp.getValue().getProjectId());
        }
        if (projectIds.size() != 0) {
            List<PerformanceWorkGroupDto> performanceWorkGroupDtoList = performanceWorkGroupMapper.selectWithProjectIdByProjectIds(projectIds);
            for (PerformanceWorkGroupDto p : performanceWorkGroupDtoList) {
                result.put(p.getProjectId(), p);
            }
        }
        return result;
    }


    private Map<UserPerformance, UserPerformanceDto> matchRecordById(List<UserPerformance> userPerformanceList,
                                                                     List<UserPerformanceDto> userPerformanceDtoList) {
        Map<UserPerformance, UserPerformanceDto> resultMap = new HashMap<>();
        if (userPerformanceDtoList == null) {
            logger.info("record is not exists...");
            throw new ParamException("绩效记录不存在");
        }

        boolean isExitFlag = false;
        for (UserPerformance userPerformance : userPerformanceList) {
            for (UserPerformanceDto userPerformanceDto : userPerformanceDtoList) {
                if (userPerformance.getId().equals(userPerformanceDto.getId())) {
                    resultMap.put(userPerformance, userPerformanceDto);
                    isExitFlag = true;
                }
            }
            if (!isExitFlag) {
                logger.info("record is not exists...");
                throw new ParamException(userPerformance.getUserId() + "的绩效记录不存在");
            }
        }
        return resultMap;
    }

    private void checkPerformance(List<UserPerformance> userPerformances, int year, int month) {
        List<Long> userIds = new ArrayList<>();
        for (UserPerformance u : userPerformances) {
            userIds.add(u.getUserId());
        }
        if (userIds.size() != 0) {
            List<UserPerformance> userPerformanceList = userPerformanceMapper.selectByUserIdsAndYearAndMonth(userIds, year, month);
            if (null != userPerformanceList && userPerformanceList.size() != 0) {
                logger.info("record is exists");
                throw new ParamException("用户" + year + "年" + month + "月的绩效记录已存在");
            }
        }
    }


    @Override
    public void submitSelfPerformance(Long id) {
        if (null == id) {
            logger.info("id is null...");
            throw new ParamException("id不能为空");
        }

        User logonUser = MyTokenUtils.getCurrentUser();
        UserPerformance userPerformance = userPerformanceMapper.selectByPrimaryKey(id);
        if (null == userPerformance) {
            logger.info("record is not exists...");
            throw new ParamException("当前绩效记录不存在");
        }
        if (null == userPerformance.getSelfComment() || null == userPerformance.getSelfPerformance() || null == userPerformance.getMonthGoal()) {
            logger.info("self_performance or self_comment or month_goal is not null...");
            throw new ParamException("月度目标和自评不能为空");
        }
        if (!userPerformance.getUserId().equals(logonUser.getId())) {
            logger.info("can not submit other people performance");
            throw new ParamException("不能提交别人的绩效记录");
        }
        // 只有未提交状态下才能提交个人绩效
        if (!UserPerformance.Status.unsubmitted.equals(userPerformance.getStatus())) {
            if (userPerformance.getUserId().equals(logonUser.getId())) {
                logger.info("record can not submit. current status is {}", userPerformance.getStatus());
                throw new ParamException(String.format("%s状态下，不允许提交个人绩效", userPerformance.getStatus()));
            }
        }

        userPerformance.setStatus(UserPerformance.Status.submitted);
        userPerformance.setUpdateTime(new Date());

        userPerformanceMapper.updateByPrimaryKeySelective(userPerformance);
    }

    @Override
    public void submitWorkGroup(Long workGroupId, Integer year, Integer month) {
        checkCurrentMonthSubmitSupport(year, month, true);

        User logonUser = MyTokenUtils.getCurrentUser();
        if (null == workGroupId) {
            updateWorkGroup(logonUser, year, month);
        } else {
            // 上级负责人代提交
            updateWorkGroup(workGroupId, logonUser, year, month);
        }
    }

    @Transactional
    @Override
    public void confirmPerformance(Integer year, Integer month) {
        checkCurrentMonthSubmitSupport(year, month, true);

        User logonUser = MyTokenUtils.getCurrentUser();
        if (!MyTokenUtils.isBoss(logonUser)) {
            throw new ParamException("没有权限进行本操作");
        }
        updateWorkGroup(logonUser, year, month);
        userPerformanceMapper.updateStatusToCompleteByYearAndMonth(year, month);
        perfWorkGroupStatusMapper.updateStatusToCompleteByYearAndMonth(year, month);
        operateLogService.add(OperateLog.Type.all_performance_confirm, String.format("%s 确认了所有绩效", logonUser.getName()), logonUser.getId());
    }

    @Override
    @Transactional
    public void updateWorkGroup(User logonUser, Integer year, Integer month) {
        List<PerformanceWorkGroupDto> allGroups = performanceWorkGroupMapper.selectAllWithProjectIdByActive();
        List<PerformanceWorkGroupDto> managerGroups = allGroups.stream().filter(g -> logonUser.getId().equals(g.getPerformanceManagerId())).collect(Collectors.toList());
        List<UserPerformanceDto> allUserPerformances = userPerformanceMapper.selectAllByYearAndMonth(year, month);
        List<PerformanceUserDto> allUsers = userMapper.selectAllEntityWithPerformanceByPerformanceWorkGroup();
        List<WorkGroupDto> allPerformanceGroups = performanceWorkGroupMapper.selectAllByActive();
        List<FmMemberDto> allFmMembers = fmMemberMapper.selectAll();
        List<FmGroupConfirmInfoVo> allFmGroupConfirmInfoList = fmGroupConfirmInfoMapper.selectAllByYearMonth(year, month);
        List<ProjectMaxMemberVo> projects = projectMapper.selectAllWithMaxMember();
        List<PerfWorkGroupStatus> allPerfWorkGroupStatus = perfWorkGroupStatusMapper.selectByYearAndMonth(year, month);

        // 把记录放到map里方便查找
        Map<Long, UserPerformanceDto> userPerformanceMap = allUserPerformances.stream().collect(Collectors.toMap(p -> p.getUserId(), p -> p));
        Map<Long, String> performanceWorkGroupStatusMap = PerformanceDataHelper.getPerformanceWorkGroupStatusMap(allGroups, allPerfWorkGroupStatus);
        Map<Long, FmMemberDto> fmMemberMap = allFmMembers.stream().collect((Collectors.toMap(m -> m.getUserId(), m -> m)));
        Map<Long, PerformanceWorkGroupDto> allGroupMap = allGroups.stream().collect(Collectors.toMap(g -> g.getId(), g -> g));
        Map<Long, List<FmGroupConfirmInfo>> allFmGroupConfirmInfoListMap = allFmGroupConfirmInfoList.stream().collect(Collectors.groupingBy(i -> i.getPerformanceWorkGroupId()));

        // 重设员工绩效组
        PerformanceDataHelper.resetUserPerformanceWorkGroup(allUsers, userPerformanceMap);

        // 需要与项目确认固化组
        List<FmGroupConfirmInfo> fmGroups = new ArrayList<>();
        Set<Long> projectIds = new HashSet<>();

        // 待数据库处理记录
        List<UserPerformanceDto> waittingForUpdateRecords = new ArrayList<>();
        List<UserPerformanceDto> waittingFroInsertRecords = new ArrayList<>();

        List<UserPerformanceDto> validRecords = new ArrayList<>(); //有效记录，计算比例用
        Integer strictType = PerformanceWorkGroup.GroupStrictType.normalNum;

        // 查找管理组的strictType list
        List<Integer> groupStrictTypes = new ArrayList<>();

        List<Long> insertPerfWorkGroupIds = new ArrayList<>();
        for (PerformanceWorkGroupDto performanceWorkGroup : managerGroups) {
            // 找出根目录，初始化树
            WorkGroupDto performanceRootGroup = PerformanceTreeHelper.getWorkGroupTree(performanceWorkGroup.getId(), allPerformanceGroups, allUsers, false);
            //根据根树获取所有绩效组ID，
            insertPerfWorkGroupIds.add(performanceRootGroup.getId());
            PerformanceTreeHelper.getAllSubPerfWorkGroupIdByRootTree(insertPerfWorkGroupIds, performanceRootGroup);
            // 本组负责人提交逻辑
            // 获取直接下属
            List<PerformanceUserDto> directMembers = performanceRootGroup.getMembers();
            // 获取下属团队
            List<WorkGroupDto> directGroups = performanceRootGroup.getChildWorkGroups();


            // 找出未提交和已提交的子团队  update -> 未提交子团队不再处理为不参与，会正常提交
            List<WorkGroupDto> unSubmittedWorkGroups = new ArrayList<>();
            List<WorkGroupDto> submittedWorkGroups = new ArrayList<>();

            for (WorkGroupDto workGroupDto : directGroups) {
                String status = PerformanceDataHelper.getPerformanceWorkGroupStatus((workGroupDto.getId()), performanceWorkGroupStatusMap);
                if (status.equals(SubPerformanceAppVo.HistoryInfo.Status.processing)) {
                    unSubmittedWorkGroups.add(workGroupDto);
                } else {
                    submittedWorkGroups.add(workGroupDto);
                }
            }

            // 处理直接下属绩效
            for (PerformanceUserDto user : directMembers) {
                if (userPerformanceMap.containsKey(user.getUserId())) {
                    UserPerformanceDto userPerformance = userPerformanceMap.get(user.getUserId());

                    if (UserPerformance.Status.unsubmitted.equals(userPerformance.getStatus()) || UserPerformance.Status.submitted.equals(userPerformance.getStatus())) {
                        // 未提交，已提交的记录变为作废，放入待更新列表
                        userPerformance.setFinalPerformance(UserPerformance.Performance.invalided);
                        userPerformance.setStatus(UserPerformance.Status.locked);
                        if (performanceWorkGroup.getProjectConfirmFlag()) {
                            PerformanceDataHelper.setUserPerformanceFmGroupInfo(userPerformance, fmMemberMap, null, projectIds);
                        }
                        waittingForUpdateRecords.add(userPerformance);
                    } else if (UserPerformance.Status.assessed.equals(userPerformance.getStatus())) {
                        // 已评定的记录变为待确认，放入待更新列表
                        userPerformance.setStatus(UserPerformance.Status.locked);
                        if (performanceWorkGroup.getProjectConfirmFlag()) {
                            PerformanceDataHelper.setUserPerformanceFmGroupInfo(userPerformance, fmMemberMap, null, projectIds);
                        }
                        waittingForUpdateRecords.add(userPerformance);

                        validRecords.add(userPerformance);
                    } else {
                        validRecords.add(userPerformance);
                        if (performanceWorkGroup.getProjectConfirmFlag()) {
                            PerformanceDataHelper.setUserPerformanceFmGroupInfo(userPerformance, fmMemberMap, null, projectIds);
                            waittingForUpdateRecords.add(userPerformance);
                        }
                    }
                } else {
                    // 空记录变为作废，放入待新建列表
                    UserPerformanceDto userPerformance = PerformanceDataHelper.getNewInvalidedUserPerformance(user, year, month, UserPerformance.Status.locked);
                    if (performanceWorkGroup.getProjectConfirmFlag()) {
                        PerformanceDataHelper.setUserPerformanceFmGroupInfo(userPerformance, fmMemberMap, null, projectIds);
                    }
                    waittingFroInsertRecords.add(userPerformance);
                }
            }

            // 处理未提交子团队绩效
            Map<Long, Set<Long>> subProjectIdsMap = new HashMap<>();
            for (WorkGroupDto workGroupDto : unSubmittedWorkGroups) {
                Set<Long> subProjectIds = new HashSet<>();
                List<PerformanceUserDto> allSubGroupMembers = PerformanceTreeHelper.getAllMembersByRootWorkGroup(workGroupDto);

                // 未提交子团队也加入计算记录
                List<UserPerformanceDto> allSubWorkGroupUserPerformances = PerformanceDataHelper.getSubUserPerformances(workGroupDto.getId(), allUserPerformances);
                validRecords.addAll(allSubWorkGroupUserPerformances);

                for (PerformanceUserDto user : allSubGroupMembers) {
                    if (userPerformanceMap.containsKey(user.getUserId())) {
                        // 作废已有记录，放入待更新列表
                        UserPerformanceDto userPerformance = userPerformanceMap.get(user.getUserId());
                        if (userPerformance.getFinalPerformance() == null || userPerformance.getFinalPerformance().equals("")) {
                            userPerformance.setFinalPerformance(UserPerformance.Performance.invalided);
                        }
//                        userPerformance.setFinalPerformance(UserPerformance.Performance.invalided);
                        userPerformance.setStatus(UserPerformance.Status.locked);
                        if (performanceWorkGroup.getProjectConfirmFlag()) {
                            PerformanceDataHelper.setUserPerformanceFmGroupInfo(userPerformance, fmMemberMap, null, subProjectIds);
                        }
                        waittingForUpdateRecords.add(userPerformance);
                    } else {
                        // 空记录变为作废，放入待新建列表，如果当前员工所在绩效组已提交则不做处理
                        String status = PerformanceDataHelper.getPerformanceWorkGroupStatus(user.getWorkGroupId(), performanceWorkGroupStatusMap);
                        if (status.equals(SubPerformanceAppVo.HistoryInfo.Status.processing)) {
                            UserPerformanceDto userPerformance = PerformanceDataHelper.getNewInvalidedUserPerformance(user, year, month, UserPerformance.Status.locked);
                            if (performanceWorkGroup.getProjectConfirmFlag()) {
                                PerformanceDataHelper.setUserPerformanceFmGroupInfo(userPerformance, fmMemberMap, null, subProjectIds);
                            }
                            waittingFroInsertRecords.add(userPerformance);
                        }
                    }
                }
                subProjectIdsMap.put(workGroupDto.getId(), subProjectIds);
                projectIds.addAll(subProjectIds);
            }


            // 不处理已提交团队，放入有效记录
            for (WorkGroupDto workGroupDto : submittedWorkGroups) {
                List<UserPerformanceDto> allSubWorkGroupUserPerformances = PerformanceDataHelper.getSubUserPerformances(workGroupDto.getId(), allUserPerformances);
                validRecords.addAll(allSubWorkGroupUserPerformances);
                if (performanceWorkGroup.getProjectConfirmFlag()) {
                    // 更新固化成员绩效
                    for (UserPerformanceDto userPerformance : allSubWorkGroupUserPerformances) {
                        PerformanceDataHelper.setUserPerformanceFmGroupInfo(userPerformance, fmMemberMap, null, projectIds);
                        waittingForUpdateRecords.add(userPerformance);
                    }
                }
            }

            // 固化组处理
            if (performanceWorkGroup.getProjectConfirmFlag()) {
                if (!allFmGroupConfirmInfoListMap.containsKey(performanceWorkGroup.getId())) {
                    List<FmGroupConfirmInfo> curFmGroups = PerformanceDataHelper.getFmGroups(year, month, performanceWorkGroup, performanceRootGroup, projectIds, allFmMembers, projects, true);
                    if (!curFmGroups.isEmpty()) {
                        fmGroups.addAll(curFmGroups);
                    }
                }
//                // 未提交下属团队也要处理
                for (WorkGroupDto workGroupDto : unSubmittedWorkGroups) {
                    PerformanceWorkGroupDto subPerformanceWorkGroup = allGroupMap.get(workGroupDto.getId());
                    Set<Long> subProjectIds = subProjectIdsMap.get(workGroupDto.getId());
                    if (subPerformanceWorkGroup.getProjectConfirmFlag() && !allFmGroupConfirmInfoListMap.containsKey(subPerformanceWorkGroup.getId())) {
                        List<FmGroupConfirmInfo> curFmGroups = PerformanceDataHelper.getFmGroups(year, month, subPerformanceWorkGroup, workGroupDto, subProjectIds, allFmMembers, projects, true);
                        if (!curFmGroups.isEmpty()) {
                            fmGroups.addAll(curFmGroups);
                        }
                    }
                }
            }

            groupStrictTypes.add(performanceRootGroup.getStrictType());
        }

        strictType = UserPerformanceHelper.getStrictType(groupStrictTypes);

        // 老k不检查比例：检查温和比例 和 严格比例
        if (!MyTokenUtils.isBoss(logonUser)) {
            if (strictType.equals(PerformanceWorkGroup.GroupStrictType.normalNum)) {
                PerformanceWorkGroupInfoAppVo.PerformancePro performancePro = PerformanceDataHelper.getPerformancePro(validRecords);
                float total = performancePro.getSCount() + performancePro.getACount() + performancePro.getBCount() + performancePro.getCCount();
                if (total == 0.0 || validRecords.size() > 0 && (performancePro.getS() > 20 || (performancePro.getSa()) > 50 || performancePro.getCCount() < Math.floor(total * 0.1))) {
                    logger.info("performance proportion error");
                    throw new ParamException("S比例不能超过20%，S+A比例不能超过50%，C比例不能少于10%");
                }
            } else if (strictType.equals(PerformanceWorkGroup.GroupStrictType.strictNum)) {
                PerformanceWorkGroupInfoAppVo.PerformancePro performancePro = PerformanceDataHelper.getPerformancePro(validRecords);
                if (validRecords.size() > 0 && (performancePro.getS() > 20 || (performancePro.getSa()) > 50 || performancePro.getC() < 10)) {
                    logger.info("performance proportion error");
                    throw new ParamException("S比例不能超过20%，S+A比例不能超过50%，C比例不能少于10%");
                }
            }
        }

        // 批量更新和批量插入记录
        if (!waittingFroInsertRecords.isEmpty()) {
            userPerformanceMapper.batchInsertByLeaderSubmit(waittingFroInsertRecords);
        }
        if (!waittingForUpdateRecords.isEmpty()) {
            // 修改固化成员状态
            for (UserPerformanceDto userPerformance : waittingForUpdateRecords) {
                if (null != userPerformance.getFmConfirmedStatus()) {
                    switch (userPerformance.getFmConfirmedStatus()) {
                        case UserPerformance.FmConfirmedStatus.firstConfirmed:
                            userPerformance.setFmConfirmedStatus(UserPerformance.FmConfirmedStatus.firstComplete);
                            break;
                        case UserPerformance.FmConfirmedStatus.secondConfirmed:
                            userPerformance.setFmConfirmedStatus(UserPerformance.FmConfirmedStatus.secondComplete);
                            break;
                        default:
                            break;
                    }
                }
            }
            userPerformanceMapper.batchUpdateStatusByPks(waittingForUpdateRecords);
        }

        // 固化组记录
        if (!fmGroups.isEmpty()) {
            fmGroupConfirmInfoMapper.batchInsertByPlatConfirmerSubmit(fmGroups);
        }

        insertPerfWorkGroupStatus(insertPerfWorkGroupIds, performanceWorkGroupStatusMap, year, month, logonUser);
        // 操作记录
        operateLogService.add(OperateLog.Type.group_performance_submit, String.format("%s 提交了所负责组的绩效", logonUser.getName()), logonUser.getId());
    }

    @Override
    public boolean insertPerfWorkGroupStatus(List<Long> insertPerfWorkGroupIds, Map<Long, String> performanceWorkGroupStatusMap, Integer year, Integer month, User currentUser) {
        insertPerfWorkGroupIds = PerformanceDataHelper.getNeedInsertPerfWorkGoupIds(insertPerfWorkGroupIds, performanceWorkGroupStatusMap);
        List<PerfWorkGroupStatus> inserts = new ArrayList<>();
        for (Long perfWorkGroupId : insertPerfWorkGroupIds) {
            PerfWorkGroupStatus perfWorkGroupStatus = new PerfWorkGroupStatus();
            perfWorkGroupStatus.setCreateTime(new Date());
            perfWorkGroupStatus.setUpdateTime(new Date());
            perfWorkGroupStatus.setYear(year);
            perfWorkGroupStatus.setMonth(month);
            perfWorkGroupStatus.setStatus(UserPerformance.Status.submitted);
            perfWorkGroupStatus.setOperateId(currentUser.getId());
            perfWorkGroupStatus.setPerfWorkGroupId(perfWorkGroupId);
            inserts.add(perfWorkGroupStatus);
        }
        if (!inserts.isEmpty()) {
            perfWorkGroupStatusMapper.batchInsert(inserts);
            return true;
        }
        return false;
    }

    @Override
    public String downloadUserPerformanceData(Long workGroupId, Integer year, Integer month, String grade) {
        String result;
        String fileName;
        List<Long> allSubUserIds;
        List<Long> allExportPerfWorkGoupIds;
        List<UserPerformanceDto> userPerformanceDatas;
        Map<Long, String> perfWorkGroupIdAndFullNameMap = new LinkedHashMap<>();

        List<PerformanceWorkGroupDto> allActiveWorkGroups = performanceWorkGroupMapper.selectAllWithProjectIdByActive();
        if (workGroupId == null) {
            //绩效负责人
            User user = MyTokenUtils.getCurrentUser();
            fileName = year + "年_" + (month == null ? "" : month + "月_") + user.getName() + "_" + grade + "_的下属绩效数据.xls";
            List<Long> managerWorkGroupIds = new ArrayList<>();
            Map<Long, List<PerformanceWorkGroup>> parentWorkGroupMap = new HashMap<>();
            Map<Long, PerformanceWorkGroup> idPWGMap = new HashMap<>();
            for (PerformanceWorkGroupDto performanceWorkGroup : allActiveWorkGroups) {
                if (performanceWorkGroup.getPerformanceManagerId().equals(user.getId())) {
                    managerWorkGroupIds.add(performanceWorkGroup.getId());
                }
                if (performanceWorkGroup.getParent() != null) {
                    if (parentWorkGroupMap.containsKey(performanceWorkGroup.getParent())) {
                        parentWorkGroupMap.get(performanceWorkGroup.getParent()).add(performanceWorkGroup);
                    } else {
                        List<PerformanceWorkGroup> performanceWorkGroups = new ArrayList<>(1);
                        performanceWorkGroups.add(performanceWorkGroup);
                        parentWorkGroupMap.put(performanceWorkGroup.getParent(), performanceWorkGroups);
                    }
                }
                idPWGMap.put(performanceWorkGroup.getId(), performanceWorkGroup);
            }

            if (managerWorkGroupIds.size() == 0) {
                throw new ParamException("该用户无下属绩效导出权限");
            }

            for (Long groupId : managerWorkGroupIds) {
                perfWorkGroupIdAndFullNameMap.putAll(performanceDataService.getSubWorkGroupIdAndFullNames(groupId, parentWorkGroupMap, idPWGMap));
            }
        } else {
            //数据专员
            //校验该用户是否有导出该绩效组数据权限
            RUserPerformancePerm rUserPerformancePerm = new RUserPerformancePerm();
            rUserPerformancePerm.setUserId(MyTokenUtils.getCurrentUserId());
            rUserPerformancePerm.setPerformanceWorkGroupId(workGroupId);
            rUserPerformancePerm.setPerformanceWorkGroupRoleId(PerformanceWorkGroupRole.Role.performance_data_access_role_id);
            List<RUserPerformancePerm> rUserPerformancePerms = rUserPerformancePermMapper.selectSelectiveByRole(rUserPerformancePerm);
            if (rUserPerformancePerms.isEmpty()) {
                throw new ParamException("您没有导出该绩效组的权限");
            }

            Map<Long, List<PerformanceWorkGroup>> parentWorkGroupMap = new HashMap<>();
            Map<Long, PerformanceWorkGroup> idPWGMap = new HashMap<>();
            for (PerformanceWorkGroup performanceWorkGroup : allActiveWorkGroups) {
                if (performanceWorkGroup.getParent() != null) {
                    if (parentWorkGroupMap.containsKey(performanceWorkGroup.getParent())) {
                        parentWorkGroupMap.get(performanceWorkGroup.getParent()).add(performanceWorkGroup);
                    } else {
                        List<PerformanceWorkGroup> performanceWorkGroups = new ArrayList<>(1);
                        performanceWorkGroups.add(performanceWorkGroup);
                        parentWorkGroupMap.put(performanceWorkGroup.getParent(), performanceWorkGroups);
                    }
                }
                idPWGMap.put(performanceWorkGroup.getId(), performanceWorkGroup);
            }

            PerformanceWorkGroup currentWorkGroup = idPWGMap.get(workGroupId);
            if (null == currentWorkGroup) {
                throw new ParamException("所选的绩效组不存在");
            }

            fileName = year + "年_" + (month == null ? "" : month + "月_") + currentWorkGroup.getName() + "_" + grade + "_的下属绩效数据.xls";

            perfWorkGroupIdAndFullNameMap = performanceDataService.getSubWorkGroupIdAndFullNames(currentWorkGroup.getId(), parentWorkGroupMap, idPWGMap);
        }

        allExportPerfWorkGoupIds = new ArrayList<>(perfWorkGroupIdAndFullNameMap.keySet());
        allSubUserIds = userMapper.selectActiveUserIdByPerfWorkGroup(allExportPerfWorkGoupIds);
        if (month == null) {
            //按年，导出当前绩效树下的所有员工绩效信息
            userPerformanceDatas = userPerformanceMapper.selectDtoByUserIdsAndDate(year, month, allSubUserIds);
        } else {
            //按月，导出绩效组在该月的所有绩效记录
            userPerformanceDatas = userPerformanceMapper.selectDtoByWorkGroupIdsAndDate(year, month, allExportPerfWorkGoupIds);
        }

        if (userPerformanceDatas.isEmpty()) {
            throw new ParamException(year + "年" + (null == month ? "" : month + "月") + "_" + grade + "_的绩效没数据");
        }

        //过滤
        if (!UserPerformance.Performance.All.equals(grade)) {
            userPerformanceDatas = userPerformanceDatas.stream().filter(p -> grade.equals(p.getFinalPerformance())).collect(Collectors.toList());
        }

        //执行导出操作
        if (null == month) {
            //按年
            //已离组人员信息
            List<UserPerformanceDto> leavedUserPerformanceDatas = userPerformanceMapper.selectLeavedByGroupIdsAndUserIds(year, allExportPerfWorkGoupIds, allSubUserIds);

            if (!UserPerformance.Performance.All.equals(grade)) {
                leavedUserPerformanceDatas = leavedUserPerformanceDatas.stream().filter(p -> grade.equals(p.getFinalPerformance())).collect(Collectors.toList());
            }
            result = downloadYearlyToExcel(userPerformanceDatas, perfWorkGroupIdAndFullNameMap, fileName, leavedUserPerformanceDatas);
        } else {
            //按月
            result = downloadToExcel(userPerformanceDatas, fileName, allActiveWorkGroups, allSubUserIds, perfWorkGroupIdAndFullNameMap);
        }

        return result;
    }

    @Transactional
    public void updateWorkGroup(Long workGroupId, User logonUser, Integer year, Integer month) {
        List<PerformanceWorkGroupDto> allGroups = performanceWorkGroupMapper.selectAllWithProjectIdByActive();
        PerformanceWorkGroupDto performanceWorkGroup = allGroups.stream().filter(g -> workGroupId.equals(g.getId())).findFirst().get();
        List<UserPerformanceDto> allUserPerformances = userPerformanceMapper.selectAllByYearAndMonth(year, month);
        List<PerformanceUserDto> allUsers = userMapper.selectAllEntityWithPerformanceByPerformanceWorkGroup();
        List<WorkGroupDto> allPerformanceGroups = performanceWorkGroupMapper.selectAllByActive();
        List<FmMemberDto> allFmMembers = fmMemberMapper.selectAll();
        List<ProjectMaxMemberVo> projects = projectMapper.selectAllWithMaxMember();
        List<PerfWorkGroupStatus> perfWorkGroupStatuses = perfWorkGroupStatusMapper.selectByYearAndMonth(year, month);

        // 把记录放到map里方便查找
        Map<Long, UserPerformanceDto> userPerformanceMap = allUserPerformances.stream().collect(Collectors.toMap(p -> p.getUserId(), p -> p));
        Map<Long, String> performanceWorkGroupStatusMap = PerformanceDataHelper.getPerformanceWorkGroupStatusMap(allGroups, perfWorkGroupStatuses);
        Map<Long, FmMemberDto> fmMemberMap = allFmMembers.stream().collect((Collectors.toMap(m -> m.getUserId(), m -> m)));

        // 重设员工绩效组
        PerformanceDataHelper.resetUserPerformanceWorkGroup(allUsers, userPerformanceMap);

        // 找出根目录，初始化树
        WorkGroupDto performanceRootGroup = PerformanceTreeHelper.getWorkGroupTree(workGroupId, allPerformanceGroups, allUsers, true);

        // 获取所有下属
        List<PerformanceUserDto> allMembers = PerformanceTreeHelper.getAllMembersByRootWorkGroup(performanceRootGroup);

        // 待数据库处理记录
        List<UserPerformanceDto> waittingForUpdateRecords = new ArrayList<>();
        List<UserPerformanceDto> waittingFroInsertRecords = new ArrayList<>();
        Set<Long> projectIds = new HashSet<>();

        //拿到当前绩效组的所有下属组Id(遍历)
        List<Long> insertPerfWorkGroupIds = new ArrayList<>();
        insertPerfWorkGroupIds.add(performanceRootGroup.getId());
        PerformanceTreeHelper.getAllSubPerfWorkGroupIdByRootTree(insertPerfWorkGroupIds, performanceRootGroup);

        // 上级负责人代提交逻辑
        for (PerformanceUserDto user : allMembers) {
            if (userPerformanceMap.containsKey(user.getUserId())) {
                UserPerformanceDto userPerformance = userPerformanceMap.get(user.getUserId());
                if (UserPerformance.Status.unsubmitted.equals(userPerformance.getStatus()) || UserPerformance.Status.submitted.equals(userPerformance.getStatus())) {
                    // 未提交，已提交的记录变为作废，放入待更新列表
                    userPerformance.setFinalPerformance(UserPerformance.Performance.invalided);
                    userPerformance.setStatus(UserPerformance.Status.locked);
                    PerformanceDataHelper.setUserPerformanceFmGroupInfo(userPerformance, fmMemberMap, null, projectIds);
                    waittingForUpdateRecords.add(userPerformance);
                } else if (UserPerformance.Status.assessed.equals(userPerformance.getStatus())) {
                    // 已评定的记录变为待确认，放入待更新列表
                    userPerformance.setStatus(UserPerformance.Status.locked);
                    PerformanceDataHelper.setUserPerformanceFmGroupInfo(userPerformance, fmMemberMap, null, projectIds);
                    waittingForUpdateRecords.add(userPerformance);
                }
            } else {
                // 空记录变为作废，放入待新建列表，如果当前员工所在绩效组已提交则不做处理
                String status = PerformanceDataHelper.getPerformanceWorkGroupStatus(user.getWorkGroupId(), performanceWorkGroupStatusMap);
                if (status.equals(SubPerformanceAppVo.HistoryInfo.Status.processing)) {
                    UserPerformanceDto userPerformanceDto = PerformanceDataHelper.getNewInvalidedUserPerformance(user, year, month, UserPerformance.Status.locked);
                    PerformanceDataHelper.setUserPerformanceFmGroupInfo(userPerformanceDto, fmMemberMap, null, projectIds);
                    waittingFroInsertRecords.add(userPerformanceDto);
                }
            }
        }

        // 批量更新和批量插入记录
        if (!waittingFroInsertRecords.isEmpty()) {
            userPerformanceMapper.batchInsertByLeaderSubmit(waittingFroInsertRecords);
        }
        if (!waittingForUpdateRecords.isEmpty()) {
            // 修改固化成员状态
            for (UserPerformanceDto userPerformance : waittingForUpdateRecords) {
                if (null != userPerformance.getFmConfirmedStatus()) {
                    if (userPerformance.getFmConfirmedStatus().contains("首次")) {
                        userPerformance.setFmConfirmedStatus(UserPerformance.FmConfirmedStatus.firstComplete);
                    } else if (userPerformance.getFmConfirmedStatus().contains("二次")) {
                        userPerformance.setFmConfirmedStatus(UserPerformance.FmConfirmedStatus.secondComplete);
                    }
                }
            }
            userPerformanceMapper.batchUpdateStatusByPks(waittingForUpdateRecords);
        }

        // 固化组处理
        if (performanceWorkGroup.getProjectConfirmFlag()) {
            List<FmGroupConfirmInfoVo> fmGroupConfirmInfoList = fmGroupConfirmInfoMapper.selectAllByPerformanceWorkGroupIdAndYearMonth(performanceWorkGroup.getId(), year, month);
            if (fmGroupConfirmInfoList.isEmpty()) {
                List<FmGroupConfirmInfo> fmGroups = PerformanceDataHelper.getFmGroups(year, month, performanceWorkGroup, performanceRootGroup, projectIds, allFmMembers, projects, true);
                if (!fmGroups.isEmpty()) {
                    fmGroupConfirmInfoMapper.batchInsertByPlatConfirmerSubmit(fmGroups);
                }
            }
        }

        insertPerfWorkGroupStatus(insertPerfWorkGroupIds, performanceWorkGroupStatusMap, year, month, logonUser);
        // 操作记录
        operateLogService.add(OperateLog.Type.group_performance_submit,
                String.format("%s 代提交了 %s 的绩效", logonUser.getName(), performanceRootGroup.getName()),
                logonUser.getId());
    }

    @Override
    public UserPhotoWallVo selectUserPerformance(Integer year, Integer month, String floorId) {
        UserPhotoWallVo userPhotoWallVo = new UserPhotoWallVo();
        Map<String, List<UserPhotoWallVo>> projects = new HashMap<String, List<UserPhotoWallVo>>();
        List<ProjectMinVo> listProject = new ArrayList<ProjectMinVo>();
        List<UserPhotoWallVo> listPhoto = new ArrayList<UserPhotoWallVo>();
        if (year == null && month == null) {
            UserPhotoWallVo vo = userPerformanceMapper.selectUserPerformanceDate();//获取最新绩效时间
            if (vo == null) {
                return userPhotoWallVo;
            }
            year = vo.getYear();
            month = vo.getMonth();
        }
        List<UserPhotoWallVo> list = userPerformanceMapper.selectUserPerformance(year, month, floorId);
        if (list != null) {
            ProjectMinVo projectMinVo = null;
            for (UserPhotoWallVo vo : list) {
                setZipPatch(vo);
                if (projects.containsKey(vo.getProjectName())) {
                    projects.get(vo.getProjectName()).add(vo);
                } else {
                    listPhoto = new ArrayList<UserPhotoWallVo>();
                    listPhoto.add(vo);
                    projects.put(vo.getProjectName(), listPhoto);
                }
            }
            Iterator it = projects.keySet().iterator();
            String key = null;
            while (it.hasNext()) {
                key = (String) it.next();
                projectMinVo = new ProjectMinVo();
                projectMinVo.setProjectName(key);
                projectMinVo.setItems(projects.get(key));
                listProject.add(projectMinVo);
            }
        }
        sort(listProject);
        userPhotoWallVo.setYear(year);
        userPhotoWallVo.setMonth(month);
        userPhotoWallVo.setItems(listProject);
        return userPhotoWallVo;
    }

    /**
     * 获取压缩包路径
     *
     * @param vo
     * @return
     */
    private void setZipPatch(UserPhotoWallVo vo) {
        String monthStr = "";
        String floorNo = "";
        if (vo.getMonth() != null && vo.getMonth().compareTo(10) < 0) {
            monthStr = "0" + vo.getMonth();
        } else {
            monthStr = String.valueOf(vo.getMonth());
        }
        if ("1".equalsIgnoreCase(vo.getFloorNo()) || "2".equalsIgnoreCase(vo.getFloorNo()) || "3".equalsIgnoreCase(vo.getFloorNo())) {
            vo.setTargetPath("/" + vo.getYear() + monthStr + "/" + vo.getFloorNo() + "/" + vo.getProjectName() + "/");
        } else {
            vo.setTargetPath("/" + vo.getYear() + monthStr + "/其他/" + vo.getProjectName() + "/");
        }
    }

    /**
     * 排序
     *
     * @param listProject
     */
    private void sort(List<ProjectMinVo> listProject) {
        for (ProjectMinVo vo : listProject) {
            if (vo.getItems() != null) {
                vo.setSize(new Integer(vo.getItems().size()));
            } else {
                vo.setSize(new Integer(0));
            }
        }
        Collections.sort(listProject, new Comparator<ProjectMinVo>() {
            @Override
            public int compare(ProjectMinVo o1, ProjectMinVo o2) {
                return o2.getSize().compareTo(o1.getSize());
            }
        });
    }

    @Override
    public boolean uploadUserPhoto(MultipartFile file, String loginId) {
        boolean flag = false;
        if (file.isEmpty()) {
            throw new ParamException("文件为空");
        }
        try {
            String fileName = file.getOriginalFilename();
            String prefix = fileName.substring(fileName.lastIndexOf("."));
            String srcPatch = filePathPrefix + userKoaIcon + "/" + loginId + prefix;
            file.transferTo(new File(srcPatch));
//            MyImageUtils.resizeImg(srcPatch, srcPatch, 256, 256, 1.0f);
            String destPatch = filePathPrefix + userKoaIconCirde + "/" + loginId + prefix;
            File destFile = new File(destPatch);
            Thumbnails.of(srcPatch).crop(Positions.CENTER).size(512, 512).keepAspectRatio(true).toFile(destFile);
            MyImageUtils.makeRoundedCornerImg(destFile.getPath(), destPatch, 512);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ParamException("照片 保存失败");
        }
        return flag;
    }

    @Override
    public String downloadUserPhoto(Integer year, Integer month, String floorName) {
        UserPhotoWallVo userPhotoWallVo = selectUserPerformance(year, month, floorName);
        String splitFlag = "/";
        String filePath = "";
        if (userPhotoWallVo != null) {
            String cirdeFolder = filePathPrefix + userKoaIconCirde;//圆图路径
            String zipFolder = filePathPrefix + sUserPhoto;//压缩路径
            String monthStr = "";
            if (userPhotoWallVo.getMonth() != null && userPhotoWallVo.getMonth().compareTo(10) < 0) {
                monthStr = "0" + userPhotoWallVo.getMonth();
            } else {
                monthStr = String.valueOf(userPhotoWallVo.getMonth());
            }
            String date = userPhotoWallVo.getYear() + monthStr;
            String dateFolder = splitFlag + date;
            MyFileUtils.makeDirs(true, zipFolder + dateFolder);//删除在建立年月目录
            String targetPath = null;
            List<ProjectMinVo> projects = userPhotoWallVo.getItems();
            List<UserPhotoWallVo> userPhoto = null;
            String srcFile = null;
            String destFile = null;
            for (ProjectMinVo pvo : projects) {
                userPhoto = pvo.getItems();
                for (UserPhotoWallVo photo : userPhoto) {
                    targetPath = photo.getTargetPath();
                    MyFileUtils.makeDirs(false, zipFolder + targetPath);//创建 /年月/楼栋/项目/ 压塑目录
                    srcFile = cirdeFolder + splitFlag + photo.getPhoto();//获取圆形图
                    destFile = zipFolder + splitFlag + targetPath + photo.getPhoto();
                    MyFileUtils.copyFileUsingFileChannels(srcFile, destFile);//拷贝文件
                }
            }
            String fileName = MyZipUtils.toZip(zipFolder + dateFolder, zipFolder, date);
            filePath = sUserPhoto + fileName;
        }
        return filePath;
    }

    @Override
    public void notifyUserPerformance() {
        logger.info("begin notifyUserPerformance...");
        Integer year;
        Integer month;
        List<UserMessage> insertMessages = new ArrayList<>();

        YearMonthDto lastCompleteYearMonth = perfWorkGroupStatusMapper.selectLastCompleteYearMonth();
        if (lastCompleteYearMonth != null) {
            YearMonthDto yearMonthDto = MyDateUtils.yearMonthCalculation(lastCompleteYearMonth.getYear(), lastCompleteYearMonth.getMonth(), 1);
            year = yearMonthDto.getYear();
            month = yearMonthDto.getMonth();
        } else {
            // 极端情况，数据库里没有已完成的绩效记录
            YearMonthDto yearMonthDto = userPerformanceMapper.selectLastWriteYearMonth();
            if (yearMonthDto != null) {
                year = yearMonthDto.getYear();
                month = yearMonthDto.getMonth();
            } else {
                // 极端情况，数据库里没有绩效记录
                Calendar nowDate = Calendar.getInstance();
                // < 25号，通知上个月
                if (nowDate.get(Calendar.DAY_OF_MONTH) < 25) {
                    // 防止1月份，-1
                    nowDate.add(Calendar.MONTH, -1);
                    year = nowDate.get(Calendar.YEAR);
                    month = nowDate.get(Calendar.MONTH) + 1;
                }
                // >= 25号，通知这个月
                else {
                    year = nowDate.get(Calendar.YEAR);
                    month = nowDate.get(Calendar.MONTH) + 1;
                }
            }
        }

        // 三种通知情况的消息体
        String notifyGroupSubmitStr = String.format(UserMessage.Content.perf_work_group_submit_notify, year, month);
        String notifySubmitUserPerfStr = String.format(UserMessage.Content.user_perf_submit_notify, year, month);
        String notifyWriteUserPerfStr = String.format(UserMessage.Content.user_perf_write_notify, year, month);

        // 容错设施，判断是否发送过通知
        Integer count = userMessageMapper.selectPerfNotifyCountByYearAndMonth(notifyGroupSubmitStr, notifySubmitUserPerfStr, notifyWriteUserPerfStr);
        if (count != null && count > 0) {
            logger.info("这台服务器进入发起绩效通知service,但检查发现数据库已有数据，return。。。");
            // logger.info("数据库里有通知绩效的记录，但是却发起了这次检查，请马上检查redis情况和定时器情况。。。");
            return;
        }

        List<WorkGroupDto> allGroups = performanceWorkGroupMapper.selectAllByActive();
        List<PerfWorkGroupStatus> perfWorkGroupStatuses = perfWorkGroupStatusMapper.selectByYearAndMonth(year, month);
        List<UserPerformanceDto> userPerformanceDtos = userPerformanceMapper.selectAllByYearAndMonth(year, month);
        Map<Long, String> groupStatusByIdMap = perfWorkGroupStatuses.stream().collect(Collectors.toMap(x -> x.getPerfWorkGroupId(), x -> x.getStatus()));
        Map<Long, UserPerformanceDto> perfDataByUserIdMap = userPerformanceDtos.stream().collect(Collectors.toMap(x -> x.getUserId(), x -> x));

        // 用set，有人管多个组
        Set<Long> notifySubmitGroupUserIds = new HashSet<>(allGroups.size());
        for (WorkGroupDto allGroup : allGroups) {
            if (allGroup.getLeaderId() == null) {
                continue;
            }
            if (groupStatusByIdMap.get(allGroup.getId()) == null) {
                notifySubmitGroupUserIds.add(allGroup.getLeaderId());
            }
        }

        //所有绩效人员
        List<Long> allPerfUserIds = userMapper.selectAllPerfUserIds();
        List<Long> notifyWriteUserIds = new ArrayList<>(allPerfUserIds.size());
        List<Long> notifySubmitUserIds = new ArrayList<>(allPerfUserIds.size());

        for (Long userId : allPerfUserIds) {
            if (!perfDataByUserIdMap.containsKey(userId)) {
                notifyWriteUserIds.add(userId);
            } else {
                UserPerformanceDto userPerformanceDto = perfDataByUserIdMap.get(userId);
                if (UserPerformance.Status.unsubmitted.equals(userPerformanceDto.getStatus())) {
                    notifySubmitUserIds.add(userId);
                }
            }
        }
        // 绩效组负责人未提交
        insertMessages.addAll(createUserMeaagesOfPerfNotify(new ArrayList<>(notifySubmitGroupUserIds), notifyGroupSubmitStr, UserMessage.Location.performanceMyPerformance));
        // 个人绩效未提交
        insertMessages.addAll(createUserMeaagesOfPerfNotify(notifySubmitUserIds, notifySubmitUserPerfStr, UserMessage.Location.performanceMyPerformance));
        // 个人绩效未填写
        insertMessages.addAll(createUserMeaagesOfPerfNotify(notifyWriteUserIds, notifyWriteUserPerfStr, UserMessage.Location.performanceMyPerformance));

        if (!insertMessages.isEmpty()) {
            userMessageService.batchAdd(insertMessages);
        }
    }

    private List<UserMessage> createUserMeaagesOfPerfNotify(List<Long> receivers, String content, String location) {
        List<UserMessage> result = new ArrayList<>();
        for (Long notifyUserId : receivers) {
            UserMessage userMessage = new UserMessage();
            userMessage.setSender(MyTokenUtils.ADMIN_ID);
            userMessage.setReceiver(notifyUserId);
            userMessage.setType(UserMessage.Type.system);
            userMessage.setContent(content);
            userMessage.setLocation(location);
            userMessage.setReadFlag(false);
            userMessage.setCreateTime(new Date());
            result.add(userMessage);
        }
        return result;
    }

    private boolean hasPhoto(String loginId){
        File stdIcon = new File(filePathPrefix + userKoaIcon + "/" + loginId + ".png");
        File circleIcon = new File(filePathPrefix + userKoaIconCirde + "/" + loginId + ".png");
        return stdIcon.exists() && circleIcon.exists() ;
    }
    private boolean updatePhoto(String loginId) throws IOException {
        KsLifeCommonVo result = ksUserService.getAvatar(loginId);
        String emptyWarnMsg = "Cannot find photo from Kingsoft Life for " + loginId;
        if (result == null || result.getData() == null) {
            logger.warn(emptyWarnMsg);
            return false;
        } else {
            String value = result.getData().toString();
            if (StringUtils.isEmpty(value) || !(StringUtils.endsWithIgnoreCase(value,".jpg") || StringUtils.endsWithIgnoreCase(value,".jpeg") || StringUtils.endsWithIgnoreCase(value,".png"))) {
                logger.warn(emptyWarnMsg);
                return false;
            }
            String fileName = loginId + ".png";
            InputStream inputStream = new URL(value).openStream();
            MultipartFile mulFile = new MockMultipartFile(fileName,fileName, ContentType.APPLICATION_OCTET_STREAM.toString(), inputStream);
            uploadUserPhoto(mulFile, loginId);
            inputStream.close();
        }
        return true;
    }

    @Override
    public void syncPhoto() {
        List<User> userList = userMapper.findUsersNeedSyncPhoto();
        if (userList.size() == 0 ){
            logger.info("Every user has latest photo status.");
        } else {
            logger.info("The size of users without latest photo status is " + userList.size());
            List<User> needUpdateUserList = userList.stream().filter(u -> {
                boolean isExist = hasPhoto(u.getLoginId());
                if (isExist) {
                    userMapper.updatePhotoStatus(u.getId());
                }
                return !isExist;
            }).collect(Collectors.toList());
            if (needUpdateUserList.size() == 0) {
                logger.info("No user need update photo.");
            } else {
                logger.info("The size of users need sync photo is " + needUpdateUserList.size());
                needUpdateUserList.stream().forEach(u -> {
                    try {
                        if (updatePhoto(u.getLoginId())) {
                            userMapper.updatePhotoStatus(u.getId());
                        }
                    } catch (IOException e) {
                        logger.error("Sync photo fail for " + u.getLoginId(), e);
                    }
                });
            }
        }
        logger.info("Sync photo finished.");
    }

    @Override
    public List<SubGroupPerformanceVo> getSubGroupPerformanceByDate(Long userId, Integer year, Integer month) {
        List<SubGroupPerformanceVo> result = new ArrayList<>();

        //准备数据
        List<PerfWorkGroupStatus> perfWorkGroupStatuses = perfWorkGroupStatusMapper.selectByYearAndMonth(year, month);
        List<PerformanceWorkGroupDto> allPerfWorkGroups = performanceWorkGroupMapper.selectAllWithProjectIdByActive();
        List<FmGroupConfirmInfoVo> allFmGroupConfirmInfoList = fmGroupConfirmInfoMapper.selectAllConfirmInfoByYearAndMonth(year, month);

        Map<Long, List<FmGroupConfirmInfoVo>> fmGroupConfirmInfoByPerfWorkGroupIdMap = allFmGroupConfirmInfoList.stream().collect(Collectors.groupingBy(x -> x.getPerformanceWorkGroupId()));
        Map<Long, String> performanceWorkGroupStatusMap = PerformanceDataHelper.getPerformanceWorkGroupStatusMap(allPerfWorkGroups, perfWorkGroupStatuses);

        Set<Long> managerGroupIds;
        if (userId == null) {
            Long currentUserId = MyTokenUtils.getCurrentUserId();
            managerGroupIds = allPerfWorkGroups.stream().filter(g -> currentUserId.equals(g.getPerformanceManagerId())).map(x -> x.getId()).collect(Collectors.toSet());
            // 判断是否是拉登，他没管绩效组但是要看珠海美术中心的绩效
            if (User.Id.LADENG.equals(currentUserId)) {
                managerGroupIds.add(PerformanceWorkGroup.Id.ZHUHAI_ART_CENTER);
            }
        } else {
            managerGroupIds = allPerfWorkGroups.stream().filter(g -> userId.equals(g.getPerformanceManagerId())).map(x -> x.getId()).collect(Collectors.toSet());
        }

        List<WorkGroupDto> workGroupTrees = new LinkedList<>();
        for (Long managerGroupId : managerGroupIds) {
            WorkGroupDto workGroupTree = PerformanceTreeHelper.getNoMemberWorkGroupTree(managerGroupId, allPerfWorkGroups, true);
            workGroupTrees.add(workGroupTree);
        }

        List<Long> subGroupIds = new ArrayList<>();
        for (WorkGroupDto workGroupTree : workGroupTrees) {
            PerformanceTreeHelper.getAllSubPerfWorkGroupIdByRootTree(subGroupIds, workGroupTree);
        }
        subGroupIds = MyListUtils.removeDuplicate(subGroupIds);
        for (WorkGroupDto managerGroup : workGroupTrees) {
            if (subGroupIds.contains(managerGroup.getId())) {
                continue;
            }
            result.add(getSubGroupPerf(managerGroup, performanceWorkGroupStatusMap, fmGroupConfirmInfoByPerfWorkGroupIdMap));
        }

        return result;
    }

    private SubGroupPerformanceVo getSubGroupPerf(WorkGroupDto workGroupDto, Map<Long, String> performanceWorkGroupStatusMap, Map<Long, List<FmGroupConfirmInfoVo>> groupFmGroupConfirmInfoMap) {
        SubGroupPerformanceVo result = new SubGroupPerformanceVo();
        result.setConfirmList(new ArrayList<>());
        result.setSubGroups(new ArrayList<>());

        SubGroupPerformanceVo.Manager manager = new SubGroupPerformanceVo.Manager();
        if (workGroupDto.getLeader() != null) {
            manager.setUserId(workGroupDto.getLeader().getUserId());
            manager.setLoginId(workGroupDto.getLeader().getLoginId());
            manager.setManagerName(workGroupDto.getLeader().getName());
        }

        String status = performanceWorkGroupStatusMap.get(workGroupDto.getId());

        if (groupFmGroupConfirmInfoMap.containsKey(workGroupDto.getId())) {
            for (FmGroupConfirmInfoVo fmGroupConfirmInfoVo : groupFmGroupConfirmInfoMap.get(workGroupDto.getId())) {
                SubGroupPerformanceVo.Confirm confirm = new SubGroupPerformanceVo.Confirm();
                confirm.setConfirmFlag(FmGroupConfirmInfo.Status.confirmed.equals(fmGroupConfirmInfoVo.getStatus()));
                confirm.setProjectName(fmGroupConfirmInfoVo.getProjectName());
                confirm.setProjectConfirmerName(fmGroupConfirmInfoVo.getProjectConfirmerName());
                result.getConfirmList().add(confirm);
            }
        }

        List<WorkGroupDto> childWorkGroups = workGroupDto.getChildWorkGroups();
        if (childWorkGroups != null && childWorkGroups.size() > 0) {
            for (WorkGroupDto childWorkGroup : childWorkGroups) {
                result.getSubGroups().add(getSubGroupPerf(childWorkGroup, performanceWorkGroupStatusMap, groupFmGroupConfirmInfoMap));
            }
        }

        result.setGroupId(workGroupDto.getId());
        result.setName(workGroupDto.getName());
        result.setStatus(SubPerformanceAppVo.HistoryInfo.Status.processing.equals(status) ? UserPerformance.Status.unsubmitted : status);
        result.setManager(manager);

        return result;
    }

    /**
     * 更新绩效自动确认
     */
    public void updateUserPerformanceStatus() {
        CfgSystemParam cfg = cfgSystemParamMapper.selectByName("performance-auto-confirm-flag");//获取参数判断是否绩效完成了
        String comment = cfg.getComment();
        String day = cfg.getValue();
        logger.warn("updateUserPerformanceStatus 执行员工自动确认任务");
        if (!comment.isEmpty()) {
            String[] str = comment.split("\\|");
            String flag = str[0];
            String date = str[1];
            if (MyDateUtils.daysComparison(date, MyDateUtils.dateToString(new Date(), MyDateUtils.DATE_FORMAT_YYYY_MM_DD)) > Integer.parseInt(day)) {
                if ("true".equalsIgnoreCase(flag)) {
                    String year = date.substring(0, date.indexOf("-"));
                    String month = date.substring(date.indexOf("-")+1, date.lastIndexOf("-"));
                    List<UserPerformanceVo> list = userPerformanceMapper.selectUserPerByYearAndMonth(Integer.parseInt(year), Integer.parseInt(month));//查询状态没确认并且没有申诉记录的绩效员工信息
                    if(list != null && !list.isEmpty()) {
                        userPerformanceMapper.updateUserPerByYearAndMonth(Integer.parseInt(year), Integer.parseInt(month), list);//更新员工绩效状态为已确认
                    }
                    cfg.setComment("false|" + str[1]);
                    cfgSystemParamMapper.updateByName(cfg);//更新系统参数无需再重复更新

                }
            }
        }
        logger.warn(" updateUserPerformanceStatus 完成员工自动确认任务");
    }

}
