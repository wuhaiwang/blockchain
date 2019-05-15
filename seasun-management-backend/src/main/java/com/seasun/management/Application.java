package com.seasun.management;

import java.util.Properties;
import java.util.concurrent.Executor;

import com.seasun.management.util.MyAddressUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.seasun.management.mapper.FnTaskMapper;
import com.seasun.management.util.MyBeanUtils;
import com.seasun.management.util.MyEnvUtils;

@SpringBootApplication
@EnableTransactionManagement
@EnableScheduling
@EnableAsync
@EnableCaching
@MapperScan({"com.seasun.management.mapper"})
@ServletComponentScan
public class Application extends AsyncConfigurerSupport implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    @Autowired
    private CacheManager cacheManager;

    public static void main(String[] args) {
        Properties
        SpringApplication.run(Application.class, args);

    }

    @Override
    public void run(String... args) throws Exception {
        String ip = MyAddressUtil.getIpAddress();
        logger.info("local ip is:{}",ip);
        // 重启后，废弃所有未完成的任务
        if (!MyEnvUtils.isLocalEnv()){
            MyBeanUtils.getBean(FnTaskMapper.class).discardAllUnfinishedTask("system reboot");
        }
        logger.info("cache manager: " + this.cacheManager.getClass().getName());
    }

    @Override
    @Bean
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("management-");
        executor.initialize();
        return executor;
    }
}
