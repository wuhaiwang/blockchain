package com.seasun.management.controller.admin;

import com.alibaba.fastjson.JSONObject;
import com.seasun.management.service.SqlFileGenerateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/apis/auth")
public class DataImportController {

    private static final Logger logger = LoggerFactory.getLogger(DataImportController.class);

    @Autowired
    private SqlFileGenerateService projectSqlService;

    @RequestMapping(value = "/project-outsourceing-sql-file", method = RequestMethod.POST)
    public ResponseEntity<?> exportProjectSqlByExcel(MultipartFile file, @RequestParam Integer year, @RequestParam Integer month, @RequestParam String type) {
        logger.info("begin exportProjectOutsourcingSqlFileByExcel...");

        JSONObject result = null;
        if ("old".equals(type)) {
            result = projectSqlService.exportProjectOutsourcingSqlForOldFinanceSystem(file, year, month);
        } else {
            result = projectSqlService.exportProjectOutsourcingSqlForNewITSystem(file, year, month);
        }

        logger.info("end exportProjectOutsourcingSqlFileByExcel...");
        if (result != null) {
            result.put("code", 0);
        } else {
            logger.error("exportProjectOutsourcingSqlByExcel error,because excelData is error");
            result = new JSONObject();
            result.put("code", 708);
            result.put("message", "文件解析异常");
        }
        return ResponseEntity.ok(result);
    }
}
