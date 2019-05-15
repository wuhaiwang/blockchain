package com.seasun.management.vo;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

public class HrUserTransactionAppVo {
    private JSONObject requestParam;

    private List<Statistics> statistics;

    private List<Detail> project;

    private List<Detail> platform;

    public JSONObject getRequestParam() {
        return requestParam;
    }

    public void setRequestParam(JSONObject requestParam) {
        this.requestParam = requestParam;
    }

    public List<Statistics> getStatistics() {
        return statistics;
    }

    public void setStatistics(List<Statistics> statistics) {
        this.statistics = statistics;
    }

    public List<Detail> getProject() {
        return project;
    }

    public void setProject(List<Detail> project) {
        this.project = project;
    }

    public List<Detail> getPlatform() {
        return platform;
    }

    public void setPlatform(List<Detail> platform) {
        this.platform = platform;
    }

    public static class Statistics {

        private Integer year;

        private Integer month;

        private Long totalPeople;

        private Long servingPeople;

        private Long leavingPeople;

        private Long transInPeople;

        private Long transOutPeople;


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

        public Long getTotalPeople() {
            return totalPeople;
        }

        public void setTotalPeople(Long totalPeople) {
            this.totalPeople = totalPeople;
        }

        public Long getServingPeople() {
            return servingPeople;
        }

        public void setServingPeople(Long servingPeople) {
            this.servingPeople = servingPeople;
        }

        public Long getLeavingPeople() {
            return leavingPeople;
        }

        public void setLeavingPeople(Long leavingPeople) {
            this.leavingPeople = leavingPeople;
        }

        public Long getTransInPeople() {
            return transInPeople;
        }

        public void setTransInPeople(Long transInPeople) {
            this.transInPeople = transInPeople;
        }

        public Long getTransOutPeople() {
            return transOutPeople;
        }

        public void setTransOutPeople(Long transOutPeople) {
            this.transOutPeople = transOutPeople;
        }
    }

    public static class Detail {

        private String name;

        private Long twoMonthAgoPeople;

        private Long lastMonthPeople;

        private Long currentMonthPeople;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Long getTwoMonthAgoPeople() {
            return twoMonthAgoPeople;
        }

        public void setTwoMonthAgoPeople(Long twoMonthAgoPeople) {
            this.twoMonthAgoPeople = twoMonthAgoPeople;
        }

        public Long getLastMonthPeople() {
            return lastMonthPeople;
        }

        public void setLastMonthPeople(Long lastMonthPeople) {
            this.lastMonthPeople = lastMonthPeople;
        }

        public Long getCurrentMonthPeople() {
            return currentMonthPeople;
        }

        public void setCurrentMonthPeople(Long currentMonthPeople) {
            this.currentMonthPeople = currentMonthPeople;
        }
    }
}
