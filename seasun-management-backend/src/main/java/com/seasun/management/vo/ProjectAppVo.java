package com.seasun.management.vo;


import java.math.BigDecimal;
import java.util.Date;

public class ProjectAppVo {

    private Integer year;

    private Integer month;

    private Long id;

    private String type;

    private String name;

    private String contactName;

    private Date establishTime;

    private Integer memberNumber;

    private Float platShareNumber;

    private Float income;

    private Float lastMonthIncome;

    private Float cost;

    private Float lastMonthCost;

    private Float totalCost;

    private Float expectIncome;

    private Integer maxMember;

    private String logo;

    private Long workGroupId;

    private Boolean hasSumShareData;

    private Boolean hasRealTimeFlag;

    private Boolean hasStage;

    private Boolean hasCPFlag;

    private String city;

    private BigDecimal budgetAmount;

    private String cPProjectIdsStr;

    private Float budgetPercent;

    private Boolean hasQualityFlag;

    public Float getBudgetPercent() {
        return budgetPercent;
    }

    public void setBudgetPercent(Float budgetPercent) {
        this.budgetPercent = budgetPercent;
    }

    public String getcPProjectIdsStr() {
        return cPProjectIdsStr;
    }

    public void setcPProjectIdsStr(String cPProjectIdsStr) {
        this.cPProjectIdsStr = cPProjectIdsStr;
    }

    public BigDecimal getBudgetAmount() {
        return budgetAmount;
    }

    public void setBudgetAmount(BigDecimal budgetAmount) {
        this.budgetAmount = budgetAmount;
    }

    public Boolean getHasCPFlag() {
        return hasCPFlag;
    }

    public void setHasCPFlag(Boolean hasCPFlag) {
        this.hasCPFlag = hasCPFlag;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public Date getEstablishTime() {
        return establishTime;
    }

    public void setEstablishTime(Date establishTime) {
        this.establishTime = establishTime;
    }

    public Integer getMemberNumber() {
        return memberNumber;
    }

    public void setMemberNumber(Integer memberNumber) {
        this.memberNumber = memberNumber;
    }

    public Float getPlatShareNumber() {
        return platShareNumber;
    }

    public void setPlatShareNumber(Float platShareNumber) {
        this.platShareNumber = platShareNumber;
    }

    public Float getIncome() {
        return income;
    }

    public void setIncome(Float income) {
        this.income = income;
    }

    public Float getLastMonthIncome() {
        return lastMonthIncome;
    }

    public void setLastMonthIncome(Float lastMonthIncome) {
        this.lastMonthIncome = lastMonthIncome;
    }

    public Float getCost() {
        return cost;
    }

    public void setCost(Float cost) {
        this.cost = cost;
    }

    public Float getLastMonthCost() {
        return lastMonthCost;
    }

    public void setLastMonthCost(Float lastMonthCost) {
        this.lastMonthCost = lastMonthCost;
    }

    public Float getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Float totalCost) {
        this.totalCost = totalCost;
    }

    public Float getExpectIncome() {
        return expectIncome;
    }

    public void setExpectIncome(Float expectIncome) {
        this.expectIncome = expectIncome;
    }

    public Integer getMaxMember() {
        return maxMember;
    }

    public void setMaxMember(Integer maxMember) {
        this.maxMember = maxMember;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public Long getWorkGroupId() {
        return workGroupId;
    }

    public void setWorkGroupId(Long workGroupId) {
        this.workGroupId = workGroupId;
    }

    public Boolean getHasSumShareData() {
        return hasSumShareData;
    }

    public void setHasSumShareData(Boolean hasSumShareData) {
        this.hasSumShareData = hasSumShareData;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Boolean getHasRealTimeFlag() {
        return hasRealTimeFlag;
    }

    public void setHasRealTimeFlag(Boolean hasRealTimeFlag) {
        this.hasRealTimeFlag = hasRealTimeFlag;
    }

    public Boolean getHasStage() {
        return hasStage;
    }

    public void setHasStage(Boolean hasStage) {
        this.hasStage = hasStage;
    }

    public Boolean getHasQualityFlag() {
        return hasQualityFlag;
    }

    public void setHasQualityFlag(Boolean hasQualityFlag) {
        this.hasQualityFlag = hasQualityFlag;
    }
}
