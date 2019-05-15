package com.seasun.management.model;

import java.util.Date;

public class UserSalary {
    private Long id;

    private Long userId;

    private String salary;

    private String lastSalaryChangeDate;

    private String lastSalaryChangeAmount;

    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary == null ? null : salary.trim();
    }

    public String getLastSalaryChangeDate() {
        return lastSalaryChangeDate;
    }

    public void setLastSalaryChangeDate(String lastSalaryChangeDate) {
        this.lastSalaryChangeDate = lastSalaryChangeDate == null ? null : lastSalaryChangeDate.trim();
    }

    public String getLastSalaryChangeAmount() {
        return lastSalaryChangeAmount;
    }

    public void setLastSalaryChangeAmount(String lastSalaryChangeAmount) {
        this.lastSalaryChangeAmount = lastSalaryChangeAmount == null ? null : lastSalaryChangeAmount.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "UserSalary{" +
                "id=" + id +
                ", userId=" + userId +
                ", salary='" + salary + '\'' +
                ", lastSalaryChangeDate='" + lastSalaryChangeDate + '\'' +
                ", lastSalaryChangeAmount='" + lastSalaryChangeAmount + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}