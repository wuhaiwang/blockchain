package com.seasun.management.model.dataCenter;

import java.math.BigDecimal;
import java.util.Date;

public class VDWChargeLog {
    private String gameCode;

    private BigDecimal iid;

    private Date fillDatetime;

    private BigDecimal cardType;

    private BigDecimal cardAmount;

    private BigDecimal fillType;

    private String gameuid;

    private String gateway;

    public String getGameCode() {
        return gameCode;
    }

    public void setGameCode(String gameCode) {
        this.gameCode = gameCode == null ? null : gameCode.trim();
    }

    public BigDecimal getIid() {
        return iid;
    }

    public void setIid(BigDecimal iid) {
        this.iid = iid;
    }

    public Date getFillDatetime() {
        return fillDatetime;
    }

    public void setFillDatetime(Date fillDatetime) {
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

    public String getGameuid() {
        return gameuid;
    }

    public void setGameuid(String gameuid) {
        this.gameuid = gameuid == null ? null : gameuid.trim();
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway == null ? null : gateway.trim();
    }

    public interface FillType {
        int dayCardType = 2;
        int monthCardType = 1;
        int gameCoinsType = 6;

    }
}