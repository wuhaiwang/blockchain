package com.seasun.management.vo;

import com.seasun.management.model.OperateLog;

import java.util.List;

public class OperateLogVo {

    private Long totalPages;

    private List<OperateLog> operateLogList;

    public Long getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Long totalPages) {
        this.totalPages = totalPages;
    }

    public List<OperateLog> getOperateLogList() {
        return operateLogList;
    }

    public void setOperateLogList(List<OperateLog> operateLogList) {
        this.operateLogList = operateLogList;
    }
}
