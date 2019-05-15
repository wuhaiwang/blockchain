package com.seasun.management.service.excel;

import com.alibaba.fastjson.JSON;
import com.seasun.management.constant.ErrorCode;
import com.seasun.management.exception.DataImportException;
import com.seasun.management.exception.ExcelDataAccessException;
import com.seasun.management.handler.ExcelImportHandler;
import com.seasun.management.helper.ExcelHelper;
import com.seasun.management.mapper.FnTaskMapper;
import com.seasun.management.model.FnTask;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.socket.TextMessage;

import java.io.File;
import java.util.Date;
import java.util.Map;
import java.util.Set;

public abstract class AbstractExcelTaskExecutor implements ExcelTaskExecutor {

    private static final Logger logger = LoggerFactory.getLogger(ExcelShareDataExecutorImpl.class);

    @Autowired
    FnTaskMapper fnTaskMapper;


    @Autowired
    ExcelImportHandler excelImportHandler;

    /**
     * 判断cell是否有效
     *
     * @param cell
     * @param validRowIndexes
     * @param validColumnIndexes
     * @return
     */
    public abstract boolean isValidCell(Cell cell, Set<Integer> validRowIndexes, Set<Integer> validColumnIndexes);

    public abstract Map<Long, Sheet> getValidSheet(Workbook wb, FnTask fnTask, int month);

    /**
     * 处理单个sheet
     *
     * @param sheet
     * @param fnTask
     */
    public abstract String run(Sheet sheet, Long sheetIdentifyId, FnTask fnTask, int year, int month);

    @Async
    @Override
    public void execute(Map<Long, Sheet> sheetMap, FnTask fnTask, Map<String, Object> extParams) {
        logger.info("begin process excel: {} ...", fnTask.getId());

        // 清除历史导入日志
        cleanIfExceed(1, (String) extParams.get("type"));

        // month为0，则导入所有月份。否则，只导入指定月份的数据。
        int month = (Integer) extParams.get("month");
        int year = (Integer) extParams.get("year");

        try {
            for (Map.Entry<Long, Sheet> entry : sheetMap.entrySet()) {
                logger.info("executor processing sheet {}: currentIndex={},total={}", entry.getValue().getSheetName(), fnTask.getCurrent(), sheetMap.size());

                // 导入过程中,若在此期间取消任务,则取消导入.
                ExcelHelper.checkTaskStatus(fnTask.getId());

                // 处理单个sheet,记录忽略信息
                run(entry.getValue(), entry.getKey(), fnTask, year, month);

                // 更新进度
                Sheet sheet = entry.getValue();
                updateProgress(fnTask, sheet.getSheetName());
            }
        }
        catch (DataImportException e) {
            e.printStackTrace();
            logger.error("executor failed,dataImportException：{}", e.getMessage());
            recordFailedTask(fnTask.getId(), e.getMessage());
            throw new DataImportException(e.getCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            e.printStackTrace();
            logger.error("executor failed,exception：{}", e.getMessage());
            recordFailedTask(fnTask.getId(), e.getMessage());
            throw new DataImportException(ErrorCode.FILE_IMPORT_COMMON_ERROR, "executor failed : fail to process sheet...");
        }
        logger.info("end process excel: {} ...");
    }

    // 进入executor后，通过task管理状态
    private void recordFailedTask(Long id, String message) {
        message = message == null ? "" : message;
        String resultMessage = message.length() > 250 ? message.substring(0, 250) : message;
        FnTask tmp = new FnTask();
        tmp.setId(id);
        tmp.setStatus(FnTask.TaskStatus.failed);
        tmp.setUpdateTime(new Date());
        tmp.setResultMessage(resultMessage);
        fnTaskMapper.updateByPrimaryKeySelective(tmp);
        String msg = JSON.toJSONStringWithDateFormat(fnTaskMapper.selectByPrimaryKey(id), "yyyy-MM-dd kk:mm:ss");
        excelImportHandler.sendMessage(new TextMessage(msg));
    }

    // 清除导入日志
    private void cleanIfExceed(int maxFileNumber, String type) {
        File logDir;
        try {
            logDir = new File(ExcelHelper.ERR_LOG_DIR + type + "/");
        } catch (NullPointerException e) {
            throw new DataImportException(ErrorCode.DATA_SYNC_ERROR, "文件创建失败");
        }
        File[] files = logDir.listFiles();
        if (files != null && files.length > maxFileNumber) {
            ExcelHelper.deleteAll(logDir);
        }
    }


    // 更新任务进度
    private void updateProgress(FnTask fnTask, String sheetName) {
        FnTask fnTaskCond = new FnTask();
        fnTaskCond.setId(fnTask.getId());

        int current = fnTask.getCurrent() + 1;
        fnTaskCond.setCurrent(current);
        fnTaskCond.setProcessMessage("current sheet: " + sheetName);
        fnTaskCond.setUpdateTime(new Date());
        fnTask.setCurrent(current);
        fnTask.setProcessMessage("current sheet: " + sheetName);
        fnTask.setUpdateTime(new Date());
        // 处理完成
        if (current == fnTask.getTotal() && fnTask.getTotal() > 0) {
            fnTaskCond.setStatus(FnTask.TaskStatus.done);
            fnTaskCond.setProcessMessage("finished");
            fnTaskCond.setResultMessage("success");
            fnTaskCond.setUpdateTime(new Date());
            fnTask.setStatus(FnTask.TaskStatus.done);
            fnTask.setProcessMessage("finished");
            fnTask.setResultMessage("success");
            fnTask.setUpdateTime(new Date());
        }

        fnTaskMapper.updateByPrimaryKeySelective(fnTaskCond);
        excelImportHandler.sendMessage(new TextMessage(JSON.toJSONStringWithDateFormat(fnTask, "yyyy-MM-dd kk:mm:ss")));

    }
}
