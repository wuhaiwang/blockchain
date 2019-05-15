package com.seasun.management.Utils;

import com.seasun.management.Application;
import com.seasun.management.util.MyEnvUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@ActiveProfiles("local")
public class MyEnvUtilsTest {

    @Test
    public void testIsEnvMatch(){
        MyEnvUtils.isLocalEnv();
    }
}
