package com.seasun.management.task;

import com.seasun.management.service.UserPerformanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class UserPhotoSyncTask {

    @Autowired
    private UserPerformanceService userPerformanceService;

    @Scheduled(cron = "0 0 0 * * *")
    public void syncPhoto() {
        userPerformanceService.syncPhoto();
    }
}
