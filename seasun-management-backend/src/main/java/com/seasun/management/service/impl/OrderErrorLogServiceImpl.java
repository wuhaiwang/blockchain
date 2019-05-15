package com.seasun.management.service.impl;

import com.seasun.management.mapper.OrderErrorLogMapper;
import com.seasun.management.model.OrderErrorLog;
import com.seasun.management.service.OrderErrorLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class OrderErrorLogServiceImpl implements OrderErrorLogService {

    @Autowired
    private OrderErrorLogMapper orderErrorLogMapper;

    @Override
    public void insertOrderErrorLog(Long userId, String clientFullInfo, String response, String errorMessage) {
        OrderErrorLog insert = new OrderErrorLog();
        insert.setUserId(userId);
        insert.setClientFullInfo(clientFullInfo);
        if (response != null && response.length() >= 1024) {
            response = response.substring(0, 1024);
        }
        if (errorMessage != null && errorMessage.length() >= 1024) {
            errorMessage = errorMessage.substring(0, 1024);
        }
        insert.setErrorMessage(errorMessage);
        insert.setResponse(response);
        insert.setCreateTime(new Date());
        orderErrorLogMapper.insert(insert);
    }
}
