package com.seasun.management.service.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

public class RedisMessageListener implements MessageListener {

    private static final Logger logger = LoggerFactory.getLogger(RedisMessageListener.class);


    @Override
    public void onMessage(Message message, byte[] pattern) {
        logger.debug("receiver message:{}", message);
    }
}
