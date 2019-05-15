package com.seasun.management.dto.dataCenter;

import java.math.BigDecimal;

public class DailyDto {

    private BigDecimal ConsumeItemValue;
    private BigDecimal ActvAccountNum;
    private BigDecimal NewAccountNum;
    private BigDecimal CardMoneyValue;
    private BigDecimal ConsumeValue;
    private BigDecimal ChargeAccountNum;
    private BigDecimal NewChargeAccntNum;

    public BigDecimal getConsumeItemValue() {
        return ConsumeItemValue;
    }

    public void setConsumeItemValue(BigDecimal ConsumeItemValue) {
        this.ConsumeItemValue = ConsumeItemValue;
    }

    public BigDecimal getActvAccountNum() {
        return ActvAccountNum;
    }

    public void setActvAccountNum(BigDecimal ActvAccountNum) {
        this.ActvAccountNum = ActvAccountNum;
    }

    public BigDecimal getNewAccountNum() {
        return NewAccountNum;
    }

    public void setNewAccountNum(BigDecimal NewAccountNum) {
        this.NewAccountNum = NewAccountNum;
    }

    public BigDecimal getCardMoneyValue() {
        return CardMoneyValue;
    }

    public void setCardMoneyValue(BigDecimal CardMoneyValue) {
        this.CardMoneyValue = CardMoneyValue;
    }

    public BigDecimal getConsumeValue() {
        return ConsumeValue;
    }

    public void setConsumeValue(BigDecimal ConsumeValue) {
        this.ConsumeValue = ConsumeValue;
    }

    public BigDecimal getChargeAccountNum() {
        return ChargeAccountNum;
    }

    public void setChargeAccountNum(BigDecimal ChargeAccountNum) {
        this.ChargeAccountNum = ChargeAccountNum;
    }

    public BigDecimal getNewChargeAccntNum() {
        return NewChargeAccntNum;
    }

    public void setNewChargeAccntNum(BigDecimal NewChargeAccntNum) {
        this.NewChargeAccntNum = NewChargeAccntNum;
    }
}
