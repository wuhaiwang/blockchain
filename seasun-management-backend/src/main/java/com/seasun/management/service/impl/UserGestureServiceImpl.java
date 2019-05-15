package com.seasun.management.service.impl;

import com.seasun.management.mapper.UserGestureMapper;
import com.seasun.management.model.UserGesture;
import com.seasun.management.service.UserGestureService;
import com.seasun.management.util.MyTokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserGestureServiceImpl implements UserGestureService {
    private static final Logger logger = LoggerFactory.getLogger(UserGestureServiceImpl.class);

    @Autowired
    UserGestureMapper userGestureMapper;

    @Override
    public void modifyUserGesture(UserGesture userGesture) {
        Long userId = MyTokenUtils.getCurrentUserId();
        UserGesture oldUserGesture = userGestureMapper.selectByUserId(userId);
        if (null != oldUserGesture) {
            oldUserGesture.setGesture(userGesture.getGesture());
            userGestureMapper.updateByPrimaryKeySelective(oldUserGesture);
        } else {
            userGesture.setUserId(userId);
            userGesture.setCreateTime(new Date());
            userGestureMapper.insert(userGesture);
        }
    }

    @Override
    public Boolean verifyGesture(String gesture) {
        Long userId = MyTokenUtils.getCurrentUserId();
        UserGesture userGesture = userGestureMapper.selectByUserId(userId);
        Boolean isVerify = null != userGesture && gesture.equals(userGesture.getGesture());
        return isVerify;
    }

    @Override
    public Boolean useGesture(Long userId) {
        UserGesture userGesture = userGestureMapper.selectByUserId(userId);
        return null != userGesture;
    }
}
