package com.seasun.management.vo.dataCenter;

import java.math.BigDecimal;

public class BossOnlineSumVo {
    private String time;
    private BigDecimal value;
    public BossOnlineSumVo(){

    }
    public BossOnlineSumVo(String time,BigDecimal value){
        this.time = time;
        this.value = value;
    }
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }
}
