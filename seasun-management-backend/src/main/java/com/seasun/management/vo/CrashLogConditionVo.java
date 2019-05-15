package com.seasun.management.vo;

import java.util.List;

public class CrashLogConditionVo {

    private Long totalPages;

    private List<CrashLogVo>  crashLogVoList;

    public Long getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Long totalPages) {
        this.totalPages = totalPages;
    }

    public List<CrashLogVo> getCrashLogVoList() {
        return crashLogVoList;
    }

    public void setCrashLogVoList(List<CrashLogVo> crashLogVoList) {
        this.crashLogVoList = crashLogVoList;
    }
}
