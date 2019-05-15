package com.seasun.management.handler;

import com.alibaba.fastjson.JSON;
import com.seasun.management.constant.SessionAttribute;
import com.seasun.management.model.FnTask;
import org.apache.catalina.manager.util.SessionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.ArrayList;


@Service
public class ExcelImportHandler implements WebSocketHandler {

    private static final ArrayList<WebSocketSession> sessions = new ArrayList<>();
    private static final Logger logger = LoggerFactory.getLogger(ExcelImportHandler.class);


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        logger.debug("connect to the webSocket success......");
        sessions.add(session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {

    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        if (session.isOpen()) {
            session.close();
        }
        logger.debug("webSocket connection closed......");
        sessions.remove(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        logger.debug("webSocket connection closed......");
        sessions.remove(session);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }


    // 向指定用户推送消息
    public void sendMessage(TextMessage message) {
        FnTask fnTask = JSON.parseObject(message.getPayload(), FnTask.class);

        for (WebSocketSession session : sessions) {
            if (session.isOpen() && session.getAttributes().get(SessionAttribute.sessionId).equals(fnTask.getTag())) {
                logger.info("message={}", message.getPayload());
                try {
                    session.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.error("send message failed,{}", e.getMessage());
                }
            }
        }
    }
}
