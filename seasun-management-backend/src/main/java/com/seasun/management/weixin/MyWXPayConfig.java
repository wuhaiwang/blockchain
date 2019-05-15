package com.seasun.management.weixin;

import com.github.wxpay.sdk.WXPayConfig;
import com.seasun.management.exception.UserInvalidOperateException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.*;

@Component
public class MyWXPayConfig implements WXPayConfig {
    //证书
    private byte[] certData;
    //appId
    @Value("${wx.appID}")
    private String appID ;
    //商户Id
    @Value("${wx.mchID}")
    private String mchID ;
    //apiKey
    @Value("${wx.key}")
    private String key ;
    // 连接后读取超时时间 单位:毫秒
    private int httpReadTimeoutMs = 8000;
    // 连接等待时间 单位:毫秒
    private int httpConnectTimeoutMs = 10000;
    //证书路径
    @Value("${wx.cert.path}")
    private String certPath;

    private MyWXPayConfig() {
    }

    @Override
    public String getAppID() {
        return appID;
    }

    @Override
    public String getMchID() {
        return mchID;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public InputStream getCertStream() {
        try {
            File file = new File(certPath);
            InputStream certStream = new FileInputStream(file);
            this.certData = new byte[(int) file.length()];
            certStream.read(this.certData);
            certStream.close();
        } catch (IOException e) {
            throw new UserInvalidOperateException("微信支付证书异常");
        }
        ByteArrayInputStream certBis = new ByteArrayInputStream(this.certData);
        return certBis;
    }

    @Override
    public int getHttpConnectTimeoutMs() {
        return httpConnectTimeoutMs;
    }

    @Override
    public int getHttpReadTimeoutMs() {
        return httpReadTimeoutMs;
    }

    //todo:使用沙箱环境，需先从微信服务器拿到sandboxKey
   /* private String getSandboxSignKey() throws Exception {
        Map<String, String> reqMap = new HashMap<String, String>();
        reqMap.put(WXReturnFieldConstant.mch_id, this.mchID);
        reqMap.put(WXReturnFieldConstant.nonce_str,  WXPayUtil.generateNonceStr());
        reqMap.put(WXReturnFieldConstant.sign, WXPayUtil.generateSignature(reqMap, this.key));
        String xmlStr = WXPayUtil.mapToXml(reqMap);
        String url = "https://api.mch.weixin.qq.com/sandbox/pay/getsignkey";
        String response = MyHttpClientUtils.doPost(url, xmlStr, "application/json");
        Map<String, String> stringStringMap = WXPayUtil.xmlToMap(response);
        return stringStringMap.get("sandbox_signkey");
    }*/

}