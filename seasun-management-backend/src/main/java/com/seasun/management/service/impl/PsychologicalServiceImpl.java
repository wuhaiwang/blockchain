package com.seasun.management.service.impl;

import com.seasun.management.mapper.UserMapper;
import com.seasun.management.mapper.UserPsychologicalConsultationMapper;
import com.seasun.management.model.User;
import com.seasun.management.model.UserPsychologicalConsultation;
import com.seasun.management.service.PsychologicalService;
import com.seasun.management.util.MyEncryptorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

@Service
public class PsychologicalServiceImpl implements PsychologicalService {


    private static final Logger logger = LoggerFactory.getLogger(PsychologicalServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserPsychologicalConsultationMapper userPsychologicalConsultationMapper;

    @Override
    public void generateNewPassword() {
        logger.info("begin generateNewPassword");
        List<User> userDtos = userMapper.selectActiveEntityUserIdAndEmployeeNo();
        List<UserPsychologicalConsultation> inserts = new ArrayList<>();
        Calendar nowDate = Calendar.getInstance();
        int nowYear = nowDate.get(Calendar.YEAR);
        int nowMonth = nowDate.get(Calendar.MONTH) + 1;
        Random random = new Random();

        for (User user : userDtos) {
            UserPsychologicalConsultation insert = new UserPsychologicalConsultation();
            insert.setUserId(user.getId());
            insert.setEmployeeNo(user.getEmployeeNo());
            insert.setYear(nowYear);
            insert.setMonth(nowMonth);
            insert.setPassword(MyEncryptorUtils.getRandomNumber(random, 6));
            inserts.add(insert);
        }
        int i = userPsychologicalConsultationMapper.batchInsert(inserts);

        if (i > 0) {
            logger.info("end generateNewPassword");
        } else {
            logger.info("error generateNewPassword");
        }

    }
}
