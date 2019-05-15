package com.seasun.management.dto;

import java.math.BigDecimal;
import java.util.Map;

public class UserFnShareDataDto {
    private Long employeeNo;
    private String userName;
    private Long userId;
    private Map<Long, BigDecimal> shareData;
    private BigDecimal shareTotal;
    private String remark;

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getEmployeeNo() {
        return employeeNo;
    }

    public void setEmployeeNo(Long employeeNo) {
        this.employeeNo = employeeNo;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Map<Long, BigDecimal> getShareData() {
        return shareData;
    }

    public void setShareData(Map<Long, BigDecimal> shareData) {
        this.shareData = shareData;
    }

    public BigDecimal getShareTotal() {
        return shareTotal;
    }

    public void setShareTotal(BigDecimal shareTotal) {
        this.shareTotal = shareTotal;
    }
}
