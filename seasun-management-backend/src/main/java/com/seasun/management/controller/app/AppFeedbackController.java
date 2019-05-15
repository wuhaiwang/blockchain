package com.seasun.management.controller.app;

import com.seasun.management.controller.response.CommonResponse;
import com.seasun.management.model.UserFeedback;
import com.seasun.management.service.UserFeedbackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/apis/auth/app")
public class AppFeedbackController {
    private static final Logger logger = LoggerFactory.getLogger(AppFeedbackController.class);

    @Autowired
    UserFeedbackService userFeedbackService;

    @RequestMapping(value = "/feedback", method = RequestMethod.POST)
    public ResponseEntity<?> createAppFeedback(@RequestBody UserFeedback userFeedback) {
        logger.info("begin createAppFeedback...");
        userFeedbackService.createUserFeedback(userFeedback);
        logger.info("end createAppFeedback...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }
}
