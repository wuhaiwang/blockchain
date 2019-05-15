package com.seasun.management.controller.perf;

import com.seasun.management.service.OperateLogService;
import com.seasun.management.vo.LogQueryConditionVo;
import com.seasun.management.vo.OperateLogVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LogController {
    private static final Logger logger = LoggerFactory.getLogger(LogController.class);

    @Autowired
    OperateLogService operateLogService;

    @RequestMapping(value = "/apis/auth/log", method = RequestMethod.GET)
    public ResponseEntity<?> queryOperateLogByCondition(LogQueryConditionVo logQueryConditionVo) {
        logger.info("begin queryOperateLogByCondition...");
        OperateLogVo result = operateLogService.selectByCondition(logQueryConditionVo);
        logger.info("end queryOperateLogByCondition...");
        return ResponseEntity.ok(result);
    }
}
