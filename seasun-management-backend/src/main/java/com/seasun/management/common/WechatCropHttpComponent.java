package com.seasun.management.common;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.seasun.management.constant.WechatCropConstant;
import com.seasun.management.dto.WechatCropConcatUserDto;
import com.seasun.management.util.MyHttpAsyncClientUtils;
import com.seasun.management.util.MyHttpClientUtils;
import com.seasun.management.vo.WechatCropAccessToken;
import com.seasun.management.vo.WechatCropBaseVo;
import com.seasun.management.vo.WechatCropConcatUserVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Component
public class WechatCropHttpComponent {

    static Logger logger = LoggerFactory.getLogger(WechatCropHttpComponent.class);

    private static String getUrl (WechatCropConstant.WechatCropRequestApiEnum wechatCropRequestApiEnum) {
        return wechatCropRequestApiEnum.getUrl();
    }

    private static String mapToQuerystring (Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry entry: params.entrySet()) {
            sb.append("&").append(entry.getKey()).append("=").append(entry.getValue());
        }
        String querystring  = sb.substring(1);
        return querystring;
    }

    public static List<BasicNameValuePair> mapToValuePairs (Map<String, String> params) {

        List<BasicNameValuePair> basicNameValuePairs = new LinkedList<>();
        for (Map.Entry<String, String> entry: params.entrySet()) {
            basicNameValuePairs.add (new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        return basicNameValuePairs;
    }

    public static String mapToJSONString (Map<String, Object> params) {
        JSONObject data = new JSONObject();
        for (Map.Entry<String, Object> entry: params.entrySet()) {
            data.put(entry.getKey(), entry.getValue());
        }
        return data.toJSONString();
    }



    private static <T> T simpleHttpGet(String url, Map<String, String> params, Class<T> clazz) throws Exception{
        String querystring  = mapToQuerystring(params);
        logger.info("requestParams -> {}", querystring);
        String responseStr = MyHttpAsyncClientUtils.httpAsyncGet(url, querystring, null, null, null);
        logger.info("responseStr -> {}", responseStr);
        T o =  JSON.parseObject(responseStr,clazz);
        return o;
    }

    private static <T> T simpleHttpPost (String url, Map<String, String> urlParams, Map<String,Object> body, Class<T> returnClzz) throws Exception {
        String responseStr = MyHttpAsyncClientUtils.httpAsyncPost(url, mapToJSONString(body) , null, mapToQuerystring(urlParams), null);
        logger.info("responseStr -> {}", responseStr);
        T o =  JSON.parseObject(responseStr,returnClzz);
        return o;
    }

    private static <T> T simpleWechatCropRequest (WechatCropConstant.WechatCropRequestApiEnum wechatCropRequestApiEnum, Map<String, String> urlParams, Class<T> returnClazz) throws Exception{
        final String url = getUrl(wechatCropRequestApiEnum);
        return simpleHttpGet(url, urlParams, returnClazz);
    }

    private static <T> T simpleWechatCropConcatUserRequest (WechatCropConstant.WechatCropRequestApiEnum wechatCropRequestApiEnum, String loginId, WechatCropAccessToken accessToken, Class<T> returnClazz) throws Exception {
        Map<String, String> urlParams = new HashMap<>();
        if (StringUtils.isBlank(accessToken.getAccessToken())) {
            urlParams.put("access_token", "");
        }else {
            urlParams.put("access_token", accessToken.getAccessToken());
        }
        urlParams.put ("userid", loginId);
        return simpleWechatCropRequest (wechatCropRequestApiEnum, urlParams, returnClazz);
    }

    private static <T> T simpleWechatCropConcatUserRequest (WechatCropConstant.WechatCropRequestApiEnum wechatCropRequestApiEnum, Map<String, String> urlParams, Class<T> returnClazz) throws Exception{
        final String url = getUrl(wechatCropRequestApiEnum);
        return simpleHttpGet(url, urlParams, returnClazz);
    }

    @CachePut("wechatCropAccessToken")
    public WechatCropAccessToken putWechatCropAccessTokenIntoCache (WechatCropAccessToken wechatCropAccessToken) {
        return wechatCropAccessToken;
    }

    @Cacheable("wechatCropAccessToken")
    public WechatCropAccessToken getWechatCropAccessTokenFromCache () throws Exception{
        return requestAccessToken(WechatCropConstant.WechatCropApp.CROPCONCATAPP);
    }

    public WechatCropAccessToken requestAccessToken ( WechatCropConstant.WechatCropApp app) throws Exception{
        Map<String, String> map = new HashMap<>();
        map.put("corpid", app.CROPCONCATAPP.getCropId());
        map.put("corpsecret", app.CROPCONCATAPP.getSecret());
        return simpleWechatCropConcatUserRequest (WechatCropConstant.WechatCropRequestApiEnum.GETACCESSTOKEN, map,  WechatCropAccessToken.class);
    }

    public WechatCropConcatUserVo httpRequestFindWechatCropConcatUserByLoginId (String loginId, WechatCropAccessToken accessToken) throws Exception{
        return simpleWechatCropConcatUserRequest (WechatCropConstant.WechatCropRequestApiEnum.GETCROPUSER, loginId, accessToken, WechatCropConcatUserVo.class);
    }

    public WechatCropBaseVo httpRequestCreateWechatCropConcatUser (WechatCropConcatUserDto wechatCropConcatUserDto, WechatCropAccessToken accessToken) throws Exception {
        String url = getUrl(WechatCropConstant.WechatCropRequestApiEnum.CREATECROPUSER);
        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("access_token", accessToken.getAccessToken());
        Map<String, Object> body = new HashMap<>();
        body.put("userid", wechatCropConcatUserDto.getUserid());
        body.put("name", wechatCropConcatUserDto.getName());
        body.put("mobile", wechatCropConcatUserDto.getMobile());
        body.put("gender", wechatCropConcatUserDto.getGender());
        body.put("email", wechatCropConcatUserDto.getEmail());
        body.put("status", wechatCropConcatUserDto.getStatus());
        body.put("isleader", wechatCropConcatUserDto.getIsleader());
        body.put("department",  wechatCropConcatUserDto.getDepartment());
        return simpleHttpPost(url, urlParams, body, WechatCropBaseVo.class);
    }

    public WechatCropBaseVo httpRequestDeleteWechatCropConcatUser (String loginId, WechatCropAccessToken accessToken) throws Exception {
        return simpleWechatCropConcatUserRequest (WechatCropConstant.WechatCropRequestApiEnum.DELETEUSER, loginId, accessToken, WechatCropBaseVo.class);
    }

}
