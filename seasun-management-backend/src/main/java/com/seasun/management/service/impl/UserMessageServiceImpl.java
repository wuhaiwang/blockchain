package com.seasun.management.service.impl;

import com.seasun.management.constant.BaseConstant;
import com.seasun.management.dto.RUserProjectPermDto;
import com.seasun.management.exception.ParamException;
import com.seasun.management.mapper.*;
import com.seasun.management.model.*;
import com.seasun.management.service.UserMessageService;
import com.seasun.management.util.*;
import com.seasun.management.vo.UserMessageCommuteDto;
import com.seasun.management.vo.UserMessageConditionVo;
import com.seasun.management.vo.UserMessageVo;
import com.seasun.management.vo.UserPerformanceVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.util.*;

@Service
public class UserMessageServiceImpl implements UserMessageService {

    private static final Logger logger = LoggerFactory.getLogger(UserMessageServiceImpl.class);

    @Autowired
    UserMessageMapper userMessageMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    RUserProjectPermMapper rUserProjectPermMapper;

    @Autowired
    UserPerformanceMapper userPerformanceMapper;

    @Autowired
    private CfgSystemParamMapper cfgSystemParamMapper;

    @Value("${rtx.send.message.url}")
    private String rtxSendMessageUrl;
    @Value("${rtx.appkey}")
    private String rtxAppkey;
    @Value("${rtx.app.name}")
    private String rtxAppName;


    @Override
    public UserMessageVo getMessages(UserMessageConditionVo vo) {
        UserMessageVo userMessageVo = new UserMessageVo();
        double totalCount = userMessageMapper.selectCountByCondition(vo);
        List<UserMessageCommuteDto> messageVos = userMessageMapper.selectByCondition(vo);
        userMessageVo.setMessageList(messageVos);
        userMessageVo.setTotalPages((long) Math.ceil(totalCount / vo.getPageSize()));
        return userMessageVo;
    }

    @Override
    public UserMessage add(UserMessage userMessage) {
        if (userMessage.getSender() == null) {
            userMessage.setSender(1011L);
        }
        UserMessage latestRecord = userMessageMapper.selectLatestRecordByReceiver(userMessage.getReceiver());
        userMessage.setCreateTime(new Date());
        userMessage.setReadFlag(false);
        if (latestRecord == null) {
            userMessage.setVersionId(1L);
        } else {
            userMessage.setVersionId(latestRecord.getVersionId() + 1);
        }
        userMessageMapper.insert(userMessage);
        return userMessage;
    }

    @Override
    public void batchAdd(List<UserMessage> userMessages) {
        if (userMessages == null || userMessages.size() == 0) {
            return;
        }
        for (UserMessage userMessage : userMessages) {
            if (userMessage.getSender() == null) {
                userMessage.setSender(1011L);
            }
            userMessage.setCreateTime(new Date());
            userMessage.setReadFlag(false);
        }
        userMessageMapper.batchInsertSelective(userMessages);
    }

    @Override
    public void deleteByPk(Long id) {
        userMessageMapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<UserMessage> getUserMessages(Boolean readFlag) {
        Long userId = MyTokenUtils.getCurrentUserId();
        List<UserMessage> userMessages;
        if (null == readFlag) {
            userMessages = userMessageMapper.selectAllByReceiver(userId);
        } else {
            userMessages = userMessageMapper.selectAllByReceiverAndReadFlag(userId, readFlag);
        }
        return userMessages;
    }

    @Override
    public List<UserMessage> getNewUserMessages(Long versionId) {
        Long userId = MyTokenUtils.getCurrentUserId();
        List<UserMessage> userMessages;
        if (null == versionId || versionId == 0) {
            userMessages = userMessageMapper.selectAllByReceiverAndReadFlag(userId, false);
        } else {
            userMessages = userMessageMapper.selectAllByReceiverAndVersionId(userId, versionId);
        }
        return userMessages;
    }

    @Transactional
    @Override
    public void setUserMessageReadFlagByLocation(String location) {
        Long userId = MyTokenUtils.getCurrentUserId();
        if (location.startsWith("performance")) {
            userMessageMapper.updateReadFlagByReceiverAndLocation(userId, UserMessage.Location.performanceMyPerformance);
            userMessageMapper.updateReadFlagByReceiverAndLocation(userId, UserMessage.Location.performanceSubordinate);
            userMessageMapper.updateReadFlagByReceiverAndLocation(userId, UserMessage.Location.performanceFixMember);
        } else {
            userMessageMapper.updateReadFlagByReceiverAndLocation(userId, location);
        }
    }

    @Override
    public void setUserMessageReadFlagById(Long id) {
        userMessageMapper.updateReadFlagById(id);
    }

    @Override
    public void createNewPerformanceMessage(int year, int month) {
        List<UserMessage> userMessages = new ArrayList<>();
        List<User> users = userMapper.selectAllByPerformance();
        String content = year + "年" + month + "月的绩效已经开始，请及时填写并提交您的绩效。";
        List<String>  receverLoginIds=new ArrayList<>();
        for (User user : users) {
            UserMessage userMessage = getNewSystemUserMessage(user.getId(), UserMessage.Location.performanceMyPerformance,
                    content);
            userMessages.add(userMessage);
            receverLoginIds.add(user.getLoginId());
        }
        sendSeasunMessages(BaseConstant.PC_NAME,content, BaseConstant.SendMessageType.rtx, receverLoginIds);
        userMessageMapper.batchInsertSelective(userMessages);
    }

    @Override
    public void createFinishPerformanceMessage(int year, int month) {
        List<UserMessage> userMessages = new ArrayList<>();
        List<UserPerformanceVo> userPerformances = userPerformanceMapper.selectPureModelByYearAndMonth(year, month);
        String message=year + "年" + month + "月的绩效已经完成，登录 https://it.seasungame.com 选择绩效管理系统 或打开【移动办公App】可查看您的绩效结果。";
        List<String> userIds=new ArrayList<String>();
        if (userPerformances.isEmpty()) {
            throw new ParamException("没有该月的绩效数据");
        }
        for (UserPerformanceVo userPerformanceVo : userPerformances) {
            UserMessage userMessage = getNewSystemUserMessage(userPerformanceVo.getUserId(), UserMessage.Location.performanceMyPerformance,
                    message);
            userMessages.add(userMessage);
            userIds.add(userPerformanceVo.getLoginId());
        }
        sendSeasunMessages(BaseConstant.PC_NAME, message, BaseConstant.SendMessageType.rtx,userIds);

    }

    @Override
    public void createNewFinanceDataMessage(String date) {
        List<UserMessage> userMessages = new ArrayList<>();
        List<Long> projectManagers = new ArrayList<>();
        List<Long> platformManagers = new ArrayList<>();
        List<Long> financeManagers = new ArrayList<>();
        List<RUserProjectPermDto> rUserProjectPerms = rUserProjectPermMapper.selectAllByAppRole();
        for (RUserProjectPermDto rUserProjectPerm : rUserProjectPerms) {
            Long userId = rUserProjectPerm.getUserId();

            // app后台管理员
            if (ProjectRole.AppRole.manager_id.equals(rUserProjectPerm.getProjectRoleId())) {
                if (!projectManagers.contains(userId)) {
                    UserMessage userMessage = getNewSystemUserMessage(userId, UserMessage.Location.project, "项目看板的财务数据有更新，最新财报：" + date);
                    userMessages.add(userMessage);
                    projectManagers.add(userId);
                }
                if (!platformManagers.contains(userId)) {
                    UserMessage userMessage = getNewSystemUserMessage(userId, UserMessage.Location.platform, "平台看板的财务数据有更新，最新财报：" + date);
                    userMessages.add(userMessage);
                    platformManagers.add(userId);
                }
                if (!financeManagers.contains(userId)) {
                    UserMessage userMessage = getNewSystemUserMessage(userId, UserMessage.Location.finance, "财务看板的数据有更新，最新财报：" + date);
                    userMessages.add(userMessage);
                    financeManagers.add(userId);
                }
            }
            // app项目负责人
            else if (ProjectRole.AppRole.project_id.equals(rUserProjectPerm.getProjectRoleId())) {
                if (Project.ServiceLine.plat.equals(rUserProjectPerm.getServiceLine())) {
                    if (!platformManagers.contains(userId)) {
                        UserMessage userMessage = getNewSystemUserMessage(userId, UserMessage.Location.platform, "平台看板的财务数据有更新，最新财报：" + date);
                        userMessages.add(userMessage);
                        platformManagers.add(userId);
                    }
                } else {
                    if (!projectManagers.contains(userId)) {
                        UserMessage userMessage = getNewSystemUserMessage(userId, UserMessage.Location.project, "项目看板的财务数据有更新，最新财报：" + date);
                        userMessages.add(userMessage);
                        projectManagers.add(userId);
                    }
                }
            }
            // app财务
            else if (ProjectRole.AppRole.finance_id.equals(rUserProjectPerm.getProjectRoleId())) {
                if (!financeManagers.contains(userId)) {
                    UserMessage userMessage = getNewSystemUserMessage(userId, UserMessage.Location.finance, "财务看板的数据有更新");
                    userMessages.add(userMessage);
                    financeManagers.add(userId);
                }
            }
        }
        userMessageMapper.batchInsertSelective(userMessages);
    }

    @Override
    public void setUserMessageReadFlagByIds(List<Long> ids) {
        userMessageMapper.updateReadFlagByIds(ids);
    }

    @Override
    public void setUserAllMessageReadFlag(Long userId) {
        if (userId == null) {
            userId = MyTokenUtils.getCurrentUserId();
        }
        userMessageMapper.setUserAllMessageReadFlag(userId);
    }

    @Override
    @Async
    public void sendSeasunMessage(String title, String message, String sendtype, String loginId) {
        if (loginId == null || loginId.isEmpty()) {
            logger.info("error send rtx message loginId is empty ");
            return;
        }
        List<String> recevices = new ArrayList<>(2);
        recevices.add(loginId);
        executeSendSeasunMessage(title, message, sendtype, recevices);
    }

    @Override
    @Async
    public void sendSeasunMessages(String title, String message, String sendtype, List<String> receivcers) {
        executeSendSeasunMessage(title, message, sendtype, receivcers);
    }

    /**
     * 发送rtx消息
     * title:标题
     * message:内容
     * sendtype 发送类型 rtx email weixin...
     * receivcers 接收人loginId
     *
     * @return
     */
    private void executeSendSeasunMessage(String title, String message, String sendtype, List<String> receivcers) {
        logger.info("begin send rtx message");

        if (receivcers.isEmpty()) {
            logger.info("error send rtx message receivcers is empty ");
            return;
        }

        StringBuilder sb = new StringBuilder();
        // 非prod环境，rtx接收者为配置的用户
        if (!MyEnvUtils.isProdEnv()) {
            CfgSystemParam cfgSystemParam = cfgSystemParamMapper.selectByName(MySystemParamUtils.Key.RtxTestSendUser);
            if (cfgSystemParam != null) {
                for (String item : cfgSystemParam.getValue().split(",")) {
                    sb.append(item);
                    sb.append(",");
                }
            }
            // 正式环境
        } else {
            for (String recevice : receivcers) {
                sb.append(recevice);
                logger.info("will send rtx message to " + recevice);
                sb.append(",");
            }
        }
        sb = new StringBuilder(sb.substring(0, sb.length() - 1));
      /*  sb.append("&title=").append(title).append("&").append("message=").append(message).append("&").append("sendtype=").append(sendtype).append("&").append("appname=").append(rtxAppName).append("&").append("appkey")
                .append(rtxAppkey);*/

        String result = null;
        StringBuilder body = new StringBuilder();
        try {
            body.append("recevice=").append(URLEncoder.encode(sb.toString(), "utf-8"))
                    .append("&").append("title=").append(URLEncoder.encode(title, "utf-8"))
                    .append("&").append("message=").append(URLEncoder.encode(message, "utf-8"))
                    .append("&").append("sendtype=").append(URLEncoder.encode(sendtype, "utf-8"))
                    .append("&").append("appname=").append(URLEncoder.encode(rtxAppName, "utf-8"))
                    .append("&").append("appkey=").append(URLEncoder.encode(rtxAppkey, "utf-8"));

            result = MyHttpClientUtils.doPost(rtxSendMessageUrl, body.toString(), "application/x-www-form-urlencoded");

            //  result = MyHttpClientUtils.doPost(rtxSendMessageUrl, sb.toString());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        logger.info("end send rtx message,result===" + result);
    }


    private UserMessage getNewSystemUserMessage(Long receiver, String location, String content) {
        UserMessage userMessage = new UserMessage();
        userMessage.setSender(1011L);
        userMessage.setContent(content);
        userMessage.setReceiver(receiver);
        userMessage.setType(UserMessage.Type.system);
        userMessage.setLocation(location);
        userMessage.setReadFlag(false);
        userMessage.setCreateTime(new Date());
        return userMessage;
    }
}
