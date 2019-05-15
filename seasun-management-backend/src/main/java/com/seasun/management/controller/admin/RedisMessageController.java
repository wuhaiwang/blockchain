package com.seasun.management.controller.admin;

import com.alibaba.fastjson.JSON;
import com.seasun.management.constant.RedisConstant;
import com.seasun.management.controller.response.AddRecordResponse;
import com.seasun.management.controller.response.CommonResponse;
import com.seasun.management.mapper.UserMessageMapper;
import com.seasun.management.model.UserMessage;
import com.seasun.management.service.UserMessageService;
import com.seasun.management.service.redis.RedisService;
import com.seasun.management.vo.UserMessageConditionVo;
import com.seasun.management.vo.UserMessageVo;
import com.seasun.management.vo.UserMiniVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RedisMessageController {

    private static final Logger logger = LoggerFactory.getLogger(RedisMessageController.class);


    @Autowired
    private RedisService redisService;


    @Autowired
    private UserMessageMapper userMessageMapper;


    @Autowired
    private UserMessageService userMessageService;


    @RequestMapping(value = "/apis/auth/user-message", method = RequestMethod.GET)
    public ResponseEntity<?> getUserMessage(UserMessageConditionVo vo) {
        logger.info("begin getAllUserMessage...");
        UserMessageVo userMessages = userMessageService.getMessages(vo);
        logger.info("end getAllUserMessage...");
        return ResponseEntity.ok(userMessages);
    }

    @RequestMapping(value = "/apis/auth/test/user-message", method = RequestMethod.POST)
    public ResponseEntity<?> addUserMessage(@RequestBody UserMessage userMessage) {
        logger.info("begin addUserMessage...");
        UserMessage newRecord = userMessageService.add(userMessage);
        logger.info("end addUserMessage...");
        return ResponseEntity.ok(new AddRecordResponse(0, "success", newRecord.getId()));
    }

    @RequestMapping(value = "/apis/auth/test/user-message/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteUserMessage(@PathVariable Long id) {
        logger.info("begin deleteUserMessage...");
        userMessageService.deleteByPk(id);
        logger.info("end deleteUserMessage...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

    @RequestMapping(value = "/apis/auth/test/user-message-push/{id}", method = RequestMethod.POST)
    public ResponseEntity<?> pushUserMessage(@PathVariable Long id) {
        logger.info("begin getAllUserMessage...");
        UserMessage userMessage = userMessageMapper.selectByPrimaryKey(id);
        redisService.publish(RedisConstant.it_channel, JSON.toJSONString(userMessage));
        logger.info("end getAllUserMessage...");
        return ResponseEntity.ok(new CommonResponse(0, "success"));
    }

    @RequestMapping(value = "/apis/auth/test/redis/user-sessions", method = RequestMethod.GET)
    public ResponseEntity<?> getOnlineUserSession(@RequestParam String host) {
        logger.info("begin getOnlineUserSession...");
        List<UserMiniVo> onlineUserSessions = redisService.getOnlineUserFromPushServer(host);
        logger.info("end getOnlineUserSession...");
        return ResponseEntity.ok(onlineUserSessions);
    }

    @RequestMapping(value = "/apis/auth/test/redis/push-servers", method = RequestMethod.GET)
    public ResponseEntity<?> getPushServers() {
        logger.info("begin getPushServers...");
        List<String> pushServers = redisService.getPushServers();
        logger.info("end getPushServers...");
        return ResponseEntity.ok(pushServers);
    }

}
