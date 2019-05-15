package com.seasun.management.task;

import com.alibaba.fastjson.JSONObject;
import com.seasun.management.annotation.DataSourceTarget;
import com.seasun.management.config.DataSourceContextHolder;
import com.seasun.management.dto.UserDto;
import com.seasun.management.dto.dataCenter.VFactJx3StatDDto;
import com.seasun.management.mapper.RUserProjectPermMapper;
import com.seasun.management.mapper.UserMapper;
import com.seasun.management.mapper.UserMessageMapper;
import com.seasun.management.mapper.UserPsychologicalConsultationMapper;
import com.seasun.management.mapper.dataCenter.VFactJx3StatDMapper;
import com.seasun.management.model.*;
import com.seasun.management.util.MyEncryptorUtils;
import com.seasun.management.util.MyTokenUtils;
import com.seasun.management.vo.UserProjectPermVo;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class DataCenterDataCheckTask {

    @Autowired
    private RUserProjectPermMapper rUserProjectPermMapper;

    @Autowired
    private VFactJx3StatDMapper vFactJx3StatDMapper;

    @Autowired
    private UserMessageMapper userMessageMapper;

    @Value("${delayTime}")
    private int delayTime;

    @Value("${intervalTime}")
    private int intervalTime;

    private ScheduledExecutorService scheduledExecutorService = null;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private static final Logger logger = LoggerFactory.getLogger(DataCenterDataCheckTask.class);

    //每日6点启动
    @Scheduled(cron = "0 0 6 * * *")
    public void insertDailyProjectMessage() {

        if (null != scheduledExecutorService && !scheduledExecutorService.isShutdown()) {
            scheduledExecutorService.shutdown();
        }

        if (scheduledExecutorService == null || scheduledExecutorService.isShutdown()) {
            Calendar date =Calendar.getInstance();
            String selectEndDate = simpleDateFormat.format(date.getTime());
            date.add(Calendar.DAY_OF_MONTH, -1);
            String selectStartDate = simpleDateFormat.format(date.getTime());

            scheduledExecutorService = new ScheduledThreadPoolExecutor(1,
                    new BasicThreadFactory.Builder().namingPattern("example-schedule-pool-%d").daemon(true).build());

            scheduledExecutorService.scheduleWithFixedDelay(new TimerTask() {
                @Override
                public void run() {
                    //获取昨日剑网三日报消息
                    DataSourceContextHolder.useSecond();
                    List<VFactJx3StatDDto> jx3StatDList = vFactJx3StatDMapper.selectJX3TimeRangeStatD(selectStartDate, selectEndDate);
                    DataSourceContextHolder.usePrimary();

                    logger.info("检查数据更新...");
                    if (!jx3StatDList.isEmpty()) {
                        //消息
                        String content = "剑网3在" + date.get(Calendar.YEAR) + "年" + (date.get(Calendar.MONTH) + 1) + "月" + date.get(Calendar.DAY_OF_MONTH) + "日有新的日报消息";

                        //查询消息是否已发送
                        int i = userMessageMapper.checkDailyMessage(content);

                        if (i < 1) {

                            List<UserMessage> insertList = new ArrayList<>();
                            List<Long> insertUserIds = new ArrayList<>();
                            Date nowDate = new Date();

                            //在cfg_plat_attr表中data_center_flag为true的项目（目前只发送剑网3）
                            List<UserProjectPermVo> needSendMessageProjects = rUserProjectPermMapper.selectAppDailyProjectMessage(ProjectRole.AppRole.manager_id, ProjectRole.AppRole.project_id);
                            for (UserProjectPermVo userProjectPermVo : needSendMessageProjects) {
                                if (insertUserIds.contains(userProjectPermVo.getUserId()) || userProjectPermVo.getProjectId() != null && !userProjectPermVo.getProjectId().equals(Project.Id.JX3)) {
                                    continue;
                                }
                                insertUserIds.add(userProjectPermVo.getUserId());
                                UserMessage message = new UserMessage();
                                message.setSender(MyTokenUtils.ADMIN_ID);
                                message.setReceiver(userProjectPermVo.getUserId());
                                message.setType(UserMessage.Type.system);
                                message.setLocation(UserMessage.Location.dailyMessage);

                                JSONObject params = new JSONObject();
                                params.put("projectId", Project.Id.JX3);
                                message.setParams(params.toJSONString());
                                message.setContent(content);
                                message.setReadFlag(false);
                                message.setCreateTime(nowDate);
                                insertList.add(message);
                            }
                            int insertNum = userMessageMapper.batchInsertSelective(insertList);
                            logger.info(selectEndDate + "发送日报消息" + insertNum + "条");
                        }
                        scheduledExecutorService.shutdown();
                    }
                }
            }, delayTime, intervalTime, TimeUnit.SECONDS);
        }
    }
}
