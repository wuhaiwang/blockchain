package com.seasun.management.vo;

import java.math.BigDecimal;

public class FnUserPlatWeekShareConfigVo extends  FnPlatWeekShareConfigVo{
    private String remark;
    private Long remarkId;
    private BigDecimal workday;
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getWorkday() {
        return workday;
    }

    public void setWorkday(BigDecimal workday) {
        this.workday = workday;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getRemarkId() {
        return remarkId;
    }

    public void setRemarkId(Long remarkId) {
        this.remarkId = remarkId;
    }
}
