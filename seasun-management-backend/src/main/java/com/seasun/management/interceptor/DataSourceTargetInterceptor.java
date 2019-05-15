package com.seasun.management.interceptor;

import com.seasun.management.annotation.DataSourceTarget;
import com.seasun.management.config.DataSourceContextHolder;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Configuration
public class DataSourceTargetInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceTargetInterceptor.class);

    @Pointcut("execution(public * com.seasun.management.service..*.*(..))")
    public void dbPointCut() {
    }

    @Before(value = "dbPointCut()"
            + "&& @annotation(com.seasun.management.annotation.DataSourceTarget)"
            + "&& @annotation(dataSourceTarget)", argNames = "dataSourceTarget")
    public void changeDataSource(DataSourceTarget dataSourceTarget) throws Throwable {
        String dsId = dataSourceTarget.name();
        if (!DataSourceContextHolder.containsDataSource(dsId)) {
            logger.error("数据源[{}]不存在，将使用默认数据源: {}");
        } else {
            logger.debug("使用数据源 : {}", dataSourceTarget.name());
            DataSourceContextHolder.setType(dataSourceTarget.name());
        }
    }

    @After(value = "dbPointCut()")
    public void clearDataSource() {
        DataSourceContextHolder.clearDBType();
    }

}
