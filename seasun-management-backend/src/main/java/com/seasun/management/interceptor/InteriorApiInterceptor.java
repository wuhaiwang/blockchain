package com.seasun.management.interceptor;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 内部子系统接口调用拦截器
 * */
@Service
public class InteriorApiInterceptor implements HandlerInterceptor {

    private static final String KEY = "cBPXN";

    private static final String SECRET = "ee9T6hZYX6d0YRCz";

    private static final Long _15_MINS_MILLS = 60*15*1000L;

    Logger logger = LoggerFactory.getLogger(InteriorApiInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        logger.info ("querystr => {}", request.getQueryString());
        return validate(request,response);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

    }

    private Boolean validate (HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        final String sign = httpServletRequest.getParameter("sign");
        final String key = httpServletRequest.getParameter("key");
        Long timestamp = 0L;
        Long nonce = 0l;
        try{
            timestamp = Long.parseLong( httpServletRequest.getParameter("timestamp"));
            nonce =  Long.parseLong( httpServletRequest.getParameter("nonce"));
        }catch(RuntimeException e) {
            logger.error("转换参数错误, timestamp => {}, nonce => {}",httpServletRequest.getParameter("timestamp"), httpServletRequest.getParameter("nonce"));
            setNoAccessRightInfo(httpServletResponse, "timestamp或者nonce必须为整形数字");
            return Boolean.FALSE;
        }

        return validate (key, timestamp, nonce, sign, httpServletResponse);
    }


    /**
     * @param key
     * @param timestamp 长度13
     * @param nonce
     * @param sign
     * */
    private Boolean validate (final String key, final long timestamp, final long nonce, final String sign, final HttpServletResponse httpServletResponse) {
        logger.info ("内部子系统接口调用, 参数 key => {}, timestamp => {}, nonce => {} , sign =>{}", key, timestamp, nonce, sign);
        if (!validateStr(key) || !validateStr(sign)) {
            logger.error ("签名校验失败, 参数为空");
            setNoAccessRightInfo(httpServletResponse, "签名校验失败, 请补齐key和sign");
            return Boolean.FALSE;
        }

        if (!KEY.equals(key)) {
            logger.error ("签名校验失败, key错误");
            setNoAccessRightInfo(httpServletResponse, "签名校验失败, key错误");
            return Boolean.FALSE;
        }

        long now = System.currentTimeMillis();
        long range = Math.abs(now-timestamp);
        if (range>=_15_MINS_MILLS) {
            logger.error ("时间范围只能为15分钟,校验出错 , range => {}", range/1000*60);
            setNoAccessRightInfo(httpServletResponse, "时间戳校验出错");
            return  Boolean.FALSE;
        }
        List list = new ArrayList<>();
        list.add(key);
        list.add(SECRET);
        list.add(timestamp + "");
        list.add (nonce + "");
        Collections.sort(list);
        StringBuilder sb = new StringBuilder();

        list.stream().forEach(item->{
            sb.append(item);
        });

        logger.info ("排序后 => {}", sb);

        final String encrypt = DigestUtils.sha256Hex(sb.toString().getBytes());
        logger.info ("服务器加密 => {}, 客户端加密 => {}", encrypt, sign);
        if (!sign.equals(encrypt)) {
            logger.error ("签名错误");
            setNoAccessRightInfo(httpServletResponse, "签名错误");
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    private Boolean validateStr (final String text) {
        if (StringUtils.isBlank(text)) return Boolean.FALSE;
        return Boolean.TRUE;
    }

    private void setNoAccessRightInfo (HttpServletResponse response, String message) {

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");

        JSONObject returnJson = new JSONObject();
        returnJson.put("code", 401);
        returnJson.put("message", message);
        returnJson.put("description", message);

        PrintWriter out = null;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        out.append(returnJson.toJSONString());
        out.flush();
        out.close();

    }

}
