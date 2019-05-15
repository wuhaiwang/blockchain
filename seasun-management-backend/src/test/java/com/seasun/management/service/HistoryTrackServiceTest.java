package com.seasun.management.service;


import com.seasun.management.Application;
import com.seasun.management.dto.UserSalaryChangeDto;
import com.seasun.management.model.UserPerformance;
import com.seasun.management.service.performanceCore.historyTrack.HistoryTrackService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@ActiveProfiles("local")
public class HistoryTrackServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(HistoryTrackServiceTest.class);

    @Autowired
    HistoryTrackService<UserSalaryChangeDto> userSalaryChangeDtoHistoryTrackService;

    @Autowired
    HistoryTrackService<UserPerformance> userPerformanceHistoryTrackService;

    @Test
    public void testFindAllHistoryMemberList() {
        Long workGroupId = 1000L;
        int year = 2017;
        int month = 4;
        int quarter = 1;
        userSalaryChangeDtoHistoryTrackService.getAllHistoryMembersByWorkGroupIdAndTime(workGroupId, year, month, quarter);
    }
}