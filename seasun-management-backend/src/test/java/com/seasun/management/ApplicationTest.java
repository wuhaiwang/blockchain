package com.seasun.management;

import com.seasun.management.util.MyTokenUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;



@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@ActiveProfiles("local")
public class ApplicationTest {

    private static final Logger logger= LoggerFactory.getLogger(ApplicationTest.class);

    @Test
    public void testApp() {
        logger.debug("system is running ...");

        boolean isProjectManager = MyTokenUtils.isProjectManager(6276L);
        isProjectManager = MyTokenUtils.isProjectManager(1440L);


        if (isProjectManager) {
            logger.debug("isProjectManager is true ");
        }else {
            logger.debug("isProjectManager is false ");
        }

    }

}
