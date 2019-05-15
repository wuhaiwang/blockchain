package com.seasun.management.service.impl;

import com.seasun.management.dto.UserFeedbackDto;
import com.seasun.management.exception.ParamException;
import com.seasun.management.mapper.UserFeedbackMapper;
import com.seasun.management.model.User;
import com.seasun.management.model.UserFeedback;
import com.seasun.management.service.UserFeedbackService;
import com.seasun.management.util.MyEmojiUtil;
import com.seasun.management.util.MyTokenUtils;
import com.seasun.management.vo.UserFeedbackQueryConditionVo;
import com.seasun.management.vo.UserFeedbackVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class UserFeedbackServiceImpl implements UserFeedbackService {
    private static final Logger logger = LoggerFactory.getLogger(UserFeedbackServiceImpl.class);

    @Autowired
    UserFeedbackMapper userFeedbackMapper;

    @Override
    public UserFeedbackVo getUserFeedbackList(UserFeedbackQueryConditionVo vo) {
        UserFeedbackVo userFeedbackVo = new UserFeedbackVo();
        List<UserFeedbackDto> dbFeedBackList = userFeedbackMapper.selectByCondition(vo);
        double dbFeedBackCounts = userFeedbackMapper.selectCountByCondition(vo);
        if (dbFeedBackList != null) {
            dbFeedBackList.forEach(f -> f.setContent(MyEmojiUtil.resolveToEmojiFromByte(f.getContent())));
        }
        userFeedbackVo.setUserFeedbackList(dbFeedBackList);
        userFeedbackVo.setTotalPages((long) Math.ceil(dbFeedBackCounts / vo.getPageSize()));
        return userFeedbackVo;
    }


    @Override
    public void createUserFeedback(UserFeedback userFeedback) {
        if (null == userFeedback.getContent() || userFeedback.getContent().isEmpty()) {
            throw new ParamException("请填写反馈意见");
        }

        userFeedback.setContent(MyEmojiUtil.resolveToByteFromEmoji(userFeedback.getContent()));
        User logonUser = MyTokenUtils.getCurrentUser();

        userFeedback.setUserId(logonUser.getId());
        userFeedback.setChannel(MyTokenUtils.getChannel());
        userFeedback.setCreateTime(new Date());

        userFeedbackMapper.insertSelective(userFeedback);
    }
}
