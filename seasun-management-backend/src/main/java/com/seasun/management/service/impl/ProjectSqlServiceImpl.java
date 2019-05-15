package com.seasun.management.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.seasun.management.service.SqlFileGenerateService;
import com.seasun.management.util.MyCellUtils;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Service
public class ProjectSqlServiceImpl implements SqlFileGenerateService {

    @Value("${export.excel.path}")
    private String exportExcelPath;

    @Value("${file.sys.prefix}")
    private String filePathPrefix;

    private static final org.slf4j.Logger Logger = LoggerFactory.getLogger(ProjectSqlServiceImpl.class);


    private static final String old_finance_outsourcing_table = "repr_proj_data";
    private static final String new_it_outsourcing_table = "fn_project_stat_data";

    private static final String old_outsourcing_stat_id = "20065";
    private static final String new_outsourcing_stat_id = "3700";

    private static final String old_outsourcing_stat_field_name = "stat_id";
    private static final String new_outsourcing_stat_field_name = "fn_stat_id";

    private static final String old_project_table = "repr_proj_define";
    private static final String new_project_table = "project";

    @Override
    public JSONObject exportProjectOutsourcingSqlForOldFinanceSystem(MultipartFile file, Integer year, Integer month) {
        String exportPath = exportExcelPath + File.separator + old_finance_outsourcing_table;
        genSqlFile(file, year, month, old_finance_outsourcing_table, old_outsourcing_stat_id, old_outsourcing_stat_field_name, old_project_table);
        JSONObject result = new JSONObject();
        result.put("url", exportPath);
        return result;
    }

    @Override
    public JSONObject exportProjectOutsourcingSqlForNewITSystem(MultipartFile file, Integer year, Integer month) {
        String exportPath = exportExcelPath + File.separator + new_it_outsourcing_table;
        genSqlFile(file, year, month, new_it_outsourcing_table, new_outsourcing_stat_id, new_outsourcing_stat_field_name, new_project_table);
        JSONObject result = new JSONObject();
        result.put("url", exportPath);
        return result;
    }

    private void genSqlFile(MultipartFile file, Integer year, Integer month, String tableName, String outsourcingStatId, String outsourcingFieldName, String projectTableName) {
        String exportDiskPath = filePathPrefix + exportExcelPath + File.separator + tableName + ".sql";
        File exportFile = new File(exportDiskPath);
        Workbook wb = null;
        JSONObject result = null;
        FileWriter fw = null;
        BufferedWriter bw = null;
        Integer currentColumnIndex;
        Integer currentMonth;
        if (month > 12) {
            month = 12;
        }
        if (month < 0) {
            month = 0;
        }
        try {
            wb = WorkbookFactory.create(file.getInputStream());
        } catch (Exception e) {
            Logger.error("读取文件失败");
            e.printStackTrace();
        }

        Sheet sheet = wb.getSheetAt(0);

        try {
            if (exportFile.exists()) {
                exportFile.delete();
            }
            exportFile.createNewFile();
            fw = new FileWriter(exportFile);
            bw = new BufferedWriter(fw);

            for (int i = 1; i <= month + 1; i++) {
                currentColumnIndex = i + 1;
                currentMonth = i;
                if (i > month) {
                    currentColumnIndex = 14;
                    currentMonth = 0;
                }
                bw.write("delete from " + tableName + " where year = " + year + " and " + outsourcingFieldName + " = " + outsourcingStatId + " and  month = " + currentMonth + ";\r\n");
                float fsmValue = 0f;
                for (int j = 2; j < sheet.getLastRowNum(); j++) {
                    Row row = sheet.getRow(j);
                    if (null == row) {
                        continue;
                    }
                    Cell cell = row.getCell(0);
                    if (null == cell) {
                        continue;
                    }
                    float value = MyCellUtils.formatFloatNumber((float) row.getCell(currentColumnIndex).getNumericCellValue() / 10000, 2);
                    if (cell.getStringCellValue().equalsIgnoreCase("debrion") || cell.getStringCellValue().equalsIgnoreCase("FSM") || cell.getStringCellValue().equalsIgnoreCase("FSM手游")) {
                        fsmValue += value;
                        continue;
                    }
                    bw.write("INSERT into " + tableName + "(project_id," + outsourcingFieldName + ",year,month,value)  SELECT id, " + outsourcingStatId + ", " + year + ", " + currentMonth + ", " + value + " from " + projectTableName + " where name = '" + row.getCell(0).getStringCellValue() + "';\r\n");
                }
                bw.write("INSERT into " + tableName + "(project_id," + outsourcingFieldName + ",year,month,value)  SELECT id, " + outsourcingStatId + ", " + year + ", " + currentMonth + ", " + fsmValue + " from " + projectTableName + " where name = 'Debrion';\r\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.error("解析文件失败");
        } finally {
            try {
                bw.flush();
                bw.close();
                fw.close();
            } catch (IOException e) {
                Logger.error("解析文件失败");
                e.printStackTrace();
            }
        }
    }


}