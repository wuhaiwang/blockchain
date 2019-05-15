package com.seasun.management.service;

import com.seasun.management.vo.CrashLogConditionVo;
import com.seasun.management.vo.CrashLogQueryConditionVo;
import com.seasun.management.vo.CrashLogVo;

public interface CrashLogService {

    void createCrashLog(CrashLogVo crashLogVo);

    CrashLogConditionVo getCrashLogList(CrashLogQueryConditionVo vo);
}
