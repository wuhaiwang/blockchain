package com.seasun.management.vo;

public class WechatCropAccessToken extends WechatCropBaseVo {

    private Long  expiresIn;

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    private String accessToken;

    @Override
    public String toString() {
        return "WechatCropAccessToken{" +
                "expiresIn=" + expiresIn +
                ", accessToken='" + accessToken + '\'' +
                '}';
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
