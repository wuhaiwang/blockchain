package com.seasun.management.interceptor;

import com.seasun.management.mapper.CfgPublicApiMapper;
import com.seasun.management.model.CfgPublicApi;
import com.seasun.management.util.MyTokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class PubApiInterceptor implements HandlerInterceptor {

    private static final String PUB_TOKEN = "6#3arbE7Y@qZm@x5";

    @Autowired
    CfgPublicApiMapper cfgPublicApiMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = MyTokenUtils.getToken(request);
        if (!PUB_TOKEN.equals(token)) {
            return false;
        }

        String url = request.getRequestURI();
        CfgPublicApi cfgPublicApi = cfgPublicApiMapper.selectByUrl(url);
        if (null == cfgPublicApi || !cfgPublicApi.getOnFlag()) {
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
}
