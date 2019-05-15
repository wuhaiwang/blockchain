package com.seasun.management;

import com.seasun.management.annotation.DataSourceTarget;
import com.seasun.management.config.DataSourceContextHolder;
import com.seasun.management.mapper.dataCenter.Jx2LoginstatMapper;
import com.seasun.management.mapper.dataCenter.VFactJx3StatDMapper;
import com.seasun.management.model.dataCenter.Jx2Loginstat;
import com.seasun.management.model.dataCenter.VFactJx3StatD;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@ActiveProfiles("local")
public class OracleMapperTest {

    @Autowired
    VFactJx3StatDMapper vFactJx3StatDMapper;

    @Autowired
    Jx2LoginstatMapper jx2LoginstatMapper;

    private static final Logger logger = LoggerFactory.getLogger(OracleMapperTest.class);

    @Test
    public void testKboss20Select() {
        DataSourceContextHolder.useSecond();
        List<VFactJx3StatD> result = vFactJx3StatDMapper.selectAllWithDate("2017-05-07", "2017-06-06");
        logger.debug("size is" + result.size());
    }

    @Test
    public void testKbossPaySys10Select() {
        DataSourceContextHolder.useSecond();
        List<Jx2Loginstat> result = jx2LoginstatMapper.selectAllWithDateAndType("2017-05-08", "2017-06-07", "day");
        logger.debug("size is" + result.size());
    }

}
