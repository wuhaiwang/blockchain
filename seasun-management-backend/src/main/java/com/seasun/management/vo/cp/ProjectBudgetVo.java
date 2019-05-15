package com.seasun.management.vo.cp;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class ProjectBudgetVo {
    private Long id;
    private Long cpBudgetId;//预算金额Id
    private Long cpProjectId;

    private Integer budgetYear;

    private BigDecimal amount;

    private Date createTime;

    private Date updateTime;

    private List<CpBudgetAppendInfoVo> items;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCpProjectId() {
        return cpProjectId;
    }

    public void setCpProjectId(Long cpProjectId) {
        this.cpProjectId = cpProjectId;
    }

    public Integer getBudgetYear() {
        return budgetYear;
    }

    public void setBudgetYear(Integer budgetYear) {
        this.budgetYear = budgetYear;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public List<CpBudgetAppendInfoVo> getItems() {
        return items;
    }

    public void setItems(List<CpBudgetAppendInfoVo> items) {
        this.items = items;
    }

    public Long getCpBudgetId() {
        return cpBudgetId;
    }

    public void setCpBudgetId(Long cpBudgetId) {
        this.cpBudgetId = cpBudgetId;
    }
}