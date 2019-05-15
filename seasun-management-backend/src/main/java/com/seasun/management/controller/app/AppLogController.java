package com.seasun.management.controller.app;

import com.seasun.management.controller.response.CommonResponse;
import com.seasun.management.model.OperateLog;
import com.seasun.management.service.CrashLogService;
import com.seasun.management.service.OperateLogService;
import com.seasun.management.util.MyTokenUtils;
import com.seasun.management.vo.CrashLogVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class AppLogController {
    private static final Logger logger = LoggerFactory.getLogger(AppLogController.class);

    @Autowired
    CrashLogService crashLogService;

    @Autowired
    OperateLogService operateLogService;

    @RequestMapping(value = "/pub/crash-log", method = RequestMethod.POST)
    public ResponseEntity<?> createAppCrashLog(@RequestBody CrashLogVo crashLogVo) {
        logger.info("begin createAppCrashLog...");
        crashLogService.createCrashLog(crashLogVo);
        logger.info("end createAppCrashLog...");
        return ResponseEntity.ok(new CommonResponse(0,"success"));
    }

    @RequestMapping(value = "/apis/auth/app/operate-log", method = RequestMethod.POST)
    public ResponseEntity<?> createAppOperateLog(@RequestBody OperateLog operateLog) {
        logger.info("begin createAppOperateLog...");
        operateLog.setCreateTime(new Date());
        operateLog.setOperateId(MyTokenUtils.getCurrentUserId());
        operateLog.setChannel("mobile");
        operateLogService.addLog(operateLog);
        logger.info("end createAppOperateLog...");
        return ResponseEntity.ok(new CommonResponse(0,"success"));
    }


}
