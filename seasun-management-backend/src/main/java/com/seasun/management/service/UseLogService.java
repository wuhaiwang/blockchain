package com.seasun.management.service;

import com.alibaba.fastjson.JSONObject;
import com.seasun.management.vo.UseLogVo;

import java.util.List;

public interface UseLogService {
    void createUseLog(Long userId, JSONObject jsonObject);

    List<UseLogVo> getUseLogList(Long currentPage, Long pageSize);
}
