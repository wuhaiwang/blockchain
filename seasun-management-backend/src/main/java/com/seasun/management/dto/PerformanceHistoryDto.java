package com.seasun.management.dto;

import com.seasun.management.vo.SubPerformanceBaseVo;

import java.util.List;

public class PerformanceHistoryDto {

    private int selectIndex;

    private int startIndex;

    private int endIndex;

    public int getSelectIndex() {
        return selectIndex;
    }

    public void setSelectIndex(int selectIndex) {
        this.selectIndex = selectIndex;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }
}
