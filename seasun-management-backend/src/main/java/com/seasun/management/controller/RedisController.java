package com.seasun.management.controller;

import com.alibaba.fastjson.JSONObject;
import com.seasun.management.controller.response.CommonResponse;
import com.seasun.management.service.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RedisController {

    private static final Logger logger = LoggerFactory.getLogger(DataSyncController.class);

    @Autowired
    RedisService redisService;

    @RequestMapping(value = "/apis/auth/redis/data-fetch", method = RequestMethod.GET)
    public ResponseEntity<?> getRedisValue(@RequestParam String key) {
        logger.info("begin getRedisValue...");
        String result = redisService.get(key);
        logger.info("end getRedisValue...");
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/apis/auth/redis/message-publish", method = RequestMethod.POST)
    public ResponseEntity<?> publishRedisMessage(@RequestParam String channel, @RequestParam String content) {
        logger.info("begin getRedisValue...");
        redisService.publish(channel, content);
        logger.info("end getRedisValue...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

}
