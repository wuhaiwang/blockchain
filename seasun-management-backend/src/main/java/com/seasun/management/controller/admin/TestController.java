package com.seasun.management.controller.admin;

import com.alibaba.fastjson.JSONObject;
import com.seasun.management.controller.response.CommonResponse;
import com.seasun.management.service.PsychologicalService;
import com.seasun.management.service.TestService;
import com.seasun.management.task.UserPerformanceTask;
import com.seasun.management.vo.UserCountCheckVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@RestController
public class TestController {
    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @Autowired
    private TestService testService;

    @Autowired
    PsychologicalService psychologicalService;

    @RequestMapping(value = "/apis/auth/test/user-psychological", method = RequestMethod.POST)
    public ResponseEntity<?> generateNewPyPassword() {
        logger.info("begin cleanUserPerformance...");
        psychologicalService.generateNewPassword();
        logger.info("end cleanUserPerformance...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }


    @RequestMapping(value = "/apis/auth/test/user-performance", method = RequestMethod.DELETE)
    public ResponseEntity<?> cleanUserPerformance(@RequestParam Integer year, @RequestParam Integer month) {
        logger.info("begin cleanUserPerformance...");
        testService.cleanUserPerformance(year, month);
        logger.info("end cleanUserPerformance...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

    @RequestMapping(value = "/apis/auth/test/user-salary-change", method = RequestMethod.DELETE)
    public ResponseEntity<?> cleanUserSalaryChange(@RequestParam Integer year, @RequestParam Integer quarter) {
        logger.info("begin cleanUserSalaryChange...");
        testService.cleanUserSalaryChange(year, quarter);
        logger.info("end cleanUserSalaryChange...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

    @RequestMapping(value = "/apis/auth/test/user-performance", method = RequestMethod.POST)
    public ResponseEntity<?> batchCreateUserPerformance(@RequestBody JSONObject jsonObject) {
        logger.info("begin batchCreateUserPerformance...");
        Integer year = jsonObject.getIntValue("year");
        Integer month = jsonObject.getIntValue("month");
        Integer quarter = jsonObject.getIntValue("quarter");
        testService.batchCreateUserPerformance(year, month, quarter);
        logger.info("end batchCreateUserPerformance...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }


    @RequestMapping(value = "/apis/auth/test/sync-fixed-outnumber", method = RequestMethod.POST)
    public ResponseEntity<?> syncFixedOutnumber(@RequestBody JSONObject jsonObject) {
        logger.info("begin syncFixedOutnumber...");
        testService.syncFixedOutnumber(jsonObject);
        logger.info("end syncFixedOutnumber...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

    @RequestMapping(value = "/apis/auth/test/sync-sched-data", method = RequestMethod.POST)
    public ResponseEntity<?> syncSchedData(@RequestBody JSONObject jsonObject) {
        logger.info("begin syncSchedData...");
        testService.syncSchedData(jsonObject);
        logger.info("end syncSchedData...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

    @RequestMapping(value = "/apis/auth/test/sync-outsourcing-data", method = RequestMethod.POST)
    public ResponseEntity<?> syncOutsourcingData(@RequestBody JSONObject jsonObject) {
        logger.info("begin syncOutsourcingData...");
        testService.syncOutsourcingData(jsonObject);
        logger.info("end syncOutsourcingData...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

    @RequestMapping(value = "/apis/auth/test/check-hr-user-count", method = RequestMethod.GET)
    public ResponseEntity<?> checkHrUserCount(@RequestParam(name = "city", required = false) String city, @RequestParam(name = "workGroupId", required = false) Long workGroupId) {
        logger.info("begin checkHrUserCount...");
        UserCountCheckVo userCountCheckVo = testService.checkHrUserCount(city, workGroupId);
        logger.info("end checkHrUserCount...");
        return ResponseEntity.ok(userCountCheckVo);
    }

    @RequestMapping(value = "/apis/auth/test/xi-mi-users", method = RequestMethod.GET)
    public ResponseEntity<?> getXimiUsers() {
        return ResponseEntity.ok(testService.getXimiUsers());
    }


    @RequestMapping(value = "/apis/auth/test/attachment/{id}", method = RequestMethod.GET)
    public void getAttachment(HttpServletResponse response, @PathVariable int id) {
        logger.info("begin getAttachment...");
        response.setHeader("Content-Disposition", "attachment; filename=" + getAttachmentName(id));
        response.setHeader("Content-Type", "application/octet-stream");
        response.setHeader("X-Accel-Redirect", getAttachmentPath(id) + getAttachmentName(id));
        logger.info("end getAttachment...");
    }

    @RequestMapping(value = "/apis/auth/test/html", method = RequestMethod.GET)
    public void getHtml(HttpServletResponse response, HttpServletRequest request) {

        String targetUrl = "http://www.baidu.com";
        try {
            response.sendRedirect(targetUrl);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("源地址{0}读取失败:{1}", targetUrl, e.getMessage());
        }

    }

    @RequestMapping(value = "/test/pub/update-task", method = RequestMethod.PUT)
    public void updateTask() {
        UserPerformanceTask.setPerfNotifyTaskIntervalTime();
    }

    @RequestMapping(value = "/test/pub/sqlServer", method = RequestMethod.GET)
    public ResponseEntity<?> testSqlServer() {
        return ResponseEntity.ok(testService.testSqlServer());
    }


    private String getAttachmentName(int id) {
        return "repr_proj_data.sql";
    }

    private String getAttachmentPath(int id) {
        return "/attachment/";
    }
}
