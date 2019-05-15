package com.seasun.management.vo.cp;

import java.math.BigDecimal;

public class CpProjectRelationVo {
    private Long itProjectId;

    private String itProjectName;

    private Long cpProjectId;

    private String cpProjectName;

    private BigDecimal amount;

    public Long getItProjectId() {
        return itProjectId;
    }

    public void setItProjectId(Long itProjectId) {
        this.itProjectId = itProjectId;
    }

    public Long getCpProjectId() {
        return cpProjectId;
    }

    public void setCpProjectId(Long cpProjectId) {
        this.cpProjectId = cpProjectId;
    }

    public String getItProjectName() {
        return itProjectName;
    }

    public void setItProjectName(String itProjectName) {
        this.itProjectName = itProjectName;
    }

    public String getCpProjectName() {
        return cpProjectName;
    }

    public void setCpProjectName(String cpProjectName) {
        this.cpProjectName = cpProjectName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}