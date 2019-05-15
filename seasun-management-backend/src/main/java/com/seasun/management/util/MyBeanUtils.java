package com.seasun.management.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class MyBeanUtils implements ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyBeanUtils.class);

    private static ApplicationContext applicationContext;


    public static Object getBean(String beanName) {
        Object beanInstance;
        try {
            beanInstance = applicationContext.getBean(beanName);
        } catch (NoSuchBeanDefinitionException e) {
            LOGGER.info("Bean '{}' not be found", beanName);
            return null;
        }
        return beanInstance;
    }

    public static <T> T getBean(String beanName, Class<T> clazz) {
        Object beanInstance = getBean(beanName);
        if (beanInstance == null) {
            return null;
        }
        return clazz.cast(beanInstance);
    }

    public static <T> T getBean(Class<T> clazz) {
        T beanInstance;
        try {
            beanInstance = applicationContext.getBean(clazz);
        } catch (NoSuchBeanDefinitionException e) {
            LOGGER.info("Bean '{}' not be found", clazz);
            return null;
        }
        return beanInstance;
    }

    private static void setAppContext(ApplicationContext appContext) {
        applicationContext = appContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        setAppContext(applicationContext);
    }


}
