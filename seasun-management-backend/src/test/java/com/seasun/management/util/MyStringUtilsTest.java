package com.seasun.management.util;

import com.seasun.management.Application;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@ActiveProfiles("local")
public class MyStringUtilsTest {

    @Test
    public void testIsPhone() {
        String str = "13759908970";
        MyStringUtils.isPhoneLegal(str);
        System.out.println(MyStringUtils.isPhoneLegal(str));
    }
}