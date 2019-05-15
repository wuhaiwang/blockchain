package com.seasun.management.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.seasun.management.model.FnPlatShareConfig;

import java.math.BigDecimal;
import java.util.Date;

public class FnPlatShareConfigVo extends FnPlatShareConfig {

    private Long userId;

    private String city;

    private String projectName;

    private String projectUsedNames;

    private String createUserName;

    private BigDecimal weight;

    private BigDecimal totalWeight;

    private BigDecimal sumSharePro;

    private Long sumShareProId;

    public FnPlatShareConfigVo() {
        this.weight = new BigDecimal(0);
    }

    @JsonIgnore
    private Date createTime;

    @JsonIgnore
    private Date updateTime;

    public Long getSumShareProId() {
        return sumShareProId;
    }

    public void setSumShareProId(Long sumShareProId) {
        this.sumShareProId = sumShareProId;
    }

    public String getProjectUsedNames() {
        return projectUsedNames;
    }

    public void setProjectUsedNames(String projectUsedNames) {
        this.projectUsedNames = projectUsedNames;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public BigDecimal getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(BigDecimal totalWeight) {
        this.totalWeight = totalWeight;
    }

    public BigDecimal getSumSharePro() {
        return sumSharePro;
    }

    public void setSumSharePro(BigDecimal sumSharePro) {
        this.sumSharePro = sumSharePro;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
