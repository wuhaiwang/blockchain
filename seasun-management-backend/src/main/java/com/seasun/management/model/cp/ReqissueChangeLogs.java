package com.seasun.management.model.cp;

public class ReqissueChangeLogs {
    private Long id;

    private Long historyId;

    private String changeFieldName;

    private String preValue;

    private String postValue;

    private Integer status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getHistoryId() {
        return historyId;
    }

    public void setHistoryId(Long historyId) {
        this.historyId = historyId;
    }

    public String getChangeFieldName() {
        return changeFieldName;
    }

    public void setChangeFieldName(String changeFieldName) {
        this.changeFieldName = changeFieldName == null ? null : changeFieldName.trim();
    }

    public String getPreValue() {
        return preValue;
    }

    public void setPreValue(String preValue) {
        this.preValue = preValue == null ? null : preValue.trim();
    }

    public String getPostValue() {
        return postValue;
    }

    public void setPostValue(String postValue) {
        this.postValue = postValue == null ? null : postValue.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}