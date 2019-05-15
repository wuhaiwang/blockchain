package com.seasun.management.service.kingsoftLife;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.seasun.management.common.KsHttpComponent;
import com.seasun.management.constant.ErrorCode;
import com.seasun.management.controller.response.CommonResponse;
import com.seasun.management.exception.KsException;
import com.seasun.management.util.MySystemParamUtils;
import com.seasun.management.util.MyTokenUtils;
import com.seasun.management.vo.KsLifeCommonVo;
import com.seasun.management.vo.KsPositionDetailVo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class KsAuthServiceImpl implements KsAuthService {

    @Value("${ks.life.address}")
    private String address;


    @Override
    public Boolean accountExists(String emailId) {
        String loginId = MyTokenUtils.getCurrentUser().getLoginId();
        KsHttpComponent ksHttpComponent = new KsHttpComponent();
        String verifyUrl = "/login/checkAccountExists?emailId=" + emailId;
        String response = ksHttpComponent.doGetHttpRequest(address + verifyUrl, null, loginId, null);

        KsLifeCommonVo result = KsHttpComponent.responseToObj(response);
        String ksData = (String) ksHttpComponent.ksDecryptData(result.getData().toString());
        JSONObject ksDataJsonObj = JSON.parseObject(ksData, JSONObject.class);
        return (Boolean) ksDataJsonObj.get("exists");
    }

    @Override
    public String busLogin() {
        if (MySystemParamUtils.getSystemConfigWithDefaultValue(MySystemParamUtils.Key.BusModeFlag,MySystemParamUtils.DefaultValue.reportBossRealTimeFlag)) {
            String loginId = MyTokenUtils.getCurrentUser().getLoginId();
            KsHttpComponent ksHttpComponent = new KsHttpComponent();
            String response = ksHttpComponent.doGetHttpRequest("http://zhweb.kingsoft.com/lcola/index", null, loginId, null);
            KsLifeCommonVo ksResult = KsHttpComponent.responseToObj(response);

            if (Boolean.parseBoolean(ksResult.getSuccess())) {
                String ksData = (String) ksHttpComponent.ksDecryptData(ksResult.getData().toString());
                JSONObject ksDataJsonObj = JSON.parseObject(ksData, JSONObject.class);
                return (String) ksDataJsonObj.get("target"); // 返回可直接访问的url地址
            } else {
                throw new KsException(ErrorCode.USER_INVALID_OPERATE_ERROR, ksResult.getMsg());
            }
        }else{
            return null;
        }
    }

}
