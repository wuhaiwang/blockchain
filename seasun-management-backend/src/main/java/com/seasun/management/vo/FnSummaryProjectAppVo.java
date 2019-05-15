package com.seasun.management.vo;

import java.util.List;

public class FnSummaryProjectAppVo {

    private Integer year;

    private Integer month;

    private Long id;

    private String name;

    private Long parentShareId;

    private Float profit;

    private Float totalProfit;

    private List<FnSummaryProjectAppVo> children;

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

    public Long getParentShareId() {
        return parentShareId;
    }

    public void setParentShareId(Long parentShareId) {
        this.parentShareId = parentShareId;
    }

    public Float getProfit() {
        return profit;
    }

    public void setProfit(Float profit) {
        this.profit = profit;
    }

    public Float getTotalProfit() {
        return totalProfit;
    }

    public void setTotalProfit(Float totalProfit) {
        this.totalProfit = totalProfit;
    }

    public List<FnSummaryProjectAppVo> getChildren() {
        return children;
    }

    public void setChildren(List<FnSummaryProjectAppVo> children) {
        this.children = children;
    }
}
