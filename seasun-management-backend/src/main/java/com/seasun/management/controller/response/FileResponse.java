package com.seasun.management.controller.response;

public class FileResponse {
    private int code;
    private String url;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public FileResponse(int code, String url) {
        this.code = code;
        this.url = url;
    }
}
