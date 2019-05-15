package com.seasun.management.service;

import com.seasun.management.model.OperateLog;
import com.seasun.management.util.MyTokenUtils;
import com.seasun.management.vo.LogQueryConditionVo;
import com.seasun.management.vo.OperateLogVo;

import java.util.List;

public interface OperateLogService {

    void addLog(OperateLog operateLog);

    void add(String type, String desc, Long userId);

    void batchInsert(List<OperateLog> operateLogList, Long userId);

    OperateLogVo selectByCondition(LogQueryConditionVo conditionVo);

    void addStateAccessLog(String state, Long userId);

}
