package com.seasun.management.controller.admin;

import com.seasun.management.controller.response.CommonResponse;
import com.seasun.management.model.UserFeedback;
import com.seasun.management.service.UserFeedbackService;
import com.seasun.management.vo.UserFeedbackQueryConditionVo;
import com.seasun.management.vo.UserFeedbackVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/apis/auth")
public class FeedbackController {
    private static final Logger logger = LoggerFactory.getLogger(FeedbackController.class);

    @Autowired
    UserFeedbackService userFeedbackService;

    @RequestMapping(value = "/feedback")
    public ResponseEntity<?> getFeedbackList(UserFeedbackQueryConditionVo vo) {
        logger.info("begin getFeedbackList...");
        UserFeedbackVo userFeedbackList = userFeedbackService.getUserFeedbackList(vo);
        logger.info("end getFeedbackList...");
        return ResponseEntity.ok(userFeedbackList);
    }

    @RequestMapping(value = "/feedback", method = RequestMethod.POST)
    public ResponseEntity<?> createFeedback(@RequestBody UserFeedback userFeedback) {
        logger.info("begin createFeedback...");
        userFeedbackService.createUserFeedback(userFeedback);
        logger.info("end createFeedback...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }
}
