package com.seasun.management.service.impl;

import com.seasun.management.dto.AppInfoDto;
import com.seasun.management.mapper.OperateLogMapper;
import com.seasun.management.model.OperateLog;
import com.seasun.management.model.User;
import com.seasun.management.service.OperateLogService;
import com.seasun.management.util.MyTokenUtils;
import com.seasun.management.vo.LogQueryConditionVo;
import com.seasun.management.vo.OperateLogVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OperateLogServiceImpl implements OperateLogService {

    @Autowired
    OperateLogMapper operateLogMapper;

    @Override
    public void addLog(OperateLog operateLog) {
        operateLogMapper.insertSelective(operateLog);
    }

    @Override
    public void add(String type, String desc, Long userId) {
 String channel = MyTokenUtils.getCurrentRequest().getHeader("channel");
        if (channel == null) {
            channel = "";
        }        OperateLog log = new OperateLog();
        log.setOperateId(userId);
        int maxLength = desc.length() > 1024 ? 1024 : desc.length();
        log.setDescription(desc.substring(0, maxLength));
        log.setType(type);
        log.setChannel(channel);
        log.setCreateTime(new Date());
        if (MyTokenUtils.Channel.mobile.equalsIgnoreCase(channel)) {
            AppInfoDto appInfo = MyTokenUtils.getAppInfo();
            log.setSystem(appInfo.getSystem());
            log.setVersion(appInfo.getVersion());
            log.setModel(appInfo.getModel());
            log.setAppVersion(appInfo.getAppVersion());
            log.setImei(appInfo.getImei());
            log.setCodePushLabel(appInfo.getCodePushLabel());
            log.setCodePushEnvironment(appInfo.getCodePushEnvironment());
        }
        operateLogMapper.insertSelective(log);
    }

    @Override
    public void batchInsert(List<OperateLog> operateLogList, Long userId) {
        for (OperateLog operateLog : operateLogList) {
            operateLog.setOperateId(userId);
            operateLog.setCreateTime(new Date());
            if (StringUtils.isNotEmpty(operateLog.getDescription())) {
                int maxLength = operateLog.getDescription().length() > 1024 ? 1024 : operateLog.getDescription().length();
                operateLog.setDescription(operateLog.getDescription().substring(0, maxLength));
            }
        }
        operateLogMapper.batchInsertSelective(operateLogList);
    }

    @Override
    public OperateLogVo selectByCondition(LogQueryConditionVo conditionVo) {
        OperateLogVo result = new OperateLogVo();
        conditionVo.setHasOther(false);
        //locations中有other字段,处理未选中type，把未选中的type排除 todo:有other时，筛选条件为not like locations
        if (conditionVo.getLocations() != null && !conditionVo.getLocations().isEmpty() && conditionVo.getLocations().contains("other")) {
            conditionVo.setHasOther(true);
            if (conditionVo.getLocations().size() > 1) {
                List<String> notIns = new ArrayList<>();
                for (String type : LogQueryConditionVo.types) {
                    if (!conditionVo.getLocations().contains(type)) {
                        notIns.add(type);
                    }
                }
                //web页面type全部勾选
                if (notIns.size() == LogQueryConditionVo.types.size()) {
                    //设为Null,则不对表进行过滤
                    conditionVo.setLocations(null);
                } else {
                    conditionVo.setLocations(notIns);
                }
            } else {
                //locations只带有other,则把所有type放进locations进行排除
                conditionVo.setLocations(LogQueryConditionVo.types);
            }
        }

        List<OperateLog> operateLogList = operateLogMapper.selectByCondition(conditionVo);

        // 过滤绩效中的敏感数据
        for (OperateLog operateLog : operateLogList) {
            if (OperateLog.Type.user_performance_modify.equals(operateLog.getType()) && operateLog.getDescription() != null) {
                operateLog.setDescription(operateLog.getDescription().split("，")[0]);
            }
        }

        double totalCount = operateLogMapper.selectCountByCondition(conditionVo);
        result.setOperateLogList(operateLogList);
        result.setTotalPages((long) Math.ceil(totalCount / conditionVo.getPageSize()));
        return result;
    }

    public void addStateAccessLog(String state, Long userId) {
        OperateLog webStateChangeLog = new OperateLog();
        webStateChangeLog.setChannel("pc");
        webStateChangeLog.setType(state);
        User user = MyTokenUtils.getCurrentUser();
        webStateChangeLog.setOperateId(user.getId());
        webStateChangeLog.setDescription(user.getName() + "访问了web端的页面:" + state);
        webStateChangeLog.setCreateTime(new Date());
        operateLogMapper.insertSelective(webStateChangeLog);
    }
}
