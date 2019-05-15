package com.seasun.management.model;

import java.util.Date;

public class OrderErrorLog {
    private Long id;

    private Long userId;

    private String clientFullInfo;

    private String response;

    private String errorMessage;

    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getClientFullInfo() {
        return clientFullInfo;
    }

    public void setClientFullInfo(String clientFullInfo) {
        this.clientFullInfo = clientFullInfo == null ? null : clientFullInfo.trim();
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response == null ? null : response.trim();
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage == null ? null : errorMessage.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}