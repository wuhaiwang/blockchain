package com.seasun.management.vo.cp;

import java.math.BigDecimal;

public class OutsourcerVo {

    private Integer id;

    private String  name;

    private  String makingType;

    private BigDecimal totalAmount;

    private Double percent;

    private Integer ordersRecord;

    private  Integer grade;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Double getPercent() {
        return percent;
    }

    public void setPercent(Double percent) {
        this.percent = percent;
    }

    public Integer getOrdersRecord() {
        return ordersRecord;
    }

    public void setOrdersRecord(Integer ordersRecord) {
        this.ordersRecord = ordersRecord;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }
}
