package com.seasun.management.controller.app;

import com.seasun.management.controller.response.CommonResponse;
import com.seasun.management.model.UserGesture;
import com.seasun.management.service.UserGestureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/apis/auth/app")
public class AppUserGestureController {
    private static final Logger logger = LoggerFactory.getLogger(AppUserGestureController.class);

    @Autowired
    UserGestureService userGestureService;

    @RequestMapping(value = "/gesture", method = RequestMethod.POST)
    public ResponseEntity<?> addUserGesture(@RequestBody UserGesture userGesture) {
        logger.info("begin addUserGesture...");
        userGestureService.modifyUserGesture(userGesture);
        logger.info("end addUserGesture...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

    @RequestMapping(value = "/gesture", method = RequestMethod.PUT)
    public ResponseEntity<?> updateUserGesture(@RequestBody UserGesture userGesture) {
        logger.info("begin modifyUserGesture...");
        userGestureService.modifyUserGesture(userGesture);
        logger.info("end modifyUserGesture...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }
}
