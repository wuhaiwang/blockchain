package com.seasun.management.service.impl;

import com.seasun.management.mapper.CrashLogMapper;
import com.seasun.management.mapper.UserMapper;
import com.seasun.management.model.User;
import com.seasun.management.service.CrashLogService;
import com.seasun.management.vo.CrashLogConditionVo;
import com.seasun.management.vo.CrashLogQueryConditionVo;
import com.seasun.management.vo.CrashLogVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CrashLogServiceImpl implements CrashLogService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    CrashLogMapper crashLogMapper;

    private static final Logger logger = LoggerFactory.getLogger(CrashLogServiceImpl.class);

    @Override
    public void createCrashLog(CrashLogVo crashLogVo) {
        if (null != crashLogVo.getLoginId()) {
            User user = userMapper.selectActiveUserByLoginId(crashLogVo.getLoginId());
            if (null != user) {
                crashLogVo.setUserId(user.getId());
            }
        }
        if (null != crashLogVo.getSystem()) {
            crashLogVo.setSystem(crashLogVo.getSystem().toLowerCase());
        }
        if (crashLogVo.getDescription() != null) {
            String[] descriptions = crashLogVo.getDescription().split("\n");
            for (String description : descriptions) {
                //崩溃时间
                if (description.startsWith("crashTimestamp")) {
                    Long crashTimestamp = null;
                    try {
                        crashTimestamp = Long.parseLong(description.substring(15));
                    } catch (NumberFormatException e) {
                        logger.info("loginId: " + crashLogVo.getLoginId() + " crashLog NumberFormatException...");
                    }
                    if (crashTimestamp != null) {
                        crashLogVo.setCreateTime(new Date(crashTimestamp));
                    }
                    break;
                }
            }
        }
        crashLogMapper.insertSelective(crashLogVo);
    }

    @Override
    public CrashLogConditionVo getCrashLogList(CrashLogQueryConditionVo vo) {
        CrashLogConditionVo crashLogVo = new CrashLogConditionVo();
        double totalCount = crashLogMapper.selectCountByCondition(vo);
        List<CrashLogVo> crashLogVoList = crashLogMapper.selectByCondition(vo);
        crashLogVo.setCrashLogVoList(crashLogVoList);
        crashLogVo.setTotalPages((long) Math.ceil(totalCount / vo.getPageSize()));
        return crashLogVo;
    }
}
