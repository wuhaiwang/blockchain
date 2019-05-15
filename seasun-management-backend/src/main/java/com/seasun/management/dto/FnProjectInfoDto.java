package com.seasun.management.dto;

import com.seasun.management.model.Project;

public class FnProjectInfoDto extends Project {
    private Float income;

    private Float cost;

    private Float profit;

    private Float lastMonthIncome;

    private Float lastMonthCost;

    private Float lastMonthProfit;

    private Float twoMonthAgoIncome;

    private Float twoMonthAgoCost;

    private Float twoMonthAgoProfit;

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

    public Float getLastMonthIncome() {
        return lastMonthIncome;
    }

    public void setLastMonthIncome(Float lastMonthIncome) {
        this.lastMonthIncome = lastMonthIncome;
    }

    public Float getLastMonthCost() {
        return lastMonthCost;
    }

    public void setLastMonthCost(Float lastMonthCost) {
        this.lastMonthCost = lastMonthCost;
    }

    public Float getLastMonthProfit() {
        return lastMonthProfit;
    }

    public void setLastMonthProfit(Float lastMonthProfit) {
        this.lastMonthProfit = lastMonthProfit;
    }

    public Float getTwoMonthAgoIncome() {
        return twoMonthAgoIncome;
    }

    public void setTwoMonthAgoIncome(Float twoMonthAgoIncome) {
        this.twoMonthAgoIncome = twoMonthAgoIncome;
    }

    public Float getTwoMonthAgoCost() {
        return twoMonthAgoCost;
    }

    public void setTwoMonthAgoCost(Float twoMonthAgoCost) {
        this.twoMonthAgoCost = twoMonthAgoCost;
    }

    public Float getTwoMonthAgoProfit() {
        return twoMonthAgoProfit;
    }

    public void setTwoMonthAgoProfit(Float twoMonthAgoProfit) {
        this.twoMonthAgoProfit = twoMonthAgoProfit;
    }
}
