package com.seasun.management.service.excel;

import com.seasun.management.model.FnTask;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.Map;

public interface ExcelTaskExecutor {

    /**
     * 执行导入任务
     *
     * @param sheetMap  待导入的文件
     * @param fnTask    任务标示
     * @param extParams 其他参数
     */
    void execute(Map<Long, Sheet> sheetMap, FnTask fnTask, Map<String, Object> extParams);

    /**
     * 获取有效的sheet
     *
     * @param wb
     * @param fnTask
     * @return
     */
    Map<Long, Sheet> getValidSheet(Workbook wb, FnTask fnTask, int month);


}
