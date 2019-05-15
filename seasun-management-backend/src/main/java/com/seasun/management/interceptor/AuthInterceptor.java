package com.seasun.management.interceptor;


import com.alibaba.fastjson.JSONObject;
import com.seasun.management.constant.SessionAttribute;
import com.seasun.management.mapper.UserTokenMapper;
import com.seasun.management.model.UserToken;
import com.seasun.management.service.UserVerifyService;
import com.seasun.management.util.MyEnvUtils;
import com.seasun.management.util.MySystemParamUtils;
import com.seasun.management.util.MyTokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@Service
public class AuthInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(AuthInterceptor.class);

    @Autowired
    UserVerifyService userVerifyService;

    @Autowired
    UserTokenMapper userTokenMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String token = MyTokenUtils.getToken(request);

        boolean success = userVerifyService.verifyToken(token, UserToken.Type.web);
        if (!success) {
            setNoAccessRightInfo(response);
            return false;
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }


    private void setNoAccessRightInfo(HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");

        JSONObject returnJson = new JSONObject();
        returnJson.put("code", 401);
        returnJson.put("message", "您没有访问权限，即将跳转");
        returnJson.put("description", "您没有访问权限，即将跳转");

        PrintWriter out = response.getWriter();
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        out.append(returnJson.toJSONString());
        out.flush();
        out.close();
    }
}
