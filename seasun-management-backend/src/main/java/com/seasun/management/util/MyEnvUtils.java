package com.seasun.management.util;

import com.seasun.management.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyEnvUtils implements ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(MyEnvUtils.class);

    private static ApplicationContext applicationContext;

    private static void setAppContext(ApplicationContext appContext) {
        applicationContext = appContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        setAppContext(applicationContext);
    }

    public static boolean isLocalEnv() {
        return isEnvMatch("local");
    }

    public static boolean isQaEnv() {
        return isEnvMatch("qa");
    }

    public static boolean isProdEnv() {
        return isEnvMatch("prod");
    }

    private static boolean isEnvMatch(String profile) {
        String[] activeProfiles = applicationContext.getEnvironment().getActiveProfiles();
        for (String temp : activeProfiles) {
            logger.info("当前使用的profile为:{}", temp);
            if (profile.equalsIgnoreCase(temp)) {
                return true;
            }
        }
        return false;
    }
}
