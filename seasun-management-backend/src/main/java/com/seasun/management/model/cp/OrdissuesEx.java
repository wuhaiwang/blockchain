package com.seasun.management.model.cp;

import java.math.BigDecimal;
import java.util.Date;

public class OrdissuesEx {
    private Integer id;

    private Integer issueId;

    private Integer cPId;

    private String gameProject;

    private String resType;

    private Integer amount;

    private BigDecimal attitude;

    private BigDecimal feedback;

    private BigDecimal onTime;

    private BigDecimal quality;

    private Integer orderStatus;

    private String orderNo;

    private String description;

    private Date evaluationTime;

    private String reimbursementNo;

    private Integer orderID;

    private Integer sign;

    private Integer autoStatus;

    private String cPOrderNo;

    private Byte endEvaluation;

    private String currencies;

    private Integer payTimes;

    private Integer verifyTimes;

    private BigDecimal prePayMoney;

    private BigDecimal midPayMoney;

    private BigDecimal lastPayMoney;

    private BigDecimal realPrePayMoney;

    private BigDecimal realMidPayMoney;

    private BigDecimal realLastPayMoney;

    private BigDecimal realPayMoney;

    private String remark;

    private Byte modifyRealPayMoney;

    private Integer verify;

    private String appraise;

    public interface Currencies {
        String RMB = "人民币";
        String USD = "美元";
        String EURO = "欧元";
        String YEN = "日元";
    }


    public interface Verify {
        Integer unVerify = 0;
        Integer verifying = 1;
        Integer verified = 2;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIssueId() {
        return issueId;
    }

    public void setIssueId(Integer issueId) {
        this.issueId = issueId;
    }

    public Integer getcPId() {
        return cPId;
    }

    public void setcPId(Integer cPId) {
        this.cPId = cPId;
    }

    public String getGameProject() {
        return gameProject;
    }

    public void setGameProject(String gameProject) {
        this.gameProject = gameProject == null ? null : gameProject.trim();
    }

    public String getResType() {
        return resType;
    }

    public void setResType(String resType) {
        this.resType = resType == null ? null : resType.trim();
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public BigDecimal getAttitude() {
        return attitude;
    }

    public void setAttitude(BigDecimal attitude) {
        this.attitude = attitude;
    }

    public BigDecimal getFeedback() {
        return feedback;
    }

    public void setFeedback(BigDecimal feedback) {
        this.feedback = feedback;
    }

    public BigDecimal getOnTime() {
        return onTime;
    }

    public void setOnTime(BigDecimal onTime) {
        this.onTime = onTime;
    }

    public BigDecimal getQuality() {
        return quality;
    }

    public void setQuality(BigDecimal quality) {
        this.quality = quality;
    }

    public Integer getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(Integer orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo == null ? null : orderNo.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public Date getEvaluationTime() {
        return evaluationTime;
    }

    public void setEvaluationTime(Date evaluationTime) {
        this.evaluationTime = evaluationTime;
    }

    public String getReimbursementNo() {
        return reimbursementNo;
    }

    public void setReimbursementNo(String reimbursementNo) {
        this.reimbursementNo = reimbursementNo == null ? null : reimbursementNo.trim();
    }

    public Integer getOrderID() {
        return orderID;
    }

    public void setOrderID(Integer orderID) {
        this.orderID = orderID;
    }

    public Integer getSign() {
        return sign;
    }

    public void setSign(Integer sign) {
        this.sign = sign;
    }

    public Integer getAutoStatus() {
        return autoStatus;
    }

    public void setAutoStatus(Integer autoStatus) {
        this.autoStatus = autoStatus;
    }

    public String getcPOrderNo() {
        return cPOrderNo;
    }

    public void setcPOrderNo(String cPOrderNo) {
        this.cPOrderNo = cPOrderNo == null ? null : cPOrderNo.trim();
    }

    public Byte getEndEvaluation() {
        return endEvaluation;
    }

    public void setEndEvaluation(Byte endEvaluation) {
        this.endEvaluation = endEvaluation;
    }

    public String getCurrencies() {
        return currencies;
    }

    public void setCurrencies(String currencies) {
        this.currencies = currencies == null ? null : currencies.trim();
    }

    public Integer getPayTimes() {
        return payTimes;
    }

    public void setPayTimes(Integer payTimes) {
        this.payTimes = payTimes;
    }

    public Integer getVerifyTimes() {
        return verifyTimes;
    }

    public void setVerifyTimes(Integer verifyTimes) {
        this.verifyTimes = verifyTimes;
    }

    public BigDecimal getPrePayMoney() {
        return prePayMoney;
    }

    public void setPrePayMoney(BigDecimal prePayMoney) {
        this.prePayMoney = prePayMoney;
    }

    public BigDecimal getMidPayMoney() {
        return midPayMoney;
    }

    public void setMidPayMoney(BigDecimal midPayMoney) {
        this.midPayMoney = midPayMoney;
    }

    public BigDecimal getLastPayMoney() {
        return lastPayMoney;
    }

    public void setLastPayMoney(BigDecimal lastPayMoney) {
        this.lastPayMoney = lastPayMoney;
    }

    public BigDecimal getRealPrePayMoney() {
        return realPrePayMoney;
    }

    public void setRealPrePayMoney(BigDecimal realPrePayMoney) {
        this.realPrePayMoney = realPrePayMoney;
    }

    public BigDecimal getRealMidPayMoney() {
        return realMidPayMoney;
    }

    public void setRealMidPayMoney(BigDecimal realMidPayMoney) {
        this.realMidPayMoney = realMidPayMoney;
    }

    public BigDecimal getRealLastPayMoney() {
        return realLastPayMoney;
    }

    public void setRealLastPayMoney(BigDecimal realLastPayMoney) {
        this.realLastPayMoney = realLastPayMoney;
    }

    public BigDecimal getRealPayMoney() {
        return realPayMoney;
    }

    public void setRealPayMoney(BigDecimal realPayMoney) {
        this.realPayMoney = realPayMoney;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Byte getModifyRealPayMoney() {
        return modifyRealPayMoney;
    }

    public void setModifyRealPayMoney(Byte modifyRealPayMoney) {
        this.modifyRealPayMoney = modifyRealPayMoney;
    }

    public Integer getVerify() {
        return verify;
    }

    public void setVerify(Integer verify) {
        this.verify = verify;
    }

    public String getAppraise() {
        return appraise;
    }

    public void setAppraise(String appraise) {
        this.appraise = appraise == null ? null : appraise.trim();
    }
}