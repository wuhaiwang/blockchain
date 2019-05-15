package com.seasun.management.service.oldShare;

import com.alibaba.fastjson.JSONObject;
import com.seasun.management.constant.ErrorCode;
import com.seasun.management.constant.ErrorMessage;
import com.seasun.management.constant.UserShareType;
import com.seasun.management.dto.FnPlatShareConfigUserDTO;
import com.seasun.management.dto.UserEmployeeNoDto;
import com.seasun.management.dto.UserFnShareDataDto;
import com.seasun.management.exception.CopyShareConfigDataException;
import com.seasun.management.exception.DataImportException;
import com.seasun.management.exception.ParamException;
import com.seasun.management.exception.UserInvalidOperateException;
import com.seasun.management.helper.ExcelHelper;
import com.seasun.management.helper.PermissionHelper;
import com.seasun.management.helper.ReportHelper;
import com.seasun.management.mapper.*;
import com.seasun.management.model.*;
import com.seasun.management.service.FnPlatShareConfigService;
import com.seasun.management.util.*;
import com.seasun.management.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service(value = "oldShareFnPlatShareConfigService")
public class OldShareFnPlatShareConfigServiceImpl implements FnPlatShareConfigService {

    private static final Logger logger = LoggerFactory.getLogger(OldShareFnPlatShareConfigServiceImpl.class);

    @Value("${export.excel.path}")
    private String exportExcelPath;

    @Value("${file.sys.prefix}")
    private String filePathPrefix;

    @Value("${backup.excel.url}")
    private String backupExcelUrl;

    @Autowired
    FnPlatShareConfigMapper fnPlatShareConfigMapper;

    @Autowired
    FnPlatShareMemberMapper fnPlatShareMemberMapper;

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

    @Resource(name = "fnPlatShareConfigService")
    private FnPlatShareConfigService fnPlatShareConfigService;

    @Override
    @Deprecated
    public boolean getPlatLockFlag(int year, int month, long platId) {
        return false;
    }

    @Override
    public FnPlatShareConfigLockVo getUserShareConfigData(String userType, Long platId, int year, int month) {
        Long userId = MyTokenUtils.getCurrentUserId();
        FnPlatShareConfigLockVo fnPlatShareConfigLockVo = new FnPlatShareConfigLockVo();
        List<FnPlatShareConfigVo> fnPlatShareConfigs;

        boolean lockFlag = fnPlatShareConfigService.getPlatLockFlag(year, month, platId);
        fnPlatShareConfigLockVo.setLockFlag(lockFlag);

        // 若userType为成员：展示现有数据
        if (UserShareType.member.toString().equals(userType)) {
            fnPlatShareConfigs = fnPlatShareConfigMapper.selectMemberDataByCond(platId, year, month, userId);
        }
        // 若userType为平台负责人：展现汇总数据
        else if (UserShareType.manager.toString().equals(userType)) {
            // 此处会判断是否存在调整后的汇总分摊比例,若有,则会填写sumSharePro字段，
            if (lockFlag) {
                // 若已锁定，则去掉为0的fnSumSharePro
                fnPlatShareConfigs = fnPlatShareConfigMapper.selectSumDataByCondWithoutZeroFnSumSharePro(platId, year, month);
            } else {
                fnPlatShareConfigs = fnPlatShareConfigMapper.selectSumDataByCond(platId, year, month);
            }

        } else {
            throw new ParamException("userType不合法， 可选值为[manager,member]");
        }

        List<FnSumShareConfig> sumShareConfigs = fnSumShareConfigMapper.selectSumShareConfigByPlatIdAndYearAndMonth(platId, year, month);
        addEmptyDataForMissedProjects(platId, fnPlatShareConfigs, sumShareConfigs);
        fnPlatShareConfigLockVo.setPlatProcessList(fnPlatShareConfigs);


        return fnPlatShareConfigLockVo;
    }

    @Override
    public List<FnPlatSumProVo> getPlatSumConfigList(Integer year, Integer month) {
        List<FnPlatSumProVo> fnPlatSumProVoList = new ArrayList<>();
        long userId = MyTokenUtils.getCurrentUserId();
        List<RUserProjectPermVo> rUserProjectPermVos = rUserProjectPermMapper.selectByUserIdAndOrderByProjectRoleIdAsc(userId);
        boolean adminFlag = false;
        if (null != rUserProjectPermVos && rUserProjectPermVos.size() > 0) {
            for (RUserProjectPermVo rUserProjectPermVo : rUserProjectPermVos) {
                if (PermissionHelper.SystemRole.BACKEND_MANAGEMENT_ID.equals(rUserProjectPermVo.getProjectRoleId()) || PermissionHelper.SystemRole.BACKEND_FINANCE_DATA_ID.equals(rUserProjectPermVo.getProjectRoleId())) {
                    adminFlag = true;
                    break;
                }
            }
        }
        if (adminFlag) fnPlatSumProVoList = fnPlatShareConfigMapper.getPlatSumConfigList(year, month);
        return fnPlatSumProVoList;
    }

    @Override
    public void batchUpdateLockFlag(Long[] plats, Integer year, Integer month, String type) {
        boolean lockFlag = "lock".equals(type);
        fnSumShareConfigMapper.batchUpdateLockStatus(Arrays.asList(plats), year, month, lockFlag);
    }

    @Override
    public List<FnPlatShareConfigVo> getMemberShareConfigData(String userType, Long platId, int year, int month, Long memberId) {

        List<FnPlatShareConfigVo> fnPlatShareConfigs = null;
        if (UserShareType.manager.toString().equals(userType)) {
            fnPlatShareConfigs = fnPlatShareConfigMapper.selectMemberDataByCond(platId, year, month, memberId);
            List<ProjectVo> shareProjects = projectMapper.selectAllShareProject();
            // 过滤 平台-公司 项目
            shareProjects = shareProjects.stream().filter(x -> !Project.Id.PLAT_COMPANY.equals(x.getId())).collect(Collectors.toList());
            for (ProjectVo projectVo : shareProjects) {
                boolean isContainsInProject = false;
                for (FnPlatShareConfigVo fnPlatShareConfigVo : fnPlatShareConfigs) {
                    if (fnPlatShareConfigVo.getProjectId().equals(projectVo.getId())) {
                        isContainsInProject = true;
                        break;
                    }
                }

                // 若存在未填写的项目,补充空数据
                if (!isContainsInProject) {
                    FnPlatShareConfigVo emptyOne = new FnPlatShareConfigVo();
                    emptyOne.setProjectId(projectVo.getId());
                    emptyOne.setPlatId(platId);
                    emptyOne.setProjectName(projectVo.getName());
                    emptyOne.setProjectUsedNames(projectVo.getUsedNamesStr());
                    emptyOne.setCity(projectVo.getCity());
                    fnPlatShareConfigs.add(emptyOne);
                }
            }

        }
        return fnPlatShareConfigs;
    }

    @Override
    public List<FnPlatShareConfigVo> getDetailData(Long platId, Long projectId, int year, int month) {
        List<FnPlatShareConfigVo> fnPlatShareConfigs = fnPlatShareConfigMapper.selectDetailDataByCond(platId, projectId, year, month);
        return fnPlatShareConfigs;
    }

    @Override
    public boolean insert(FnPlatShareConfigVo fnPlatShareConfigVo) {

        // 负责人代替填写时,会传入代替填写的userId
        if (fnPlatShareConfigVo.getUserId() != null) {
            fnPlatShareConfigVo.setCreateBy(fnPlatShareConfigVo.getUserId());
        } else {
            fnPlatShareConfigVo.setCreateBy(MyTokenUtils.getCurrentUserId());
        }


        FnPlatShareConfig fnPlatShareConfig = fnPlatShareConfigMapper.selectByCond(fnPlatShareConfigVo);
        if (null != fnPlatShareConfig) {
            throw new ParamException("platId and projectId and createBy and year and month is duplicate");
        } else {
            fnPlatShareConfigVo.setCreateTime(new Date());
            fnPlatShareConfigMapper.insertSelective(fnPlatShareConfigVo);
        }
        return true;
    }

    @Override
    public JSONObject update(FnPlatShareConfigVo fnPlatShareConfigVo) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", 0);
        jsonObject.put("message", "success");

        if (null == fnPlatShareConfigVo.getId()) {
            jsonObject.put("code", -1);
            jsonObject.put("message", "id can not be null or empty");
            throw new ParamException("id can not be null or empty...");
        }

        // 只更新remark
        if (fnPlatShareConfigVo.getRemark() != null) {
            fnPlatShareConfigVo.setSharePro(null);
            fnPlatShareConfigVo.setFixedNumber(null);
            fnPlatShareConfigMapper.updateByPrimaryKeySelective(fnPlatShareConfigVo);
            return jsonObject;
        }

        if (fnPlatShareConfigVo.getSharePro() != null && fnPlatShareConfigVo.getSharePro().floatValue() == 0
                && fnPlatShareConfigVo.getFixedNumber() != null && fnPlatShareConfigVo.getFixedNumber().floatValue() == 0F) {
            fnPlatShareConfigMapper.deleteByPrimaryKey(fnPlatShareConfigVo.getId());
        } else {
            // 增加后台校验: 不允许私人分摊百分比>100%.
            Float sumSharePro = 0F;

            // todo: xionghaitao 这里不是bug，在汇总分摊里修改分摊比例是做的验证，这里传回来的createBy不为null
            if (null != fnPlatShareConfigVo.getCreateBy()) {
                sumSharePro = fnPlatShareConfigMapper.selectOtherSumShareProByRecord(fnPlatShareConfigVo);
            }

            if (sumSharePro + fnPlatShareConfigVo.getSharePro().floatValue() > 1) {
                jsonObject.put("code", -2);
                jsonObject.put("message", "the employee total share_pro more than 100%");
                jsonObject.put("maxSharePro", ReportHelper.formatNumberByScale(1 - sumSharePro, 4));
            }

            fnPlatShareConfigVo.setUpdateTime(new Date());
            fnPlatShareConfigMapper.updateByPrimaryKeySelective(fnPlatShareConfigVo);
        }
        return jsonObject;
    }


    private JSONObject exportSumDataToExcel(MultipartFile file, int year, int month) {
        List<String> errorList = new ArrayList<>();
        String exportErrorFilePath = null;
        String exportFilePath = exportExcelPath + File.separator + year + "_" + month + "_各平台分摊数据.xlsx";
        String exportFileDiskPath = filePathPrefix + exportFilePath;
        int[] formulaSkippedColumns = {0, 2, 3};
        int[] formulaSkippedRows = {0, 1, 2, 3, 4, 5};
        JSONObject result = new JSONObject();

        // 0.是否带公式
        boolean formulaFlag = file.getOriginalFilename().contains("公式");

        // 1. 读取备份文件
        Workbook wb = null;
        try {
            wb = WorkbookFactory.create(file.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 2. 解析上传的分摊模板,回填数据
        if (null == wb) {
            throw new RuntimeException("无法读取到表格");
        }

        try {
            Sheet sheet = wb.getSheetAt(0);
            List<ProjectVo> shareProjects = projectMapper.selectAllShareProject();
            Iterator<ProjectVo> shareProjectsIterator = shareProjects.iterator();
            //List<ProjectVo> sharePlats = projectMapper.selectAllSharePlat();
            List<ProjectVo> sharePlats = projectMapper.selectCfgAllSharePlat();
            Iterator<ProjectVo> sharePlatsIterator = sharePlats.iterator();
            // 2.1 为首行匹配项目Id
            Map<Long, Long> columnMap = new HashMap<>();
            Row header = sheet.getRow(0);
            //带公式时,从第二行开始
            if (formulaFlag) {
                header = sheet.getRow(1);
            }

            for (int i = 0; i < header.getLastCellNum(); i++) {

                // 带公式时,忽略特殊列
                if (formulaFlag && MyCellUtils.isContainsIntValue(formulaSkippedColumns, i)) {
                    continue;
                }

                Cell cell = header.getCell(i);
                if (cell == null) {
                    continue;
                }

                String projectName = cell.getStringCellValue();
                Long columnId = Long.valueOf(cell.getColumnIndex());
                columnMap.put(columnId, 0L);// 默认填写0
                if (!StringUtils.isEmpty(projectName)) {
                    String pureProjectName = ExcelHelper.trimSpaceAndSpecialSymbol(projectName);
                    while (shareProjectsIterator.hasNext()) {
                        ProjectVo item = shareProjectsIterator.next();
                        List<String> nameList = ExcelHelper.buildProjectUsedNames(item);
                        if (nameList.contains(pureProjectName)) {
                            columnMap.put(columnId, item.getId());
                            shareProjectsIterator.remove();
                            break;
                        }
                    }
                    shareProjectsIterator = shareProjects.iterator();
                }
                if (columnMap.get(columnId).equals(0L)) {
                    errorList.add("第" + (header.getRowNum() + 1) + "行,第" + (i + 1) + "列,项目名称:" + header.getCell(i).getStringCellValue() + " 无法在系统中匹配，请检查。");
                    // 高亮不匹配的列
                    CellStyle style = wb.createCellStyle();
                    style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
                    style.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
                    Cell columnHeader = header.getCell(cell.getColumnIndex());
                    if (null != columnHeader) {
                        columnHeader.setCellStyle(style);
                    }
                }
            }
            while (shareProjectsIterator.hasNext()) {
                errorList.add("分摊项目:" + shareProjectsIterator.next().getName() + " 在模板中未匹配到，请检查。");
            }

            // 2.2 为首列匹配platId
            Map<Long, Long> rowMap = new HashMap<>();
            for (int i = 0; i <= sheet.getLastRowNum(); i++) {

                // 带公式时,忽略特殊行
                if (formulaFlag && MyCellUtils.isContainsIntValue(formulaSkippedRows, i)) {
                    continue;
                }

                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }


                Cell cell = row.getCell(0);
                if (formulaFlag) { // 带公式时,从第二列开始
                    cell = row.getCell(1);
                }

                if (cell == null) {
                    continue;
                }

                // 带公式时,忽略"实习生"行
                if (formulaFlag && cell.getStringCellValue().contains("实习生")) {
                    continue;
                }

                String platName = cell.getStringCellValue();
                Long rowId = Long.valueOf(cell.getRowIndex());
                rowMap.put(rowId, 0L);// 默认填写0
                if (!StringUtils.isEmpty(platName)) {
                    String purePlatName = ExcelHelper.trimSpaceAndSpecialSymbol(platName);
                    while (sharePlatsIterator.hasNext()) {
                        ProjectVo item = sharePlatsIterator.next();
                        List<String> nameList = ExcelHelper.buildProjectUsedNames(item);
                        if (nameList.contains(purePlatName)) {
                            rowMap.put(rowId, item.getId());
                            sharePlatsIterator.remove();
                            break;
                        }
                    }
                    sharePlatsIterator = sharePlats.iterator();
                }

                if (rowMap.get(rowId).equals(0L)) {
                    errorList.add("第" + (i + 1) + "行，第" + (cell.getColumnIndex() + 1) + "列,平台名称:" + cell.getStringCellValue() + " 无法在系统中匹配，请检查。");
                    CellStyle style = wb.createCellStyle();
                    style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND); // 高亮不匹配的行
                    style.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
                    Cell rowHeader = row.getCell(0);
                    if (null != rowHeader) {
                        rowHeader.setCellStyle(style);
                    }
                }
            }
            while (sharePlatsIterator.hasNext()) {
                errorList.add("分摊平台:" + sharePlatsIterator.next().getName() + " 在模板中未匹配到，请检查。");
            }
            // 2.3 填充所有cell.
            List<FnSumShareConfig> fnSumShareConfigs = fnSumShareConfigMapper.selectSumShareConfigByYearAndMonth(year, month);// 直接读取fn_sum_share_config的汇总数据.

            for (int h = 1; h <= sheet.getLastRowNum(); h++) {
                Row row = sheet.getRow(h);
                if (row == null || !rowMap.containsKey(Long.valueOf(row.getRowNum()))) {
                    continue;
                }

                for (int j = 0; j < row.getLastCellNum(); j++) {
                    Cell myCell = row.getCell(j);
                    if (myCell == null || !columnMap.containsKey(Long.valueOf(myCell.getColumnIndex()))) {
                        continue;
                    }
                    Long rowIndex = Long.valueOf(myCell.getRowIndex());
                    Long columnIndex = Long.valueOf(myCell.getColumnIndex());


                    // 找到projectId和platId，则匹配上
                    if (!rowMap.get(rowIndex).equals(0L)
                            && !columnMap.get(columnIndex).equals(0L)) {
                        for (FnSumShareConfig config : fnSumShareConfigs) {
                            if (config.getPlatId().equals(rowMap.get(rowIndex)) &&
                                    config.getProjectId().equals(columnMap.get(columnIndex))) {

                                if (config.getSharePro() == null || config.getSharePro().floatValue() == 0) {
                                    continue; // 值不存在,或为0,无需处理.
                                }

                                // important: can only be doubleValue,other type will cause infinite number problem
                                myCell.setCellValue(config.getSharePro().setScale(5, BigDecimal.ROUND_HALF_UP).doubleValue());
                                myCell.setCellType(Cell.CELL_TYPE_NUMERIC);

                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.info("exportSumDataToExcelException : because excelData error");
            return null;
        }

        // 3. 保存文件到export目录
        FileOutputStream fileOutputStream = null;
        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            wb.setForceFormulaRecalculation(true);
            fileOutputStream = new FileOutputStream(exportFileDiskPath);
            wb.write(fileOutputStream);

            //保存错误信息
            if (!errorList.isEmpty()) {
                exportErrorFilePath = exportExcelPath + File.separator + year + "_" + month + "_平台分摊模板导入错误信息.txt";
                File exportErrorFile = new File(filePathPrefix + exportErrorFilePath);
                if (exportErrorFile.exists()) {
                    exportErrorFile.delete();
                }
                exportErrorFile.createNewFile();
                fw = new FileWriter(exportErrorFile);
                bw = new BufferedWriter(fw);
                for (String error : errorList) {
                    bw.write(error + "\r\n");
                }
            }
        } catch (IOException e) {
            logger.info("exportFileException");
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                    bw.flush();
                    bw.close();
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        result.put("fileUrl", exportFilePath);
        result.put("errorUrl", exportErrorFilePath);
        return result;
    }

    @Override
    public JSONObject importShareTemplate(MultipartFile file, int year, int month) {
        try {
            String path = ExcelHelper.saveExcelBackup(file, filePathPrefix + backupExcelUrl);
            FnShareTemplate fnShareTemplate = fnShareTemplateMapper.selectTemplateByYearAndMonth(year, month);
            // 插入并保存模板
            if (null != fnShareTemplate) {
                fnShareTemplate.setLocation(path);
                fnShareTemplateMapper.updateByPrimaryKeySelective(fnShareTemplate);
            } else {
                FnShareTemplate newTemplate = new FnShareTemplate();
                newTemplate.setYear(year);
                newTemplate.setMonth(month);
                newTemplate.setLocation(path);
                newTemplate.setCreateTime(new Date());
                fnShareTemplateMapper.insertSelective(newTemplate);
            }

            // 导出表格
            return exportSumDataToExcel(file, year, month);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

                // 为所有项目列匹配id
                if ("平台-公司".equals(cellText) && ReportHelper.PlatInShareProjectMap.get(shareProjectMapKey) != null) {
                    mapColumnProject.put(colIndexEnd, ReportHelper.PlatInShareProjectMap.get(shareProjectMapKey));
                } else {
                    ProjectVo project = projectList.stream().filter(item -> {
                        List<String> projectUsedNames = ExcelHelper.buildProjectUsedNames(item);
                        return projectUsedNames.contains(ExcelHelper.trimSpaceAndSpecialSymbol(cellText));
                    }).findFirst().orElse(null);
                    if (null == project) {
                        errorLogs.add("项目名不规范，找不到此项目 ProjectName:" + cellText);
                    } else {
                        mapColumnProject.put(colIndexEnd, project.getId());
                    }
                }


                colIndexEnd++;
            }


            // 开始读取每个人的分摊数据
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

                Cell cellEmployeeNo = rowUserShareData.getCell(0);
                if (cellEmployeeNo == null || cellEmployeeNo.toString().isEmpty() || cellEmployeeNo.toString().trim().isEmpty()) {
                    // 以工号为空做为结束行
                    break;
                }
                Double cellValueEmployeeNo = cellEmployeeNo.getNumericCellValue();
                Cell cellUserName = rowUserShareData.getCell(1);
                String cellTextUserName = cellUserName.toString();
                if (null == cellValueEmployeeNo || cellValueEmployeeNo.intValue() == 0 || StringUtils.isEmpty(cellTextUserName)) {
                    errorLogs.add("row: " + rowUserShareData.getRowNum() + " 员工编号不存在 EmployeeNo:" + cellEmployeeNo.toString());
                    continue;
                }

                UserEmployeeNoDto userInfo = userList.stream().filter(item -> item.getEmployeeNo().equals(cellValueEmployeeNo.longValue())).findFirst().orElse(null);
                if (null == userInfo) {
                    errorLogs.add("员工编号不存在 EmployeeNo:" + cellEmployeeNo.toString());
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
                List<FnPlatShareMember> fnPlatShareMemberList = new ArrayList<>();
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

                    //生成FnPlatShareMember
                    FnPlatShareMember fnPlatShareMember = new FnPlatShareMember();
                    fnPlatShareMember.setPlatId(platId);
                    fnPlatShareMember.setUserId(userFnShareDataDto.getUserId());
                    fnPlatShareMember.setWeight(BigDecimal.ONE);
                    fnPlatShareMemberList.add(fnPlatShareMember);
                }

                //先删掉再插
                if (fnPlatShareConfigList.size() > 0) {
                    // 删个人
                    fnPlatShareConfigMapper.deleteByPlatIdAndYearAndMonth(platId, year, month);
                    // 删汇总
                    fnSumShareConfigMapper.deleteByPlatIdAndYearAndMonth(platId, year, month);

                    fnPlatShareConfigMapper.batchInsert(fnPlatShareConfigList);
                }

                //先删掉再插
                if (fnPlatShareMemberList.size() > 0) {
                    fnPlatShareMemberMapper.deleteByPlatId(platId);
                    fnPlatShareMemberMapper.batchInsert(fnPlatShareMemberList);
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

            // 保存错误日志
            String fileName = "import-error-" + platId + "-" + year + "-" + month;
            ExcelHelper.saveErrorFile(errorLogs, "share-import/", fileName);
            if (errorLogs.size() > 0) {
                String downloadURL = backupExcelUrl + "/" + fileName + ".txt";
                MyFileUtils.copyFile(ExcelHelper.ERR_LOG_DIR + "share-import/" + fileName + ".log", filePathPrefix + downloadURL);
                return downloadURL;
            }
        } catch (Exception e) {
            return e.getMessage();
        }
        return "";
    }

    public FnPlatShareMemberGroupByPlatVo getFnShareMemberOfProject(long projectId) {
        FnShareTemplate fnShareTemplate = fnShareTemplateMapper.selectTemplateLast();
        if (null != fnShareTemplate) {
            FnPlatShareMemberGroupByPlatVo fnPlatShareMemberGroupByPlat = new FnPlatShareMemberGroupByPlatVo();
            fnPlatShareMemberGroupByPlat.setFnPlatShareMemberCountOfPlatVoList(new ArrayList<>());

            List<FnPlatShareMemberVo> fnPlatShareMemberVoList = fnPlatShareConfigMapper.selectMemberByProjectIdAndYearAndMonth(projectId, fnShareTemplate.getYear(), fnShareTemplate.getMonth());
            fnPlatShareMemberGroupByPlat.setFnPlatShareMemberVoList(fnPlatShareMemberVoList);
            //按平台分组
            for (FnPlatShareMemberVo fnPlatShareMemberVo : fnPlatShareMemberVoList) {
                FnPlatShareMemberCountOfPlatVo fnPlatShareMemberCountOfPlatVo = null;
                if (null == (fnPlatShareMemberCountOfPlatVo = fnPlatShareMemberGroupByPlat.getFnPlatShareMemberCountOfPlatVoList().stream().filter(item -> item.getPlatId().equals(fnPlatShareMemberVo.getPlatId())).findFirst().orElse(null))) {
                    fnPlatShareMemberCountOfPlatVo = new FnPlatShareMemberCountOfPlatVo();
                    fnPlatShareMemberCountOfPlatVo.setPlatId(fnPlatShareMemberVo.getPlatId());
                    fnPlatShareMemberCountOfPlatVo.setPlatName(fnPlatShareMemberVo.getPlatName());
                    fnPlatShareMemberCountOfPlatVo.setShareMemberCount(1);

                    fnPlatShareMemberGroupByPlat.getFnPlatShareMemberCountOfPlatVoList().add(fnPlatShareMemberCountOfPlatVo);
                } else {
                    fnPlatShareMemberCountOfPlatVo.setShareMemberCount(fnPlatShareMemberCountOfPlatVo.getShareMemberCount() + 1);
                }
            }

            return fnPlatShareMemberGroupByPlat;
        } else {
            return null;
        }
    }


    @Override
    public FnShareTemplate getShareTemplate(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, 3);
        cal.add(Calendar.MONTH, -1);
        return fnShareTemplateMapper.selectTemplateByYearAndMonth(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH));
    }

    @Override
    public void copyFromLastMonth(Long platId, int year, int month) {

        Long userId = MyTokenUtils.getCurrentUserId();
        int lastMonth = month == 1 ? 12 : month - 1;
        int lastYear = month == 1 ? year - 1 : year;
        List<FnPlatShareConfig> lastMonthData = fnPlatShareConfigMapper.selectByPlatIdAndYearAndMonthAndCreateBy(platId, lastYear, lastMonth, userId);
        if (lastMonthData.size() == 0) {
            throw new CopyShareConfigDataException("拷贝失败:没有找到上月的分摊数据");
        }

        // 将lastMonthData更改为本月数据
        for (FnPlatShareConfig copyItem : lastMonthData) {
            copyItem.setYear(year);
            copyItem.setMonth(month);
            copyItem.setUpdateTime(new Date());
        }

        // 删除此用户本月已经填写过的数据
        fnPlatShareConfigMapper.deleteForDateCopy(userId, platId, year, month);

        // 插入克隆后的数据
        fnPlatShareConfigMapper.batchInsert(lastMonthData);

    }


    @Override
    public void startNewMonthShareConfig(int year, int month) {

        int lastMonth = month == 1 ? 12 : month - 1;
        int lastYear = month == 1 ? year - 1 : year;

        // 结束上月分摊
        FnShareInfo lastFnShareInfo = fnShareInfoMapper.selectByYearAndMonth(lastYear, lastMonth);
        if (null != lastFnShareInfo) {
            lastFnShareInfo.setStatus(FnShareInfo.Status.finished);
            lastFnShareInfo.setUpdateTime(new Date());
            fnShareInfoMapper.updateByPrimaryKeySelective(lastFnShareInfo);
        }

        // 锁定上月分摊,如果没有数据,则跳过.
        List<FnSumShareConfig> records = fnSumShareConfigMapper.selectSumShareConfigByYearAndMonth(lastYear, lastMonth);
        if (records != null && records.size() > 0) {
            fnSumShareConfigMapper.lockByYearAndMonth(lastYear, lastMonth);
        } else {
            logger.info("no sum record found,will skip lock last month...");
        }

        // 开始上月分摊
        FnShareInfo fnShareInfo = new FnShareInfo();
        fnShareInfo.setMonth(month);
        fnShareInfo.setYear(year);
        fnShareInfo.setStatus(FnShareInfo.Status.processing);
        fnShareInfo.setCreateTime(new Date());
        fnShareInfoMapper.insertSelective(fnShareInfo);
    }

    @Override
    public FnShareInfo getLatestFnShareInfo() {
        FnShareInfo latestRecord = fnShareInfoMapper.selectLatestProcessingRecord();
        if (latestRecord == null) {
            latestRecord = new FnShareInfo();
            Date date = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            latestRecord.setYear(year);
            latestRecord.setMonth(month);
            latestRecord.setStatus(FnShareInfo.Status.processing);
        }
        return latestRecord;
    }

    @Override
    @Transactional
    public void saveBatchShareConfig(FnPlatShareBatchVo fnPlatShareBatchVo) {
        Long userId = MyTokenUtils.getCurrentUserId();
        List<FnPlatShareConfig> fnPlatShareConfigs = new ArrayList<>();
        for (FnPlatShareBatchVo.ProjectShareInfo temp : fnPlatShareBatchVo.getValues()) {
            FnPlatShareConfig config = new FnPlatShareConfig();
            config.setPlatId(fnPlatShareBatchVo.getPlatId());
            config.setYear(fnPlatShareBatchVo.getYear());
            config.setMonth(fnPlatShareBatchVo.getMonth());
            config.setCreateBy(userId);
            config.setProjectId(temp.getProjectId());
            config.setSharePro(temp.getSharePro());
            config.setCreateTime(new Date());
            fnPlatShareConfigs.add(config);
        }
        fnPlatShareConfigMapper.deleteByCreateBy(userId, fnPlatShareBatchVo.getYear(), fnPlatShareBatchVo.getMonth());
        fnPlatShareConfigMapper.batchInsert(fnPlatShareConfigs);
    }

    @Override
    public String exportPlatMonthShareData(Long platId, Integer year, Integer month) throws Exception {
        List<FnPlatShareConfigUserDTO> fnPlatShareConfigs = fnPlatShareConfigMapper.selectConfigUserDTOByPlatIdAndYearAndMonth(platId, year, month);
        if (fnPlatShareConfigs.isEmpty()) {
            throw new UserInvalidOperateException("该平台本月没有分摊成员填写数据，请先填写成员分摊。");
        }

        // 查看每月工作日是否配置
        FnPlatShareMonthInfo fnPlatShareMonthInfo = fnPlatShareMonthInfoMapper.selectWorkDayByPlatIdAndYearAndMonth(platId, year, month);
        if (fnPlatShareMonthInfo == null) {
            fnPlatShareMonthInfo = new FnPlatShareMonthInfo();
        }

        boolean shareAmountFlag = true;
        String fileName = year + "年" + month + "月份人力统计.xls";
        List<Project> projects = projectMapper.selectAll();
        Map<Long, Project> projectByIdMap = projects.stream().collect(Collectors.toMap(x -> x.getId(), x -> x));
        String resultFilePath = exportExcelPath + File.separator + projectByIdMap.get(platId).getName() + fileName;

        for (FnPlatShareConfigUserDTO fnPlatShareConfigUserDTO : fnPlatShareConfigs) {
            //根据是否有工作天数数据决定按工作量导出/按比例导出
            if (fnPlatShareConfigUserDTO.getShareAmount() == null) {
                shareAmountFlag = false;
                break;
            }
        }
        if (shareAmountFlag) {
            // 按工作日导出
            return fnPlatShareConfigService.exportPlatShareData(platId, fnPlatShareConfigs, year + "年" + month + "月份人力统计.xls", "本月核算时间", new HashMap<>(), fnPlatShareMonthInfo.getWorkDay(), fnPlatShareMonthInfo.getWorkdayPeriod());
        } else {
            // 按分摊比例导出
            List<Long> list = new ArrayList();
            list.add(platId);
            return fnPlatShareConfigService.exportPlatMonthShareConfig(year, month, list, resultFilePath);

        }
    }

    @Override
    @Deprecated
    public String exportPlatShareData(Long platId, List<FnPlatShareConfigUserDTO> fnPlatShareConfigs, String fileName, String workDayStr, Map<Long, String> remarkByUserIdMap, BigDecimal workday, String workdayPeriod) {
        return null;
    }

    @Override
    public List<Long> resortForFavorProject(Long platId, List<Long> projectIds) {
        return null;
    }

    @Override
    @Deprecated
    public String exportPlatMonthShareConfig(Integer year, Integer month, List<Long> platIds, String filePath) {
        return null;
    }

    @Override
    public String exportWeekly(Integer lastYear, Integer lastWeek, Integer currentYear, Integer currentWeek) {
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
}
