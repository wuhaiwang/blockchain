package com.seasun.management.vo;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.seasun.management.model.PmAttachment;

import java.io.Serializable;
import java.util.List;

public class FnProjectInfoAppVo {
    private JSONObject requestParam;

    private BaseInfo baseInfo;

    private List<ProfitInfo> profitInfo;

    private List<PerCapitaProfitInfo> perCapitaProfitInfo;

    private List<IncomeInfo> incomeInfo;

    private List<CostInfo> costInfo;

    private List<CostDetail> directCostDetail;

    private List<CostDetail> indirectCostDetail;

    private List<FnProjectInfo> incomeProjectDetail;

    private List<FnProjectInfo> costProjectDetial;

    private List<FnProjectInfo> profitProjectDetail;

    private ManagerAnalysis managerAnalysis;

    private Boolean analysisPublishedFlag;

    public Boolean getAnalysisPublishedFlag() {
        return analysisPublishedFlag;
    }

    public void setAnalysisPublishedFlag(Boolean analysisPublishedFlag) {
        this.analysisPublishedFlag = analysisPublishedFlag;
    }

    public static class ManagerAnalysis{

        private String income;

        private String cost;

        private String profit;

        private List<PmAttachment> attachments;

        public String getIncome() {
            return income;
        }

        public void setIncome(String income) {
            this.income = income;
        }

        public String getCost() {
            return cost;
        }

        public void setCost(String cost) {
            this.cost = cost;
        }

        public String getProfit() {
            return profit;
        }

        public void setProfit(String profit) {
            this.profit = profit;
        }

        public List<PmAttachment> getAttachments() {
            return attachments;
        }

        public void setAttachments(List<PmAttachment> attachments) {
            this.attachments = attachments;
        }
    }

    public ManagerAnalysis getManagerAnalysis() {
        return managerAnalysis;
    }

    public void setManagerAnalysis(ManagerAnalysis managerAnalysis) {
        this.managerAnalysis = managerAnalysis;
    }

    public JSONObject getRequestParam() {
        return requestParam;
    }

    public void setRequestParam(JSONObject requestParam) {
        this.requestParam = requestParam;
    }

    public BaseInfo getBaseInfo() {
        return baseInfo;
    }

    public void setBaseInfo(BaseInfo baseInfo) {
        this.baseInfo = baseInfo;
    }

    public List<ProfitInfo> getProfitInfo() {
        return profitInfo;
    }

    public void setProfitInfo(List<ProfitInfo> profitInfo) {
        this.profitInfo = profitInfo;
    }

    public List<PerCapitaProfitInfo> getPerCapitaProfitInfo() {
        return perCapitaProfitInfo;
    }

    public void setPerCapitaProfitInfo(List<PerCapitaProfitInfo> perCapitaProfitInfo) {
        this.perCapitaProfitInfo = perCapitaProfitInfo;
    }

    public List<IncomeInfo> getIncomeInfo() {
        return incomeInfo;
    }

    public void setIncomeInfo(List<IncomeInfo> incomeInfo) {
        this.incomeInfo = incomeInfo;
    }

    public List<CostInfo> getCostInfo() {
        return costInfo;
    }

    public void setCostInfo(List<CostInfo> costInfo) {
        this.costInfo = costInfo;
    }

    public List<CostDetail> getDirectCostDetail() {
        return directCostDetail;
    }

    public void setDirectCostDetail(List<CostDetail> directCostDetail) {
        this.directCostDetail = directCostDetail;
    }

    public List<CostDetail> getIndirectCostDetail() {
        return indirectCostDetail;
    }

    public void setIndirectCostDetail(List<CostDetail> indirectCostDetail) {
        this.indirectCostDetail = indirectCostDetail;
    }

    public List<FnProjectInfo> getIncomeProjectDetail() {
        return incomeProjectDetail;
    }

    public void setIncomeProjectDetail(List<FnProjectInfo> incomeProjectDetail) {
        this.incomeProjectDetail = incomeProjectDetail;
    }

    public List<FnProjectInfo> getCostProjectDetial() {
        return costProjectDetial;
    }

    public void setCostProjectDetial(List<FnProjectInfo> costProjectDetial) {
        this.costProjectDetial = costProjectDetial;
    }

    public List<FnProjectInfo> getProfitProjectDetail() {
        return profitProjectDetail;
    }

    public void setProfitProjectDetail(List<FnProjectInfo> profitProjectDetail) {
        this.profitProjectDetail = profitProjectDetail;
    }

    public static class BaseInfo {

        private Integer startYear;

        private Integer startMonth;

        private Integer endYear;

        private Integer endMonth;

        private Float income;

        private Float cost;

        private Float profit;

        private Float profitability;

        private Float perCapitaProfit;

        private Float annualIncomeBudget;

        private Float totalCost;

        private Float totalBudget;

        private Float incomeMOM;

        private Float costMOM;

        private Float profitMOM;

        public Float getIncomeMOM() {
            return incomeMOM;
        }

        public void setIncomeMOM(Float incomeMOM) {
            this.incomeMOM = incomeMOM;
        }

        public Float getCostMOM() {
            return costMOM;
        }

        public void setCostMOM(Float costMOM) {
            this.costMOM = costMOM;
        }

        public Float getProfitMOM() {
            return profitMOM;
        }

        public void setProfitMOM(Float profitMOM) {
            this.profitMOM = profitMOM;
        }

        public Integer getStartYear() {
            return startYear;
        }

        public void setStartYear(Integer startYear) {
            this.startYear = startYear;
        }

        public Integer getStartMonth() {
            return startMonth;
        }

        public void setStartMonth(Integer startMonth) {
            this.startMonth = startMonth;
        }

        public Integer getEndYear() {
            return endYear;
        }

        public void setEndYear(Integer endYear) {
            this.endYear = endYear;
        }

        public Integer getEndMonth() {
            return endMonth;
        }

        public void setEndMonth(Integer endMonth) {
            this.endMonth = endMonth;
        }

        public Float getIncome() {
            return income;
        }

        public void setIncome(Float income) {
            this.income = income;
        }

        public Float getCost() {
            return cost;
        }

        public void setCost(Float cost) {
            this.cost = cost;
        }

        public Float getProfit() {
            return profit;
        }

        public void setProfit(Float profit) {
            this.profit = profit;
        }

        public Float getProfitability() {
            return profitability;
        }

        public void setProfitability(Float profitability) {
            this.profitability = profitability;
        }

        public Float getPerCapitaProfit() {
            return perCapitaProfit;
        }

        public void setPerCapitaProfit(Float perCapitaProfit) {
            this.perCapitaProfit = perCapitaProfit;
        }

        public Float getAnnualIncomeBudget() {
            return annualIncomeBudget;
        }

        public void setAnnualIncomeBudget(Float annualIncomeBudget) {
            this.annualIncomeBudget = annualIncomeBudget;
        }

        public Float getTotalCost() {
            return totalCost;
        }

        public void setTotalCost(Float totalCost) {
            this.totalCost = totalCost;
        }

        public Float getTotalBudget() {
            return totalBudget;
        }

        public void setTotalBudget(Float totalBudget) {
            this.totalBudget = totalBudget;
        }
    }

    public static class ProfitInfo {

        private Integer year;

        private Integer month;

        private Float profit;

        private Float profitability;

        public Integer getYear() {
            return year;
        }

        public void setYear(Integer year) {
            this.year = year;
        }

        public Integer getMonth() {
            return month;
        }

        public void setMonth(Integer month) {
            this.month = month;
        }

        public Float getProfit() {
            return profit;
        }

        public void setProfit(Float profit) {
            this.profit = profit;
        }

        public Float getProfitability() {
            return profitability;
        }

        public void setProfitability(Float profitability) {
            this.profitability = profitability;
        }
    }

    public static class PerCapitaProfitInfo {

        private Integer year;

        private Integer month;

        private Float perCapitaProfit;

        public Integer getYear() {
            return year;
        }

        public void setYear(Integer year) {
            this.year = year;
        }

        public Integer getMonth() {
            return month;
        }

        public void setMonth(Integer month) {
            this.month = month;
        }

        public Float getPerCapitaProfit() {
            return perCapitaProfit;
        }

        public void setPerCapitaProfit(Float perCapitaProfit) {
            this.perCapitaProfit = perCapitaProfit;
        }
    }

    public static class IncomeInfo {

        private Integer year;

        private Integer month;

        private Float income;

        private Float turnoverTax;

        private Float afterTaxIncome;

        private Float internalSettlementCost;

        private Float netIncome;
        
        private Float taxReturn;

        public Integer getYear() {
            return year;
        }

        public void setYear(Integer year) {
            this.year = year;
        }

        public Integer getMonth() {
            return month;
        }

        public void setMonth(Integer month) {
            this.month = month;
        }

        public Float getIncome() {
            return income;
        }

        public void setIncome(Float income) {
            this.income = income;
        }

        public Float getTurnoverTax() {
            return turnoverTax;
        }

        public void setTurnoverTax(Float turnoverTax) {
            this.turnoverTax = turnoverTax;
        }

        public Float getAfterTaxIncome() {
            return afterTaxIncome;
        }

        public void setAfterTaxIncome(Float afterTaxIncome) {
            this.afterTaxIncome = afterTaxIncome;
        }

        public Float getInternalSettlementCost() {
            return internalSettlementCost;
        }

        public void setInternalSettlementCost(Float internalSettlementCost) {
            this.internalSettlementCost = internalSettlementCost;
        }

        public Float getNetIncome() {
            return netIncome;
        }

        public void setNetIncome(Float netIncome) {
            this.netIncome = netIncome;
        }
        
        public Float getTaxReturn() {
			return taxReturn;
		}
        
        public void setTaxReturn(Float taxReturn) {
			this.taxReturn = taxReturn;
		}
    }

    public static class CostInfo {

        private Integer year;

        private Integer month;

        private Float directCost;

        private Float indirectCost;

        private List<CostDetail> directCostDetail;

        private List<CostDetail> indirectCostDetail;

        public Integer getYear() {
            return year;
        }

        public void setYear(Integer year) {
            this.year = year;
        }

        public Integer getMonth() {
            return month;
        }

        public void setMonth(Integer month) {
            this.month = month;
        }

        public Float getDirectCost() {
            return directCost;
        }

        public void setDirectCost(Float directCost) {
            this.directCost = directCost;
        }

        public Float getIndirectCost() {
            return indirectCost;
        }

        public void setIndirectCost(Float indirectCost) {
            this.indirectCost = indirectCost;
        }

        public List<CostDetail> getDirectCostDetail() {
            return directCostDetail;
        }

        public void setDirectCostDetail(List<CostDetail> directCostDetail) {
            this.directCostDetail = directCostDetail;
        }

        public List<CostDetail> getIndirectCostDetail() {
            return indirectCostDetail;
        }

        public void setIndirectCostDetail(List<CostDetail> indirectCostDetail) {
            this.indirectCostDetail = indirectCostDetail;
        }
    }

    public static class CostDetail implements Serializable{

        private String name;

        private Float twoMonthAgoCost;

        private Float lastMonthCost;

        private Float currentMonthCost;

        private Boolean detailFlag;
        @JsonProperty("id")
        private Long projectStatDataId;

        private List<CostDetail> children;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Float getTwoMonthAgoCost() {
            return twoMonthAgoCost;
        }

        public void setTwoMonthAgoCost(Float twoMonthAgoCost) {
            this.twoMonthAgoCost = twoMonthAgoCost;
        }

        public Float getLastMonthCost() {
            return lastMonthCost;
        }

        public void setLastMonthCost(Float lastMonthCost) {
            this.lastMonthCost = lastMonthCost;
        }

        public Float getCurrentMonthCost() {
            return currentMonthCost;
        }

        public void setCurrentMonthCost(Float currentMonthCost) {
            this.currentMonthCost = currentMonthCost;
        }

        public Boolean getDetailFlag() {
            return detailFlag;
        }

        public void setDetailFlag(Boolean detailFlag) {
            this.detailFlag = detailFlag;
        }

        public Long getProjectStatDataId() {
            return projectStatDataId;
        }

        public void setProjectStatDataId(Long projectStatDataId) {
            this.projectStatDataId = projectStatDataId;
        }

        public List<CostDetail> getChildren() {
            return children;
        }

        public void setChildren(List<CostDetail> children) {
            this.children = children;
        }
    }

    public static class FnProjectInfo {
        private String name;

        private Float currentMonthValue;

        private Float lastMonthValue;

        private Float twoMonthAgoValue;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Float getCurrentMonthValue() {
            return currentMonthValue;
        }

        public void setCurrentMonthValue(Float currentMonthValue) {
            this.currentMonthValue = currentMonthValue;
        }

        public Float getLastMonthValue() {
            return lastMonthValue;
        }

        public void setLastMonthValue(Float lastMonthValue) {
            this.lastMonthValue = lastMonthValue;
        }

        public Float getTwoMonthAgoValue() {
            return twoMonthAgoValue;
        }

        public void setTwoMonthAgoValue(Float twoMonthAgoValue) {
            this.twoMonthAgoValue = twoMonthAgoValue;
        }
    }
}
