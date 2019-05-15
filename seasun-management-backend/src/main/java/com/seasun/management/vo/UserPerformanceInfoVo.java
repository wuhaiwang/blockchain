package com.seasun.management.vo;


public class UserPerformanceInfoVo {

    private String[] columns;

    public String[] getColumns() {
        return columns.clone();
    }

    public void setColumns(String[] columns) {
        this.columns = columns.clone();
    }
}
