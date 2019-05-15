package com.seasun.management.service;

import com.seasun.management.dto.KeyValueDto;
import com.seasun.management.model.UserMessage;
import com.seasun.management.model.UserPsychologicalConsultation;
import com.seasun.management.vo.UserMessageCommuteDto;
import com.seasun.management.vo.UserMessageConditionVo;
import com.seasun.management.vo.UserMessageVo;

import java.util.List;

public interface UserMessageService {

    UserMessageVo getMessages(UserMessageConditionVo vo);

    UserMessage add(UserMessage userMessage);

    void batchAdd(List<UserMessage> userMessages);

    void deleteByPk(Long id);

    List<UserMessage> getUserMessages(Boolean readFlag);

    List<UserMessage> getNewUserMessages(Long versionId);

    void setUserMessageReadFlagByLocation(String location);

    void setUserMessageReadFlagById(Long id);

    void createNewPerformanceMessage(int year, int month);

    void createFinishPerformanceMessage(int year, int month);

    void createNewFinanceDataMessage(String date);

    void setUserMessageReadFlagByIds(List<Long> ids);

    void setUserAllMessageReadFlag(Long userId);

    void sendSeasunMessages(String title, String message, String sendType, List<String> recevices);

    void sendSeasunMessage(String title, String message, String sendType, String loginId);
}
