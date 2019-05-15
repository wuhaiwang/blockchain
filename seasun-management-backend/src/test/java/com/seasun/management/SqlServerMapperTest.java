package com.seasun.management;

import com.seasun.management.config.DataSourceContextHolder;
import com.seasun.management.mapper.dataCenter.Jx2LoginstatMapper;
import com.seasun.management.mapper.dsp.BankCardMapper;
import com.seasun.management.model.dataCenter.VFactJx3StatD;
import com.seasun.management.model.dsp.BankCard;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@ActiveProfiles("local")
public class SqlServerMapperTest {

    @Autowired
    BankCardMapper bankCardMapper;

    private static final Logger logger = LoggerFactory.getLogger(OracleMapperTest.class);

    @Test
    public void testBankCardSelect() {
        DataSourceContextHolder.useFourth();
        List<BankCard> result = bankCardMapper.selectAll();
        logger.debug("size is" + result.size());
    }

}
