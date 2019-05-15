package com.seasun.management.vo;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

public class FnPlatCostAppVo {
    private JSONObject requestParam;

    private BasePeriodAppVo baseInfo;

    private List<Statistic> statistics;

    public JSONObject getRequestParam() {
        return requestParam;
    }

    public void setRequestParam(JSONObject requestParam) {
        this.requestParam = requestParam;
    }

    public BasePeriodAppVo getBaseInfo() {
        return baseInfo;
    }

    public void setBaseInfo(BasePeriodAppVo baseInfo) {
        this.baseInfo = baseInfo;
    }

    public List<Statistic> getStatistics() {
        return statistics;
    }

    public void setStatistics(List<Statistic> statistics) {
        this.statistics = statistics;
    }

    public static class Statistic {

        private Integer year;

        private Integer month;

        private Float internalCost;

        private Float shareCost;

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

        public Float getInternalCost() {
            return internalCost;
        }

        public void setInternalCost(Float internalCost) {
            this.internalCost = internalCost;
        }

        public Float getShareCost() {
            return shareCost;
        }

        public void setShareCost(Float shareCost) {
            this.shareCost = shareCost;
        }
    }

}
