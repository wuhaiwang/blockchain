package com.seasun.management.dto.cp;

import com.seasun.management.model.CfgExchangeRate;

import java.math.BigDecimal;

public class ExchangeRateDto {
    public static final String CURRENCY="currency";
    public static final String CURRENCY_CODE="currencyCode";
    public static final String RATE="rate";

    private Long id;
    private String currency;
    private String currencyCode;
    private BigDecimal rate;

    public ExchangeRateDto(String currencyCode,String currency,BigDecimal rate) {
        this.currencyCode=currencyCode;
        this.currency=currency;
        this.rate=rate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public CfgExchangeRate toStandardRateObj(){
        CfgExchangeRate rate = new CfgExchangeRate();
        rate.setsCode(this.getCurrencyCode());
        rate.setsName(this.getCurrency());
        rate.setRate(this.getRate());
        rate.setdCode("CNY");
        rate.setdName("人民币");
        return rate;
    }

}
