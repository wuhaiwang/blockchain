package com.seasun.management.dto.dataCenter;

import java.math.BigDecimal;

public class VFactJx3StatDDto {

    private String statDate;

    private BigDecimal actvAccountNum;

    private BigDecimal newAccountNum;

    private BigDecimal cardMoneyValue;

    private BigDecimal consumeValue;

    private BigDecimal consumeItemValue;

    private BigDecimal chargeAccountNum;

    private BigDecimal newChargeAccntNum;

    public String getStatDate() {
        return statDate;
    }

    public void setStatDate(String statDate) {
        this.statDate = statDate;
    }

    public BigDecimal getActvAccountNum() {
        return actvAccountNum;
    }

    public void setActvAccountNum(BigDecimal actvAccountNum) {
        this.actvAccountNum = actvAccountNum;
    }

    public BigDecimal getNewAccountNum() {
        return newAccountNum;
    }

    public void setNewAccountNum(BigDecimal newAccountNum) {
        this.newAccountNum = newAccountNum;
    }

    public BigDecimal getCardMoneyValue() {
        return cardMoneyValue;
    }

    public void setCardMoneyValue(BigDecimal cardMoneyValue) {
        this.cardMoneyValue = cardMoneyValue;
    }

    public BigDecimal getConsumeValue() {
        return consumeValue;
    }

    public void setConsumeValue(BigDecimal consumeValue) {
        this.consumeValue = consumeValue;
    }

    public BigDecimal getConsumeItemValue() {
        return consumeItemValue;
    }

    public void setConsumeItemValue(BigDecimal consumeItemValue) {
        this.consumeItemValue = consumeItemValue;
    }

    public BigDecimal getChargeAccountNum() {
        return chargeAccountNum;
    }

    public void setChargeAccountNum(BigDecimal chargeAccountNum) {
        this.chargeAccountNum = chargeAccountNum;
    }

    public BigDecimal getNewChargeAccntNum() {
        return newChargeAccntNum;
    }

    public void setNewChargeAccntNum(BigDecimal newChargeAccntNum) {
        this.newChargeAccntNum = newChargeAccntNum;
    }
}
