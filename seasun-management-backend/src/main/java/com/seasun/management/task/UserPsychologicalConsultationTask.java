package com.seasun.management.task;

import com.seasun.management.mapper.UserPsychologicalConsultationMapper;
import com.seasun.management.service.PsychologicalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class UserPsychologicalConsultationTask {

    private static final Logger logger = LoggerFactory.getLogger(UserPsychologicalConsultationTask.class);

    @Autowired
    private PsychologicalService psychologicalService;

    // 每个月一号触发
    @Scheduled(cron = "0 0 0 1 * *")
    public void psychologicalConsultantPassword() {
        logger.info("psychological task begin ");
        psychologicalService.generateNewPassword();
        logger.info("psychological task end ");
    }

}
