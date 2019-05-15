package com.seasun.management.interceptor;


import com.seasun.management.annotation.AccessLog;
import com.seasun.management.helper.UserPerformanceHelper;
import com.seasun.management.model.User;
import com.seasun.management.service.OperateLogService;
import com.seasun.management.util.MyTokenUtils;
import org.apache.poi.util.StringUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Aspect
@Configuration
public class AccessLogInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(AccessLogInterceptor.class);

    @Autowired
    OperateLogService operateLogService;

    @Pointcut("execution(public * com.seasun.management.controller..*.*(..))")
    public void logPointCut() {
    }

    @Around(value = "logPointCut()"
            + "&& target(bean) "
            + "&& @annotation(com.seasun.management.annotation.AccessLog)"
            + "&& @annotation(accessLog)", argNames = "jp,bean,accessLog")
    public Object log(ProceedingJoinPoint jp, Object bean, AccessLog accessLog) throws Throwable {
        User user = MyTokenUtils.getCurrentUser();
        // 无效日志，不处理
        if (accessLog.tag().equals("default") && accessLog.message().equals("default")) {
            logger.debug("pointless log content,will skip ...");
        }
        // 若配置为只记录boss，而当前用户不是boss,则不处理
        else if (accessLog.onlyEnableBoss() && !MyTokenUtils.isBoss(user)) {
            logger.debug("current user is not boss,will skip ...");
        }
        // 其他情况，记录日志
        else {
            String channel = MyTokenUtils.getChannel();
            //String operator = user.getName() == null ? "" + user.getId() : user.getName();
            //user.getName() 不可能为空，getName = StringA + StringB
            String operator = StringUtils.isEmpty(user.getName()) ? "" + user.getId() : user.getName();
            String message = operator + " 通过" + channel + accessLog.message();
            operateLogService.add(accessLog.tag(), message, user.getId());
        }

        return jp.proceed();
    }

}
