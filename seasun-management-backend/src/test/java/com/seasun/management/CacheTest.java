package com.seasun.management;

import com.seasun.management.mapper.SchoolMapper;
import com.seasun.management.model.School;
import com.seasun.management.service.SchoolService;
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
public class CacheTest {

    @Autowired
    private SchoolMapper schoolMapper;

    private static final Logger logger = LoggerFactory.getLogger(CacheTest.class);


    @Autowired
    SchoolService schoolService;

    /**
     * timeToIdleSeconds = "1"
     * timeToLiveSeconds = "1"
     * 1s后过期
     */
    @Test
    public void testCacheEffectTime() {
        School school1 = schoolMapper.selectByPrimaryKey(592L);
        Long now = System.currentTimeMillis();
        while (true) {
            Long current = System.currentTimeMillis();
            if (current - now > 100) {
                logger.debug("reach time-threshold ...");
                break;
            }
        }
        School school2 = schoolMapper.selectByPrimaryKey(592L);
    }

    /**
     * service 缓存
     */
    @Test
    public void TestServiceCache() {
        List<School> schools1 = schoolService.selectAll();
        List<School> schools2 = schoolService.selectAll();
    }

    /**
     * reload 后缓存失效
     */
    @Test
    public void testCacheReload() {
        School school1 = schoolMapper.selectByPrimaryKey(592L);
        logger.debug("old name:{}", school1.getName());
        School record = new School();
        record.setId(592L);
        record.setName(school1.getName() + "_new");
        schoolMapper.updateByPrimaryKey(record);
        schoolMapper.reloadCache();
        School school2 = schoolMapper.selectByPrimaryKey(592L);
        logger.debug("new name:{}", school2.getName());
    }
}
