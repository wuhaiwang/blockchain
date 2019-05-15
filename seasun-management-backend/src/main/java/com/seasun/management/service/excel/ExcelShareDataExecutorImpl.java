package com.seasun.management.service.excel;

import com.seasun.management.constant.ErrorCode;
import com.seasun.management.dto.SectionInfo;
import com.seasun.management.exception.DataImportException;
import com.seasun.management.helper.ExcelHelper;
import com.seasun.management.mapper.FnShareDataMapper;
import com.seasun.management.mapper.ProjectMapper;
import com.seasun.management.model.FnShareData;
import com.seasun.management.model.FnTask;
import com.seasun.management.util.MyCellUtils;
import com.seasun.management.vo.ProjectVo;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Service(value = "share")
public class ExcelShareDataExecutorImpl extends AbstractExcelTaskExecutor {

    private static final Logger logger = LoggerFactory.getLogger(ExcelShareDataExecutorImpl.class);

    private String[] includeSheets = new String[]{"比例-1", "比例-2", "比例-3", "比例-4", "比例-5", "比例-6", "比例-7", "比例-8",
            "比例-9", "比例-10", "比例-11", "比例-12"};

    @Autowired
    FnShareDataMapper fnShareDataMapper;

    @Autowired
    ProjectMapper projectMapper;

    @Override
    public String run(Sheet sheet, Long sheetIdentifyId, FnTask fnTask, int year, int month) {
        logger.info("enter share executor ...");
        String date = ExcelHelper.getYearAndMonth(sheet.getRow(0).getCell(0));

        // 首行首列为日期标示。若指定了月份，则网页端的参数一致
        int yearFromExcel = Integer.parseInt(date.split("-")[0]);
        if (year != 0 && year != yearFromExcel) {
            throw new DataImportException(ErrorCode.FILE_IMPORT_SELECT_YEAR_DO_NOT_MATCH_IN_SHEET_ERROR, "sheet:" + sheet.getSheetName() + "year in request param does not match the year from excel ");
        }
        int monthFromExcel = Integer.parseInt(date.split("-")[1]);
        if (month != 0 && month != monthFromExcel) {
            throw new DataImportException(ErrorCode.FILE_IMPORT_SELECT_MONTH_DO_NOT_MATCH_IN_SHEET_ERROR, "sheet:" + sheet.getSheetName() + "month in request param does not match the month from excel ");
        }

        List<ProjectVo> availableRows = getAvailableRows();
        List<ProjectVo> availableColumns = getAvailableColumns();
        Map<Integer, ProjectVo> validRowIndexes = initValidRows(sheet, fnTask.getId(), availableRows);
        Map<Integer, ProjectVo> validColumnIndexes = initValidColumns(sheet, fnTask.getId(), availableColumns);

        List<SectionInfo> sectionInfos = buildSectionInfo(sheet);
        Map<String, FnShareData> shareDataMap = new HashMap<>();

        //分段处理
        for (int sectionId = 0; sectionId < sectionInfos.size(); sectionId++) {
            SectionInfo currentSection = sectionInfos.get(sectionId);

            // 行遍历
            for (int i = currentSection.getStartRowIndex(); i < currentSection.getEndRowIndex() + 1; i++) {
                logger.info("section:{},current row :{},end row :{}",
                        currentSection.getSectionTitle(), i, currentSection.getEndRowIndex());
                Row row = sheet.getRow(i);

                // 去除无效行
                if (!validRowIndexes.containsKey(i)) {
                    continue;
                }

                // 列遍历，加上遍历第二列
                for (int j = 1; j <= row.getLastCellNum(); j++) {
                    // 去除分摊比例第二列
                    if (sectionId == 0 && j == 1) {
                        continue;
                    }

                    Cell cell = row.getCell(j);

                    // 去除无效列，无效cell
                    boolean isValidCell = isValidCell(cell, validRowIndexes.keySet(), validColumnIndexes.keySet());
                    if (!isValidCell) {
                        logger.debug("skip invalid cell...");
                        continue;
                    }

                    Long relativePlatId = validRowIndexes.get(cell.getRowIndex()).getId();
                    Long relativeProjectId = validColumnIndexes.get(cell.getColumnIndex()).getId();
                    String shareDataKey = relativePlatId + "_" + relativeProjectId;

                    FnShareData shareDataCell = shareDataMap.get(shareDataKey);
                    if (null == shareDataCell) {
                        shareDataCell = new FnShareData();
                        shareDataMap.put(shareDataKey, shareDataCell);
                        shareDataCell.setPlatId(relativePlatId);
                        shareDataCell.setProjectId(relativeProjectId);
                        shareDataCell.setMonth(monthFromExcel);
                        shareDataCell.setYear(yearFromExcel);
                        shareDataCell.setCreateTime(new Date());
                        shareDataCell.setUpdateTime(new Date());
                    }

                    if (sectionId == 0) {
                        shareDataCell.setSharePro((float) cell.getNumericCellValue());
                    } else if (sectionId == 1) {
                        shareDataCell.setShareAmount((float) cell.getNumericCellValue());
                    } else if (sectionId == 2) {
                        shareDataCell.setShareNumber((float) cell.getNumericCellValue());

                        // 若分摊比例,分摊金额,分摊人数均为0,则去除该记录.
                        boolean isEmptyPro = shareDataCell.getSharePro() == null || shareDataCell.getSharePro() == 0;
                        boolean isEmptyAmount = shareDataCell.getShareAmount() == null || shareDataCell.getShareAmount() == 0;
                        boolean isEmptyNumber = shareDataCell.getShareNumber() == null || shareDataCell.getShareNumber() == 0;
                        if (isEmptyPro && isEmptyAmount && isEmptyNumber) {
                            logger.debug("cell is zero share value, will skip ...");
                            shareDataMap.remove(shareDataKey);
                        }
                    }
                }

            }
        }

        if (shareDataMap.values().size() > 0) {
            // 删除旧数据
            fnShareDataMapper.deleteByYearAndMonth(yearFromExcel, monthFromExcel);

            // 插入新数据
            fnShareDataMapper.batchInsert(shareDataMap.values());
            logger.info("batch insert finish...");
        }
        return "success";
    }


    @Override
    public Map<Long, Sheet> getValidSheet(Workbook wb, FnTask fnTask, int month) {
        Map<Long, Sheet> sheets = new HashMap<>();

        // month不为0，则只导入指定月份数据
        if (month != 0) {
            for (int i = 0; i < wb.getNumberOfSheets(); i++) {
                Sheet sheet = wb.getSheetAt(i);
                if (sheet.getSheetName().equals("比例-" + month)) {
                    sheets.put(Long.valueOf(i), sheet);
                    break;
                }
            }
        }
        // 否则，导入全年的数据
        else {
            for (int i = 0; i < wb.getNumberOfSheets(); i++) {
                Sheet sheet = wb.getSheetAt(i);
                if (isValidSheet(sheet.getSheetName(), includeSheets)) {
                    sheets.put(Long.valueOf(i), sheet);
                }
            }
        }

        return sheets;
    }

    /**
     * 判断当前cell是否有效。当前规则为：1.当前列有效，2.当前行有效，3.cell不为空。
     *
     * @param cell
     * @param validRowIndexes
     * @param validColumnIndexes
     * @return
     */
    @Override
    public boolean isValidCell(Cell cell, Set<Integer> validRowIndexes, Set<Integer> validColumnIndexes) {
        String value = MyCellUtils.getCellValue(cell);

        if (null == value || "".equals(value)) {
            logger.debug("found invalid cell,cell is empty or null");
            return false;
        }

        if (!validRowIndexes.contains(Integer.valueOf(cell.getRowIndex()))) {
            logger.debug("invalid row...");
            return false;
        }

        if (!validColumnIndexes.contains(Integer.valueOf(cell.getColumnIndex()))) {
            logger.debug("invalid column...");
            return false;
        }
        return true;
    }

    private List<ProjectVo> getAvailableRows() {
        return projectMapper.selectAllSharePlat();
    }


    private List<ProjectVo> getAvailableColumns() {
        return projectMapper.selectAllShareProject();
    }

    private Map<Integer, ProjectVo> initValidRows(Sheet sheet, Long taskId, List<ProjectVo> availableRowNames) {
        Map<Integer, ProjectVo> validRowIndexes = new HashMap<>();
        List<String> invalidRows = new ArrayList<>();

        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            if (null != sheet.getRow(i)) {
                Cell cell = sheet.getRow(i).getCell(0);
                String cellStr = MyCellUtils.getCellValue(cell);
                if ("".equals(cellStr)) {
                    invalidRows.add("空行：" + i);
                    continue;
                }

                String platName = ExcelHelper.trimSpaceAndSpecialSymbol(cellStr);
                boolean isMatch = false;
                for (ProjectVo projectVo : availableRowNames) {
                    List<String> projectUsedNames = ExcelHelper.buildProjectUsedNames(projectVo);
                    if (projectUsedNames.contains(platName)) {
                        validRowIndexes.put(Integer.valueOf(i), projectVo);
                        isMatch = true;
                        break;
                    }
                }
                if (!isMatch) {
                    invalidRows.add("找不到平台的行：" + cell.getRowIndex() + ":" + platName);
                }
            }
        }
        ExcelHelper.saveErrorFile(invalidRows, "share/", "03_taskId_" + taskId + "_" + sheet.getSheetName() + "_invalid-row");
        return validRowIndexes;
    }


    /**
     * 获取有效列
     *
     * @param sheet
     * @param availableColumnNames
     * @return Map<列id,列对应的project>
     */
    private Map<Integer, ProjectVo> initValidColumns(Sheet sheet, Long taskId, List<ProjectVo> availableColumnNames) {
        Map<Integer, ProjectVo> validColumnIndexes = new HashMap<>();
        List<String> invalidColumns = new ArrayList<>();

        Row row = sheet.getRow(0);
        for (int i = 1; i < row.getPhysicalNumberOfCells(); i++) {
            Cell cell = row.getCell(i);
            if (null == cell) {
                invalidColumns.add("空列：" + i);
                continue;
            }

            // 处理汇总列，设置projectId为1
            if (i == 1) {
                ProjectVo projectVo = new ProjectVo();
                projectVo.setId(1L);
                projectVo.setName("汇总项");
                validColumnIndexes.put(Integer.valueOf(i), projectVo);
                continue;
            }

            String cellStr = ExcelHelper.trimSpaceAndSpecialSymbol(MyCellUtils.getCellValue(cell));
            boolean isMatch = false;
            for (ProjectVo projectVo : availableColumnNames) {
                List<String> projectUsedNames = ExcelHelper.buildProjectUsedNames(projectVo);
                if (projectUsedNames.contains(cellStr)) {
                    validColumnIndexes.put(Integer.valueOf(i), projectVo);
                    isMatch = true;
                    break;
                }
            }
            if (!isMatch) {
                invalidColumns.add("找不到项目的列：" + cell.getColumnIndex() + ":" + cellStr);
            }
        }
        ExcelHelper.saveErrorFile(invalidColumns, "share/", "02_taskId_" + taskId + "_" + sheet.getSheetName() + "_invalid-column");
        return validColumnIndexes;
    }

    private boolean isValidSheet(String sheetName, String[] excludeSheets) {
        for (int i = 0; i < excludeSheets.length; i++) {
            if (StringUtils.endsWithIgnoreCase(sheetName, excludeSheets[i])) {
                return true;
            }
        }
        return false;
    }

    /*
    * 比例表需分段读取：人数，比例，金额.
    * */
    private List<SectionInfo> buildSectionInfo(Sheet sheet) {

        SectionInfo sectionInfo0 = new SectionInfo();
        SectionInfo sectionInfo1 = new SectionInfo();
        SectionInfo sectionInfo2 = new SectionInfo();

        for (int i = sheet.getFirstRowNum(); i < sheet.getPhysicalNumberOfRows(); i++) {

            if (sheet.getRow(i) == null || sheet.getRow(i).getCell(1) == null) {
                continue;
            }
            Cell cell = sheet.getRow(i).getCell(1);
            if (MyCellUtils.getCellValue(cell).equals("比例")) {
                sectionInfo0.setSectionTitle("比例");
                sectionInfo0.setSectionId(0);
                sectionInfo0.setStartRowIndex(cell.getRowIndex() + 1);
                continue;
            } else if (MyCellUtils.getCellValue(cell).equals("金额")) {
                sectionInfo1.setSectionTitle("金额");
                sectionInfo1.setSectionId(1);
                sectionInfo1.setStartRowIndex(cell.getRowIndex() + 1);
                sectionInfo0.setEndRowIndex(cell.getRowIndex() - 2);
                continue;
            } else if (MyCellUtils.getCellValue(cell).equals("人数")) {
                sectionInfo2.setSectionTitle("人数");
                sectionInfo2.setSectionId(2);
                sectionInfo2.setStartRowIndex(cell.getRowIndex() + 1);
                sectionInfo1.setEndRowIndex(cell.getRowIndex() - 2);
                break;
            }
        }


        sectionInfo2.setEndRowIndex(sheet.getPhysicalNumberOfRows());
        List<SectionInfo> sectionInfos = new ArrayList<>();
        sectionInfos.add(sectionInfo0);
        sectionInfos.add(sectionInfo1);
        sectionInfos.add(sectionInfo2);

        for (SectionInfo sectionInfo : sectionInfos) {
            logger.info("section info:" + sectionInfo.getSectionTitle() + "," + sectionInfo.getStartRowIndex() + "," + sectionInfo.getEndRowIndex());
        }

        return sectionInfos;
    }

}
