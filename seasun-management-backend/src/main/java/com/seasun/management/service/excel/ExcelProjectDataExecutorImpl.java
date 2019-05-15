package com.seasun.management.service.excel;

import com.seasun.management.constant.ErrorCode;
import com.seasun.management.exception.DataImportException;
import com.seasun.management.helper.ExcelHelper;
import com.seasun.management.helper.ReportHelper;
import com.seasun.management.mapper.FnProjectStatDataDetailMapper;
import com.seasun.management.mapper.FnProjectStatDataMapper;
import com.seasun.management.mapper.FnStatMapper;
import com.seasun.management.mapper.ProjectMapper;
import com.seasun.management.model.FnProjectStatData;
import com.seasun.management.model.FnProjectStatDataDetail;
import com.seasun.management.model.FnStat;
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

import java.lang.reflect.Array;
import java.util.*;

@Service(value = "project")
public class ExcelProjectDataExecutorImpl extends AbstractExcelTaskExecutor {

    private static final Logger logger = LoggerFactory.getLogger(ExcelProjectDataExecutorImpl.class);

    private static String[] excludeSheets = new String[]{"税金check", "比例-1", "特殊要求-发前改", "层次", "中介人力比例", "比例-2", "比例-3", "比例-4", "比例-5", "比例-6", "比例-7", "比例-8",
            "比例-9", "比例-10", "比例-11", "比例-12", "总表-for大西山居", "总表-for KXB", "KXB部门费用明细", "XSJ直接研发费"};

    private static String[] splitStatNames = {
            "收入", "产品-运营成本", "产品-销售费用",
            "分摊费用", "费用合计（直接+分摊）",
            "大连平台管理费用分摊", "大连平台研发费用分摊"};

    private static String endRowStr = "人均利润（人数含分摊）";
    private static String[] skippedRowStr = {"ADPCU", "check"};
    private static Map<Integer, String> columnMonthMap = new HashMap<>();

    static {
        columnMonthMap.put(2, "1月");
        columnMonthMap.put(3, "2月");
        columnMonthMap.put(4, "3月");
        columnMonthMap.put(5, "4月");
        columnMonthMap.put(6, "5月");
        columnMonthMap.put(7, "6月");
        columnMonthMap.put(8, "7月");
        columnMonthMap.put(9, "8月");
        columnMonthMap.put(10, "9月");
        columnMonthMap.put(11, "10月");
        columnMonthMap.put(12, "11月");
        columnMonthMap.put(13, "12月");
        columnMonthMap.put(14, "合计");
    }

    @Autowired
    ProjectMapper projectMapper;

    @Autowired
    FnStatMapper fnStatMapper;

    @Autowired
    FnProjectStatDataMapper fnProjectStatDataMapper;

    @Autowired
    FnProjectStatDataDetailMapper fnProjectStatDataDetailMapper;

    @Override
    public String run(Sheet sheet, Long sheetIdentifyId, FnTask fnTask, int year, int month) {
        logger.info("enter project executor ...");

        // 首行第二列,为年标示。若指定了月份，则网页端的参数一致
        int yearFromExcel = 0;
        try {
            yearFromExcel = Integer.parseInt(sheet.getRow(0).getCell(1).getStringCellValue().split("年")[0]);
        } catch (NumberFormatException e) {
            // 记录被忽略的sheet
            List<String> contents = new ArrayList<>();
            contents.add(sheet.getSheetName() + ": 首行中找不到完整的日期信息...");
            ExcelHelper.saveErrorFile(contents, "project/", "01_taskId_" + fnTask.getId() + "_skippedSheet");
            logger.info("sheet:{} can not found date info ,will skip ...", sheet.getSheetName());
            return sheet.getSheetName() + ": 首行中找不到完整的日期信息...";
        }

        if (year != 0 && year != yearFromExcel) {
            throw new DataImportException(ErrorCode.FILE_IMPORT_SELECT_YEAR_DO_NOT_MATCH_IN_SHEET_ERROR, "year in request param does not match the year from excel ");
        }

        Long projectId = sheetIdentifyId;
        List<FnStat> availableRows = getAvailableRows(projectId);
        Map<Integer, FnStat> validRowIndexes = initValidRows(sheet, projectId, fnTask.getId(), availableRows, yearFromExcel);
        Map<Integer, String> validColumnIndexes = initValidColumns(month);
        List<FnProjectStatData> fnProjectStatDataList = new ArrayList<>();

        // 行遍历
        for (int i = 1; i < sheet.getPhysicalNumberOfRows() + 1; i++) {
            logger.debug("current row :{},end row :{}", i, sheet.getPhysicalNumberOfRows());
            Row row = sheet.getRow(i);

            // 去除无效行
            if (!validRowIndexes.containsKey(i)) {
                continue;
            }

            // 列遍历
            for (int j = 2; j <= row.getLastCellNum(); j++) {
                Cell cell = row.getCell(j);

                // 去除无效列，无效cell
                boolean isValidCell = isValidCell(cell, validRowIndexes.keySet(), validColumnIndexes.keySet());
                if (!isValidCell) {
                    logger.debug("skip invalid cell...");
                    continue;
                }

                Long relativeStatId = validRowIndexes.get(cell.getRowIndex()).getId();
                String monthStr = validColumnIndexes.get(cell.getColumnIndex());
                Integer relativeMonthId = monthStr.equals("合计") ? 0 : Integer.parseInt(monthStr.split("月")[0]);


                FnProjectStatData fnProjectStatData = new FnProjectStatData();
                fnProjectStatData.setYear(yearFromExcel);
                fnProjectStatData.setMonth(relativeMonthId);
                fnProjectStatData.setProjectId(projectId);

                fnProjectStatData.setFnStatId(relativeStatId);
                fnProjectStatData.setCreateTime(new Date());
                fnProjectStatData.setUpdateTime(new Date());
                fnProjectStatData.setValue(Float.valueOf(MyCellUtils.getCellValue(cell)));
                fnProjectStatDataList.add(fnProjectStatData);
            }
        }

        if (fnProjectStatDataList.size() > 0) {

            // 删除旧数据
            if (month != 0) {
                fnProjectStatDataMapper.deleteByYearAndMonthAndProjectId(yearFromExcel, month, projectId);
            } else {
                fnProjectStatDataMapper.deleteByYearAndProjectId(yearFromExcel, projectId);
            }

            // 如果有明细数据，则设置DetailFlag
            FnProjectStatData fnProjectStatData = fnProjectStatDataList.get(0);
            List<FnProjectStatDataDetail> details;
            // 按月导入
            if (month != 0) {
                details = fnProjectStatDataDetailMapper.selectByProjectIdYearAndMonth(fnProjectStatDataList.get(0).getProjectId(), fnProjectStatData.getYear(), fnProjectStatData.getMonth());
            }
            // 按全年导入
            else {
                details = fnProjectStatDataDetailMapper.selectByProjectIdYear(fnProjectStatDataList.get(0).getProjectId(), fnProjectStatData.getYear());
            }

            for (FnProjectStatData f : fnProjectStatDataList) {
                // 处理汇总项，因为汇总项在明细表中是没有实体项目相对应的。
                // 1. 如果该项目是汇总项
                if (ReportHelper.sumProjects.contains(f.getProjectId())
                        // 2. 且该统计项是明细费用中的指标
                        && ReportHelper.statIdNameMap.values().contains(f.getFnStatId())
                        // 3. 且该费用不为0
                        && MyCellUtils.formatFloatNumber(f.getValue(), 2) > 0) {
                    f.setDetailFlag(true);
                }
                // 处理普通项目
                else if (details.size() > 0) {
                    f.setDetailFlag(
                            details.stream().anyMatch(d -> d.getStatId().equals(f.getFnStatId())
                                    && d.getProjectId().equals(f.getProjectId())
                                    && d.getYear().equals(f.getYear())
                                    && d.getMonth().equals(f.getMonth())
                                    && MyCellUtils.formatFloatNumber(f.getValue(), 2) > 0)
                    );
                }
            }


            // 插入新数据
            fnProjectStatDataMapper.batchInsert(fnProjectStatDataList);


            logger.info("batch insert finish...");
        }
        return "success";
    }


    @Override
    public Map<Long, Sheet> getValidSheet(Workbook wb, FnTask fnTask, int month) {
        Map<Long, Sheet> sheets = new HashMap<>();
        List<String> invalidSheet = new ArrayList<>();

        // 获取所有非平台的分摊项目
        List<ProjectVo> availableProjectVos = projectMapper.selectAllFNProject();
        List<ProjectVo> allInactiveProjectVos = projectMapper.selectAllInActiveProject();

        for (int i = 0; i < wb.getNumberOfSheets(); i++) {
            Sheet sheet = wb.getSheetAt(i);

            if (wb.isSheetHidden(i)) {
                invalidSheet.add(sheet.getSheetName() + ": 该sheet为隐藏项");
                continue;
            }

            // 排查忽略项
            if (isInValidSheet(sheet.getSheetName(), excludeSheets)) {
                invalidSheet.add(sheet.getSheetName() + ": 该sheet为忽略项");
                continue;
            }

            // 若某个sheet的名称匹配上一个非激活的项目，则该sheet不导入。
            boolean isInactiveProject = allInactiveProjectVos.stream().anyMatch(p -> p.getName().equals(sheet.getSheetName()));
            if (isInactiveProject) {
                invalidSheet.add(sheet.getSheetName() + "： 该sheet名称无法匹配到项目");
                continue;
            }


            // 检查项目名称是否有效
            boolean isValidProject = false;
            for (ProjectVo projectVo : availableProjectVos) {
                List<String> projectUsedNames = ExcelHelper.buildProjectUsedNames(projectVo);
                if (projectUsedNames.contains(ExcelHelper.trimSpaceAndSpecialSymbol(sheet.getSheetName()))) {
                    isValidProject = true;
                    sheets.put(projectVo.getId(), sheet);
                    break;
                }
            }


            if (!isValidProject) {
                invalidSheet.add(sheet.getSheetName() + "： 该sheet名称无法匹配到项目");
                continue;
            }

        }

        ExcelHelper.saveErrorFile(invalidSheet, "project/", "02_taskId_" + fnTask.getId() + "_invalid-project-sheet");
        return sheets;
    }


    /**
     * 获取该项目下的有效统计项 = 固定统计项 + 项目自定义统计项
     *
     * @return
     */
    private List<FnStat> getAvailableRows(Long projectId) {
        List<FnStat> result = new ArrayList<>();

        // 固定项
        List<FnStat> fixedStats = fnStatMapper.selectAllFixedStats();
        result.addAll(fixedStats);

        // 项目自定义
        List<FnStat> projectStats = fnStatMapper.selectStatByProjectId(projectId);
        result.addAll(projectStats);
        return result;
    }

    private Collection<String> getAvailableColumnNames() {
        return columnMonthMap.values();
    }

    private Map<Integer, FnStat> initValidRows(Sheet sheet, Long projectId, Long taskId, List<FnStat> availableRows, int yearFromExcel) {
        Map<Integer, FnStat> validRowIndexes = new HashMap<>();
        List<String> invalidRows = new ArrayList<>();

        Map<String, Long> statNameRowIdMap = buildStatNameRowIdMap(sheet, Arrays.asList(splitStatNames));

        // 判断是否包含大连特殊统计项
        boolean isContainsDaLianStat = statNameRowIdMap.containsKey("大连平台管理费用分摊");

        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            if (null != sheet.getRow(i)) {

                // 首行前两列都为空,则为空行，忽略
                String headerStr = "";
                Cell cell0 = sheet.getRow(i).getCell(0);
                Cell cell1 = sheet.getRow(i).getCell(1);
                headerStr = ExcelHelper.trimSpaceAndSpecialSymbol(MyCellUtils.getCellValue(cell0));
                if ("".equals(headerStr)) {
                    headerStr = ExcelHelper.trimSpaceAndSpecialSymbol(MyCellUtils.getCellValue(cell1));
                    if ("".equals(headerStr)) {
                        logger.debug("empty row header,will skip...");
                        continue;
                    }
                }

                // 忽略无意义的统计项
                if (headerStr.equals("0") || Arrays.asList(skippedRowStr).contains(headerStr)) {
                    continue;
                }

                // todo: 临时方案:把 直接费用(研发+运营) 转成 直接费用
                if ("直接费用研发运营".equals(headerStr)) {
                    headerStr = "直接费用";
                }

                boolean isMatch = false;
                Long checkParentId = getParentIdByRowId(yearFromExcel, i, availableRows, statNameRowIdMap);
                for (FnStat fnStat : availableRows) {
                    /** 因为子分摊项会出现重复名字（比如：统计项 “其他”，会在很多地方出现），所以需要再次检查父分摊项 **/
                    boolean matchParentId =
                            null == checkParentId  // 父ID不存在，说明该统计项无需匹配父ID，算通过
                                    || null == fnStat.getParentId() // 统计项本身就没有父ID,算通过
                                    || fnStat.getParentId().equals(checkParentId); // 父ID匹配到，且正确，算通过

                    if (ExcelHelper.trimSpaceAndSpecialSymbol(fnStat.getName())
                            .equals(headerStr) && matchParentId) {  // 统计项名称正确，且统计项的父ID也正确的情况下，才是有效行
                        validRowIndexes.put(i, fnStat);
                        isMatch = true;
                        break;
                    }
                }
                if (!isMatch) {
                    invalidRows.add("找不到统计项的行号：" + i + "，行名：" + headerStr + "，将为该项目添加该统计项目...");

                    // 计算父统计项，规则：该行的第一个单元格为空，且第二个单元格不为空，则该行为子分摊项。
                    String cell0Str = ExcelHelper.trimSpaceAndSpecialSymbol(MyCellUtils.getCellValue(cell0));
                    String cell1Str = ExcelHelper.trimSpaceAndSpecialSymbol(MyCellUtils.getCellValue(cell1));
                    Long parentId = null;
                    if ("".equals(cell0Str) && !"".equals(cell1Str)) {
                        parentId = getParentIdByRowId(yearFromExcel, i, availableRows, statNameRowIdMap);
                    }
                    FnStat stat = new FnStat();
                    stat.setName(headerStr);
                    stat.setType(FnStat.Type.project);
                    stat.setProjectId(projectId);
                    stat.setParentId(parentId);
                    fnStatMapper.insert(stat);// 不使用批量插入,以便获取到id
                    validRowIndexes.put(i, stat);
                    availableRows.add(stat);
                }

                // 以前：只处理到 “人均利润（人数含分摊）” 这一行， 后面的均忽略
                // 现在：还需要解析“大连管理平台费用”
                if (headerStr.equals(ExcelHelper.trimSpaceAndSpecialSymbol(endRowStr)) && !isContainsDaLianStat) {
                    break;
                }


            }
        }
        ExcelHelper.saveErrorFile(invalidRows, "project/", "03_taskId_" + taskId + "_" + sheet.getSheetName() + "_invalid-row");

        return validRowIndexes;

    }

    private Map<Integer, String> initValidColumns(int month) {

        // month不为0，只导入“ 指定月份列，合计列 ”的数据
        if (month != 0) {
            Map<Integer, String> currentMonthMap = new HashMap<>();
            currentMonthMap.put(month + 1, month + "月");
            currentMonthMap.put(14, "合计");
            return currentMonthMap;
        }

        // 否则导入所有列的数据
        return columnMonthMap;
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
            logger.debug("found invalid cell,cell is empty or null...");
            return false;
        }

        try {
            Float numberValue = Float.valueOf(value);
            if (numberValue == 0) {
                logger.debug("found zero cell,skip 0 value...");
                return false;
            }

        } catch (NumberFormatException e) {
            logger.debug("found invalid cell,cell is not a valid float value...");
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

    private boolean isInValidSheet(String sheetName, String[] excludeSheets) {
        for (int i = 0; i < excludeSheets.length; i++) {
            if (StringUtils.endsWithIgnoreCase(sheetName, excludeSheets[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * 目的：为满足动态加入统计项时，各个统计项的父fnId正确，增加此方法。
     * 实现：根据行号获取父id，不同区间内的子项对应一个父id(因表整体内容变化不大，故写死。todo:优化放到配置表中)。
     *
     * @param i
     * @return
     */
    private Long getParentIdByRowId(int yearFromExcel, int i, List<FnStat> fnStats, Map<String, Long> statNameRowIdMap) {
        Long parentId = null;
        // 1-14行的父统计项为 <收入>
        if (i > 1 && i < 14) {
            parentId = getStatIdByNameWithDefaultValue("收入", fnStats, 2000L);
        }
        // "产品-运营成本"行 ~ "产品-销售费用"行，父统计项为 <产品-运营成本>
        else if (i > statNameRowIdMap.get("产品-运营成本").intValue() && i < statNameRowIdMap.get("产品-销售费用").intValue()) {
            parentId = getStatIdByNameWithDefaultValue("产品-运营成本", fnStats, 2500L);
        }
        // "分摊费用"行 ~ "费用合计（直接+分摊）"行，父统计项为 <分摊费用>
        else if (i > statNameRowIdMap.get("分摊费用").intValue() && i < statNameRowIdMap.get("费用合计（直接+分摊）").intValue()) {
            parentId = getStatIdByNameWithDefaultValue("分摊费用", fnStats, 2900L);
        }
        // "大连平台管理费用"行 ~ "大连平台研发费用分摊"行，父统计项为 <大连平台管理费用分摊>。因为<大连平台管理费用分摊>不是固定分摊项，所以默认值只能填写:-1
        else if (statNameRowIdMap.containsKey("大连平台管理费用") && statNameRowIdMap.containsKey("大连平台研发费用分摊") &&
                (i > statNameRowIdMap.get("大连平台管理费用").intValue() && i < statNameRowIdMap.get("大连平台研发费用分摊").intValue())
                ) {
            parentId = getStatIdByNameWithDefaultValue("大连平台管理费用分摊", fnStats, -1L);
        }
        // "大连平台研发费用分摊"行之后，父统计项为 <大连平台研发费用分摊>。因为<大连平台管理费用分摊>不是固定分摊项，所以默认值只能填写:-1
        else if (statNameRowIdMap.containsKey("大连平台研发费用分摊") &&
                (i > statNameRowIdMap.get("大连平台研发费用分摊").intValue())) {
            parentId = getStatIdByNameWithDefaultValue("大连平台研发费用分摊", fnStats, -1L);
        }

        return parentId;
    }

    // 根据统计项名称获取FnStatId
    private Long getStatIdByNameWithDefaultValue(String statName, List<FnStat> fnStatList, Long defaultValue) {
        Long result = defaultValue;
        for (FnStat stat : fnStatList) {
            if (stat.getName().equals(statName)) {
                result = stat.getId();
                break;
            }
        }
        return result;
    }


    // 根据输入的统计项名称，返回这些统计项在sheet中所在的行(只遍历sheet的第一列）。
    private Map<String, Long> buildStatNameRowIdMap(Sheet sheet, List<String> statNames) {
        HashMap<String, Long> statNameRowIdMap = new HashMap<>();
        for (int i = 1; i < sheet.getPhysicalNumberOfRows(); i++) {
            if (sheet.getRow(i) == null || sheet.getRow(i).getCell(0) == null) {
                continue;
            }
            Cell cell0 = sheet.getRow(i).getCell(0);
            // 注意：若发现有重名的statName，则第二次会覆盖第一次的rowId
            if (cell0 != null && statNames.contains(MyCellUtils.getCellValue(cell0))) {
                statNameRowIdMap.put(cell0.getStringCellValue(), Long.valueOf(i));
            }
        }
        return statNameRowIdMap;
    }
}
