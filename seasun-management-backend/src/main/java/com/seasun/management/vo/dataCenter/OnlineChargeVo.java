package com.seasun.management.vo.dataCenter;

import java.math.BigDecimal;


public class OnlineChargeVo {

    private String time;
    private BigDecimal dayCard;
    private BigDecimal monthCard;
    private BigDecimal gameCoins;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public BigDecimal getDayCard() {
        return dayCard;
    }

    public void setDayCard(BigDecimal dayCard) {
        this.dayCard = dayCard;
    }

    public BigDecimal getMonthCard() {
        return monthCard;
    }

    public void setMonthCard(BigDecimal monthCard) {
        this.monthCard = monthCard;
    }

    public BigDecimal getGameCoins() {
        return gameCoins;
    }

    public void setGameCoins(BigDecimal gameCoins) {
        this.gameCoins = gameCoins;
    }


}
