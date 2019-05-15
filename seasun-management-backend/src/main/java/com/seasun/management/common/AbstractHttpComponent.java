package com.seasun.management.common;

import org.apache.http.client.config.RequestConfig;

import java.util.Map;

public abstract class AbstractHttpComponent {

    private AbsHeader header;

    void setHeader(AbsHeader header) {
        this.header = header;
    }

    AbsHeader getHeader() {
        return header;
    }

    public abstract String doGetHttpRequest(String url, Map<String, String> urlParamMap, String loginId, RequestConfig requestConfig);

    public abstract String doPostHttpRequest(String url, String body, String loginId);
    public abstract String doGetHttpRequest(String url, Map<String, String> urlParamMap, String loginId, RequestConfig requestConfig, String charsetName);
    class AbsHeader {
        Map<String, String> headerMap;

        Map<String, String> getHeaderMap() {
            return headerMap;
        }

        public void setHeaderMap(Map<String, String> headerMap) {
            this.headerMap = headerMap;
        }
    }

}
