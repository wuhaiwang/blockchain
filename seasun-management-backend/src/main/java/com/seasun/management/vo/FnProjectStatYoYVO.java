package com.seasun.management.vo;

import java.util.List;

public class FnProjectStatYoYVO {

    List<List<FnProjectInfoAppVo.ProfitInfo>> profitInfo;
    List<List<FnProjectInfoAppVo.PerCapitaProfitInfo>> perCapitaProfitInfo;
    List<List<FnProjectInfoAppVo.IncomeInfo>> incomeInfo;
    List<List<FnProjectInfoAppVo.CostInfo>> costInfo;

    public List<List<FnProjectInfoAppVo.ProfitInfo>> getProfitInfo() {
        return profitInfo;
    }

    public void setProfitInfo(List<List<FnProjectInfoAppVo.ProfitInfo>> profitInfo) {
        this.profitInfo = profitInfo;
    }

    public List<List<FnProjectInfoAppVo.PerCapitaProfitInfo>> getPerCapitaProfitInfo() {
        return perCapitaProfitInfo;
    }

    public void setPerCapitaProfitInfo(List<List<FnProjectInfoAppVo.PerCapitaProfitInfo>> perCapitaProfitInfo) {
        this.perCapitaProfitInfo = perCapitaProfitInfo;
    }

    public List<List<FnProjectInfoAppVo.IncomeInfo>> getIncomeInfo() {
        return incomeInfo;
    }

    public void setIncomeInfo(List<List<FnProjectInfoAppVo.IncomeInfo>> incomeInfo) {
        this.incomeInfo = incomeInfo;
    }

    public List<List<FnProjectInfoAppVo.CostInfo>> getCostInfo() {
        return costInfo;
    }

    public void setCostInfo(List<List<FnProjectInfoAppVo.CostInfo>> costInfo) {
        this.costInfo = costInfo;
    }
}
