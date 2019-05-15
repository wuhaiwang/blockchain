package com.seasun.management.service;

import com.alibaba.fastjson.JSON;
import com.seasun.management.Application;
import com.seasun.management.service.dataCenter.DataCenterSampleService;
import com.seasun.management.vo.dataCenter.RealTimeOnlineDataVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Calendar;
import java.util.Date;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@ActiveProfiles("local")
public class DataCenterSampleServiceTest {


    @Autowired
    DataCenterSampleService dataCenterSampleService;

    @Test
    public void testSelect() {
        dataCenterSampleService.getAllLoginInfoByCond("2017-05-08", "2017-06-07", "day");
    }

    @Test
    public void testGetRealTimeDataCollection() {
        Calendar onlineCalender = Calendar.getInstance();
        onlineCalender.set(2017, 5, 8);//2017-6-8

        Calendar chargeCalender = Calendar.getInstance();
        chargeCalender.set(2017, 8, 7);//2017-9-7

        Calendar dailyCalender = Calendar.getInstance();
        dailyCalender.set(2017, 5, 7);//2017-6-7

        Calendar chargeSumCalender = Calendar.getInstance();
        chargeSumCalender.set(2017, 7, 7);//2017-6-7

//        RealTimeOnlineDataVo result = dataCenterSampleService.getRealTimeDataCollection(onlineCalender.getTime(), chargeSumCalender.getTime(), chargeCalender.getTime(), dailyCalender.getTime());
        RealTimeOnlineDataVo result = dataCenterSampleService.getRealTimeDataCollection(new Date(), new Date(), new Date(),"JX3");

        System.out.println(JSON.toJSON(result));
    }

}
