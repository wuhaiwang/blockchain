package com.seasun.management.model.cp;

import java.math.BigDecimal;

public class CPDetails {
    private Integer id;

    private Integer cPId;

    private Integer type;

    private String txt;

    private BigDecimal digit;

    private String remark;

    private Boolean blValue;

    private BigDecimal typeCircle;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getcPId() {
        return cPId;
    }

    public void setcPId(Integer cPId) {
        this.cPId = cPId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt == null ? null : txt.trim();
    }

    public BigDecimal getDigit() {
        return digit;
    }

    public void setDigit(BigDecimal digit) {
        this.digit = digit;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Boolean getBlValue() {
        return blValue;
    }

    public void setBlValue(Boolean blValue) {
        this.blValue = blValue;
    }

    public BigDecimal getTypeCircle() {
        return typeCircle;
    }

    public void setTypeCircle(BigDecimal typeCircle) {
        this.typeCircle = typeCircle;
    }
}