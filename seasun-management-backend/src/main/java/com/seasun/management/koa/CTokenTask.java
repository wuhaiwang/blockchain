package com.seasun.management.koa;

import javax.annotation.PostConstruct;

import com.seasun.management.mapper.CfgSsoKeyMapper;
import com.seasun.management.model.CfgSsoKey;
import com.seasun.management.util.MyEnvUtils;
import com.seasun.management.util.MySystemParamUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.kingsoft.sso.api.SSORequest;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

@Component
public class CTokenTask {

    @Value("${sso_loc}")
    private String ssoLoc;

    @Value("${web_id}")
    private String webId;
    @Value("${web_secret}")
    private String webSecret;

    @Value("${app_id}")
    private String appId;
    @Value("${app_secret}")
    private String appSecret;

    @Value("${sso_key_update_task_flag}")
    private String ssoKeyUpdateTaskFlag;


    @Autowired
    private CfgSsoKeyMapper cfgSsoKeyMapper;


    @PostConstruct
    public void initURL() {
        SSORequest.registBaseUrl(ssoLoc);
    }

    @Scheduled(fixedDelay = 3600000)
    public void renewCToken() {

        // 若本机不需要启动
        if (!"true".equals(ssoKeyUpdateTaskFlag)) {
            return;
        }

        // 若本机需要启动，则继续
        CfgSsoKey cfgSsoKey = new CfgSsoKey();
        cfgSsoKey.setUpdateTime(new Date());
        JSONObject webTokenObj = SSORequest.appTokenCreate(webId, webSecret);
        JSONObject appTokenObj = SSORequest.appTokenCreate(appId, appSecret);
        if (webTokenObj.getBooleanValue("success")) {
            String webTid = webTokenObj.getString("tid");
            String webPubKey = webTokenObj.getString("loginPublicKey");
            cfgSsoKey.setWebTid(webTid);
            cfgSsoKey.setWebPubKey(webPubKey);
        }
        if (appTokenObj.getBooleanValue("success")) {
            String appTid = appTokenObj.getString("tid");
            String appPubKey = appTokenObj.getString("loginPublicKey");
            cfgSsoKey.setAppTid(appTid);
            cfgSsoKey.setAppPubKey(appPubKey);
        }
        CfgSsoKey currentSsoKey = cfgSsoKeyMapper.selectOne();
        if (currentSsoKey == null || currentSsoKey.getId() == null) {
            cfgSsoKeyMapper.insert(cfgSsoKey);
        } else {
            cfgSsoKey.setId(currentSsoKey.getId());
            cfgSsoKeyMapper.updateByPrimaryKeySelective(cfgSsoKey);
        }

    }
}
