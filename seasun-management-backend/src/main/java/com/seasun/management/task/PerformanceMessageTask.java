package com.seasun.management.task;

import com.seasun.management.constant.BaseConstant;
import com.seasun.management.dto.UserPerformanceDto;
import com.seasun.management.exception.ParamException;
import com.seasun.management.mapper.UserPerformanceMapper;
import com.seasun.management.model.UserPerformance;
import com.seasun.management.service.UserMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/*绩效提醒服务*/
@Service
public class PerformanceMessageTask {

    public static final Logger logger=LoggerFactory.getLogger(PerformanceMessageTask.class);

    @Autowired
    UserMessageService userMessageService;

    @Autowired
    UserPerformanceMapper userPerformanceMapper;

    @Scheduled(cron = "0 0 0 10 * ?")
    public void userPermanceMess(){
        logger.info("begin send unSubmitted performance message...");
        List<String> LoginIds=new ArrayList();
            //按日期查询还没有提交绩效的人
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH )+1;
        List<UserPerformanceDto> userPerformanceDtos = userPerformanceMapper.selectPerfByYearAndMonth(year,month);
        if(!userPerformanceDtos.isEmpty()) {
            for (UserPerformanceDto userPerformanceDto : userPerformanceDtos) {
                LoginIds.add(userPerformanceDto.getLoginId());
            }
        }else{
            throw new ParamException("未提交绩效数据不存在");
        }
        userMessageService.sendSeasunMessages(BaseConstant.PC_NAME,year+"年"+month+"月的绩效您还未提交,请尽快提交", BaseConstant.SendMessageType.rtx, LoginIds);
        logger.info("finsh send unsubmitted performance message...");
    }
}
