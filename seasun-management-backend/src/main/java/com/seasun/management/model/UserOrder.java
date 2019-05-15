package com.seasun.management.model;

import java.util.Date;

public class UserOrder {
    private Long id;

    private Long userId;

    private Long productId;

    private Long totalFee;

    private String localTradeNo;

    private String prepayId;

    private String payType;

    private String clientFullInfo;

    private String status;

    private String transactionId;

    private Date createTime;

    private String openId;

    private Date callbackTime;

    private Date updateTime;

    private String updateComment;

    private String payTime;

    private String errorMessage;

    public interface Status {
        String initialized = "初始化";
        String payment = "已支付";
        String payFail = "支付失败";
        String refunding = "退款中";
        String refundFail = "退款失败";
        String refunded = "已退款";
    }

    public interface PayType {
        String weixin = "weixin";
        String alipay = "alipay";
    }

    public interface FeeType {
        String CNY = "CNY";
    }

    public interface ProductId {
        Long bonus = 1L;
    }

    public interface ProductName {
        String bonus = "西山居移动办公-节目打赏";
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

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

    public String getLocalTradeNo() {
        return localTradeNo;
    }

    public void setLocalTradeNo(String localTradeNo) {
        this.localTradeNo = localTradeNo == null ? null : localTradeNo.trim();
    }

    public String getPrepayId() {
        return prepayId;
    }

    public void setPrepayId(String prepayId) {
        this.prepayId = prepayId == null ? null : prepayId.trim();
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType == null ? null : payType.trim();
    }

    public String getClientFullInfo() {
        return clientFullInfo;
    }

    public void setClientFullInfo(String clientFullInfo) {
        this.clientFullInfo = clientFullInfo == null ? null : clientFullInfo.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId == null ? null : transactionId.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getCallbackTime() {
        return callbackTime;
    }

    public void setCallbackTime(Date callbackTime) {
        this.callbackTime = callbackTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateComment() {
        return updateComment;
    }

    public void setUpdateComment(String updateComment) {
        this.updateComment = updateComment == null ? null : updateComment.trim();
    }

    public String getPayTime() {
        return payTime;
    }

    public void setPayTime(String payTime) {
        this.payTime = payTime;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage == null ? null : errorMessage.trim();
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", userId=" + userId +
                ", productId=" + productId +
                ", totalFee=" + totalFee +
                ", localTradeNo='" + localTradeNo + '\'' +
                ", prepayId='" + prepayId + '\'' +
                ", payType='" + payType + '\'' +
                ", clientFullInfo='" + clientFullInfo + '\'' +
                ", status='" + status + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", createTime=" + createTime +
                ", callbackTime=" + callbackTime +
                ", updateTime=" + updateTime +
                ", updateComment='" + updateComment + '\'' +
                ", payTime=" + payTime +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}