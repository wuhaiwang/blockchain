package com.seasun.management.controller.app;

import com.seasun.management.constant.ErrorCode;
import com.seasun.management.constant.ErrorMessage;
import com.seasun.management.controller.response.CommonAppResponse;
import com.seasun.management.controller.response.CommonResponse;
import com.seasun.management.dto.ContactGroupUserDto;
import com.seasun.management.model.UserContactGroup;
import com.seasun.management.service.app.UserContactGroupService;
import com.seasun.management.vo.AppUserContactGroupVo;
import com.seasun.management.vo.ContactsAppVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/apis/auth/app")
@RestController
public class AppUserContactGroupController {

    private Logger logger = LoggerFactory.getLogger(AppUserContactGroupController.class);

    @Autowired
    private UserContactGroupService userContactGroupService;

    @RequestMapping(value = "/user-contact-groups", method = RequestMethod.GET)
    private ResponseEntity<?> getUserContactGroups() {
        logger.info("begin getUserContactGroups...");
        List<AppUserContactGroupVo> result = userContactGroupService.getUserContactGroups();
        logger.info("end getUserContactGroups...");
        return ResponseEntity.ok(new CommonAppResponse(0, result));
    }

    @RequestMapping(value = "/user-contact-group/{id}", method = RequestMethod.PUT)
    private ResponseEntity<?> updateUserContactGroup(@PathVariable Long id, @RequestBody AppUserContactGroupVo userContactGroup) {
        logger.info("begin updateUserContactGroup...");
        int result = userContactGroupService.updateUserContactGroup(id, userContactGroup);
        logger.info("end updateUserContactGroup...");
        if (result > 0) {
            return ResponseEntity.ok(new CommonResponse());
        }
        return ResponseEntity.ok(new CommonResponse(ErrorCode.PARAM_ERROR, ErrorMessage.PARAM_ERROR_MESSAGE));
    }

    @RequestMapping(value = "/user-contact-group/{id}", method = RequestMethod.DELETE)
    private ResponseEntity<?> deleteUserContactGroup(@PathVariable Long id) {
        logger.info("begin deleteUserContactGroup...");
        int result = userContactGroupService.deleteUserContactGroup(id);
        logger.info("end deleteUserContactGroup...");
        if (result > 0) {
            return ResponseEntity.ok(new CommonResponse());
        }
        return ResponseEntity.ok(new CommonResponse(ErrorCode.PARAM_ERROR, ErrorMessage.PARAM_ERROR_MESSAGE));
    }

    @RequestMapping(value = "/user-contact-group", method = RequestMethod.POST)
    private ResponseEntity<?> insertUserContactGroup(@RequestBody AppUserContactGroupVo appUserContactGroupVo) {
        logger.info("begin insertUserContactGroup...");
        int result = userContactGroupService.insertUserContactGroup(appUserContactGroupVo);
        logger.info("end insertUserContactGroup...");
        if (result > 0) {
            return ResponseEntity.ok(new CommonResponse());
        }
        return ResponseEntity.ok(new CommonResponse(ErrorCode.USER_INVALID_OPERATE_ERROR, "群组名重复，请重新输入。"));
    }

    @RequestMapping(value = "/user-contact-group", method = RequestMethod.GET)
    private ResponseEntity<?> getUserContactGroupInfo(@RequestParam Long id) {
        logger.info("begin getUserContactGroupInfo...");
        AppUserContactGroupVo result = userContactGroupService.getUserContactGroupInfo(id);
        logger.info("end getUserContactGroupInfo...");
        return ResponseEntity.ok(new CommonAppResponse(0, result));
    }
}
