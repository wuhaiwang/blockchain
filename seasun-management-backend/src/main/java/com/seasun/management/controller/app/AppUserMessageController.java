package com.seasun.management.controller.app;

import com.alibaba.fastjson.JSONObject;
import com.seasun.management.controller.response.CommonAppResponse;
import com.seasun.management.model.UserMessage;
import com.seasun.management.model.UserPsychologicalConsultation;
import com.seasun.management.service.PsychologicalConsultantService;
import com.seasun.management.service.UserMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping(value = "/apis/auth/app")
public class AppUserMessageController {

    private static final Logger logger = LoggerFactory.getLogger(AppUserMessageController.class);

    @Autowired
    UserMessageService userMessageService;

    @Autowired
    private PsychologicalConsultantService psychologicalConsultantService;

    // todo: linjinghua, rename url to /user-messages
    @RequestMapping(value = "/user-message-all", method = RequestMethod.GET)
    public ResponseEntity<?> getAppUserMessages(@RequestParam(required = false) Boolean readFlag) {
        logger.info("begin getAppUserMessages...");
        List<UserMessage> userMessages = userMessageService.getUserMessages(readFlag);
        logger.info("end getAppUserMessages...");
        return ResponseEntity.ok(new CommonAppResponse(0, userMessages));
    }

    // todo: linjinghua, rename url to /user-message，move query cond 'new' to request params
    @RequestMapping(value = "/user-message-new", method = RequestMethod.GET)
    public ResponseEntity<?> getAppNewUserMessages(@RequestParam(required = false) Long versionId) {
        logger.info("begin getAppNewUserMessages...");
        List<UserMessage> userMessages = userMessageService.getNewUserMessages(versionId);
        logger.info("end getAppNewUserMessages...");
        return ResponseEntity.ok(new CommonAppResponse(0, userMessages));
    }

    // todo: linjinghua, rename url to /user-message，move query cond 'read' to request params
    @RequestMapping(value = "/user-message-read", method = RequestMethod.POST)
    public ResponseEntity<?> setAppUserMessageReadFlag(@RequestBody JSONObject jsonObject) {
        logger.info("begin setAppUserMessageReadFlag...");
        if (jsonObject.containsKey("id")) {
            Long id = jsonObject.getLong("id");
            userMessageService.setUserMessageReadFlagById(id);
        } else if (jsonObject.containsKey("ids")) {
            userMessageService.setUserMessageReadFlagByIds((List<Long>)jsonObject.get("ids"));
        } else if (jsonObject.containsKey("allReadFlag") && jsonObject.getBoolean("allReadFlag")) {
            userMessageService.setUserAllMessageReadFlag(null);
        } else {
            String location = jsonObject.getString("location");
            userMessageService.setUserMessageReadFlagByLocation(location);
        }
        logger.info("end setAppUserMessageReadFlag...");
        return ResponseEntity.ok(new CommonAppResponse(0, "success"));
    }

    @RequestMapping(value = "/user-psychological-consultation", method = RequestMethod.GET)
    public ResponseEntity<?> getUserPsychologicalConsultationPassword(@RequestParam(required = false) Integer year, @RequestParam(required = false) Integer month, @RequestParam(required = false) Long userId) {
        logger.info("begin getUserPsychologicalConsultationPassword...");
        UserPsychologicalConsultation result = psychologicalConsultantService.getUserPsychologicalConsultationPassword(year, month, userId);
        logger.info("end getUserPsychologicalConsultationPassword...");
        return ResponseEntity.ok(new CommonAppResponse(0, result));
    }
}
