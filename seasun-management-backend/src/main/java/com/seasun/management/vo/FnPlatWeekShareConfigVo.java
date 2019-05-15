package com.seasun.management.vo;

import java.math.BigDecimal;
import java.util.List;

public class FnPlatWeekShareConfigVo {
    private Boolean lockFlag;
    private List<WeekShareConfigVo> platProcessList;

    public Boolean getLockFlag() {
        return lockFlag;
    }

    public void setLockFlag(Boolean lockFlag) {
        this.lockFlag = lockFlag;
    }

    public List<WeekShareConfigVo> getPlatProcessList() {
        return platProcessList;
    }

    public void setPlatProcessList(List<WeekShareConfigVo> platProcessList) {
        this.platProcessList = platProcessList;
    }


}
