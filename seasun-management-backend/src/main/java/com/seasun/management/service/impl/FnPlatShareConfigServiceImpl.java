package com.seasun.management.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.seasun.management.constant.ErrorCode;
import com.seasun.management.constant.ErrorMessage;
import com.seasun.management.constant.UserShareType;
import com.seasun.management.dto.*;
import com.seasun.management.exception.DataImportException;
import com.seasun.management.exception.ParamException;
import com.seasun.management.exception.UserInvalidOperateException;
import com.seasun.management.helper.ExcelHelper;
import com.seasun.management.helper.FnShareHelper;
import com.seasun.management.helper.ReportHelper;
import com.seasun.management.mapper.*;
import com.seasun.management.model.*;
import com.seasun.management.service.FnPlatShareConfigService;
import com.seasun.management.util.*;
import com.seasun.management.vo.*;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service(value = "fnPlatShareConfigService")
public class FnPlatShareConfigServiceImpl implements FnPlatShareConfigService {

    private static final Logger logger = LoggerFactory.getLogger(FnPlatShareConfigServiceImpl.class);

    @Value("${export.excel.path}")
    private String exportExcelPath;

    @Value("${file.sys.prefix}")
    private String filePathPrefix;

    @Value("${backup.excel.url}")
    private String backupExcelUrl;

    @Autowired
    FnPlatShareConfigMapper fnPlatShareConfigMapper;

    @Autowired
    FnSumShareConfigMapper fnSumShareConfigMapper;

    @Autowired
    ProjectMapper projectMapper;

    @Autowired
    FnShareTemplateMapper fnShareTemplateMapper;

    @Autowired
    RUserProjectPermMapper rUserProjectPermMapper;

    @Autowired
    FnShareInfoMapper fnShareInfoMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    private FnPlatShareMonthInfoMapper fnPlatShareMonthInfoMapper;

    @Autowired
    private FnWeekShareCommitLogMapper fnWeekShareCommitLogMapper;

    @Autowired
    private FnPlatFavorProjectMapper fnPlatFavorProjectMapper;
    @Autowired
    private FnSumWeekShareConfigMapper fnSumWeekShareConfigMapper;
    @Autowired
    private FnPlatWeekShareConfigMapper fnPlatWeekShareConfigMapper;
    @Autowired
    private FnWeekShareWorkdayStatusMapper fnWeekShareWorkdayStatusMapper;
    @Override
    public boolean getPlatLockFlag(int year, int month, long platId) {
        boolean result = false;
        // 判断平台汇总记录有没有锁定记录
        Boolean platLockFlag = fnSumShareConfigMapper.getFlagLockStatus(platId, year, month);
        if (platLockFlag != null && platLockFlag) {
            result = true;
        }

        // 判断月分摊有没有开启新一轮分摊
        if (!result) {
            if (FnShareInfo.Status.finished.equals(fnShareInfoMapper.selectStatusByYearAndMonth(year, month))) {
                result = true;
            }
        }
        return result;
    }

    @Override
    public FnPlatShareConfigLockVo getUserShareConfigData(String userType, Long platId, int year, int month) {
        Long userId = MyTokenUtils.getCurrentUserId();
        FnPlatShareConfigLockVo fnPlatShareConfigLockVo = new FnPlatShareConfigLockVo();
        List<FnPlatShareConfigVo> fnPlatShareConfigs;

        boolean lockFlag = getPlatLockFlag(year, month, platId);

        // 查询当月已经确认的周数据有哪些，返回前台，以便做按钮级提示。
        List<FnPlatShareConfigLockVo.WeekConfirmInfo> weekConfirmInfos = fnWeekShareCommitLogMapper.selectPlatWeekConfirmData(year, month, platId);
        fnPlatShareConfigLockVo.setWeekConfirmInfo(weekConfirmInfos);

        fnPlatShareConfigLockVo.setLockFlag(lockFlag);

        // 若userType为成员：展示现有数据
        if (UserShareType.member.toString().equals(userType)) {
            fnPlatShareConfigs = fnPlatShareConfigMapper.selectMemberDataByCond(platId, year, month, userId);
        }
        // 若userType为平台负责人：展现汇总数据
        else if (UserShareType.manager.toString().equals(userType)) {
            // 此处会判断是否存在调整后的汇总分摊比例,若有,则会填写sumSharePro字段
            int totalWeight;
            if (lockFlag) {
                totalWeight = fnPlatShareConfigMapper.countAllUserByProjectIdWithoutTrainee(platId, year, month);
                // 若已锁定，则去掉为0的fnSumSharePro
                fnPlatShareConfigs = fnPlatShareConfigMapper.selectByPlatIdAndYearAndMonthWithProjectAndSumShareInfoWithoutZeroFnSumSharePro(platId, year, month);
            } else {
                totalWeight = userMapper.countAllUserByProjectIdWithoutTrainee(platId);
                fnPlatShareConfigs = fnPlatShareConfigMapper.selectByPlatIdAndYearAndMonthWithProjectAndSumShareInfo(platId, year, month);
            }

            fnPlatShareConfigs.forEach(f -> {
                if (f.getSharePro() != null) {
                    f.setSharePro(f.getSharePro().divide(new BigDecimal(totalWeight), 6, BigDecimal.ROUND_HALF_UP));
                }
            });

        } else {
            throw new ParamException("userType不合法， 可选值为[manager,member]");
        }

        List<FnSumShareConfig> sumShareConfigs = fnSumShareConfigMapper.selectSumShareConfigByPlatIdAndYearAndMonth(platId, year, month);
        addEmptyDataForMissedProjects(platId, fnPlatShareConfigs, sumShareConfigs);
        fnPlatShareConfigLockVo.setPlatProcessList(fnPlatShareConfigs);

        // 可能没有fn_sum_share_config的记录
        return fnPlatShareConfigLockVo;
    }

    @Override
    public List<FnPlatSumProVo> getPlatSumConfigList(Integer year, Integer month) {
        return null;
    }

    @Override
    public void batchUpdateLockFlag(Long[] plats, Integer year, Integer month, String type) {

    }

    @Override
    public List<FnPlatShareConfigVo> getMemberShareConfigData(String userType, Long platId, int year, int month, Long memberId) {
        return null;
    }

    @Override
    public List<FnPlatShareConfigVo> getDetailData(Long platId, Long projectId, int year, int month) {
        List<FnPlatShareConfigVo> fnPlatShareConfigs = fnPlatShareConfigMapper.selectByPlatIdAndProjectIdAndYearAndMonthWithUserInfo(platId, projectId, year, month);
        int totalWeight = userMapper.countAllUserByProjectIdWithoutTrainee(platId);
        fnPlatShareConfigs.forEach(p -> {
            p.setTotalWeight(new BigDecimal(totalWeight));
            p.setWeight(BigDecimal.ONE);
        });
        return fnPlatShareConfigs;
    }

    @Override
    public boolean insert(FnPlatShareConfigVo fnPlatShareConfigVo) {
        return true;
    }

    @Override
    public JSONObject update(FnPlatShareConfigVo fnPlatShareConfigVo) {
        return null;
    }


    @Override
    public JSONObject importShareTemplate(MultipartFile file, int year, int month) {
        return null;
    }

    @Override
    @Transactional
    public String importShareDetailExcel(MultipartFile file, long platId, int year, int month) {
        Project currentProject = projectMapper.selectByPrimaryKey(platId);
        if (currentProject == null || !currentProject.getActiveFlag()) {
            throw new UserInvalidOperateException(ErrorMessage.PLAT_INVALID_ERROR);
        }

        long shareProjectMapKey = platId;
        if (Project.Id.TECGNOLOGY_CENTER.equals(currentProject.getParentHrId())) {
            shareProjectMapKey = currentProject.getParentHrId();
        }

        Workbook wb = null;
        try {
            ExcelHelper.saveExcelBackup(file, filePathPrefix + backupExcelUrl);
            wb = WorkbookFactory.create(file.getInputStream());
        } catch (Exception e) {
            throw new DataImportException(ErrorCode.FILE_IMPORT_FILE_READ_ERROR, "文件读取失败");
        }

        List<String> errorLogs = new ArrayList<>();

        try {
            Sheet sheet = wb.getSheetAt(0);
            // 建立列和项目ID的映射关系
            List<ProjectVo> projectList = projectMapper.selectAllShareProject();
            Map<Integer, Long> mapColumnProject = new HashMap<>();
            int colIndexEnd = 7;
            Row rowProject = sheet.getRow(0);
            while (true) {
                Cell cell = rowProject.getCell(colIndexEnd);
                String cellText = cell.getStringCellValue();
                // 以这一列作为结束
                if ("工作日".equals(cellText)) {
                    break;
                }

                // 为所有项目列匹配id
                if ("平台-公司".equals(cellText) && ReportHelper.PlatInShareProjectMap.get(shareProjectMapKey) != null) {
                    mapColumnProject.put(colIndexEnd, ReportHelper.PlatInShareProjectMap.get(shareProjectMapKey));
                } else {
                    ProjectVo project = projectList.stream().filter(item -> {
                        List<String> projectUsedNames = ExcelHelper.buildProjectUsedNames(item);
                        return projectUsedNames.contains(ExcelHelper.trimSpaceAndSpecialSymbol(cellText));
                    }).findFirst().orElse(null);

                    if (null == project) {
                        errorLogs.add("- 第 " + cell.getColumnIndex() + " 列, 项目名不规范，找不到此项目 ProjectName:" + cellText);
                    } else {
                        mapColumnProject.put(colIndexEnd, project.getId());
                    }
                }

                colIndexEnd++;
            }
            //开始读取每个人的分摊数据
            List<UserFnShareDataDto> userFnShareDataDtoList = new ArrayList<>();
            List<UserEmployeeNoDto> userList = userMapper.selectUserWithEmployeeInfo();
            int rowIndexEnd = 1;
            while (true) {
                Row rowUserShareData = sheet.getRow(rowIndexEnd++);
                logger.info("current row is:{}", rowUserShareData.getRowNum());
//                Cell cellEndOfUserShare = rowUserShareData.getCell(5);
//                String cellTextEnd = cellEndOfUserShare.toString();
//                // 以这一行做结束行
//                if ("各项目工作日".equals(cellTextEnd)) {
//                    break;
//                }

                // 工号
                Cell cellEmployeeNo = rowUserShareData.getCell(0);
                if (cellEmployeeNo == null || cellEmployeeNo.toString().isEmpty()) {
                    // 以工号为空做为结束行
                    break;
                }
                Long employNo = new BigDecimal(MyCellUtils.getCellValue(cellEmployeeNo)).longValue();

                // todo: 分摊比例成员变化后，需要校验这些员工在人力上，是否属于该平台
                UserEmployeeNoDto userInfo = userList.stream().filter(item -> item.getEmployeeNo().equals(employNo)).findFirst().orElse(null);
                if (null == userInfo) {
                    errorLogs.add("- 第 " + rowUserShareData.getRowNum() + "员工编号不存在 EmployeeNo:" + cellEmployeeNo.toString());
                    continue;
                }

                for (int columnIndex = 7; columnIndex <= colIndexEnd; columnIndex++) {
                    if (!mapColumnProject.containsKey(columnIndex)) {
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
                    BigDecimal shareDay = new BigDecimal(cellText);
                    Long projectId = mapColumnProject.get(columnIndex);
                    UserFnShareDataDto userFnShareDataDto = userFnShareDataDtoList.stream().filter(item -> item.getUserId().equals(userInfo.getUserId())).findFirst().orElse(null);
                    if (null != userFnShareDataDto) {
                        if (userFnShareDataDto.getShareData().containsKey(projectId)) {
                            userFnShareDataDto.getShareData().put(projectId, userFnShareDataDto.getShareData().get(projectId).add(shareDay));
                        } else {
                            userFnShareDataDto.getShareData().put(projectId, shareDay);
                        }
                        userFnShareDataDto.setShareTotal(userFnShareDataDto.getShareTotal().add(shareDay));
                    } else {
                        userFnShareDataDto = new UserFnShareDataDto();
                        userFnShareDataDto.setEmployeeNo(userInfo.getEmployeeNo());
                        userFnShareDataDto.setUserId(userInfo.getUserId());
                        userFnShareDataDto.setUserName(userInfo.getUserName());
                        userFnShareDataDto.setShareData(new HashMap<>());
                        userFnShareDataDto.getShareData().put(projectId, shareDay);
                        userFnShareDataDto.setShareTotal(shareDay);
                        userFnShareDataDtoList.add(userFnShareDataDto);
                    }
                }
            }

            // 月工作周期的工作日总和
//            Row row = sheet.getRow(rowIndexEnd + 1);
//            if (row != null) {
//                Cell workDayPeriodCell = row.getCell(5);
//                Cell WorkDayCell = row.getCell(7);
//                if (MyCellUtils.getCellValue(workDayPeriodCell).isEmpty()) {
//                    workDayPeriodCell = null;
//                    errorLogs.add("本月核算时间未填写。");
//                }
//                if (MyCellUtils.getCellValue(WorkDayCell).isEmpty()) {
//                    WorkDayCell = null;
//                    errorLogs.add("本月工作周期的工作日之和未填写。");
//                }
//
//                fnPlatShareMonthInfoMapper.deleteByPlatIdAndYearAndMonth(platId, year, month);
//                FnPlatShareMonthInfo fnPlatShareMonthInfo = new FnPlatShareMonthInfo();
//                fnPlatShareMonthInfo.setYear(year);
//                fnPlatShareMonthInfo.setMonth(month);
//                fnPlatShareMonthInfo.setPlatId(platId);
//                fnPlatShareMonthInfo.setWorkDay(WorkDayCell == null ? null : (float) WorkDayCell.getNumericCellValue());
//                fnPlatShareMonthInfo.setWorkdayPeriod(workDayPeriodCell == null ? null : workDayPeriodCell.getStringCellValue());
//                fnPlatShareMonthInfo.setCreateTime(new Date());
//                fnPlatShareMonthInfoMapper.insert(fnPlatShareMonthInfo);
//            }

            //每个人的分摊数据读取完了，开始计算百分比、抹平
            BigDecimal shareRate = new BigDecimal(MySystemParamUtils.getSystemConfigWithDefaultValue(MySystemParamUtils.Key.FixMemberRate, MySystemParamUtils.DefaultValue.fixMemberRate));
            BigDecimal zero = new BigDecimal(0);
            if (userFnShareDataDtoList.size() > 0) {
                List<FnPlatShareConfig> fnPlatShareConfigList = new ArrayList<>();
                for (UserFnShareDataDto userFnShareDataDto : userFnShareDataDtoList) {
                    BigDecimal shareTotal = userFnShareDataDto.getShareTotal();
                    if (shareTotal.compareTo(zero) == 0) {
                        errorLogs.add("员工: " + userFnShareDataDto.getUserName() + "总分摊比例为0");
                        continue;
                    }
                    //计算FnPlatShareConfig
                    for (Map.Entry<Long, BigDecimal> item : userFnShareDataDto.getShareData().entrySet()) {
                        FnPlatShareConfig fnPlatShareConfig = new FnPlatShareConfig();
                        fnPlatShareConfig.setPlatId(platId);
                        fnPlatShareConfig.setProjectId(item.getKey());
                        fnPlatShareConfig.setYear(year);
                        fnPlatShareConfig.setMonth(month);
                        fnPlatShareConfig.setShareAmount(item.getValue());
                        fnPlatShareConfig.setSharePro(item.getValue().divide(shareTotal, 4, BigDecimal.ROUND_HALF_UP));
                        fnPlatShareConfig.setCreateBy(userFnShareDataDto.getUserId());
                        fnPlatShareConfig.setCreateTime(new Date());
                        fnPlatShareConfig.setWeight(BigDecimal.ONE);

                        //分摊比例大于90%，平台成员固化人数在此项目为1
                        if (fnPlatShareConfig.getSharePro().compareTo(shareRate) > 0) {
                            fnPlatShareConfig.setFixedNumber(BigDecimal.valueOf(1L));
                        }
                        fnPlatShareConfigList.add(fnPlatShareConfig);
                    }

                    //抹平
                    FnPlatShareConfig fnPlatShareConfigMax = null;
                    BigDecimal sharePercentTotal = BigDecimal.ZERO;
                    for (FnPlatShareConfig fnPlatShareConfig : fnPlatShareConfigList) {
                        if (fnPlatShareConfig.getCreateBy().equals(userFnShareDataDto.getUserId())) {
                            sharePercentTotal = sharePercentTotal.add(fnPlatShareConfig.getSharePro());

                            if (null == fnPlatShareConfigMax || fnPlatShareConfig.getSharePro().compareTo(fnPlatShareConfigMax.getSharePro()) > 0) {
                                fnPlatShareConfigMax = fnPlatShareConfig;
                            }
                        }
                    }
                    if (null != fnPlatShareConfigMax) {
                        if (sharePercentTotal.compareTo(BigDecimal.ONE) < 0) {
                            fnPlatShareConfigMax.setSharePro(fnPlatShareConfigMax.getSharePro().add(BigDecimal.ONE.subtract(sharePercentTotal)));
                        } else if (sharePercentTotal.compareTo(BigDecimal.ONE) > 0) {
                            fnPlatShareConfigMax.setSharePro(fnPlatShareConfigMax.getSharePro().subtract(sharePercentTotal.subtract(BigDecimal.ONE)));
                        }
                    }

                }

                //先删掉再插
                if (fnPlatShareConfigList.size() > 0) {
                    // 删个人
                    fnPlatShareConfigMapper.deleteByPlatIdAndYearAndMonth(platId, year, month);
                    // 删汇总
                    fnSumShareConfigMapper.deleteByPlatIdAndYearAndMonth(platId, year, month);

                    fnPlatShareConfigMapper.batchInsert(fnPlatShareConfigList);
                }

            }

            // 开始读分摊汇总
//            Long userId = MyTokenUtils.getCurrentUserId();
//            Row rowSumShare = sheet.getRow(rowIndexEnd + 5);
//            List<FnSumShareConfig> fnSumShareConfigList = new ArrayList<>();
//            for (Map.Entry<Integer, Long> entry : mapColumnProject.entrySet()) {
//                Cell cellSumOfProject = rowSumShare.getCell(entry.getKey());
//                Double cellValue = cellSumOfProject.getNumericCellValue();
//                if (null == cellValue || cellValue.equals(Double.valueOf(0f))) {
//                    continue;
//                }
//
//                FnSumShareConfig fnSumShareConfig = new FnSumShareConfig();
//                fnSumShareConfig.setPlatId(platId);
//                fnSumShareConfig.setProjectId(entry.getValue());
//                fnSumShareConfig.setYear(year);
//                fnSumShareConfig.setMonth(month);
//                fnSumShareConfig.setSharePro(BigDecimal.valueOf(cellValue));
//                fnSumShareConfig.setUpdateBy(userId);
//                fnSumShareConfig.setLockFlag(false);
//                fnSumShareConfig.setCreateTime(new Date());
//
//                fnSumShareConfigList.add(fnSumShareConfig);
//            }
//            if (fnSumShareConfigList.size() > 0) {
//                fnSumShareConfigMapper.deleteByPlatIdAndYearAndMonth(platId, year, month);
//
//                fnSumShareConfigMapper.batchInsert(fnSumShareConfigList);
//            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }

        // 保存错误日志
        String fileName = "import-error-" + platId + "-" + year + "-" + month;
        ExcelHelper.saveErrorFile(errorLogs, "share-import/", fileName);
        if (errorLogs.size() > 0) {
            String downloadURL = backupExcelUrl + "/" + fileName + ".txt";
            MyFileUtils.copyFile(ExcelHelper.ERR_LOG_DIR + "share-import/" + fileName + ".log", filePathPrefix + downloadURL);
            return downloadURL;
        }
        return "";
    }

    @Override
    public FnShareTemplate getShareTemplate(int year, int month) {
        return null;
    }

    @Override
    public void copyFromLastMonth(Long platId, int year, int week) {
        Calendar nowDate = Calendar.getInstance();
        nowDate.setFirstDayOfWeek(Calendar.MONDAY);
        nowDate.set(Calendar.YEAR, year);
        nowDate.set(Calendar.WEEK_OF_YEAR, week);
    }

    @Override
    public void startNewMonthShareConfig(int year, int month) {
    }

    @Override
    public FnShareInfo getLatestFnShareInfo() {
        return null;
    }

    @Override
    @Transactional
    public void saveBatchShareConfig(FnPlatShareBatchVo fnPlatShareBatchVo) {
    }

    @Override
    @Deprecated
    public String exportPlatMonthShareData(Long platId, Integer year, Integer month) {
        return null;
    }


    /**
     * 前台需求，若某个项目未填写分摊项，则返回该项目的空值记录。
     *
     * @param platId
     * @param configs
     * @return
     */
    private List<FnPlatShareConfigVo> addEmptyDataForMissedProjects(Long platId, List<FnPlatShareConfigVo> configs, List<FnSumShareConfig> sumShareConfigs) {
        List<ProjectVo> shareProjects = projectMapper.selectAllShareProject();
        // 过滤平台-公司项目
        shareProjects = shareProjects.stream().filter(x -> !Project.Id.PLAT_COMPANY.equals(x.getId())).collect(Collectors.toList());
        for (ProjectVo shareProject : shareProjects) {
            boolean isContainsInShareProjects = false;
            for (FnPlatShareConfigVo configVo : configs) {
                if (configVo.getProjectId().equals(shareProject.getId())) {
                    isContainsInShareProjects = true;
                    break;
                }
            }
            if (!isContainsInShareProjects) {
                FnPlatShareConfigVo emptyOne = new FnPlatShareConfigVo();
                emptyOne.setProjectId(shareProject.getId());
                emptyOne.setPlatId(platId);
                emptyOne.setProjectName(shareProject.getName());
                emptyOne.setProjectUsedNames(shareProject.getUsedNamesStr());
                emptyOne.setCity(shareProject.getCity());

                // 补充填写sumShare
                if (sumShareConfigs != null) {
                    for (FnSumShareConfig sumShareConfig : sumShareConfigs) {
                        if (shareProject.getId().equals(sumShareConfig.getProjectId())) {
                            emptyOne.setSumSharePro(sumShareConfig.getSharePro());
                            emptyOne.setSumShareProId(sumShareConfig.getId());
                            break;
                        }
                    }
                }
                configs.add(emptyOne);
            }
        }
        return configs;
    }

    // 数据库中存放了平台常用的分摊项目，该方法将这些项目前置。
    @Override
    public List<Long> resortForFavorProject(Long platId, List<Long> projectIds) {
        List<Long> favorProjectIds = fnPlatFavorProjectMapper.selectFavorProjectIdsByPlatId(platId);

        List<Long> newProjectIds = new ArrayList<>();
        if (!CollectionUtils.isEmpty(favorProjectIds)) {
            List<Long> existFavorProjects = new ArrayList<>();
            List<Long> others = new ArrayList<>();
            projectIds.forEach(p -> {
                if (favorProjectIds.contains(p)) {
                    existFavorProjects.add(p);
                } else {
                    others.add(p);
                }
            });
            newProjectIds.addAll(existFavorProjects);
            newProjectIds.addAll(others);
        } else {
            newProjectIds = projectIds;
        }
        return newProjectIds;
    }

    @Override
    public String exportPlatMonthShareConfig(Integer year, Integer month, List<Long> platIds, String filePath) throws IOException {
        List<FnPlatShareConfigUserDTO> fnPlatShareConfigs = fnPlatShareConfigMapper.selectConfigUserDTOByPlatIdsAndYearAndMonth(platIds, year, month);
        if (!fnPlatShareConfigs.isEmpty()) {
            Map<Long, List<FnPlatShareConfigUserDTO>> shareConfigsByUserIdMap = new LinkedHashMap<>();
            Set<Long> projectSet = new HashSet<>();
            for (FnPlatShareConfigUserDTO fnPlatShareConfig : fnPlatShareConfigs) {
                if (shareConfigsByUserIdMap.containsKey(fnPlatShareConfig.getCreateBy())) {
                    shareConfigsByUserIdMap.get(fnPlatShareConfig.getCreateBy()).add(fnPlatShareConfig);
                } else {
                    List<FnPlatShareConfigUserDTO> configUserDTOS = new ArrayList<>();
                    configUserDTOS.add(fnPlatShareConfig);
                    shareConfigsByUserIdMap.put(fnPlatShareConfig.getCreateBy(), configUserDTOS);
                }
                projectSet.add(fnPlatShareConfig.getProjectId());
            }

            List<Project> projects = projectMapper.selectByIds(new ArrayList<>(projectSet));
            Map<Long, Integer> cellIndexByProjectIdMap = new HashMap<>();
            Workbook wb = new HSSFWorkbook();
            Sheet sheet = wb.createSheet();
            // 百分比格式
            CellStyle percentageStyle = wb.createCellStyle();
            percentageStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00%"));
            percentageStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

            int rowIndex = 0;
            Row firstRow = sheet.createRow(rowIndex);
            rowIndex++;
            Cell firstCell = firstRow.createCell(0);
            firstCell.setCellValue("员工编号");
            Cell secondCell = firstRow.createCell(1);
            secondCell.setCellValue("姓名");
            Cell threeCell = firstRow.createCell(2);
            threeCell.setCellValue("性别");
            Cell fourCell = firstRow.createCell(3);
            fourCell.setCellValue("状态");
            Cell fiveCell = firstRow.createCell(4);
            fiveCell.setCellValue("岗位");
            Cell sixCell = firstRow.createCell(5);
            sixCell.setCellValue("入职日期");
            Cell employeeNoCell = firstRow.createCell(6);
            employeeNoCell.setCellValue("功能组");
            Integer index = 7;
            for (Project project : projects) {
                Cell cell = firstRow.createCell(index);
                cell.setCellValue(project.getName());
                cellIndexByProjectIdMap.put(project.getId(), index);
                index++;
            }

            for (Map.Entry<Long, List<FnPlatShareConfigUserDTO>> entry : shareConfigsByUserIdMap.entrySet()) {
                FnPlatShareConfigUserDTO item = entry.getValue().get(0);
                Row row = sheet.createRow(rowIndex);
                Cell groupNameCell = row.createCell(0);
                groupNameCell.setCellValue(item.getEmployeeNo());
                Cell userNameCell = row.createCell(1);
                userNameCell.setCellValue(item.getUserName());
                Cell employeeCell = row.createCell(2);
                employeeCell.setCellValue(item.getGender());
                Cell cell1Three = row.createCell(3);
                cell1Three.setCellValue(item.getWorkStatus());
                Cell cellFour = row.createCell(4);
                cellFour.setCellValue(item.getPost());
                Cell cellFive = row.createCell(5);
                cellFive.setCellValue(item.getInDate());
                Cell cellSix = row.createCell(6);
                cellSix.setCellValue(item.getWorkGroupName());
                for (FnPlatShareConfigUserDTO valueItem : entry.getValue()) {
                    if (cellIndexByProjectIdMap.containsKey(valueItem.getProjectId())) {
                        Integer cellIndex = cellIndexByProjectIdMap.get(valueItem.getProjectId());
                        Cell cell = row.createCell(cellIndex);
                        cell.setCellValue(valueItem.getSharePro().floatValue());
                        cell.setCellStyle(percentageStyle);
                    }
                }
                rowIndex++;
            }
            FileOutputStream fileOutputStream = new FileOutputStream(new File(filePathPrefix + filePath));
            wb.write(fileOutputStream);
            fileOutputStream.close();
            wb.close();
        }
        return filePath;
    }

    @Override
    // 文件只要文件名，不要路径
    public String exportPlatShareData(Long platId, List<FnPlatShareConfigUserDTO> fnPlatShareConfigs, String fileName, String workDayStr, Map<Long, String> remarkByUserIdMap, BigDecimal workday, String workdayPeriod) {
        Workbook wb = new HSSFWorkbook();
        FileOutputStream fos = null;
        try {
            String resultFilePath = createPlatShareData(wb, null,platId, fnPlatShareConfigs,  fileName,  workDayStr,  remarkByUserIdMap,workday,workdayPeriod);
            if(!StringUtils.isEmpty(resultFilePath)) {
                createExcel(wb, fos, filePathPrefix + resultFilePath);//生成文件
            }
            return resultFilePath;
        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            return null;
        }finally {
            try {
                if(fos !=null){
                    fos.close();
                }
                if(wb !=null){
                    wb.close();
                }
            } catch (IOException e) {
                logger.error("关闭流异常",e);
                e.printStackTrace();
            }
        }
    }

    /**
     * 导出分摊数据
     * @param wb
     * @param platId
     * @param fnPlatShareConfigs
     * @param fileName
     * @param workDayStr
     * @param remarkByUserIdMap
     * @return
     */
    public String createPlatShareData(Workbook wb,FnPlatWeekShareReporKeytDto fnPlatWeekShareReporKeytDto,Long platId, List<FnPlatShareConfigUserDTO> fnPlatShareConfigs , String fileName, String workDayStr
            , Map<Long, String> remarkByUserIdMap,BigDecimal workday,String workdayPeriod) {
        Map<Long, List<FnPlatShareConfigUserDTO>> platShareConfigByUserIdMap = new HashMap<>();
        // 这个map和set用来按工作组排序
        Map<Long, Set<Long>> userIdsByWorkGroupIdMap = new HashMap<>();
        Set<Long> workGroupIdSet = new TreeSet<>();

        Set<Long> projectSet = new HashSet<>();
        for (FnPlatShareConfigUserDTO fnPlatShareConfig : fnPlatShareConfigs) {
            if (platShareConfigByUserIdMap.containsKey(fnPlatShareConfig.getCreateBy())) {
                platShareConfigByUserIdMap.get(fnPlatShareConfig.getCreateBy()).add(fnPlatShareConfig);
            } else {
                List<FnPlatShareConfigUserDTO> data = new ArrayList<>();
                data.add(fnPlatShareConfig);
                platShareConfigByUserIdMap.put(fnPlatShareConfig.getCreateBy(), data);
            }
            if (fnPlatShareConfig.getProjectId() != null) {
                projectSet.add(fnPlatShareConfig.getProjectId());
            }
            if (userIdsByWorkGroupIdMap.containsKey(fnPlatShareConfig.getWorkGroupId())) {
                userIdsByWorkGroupIdMap.get(fnPlatShareConfig.getWorkGroupId()).add(fnPlatShareConfig.getCreateBy());
            } else {
                workGroupIdSet.add(fnPlatShareConfig.getWorkGroupId());
                Set<Long> userIds = new TreeSet<>();
                userIds.add(fnPlatShareConfig.getCreateBy());
                userIdsByWorkGroupIdMap.put(fnPlatShareConfig.getWorkGroupId(), userIds);
            }
        }
        List<Long> projectIds = new ArrayList<>(projectSet);

        // 将项目名按常用序列重排
        projectIds = resortForFavorProject(platId, projectIds);

        List<Project> projects = projectMapper.selectAll();
        Map<Long, Project> projectByIdMap = projects.stream().collect(Collectors.toMap(x -> x.getId(), x -> x));
        String resultFilePath = exportExcelPath + File.separator + projectByIdMap.get(platId).getName() + fileName;
        // 项目的列坐标
        Map<Long, Integer> projectCellIndexByProjectIdMap = new HashMap<>();
        //  各项目工作日
        Map<Long, BigDecimal> workDaysByProjectIdMap = new HashMap<>();
        // 各个项目参与过人数
        Map<Long, BigDecimal> workPeoplesByProjectIdMap = new HashMap<>();


        // 粗体显示
        CellStyle overstrikingStyle = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        overstrikingStyle.setFont(font);

        // 灰色字体
        CellStyle greyStyle = wb.createCellStyle();
        Font grayFont = wb.createFont();
        grayFont.setColor(HSSFColor.GREY_50_PERCENT.index);/// #C0C0C0
        greyStyle.setFont(grayFont);

        // 黄色背景
        CellStyle yellowStyle = wb.createCellStyle();
        yellowStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        yellowStyle.setFillForegroundColor(HSSFColor.YELLOW.index);
        yellowStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

        // 黄色背景+粗体
        CellStyle yellowAndOverstrikingStyle = wb.createCellStyle();
        Font yellowAndOverstrikingStyleFont = wb.createFont();
        yellowAndOverstrikingStyleFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        yellowAndOverstrikingStyle.setFont(font);
        yellowAndOverstrikingStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        yellowAndOverstrikingStyle.setFillForegroundColor(HSSFColor.GOLD.index);
        yellowAndOverstrikingStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

        // 居中
        CellStyle midStyle = wb.createCellStyle();
        midStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

        // 右对齐+加粗
        CellStyle rightStyle = wb.createCellStyle();
        Font rightStyleFont = wb.createFont();
        rightStyleFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        rightStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
        rightStyle.setFont(rightStyleFont);

        // 百分比格式
        CellStyle percentageStyle = wb.createCellStyle();
        percentageStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00%"));
        percentageStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

        // 保留两位小数格式
        CellStyle oneScaleStyle = wb.createCellStyle();
        oneScaleStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("#,#0.0"));
        oneScaleStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);

        Sheet sheet = wb.createSheet();
        if(fnPlatWeekShareReporKeytDto!=null){
            wb.setSheetName(fnPlatWeekShareReporKeytDto.getIndex(), fnPlatWeekShareReporKeytDto.getSheetName());
        }
        int rowIndex = 0;
        Row firstRow = sheet.createRow(rowIndex++);

        int cellIndex = 0;
        BigDecimal totalWorkDays = new BigDecimal(0);
        int workDayCellIndex = 0;


        // 首行员工信息
        for (int i = 0; i < FnShareHelper.exportPlatShareConfigColums.size() - 3; i++) {
            Cell cell = firstRow.createCell(cellIndex++);
            cell.setCellValue(FnShareHelper.exportPlatShareConfigColumNameMap.get(FnShareHelper.exportPlatShareConfigColums.get(i)));
            cell.setCellStyle(midStyle);
        }

        //首行项目
        Long platInShareProjectId = ReportHelper.PlatInShareProjectMap.get(Long.valueOf(platId));
        boolean platInShareProjectFlag = false;
        int firstRowLastCellCondition = 2;
        for (Long projectId : projectIds) {
            if (projectId.equals(platInShareProjectId)) {
                platInShareProjectFlag = true;
                firstRowLastCellCondition = 3;
                continue;
            }
            Cell projectNameCell = firstRow.createCell(cellIndex);
            projectNameCell.setCellValue(projectByIdMap.get(projectId).getName());
            projectNameCell.setCellStyle(yellowStyle);
            projectCellIndexByProjectIdMap.put(projectId, cellIndex);
            cellIndex++;
        }
        // 将平台-项目放到最后
        if (platInShareProjectFlag) {
            projectCellIndexByProjectIdMap.put(platInShareProjectId, cellIndex);
        }

        // 平台-公司,工作日,备注说明
        for (int i = FnShareHelper.exportPlatShareConfigColums.size() - firstRowLastCellCondition; i < FnShareHelper.exportPlatShareConfigColums.size(); i++) {
            String cellName = FnShareHelper.exportPlatShareConfigColumNameMap.get(FnShareHelper.exportPlatShareConfigColums.get(i));
            if ("工作日".equals(cellName)) {
                workDayCellIndex = cellIndex;
            }
            Cell cell = firstRow.createCell(cellIndex);
            cell.setCellValue(cellName);
            cell.setCellStyle(midStyle);
            cellIndex++;
        }

        // 回到备注的列标，下方需要回填数据
        cellIndex--;

        try {
            // 填充用户数据

            for (Long workGroupId : workGroupIdSet) {
                Set<Long> userIds = userIdsByWorkGroupIdMap.get(workGroupId);
                for (Long userId : userIds) {
                    List<FnPlatShareConfigUserDTO> configUserDTOS = platShareConfigByUserIdMap.get(userId);
                    Row row = sheet.createRow(rowIndex++);
                    FnPlatShareConfigUserDTO fnPlatShareConfig = configUserDTOS.get(0);
                    // 用户基本信息数据
                    for (int i = 0; i < 7; i++) {
                        Method method = FnPlatShareConfigUserDTO.class.getMethod("get" + FnShareHelper.exportPlatShareConfigColums.get(i));
                        Object invoke = method.invoke(fnPlatShareConfig);
                        if (invoke != null) {
                            Cell cell = row.createCell(row.getPhysicalNumberOfCells());
                            cell.setCellValue(invoke.toString());
                            cell.setCellStyle(oneScaleStyle);
                        }
                    }

                    // 用户分摊数据
                    BigDecimal sumWorkDay = new BigDecimal(0);
                    for (FnPlatShareConfigUserDTO fnPlatShareConfigUserDTO : configUserDTOS) {

                        Long projectId = fnPlatShareConfigUserDTO.getProjectId();
                        if (projectCellIndexByProjectIdMap.containsKey(projectId)) {
                            Integer projectCellIndex = projectCellIndexByProjectIdMap.get(projectId);
                            BigDecimal shareAmount = fnPlatShareConfigUserDTO.getShareAmount();
                            Cell shareAmountCell = row.createCell(projectCellIndex);
                            shareAmountCell.setCellValue(shareAmount.doubleValue());
                            shareAmountCell.setCellStyle(midStyle);
                            totalWorkDays = totalWorkDays.add(shareAmount);
                            sumWorkDay = sumWorkDay.add(shareAmount);
                            if (workDaysByProjectIdMap.containsKey(projectId)) {
                                workDaysByProjectIdMap.put(projectId, workDaysByProjectIdMap.get(projectId).add(shareAmount));
                            } else {
                                workDaysByProjectIdMap.put(projectId, shareAmount);
                            }
                        }

                        if (remarkByUserIdMap.containsKey(userId)) {
                            Cell remarkCell1 = row.createCell(cellIndex);
                            remarkCell1.setCellValue(remarkByUserIdMap.get(userId));
                        }
                        if (projectId != null) {

                            if (!projectId.equals(platInShareProjectId)) {
                                if (workPeoplesByProjectIdMap.containsKey(projectId)) {
                                    workPeoplesByProjectIdMap.put(projectId, workPeoplesByProjectIdMap.get(projectId).add(new BigDecimal(1)));
                                } else {
                                    workPeoplesByProjectIdMap.put(projectId, new BigDecimal(1));
                                }
                                // 项目参与人数，需要排除平台
                            } else {
                                workPeoplesByProjectIdMap.put(projectId, new BigDecimal(0));
                            }
                        }

                    }
                    Cell workDaysCell = row.createCell(workDayCellIndex);
                    workDaysCell.setCellValue(sumWorkDay.floatValue());
                    workDaysCell.setCellStyle(midStyle);
                }
            }


            if (totalWorkDays.compareTo(new BigDecimal(0)) == 0) {
                return null;
            }

            // 汇总数据列标
            if (workday == null || new BigDecimal(0).compareTo(workday) == 0) {
                // 没有总工作日，则不导出汇总数据
            } else {
                int sumCellIndex = 6;

                rowIndex++;

                Row platFixWorkDaysRow = sheet.createRow(rowIndex++);
                Cell platFixWorkDaysRowCell1 = platFixWorkDaysRow.createCell(sumCellIndex - 1);
                platFixWorkDaysRowCell1.setCellValue(workDayStr);
                platFixWorkDaysRowCell1.setCellStyle(yellowAndOverstrikingStyle);
                Cell platFixWorkDaysRowCell2 = platFixWorkDaysRow.createCell(sumCellIndex);
                platFixWorkDaysRowCell2.setCellValue(workdayPeriod);
                platFixWorkDaysRowCell2.setCellStyle(midStyle);
                Cell platFixWorkDaysRowCell3 = platFixWorkDaysRow.createCell(sumCellIndex + 1);
                platFixWorkDaysRowCell3.setCellValue(workday + " 工作日/人");

                rowIndex++;

                Row workDaysOfProjectRow = sheet.createRow(rowIndex++);
                Cell workDaysOfProjectRowCell1 = workDaysOfProjectRow.createCell(sumCellIndex - 1);
                workDaysOfProjectRowCell1.setCellValue("成本分摊");
                workDaysOfProjectRowCell1.setCellStyle(yellowAndOverstrikingStyle);
                Cell workDaysOfProjectRowCell2 = workDaysOfProjectRow.createCell(sumCellIndex);
                workDaysOfProjectRowCell2.setCellValue("各项目工作日总和");
                workDaysOfProjectRowCell2.setCellStyle(overstrikingStyle);
                Cell workDaysOfProjectRowCell3 = workDaysOfProjectRow.createCell(sumCellIndex + 1);
                workDaysOfProjectRowCell3.setCellValue(totalWorkDays.doubleValue());
                workDaysOfProjectRowCell3.setCellStyle(oneScaleStyle);

                Row projectNameRow = sheet.createRow(rowIndex++);
                Cell projectNameRowCell1 = projectNameRow.createCell(sumCellIndex);
                projectNameRowCell1.setCellValue("    项目名称");
                projectNameRowCell1.setCellStyle(overstrikingStyle);

                Row sumWorkDaysRow = sheet.createRow(rowIndex++);
                Cell sumWorkDaysRowCell1 = sumWorkDaysRow.createCell(sumCellIndex);
                sumWorkDaysRowCell1.setCellValue("    各项目工作日");
                sumWorkDaysRowCell1.setCellStyle(overstrikingStyle);


                Row workDaysAsPeoplesRow = sheet.createRow(rowIndex++);
                Cell workDaysAsPeoplesRowCell1 = workDaysAsPeoplesRow.createCell(sumCellIndex);
                workDaysAsPeoplesRowCell1.setCellValue("    工作日折算成人数");
                workDaysAsPeoplesRowCell1.setCellStyle(overstrikingStyle);

                Row percentageRow = sheet.createRow(rowIndex++);
                Cell percentageRowCell1 = percentageRow.createCell(sumCellIndex);
                percentageRowCell1.setCellValue("    百分比");
                percentageRowCell1.setCellStyle(overstrikingStyle);


                rowIndex = rowIndex + 1;

                Row totalWorkPeoplesRow = sheet.createRow(rowIndex++);
                Cell totalWorkPeoplesRowCell1 = totalWorkPeoplesRow.createCell(sumCellIndex - 1);
                totalWorkPeoplesRowCell1.setCellValue("参与人数");
                totalWorkPeoplesRowCell1.setCellStyle(yellowAndOverstrikingStyle);
                Cell totalWorkPeoplesRowCell2 = totalWorkPeoplesRow.createCell(sumCellIndex);
                totalWorkPeoplesRowCell2.setCellValue("各个项目参与过人数总和");
                totalWorkPeoplesRowCell2.setCellStyle(overstrikingStyle);
                Cell totalWorkPeoplesRowCell3 = totalWorkPeoplesRow.createCell(sumCellIndex + 1);

                Row secondProjectNameRow = sheet.createRow(rowIndex++);
                Cell secondProjectNameRowCell1 = secondProjectNameRow.createCell(sumCellIndex);
                secondProjectNameRowCell1.setCellValue("    项目名称");
                secondProjectNameRowCell1.setCellStyle(overstrikingStyle);

                Row workPeoplesOfProjectRow = sheet.createRow(rowIndex++);
                Cell workPeoplesOfProjectRowCell1 = workPeoplesOfProjectRow.createCell(sumCellIndex);
                workPeoplesOfProjectRowCell1.setCellValue("    各个项目参与过人数");
                workPeoplesOfProjectRowCell1.setCellStyle(overstrikingStyle);

                Row peoplePercentageRow = sheet.createRow(rowIndex++);
                Cell peoplePercentageRowCell1 = peoplePercentageRow.createCell(sumCellIndex);
                peoplePercentageRowCell1.setCellValue("    人员固定率");
                peoplePercentageRowCell1.setCellStyle(overstrikingStyle);

                // 填充汇总数据
                sumCellIndex++;
                BigDecimal totalWorkDaysAsPeoples = new BigDecimal(0);
                BigDecimal totalPercentage = new BigDecimal(0);
                BigDecimal totalWorkPeoples = new BigDecimal(0);
                for (Long projectId : projectIds) {

                    Integer projectCellIndex = projectCellIndexByProjectIdMap.get(projectId);
                    Cell projectNameCell = projectNameRow.createCell(projectCellIndex);
                    Cell secondProjectNameRowCell = secondProjectNameRow.createCell(projectCellIndex);
                    if (projectId.equals(platInShareProjectId)) {
                        projectNameCell.setCellValue("平台-公司");
                        secondProjectNameRowCell.setCellValue("平台-公司");
                    } else {
                        projectNameCell.setCellValue(projectByIdMap.get(projectId).getName());
                        secondProjectNameRowCell.setCellValue(projectByIdMap.get(projectId).getName());
                    }
                    projectNameCell.setCellStyle(yellowStyle);
                    secondProjectNameRowCell.setCellStyle(yellowStyle);

                    if (workDaysByProjectIdMap.get(projectId) != null) {
                        BigDecimal sumWorkDay = workDaysByProjectIdMap.get(projectId);
                        // 各项目工作日
                        Cell workDayAmountOfProjectCell = sumWorkDaysRow.createCell(projectCellIndex);
                        workDayAmountOfProjectCell.setCellValue(sumWorkDay.doubleValue());
                        workDayAmountOfProjectCell.setCellStyle(oneScaleStyle);
                        // 工作日折算成人数
                        Cell workDayAsPeopleAmountCell = workDaysAsPeoplesRow.createCell(projectCellIndex);
                        BigDecimal peoples = sumWorkDay.divide(workday, 4, BigDecimal.ROUND_HALF_UP);
                        totalWorkDaysAsPeoples = totalWorkDaysAsPeoples.add(peoples);
                        workDayAsPeopleAmountCell.setCellValue(peoples.doubleValue());
                        workDayAsPeopleAmountCell.setCellStyle(oneScaleStyle);
                        // 百分比
                        Cell percentageCell = percentageRow.createCell(projectCellIndex);
                        BigDecimal percentage = sumWorkDay.divide(totalWorkDays, 4, BigDecimal.ROUND_HALF_UP);
                        totalPercentage = totalPercentage.add(percentage);
                        percentageCell.setCellValue(percentage.doubleValue());
                        percentageCell.setCellStyle(percentageStyle);
                        // 各个项目参与过人数
                        Cell peopleAmountByProjectCell = workPeoplesOfProjectRow.createCell(projectCellIndex);
                        totalWorkPeoples = totalWorkPeoples.add(workPeoplesByProjectIdMap.get(projectId));
                        peopleAmountByProjectCell.setCellValue(workPeoplesByProjectIdMap.get(projectId).doubleValue());
                        peopleAmountByProjectCell.setCellStyle(oneScaleStyle);
                        // 人员固定率
                        Cell peoplePercentageCell = peoplePercentageRow.createCell(projectCellIndex);
                        if (projectId.equals(platInShareProjectId)) {
                            peoplePercentageCell.setCellValue(0);
                        } else {
                            peoplePercentageCell.setCellValue(sumWorkDay.divide(workday, 4, BigDecimal.ROUND_HALF_UP).divide(workPeoplesByProjectIdMap.get(projectId), 1, BigDecimal.ROUND_HALF_UP).doubleValue());
                        }
                        peoplePercentageCell.setCellStyle(percentageStyle);
                    }
                    sumCellIndex++;
                }
                if (platInShareProjectFlag) {
                    Cell cell = totalWorkPeoplesRow.createCell(projectCellIndexByProjectIdMap.get(platInShareProjectId));
                    cell.setCellValue("公司平台是大概折算人数");
                    cell.setCellStyle(greyStyle);
                }

                totalWorkPeoplesRowCell3.setCellValue(totalWorkPeoples.doubleValue());
                totalWorkPeoplesRowCell3.setCellStyle(midStyle);

                Cell sumNameCell = projectNameRow.createCell(sumCellIndex);
                Cell secondProjectNameRowCell = secondProjectNameRow.createCell(sumCellIndex);
                sumNameCell.setCellValue("汇总");
                secondProjectNameRowCell.setCellValue("汇总");
                sumNameCell.setCellStyle(yellowStyle);
                secondProjectNameRowCell.setCellStyle(yellowStyle);

                Cell sumPeoplePercentageCell = peoplePercentageRow.createCell(sumCellIndex);
                sumPeoplePercentageCell.setCellValue(totalWorkDaysAsPeoples.divide(totalWorkPeoples, 4, BigDecimal.ROUND_HALF_UP).doubleValue());
                sumPeoplePercentageCell.setCellStyle(percentageStyle);

                Cell sumWorkDaysAsPeoplesCell = workDaysAsPeoplesRow.createCell(sumCellIndex);
                sumWorkDaysAsPeoplesCell.setCellValue(totalWorkDaysAsPeoples.doubleValue());
                sumWorkDaysAsPeoplesCell.setCellStyle(oneScaleStyle);

                Cell sumPercentageCell = percentageRow.createCell(sumCellIndex);
                sumPercentageCell.setCellValue(totalPercentage.doubleValue());
                sumPercentageCell.setCellStyle(percentageStyle);

                Cell sumWorkDaysOfProjectCell = sumWorkDaysRow.createCell(sumCellIndex);
                sumWorkDaysOfProjectCell.setCellValue(totalWorkDays.doubleValue());
                sumWorkDaysOfProjectCell.setCellStyle(oneScaleStyle);

                Cell sumWorkPeoplesOfProjectCell = workPeoplesOfProjectRow.createCell(sumCellIndex);
                sumWorkPeoplesOfProjectCell.setCellValue(totalWorkPeoples.doubleValue());
                sumWorkPeoplesOfProjectCell.setCellStyle(oneScaleStyle);

                Row directionsRow0 = sheet.createRow(rowIndex++);
                Cell directionsCell0 = directionsRow0.createCell(6);
                directionsCell0.setCellValue("人员固定率：本月折算人数和参与过人数的比例，比例越高说明本月固定参与项目的人越多，反之");
                directionsCell0.setCellStyle(greyStyle);

                rowIndex++;

                // 说明书
                Row directionsRow = sheet.createRow(rowIndex++);
                Cell directionsCell = directionsRow.createCell(5);
                directionsCell.setCellValue("说明：");
                directionsCell.setCellStyle(rightStyle);
                Cell directionsCell1 = directionsRow.createCell(6);
                directionsCell1.setCellValue("项目需要根据本地接手项目来填");
                Row directionsRow2 = sheet.createRow(rowIndex++);
                Cell directionsCel2 = directionsRow2.createCell(6);
                directionsCel2.setCellValue("人员信息需要每月整理");
                Row directionsRow3 = sheet.createRow(rowIndex++);
                Cell directionsCel3 = directionsRow3.createCell(6);
                directionsCel3.setCellValue("每个人的各项目投入时间需要手动填写，总和会自动计算，数字不能超过当月工作日");
                Row directionsRow4 = sheet.createRow(rowIndex++);
                Cell directionsCel4 = directionsRow4.createCell(6);
                directionsCel4.setCellValue("每月的工作日周期和工作日要根据当月工作时间手动输入");
                Row directionsRow5 = sheet.createRow(rowIndex++);
                Cell directionsCel5 = directionsRow5.createCell(6);
                directionsCel5.setCellValue("成本分摊数据会自动统计，不需要动");

                // 冻结首行
                sheet.createFreezePane(0, 1, 0, 1);
                //设置列自动调整宽度,
                sheet.setColumnWidth(4, "我要五个字".getBytes().length * 256);
                sheet.setColumnWidth(5, "本月核算时间：".getBytes().length * 256);
                sheet.autoSizeColumn(6);
            }
            return resultFilePath;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }
    /**
     * 创建查询参数
     * @param fnPlatWeekShareReportDto
     */
    private void setParameters(FnPlatWeekShareReportDto fnPlatWeekShareReportDto){
        List<Long> platIds = new ArrayList<Long>();//全部平台id
        platIds.add(1081L);//珠海美术中心
        platIds.add(1147L);//成都美术中心
        platIds.add(1188L);//广州美术中心
        platIds.add(1225L);//北京美术中心
        platIds.add(1260L);//武汉美术中心

        platIds.add(1248L);//音频

        platIds.add(5045L);//珠海技术中心,以及下属子平台
        platIds.add(1133L);
        platIds.add(1137L);
        platIds.add(1139L);
        platIds.add(1141L);
        platIds.add(1229L);
        platIds.add(1250L);
        platIds.add(1251L);

        platIds.add(1086L);//质量

        platIds.add(1066L);//测试

        platIds.add(1244L);//WEB
        platIds.add(1228L);//WEB

        platIds.add(1080L);//运营
        platIds.add(1266L);//运营
        fnPlatWeekShareReportDto.setPlatIds(platIds);

        List<Long> artsPlatIds = new ArrayList<Long>();//美术合并
        artsPlatIds.add(1081L);//珠海美术中心
        artsPlatIds.add(1147L);//成都美术中心
        artsPlatIds.add(1188L);//广州美术中心
        artsPlatIds.add(1225L);//北京美术中心
        artsPlatIds.add(1260L);//武汉美术中心
        fnPlatWeekShareReportDto.setArtsPlatIds(artsPlatIds);

        Long zhPlatId = 1081L;//珠海美术中心
        fnPlatWeekShareReportDto.setZhPlatId(zhPlatId);
        Long cdPlatId = 1147L;//成都美术中心
        fnPlatWeekShareReportDto.setCdPlatId(cdPlatId);
        Long gzPlatId = 1188L;//广州美术中心
        fnPlatWeekShareReportDto.setGzPlatId(gzPlatId);
        Long bjPlatId = 1225L;//北京美术中心
        fnPlatWeekShareReportDto.setBjPlatId(bjPlatId);
        Long whPlatId = 1260L;//武汉美术中心
        fnPlatWeekShareReportDto.setWhPlatId(whPlatId);

        List<Long> audioPlatIds = new ArrayList<Long>();//音频
        audioPlatIds.add(1248L);//音频
        fnPlatWeekShareReportDto.setAudioPlatIds(audioPlatIds);

        List<Long> technologyPlatIds = new ArrayList<Long>();//技术
        technologyPlatIds.add(5045L);//珠海技术中心,以及下属子平台
        technologyPlatIds.add(1133L);
        technologyPlatIds.add(1137L);
        technologyPlatIds.add(1139L);
        technologyPlatIds.add(1141L);
        technologyPlatIds.add(1229L);
        technologyPlatIds.add(1250L);
        technologyPlatIds.add(1251L);
        fnPlatWeekShareReportDto.setTechnologyPlatIds(technologyPlatIds);

        List<Long> qualityPlatIds = new ArrayList<Long>();//质量
        qualityPlatIds.add(1086L);//质量
        fnPlatWeekShareReportDto.setQualityPlatIds(qualityPlatIds);

        List<Long> testPlatIds = new ArrayList<Long>();//测试
        testPlatIds.add(1066L);
        fnPlatWeekShareReportDto.setTestPlatIds(testPlatIds);

        List<Long> webPlatIds = new ArrayList<Long>();//WEB
        webPlatIds.add(1244L);
        webPlatIds.add(1228L);
        fnPlatWeekShareReportDto.setWebPlatIds(webPlatIds);

        List<Long> operatePlatIds = new ArrayList<Long>();//运营
        operatePlatIds.add(1080L);
        operatePlatIds.add(1266L);
        fnPlatWeekShareReportDto.setOperatePlatIds(operatePlatIds);
    }

    /**
     * 设置字体大小
     * @param wb
     * @param fontSize
     * @return
     */
    private CellStyle getCellStyle(Workbook wb,int fontSize,boolean bold){
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);//水平居中
        cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//垂直居中
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
        HSSFFont font = (HSSFFont) wb.createFont();
        font.setFontName("微软雅黑");
        if(bold){
            font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);//粗体显示
        }
        font.setFontHeightInPoints((short) fontSize);  //字体大小
        cellStyle.setFont(font);//选择需要用到的字体格式
        return cellStyle;
    }
    /**
     * 创建导出头部
     * @param sheet
     */
    private void createHead(FnPlatWeekShareReportDto fnPlatWeekShareReportDto,Sheet sheet){
        Row row = sheet.createRow(0);
        row.setHeight((short) 1000);//目的是想把行高设置成25px
        mergedRegion(sheet, 0, 0, 0,  16);
        HSSFCell cell = (HSSFCell) row.createCell(0);
        HSSFWorkbook  wb = (HSSFWorkbook) fnPlatWeekShareReportDto.getWb();

        FnWeekShareWorkdayStatus source = fnWeekShareWorkdayStatusMapper.selectByYearAndWeek(fnPlatWeekShareReportDto.getCurrentYear(),fnPlatWeekShareReportDto.getCurrentWeek());
        String str = "";
        if(source!=null){
             str = MyDateUtils.getDateStrByFnWeekShareWorkdayStatus(source);
        }
        CellStyle cellStyle  = getCellStyle(wb, 14,true);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("平台人力分布统计——"+str);

        CellStyle cellStyle0  = getCellStyle(wb, 11,true);
        HSSFPalette customPalette = wb.getCustomPalette();
        customPalette.setColorAtIndex(HSSFColor.LIGHT_BLUE.index, (byte) 255, (byte) 242, (byte) 204);
        cellStyle0.setFillForegroundColor(HSSFColor.LIGHT_BLUE.index);
        cellStyle0.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        cellStyle  = getCellStyle(wb, 11,true);
        row = sheet.createRow(1);
        cell = (HSSFCell) row.createCell(0);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("序号");
        cell = (HSSFCell) row.createCell(1);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("项目名称");
        sheet.setColumnWidth(1, 5000);
        cell = (HSSFCell) row.createCell(2);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("占全部比例");
        sheet.setColumnWidth(2, 4500);
        cell = (HSSFCell) row.createCell(3);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("总工作量(人周)");
        sheet.setColumnWidth(3, 4500);
        cell = (HSSFCell) row.createCell(4);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("与上周环比");
        sheet.setColumnWidth(4, 4500);
        cell = (HSSFCell) row.createCell(5);
        cell.setCellStyle(cellStyle0);
        cell.setCellValue("美术合并");
        sheet.setColumnWidth(5, 3500);
        cell = (HSSFCell) row.createCell(6);
        cell.setCellStyle(cellStyle0);
        cell.setCellValue("珠海");
        sheet.setColumnWidth(6, 1800);
        cell = (HSSFCell) row.createCell(7);
        cell.setCellStyle(cellStyle0);
        cell.setCellValue("广州");
        sheet.setColumnWidth(7, 1800);
        cell = (HSSFCell) row.createCell(8);
        cell.setCellStyle(cellStyle0);
        cell.setCellValue("武汉");
        sheet.setColumnWidth(8, 1800);
        cell = (HSSFCell) row.createCell(9);
        cell.setCellStyle(cellStyle0);
        cell.setCellValue("成都");
        sheet.setColumnWidth(9, 1800);
        cell = (HSSFCell) row.createCell(10);
        cell.setCellStyle(cellStyle0);
        cell.setCellValue("北京");
        sheet.setColumnWidth(10, 1800);
        cell = (HSSFCell) row.createCell(11);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("音频");
        sheet.setColumnWidth(11, 1800);
        cell = (HSSFCell) row.createCell(12);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("技术");
        sheet.setColumnWidth(12, 1800);
        cell = (HSSFCell) row.createCell(13);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("质量");
        sheet.setColumnWidth(13, 1800);
        cell = (HSSFCell) row.createCell(14);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("测试");
        sheet.setColumnWidth(14, 1800);
        cell = (HSSFCell) row.createCell(15);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("WEB");
        sheet.setColumnWidth(15, 1800);
        cell = (HSSFCell) row.createCell(16);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("运营");
        sheet.setColumnWidth(16, 1800);
    }

    /**
     * 获取平台上线和当前平台人数
     * @param fnPlatWeekShareReportDto
     * @return
     */
    private Map<String, Map<Long, BigDecimal>> getUserCount(FnPlatWeekShareReportDto fnPlatWeekShareReportDto){
        List<FnPlatWeekShareUserCountDto> users = new ArrayList<>();
        List<Long> platIds = new ArrayList<Long>();
        platIds.add(1081L);//珠海美术中心
        platIds.add(1147L);//成都美术中心
        platIds.add(1188L);//广州美术中心
        platIds.add(1225L);//北京美术中心
        platIds.add(1260L);//武汉美术中心

        platIds.add(1248L);//音频
        platIds.add(5045L);//珠海技术中心
        platIds.add(1086L);//质量
        platIds.add(1066L);//测试
        platIds.add(1244L);//WEB

        platIds.add(1080L);//珠海运营
        platIds.add(1266L);//成都运营
        for(Long platId : platIds){
            users.addAll(fnPlatShareConfigMapper.selectUserNo(platId));
        }
        Map<String, Map<Long, BigDecimal>> all = new HashMap<String, Map<Long, BigDecimal>>();
        if(users !=null) {
            List<FnPlatWeekShareUserCountDto> limit = users.stream().filter(fnPlatWeekShareUserCountDto -> "1".equals(fnPlatWeekShareUserCountDto.getType())).collect(Collectors.toList());
            Map<Long, BigDecimal> mapLimit = limit.stream().collect(Collectors.toMap(fnPlatWeekShareUserCountDto -> fnPlatWeekShareUserCountDto.getId(), fnPlatWeekShareUserCountDto -> fnPlatWeekShareUserCountDto.getTotal()));
            all.put("1",mapLimit);
            List<FnPlatWeekShareUserCountDto> current = users.stream().filter(fnPlatWeekShareUserCountDto -> "2".equals(fnPlatWeekShareUserCountDto.getType())).collect(Collectors.toList());
            Map<Long, BigDecimal> mapCurrent = current.stream().collect(Collectors.toMap(fnPlatWeekShareUserCountDto -> fnPlatWeekShareUserCountDto.getId(), fnPlatWeekShareUserCountDto -> fnPlatWeekShareUserCountDto.getTotal()));
            all.put("2",mapCurrent);
        }
        return all;
    }

    /**
     * 折算成人数汇总
     * @param items
     * @param list
     */
    private void summaryUser(FnPlatWeekShareReportDto platWeekShareReportDto,List<FnPlatWeekShareReportDto> items, List<FnPlatWeekShareReportDto> list){
        BigDecimal totalWorkload = BigDecimal.ZERO;//当前周总工作量(人周)
        BigDecimal artsWorkload = BigDecimal.ZERO;//美术合并
        BigDecimal zhWorkload = BigDecimal.ZERO;//珠海
        BigDecimal gzWorkload = BigDecimal.ZERO;//广州
        BigDecimal whWorkload = BigDecimal.ZERO;//武汉
        BigDecimal cdWorkload = BigDecimal.ZERO;//成都
        BigDecimal bjWorkload = BigDecimal.ZERO;//北京
        BigDecimal audioWorkload = BigDecimal.ZERO;//音频
        BigDecimal technologyWorkload = BigDecimal.ZERO;//技术
        BigDecimal qualityWorkload = BigDecimal.ZERO;//质量
        BigDecimal testWorkload = BigDecimal.ZERO;//测试
        BigDecimal webWorkload = BigDecimal.ZERO;//WEB
        BigDecimal operateWorkload = BigDecimal.ZERO;//运营
        FnWeekShareWorkdayStatus workdayStatus = platWeekShareReportDto.getWorkdayStatus();//工作日配置信息
        BigDecimal workday = null;
        if(workdayStatus!=null){
            workday = workdayStatus.getWorkday();
        }
        if(list!=null && list.size()>0) {
            for(FnPlatWeekShareReportDto fnPlatWeekShareReportDto : list) {
                if(fnPlatWeekShareReportDto.getTotalWorkload() != null){
                    totalWorkload = totalWorkload.add(fnPlatWeekShareReportDto.getTotalWorkload());//当前周总工作量(人周)
                }
                if(fnPlatWeekShareReportDto.getArtsWorkload() != null){
                    artsWorkload = artsWorkload.add(fnPlatWeekShareReportDto.getArtsWorkload());;//美术合并
                }
                if(fnPlatWeekShareReportDto.getZhWorkload() != null){
                    zhWorkload = zhWorkload.add(fnPlatWeekShareReportDto.getZhWorkload());;//珠海
                }
                if(fnPlatWeekShareReportDto.getGzWorkload() != null){
                    gzWorkload = gzWorkload.add(fnPlatWeekShareReportDto.getGzWorkload());;//广州
                }
                if(fnPlatWeekShareReportDto.getWhWorkload() != null){
                    whWorkload = whWorkload.add(fnPlatWeekShareReportDto.getWhWorkload());;//武汉
                }
                if(fnPlatWeekShareReportDto.getCdWorkload() != null){
                    cdWorkload = cdWorkload.add(fnPlatWeekShareReportDto.getCdWorkload());;//成都
                }
                if(fnPlatWeekShareReportDto.getBjWorkload() != null){
                    bjWorkload = bjWorkload.add(fnPlatWeekShareReportDto.getBjWorkload());;//北京
                }
                if(fnPlatWeekShareReportDto.getAudioWorkload() != null){
                    audioWorkload = audioWorkload.add(fnPlatWeekShareReportDto.getAudioWorkload());;//音频
                }
                if(fnPlatWeekShareReportDto.getTechnologyWorkload() != null){
                    technologyWorkload = technologyWorkload.add(fnPlatWeekShareReportDto.getTechnologyWorkload());;//技术
                }
                if(fnPlatWeekShareReportDto.getQualityWorkload() != null){
                    qualityWorkload = qualityWorkload.add(fnPlatWeekShareReportDto.getQualityWorkload());;//质量
                }
                if(fnPlatWeekShareReportDto.getTestWorkload() != null){
                    testWorkload = testWorkload.add(fnPlatWeekShareReportDto.getTestWorkload());;//测试
                }
                if(fnPlatWeekShareReportDto.getWebWorkload() != null){
                    webWorkload = webWorkload.add(fnPlatWeekShareReportDto.getWebWorkload());;//WEB
                }
                if(fnPlatWeekShareReportDto.getOperateWorkload() != null){
                    operateWorkload = operateWorkload.add(fnPlatWeekShareReportDto.getOperateWorkload());;//运营
                }
            }
        }
        FnPlatWeekShareReportDto dto = new FnPlatWeekShareReportDto();
        dto.setSumName("折算成人数汇总（不含客服）");
        dto.setAllTotalWorkload(conversionDay(workday,totalWorkload));//总工作量
        dto.setAllArtsWorkload(conversionDay(workday,artsWorkload));//美术合并
        dto.setZhPeopleNo(conversionDay(workday,zhWorkload));//珠海
        dto.setGzPeopleNo(conversionDay(workday,gzWorkload));//广州
        dto.setWhPeopleNo(conversionDay(workday,whWorkload));//武汉
        dto.setCdPeopleNo(conversionDay(workday,cdWorkload));//成都
        dto.setBjPeopleNo(conversionDay(workday,bjWorkload));//北京
        dto.setAudioPeopleNo(conversionDay(workday,audioWorkload));//音频
        dto.setTechnologyPeopleNo(conversionDay(workday,technologyWorkload));//技术
        dto.setQualityPeopleNo(conversionDay(workday,qualityWorkload));//质量
        dto.setTestPeopleNo(conversionDay(workday,testWorkload));//测试
        dto.setWebPeopleNo(conversionDay(workday,webWorkload));//WEB
        dto.setOperatePeopleNo(conversionDay(workday,operateWorkload));//运营
        items.add(dto);
    }

    /**
     * 计算人数
     * @param ids
     * @param map
     * @return
     */
    private BigDecimal calculation(List<Long> ids,Map<Long, BigDecimal> map){
        BigDecimal result = BigDecimal.ZERO;
        BigDecimal value = null;
        for( Long id : ids){
            value = map.get(id);
            if(value!=null){
                result = result.add(value);
            }
        }
        return result;
    }
    /**
     * 获取部门上限人数
     * @param items
     * @param map
     */
    private void setDepLimitUser(FnPlatWeekShareReportDto fnPlatWeekShareReportDto,List<FnPlatWeekShareReportDto> items, Map<String, Map<Long, BigDecimal>> map ){
        FnPlatWeekShareReportDto dto = new FnPlatWeekShareReportDto();
        Map<Long, BigDecimal> limitUserCount = map.get("1");
        dto.setSumName("部门人数上限（HRD"+fnPlatWeekShareReportDto.getDate()+"数据）");
        dto.setZhPeopleNo(limitUserCount.get(1081L));//珠海
        dto.setGzPeopleNo(limitUserCount.get(1188L));//广州
        dto.setWhPeopleNo(limitUserCount.get(1260L));//武汉
        dto.setCdPeopleNo(limitUserCount.get(1147L));//成都
        dto.setBjPeopleNo(limitUserCount.get(1225L));//北京
        List<Long> ids = new ArrayList<>();
        ids.add(1080L);//珠海
        ids.add(1266L);//成都
        BigDecimal operatePeopleNo = calculation(ids,limitUserCount);//运营
        dto.setAudioPeopleNo(limitUserCount.get(1248L));//音频
        dto.setTechnologyPeopleNo(limitUserCount.get(5045L));//技术
        dto.setQualityPeopleNo(limitUserCount.get(1086L));//质量
        dto.setTestPeopleNo(limitUserCount.get(1066L));//测试
        dto.setWebPeopleNo(limitUserCount.get(1244L));//WEB
        dto.setOperatePeopleNo(operatePeopleNo);//运营
        items.add(dto);
    }

    /**
     * 获取合计部门当前人数
     * @param items
     * @param map
     */
    private void setDepCurrentUser(FnPlatWeekShareReportDto fnPlatWeekShareReportDto,List<FnPlatWeekShareReportDto> items, Map<String, Map<Long, BigDecimal>> map ){
        Map<Long, BigDecimal> currentUseCountr = map.get("2");
        FnPlatWeekShareReportDto dto = new FnPlatWeekShareReportDto();
        dto.setSumName("部门当前人数（"+fnPlatWeekShareReportDto.getDate()+"）");
        dto.setZhPeopleNo(currentUseCountr.get(1081L));//珠海
        dto.setGzPeopleNo(currentUseCountr.get(1188L));//广州
        dto.setWhPeopleNo(currentUseCountr.get(1260L));//武汉
        dto.setCdPeopleNo(currentUseCountr.get(1147L));//成都
        dto.setBjPeopleNo(currentUseCountr.get(1225L));//北京
        List<Long> ids = new ArrayList<>();
        ids.add(1080L);//珠海
        ids.add(1266L);//成都
        BigDecimal operatePeopleNo = calculation(ids,currentUseCountr);//运营
        dto.setAudioPeopleNo(currentUseCountr.get(1248L));//音频
        dto.setTechnologyPeopleNo(currentUseCountr.get(5045L));//技术
        dto.setQualityPeopleNo(currentUseCountr.get(1086L));//质量
        dto.setTestPeopleNo(currentUseCountr.get(1066L));//测试
        dto.setWebPeopleNo(currentUseCountr.get(1244L));//WEB
        dto.setOperatePeopleNo(operatePeopleNo);//运营
        items.add(dto);
    }

    /**
     * 检查人力组人数是否超上限
     * @param items
     */
    private Map<String,Boolean> check(List<FnPlatWeekShareReportDto> items){
        Map<String,Boolean> map = new HashMap();
        map.put("a",false);
        map.put("b",false);
        map.put("c",false);
        map.put("d",false);
        map.put("e",false);
        map.put("f",false);
        map.put("g",false);
        map.put("h",false);
        map.put("i",false);
        map.put("j",false);
        map.put("k",false);
        if (items != null && items.size() > 1) {
            FnPlatWeekShareReportDto fnPlatWeekShareReport1 = items.get(0);
            FnPlatWeekShareReportDto fnPlatWeekShareReport2 = items.get(1);

            BigDecimal a1 = (fnPlatWeekShareReport1.getZhPeopleNo());
            BigDecimal b1 = (fnPlatWeekShareReport1.getGzPeopleNo());
            BigDecimal c1 = (fnPlatWeekShareReport1.getWhPeopleNo());
            BigDecimal d1 = (fnPlatWeekShareReport1.getCdPeopleNo());
            BigDecimal e1 = (fnPlatWeekShareReport1.getBjPeopleNo());
            BigDecimal f1 = (fnPlatWeekShareReport1.getAudioPeopleNo());
            BigDecimal g1 = (fnPlatWeekShareReport1.getTechnologyPeopleNo());
            BigDecimal h1 = (fnPlatWeekShareReport1.getQualityPeopleNo());
            BigDecimal i1 = (fnPlatWeekShareReport1.getTestPeopleNo());
            BigDecimal j1 = (fnPlatWeekShareReport1.getWebPeopleNo());
            BigDecimal k1 = (fnPlatWeekShareReport1.getOperatePeopleNo());

            BigDecimal a2 = (fnPlatWeekShareReport2.getZhPeopleNo());
            BigDecimal b2 = (fnPlatWeekShareReport2.getGzPeopleNo());
            BigDecimal c2 = (fnPlatWeekShareReport2.getWhPeopleNo());
            BigDecimal d2 = (fnPlatWeekShareReport2.getCdPeopleNo());
            BigDecimal e2 = (fnPlatWeekShareReport2.getBjPeopleNo());
            BigDecimal f2 = (fnPlatWeekShareReport2.getAudioPeopleNo());
            BigDecimal g2 = (fnPlatWeekShareReport2.getTechnologyPeopleNo());
            BigDecimal h2 = (fnPlatWeekShareReport2.getQualityPeopleNo());
            BigDecimal i2 = (fnPlatWeekShareReport2.getTestPeopleNo());
            BigDecimal j2 = (fnPlatWeekShareReport2.getWebPeopleNo());
            BigDecimal k2 = (fnPlatWeekShareReport2.getOperatePeopleNo());

            if(isGreaterLimit(a2,a1)){
                map.put("a",true);
            }
            if(isGreaterLimit(b2,b1)){
                map.put("b",true);
            }
            if(isGreaterLimit(c2,c1)){
                map.put("c",true);
            }
            if(isGreaterLimit(d2,d1)){
                map.put("d",true);
            }
            if(isGreaterLimit(e2,e1)){
                map.put("e",true);
            }
            if(isGreaterLimit(f2,f1)){
                map.put("f",true);
            }
            if(isGreaterLimit(g2,g1)){
                map.put("g",true);
            }
            if(isGreaterLimit(h2,h1)){
                map.put("h",true);
            }
            if(isGreaterLimit(i2,i1)){
                map.put("i",true);
            }
            if(isGreaterLimit(j2,j1)){
                map.put("j",true);
            }
            if(isGreaterLimit(k2,k1)){
                map.put("k",true);
            }
        }
        return map;
    }
    /**
     * 创建合并信息
     * @param sheet
     * @param items
     */
    private void createButtom(FnPlatWeekShareReportDto fnPlatWeekShareReportDto,Sheet sheet,List<FnPlatWeekShareReportDto> items){
        Map<String, Map<Long, BigDecimal>> map = getUserCount(fnPlatWeekShareReportDto);
        setDepLimitUser(fnPlatWeekShareReportDto,items, map );//设置部门上线人数
        setDepCurrentUser(fnPlatWeekShareReportDto,items, map );//设置部门当前人数
        if (items != null && items.size() > 0) {
            Row row = null;
            HSSFCell cell = null;
            int index = 0;
            CellStyle cellStyleNor  = getCellStyle(fnPlatWeekShareReportDto.getWb(), 11,true);
            CellStyle cellStyle0  = getCellStyle(fnPlatWeekShareReportDto.getWb(), 11,true);
            CellStyle cellStyle  = getCellStyle(fnPlatWeekShareReportDto.getWb(), 11,true);
            HSSFWorkbook  wb = (HSSFWorkbook) fnPlatWeekShareReportDto.getWb();
            HSSFPalette customPalette = wb.getCustomPalette();
            customPalette.setColorAtIndex(HSSFColor.LIGHT_GREEN.index, (byte) 248, (byte) 203, (byte) 173);
            cellStyle.setFillForegroundColor(HSSFColor.LIGHT_GREEN.index);
            cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

            customPalette.setColorAtIndex(HSSFColor.LIGHT_ORANGE.index, (byte) 255, (byte) 80, (byte) 80);
            cellStyle0.setFillForegroundColor(HSSFColor.LIGHT_ORANGE.index);
            cellStyle0.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

            Map<String,Boolean> data = check(items);
            int rowIndex = fnPlatWeekShareReportDto.getRowIndex();
            for (FnPlatWeekShareReportDto fnPlatWeekShareReport : items) {//显示合并信息
//                if(index==0){
//                    cellStyle =  cellStyle0;
//                }else{
//                    cellStyle = cellStyle;
//                }
                rowIndex++;
                row = sheet.createRow(rowIndex);
                mergedRegion(sheet, rowIndex, rowIndex, 0, 2);//合并行
                cell = (HSSFCell) row.createCell(0);
                if(index==0){
                    cell.setCellStyle(cellStyleNor);
                }else {
                    cell.setCellStyle(cellStyle);
                }
                cell.setCellValue(fnPlatWeekShareReport.getSumName());

                cell = (HSSFCell) row.createCell(3);
                if(index==0){
                    cell.setCellStyle(cellStyleNor);
                }else {
                    cell.setCellStyle(cellStyle);
                }
                cell.setCellValue(getDefultValue(fnPlatWeekShareReport.getAllTotalWorkload()));

                cell = (HSSFCell) row.createCell(4);
                if(index==0){
                    cell.setCellStyle(cellStyleNor);
                }else {
                    cell.setCellStyle(cellStyle);
                }
                cell.setCellValue(getDefultValue(fnPlatWeekShareReport.getAllRingRatio()));

                cell = (HSSFCell) row.createCell(5);
                if(index==0){
                    cell.setCellStyle(cellStyleNor);
                }else {
                    cell.setCellStyle(cellStyle);
                }
                cell.setCellValue(getDefultValue(fnPlatWeekShareReport.getAllArtsWorkload()));

                cell = (HSSFCell) row.createCell(6);
                if(data.get("a") && index==0){
                    cell.setCellStyle(cellStyle0);
                }else{
                    if(index==0){
                        cell.setCellStyle(cellStyleNor);
                    }else {
                        cell.setCellStyle(cellStyle);
                    }
                }
                cell.setCellValue(getDefultValue(fnPlatWeekShareReport.getZhPeopleNo()));

                cell = (HSSFCell) row.createCell(7);
                if(data.get("b") && index==0){
                    cell.setCellStyle(cellStyle0);
                }else{
                    if(index==0){
                        cell.setCellStyle(cellStyleNor);
                    }else {
                        cell.setCellStyle(cellStyle);
                    }
                }
                cell.setCellValue(getDefultValue(fnPlatWeekShareReport.getGzPeopleNo()));

                cell = (HSSFCell) row.createCell(8);
                if(data.get("c") && index==0){
                    cell.setCellStyle(cellStyle0);
                }else{
                    if(index==0){
                        cell.setCellStyle(cellStyleNor);
                    }else {
                        cell.setCellStyle(cellStyle);
                    }
                }
                cell.setCellValue(getDefultValue(fnPlatWeekShareReport.getWhPeopleNo()));

                cell = (HSSFCell) row.createCell(9);
                if(data.get("d") && index==0){
                    cell.setCellStyle(cellStyle0);
                }else{
                    if(index==0){
                        cell.setCellStyle(cellStyleNor);
                    }else {
                        cell.setCellStyle(cellStyle);
                    }
                }
                cell.setCellValue(getDefultValue(fnPlatWeekShareReport.getCdPeopleNo()));

                cell = (HSSFCell) row.createCell(10);
                if(data.get("e") && index==0){
                    cell.setCellStyle(cellStyle0);
                }else{
                    if(index==0){
                        cell.setCellStyle(cellStyleNor);
                    }else {
                        cell.setCellStyle(cellStyle);
                    }
                }
                cell.setCellValue(getDefultValue(fnPlatWeekShareReport.getBjPeopleNo()));

                cell = (HSSFCell) row.createCell(11);
                if(data.get("f") && index==0){
                    cell.setCellStyle(cellStyle0);
                }else{
                    if(index==0){
                        cell.setCellStyle(cellStyleNor);
                    }else {
                        cell.setCellStyle(cellStyle);
                    }
                }
                cell.setCellValue(getDefultValue(fnPlatWeekShareReport.getAudioPeopleNo()));

                cell = (HSSFCell) row.createCell(12);
                if(data.get("g") && index==0){
                    cell.setCellStyle(cellStyle0);
                }else{
                    if(index==0){
                        cell.setCellStyle(cellStyleNor);
                    }else {
                        cell.setCellStyle(cellStyle);
                    }
                }
                cell.setCellValue(getDefultValue(fnPlatWeekShareReport.getTechnologyPeopleNo()));

                cell = (HSSFCell) row.createCell(13);
                if(data.get("h") && index==0){
                    cell.setCellStyle(cellStyle0);
                }else{
                    if(index==0){
                        cell.setCellStyle(cellStyleNor);
                    }else {
                        cell.setCellStyle(cellStyle);
                    }
                }
                cell.setCellValue(getDefultValue(fnPlatWeekShareReport.getQualityPeopleNo()));

                cell = (HSSFCell) row.createCell(14);
                if(data.get("i") && index==0){
                    cell.setCellStyle(cellStyle0);
                }else{
                    if(index==0){
                        cell.setCellStyle(cellStyleNor);
                    }else {
                        cell.setCellStyle(cellStyle);
                    }
                }
                cell.setCellValue(getDefultValue(fnPlatWeekShareReport.getTestPeopleNo()));

                cell = (HSSFCell) row.createCell(15);
                if(data.get("j") && index==0){
                    cell.setCellStyle(cellStyle0);
                }else{
                    if(index==0){
                        cell.setCellStyle(cellStyleNor);
                    }else {
                        cell.setCellStyle(cellStyle);
                    }
                }
                cell.setCellValue(getDefultValue(fnPlatWeekShareReport.getWebPeopleNo()));

                cell = (HSSFCell) row.createCell(16);
                if(data.get("k") && index==0){
                    cell.setCellStyle(cellStyle0);
                }else{
                    if(index==0){
                        cell.setCellStyle(cellStyleNor);
                    }else {
                        cell.setCellStyle(cellStyle);
                    }
                }
                cell.setCellValue(getDefultValue(fnPlatWeekShareReport.getOperatePeopleNo()));

                index++;
            }
        }
    }

    /**
     * 处理空值
     * @param value
     * @return
     */
    private String getDefultValue(BigDecimal value){
        if(value != null){
            return String.valueOf(value);
        }else if(value == null || BigDecimal.ZERO.equals(value)){
            return "";
        }else{
            return "";
        }
    }
    /**
     *
     * @param firstRow 起始行
     * @param lastRow 终止行
     * @param firstCol 起始列
     * @param lastCol 终止列
     */
    private void  mergedRegion(Sheet sheet,int firstRow, int lastRow, int firstCol, int lastCol){
        CellRangeAddress region1 = new CellRangeAddress(firstRow, lastRow, firstCol, (lastCol)); //参数1：起始行 参数2： 参数3： 参数4：
        sheet.addMergedRegion(region1);
    }

    /**
     * 获取占比
     * @param items
     * @param value
     * @return
     */
    private String getDayPre(BigDecimal workday,List<FnPlatWeekShareReportDto> items,   BigDecimal value){
        String result = "——";
        BigDecimal total = null;
        if(items != null && items.size()>0 && value !=null && workday !=null && BigDecimal.ZERO.compareTo(workday)!=0){
            FnPlatWeekShareReportDto dto = items.get(0);
            BigDecimal allTotalWorkload = dto.getAllTotalWorkload();
            value = value.divide(workday, 1, BigDecimal.ROUND_HALF_EVEN);
            if (allTotalWorkload != null && BigDecimal.ZERO.compareTo(allTotalWorkload) != 0) {
                total = value.divide(dto.getAllTotalWorkload(), 3, BigDecimal.ROUND_HALF_EVEN).multiply(new BigDecimal("100"));
                if (total != null) {
                    DecimalFormat df = new DecimalFormat("0.0");
                    result = df.format(total) + "%";
                }
            }
        }
        return result;
    }

    /**
     * 获取周环比
     * @param preValue
     * @param currentValue
     * @return
     */
    private String getDayPre(BigDecimal preValue, BigDecimal currentValue){
        String result = "——";
        BigDecimal total = null;
        if(preValue !=null && BigDecimal.ZERO.compareTo(preValue)!=0  && currentValue !=null){
            total = (currentValue.subtract(preValue)).divide(preValue, 4, BigDecimal.ROUND_HALF_EVEN).multiply(new BigDecimal("100"));
            if(total!=null){
                DecimalFormat df = new DecimalFormat("0.0");
                result = df.format(total) + "%";
            }
        }
        return result;
    }

    /**
     * 创建数据行
     * @param fnPlatWeekShareReportDto
     * @param sheet
     * @param items
     * @param list
     */
    private void createRowData(FnPlatWeekShareReportDto fnPlatWeekShareReportDto,Sheet sheet,List<FnPlatWeekShareReportDto> items,List<FnPlatWeekShareReportDto> list){
        if (list != null && list.size() > 0) {
            HSSFWorkbook  wb = (HSSFWorkbook) fnPlatWeekShareReportDto.getWb();
            CellStyle cellStyle  = getCellStyle(wb, 11,true);
            CellStyle cellStyle2  = getCellStyle(wb, 11,false);

            CellStyle cellStyle3  = getCellStyle(wb, 11,false);
            CellStyle cellStyle4  = getCellStyle(wb, 11,true);
            HSSFPalette customPalette = wb.getCustomPalette();
            customPalette.setColorAtIndex(HSSFColor.LIGHT_BLUE.index, (byte) 255, (byte) 242, (byte) 204);
            cellStyle3.setFillForegroundColor(HSSFColor.LIGHT_BLUE.index);

            customPalette.setColorAtIndex(HSSFColor.LIGHT_ORANGE.index, (byte) 255, (byte) 80, (byte) 80);
            cellStyle4.setFillForegroundColor(HSSFColor.LIGHT_ORANGE.index);

            cellStyle3.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            cellStyle4.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

            Row row = null;
            HSSFCell cell = null;
            int count = 1;
            BigDecimal workday = fnPlatWeekShareReportDto.getWorkdayStatus().getWorkday();//工作日
            int rowIndex = fnPlatWeekShareReportDto.getRowIndex();
            String totalWorkload = null;
            String ringRatio = null;
            for (FnPlatWeekShareReportDto dto : list) {//显示项目信息
                row = sheet.createRow(rowIndex++);

                cell = (HSSFCell) row.createCell(0);
                cell.setCellStyle(cellStyle2);
                cell.setCellValue(count);//序号

                cell = (HSSFCell) row.createCell(1);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(dto.getProjectName());//项目名称

                cell = (HSSFCell) row.createCell(2);
                cell.setCellStyle(cellStyle2);
                cell.setCellValue(getDayPre(workday,items, dto.getTotalWorkload()));//占比

                cell = (HSSFCell) row.createCell(3);
                totalWorkload = getConversionDay(workday,dto.getTotalWorkload());
                cell.setCellStyle(cellStyle);
                cell.setCellValue(totalWorkload);//总工作量

                cell = (HSSFCell) row.createCell(4);
                ringRatio = getDayPre(dto.getPreTotalWorkload(), dto.getTotalWorkload());
                if(isGreaterLimit(new BigDecimal(20), ringRatio)){
                    cell.setCellStyle(cellStyle4);
                }else{
                    cell.setCellStyle(cellStyle);
                }
                cell.setCellValue(ringRatio);//环比

                cell = (HSSFCell) row.createCell(5);
                cell.setCellStyle(cellStyle3);
                cell.setCellValue(getConversionDay(workday,dto.getArtsWorkload()));//美术总工作量

                cell = (HSSFCell) row.createCell(6);
                cell.setCellStyle(cellStyle3);
                cell.setCellValue(getConversionDay(workday,dto.getZhWorkload()));//珠海

                cell = (HSSFCell) row.createCell(7);
                cell.setCellStyle(cellStyle3);
                cell.setCellValue(getConversionDay(workday,dto.getGzWorkload()));//广州

                cell = (HSSFCell) row.createCell(8);
                cell.setCellStyle(cellStyle3);
                cell.setCellValue(getConversionDay(workday,dto.getWhWorkload()));//武汉

                cell = (HSSFCell) row.createCell(9);
                cell.setCellStyle(cellStyle3);
                cell.setCellValue(getConversionDay(workday,dto.getCdWorkload()));//成都

                cell = (HSSFCell) row.createCell(10);
                cell.setCellStyle(cellStyle3);
                cell.setCellValue(getConversionDay(workday,dto.getBjWorkload()));//北京

                cell = (HSSFCell) row.createCell(11);
                cell.setCellStyle(cellStyle2);
                cell.setCellValue(getConversionDay(workday,dto.getAudioWorkload()));//音频

                cell = (HSSFCell) row.createCell(12);
                cell.setCellStyle(cellStyle2);
                cell.setCellValue(getConversionDay(workday,dto.getTechnologyWorkload()));//技术

                cell = (HSSFCell) row.createCell(13);
                cell.setCellStyle(cellStyle2);
                cell.setCellValue(getConversionDay(workday,dto.getQualityWorkload()));//质量

                cell = (HSSFCell) row.createCell(14);
                cell.setCellStyle(cellStyle2);
                cell.setCellValue(getConversionDay(workday,dto.getTestWorkload()));//测试

                cell = (HSSFCell) row.createCell(15);
                cell.setCellStyle(cellStyle2);
                cell.setCellValue(getConversionDay(workday,dto.getWebWorkload()));//web

                cell = (HSSFCell) row.createCell(16);
                cell.setCellStyle(cellStyle2);
                cell.setCellValue(getConversionDay(workday,dto.getOperateWorkload()));//运营
                count++;
            }
            fnPlatWeekShareReportDto.setRowIndex(rowIndex);
        }
    }

    /**
     * 创建excel
     * @param wb
     * @param fos
     * @param url
     */
    private void createExcel( Workbook wb,FileOutputStream fos,String url){
        try {
            fos = new FileOutputStream((new File(url)));//生成文件
            wb.write(fos);
        } catch (Exception e) {
            logger.error("关闭流异常",e);
            e.printStackTrace();
        }finally{
            try {
                if(fos!=null){
                    fos.close();
                }
                if(wb!=null){
                    wb.close();
                }
            } catch (IOException e) {
                logger.error("关闭流异常",e);
                e.printStackTrace();
            }
        }
    }

    /**
     * 生成子页签
     * @param fnPlatWeekShareReportDto
     * @param platInfo
     * @throws Exception
     */
    private void createWeeklyForChild(FnPlatWeekShareReportDto fnPlatWeekShareReportDto,List<FnPlatWeekShareReporKeytDto> platInfo) throws Exception {
        int index = 1;
        FnWeekShareWorkdayStatus workdayStatus = fnPlatWeekShareReportDto.getWorkdayStatus();
        if (workdayStatus == null || workdayStatus.getWorkday() == null || new BigDecimal(0).compareTo(workdayStatus.getWorkday()) == 0) {
            throw new UserInvalidOperateException("本周未开启周分摊或者本周工作日为0，无法导出。");
        }
        Integer year = fnPlatWeekShareReportDto.getCurrentYear();
        Integer week = fnPlatWeekShareReportDto.getCurrentWeek();
        String workdayStr = MyDateUtils.getDateStrByFnWeekShareWorkdayStatus(fnPlatWeekShareReportDto.getWorkdayStatus());
        for(FnPlatWeekShareReporKeytDto dto : platInfo){
            List<Long> selectCond = new ArrayList<>(2);
            selectCond.add(dto.getPlatId());
            List<Long> projectIds = projectMapper.selectByIdsOrParentHrIds(selectCond).stream().map(x -> x.getId()).collect(Collectors.toList());
            if (!projectIds.isEmpty()) {

                List<Long> userIds = userMapper.selectActiveUserIdsByPlatIds(projectIds);
                Map<Long, String> remarkByUserIdMap = fnSumWeekShareConfigMapper.selectByYearAndWeekAndUserIds(year, week, userIds).stream().collect(Collectors.toMap(x -> x.getUserId(), y -> y.getRemark(), (x, y) -> y));
                List<FnPlatShareConfigUserDTO> configUserDTOS = fnPlatWeekShareConfigMapper.selectConfigUserDTOByPlatIdsAndYearAndMonth(projectIds, year, week);
                createPlatShareData(fnPlatWeekShareReportDto.getWb(),dto,dto.getPlatId(), configUserDTOS, year + "年" + week + "周人力统计.xls", "本周工作日:", remarkByUserIdMap,workdayStatus.getWorkday(),workdayStr);
                index++;
            }
        }
    }

    /**
     * 创建子页签数据
     * @param fnPlatWeekShareReportDto
     */
    private void createChildSheet(FnPlatWeekShareReportDto fnPlatWeekShareReportDto){
        List<FnPlatWeekShareReporKeytDto> platInfo = new ArrayList<FnPlatWeekShareReporKeytDto>();
        FnPlatWeekShareReporKeytDto fnPlatWeekShareReporKeytDto = null;

        fnPlatWeekShareReporKeytDto = new FnPlatWeekShareReporKeytDto(1, 1081l, "珠海美术");
        platInfo.add(fnPlatWeekShareReporKeytDto);
        fnPlatWeekShareReporKeytDto = new FnPlatWeekShareReporKeytDto(2, 1188l, "广州美术");
        platInfo.add(fnPlatWeekShareReporKeytDto);
        fnPlatWeekShareReporKeytDto = new FnPlatWeekShareReporKeytDto(3, 1260l, "武汉美术");
        platInfo.add(fnPlatWeekShareReporKeytDto);
        fnPlatWeekShareReporKeytDto = new FnPlatWeekShareReporKeytDto(4, 1147l, "成都美术");
        platInfo.add(fnPlatWeekShareReporKeytDto);
        fnPlatWeekShareReporKeytDto = new FnPlatWeekShareReporKeytDto(5, 1225l, "北京美术");
        platInfo.add(fnPlatWeekShareReporKeytDto);
        fnPlatWeekShareReporKeytDto = new FnPlatWeekShareReporKeytDto(6, 1248l, "珠海音频");
        platInfo.add(fnPlatWeekShareReporKeytDto);
        fnPlatWeekShareReporKeytDto = new FnPlatWeekShareReporKeytDto(7, 5045l, "珠海技术");
        platInfo.add(fnPlatWeekShareReporKeytDto);
        fnPlatWeekShareReporKeytDto = new FnPlatWeekShareReporKeytDto(8, 1086l, "珠海质量");
        platInfo.add(fnPlatWeekShareReporKeytDto);
        fnPlatWeekShareReporKeytDto = new FnPlatWeekShareReporKeytDto(9, 1066l, "测试中心");
        platInfo.add(fnPlatWeekShareReporKeytDto);
        fnPlatWeekShareReporKeytDto = new FnPlatWeekShareReporKeytDto(10, 1244l, "珠海web");
        platInfo.add(fnPlatWeekShareReporKeytDto);
        fnPlatWeekShareReporKeytDto = new FnPlatWeekShareReporKeytDto(11, 1080l, "珠海运营");
        platInfo.add(fnPlatWeekShareReporKeytDto);
        fnPlatWeekShareReporKeytDto = new FnPlatWeekShareReporKeytDto(12, 1266l, "成都运营");
        platInfo.add(fnPlatWeekShareReporKeytDto);

        try {
            createWeeklyForChild(fnPlatWeekShareReportDto,platInfo);//创建子页签
        } catch (Exception e) {
            logger.error("生成子页签失败",e);
            e.printStackTrace();
        }
    }

    /**
     * 判断值是否大于上限值
     * @param limit
     * @param value
     * @return
     */
    private boolean isGreaterLimit(BigDecimal limit,String value){
        if(!StringUtils.isEmpty(value) && !"——".equalsIgnoreCase(value)){
            value = value.replace("%","");
            BigDecimal dd = new BigDecimal(value);
            if(limit.compareTo(dd)<=0){
                return true;
            }
        }
        return false;
    }
    /**
     * 判断值是否大于上限值
     * @param limit
     * @param value
     * @return
     */
    private boolean isGreaterLimit(BigDecimal limit,BigDecimal value){
        if(limit != null && value!=null){
            if(limit.compareTo(value)<0){
                return true;
            }
        }
        return false;
    }

    /**
     * 获取折算后的工作日
     * @param workDay
     * @param value
     * @return
     */
    private String getConversionDay(BigDecimal workDay ,BigDecimal value){
        String result ="";
        if(workDay != null && BigDecimal.ZERO.compareTo(workDay) != 0 && value != null){
            BigDecimal  total = null;
            total = value.divide(workDay, 4, BigDecimal.ROUND_HALF_EVEN);
            DecimalFormat df = new DecimalFormat("0.0");
            result = df.format(total);
            return result;
        }
        return result;
    }
    /**
     * 获取折算后的工作日
     * @param workDay
     * @param value
     * @return
     */
    private BigDecimal conversionDay(BigDecimal workDay ,BigDecimal value){
        BigDecimal  total = null;
        if(workDay != null && BigDecimal.ZERO.compareTo(workDay) != 0 && value != null){
            total = value.divide(workDay, 1, BigDecimal.ROUND_HALF_EVEN);
            return total;
        }
        return total;
    }
    /**
     *周报表导出,周有跨月跨年的情况
     * @param previousYear 上周是哪年
     * @param previousWeek 上周
     * @param currentYear 当前周是哪年
     * @param currentWeek 当前周
     * @return 报表路径
     */
    public String exportWeekly(Integer previousYear,Integer previousWeek,Integer currentYear,Integer currentWeek) {
        FnWeekShareWorkdayStatus workdayStatus = fnWeekShareWorkdayStatusMapper.selectByYearAndWeek(currentYear, currentWeek);
        if (workdayStatus == null || workdayStatus.getWorkday() == null || new BigDecimal(0).compareTo(workdayStatus.getWorkday()) == 0) {
            throw new UserInvalidOperateException("本周未开启周分摊或者本周工作日为0，无法导出。");
        }
        FnPlatWeekShareReportDto fnPlatWeekShareReportDto = new FnPlatWeekShareReportDto();
        fnPlatWeekShareReportDto.setPreviousYear(previousYear);
        fnPlatWeekShareReportDto.setPreviousWeek(previousWeek);
        fnPlatWeekShareReportDto.setCurrentYear(currentYear);
        fnPlatWeekShareReportDto.setCurrentWeek(currentWeek);
        fnPlatWeekShareReportDto.setDate(MyDateUtils.dateToString(new Date(), "MM-dd"));
        fnPlatWeekShareReportDto.setWorkdayStatus(workdayStatus);//设置工作日配置信息
        setParameters(fnPlatWeekShareReportDto);//设置查询参数
        List<FnPlatWeekShareReportDto> list = fnPlatShareConfigMapper.selectWeekly(fnPlatWeekShareReportDto);//获取周报数据
        Workbook wb = new HSSFWorkbook();
        fnPlatWeekShareReportDto.setWb(wb);
        FileOutputStream fos = null;
        Sheet sheet = wb.createSheet();
        wb.setSheetName(0, currentWeek + "周汇总");
        createHead(fnPlatWeekShareReportDto,sheet);//创建头部
        int rowIndex = 2;
        fnPlatWeekShareReportDto.setRowIndex(rowIndex);
        List<FnPlatWeekShareReportDto> items = new ArrayList<FnPlatWeekShareReportDto>();
        summaryUser(fnPlatWeekShareReportDto,items, list);//设置底部第一行合并信息
        createRowData(fnPlatWeekShareReportDto,sheet,items,list);//创建列表数据行
        createButtom(fnPlatWeekShareReportDto,sheet,items);//创建底部合计信息
        createChildSheet(fnPlatWeekShareReportDto);//创建隔平台子页签
        String filePatch = exportExcelPath + "/平台人力分布统计周报-"+currentYear+"年第"+currentWeek+"周.xls";
        String url = filePathPrefix + filePatch;
        createExcel(wb,fos, url);//创建excel
        return filePatch;
    }
}
