package com.seasun.management.task;

import com.seasun.management.service.kingsoftLife.KsDormitoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class DormitorySyncCheckinAndCheckoutTask {

    Logger logger = LoggerFactory.getLogger(DormitorySyncCheckinAndCheckoutTask.class);

    @Autowired
    KsDormitoryService ksDormitoryService;

    @Scheduled(cron = "0 0/5 * * * ?")
    public void sync () throws InterruptedException{
        logger.info ("开始执行同步db入住信息, 当前线程 -> {}", Thread.currentThread().getId());
        ksDormitoryService.updateNonCheckInOrNonCheckOutOrNonUserId();
        logger.info ("结束执行同步db入住信息");
    }

}
