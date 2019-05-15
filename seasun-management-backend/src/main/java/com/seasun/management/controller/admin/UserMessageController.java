package com.seasun.management.controller.admin;

import com.seasun.management.controller.response.CommonResponse;
import com.seasun.management.service.UserMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/apis/auth")
public class UserMessageController {
    private static final Logger logger = LoggerFactory.getLogger(UserMessageController.class);

    @Autowired
    UserMessageService userMessageService;

    @RequestMapping(value = "/user-message/performance-start-notice", method = RequestMethod.POST)
    public ResponseEntity<?> createNewPerformanceMessage(@RequestParam int year, @RequestParam int month) {
        logger.info("begin createNewPerformanceMessage...");
        userMessageService.createNewPerformanceMessage(year, month);
        logger.info("end createNewPerformanceMessage...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

    @RequestMapping(value = "/user-message/performance-end-notice", method = RequestMethod.POST)
    public ResponseEntity<?> createFinishPerformanceMessage(@RequestParam int year, @RequestParam int month) {
        logger.info("begin createFinishPerformanceMessage...");
        userMessageService.createFinishPerformanceMessage(year, month);
        logger.info("end createFinishPerformanceMessage...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

    @RequestMapping(value = "/user-message/finance-notice", method = RequestMethod.POST)
    public ResponseEntity<?> createNewFinanceDataMessage(@RequestParam String date) {
        logger.info("begin createNewFinanceDataMessage...");
        userMessageService.createNewFinanceDataMessage(date);
        logger.info("end createNewFinanceDataMessage...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }


}
