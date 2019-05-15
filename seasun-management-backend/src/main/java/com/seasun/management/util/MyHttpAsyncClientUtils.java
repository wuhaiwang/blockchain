package com.seasun.management.util;


import com.seasun.management.util.http.HttpClientFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;


// http 异步请求封装类
public class MyHttpAsyncClientUtils {

    private static Logger logger = LoggerFactory.getLogger(MyHttpAsyncClientUtils.class);
    private static String utf8Charset = "utf-8";

    /**
     * 向指定的url发送一次异步post请求,参数是字符串
     *
     * @param baseUrl    请求地址
     * @param postString 请求参数,格式是json.toString()
     * @param urlParams  请求参数,格式是String
     * @param callback   回调方法,格式是FutureCallback
     * @return 返回结果, 请求失败时返回null
     * @apiNote http接口处用 @RequestParam接收参数
     */
    public static String httpAsyncPost(String baseUrl, String postString, Map<String, String> headerMsg,
                                       String urlParams, FutureCallback callback) throws Exception {
        Future<HttpResponse> response = null;
        StringBuffer stringBuffer = new StringBuffer();
        if (baseUrl == null || "".equals(baseUrl)) {
            logger.warn("we don't have base url, check config");
            throw new Exception("missing base url");
        }
        CloseableHttpAsyncClient hc = HttpClientFactory.getInstance().getHttpAsyncClientPool()
                .getAsyncHttpClient();
        try {
            hc.start();
            HttpPost httpPost = new HttpPost(baseUrl);
            httpPost.setHeader("Connection", "close");
            if (headerMsg != null && headerMsg.size() != 0) {
                for (Map.Entry<String, String> header : headerMsg.entrySet()) {
                    httpPost.setHeader(header.getKey(), header.getValue());
                }
            }
            if (null != postString) {
                logger.debug("exeAsyncReq post postBody={}", postString);
                StringEntity entity = new StringEntity(postString, utf8Charset);
                entity.setContentEncoding("UTF-8");
                entity.setContentType("application/json");
                httpPost.setEntity(entity);
            }
            if (null != urlParams && !urlParams.equals("")) {
                httpPost.setURI(new URI(httpPost.getURI().toString()
                        + "?" + urlParams));
            }
            logger.warn("exeAsyncReq getparams:" + httpPost.getURI());
            response = hc.execute(httpPost, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpEntity httpEntity = response.get().getEntity();
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(httpEntity.getContent()));
            String ln;
            while ((ln = in.readLine()) != null) {
                stringBuffer.append(ln);
                stringBuffer.append("\r\n");
            }
            EntityUtils.consume(httpEntity);
        } finally {
            if (in != null) {
                in.close();
            }
        }
        return stringBuffer.toString();
    }


    /**
     * 向指定的url发送一次异步post请求,参数是字符串
     *
     * @param baseUrl   请求地址
     * @param urlParams 请求参数,格式是List<BasicNameValuePair>
     * @param callback  回调方法,格式是FutureCallback
     * @return 返回结果, 请求失败时返回null
     * @apiNote http接口处用 @RequestParam接收参数
     */
    public static String httpAsyncPost(String baseUrl, List<BasicNameValuePair> postBody,
                                       List<BasicNameValuePair> urlParams, FutureCallback callback) throws Exception {
        Future<HttpResponse> response = null;
        String responseMsg = null;
        if (baseUrl == null || "".equals(baseUrl)) {
            logger.warn("we don't have base url, check config");
            throw new Exception("missing base url");
        }

        try {
            CloseableHttpAsyncClient hc = HttpClientFactory.getInstance().getHttpAsyncClientPool()
                    .getAsyncHttpClient();
            hc.start();
            HttpPost httpPost = new HttpPost(baseUrl);
            httpPost.setHeader("Connection", "close");
            if (null != postBody) {
                logger.debug("exeAsyncReq post postBody={}", postBody);
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
                        postBody, "UTF-8");
                httpPost.setEntity(entity);
            }

            if (null != urlParams) {
                String getUrl = EntityUtils
                        .toString(new UrlEncodedFormEntity(urlParams));
                httpPost.setURI(new URI(httpPost.getURI().toString()
                        + "?" + getUrl));
            }
            logger.warn("exeAsyncReq getparams:" + httpPost.getURI());
            response = hc.execute(httpPost, callback);
            if(response!=null) {
                HttpEntity entity = response.get().getEntity();
                responseMsg = EntityUtils.toString(entity, "utf-8");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return responseMsg;
    }

    /**
     * 向指定的url发送一次异步get请求,参数是String
     *
     * @param baseUrl   请求地址
     * @param urlParams 请求参数,格式是String
     * @param callback  回调方法,格式是FutureCallback
     * @return 返回结果, 请求失败时返回null
     * @apiNote http接口处用 @RequestParam接收参数
     */
    public static String httpAsyncGet(String baseUrl, String urlParams, Map<String, String> headerMsg, FutureCallback callback, RequestConfig requestConfig) throws Exception {
        Future<HttpResponse> response = null;
        StringBuffer stringBuffer = new StringBuffer();
        if (baseUrl == null || "".equals(baseUrl)) {
            logger.warn("we don't have base url, check config");
            throw new Exception("missing base url");
        }
        CloseableHttpAsyncClient hc = HttpClientFactory.getInstance().getHttpAsyncClientPool()
                .getAsyncHttpClient();
        try {
            hc.start();
            HttpGet httpGet = getHttpGet(baseUrl, urlParams, headerMsg, callback, requestConfig);
            response = hc.execute(httpGet, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpEntity httpEntity = response.get().getEntity();
        String str = getResponseStr(httpEntity.getContent(),null);
        EntityUtils.consume(httpEntity);
        return str;
    }

    /**
     * 向指定的url发送一次异步get请求,参数是List<BasicNameValuePair>
     *
     * @param baseUrl   请求地址
     * @param urlParams 请求参数,格式是List<BasicNameValuePair>
     * @param callback  回调方法,格式是FutureCallback
     * @return 返回结果, 请求失败时返回null
     * @apiNote http接口处用 @RequestParam接收参数
     */
    public static void httpAsyncGet(String baseUrl, List<BasicNameValuePair> urlParams, Map<String, String> headerMsg, FutureCallback callback) throws Exception {
        if (baseUrl == null || "".equals(baseUrl)) {
            logger.warn("we don't have base url, check config");
            throw new Exception("missing base url");
        }
        try {
            CloseableHttpAsyncClient hc = HttpClientFactory.getInstance().getHttpAsyncClientPool()
                    .getAsyncHttpClient();
            hc.start();
            HttpPost httpGet = new HttpPost(baseUrl);
            httpGet.setHeader("Connection", "close");

            if (headerMsg != null && headerMsg.size() != 0) {
                for (Map.Entry<String, String> header : headerMsg.entrySet()) {
                    httpGet.setHeader(header.getKey(), header.getValue());
                }
            }

            if (null != urlParams) {
                String getUrl = EntityUtils
                        .toString(new UrlEncodedFormEntity(urlParams));
                httpGet.setURI(new URI(httpGet.getURI().toString()
                        + "?" + getUrl));
            }
            logger.warn("exeAsyncReq getparams:" + httpGet.getURI());
            hc.execute(httpGet, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取httpGet
     * @param baseUrl
     * @param urlParams
     * @param headerMsg
     * @param callback
     * @param requestConfig
     * @return
     */
    private static HttpGet getHttpGet(String baseUrl, String urlParams, Map<String, String> headerMsg, FutureCallback callback, RequestConfig requestConfig){
        HttpGet httpGet = null;
        try {
            httpGet = new HttpGet(baseUrl);
            httpGet.setHeader("Connection", "close");
            if (headerMsg != null && headerMsg.size() != 0) {
                for (Map.Entry<String, String> header : headerMsg.entrySet()) {
                    httpGet.setHeader(header.getKey(), header.getValue());
                }
            }

            if (null != urlParams && !"".equals(urlParams)) {
                httpGet.setURI(new URI(httpGet.getURI().toString()
                        + "?" + urlParams));
            } else {
                httpGet.setURI(new URI(httpGet.getURI().toString()));
            }

            if (requestConfig != null) {
                httpGet.setConfig(requestConfig);
            }

            logger.warn("exeAsyncReq getparams:" + httpGet.getURI());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return httpGet;
    }

    /**
     * 获取响应字符串
     * @param inputStream
     * @param charsetName
     * @return
     * @throws Exception
     */
    private static String getResponseStr(InputStream inputStream, String charsetName) throws Exception{
        StringBuffer stringBuffer = new StringBuffer();
        BufferedReader in = null;
        try {
            if(charsetName != null) {
                in = new BufferedReader(new InputStreamReader(inputStream, charsetName));
            }else{
                in = new BufferedReader(new InputStreamReader(inputStream));
            }
            String ln;
            while ((ln = in.readLine()) != null) {
                stringBuffer.append(ln);
                stringBuffer.append("\r\n");
            }
        } finally {
            if (in != null) {
                in.close();
            }
        }
        return stringBuffer.toString();
    }

    /**
     * 向指定的url发送一次异步get请求,参数是String
     *
     * @param baseUrl   请求地址
     * @param urlParams 请求参数,格式是String
     * @param callback  回调方法,格式是FutureCallback
     * @return 返回结果, 请求失败时返回null
     * @apiNote http接口处用 @RequestParam接收参数
     * @param charsetName 编码
     */
    public static String httpAsyncGet(String baseUrl, String urlParams, Map<String, String> headerMsg, FutureCallback callback, RequestConfig requestConfig, String charsetName) throws Exception {
        Future<HttpResponse> response = null;
        StringBuffer stringBuffer = new StringBuffer();
        if (baseUrl == null || "".equals(baseUrl)) {
            logger.warn("we don't have base url, check config");
            throw new Exception("missing base url");
        }
        CloseableHttpAsyncClient hc = HttpClientFactory.getInstance().getHttpAsyncClientPool()
                .getAsyncHttpClient();
        try {
            hc.start();
            HttpGet httpGet = getHttpGet(baseUrl, urlParams, headerMsg, callback, requestConfig);
            response = hc.execute(httpGet, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpEntity httpEntity = response.get().getEntity();
        String str = getResponseStr(httpEntity.getContent(),charsetName);
        EntityUtils.consume(httpEntity);
        return str;
    }
}