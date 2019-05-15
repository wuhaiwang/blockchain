package com.seasun.management.vo;


import java.util.Date;

public class UserPerformanceDetailVo {

    private UserInfo userInfo;
    private PerformanceInfo performanceInfo;

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public PerformanceInfo getPerformanceInfo() {
        return performanceInfo;
    }

    public void setPerformanceInfo(PerformanceInfo performanceInfo) {
        this.performanceInfo = performanceInfo;
    }

    public static class UserInfo {
        private String name;
        private Long employeeNo;
        private Date joinTime;
        private Integer workAge;
        private String post;
        private String workGroup;
        private String status;
        private String manager;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Long getEmployeeNo() {
            return employeeNo;
        }

        public void setEmployeeNo(Long employeeNo) {
            this.employeeNo = employeeNo;
        }

        public Date getJoinTime() {
            return joinTime;
        }

        public void setJoinTime(Date joinTime) {
            this.joinTime = joinTime;
        }

        public Integer getWorkAge() {
            return workAge;
        }

        public void setWorkAge(Integer workAge) {
            this.workAge = workAge;
        }

        public String getPost() {
            return post;
        }

        public void setPost(String post) {
            this.post = post;
        }

        public String getWorkGroup() {
            return workGroup;
        }

        public void setWorkGroup(String workGroup) {
            this.workGroup = workGroup;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getManager() {
            return manager;
        }

        public void setManager(String manager) {
            this.manager = manager;
        }
    }

    public static class PerformanceInfo {
        private String performance;
        private String monthGoal;
        private String managerComment;
        private String selfComment;
        private String lastPerformance;

        public String getPerformance() {
            return performance;
        }

        public void setPerformance(String performance) {
            this.performance = performance;
        }

        public String getMonthGoal() {
            return monthGoal;
        }

        public void setMonthGoal(String monthGoal) {
            this.monthGoal = monthGoal;
        }

        public String getManagerComment() {
            return managerComment;
        }

        public void setManagerComment(String managerComment) {
            this.managerComment = managerComment;
        }

        public String getSelfComment() {
            return selfComment;
        }

        public void setSelfComment(String selfComment) {
            this.selfComment = selfComment;
        }

        public String getLastPerformance() {
            return lastPerformance;
        }

        public void setLastPerformance(String lastPerformance) {
            this.lastPerformance = lastPerformance;
        }
    }

}
