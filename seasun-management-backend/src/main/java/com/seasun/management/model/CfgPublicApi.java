package com.seasun.management.model;

public class CfgPublicApi {
    private Long id;

    private String url;

    private Boolean onFlag;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }

    public Boolean getOnFlag() {
        return onFlag;
    }

    public void setOnFlag(Boolean onFlag) {
        this.onFlag = onFlag;
    }
}