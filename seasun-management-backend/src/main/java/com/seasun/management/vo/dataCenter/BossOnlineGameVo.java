package com.seasun.management.vo.dataCenter;

import java.math.BigDecimal;

public class BossOnlineGameVo extends BossOnlineSumVo {
    private String report_name;

    public BossOnlineGameVo(String report_name) {
        this.report_name = report_name;
    }

    public BossOnlineGameVo(String time, BigDecimal value, String report_name) {
        super(time, value);
        this.report_name = report_name;
    }

    public String getReport_name() {
        return report_name;
    }

    public void setReport_name(String report_name) {
        this.report_name = report_name;
    }

}
