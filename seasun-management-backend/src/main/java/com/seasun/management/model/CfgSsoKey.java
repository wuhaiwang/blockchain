package com.seasun.management.model;

import java.util.Date;

public class CfgSsoKey {
    private Integer id;

    private String appTid;

    private String webTid;

    private String appPubKey;

    private String webPubKey;

    private Date updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAppTid() {
        return appTid;
    }

    public void setAppTid(String appTid) {
        this.appTid = appTid == null ? null : appTid.trim();
    }

    public String getWebTid() {
        return webTid;
    }

    public void setWebTid(String webTid) {
        this.webTid = webTid == null ? null : webTid.trim();
    }

    public String getAppPubKey() {
        return appPubKey;
    }

    public void setAppPubKey(String appPubKey) {
        this.appPubKey = appPubKey == null ? null : appPubKey.trim();
    }

    public String getWebPubKey() {
        return webPubKey;
    }

    public void setWebPubKey(String webPubKey) {
        this.webPubKey = webPubKey == null ? null : webPubKey.trim();
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}