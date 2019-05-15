package com.seasun.management.controller.kingsoftLife;

import com.seasun.management.controller.response.CommonResponse;
import com.seasun.management.service.kingsoftLife.KsAuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("apis/auth/app/ks-life/")
public class KsAuthController {

    @Autowired
    private KsAuthService ksAuthService;

    private static final Logger logger = LoggerFactory.getLogger(KsAuthController.class);

    @RequestMapping(value = "/account-verification", method = RequestMethod.GET)
    public ResponseEntity<?> verifyKsPermission(String loginId) {
        logger.info("begin verify ks permission");
        Boolean existFlag = ksAuthService.accountExists(loginId);
        logger.info("end verify ks permission");
        return ResponseEntity.ok(new CommonResponse(0, "", existFlag));
    }

    @RequestMapping(value = "/bus-login", method = RequestMethod.GET)
    public ResponseEntity<?> busLogin() {
        logger.info("begin busLogin");
        String redirectUrl = ksAuthService.busLogin();
        logger.info("end busLogin");
        return ResponseEntity.ok(new CommonResponse(0, "", redirectUrl));
    }

}
