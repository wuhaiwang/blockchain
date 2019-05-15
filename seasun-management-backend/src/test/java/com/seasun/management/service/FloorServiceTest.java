package com.seasun.management.service;

import com.seasun.management.Application;
import com.seasun.management.model.Floor;
import com.seasun.management.service.impl.FloorServiceImpl;
import com.seasun.management.util.MyBeanUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@EnableTransactionManagement
@ActiveProfiles("local")
public class FloorServiceTest {


    private static final Logger logger = LoggerFactory.getLogger(FloorServiceTest.class);

    @Autowired
    FloorService floorService;


    @Test
    @Transactional
    public void testTransation() {
        floorService.insertTwo();
        logger.debug("insert finish, and new record will rollback, cause @transactional is active...");
    }
}
