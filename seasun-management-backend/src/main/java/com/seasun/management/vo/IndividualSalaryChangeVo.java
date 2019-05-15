package com.seasun.management.vo;

import java.util.Date;

public class IndividualSalaryChangeVo extends OrdinateSalaryChangeAppVo {

    private UserInfo userInfo;
    private SalaryInfo salaryInfo;

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public SalaryInfo getSalaryInfo() {
        return salaryInfo;
    }

    public void setSalaryInfo(SalaryInfo salaryInfo) {
        this.salaryInfo = salaryInfo;
    }

    public static class UserInfo {
        private Long userId;
        private Long employeeNo;
        private Integer workAge;
        private Integer workAgeInKs;
        private String post;
        private String workGroupName;

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public Long getEmployeeNo() {
            return employeeNo;
        }

        public void setEmployeeNo(Long employeeNo) {
            this.employeeNo = employeeNo;
        }

        public Integer getWorkAge() {
            return workAge;
        }

        public void setWorkAge(Integer workAge) {
            this.workAge = workAge;
        }

        public Integer getWorkAgeInKs() {
            return workAgeInKs;
        }

        public void setWorkAgeInKs(Integer workAgeInKs) {
            this.workAgeInKs = workAgeInKs;
        }

        public String getPost() {
            return post;
        }

        public void setPost(String post) {
            this.post = post;
        }

        public String getWorkGroupName() {
            return workGroupName;
        }

        public void setWorkGroupName(String workGroupName) {
            this.workGroupName = workGroupName;
        }
    }

    public static class SalaryInfo {
        private Integer salary;
        private Integer lastSalaryChangeAmount;
        private String lastGrade;
        private Date lastSalaryChangeDate;
        private Date lastGradeChangeDate;

        public Integer getSalary() {
            return salary;
        }

        public void setSalary(Integer salary) {
            this.salary = salary;
        }

        public Integer getLastSalaryChangeAmount() {
            return lastSalaryChangeAmount;
        }

        public void setLastSalaryChangeAmount(Integer lastSalaryChangeAmount) {
            this.lastSalaryChangeAmount = lastSalaryChangeAmount;
        }

        public String getLastGrade() {
            return lastGrade;
        }

        public void setLastGrade(String lastGrade) {
            this.lastGrade = lastGrade;
        }

        public Date getLastSalaryChangeDate() {
            return lastSalaryChangeDate;
        }

        public void setLastSalaryChangeDate(Date lastSalaryChangeDate) {
            this.lastSalaryChangeDate = lastSalaryChangeDate;
        }

        public Date getLastGradeChangeDate() {
            return lastGradeChangeDate;
        }

        public void setLastGradeChangeDate(Date lastGradeChangeDate) {
            this.lastGradeChangeDate = lastGradeChangeDate;
        }
    }
}
