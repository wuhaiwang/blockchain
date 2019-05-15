package com.seasun.management.service;

import com.seasun.management.Application;
import com.seasun.management.model.OperateLog;
import com.seasun.management.vo.LogQueryConditionVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.logging.Logger;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@ActiveProfiles("local")
public class OperateLogServiceTest {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(HistoryTrackServiceTest.class);

    @Autowired
    OperateLogService operateLogService;

    @Test
    public void testSelectByCondition(){
        LogQueryConditionVo vo=new LogQueryConditionVo();
        vo.setChannel("mobile");
        vo.setCurrentPage(1L);
        vo.setPageSize(50L);
        logger.info(vo.getBeginNum()+"===");
        operateLogService.selectByCondition(vo);
    }
}
