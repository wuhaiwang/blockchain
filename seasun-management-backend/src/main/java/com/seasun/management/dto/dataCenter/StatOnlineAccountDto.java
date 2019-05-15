package com.seasun.management.dto.dataCenter;

import java.math.BigDecimal;


public class StatOnlineAccountDto {

    private BigDecimal accountSum;

    private String statTime;
    public StatOnlineAccountDto(){

    }
    public StatOnlineAccountDto(String statTime,BigDecimal accountSum){
        this.statTime= statTime;
        this.accountSum = accountSum;
    }
    public BigDecimal getAccountSum() {
        return accountSum;
    }

    public void setAccountSum(BigDecimal accountSum) {
        this.accountSum = accountSum;
    }

    public String getStatTime() {
        return statTime;
    }

    public void setStatTime(String statTime) {
        this.statTime = statTime;
    }
}
