package com.seasun.management.vo.dataCenter;


import java.math.BigDecimal;

public class DailyVo {

    public static class DataDetail {
        private BigDecimal index;
        private BigDecimal DoD;
        private BigDecimal MoM;

        public BigDecimal getIndex() {
            return index;
        }

        public void setIndex(BigDecimal index) {
            this.index = index;
        }

        public BigDecimal getDoD() {
            return DoD;
        }

        public void setDoD(BigDecimal doD) {
            DoD = doD;
        }

        public BigDecimal getMoM() {
            return MoM;
        }

        public void setMoM(BigDecimal moM) {
            MoM = moM;
        }
    }

    private DataDetail consumeItemValue;

    private DataDetail actvAccountNum;

    private DataDetail newAccountNum;

    private DataDetail cardMoneyValue;

    private DataDetail consumeValue;

    private DataDetail chargeAccountNum;

    private DataDetail newChargeAccntNum;

    public DataDetail getConsumeItemValue() {
        return consumeItemValue;
    }

    public void setConsumeItemValue(DataDetail consumeItemValue) {
        this.consumeItemValue = consumeItemValue;
    }

    public DataDetail getActvAccountNum() {
        return actvAccountNum;
    }

    public void setActvAccountNum(DataDetail actvAccountNum) {
        this.actvAccountNum = actvAccountNum;
    }

    public DataDetail getNewAccountNum() {
        return newAccountNum;
    }

    public void setNewAccountNum(DataDetail newAccountNum) {
        this.newAccountNum = newAccountNum;
    }

    public DataDetail getCardMoneyValue() {
        return cardMoneyValue;
    }

    public void setCardMoneyValue(DataDetail cardMoneyValue) {
        this.cardMoneyValue = cardMoneyValue;
    }

    public DataDetail getConsumeValue() {
        return consumeValue;
    }

    public void setConsumeValue(DataDetail consumeValue) {
        this.consumeValue = consumeValue;
    }

    public DataDetail getChargeAccountNum() {
        return chargeAccountNum;
    }

    public void setChargeAccountNum(DataDetail chargeAccountNum) {
        this.chargeAccountNum = chargeAccountNum;
    }

    public DataDetail getNewChargeAccntNum() {
        return newChargeAccntNum;
    }

    public void setNewChargeAccntNum(DataDetail newChargeAccntNum) {
        this.newChargeAccntNum = newChargeAccntNum;
    }
}
