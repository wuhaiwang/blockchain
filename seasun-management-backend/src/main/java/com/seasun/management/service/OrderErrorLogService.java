package com.seasun.management.service;

public interface OrderErrorLogService {

    void insertOrderErrorLog(Long userId, String clientFullInfo, String response, String errorMessage);
}
