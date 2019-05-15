package com.seasun.management.interceptor;

import com.seasun.management.common.WechatCropHttpComponent;
import com.seasun.management.constant.WechatCropConstant;
import com.seasun.management.exception.WechatCropException;
import com.seasun.management.vo.WechatCropAccessToken;
import com.seasun.management.vo.WechatCropBaseVo;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
@Aspect
public class WechatCropRequestInterceptor {

    Logger logger = LoggerFactory.getLogger(WechatCropRequestInterceptor.class);

    @Autowired
    WechatCropHttpComponent wechatCropHttpComponent;


    @Pointcut("execution(public * com.seasun.management.common.WechatCropHttpComponent.httpRequest*(..))")
    public void requestPoint () {

    }

    @Around(value = "requestPoint()")
    public Object log(ProceedingJoinPoint jp) throws Throwable {
        logger.info("开始拦截 ----");
        Object args[] = jp.getArgs();
        MethodSignature methodSignature = (MethodSignature)jp.getSignature();
        logger.debug ("method -> {}", methodSignature.getName());
        logger.debug ("args -> {}, length -> {}", args, jp.getArgs().length);
        WechatCropBaseVo wechatCropBaseVo = (WechatCropBaseVo)jp.proceed();
        if (wechatCropBaseVo.getErrcode().equals(WechatCropConstant.WechatCropResponseBodyEnum.TOKENERROR.getErrcode()) || (wechatCropBaseVo.getErrcode().equals(WechatCropConstant.WechatCropResponseBodyEnum.TOKENMISSING.getErrcode())) || (wechatCropBaseVo.getErrcode().equals(WechatCropConstant.WechatCropResponseBodyEnum.TOKENEXPIRE.getErrcode()))) {
            logger.error ("微信企业号 access_token 过期, 即将重新授权 === ");
            WechatCropAccessToken oldToken = wechatCropHttpComponent.getWechatCropAccessTokenFromCache();
            WechatCropAccessToken accessToken = wechatCropHttpComponent.requestAccessToken(WechatCropConstant.WechatCropApp.CROPCONCATAPP);
            oldToken.setAccessToken(accessToken.getAccessToken());
            oldToken.setExpiresIn(accessToken.getExpiresIn());
            wechatCropHttpComponent.putWechatCropAccessTokenIntoCache(oldToken);
            WechatCropAccessToken cropAccessToken =(WechatCropAccessToken) args[1];
            cropAccessToken.setAccessToken(accessToken.getAccessToken());
            cropAccessToken.setExpiresIn(accessToken.getExpiresIn());
            logger.error ("微信企业号 access_token 过期, 完成重新授权 === ");
            wechatCropBaseVo = (WechatCropBaseVo)jp.proceed();
            logger.info (" ========== 重新执行完成该方法 =========");
        } else if (wechatCropBaseVo.getErrcode() != 0L) {
            logger.error ("loginid -> {} execute failed, method -> {}", args[0], methodSignature.getName() );
        }
        logger.info("wechatCropBaseVo -> {}", wechatCropBaseVo.getErrcode());
        logger.info("结束拦截 ----");
        return wechatCropBaseVo;
    }

}
