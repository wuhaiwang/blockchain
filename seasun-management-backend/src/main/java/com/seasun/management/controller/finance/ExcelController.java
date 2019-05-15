package com.seasun.management.controller.finance;

import com.alibaba.fastjson.JSONObject;
import com.seasun.management.controller.response.CommonResponse;
import com.seasun.management.mapper.FnTaskMapper;
import com.seasun.management.model.FnProjectStatData;
import com.seasun.management.model.FnTask;
import com.seasun.management.service.FnProjectStatDataService;
import com.seasun.management.service.FnTaskService;
import com.seasun.management.service.excel.ExcelService;
import com.seasun.management.vo.FnProjectStatDetailDataVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class ExcelController {

    private static final Logger logger = LoggerFactory.getLogger(ExcelController.class);

    @Autowired
    private FnProjectStatDataService fnProjectStatDataService;

    @Autowired
    FnTaskMapper fnTaskMapper;

    @Autowired
    private ExcelService excelService;

    @Autowired
    private FnTaskService fnTaskService;

    // 启动导入任务
    @RequestMapping(value = "/apis/auth/excel/{type}", method = RequestMethod.POST)
    public ResponseEntity<?> importShareData(MultipartFile file, @RequestParam String date, @PathVariable String type) {
        logger.info("begin importShareData...");
        excelService.importExcel(file, date, type);
        logger.info("end importShareData...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

    // 删除导入任务
    @RequestMapping(value = "/apis/auth/excel/task/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        logger.info("begin deleteTask...");
        fnTaskMapper.deleteByPrimaryKey(id);
        logger.info("end deleteTask...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

    // 废弃导入任务
    @RequestMapping(value = "/apis/auth/excel/task/{id}", method = RequestMethod.PUT)
    public ResponseEntity<?> discardTask(@RequestBody JSONObject json, @PathVariable Long id) {
        logger.info("begin discardTask...");
        String reason = json.getString("reason") == null ? "user cancel" : json.getString("reason");
        String resultMessage = reason.length() > 250 ? reason.substring(0, 250) : reason;
        fnTaskMapper.discardTask(id, resultMessage);
        logger.info("end discardTask...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }


    // 查看导入任务
    @RequestMapping(value = "/apis/auth/excel/tasks", method = RequestMethod.GET)
    public ResponseEntity<?> getAllTasks(@RequestParam String type) {
        logger.info("begin getAllTasks...,type is:{}", type);
        List<FnTask> taskList = fnTaskService.getTaskByType(type);
        logger.info("end getAllTasks...");
        return ResponseEntity.ok(taskList);
    }


    // 查看项目明细
    @RequestMapping(value = "/apis/auth/fn-project-stat-detail-data", method = RequestMethod.GET)
    public ResponseEntity<?> getFnProjectStatDetailDataVoByCondition(@RequestParam Integer year, @RequestParam Long projectId) {
        logger.info("begin getFnProjectStatDetailDataVosByCondition...");
        FnProjectStatData param = new FnProjectStatData();
        param.setYear(year);
        param.setProjectId(projectId);
        List<FnProjectStatDetailDataVo> projectDetailData = fnProjectStatDataService.getFnProjectStatMapDataVosByCondition(param);
        logger.info("end getFnProjectStatDetailDataVosByCondition...");
        return ResponseEntity.ok(projectDetailData);
    }

}
