package com.seasun.management.service.kingsoftLife;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.seasun.management.common.KsHttpComponent;
import com.seasun.management.constant.ErrorMessage;
import com.seasun.management.util.MyTokenUtils;
import com.seasun.management.vo.KsLifeCommonVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.config.RequestConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class KsUserServiceImpl implements KsUserService {


    private static final Logger logger= LoggerFactory.getLogger(KsUserServiceImpl.class);
    @Value("${ks.life.address}")
    private String address;

    @Override
    public Object getUserSeatNo(String loginId) {

        if (loginId == null) {
            loginId = MyTokenUtils.getCurrentUser().getLoginId();
        }

        String userSeatNoUrl = "/user/seatNo";
        KsHttpComponent component = new KsHttpComponent();

        // 业务决定的 requestConfig
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(2000)
                .setSocketTimeout(1000).build();

        String response = component.doGetHttpRequest(address + userSeatNoUrl, null, loginId, requestConfig);
        if (StringUtils.isNotBlank(response)) {
            KsLifeCommonVo ksLifeCommonVo;
            try{
                ksLifeCommonVo = KsHttpComponent.responseToObj(response);
            }catch (Exception e){
                logger.info("parse json fail...");
                return null;
            }

            if(ksLifeCommonVo.getData()!=null) {
                if ("false".equals(ksLifeCommonVo.getSuccess())) {
                    return "暂未绑定卡位";
                }
                String data = (String) component.ksDecryptData(ksLifeCommonVo.getData().toString());
                JSONObject dataJson = JSON.parseObject(data);
                if (StringUtils.isBlank(dataJson.get("seatNo").toString())) {
                    return "暂未绑定卡位";
                }
                return dataJson.get("seatNo");
            }
        }
        return null;
    }

    @Override
    public KsLifeCommonVo unbindSeatNo() {

        String loginId = MyTokenUtils.getCurrentUser().getLoginId();
        String unBindUrl = "/user/unbindSeatNo";
        KsHttpComponent component = new KsHttpComponent();
        String response = component.doPostHttpRequest(address + unBindUrl, null, loginId);
        KsLifeCommonVo result = KsHttpComponent.responseToObj(response);
        Object decryptedData = component.ksDecryptData(result.getData().toString());
        result.setData(decryptedData);
        return result;
    }

    @Override
    public KsLifeCommonVo bindSeatNo(String seatNoStr) {

        String loginId = MyTokenUtils.getCurrentUser().getLoginId();
        String bindUrl = "/user/bindSeatNo";
        KsHttpComponent component = new KsHttpComponent();
        JSONObject seatNo = new JSONObject();
        seatNo.put("seatNo", seatNoStr);
        String response = component.doPostHttpRequest(address + bindUrl, seatNo.toString(), loginId);
        KsLifeCommonVo result = KsHttpComponent.responseToObj(response);
        Object decryptedData = component.ksDecryptData(result.getData().toString());
        result.setData(decryptedData);
        if ("user not found".equals(result.getMsg())) {
            result.setMsg(ErrorMessage.KOA_USER_ERROR);
        }
        return result;
    }

    @Override
    public KsLifeCommonVo getAvatar(String loginId) {
        if (StringUtils.isEmpty(loginId)) {
            return null;
        }
        KsHttpComponent component = new KsHttpComponent();
        String response = component.doGetHttpRequest(address + "/user/avatar",null,loginId,null);
        KsLifeCommonVo result = KsHttpComponent.responseToObj(response);
        JSONObject json = JSON.parseObject(String.valueOf(component.ksDecryptData(result.getData().toString())));
        result.setData(json.get("avatar"));
        return result;
    }
}
