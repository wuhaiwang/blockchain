package com.seasun.management.dto.dataCenter;

import java.math.BigDecimal;

public class VDWChargeLogDto {

    private String fillDatetime;

    private BigDecimal cardType;// 面值

    private BigDecimal cardAmount;// 充值数目

    private BigDecimal fillType;

    public String getFillDatetime() {
        return fillDatetime;
    }

    public void setFillDatetime(String fillDatetime) {
        this.fillDatetime = fillDatetime;
    }

    public BigDecimal getCardType() {
        return cardType;
    }

    public void setCardType(BigDecimal cardType) {
        this.cardType = cardType;
    }

    public BigDecimal getCardAmount() {
        return cardAmount;
    }

    public void setCardAmount(BigDecimal cardAmount) {
        this.cardAmount = cardAmount;
    }

    public BigDecimal getFillType() {
        return fillType;
    }

    public void setFillType(BigDecimal fillType) {
        this.fillType = fillType;
    }
}
