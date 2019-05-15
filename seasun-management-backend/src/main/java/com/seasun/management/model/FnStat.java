package com.seasun.management.model;

public class FnStat {

    public interface Type {
        String project = "project";
        String fixed = "fixed";
    }

    public interface id {
        // 费用合计(直接+分摊)
        Long cost = 3000L;
        // 收入
        Long income = 2000L;
        // 税前利润
        Long profit = 3100L;
        // 税前利润率
        Long profitability = 3200L;
        // 直接费用
        Long directCost = 2800L;
        // 分摊费用
        Long shareCost = 2900L;
        // 人均利润
        Long perCapitaProfit = 3600L;
        // 退税
        Long taxReturn = 2101L;
        // 内部结算成本
        Long internalSettlementCost = 2300L;
        // 流转税金
        Long turnoverTax = 2100L;
        // 税后收入
        Long afterTaxIncome = 2200L;
        // 外包费用
        Long outsrouceCost = 3700L;

    }

    private Long id;

    private String name;

    private Long parentId;

    private String type;

    private Long projectId;

    //  the following are user defined...


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
        this.name = name == null ? null : name.trim();
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
}