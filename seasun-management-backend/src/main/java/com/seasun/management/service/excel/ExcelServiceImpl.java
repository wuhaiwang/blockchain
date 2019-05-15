package com.seasun.management.service.excel;

import com.seasun.management.constant.ErrorCode;
import com.seasun.management.exception.DataImportException;
import com.seasun.management.exception.ParamException;
import com.seasun.management.helper.ExcelHelper;
import com.seasun.management.mapper.*;
import com.seasun.management.model.*;
import com.seasun.management.util.MyTokenUtils;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.*;

@Service
public class ExcelServiceImpl implements ExcelService {

    private static final Logger logger = LoggerFactory.getLogger(ExcelServiceImpl.class);

    @Value("${backup.excel.url}")
    private String backupExcelUrl;

    @Value("${file.sys.prefix}")
    private String filePathPrefix;

    @Autowired
    FnTaskMapper fnTaskMapper;

    @Resource(name = "share")
    ExcelTaskExecutor excelShareDataExecutor;

    @Resource(name = "project")
    ExcelTaskExecutor excelProjectDataExecutor;

    @Override
    public void importExcel(MultipartFile file, String date, String type) {

        Integer year = 0;
        Integer month = 0;
        try {
            if (date.length() == 4) {
                year = Integer.valueOf(date);
            } else {
                year = Integer.valueOf(date.split("-")[0]);
                month = Integer.valueOf(date.split("-")[1]);
            }
        } catch (NumberFormatException e) {
            logger.error("date format error! expected [ yyyy-mm | all ]");
            throw new ParamException("date format error!  expected [ yyyy-mm | all ]");
        }

        if (null != fnTaskMapper.selectUnfinishedOne()) {
            logger.error("file is processing , please wait...");
            throw new DataImportException(ErrorCode.FILE_IMPORT_BUSY_ERROR, "import failed: file is processing , please wait...");
        }

        if (!FnTask.Type.project_excel_import.equals(type) && !FnTask.Type.share_excel_import.equals(type) && !FnTask.Type.all.equals(type)) {
            logger.error("import failed: not a valid type...");
            throw new DataImportException(ErrorCode.FILE_IMPORT_TYPE_ERROR, "import failed: not a valid import type...");
        }

        // 登记
        FnTask task = new FnTask();
        task.setType(type);
        task.setCurrent(0);
        task.setTotal(0);
        task.setStatus(FnTask.TaskStatus.init);
        task.setCreateBy(MyTokenUtils.getCurrentUserName());
        logger.debug("sessionId is:{}", MyTokenUtils.getCurrentSessionId());
        task.setTag(MyTokenUtils.getCurrentSessionId());
        task.setFileName(file.getOriginalFilename());
        task.setCreateTime(new Date());
        task.setUpdateTime(new Date());
        fnTaskMapper.insert(task);
        logger.info("task is recorded, waiting for execute...");

        // 执行
        Map<String, Object> extParams = new HashMap<>();
        extParams.put("year", year);
        extParams.put("month", month);
        try {
            Workbook wb = null;
            try {
                wb = WorkbookFactory.create(file.getInputStream(), "876222");
            } catch (Exception e) {
                throw new DataImportException(ErrorCode.FILE_IMPORT_FILE_READ_ERROR, "文件读取失败");
            }

            // 因文件读取时间较长,若在此期间取消任务,则导入终止.
            ExcelHelper.checkTaskStatus(task.getId());

            // 更新状态
            task.setStatus(FnTask.TaskStatus.processing);
            Map<Long, Sheet> validShareSheetMap = excelShareDataExecutor.getValidSheet(wb, task, month);
            Map<Long, Sheet> validProjectSheetMap = excelProjectDataExecutor.getValidSheet(wb, task, month);
            int totalSize = validShareSheetMap.size() + validProjectSheetMap.size();
            if (totalSize == 0) {
                throw new DataImportException(ErrorCode.FILE_IMPORT_NO_VALID_SHEET_ERROR, "no valid sheet found...");
            }
            task.setTotal(totalSize);
            task.setUpdateTime(new Date());
            fnTaskMapper.updateByPrimaryKeySelective(task);

            // 处理share
            extParams.put("type", "share");
            excelShareDataExecutor.execute(validShareSheetMap, task, extParams);

            // 处理project
            extParams.put("type", "project");
            excelProjectDataExecutor.execute(validProjectSheetMap, task, extParams);

        } catch (DataImportException e) {
            logger.error(e.getMessage());
            throw new DataImportException(e.getCode(), e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new DataImportException(ErrorCode.FILE_IMPORT_COMMON_ERROR, e.getMessage());
        }

    }
}
