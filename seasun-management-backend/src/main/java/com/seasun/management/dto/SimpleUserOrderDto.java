package com.seasun.management.dto;

public class SimpleUserOrderDto {

    private Long productId;

    private Long totalFee;

    private String payType;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(Long totalFee) {
        this.totalFee = totalFee;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    @Override
    public String toString() {
        return "{" +
                "productId=" + productId +
                ", totalFee=" + totalFee +
                ", payType='" + payType + '\'' +
                '}';
    }
}
