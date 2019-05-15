package com.seasun.management.service;

import com.seasun.management.Application;
import com.seasun.management.mapper.OperateLogMapper;
import com.seasun.management.mapper.UserPerformanceMapper;
import com.seasun.management.model.OperateLog;
import com.seasun.management.model.User;
import com.seasun.management.model.UserPerformance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@ActiveProfiles("local")

public class AppPerformanceServiceTest {

    @Autowired
    private UserPerformanceService userPerformanceService;

    @Autowired
    private UserPerformanceMapper userPerformanceMapper;

    @Autowired
    private OperateLogMapper operateLogMapper;

    @Test
    public void testGroupSubmit() {
        userPerformanceService.submitWorkGroup(267L, 2016, 1);
    }

    @Test
    public void testDate() {
        String dateStr = "Fri Jul 24 00:00:00 CST 2015";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.US);
            Date lastSalaryChangeDateT = sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testBatchUpdateUserPerformanceInfoByPks() {
        List<UserPerformance> userPerformanceList = new ArrayList<>();
        UserPerformance userPerformance = new UserPerformance();
        userPerformance.setId(597L);
        userPerformance.setFinalPerformance("S");
        userPerformance.setStatus("不参与");
        userPerformanceList.add(userPerformance);
        userPerformanceMapper.batchUpdateUserPerformanceInfoByPks(userPerformanceList);
    }

    @Test
    public void batchHandlePerformanceDetail() {
        List<UserPerformance> userPerformanceList = new ArrayList<>();


        UserPerformance userPerformance = new UserPerformance();
        userPerformance.setId(597L);
        userPerformance.setFinalPerformance("S");
        userPerformance.setStatus("不参与");
        userPerformance.setYear(2017);
        userPerformance.setUserId(1740L);
        userPerformance.setMonth(7);
        userPerformance.setManagerComment("llooiidjjjvd");
        userPerformanceList.add(userPerformance);

        UserPerformance userPerformance1 = new UserPerformance();
        userPerformance1.setYear(2017);
        userPerformance1.setUserId(1740L);
        userPerformance1.setMonth(10);
        userPerformance1.setManagerComment("sfasfdsfafsafafa");

        userPerformanceList.add(userPerformance1);
        User user = new User();
        user.setId(3404L);
        userPerformanceService.batchHandlePerformanceDetail(userPerformanceList, user,2017,7);
    }

    @Test
    public void tesbatchInsertLog(){

        List<OperateLog> logList = new ArrayList<>();
        String desc = "调整了" + "薪资 from " + " to " + ".";
        OperateLog operateLog = new OperateLog();
        operateLog.setType(OperateLog.Type.user_salary_modify);
        operateLog.setDescription(desc);
        operateLog.setChannel("pc");
        logList.add(operateLog);
        operateLogMapper.batchInsertSelective(logList);
    }

}
