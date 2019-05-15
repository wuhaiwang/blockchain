package com.seasun.management.vo;

public class HomeInfoAppVo {

    private HomeProjectInfo projectInfo;

    private HomePlatformInfo platformInfo;

    private HomeFinanceInfo financeInfo;

    private HomeHrInfo hrInfo;

    private HomeOutsourceInfo outsourceInfo;

    private HomePerformanceInfo performanceInfo;

    private HomeSalaryChangeInfo salaryChangeInfo;

    private HomeGradeChangeInfo gradeChangeInfo;

    private HomeProjectMaxMemberFlowInfo projectMaxMemberFlowInfo;


    public HomeProjectInfo getProjectInfo() {
        return projectInfo;
    }

    public void setProjectInfo(HomeProjectInfo projectInfo) {
        this.projectInfo = projectInfo;
    }

    public HomePlatformInfo getPlatformInfo() {
        return platformInfo;
    }

    public void setPlatformInfo(HomePlatformInfo platformInfo) {
        this.platformInfo = platformInfo;
    }

    public HomeFinanceInfo getFinanceInfo() {
        return financeInfo;
    }

    public void setFinanceInfo(HomeFinanceInfo financeInfo) {
        this.financeInfo = financeInfo;
    }

    public HomeHrInfo getHrInfo() {
        return hrInfo;
    }

    public void setHrInfo(HomeHrInfo hrInfo) {
        this.hrInfo = hrInfo;
    }

    public HomeOutsourceInfo getOutsourceInfo() {
        return outsourceInfo;
    }

    public void setOutsourceInfo(HomeOutsourceInfo outsourceInfo) {
        this.outsourceInfo = outsourceInfo;
    }

    public HomePerformanceInfo getPerformanceInfo() {
        return performanceInfo;
    }

    public void setPerformanceInfo(HomePerformanceInfo performanceInfo) {
        this.performanceInfo = performanceInfo;
    }

    public HomeSalaryChangeInfo getSalaryChangeInfo() {
        return salaryChangeInfo;
    }

    public void setSalaryChangeInfo(HomeSalaryChangeInfo salaryChangeInfo) {
        this.salaryChangeInfo = salaryChangeInfo;
    }

    public HomeGradeChangeInfo getGradeChangeInfo() {
        return gradeChangeInfo;
    }

    public void setGradeChangeInfo(HomeGradeChangeInfo gradeChangeInfo) {
        this.gradeChangeInfo = gradeChangeInfo;
    }

    public HomeProjectMaxMemberFlowInfo getProjectMaxMemberFlowInfo() {
        return projectMaxMemberFlowInfo;
    }

    public void setProjectMaxMemberFlowInfo(HomeProjectMaxMemberFlowInfo projectMaxMemberFlowInfo) {
        this.projectMaxMemberFlowInfo = projectMaxMemberFlowInfo;
    }

    public static class HomeProjectInfo {

        private Integer year;

        private Integer month;

        private Integer runProjectNumber;

        private Integer developProjectNumber;

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

        public Integer getRunProjectNumber() {
            return runProjectNumber;
        }

        public void setRunProjectNumber(Integer runProjectNumber) {
            this.runProjectNumber = runProjectNumber;
        }

        public Integer getDevelopProjectNumber() {
            return developProjectNumber;
        }

        public void setDevelopProjectNumber(Integer developProjectNumber) {
            this.developProjectNumber = developProjectNumber;
        }
    }

    public static class HomePlatformInfo {

        private Integer year;

        private Integer month;

        private Integer serveNumber;

        private Integer platformNumber;

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

        public Integer getServeNumber() {
            return serveNumber;
        }

        public void setServeNumber(Integer serveNumber) {
            this.serveNumber = serveNumber;
        }

        public Integer getPlatformNumber() {
            return platformNumber;
        }

        public void setPlatformNumber(Integer platformNumber) {
            this.platformNumber = platformNumber;
        }
    }

    public static class HomeFinanceInfo {

        private Integer year;

        private Integer month;

        private Float profit;

        private Float totalProfit;

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
    }

    public static class HomeHrInfo {

        private Integer year;

        private Integer month;

        private Integer totalPeople;

        private Integer servingPeople;

        private Integer leavingPeople;

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

        public Integer getTotalPeople() {
            return totalPeople;
        }

        public void setTotalPeople(Integer totalPeople) {
            this.totalPeople = totalPeople;
        }

        public Integer getServingPeople() {
            return servingPeople;
        }

        public void setServingPeople(Integer servingPeople) {
            this.servingPeople = servingPeople;
        }

        public Integer getLeavingPeople() {
            return leavingPeople;
        }

        public void setLeavingPeople(Integer leavingPeople) {
            this.leavingPeople = leavingPeople;
        }
    }

    public static class HomeOutsourceInfo {

        private Integer year;

        private Integer month;

        private Integer orderNumber;

        private Float orderAmount;

        private Float paidAmount;

        private Integer newOrderNumber;

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

        public Integer getOrderNumber() {
            return orderNumber;
        }

        public void setOrderNumber(Integer orderNumber) {
            this.orderNumber = orderNumber;
        }

        public Float getOrderAmount() {
            return orderAmount;
        }

        public void setOrderAmount(Float orderAmount) {
            this.orderAmount = orderAmount;
        }

        public Float getPaidAmount() {
            return paidAmount;
        }

        public void setPaidAmount(Float paidAmount) {
            this.paidAmount = paidAmount;
        }

        public Integer getNewOrderNumber() {
            return newOrderNumber;
        }

        public void setNewOrderNumber(Integer newOrderNumber) {
            this.newOrderNumber = newOrderNumber;
        }
    }

    public static class HomePerformanceInfo {
        private Integer year;

        private Integer month;

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
    }

    public static class HomeSalaryChangeInfo {
        private Integer year;

        private Integer month;

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
    }

    public static class HomeGradeChangeInfo {
        private Integer year;

        private Integer month;

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
    }

    public static class HomeProjectMaxMemberFlowInfo {
        private Integer year;

        private Integer month;

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
    }
}
