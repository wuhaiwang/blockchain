package com.seasun.management.service.impl;

import com.seasun.management.constant.ErrorCode;
import com.seasun.management.dto.SubProjectInfo;
import com.seasun.management.exception.DataImportException;
import com.seasun.management.helper.ExcelHelper;
import com.seasun.management.helper.ReportHelper;
import com.seasun.management.mapper.FnProjectStatDataDetailMapper;
import com.seasun.management.mapper.FnProjectStatDataMapper;
import com.seasun.management.mapper.ProjectMapper;
import com.seasun.management.model.FnProjectStatData;
import com.seasun.management.model.FnProjectStatDataDetail;
import com.seasun.management.service.FnProjectStatDataDetailService;
import com.seasun.management.service.ProjectService;
import com.seasun.management.util.MyCellUtils;
import com.seasun.management.util.MyFileUtils;
import com.seasun.management.vo.FnProjectStatDataDetailVo;
import com.seasun.management.vo.FnProjectStatDataDetaildataVo;
import com.seasun.management.vo.ProjectVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FnProjectStatDataDetailServiceImpl implements FnProjectStatDataDetailService {

    private static final Logger logger = LoggerFactory.getLogger(FnPlatShareConfigServiceImpl.class);

    @Value("${export.excel.path}")
    private String exportExcelPath;

    @Value("${file.sys.prefix}")
    private String filePathPrefix;

    @Value("${backup.excel.url}")
    private String backUpExcelUrl;

    @Value("${export.excel.path}")
    private String exportExcelUrl;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private FnProjectStatDataMapper fnProjectStatDataMapper;

    @Autowired
    private FnProjectStatDataDetailMapper fnProjectStatDataDetailMapper;

    @Autowired
    private ProjectService projectService;

    @Override
    public String importFile(MultipartFile file, int year, int month) {
        Workbook wb = null;
        try {
            ExcelHelper.saveExcelBackup(file, filePathPrefix + backUpExcelUrl);
            wb = WorkbookFactory.create(file.getInputStream());
        } catch (Exception e) {
            throw new DataImportException(ErrorCode.FILE_IMPORT_FILE_READ_ERROR, "文件读取失败");
        }

        //数据准备，减少db操作
        List<ProjectVo> projectList = projectMapper.selectAllActiveProject();
        List<FnProjectStatData> fnProjectStatDataList = fnProjectStatDataMapper.selectByYearAndMonth(year, month);
        Map<Long, List<Long>> projectSumIdMap = buildProjectSumIdInfo(projectList);
        List<FnProjectStatData> tobeUpdateProjectData = new ArrayList<>();
        List<FnProjectStatDataDetail> tobeInsertDetails = new ArrayList<>();
        // 开始处理
        List<String> errorLogs = new ArrayList<>();
        Sheet sheet = wb.getSheetAt(0);
        int rowIndexEnd = 1;
        while (true) {
            Row rowDetailData = sheet.getRow(rowIndexEnd++);
            if (null == rowDetailData) {
                break;
            }

            int rowNumber = rowDetailData.getRowNum();
            logger.info("current row is:{}", rowNumber);
            Cell cellCode = rowDetailData.getCell(3);
            if (null == cellCode) {
                errorLogs.add("第" + rowNumber + "行的凭证号码为空，终止导入...");
                break;
            }
            String cellTextCode = cellCode.toString();

            //第三列为空，则停止。
            if (StringUtils.isEmpty(cellTextCode)) {
                break;
            }

            String costName = rowDetailData.getCell(5).getStringCellValue(); //费用名称
            Cell cellHeaderText = rowDetailData.getCell(6); //凭证抬头
            Cell cellText = rowDetailData.getCell(8); //文本
            Cell cellValue = rowDetailData.getCell(12);//金额
            Cell channelCell = rowDetailData.getCell(25);//渠道
            Cell productName = rowDetailData.getCell(15);

            if ("网游公共".equals(productName.getStringCellValue()) || "待用".equals(productName.getStringCellValue())) {
                continue; // 已和财务讨论，不处理此费用
            }

            String projectName = ExcelHelper.trimSpaceAndSpecialSymbol(productName.getStringCellValue());
            ProjectVo project = projectList.stream().filter(item -> {
                List<String> projectUsedNameList = ExcelHelper.buildProjectUsedNames(item);
                return projectUsedNameList.contains(projectName);
            }).findFirst().orElse(null);
            if (null == project) {
                errorLogs.add(rowDetailData.getRowNum() + " 行的项目 " + projectName + " 不存在");
                continue;
            }

            FnProjectStatDataDetail fnProjectStatDataDetail = new FnProjectStatDataDetail();
            fnProjectStatDataDetail.setName(costName);
            fnProjectStatDataDetail.setProjectId(project.getId());
            fnProjectStatDataDetail.setYear(year);
            fnProjectStatDataDetail.setMonth(month);
            fnProjectStatDataDetail.setCode(BigDecimal.valueOf(Double.valueOf(cellTextCode)).longValue());
            fnProjectStatDataDetail.setValue(BigDecimal.valueOf(cellValue.getNumericCellValue()));
            fnProjectStatDataDetail.setLineNumber(rowNumber + 1);

            if (!fnProjectStatDataList.isEmpty()) {
                // 广告费
                if (costName.startsWith(ReportHelper.GuangGaoFei_Name)) {
                    tobeUpdateProjectData.addAll(setGuangGaofei(fnProjectStatDataList, projectSumIdMap.get(project.getId()), channelCell, project, fnProjectStatDataDetail));
                }
                // 劳务费
                else if (costName.startsWith(ReportHelper.LaoWuFei_Name)) {
                    tobeUpdateProjectData.addAll(setLaoWuFei(fnProjectStatDataList, projectSumIdMap.get(project.getId()), cellHeaderText, cellText, project, fnProjectStatDataDetail));
                }
                // 其他都是推广费
                else {
                    tobeUpdateProjectData.addAll(setTuiGuangFei(fnProjectStatDataList, projectSumIdMap.get(project.getId()), cellHeaderText, cellText, project, fnProjectStatDataDetail));
                }

                tobeInsertDetails.add(fnProjectStatDataDetail);
            }
        }

        // 执行
        execute(tobeInsertDetails, tobeUpdateProjectData);

        // 保存错误日志
        String fileName = "import-error-project-stat-data-detail" + "-" + year + "-" + month;
        ExcelHelper.saveErrorFile(errorLogs, "project-stat-data-detail/", fileName);
        if (errorLogs.size() > 0) {
            String downloadURL = exportExcelUrl + "/" + fileName + ".txt";
            MyFileUtils.copyFile(ExcelHelper.ERR_LOG_DIR + "project-stat-data-detail/" + fileName + ".log", filePathPrefix + downloadURL);
            return downloadURL;
        }

        return "";
    }

    @Transactional
    private void execute(List<FnProjectStatDataDetail> tobeInsertDetails, List<FnProjectStatData> tobeUpdateData) {
        if (tobeInsertDetails.size() > 0) {
            fnProjectStatDataDetailMapper.deleteByYearAndMonth(tobeInsertDetails.get(0).getYear(), tobeInsertDetails.get(0).getMonth());
            fnProjectStatDataDetailMapper.batchInsert(tobeInsertDetails);
        }

        if (!tobeUpdateData.isEmpty()) {
            fnProjectStatDataMapper.batchUpdateDetailFlagByPks(tobeUpdateData);
        }
    }

    private Map<Long, List<Long>> buildProjectSumIdInfo(List<ProjectVo> allProject) {
        Map<Long, List<Long>> result = new HashMap<>();
        for (ProjectVo temp : allProject) {
            List<Long> sumIds = new ArrayList<>();
            sumIds.add(temp.getId());
            if (temp.getParentFnSumId() != null) {
                setProjectSumIds(sumIds, allProject, temp);
            }
            result.put(temp.getId(), sumIds);
        }
        return result;
    }

    private void setProjectSumIds(List<Long> sumIds, List<ProjectVo> allProject, ProjectVo projectVo) {
        for (ProjectVo temp : allProject) {
            if (projectVo.getParentFnSumId().equals(temp.getId())) {
                sumIds.add(temp.getId());
                if (temp.getParentFnSumId() != null) {
                    setProjectSumIds(sumIds, allProject, temp);
                }
            }
        }
    }

    private List<FnProjectStatData> setDetailFlag(List<FnProjectStatData> fnProjectStatData, List<Long> sumIds, Long statId) {
        List<FnProjectStatData> tobeUpdateRecords = new ArrayList<>();
        for (FnProjectStatData temp : fnProjectStatData) {
            if (sumIds.contains(temp.getProjectId()) && statId.equals(temp.getFnStatId())) {
                temp.setDetailFlag(true);
                tobeUpdateRecords.add(temp);
            }
        }
        return tobeUpdateRecords;
    }


    private List<FnProjectStatData> setTuiGuangFei(List<FnProjectStatData> fnProjectStatDataList, List<Long> sumIds, Cell cellHeaderText, Cell cellText, ProjectVo project, FnProjectStatDataDetail fnProjectStatDataDetail) {
        List<FnProjectStatData> tobeUpdateDetails = new ArrayList<>();
        fnProjectStatDataDetail.setStatId(ReportHelper.statIdNameMap.get(ReportHelper.ShiChangfei_Name));
        int year = fnProjectStatDataList.get(0).getYear();
        int month = fnProjectStatDataList.get(0).getMonth();

        // 设置detailFlag,以便查询
        FnProjectStatData fnProjectStatData = fnProjectStatDataList.stream().filter(item ->
                item.getFnStatId().equals(ReportHelper.statIdNameMap.get(ReportHelper.ShiChangfei_Name)) && item.getProjectId().equals(project.getId()) &&
                        item.getYear().equals(year) && item.getMonth().equals(month)).findFirst().orElse(null);
        if (null != fnProjectStatData && (fnProjectStatDataDetail.getValue().floatValue() > 1000 || fnProjectStatDataDetail.getValue().floatValue() < -1000)) { // 大于1000元，小于-1000的费用才会显示明细
            tobeUpdateDetails = setDetailFlag(fnProjectStatDataList, sumIds, ReportHelper.statIdNameMap.get(ReportHelper.ShiChangfei_Name));
        }

        if (null != cellText) {
            fnProjectStatDataDetail.setReason(MyCellUtils.getCellValue(cellHeaderText) + "," + MyCellUtils.getCellValue(cellText));
        } else {
            fnProjectStatDataDetail.setReason(MyCellUtils.getCellValue(cellHeaderText));
        }

        return tobeUpdateDetails;
    }

    private List<FnProjectStatData> setLaoWuFei(List<FnProjectStatData> fnProjectStatDataList, List<Long> sumIds, Cell cellHeaderText, Cell cellText, ProjectVo project, FnProjectStatDataDetail fnProjectStatDataDetail) {
        List<FnProjectStatData> tobeUpdateDetails = new ArrayList<>();
        fnProjectStatDataDetail.setStatId(ReportHelper.statIdNameMap.get(ReportHelper.LaoWuFei_Name));

        int year = fnProjectStatDataList.get(0).getYear();
        int month = fnProjectStatDataList.get(0).getMonth();

        // 设置detailFlag,以便查询
        FnProjectStatData fnProjectStatData = fnProjectStatDataList.stream().filter(item ->
                item.getFnStatId().equals(ReportHelper.statIdNameMap.get(ReportHelper.LaoWuFei_Name)) && item.getProjectId().equals(project.getId()) &&
                        item.getYear().equals(year) && item.getMonth().equals(month)).findFirst().orElse(null);
        if (null != fnProjectStatData && (fnProjectStatDataDetail.getValue().floatValue() > 1000 || fnProjectStatDataDetail.getValue().floatValue() < -1000)) { // 大于1000元，小于-1000的费用才会显示明细
            tobeUpdateDetails = setDetailFlag(fnProjectStatDataList, sumIds, ReportHelper.statIdNameMap.get(ReportHelper.LaoWuFei_Name));
        }

        if (null != cellText) {
            fnProjectStatDataDetail.setReason(cellHeaderText.getStringCellValue() + "," + cellText.getStringCellValue());
        } else {
            fnProjectStatDataDetail.setReason(cellHeaderText.getStringCellValue());
        }

        return tobeUpdateDetails;
    }

    private List<FnProjectStatData> setGuangGaofei(List<FnProjectStatData> fnProjectStatDataList, List<Long> sumIds, Cell channelColumnCell, ProjectVo project, FnProjectStatDataDetail fnProjectStatDataDetail) {

        List<FnProjectStatData> tobeUpdateDetails = new ArrayList<>();
        fnProjectStatDataDetail.setStatId(ReportHelper.statIdNameMap.get(ReportHelper.GuangGaoFei_Name));

        //第二个名称列,从这里取到渠道
        int year = fnProjectStatDataList.get(0).getYear();
        int month = fnProjectStatDataList.get(0).getMonth();

        // 设置detailFlag,以便查询
        FnProjectStatData fnProjectStatData = fnProjectStatDataList.stream().filter(item ->
                item.getFnStatId().equals(ReportHelper.statIdNameMap.get(ReportHelper.GuangGaoFei_Name)) && item.getProjectId().equals(project.getId()) &&
                        item.getYear().equals(year) && item.getMonth().equals(month)).findFirst().orElse(null);
        if (null != fnProjectStatData && (fnProjectStatDataDetail.getValue().floatValue() > 1000 || fnProjectStatDataDetail.getValue().floatValue() < -1000)) { // 大于1000元，小于-1000的费用才会显示明细
            tobeUpdateDetails = setDetailFlag(fnProjectStatDataList, sumIds, ReportHelper.statIdNameMap.get(ReportHelper.GuangGaoFei_Name));
        }

        //拿到渠道
        String stringChannel = channelColumnCell.getStringCellValue().split("/")[1].trim().replace("?", "");

        fnProjectStatDataDetail.setReason(project.getName() + "在" + stringChannel + "渠道投放广告");
        return tobeUpdateDetails;
    }

    @Override
    public List<FnProjectStatDataDetailVo> getDetailById(Long projectStatDataId) {
        FnProjectStatData fnProjectStatData = fnProjectStatDataMapper.selectByPrimaryKey(projectStatDataId);
        if (null == fnProjectStatData) {
            return null;
        }

        List<FnProjectStatDataDetailVo> result;
        SubProjectInfo subProjectInfo = projectService.getSubProjectById(fnProjectStatData.getProjectId());
        //汇总大西山居
        if (subProjectInfo.getType().equals(SubProjectInfo.Type.all)) {
            result = fnProjectStatDataDetailMapper.selectByYearAndMonthAndStatId(fnProjectStatData.getYear(), fnProjectStatData.getMonth(), fnProjectStatData.getFnStatId());
        }
        //按项目
        else if (subProjectInfo.getType().equals(SubProjectInfo.Type.project)) {
            result = fnProjectStatDataDetailMapper.selectByProjectIdYearAndMonthAndStatId(fnProjectStatData.getProjectId(), fnProjectStatData.getYear(), fnProjectStatData.getMonth(), fnProjectStatData.getFnStatId());
        }
        //按汇总拆分的子项目
        else {
            List<Long> projectIdList = subProjectInfo.getSubProjects().stream().map(item -> item.getId()).collect(Collectors.toList());
            result = fnProjectStatDataDetailMapper.getDetailByProjectIdsAndYearAndMonthAndStatId(projectIdList, fnProjectStatData.getYear(), fnProjectStatData.getMonth(), fnProjectStatData.getFnStatId());
        }
        result.forEach(r -> r.setValue(r.getValue().divide(BigDecimal.valueOf(10000)).setScale(1, BigDecimal.ROUND_HALF_UP)));
        result = result.stream().filter(r -> r.getValue().floatValue() != 0 && (r.getValue().floatValue() >= 0.1 || r.getValue().floatValue() <= -0.1)).collect(Collectors.toList());
        return result;
    }

    @Override
    public List<FnProjectStatDataDetaildataVo> getDetailByCondition(Long projectId, Long statId, Integer year, Integer month) {
        SubProjectInfo subProjectInfo = projectService.getSubProjectById(projectId);
        List<Long> projectIds = null;

        if (subProjectInfo.getType().equals(SubProjectInfo.Type.project)) {
            projectIds = new ArrayList<>(2);
            projectIds.add(projectId);
        } else {
            if (subProjectInfo.getSubProjects() != null) {
                projectIds = subProjectInfo.getSubProjects().stream().map(item -> item.getId()).collect(Collectors.toList());
            }
        }

        return fnProjectStatDataDetailMapper.selectDetailByCondition(projectIds, statId, year, month);
    }


}
