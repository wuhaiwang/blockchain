package com.seasun.management.task;

import com.seasun.management.mapper.CfgSystemParamMapper;
import com.seasun.management.model.CfgSystemParam;
import com.seasun.management.service.UserPerformanceService;
import com.seasun.management.util.MyBeanUtils;
import com.seasun.management.util.MySystemParamUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.util.TimerTask;

@Service
public class UserPerformanceTask {

    private static final Logger logger = LoggerFactory.getLogger(UserPerformanceTask.class);

    private static ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();


    private static Integer delaytime;

    @Value("${task.perf.notift.delay.time}")
    public void setTime(Integer time) {
        UserPerformanceTask.delaytime = time;
    }

    static {
        threadPoolTaskScheduler.initialize();
        //启动定时器，检查数据库中每月绩效通知填写日期，
        threadPoolTaskScheduler.schedule(new TimerTask() {
            @Override
            public void run() {
                setPerfNotifyTaskIntervalTime();

            }
            //频率，1天/次
            // }, new CronTrigger("0 0/" + 1 + " * * * *"));
        }, new CronTrigger("0 0 0 1/1 * *"));
    }

    // 刷新每月绩效通知日期
    public static void setPerfNotifyTaskIntervalTime() {
        CfgSystemParamMapper cfgSystemParamMapper = MyBeanUtils.getBean(CfgSystemParamMapper.class);
        logger.info("begin setPerfNotifyTaskIntervalTime...");
        CfgSystemParam cfgSystemParam = cfgSystemParamMapper.selectByName
                (MySystemParamUtils.Key.PerformanceNotifyDay);
        if (cfgSystemParam != null) {

            try {
                if (Integer.parseInt(cfgSystemParam.getValue()) > 28) {
                    logger.error("数据库中关于配置绩效的每月通知时间异常，请重新配置或修改cron表达式");
                    return;
                }
            } catch (NumberFormatException e) {
                logger.error(e.getMessage());
                return;
            }

            synchronized (logger) {
                threadPoolTaskScheduler.shutdown();
                threadPoolTaskScheduler.initialize();
                threadPoolTaskScheduler.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        notifyUserPerfOperation();
                    }
                    //    }, new CronTrigger("0 0/" + 1 + " * * * *"));
                    // 每月X号
                }, new CronTrigger("0 0 0 " + Integer.parseInt(cfgSystemParam.getValue()) + "  * *"));
            }
        } else {
            logger.error("数据库没有配置绩效的每月通知时间。。。");
        }
    }

    private static void notifyUserPerfOperation() {
//        RedisService redisService = MyBeanUtils.getBean
//                (RedisService.class);
//        String ipAddress = MyAddressUtil.getIpAddress();
        //  boolean lock = redisService.getlock("notifyUserPerfOperation", ipAddress, 1000 * 60 * 60);
        //  logger.info("getlock=====" + lock);
        //   if (lock) {
        logger.info("线程延迟" + delaytime / 1000 + "秒");
        try {
            Thread.sleep(delaytime);
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        }
        UserPerformanceService userPerformanceService = MyBeanUtils.getBean
                (UserPerformanceService.class);
        userPerformanceService.notifyUserPerformance();
        //   }

    }
}
