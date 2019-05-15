package com.seasun.management.vo.dataCenter;

import com.seasun.management.dto.dataCenter.StatOnlineAccountDto;

import java.math.BigDecimal;
import java.util.List;


public class StatOnlineNumVo {

    private List<StatOnlineAccountDto> currentData;
    private List<StatOnlineAccountDto> previousData;

    public List<StatOnlineAccountDto> getCurrentData() {
        return currentData;
    }

    public void setCurrentData(List<StatOnlineAccountDto> currentData) {
        this.currentData = currentData;
    }

    public List<StatOnlineAccountDto> getPreviousData() {
        return previousData;
    }

    public void setPreviousData(List<StatOnlineAccountDto> previousData) {
        this.previousData = previousData;
    }
}
