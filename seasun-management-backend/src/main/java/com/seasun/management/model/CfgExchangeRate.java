package com.seasun.management.model;

import com.seasun.management.dto.cp.ExchangeRateDto;

import java.math.BigDecimal;

public class CfgExchangeRate {
    private Long id;

    private String sCode;

    private String sName;

    private BigDecimal rate;

    private String dCode;

    private String dName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getsCode() {
        return sCode;
    }

    public void setsCode(String sCode) {
        this.sCode = sCode == null ? null : sCode.trim();
    }

    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName == null ? null : sName.trim();
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public String getdCode() {
        return dCode;
    }

    public void setdCode(String dCode) {
        this.dCode = dCode == null ? null : dCode.trim();
    }

    public String getdName() {
        return dName;
    }

    public void setdName(String dName) {
        this.dName = dName == null ? null : dName.trim();
    }

    public ExchangeRateDto toDto() {
        ExchangeRateDto dto = new ExchangeRateDto(this.sCode,this.sName,this.rate);
        dto.setId(this.id);
        return dto;
    }


}