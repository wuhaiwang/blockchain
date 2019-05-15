package com.seasun.management.controller.kingsoftLife;

import com.alibaba.fastjson.JSONObject;
import com.seasun.management.constant.ErrorCode;
import com.seasun.management.controller.response.CommonResponse;
import com.seasun.management.service.kingsoftLife.KsUserService;
import com.seasun.management.vo.KsLifeCommonVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "apis/auth/app/ks-life")
public class KsUserController {

    private static final Logger logger = LoggerFactory.getLogger(KsUserController.class);

    @Autowired
    private KsUserService ksUserService;

    @RequestMapping(value = "/user-seat-no")
    public ResponseEntity<?> getUserSeatNo() {
        logger.info("begin getUserSeatNo...");
        Object seatNoObj = ksUserService.getUserSeatNo(null);
        if(seatNoObj==null){
            return ResponseEntity.ok(new CommonResponse(ErrorCode.KS_USER_SEAT_NO_EXCEPTION,"数据获取失败"));
        }
        logger.info("end getUserSeatNo...");
        return ResponseEntity.ok(new CommonResponse(0,"success",seatNoObj));
    }

    @RequestMapping(value = "/user-seat-unbind", method = RequestMethod.POST)
    public ResponseEntity<?> unbindUserSeatNo() {
        logger.info("begin unbindUserSeatNo...");
        KsLifeCommonVo result = ksUserService.unbindSeatNo();
        logger.info("end unbindUserSeatNo...");
        return ResponseEntity.ok(Boolean.parseBoolean(result.getSuccess()) ? new CommonResponse(0, "success", result.getData()) : new CommonResponse(ErrorCode.USER_INVALID_OPERATE_ERROR, result.getMsg(), result.getData()));
    }

    @RequestMapping(value = "/user-seat-bind", method = RequestMethod.POST)
    public ResponseEntity<?> bindUserSeatNo(@RequestBody JSONObject jsonObject) {
        logger.info("begin bindUserSeatNo...");
        KsLifeCommonVo result = ksUserService.bindSeatNo(jsonObject.getString("seatNo"));
        logger.info("edn bindUserSeatNo...");
        return ResponseEntity.ok(Boolean.parseBoolean(result.getSuccess()) ? new CommonResponse(0, "success", result.getData()) : new CommonResponse(ErrorCode.USER_INVALID_OPERATE_ERROR, result.getMsg(), result.getData()));
    }

}
