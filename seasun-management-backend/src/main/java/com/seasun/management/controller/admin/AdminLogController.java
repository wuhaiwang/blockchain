package com.seasun.management.controller.admin;

import com.seasun.management.controller.response.CommonResponse;
import com.seasun.management.mapper.OperateLogMapper;
import com.seasun.management.model.OperateLog;
import com.seasun.management.service.CrashLogService;
import com.seasun.management.util.MyTokenUtils;
import com.seasun.management.vo.CrashLogConditionVo;
import com.seasun.management.vo.CrashLogQueryConditionVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping(value = "/apis/auth")
public class AdminLogController {
    private static final Logger logger = LoggerFactory.getLogger(AdminLogController.class);

    @Autowired
    CrashLogService crashLogService;

    @Autowired
    OperateLogMapper operateLogMapper;

    @RequestMapping(value = "/crash-log", method = RequestMethod.GET)
    public ResponseEntity<?> getCrashLogList(CrashLogQueryConditionVo vo) {
        logger.info("begin getCrashLogList...");
        CrashLogConditionVo crashLogVoList = crashLogService.getCrashLogList(vo);
        logger.info("end getCrashLogList...");
        return ResponseEntity.ok(crashLogVoList);
    }

    @RequestMapping(value = "/operate-log", method = RequestMethod.POST)
    public ResponseEntity<?> addOperateLog(OperateLog operateLog) {
        logger.info("begin addOperateLog...");
        operateLog.setChannel(OperateLog.channel.mobile);
        operateLog.setOperateId(MyTokenUtils.getCurrentUserId());
        operateLog.setCreateTime(new Date());
        operateLogMapper.insertSelective(operateLog);
        logger.info("end addOperateLog...");
        return ResponseEntity.ok(new CommonResponse(0,"success"));
    }

}
