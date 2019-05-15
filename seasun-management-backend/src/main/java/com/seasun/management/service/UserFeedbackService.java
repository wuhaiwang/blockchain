package com.seasun.management.service;

import com.seasun.management.model.UserFeedback;
import com.seasun.management.vo.UserFeedbackQueryConditionVo;
import com.seasun.management.vo.UserFeedbackVo;

import java.util.List;

public interface UserFeedbackService {

    UserFeedbackVo getUserFeedbackList(UserFeedbackQueryConditionVo vo);

    void createUserFeedback(UserFeedback userFeedback);
}
