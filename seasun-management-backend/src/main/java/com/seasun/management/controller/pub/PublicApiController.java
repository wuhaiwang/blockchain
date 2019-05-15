package com.seasun.management.controller.pub;

import com.alibaba.fastjson.JSONObject;
import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;
import com.seasun.management.controller.response.CommonAppResponse;
import com.seasun.management.mapper.UserMapper;
import com.seasun.management.service.UserOrderService;
import com.seasun.management.weixin.MyWXPayConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/apis/auth/pub")
public class PublicApiController {

    private static final Logger logger = LoggerFactory.getLogger(PublicApiController.class);

    @Autowired
    UserMapper userMapper;

    @Autowired
    UserOrderService userOrderService;

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public ResponseEntity<?> getPubUsers(@RequestParam String type, @RequestParam(required = false) String city) {
        logger.info("begin getPubUsers...");
        Object users = new ArrayList<>();
        if ("checkIn".equalsIgnoreCase(type)) {
            users = userMapper.selectAllWithPubByCity(city);
        }
        logger.info("end getPubUsers...");
        return ResponseEntity.ok(new CommonAppResponse(0, users));
    }



}

