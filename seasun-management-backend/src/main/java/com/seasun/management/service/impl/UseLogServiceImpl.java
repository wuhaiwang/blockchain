package com.seasun.management.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.seasun.management.mapper.UseLogMapper;
import com.seasun.management.model.UseLog;
import com.seasun.management.service.UseLogService;
import com.seasun.management.vo.UserMessageConditionVo;
import com.seasun.management.vo.UseLogVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class UseLogServiceImpl implements UseLogService {

    @Autowired
    UseLogMapper useLogMapper;

    @Override
    public void createUseLog(Long userId, JSONObject jsonObject) {
        UseLog useLog = new UseLog();
        useLog.setUserId(userId);
        if (jsonObject.containsKey("system")) {
            useLog.setSystem(jsonObject.getString("system"));
        }
        if (jsonObject.containsKey("version")) {
            useLog.setVersion(jsonObject.getString("version"));
        }
        if (jsonObject.containsKey("model")) {
            useLog.setModel(jsonObject.getString("model"));
        }
        if (jsonObject.containsKey("appVersion")) {
            useLog.setAppVersion(jsonObject.getString("appVersion"));
        }
        if (jsonObject.containsKey("imei")) {
            useLog.setImei(jsonObject.getString("imei"));
        }
        useLog.setCreateTime(new Date());
        useLogMapper.insertSelective(useLog);
    }

    @Override
    public List<UseLogVo> getUseLogList(Long currentPage, Long pageSize) {
        UserMessageConditionVo condition = new UserMessageConditionVo();
        condition.setCurrentPage(currentPage);
        condition.setPageSize(pageSize);
        return useLogMapper.selectByCondition(condition);
    }
}
