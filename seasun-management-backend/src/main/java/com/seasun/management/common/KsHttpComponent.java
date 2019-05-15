package com.seasun.management.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.seasun.management.constant.ApiCode;
import com.seasun.management.util.MyEncryptorUtils;
import com.seasun.management.util.MyHttpAsyncClientUtils;
import com.seasun.management.vo.KsLifeCommonVo;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 金山荟生活的请求组件类
 */
public class KsHttpComponent extends AbstractHttpComponent {


    private static final Logger logger = LoggerFactory.getLogger(KsHttpComponent.class);
    /**
     * ks-apiType : openapi
     * ks-email : kingsoft邮箱账号前缀
     * ks-partner : 子公司名称代码（如：CMCM,SEAON,WPS)
     * ks-timestamp : 时间戳 （后台是JAVA编写，时间戳长度是13），如有需要可调用 /timestamp 获取系统时间戳校准调用端时间
     * ks-sign : 签名
     */

    private final String KsApiType = "ks-apiType";
    private final String KsEmail = "ks-email";
    private final String KsPartner = "ks-partner";
    private final String KsTimestamp = "ks-timestamp";
    private final String KsSign = "ks-sign";


    private final String KsApiTypeValue = "openapi";
    private final String KsPartnerValue = "SEASON";

    @Override
    public String doGetHttpRequest(String url, Map<String, String> urlParamMap, String loginId, RequestConfig requestConfig) {
        setHeader(loginId);
        String responseStr;
        try {
            StringBuilder urlParamBuffer = new StringBuilder();
            if (urlParamMap != null && urlParamMap.size() != 0) {

                for (Map.Entry<String, String> entry : urlParamMap.entrySet()) {
                    if (entry.getValue() != null) {
                        urlParamBuffer.append(entry.getKey()).append("=").append(entry.getValue());
                        urlParamBuffer.append("&");
                    }
                }
            }

            String urlParamBufferStr = urlParamBuffer.toString();
            if (urlParamBufferStr.length() > 0 && urlParamBufferStr.charAt(urlParamBufferStr.length() - 1) == '&') {
                urlParamBufferStr = urlParamBufferStr.substring(0,urlParamBufferStr.length()-1);
            }

            responseStr = MyHttpAsyncClientUtils.httpAsyncGet(url, urlParamBufferStr, super.getHeader().getHeaderMap(), null, requestConfig);
        } catch (Exception e) {
            logger.error("kslife get 请求失败");
            e.printStackTrace();
            return null;
        }

        return responseStr;
    }

    @Override
    public String doPostHttpRequest(String url, String body, String loginId) {
        setHeader(loginId);
        String responseStr;
        try {
            body = encrypt(body);
            JSONObject data = new JSONObject();
            data.put("data", body);
            responseStr = MyHttpAsyncClientUtils.httpAsyncPost(url, data.toJSONString(), super.getHeader().getHeaderMap(), "", null);
        } catch (Exception e) {
            logger.error("kslife POST 请求失败");
            e.printStackTrace();
            return null;
        }
        return responseStr;
    }


    /**
     * 填充HTTP请求的 Header 信息
     *
     * @param loginId 用户的loginId
     */
    private void setHeader(String loginId) {
        String timeStamp = String.valueOf(new Date().getTime());
        KsHeader ksHeader = new KsHeader(KsApiTypeValue, loginId, KsPartnerValue, timeStamp);
        super.setHeader(ksHeader);
    }

    public void setHeader(KsHeader ksHeader) {
        super.setHeader(ksHeader);
    }


    private String encrypt(String sourceStr) throws Exception {

        if (sourceStr == null) {
            return "";
        }
        String base64Source = Base64.encodeBase64String(sourceStr.getBytes("UTF-8"));
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");

        int blockSize = cipher.getBlockSize();
        byte[] sourceStrBytes = base64Source.getBytes("UTF-8");
        int sourceStrLength = sourceStrBytes.length;
        if (sourceStrLength % blockSize != 0) {
            sourceStrLength = sourceStrLength + (blockSize - (sourceStrLength % blockSize));
        }
        byte[] newSourceStrBytes = new byte[sourceStrLength];
        System.arraycopy(sourceStrBytes, 0, newSourceStrBytes, 0, sourceStrBytes.length);

        SecretKeySpec key = new SecretKeySpec(ApiCode.KsApiKey.getBytes("UTF-8"), "AES");
        IvParameterSpec iv = new IvParameterSpec(ApiCode.KsApiKey.getBytes("UTF-8"));
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] encrypted = cipher.doFinal(newSourceStrBytes);

        return Base64.encodeBase64String(encrypted);
    }

    /**
     * 金山荟生活的通用解密方法
     *
     * @param data 返回值信息
     * @return 解密后的 String
     */
    public Object ksDecryptData(String data) {

        byte[] encrypted1 = new byte[0];
        try {
            encrypted1 = Base64.decodeBase64(data.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES/CBC/NoPadding");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        try {
            SecretKeySpec key = new SecretKeySpec(ApiCode.KsApiKey.getBytes("UTF-8"), "AES");
            IvParameterSpec iv = new IvParameterSpec(ApiCode.KsApiKey.getBytes("UTF-8"));
            assert cipher != null;
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] original = new byte[0];
        try {
            original = cipher.doFinal(encrypted1);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
        String result = "";

        try {
            result = new String(original, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            result = new String(Base64.decodeBase64(result), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static KsLifeCommonVo responseToObj(String response) {
        return JSON.parseObject(response, KsLifeCommonVo.class);
    }

    /**
     * 请求的header
     */
    class KsHeader extends AbsHeader {

        KsHeader(String apiType, String email, String partner, String timeStamp) {
            headerMap = new HashMap<>();
            headerMap.put(KsApiType, apiType);
            headerMap.put(KsEmail, email);
            headerMap.put(KsPartner, partner);
            headerMap.put(KsTimestamp, timeStamp);
            headerMap.put(KsSign, getSign());
        }

        private String getSign() {
            return MyEncryptorUtils.encryptByMD5(headerMap.get(KsEmail) + headerMap.get(KsPartner) + headerMap.get(KsTimestamp) + ApiCode.KsApiKey);
        }
    }

    @Override
    public String doGetHttpRequest(String url, Map<String, String> urlParamMap, String loginId, RequestConfig requestConfig, String charsetName) {
        setHeader(loginId);
        String responseStr;
        try {
            StringBuilder urlParamBuffer = new StringBuilder();
            if (urlParamMap != null && urlParamMap.size() != 0) {

                for (Map.Entry<String, String> entry : urlParamMap.entrySet()) {
                    if (entry.getValue() != null) {
                        urlParamBuffer.append(entry.getKey()).append("=").append(entry.getValue());
                        urlParamBuffer.append("&");
                    }
                }
            }

            String urlParamBufferStr = urlParamBuffer.toString();
            if (urlParamBufferStr.length() > 0 && urlParamBufferStr.charAt(urlParamBufferStr.length() - 1) == '&') {
                urlParamBufferStr = urlParamBufferStr.substring(0,urlParamBufferStr.length()-1);
            }

            responseStr = MyHttpAsyncClientUtils.httpAsyncGet(url, urlParamBufferStr, super.getHeader().getHeaderMap(), null, requestConfig,charsetName);
        } catch (Exception e) {
            logger.error("kslife get 请求失败");
            e.printStackTrace();
            return null;
        }

        return responseStr;
    }

    /**
     * 发送body参数请求
     * @param url
     * @param postBody
     * @param loginId
     * @return
     */
    public String doPostHttpRequest(String url, List<BasicNameValuePair> postBody, String loginId,FutureCallback callback) {
        setHeader(loginId);
        String responseStr = null;
        try {
            responseStr = MyHttpAsyncClientUtils.httpAsyncPost(url, postBody,null, callback);
        } catch (Exception e) {
            logger.error("kslife POST 请求失败");
            e.printStackTrace();
            return null;
        }
        return responseStr;
    }

}
