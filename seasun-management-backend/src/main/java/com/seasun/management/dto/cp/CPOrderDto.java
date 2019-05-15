package com.seasun.management.dto.cp;

import java.math.BigDecimal;

public class CPOrderDto {

    private String orderNo;

    private String status;

    private String name;

    private String makingType;

    private String progress;

    private BigDecimal amount;

    private String createDateStr;

    private String currencies;

    private String sendDateStr;

    private BigDecimal realPayMoney;

    private Integer verify;

    private Integer modifyRealPayMoney;

    public interface Status {
        String doing = "进行中";
        String done = "已验收";
    }

    public String getCurrencies() {
        return currencies;
    }

    public void setCurrencies(String currencies) {
        this.currencies = currencies;
    }

    public Integer getModifyRealPayMoney() {
        return modifyRealPayMoney;
    }

    public void setModifyRealPayMoney(Integer modifyRealPayMoney) {
        this.modifyRealPayMoney = modifyRealPayMoney;
    }

    public Integer getVerify() {
        return verify;
    }

    public void setVerify(Integer verify) {
        this.verify = verify;
    }

    public BigDecimal getRealPayMoney() {
        return realPayMoney;
    }

    public void setRealPayMoney(BigDecimal realPayMoney) {
        this.realPayMoney = realPayMoney;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMakingType() {
        return makingType;
    }

    public void setMakingType(String makingType) {
        this.makingType = makingType;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCreateDateStr() {
        return createDateStr;
    }

    public void setCreateDateStr(String createDateStr) {
        this.createDateStr = createDateStr;
    }

    public String getSendDateStr() {
        return sendDateStr;
    }

    public void setSendDateStr(String sendDateStr) {
        this.sendDateStr = sendDateStr;
    }
}
