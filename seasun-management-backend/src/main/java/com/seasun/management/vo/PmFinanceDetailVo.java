package com.seasun.management.vo;

import com.seasun.management.model.PmFinanceDetail;

import java.math.BigDecimal;

public class PmFinanceDetailVo  extends PmFinanceDetail {
    private String projectName;//项目
    private String city;//地点
    private String type;//类别
    private String managerName;//负责人
    private String status;//阶段
    private BigDecimal monthProfit;//当前利润
    private BigDecimal totalProfit;//累计利润
    private BigDecimal income;//当月收入
    private BigDecimal preIncome;//上个月收入
    private BigDecimal cost;//费用
    private BigDecimal innerSettle;//内部结算
    private BigDecimal realProfit;//实际利润
    private Integer memberCount;//人数
    private BigDecimal averageProfit;//人均利润
    private String incomeMoM;//收入环比
    private String remark;//备注


    private Long monthProfitId;//当前利润ID
    private Long totalProfitId;//累计利润ID
    private Long incomeId;//当月收入ID
    private Long costId;//费用ID
    private Long innerSettleId;//内部结算ID
    private Long realProfitId;//实际利润ID
    private Long memberCountId;//人数ID
    private Long averageProfitId;//人均利润ID
    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getMonthProfit() {
        return monthProfit;
    }

    public void setMonthProfit(BigDecimal monthProfit) {
        this.monthProfit = monthProfit;
    }

    public BigDecimal getTotalProfit() {
        return totalProfit;
    }

    public void setTotalProfit(BigDecimal totalProfit) {
        this.totalProfit = totalProfit;
    }

    public BigDecimal getIncome() {
        return income;
    }

    public void setIncome(BigDecimal income) {
        this.income = income;
    }

    public BigDecimal getPreIncome() {
        return preIncome;
    }

    public void setPreIncome(BigDecimal preIncome) {
        this.preIncome = preIncome;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public BigDecimal getInnerSettle() {
        return innerSettle;
    }

    public void setInnerSettle(BigDecimal innerSettle) {
        this.innerSettle = innerSettle;
    }

    public BigDecimal getRealProfit() {
        return realProfit;
    }

    public void setRealProfit(BigDecimal realProfit) {
        this.realProfit = realProfit;
    }

    public Integer getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(Integer memberCount) {
        this.memberCount = memberCount;
    }

    public BigDecimal getAverageProfit() {
        return averageProfit;
    }

    public void setAverageProfit(BigDecimal averageProfit) {
        this.averageProfit = averageProfit;
    }

    public String getIncomeMoM() {
        return incomeMoM;
    }

    public void setIncomeMoM(String incomeMoM) {
        this.incomeMoM = incomeMoM;
    }

    @Override
    public String getRemark() {
        return remark;
    }

    @Override
    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getMonthProfitId() {
        return monthProfitId;
    }

    public void setMonthProfitId(Long monthProfitId) {
        this.monthProfitId = monthProfitId;
    }

    public Long getTotalProfitId() {
        return totalProfitId;
    }

    public void setTotalProfitId(Long totalProfitId) {
        this.totalProfitId = totalProfitId;
    }

    public Long getIncomeId() {
        return incomeId;
    }

    public void setIncomeId(Long incomeId) {
        this.incomeId = incomeId;
    }

    public Long getCostId() {
        return costId;
    }

    public void setCostId(Long costId) {
        this.costId = costId;
    }

    public Long getInnerSettleId() {
        return innerSettleId;
    }

    public void setInnerSettleId(Long innerSettleId) {
        this.innerSettleId = innerSettleId;
    }

    public Long getRealProfitId() {
        return realProfitId;
    }

    public void setRealProfitId(Long realProfitId) {
        this.realProfitId = realProfitId;
    }

    public Long getMemberCountId() {
        return memberCountId;
    }

    public void setMemberCountId(Long memberCountId) {
        this.memberCountId = memberCountId;
    }

    public Long getAverageProfitId() {
        return averageProfitId;
    }

    public void setAverageProfitId(Long averageProfitId) {
        this.averageProfitId = averageProfitId;
    }
}