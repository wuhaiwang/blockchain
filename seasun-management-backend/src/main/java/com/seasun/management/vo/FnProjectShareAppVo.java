package com.seasun.management.vo;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class FnProjectShareAppVo {
    private JSONObject requestParam;

    private BasePeriodAppVo baseInfo;

    private List<ShareNumberOfYearMonth> statistics;

    @JsonProperty("detail")
    private List<ShareDetail> detail;

    private Boolean projectShareDetailFlag = false;

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

    public List<ShareNumberOfYearMonth> getStatistics() {
        return statistics;
    }

    public void setStatistics(List<ShareNumberOfYearMonth> statistics) {
        this.statistics = statistics;
    }

    public List<ShareDetail> getDetail() {
        return detail;
    }

    public void setDetail(List<ShareDetail> detail) {
        this.detail = detail;
    }

    public Boolean getProjectShareDetailFlag() {
        return projectShareDetailFlag;
    }

    public void setProjectShareDetailFlag(Boolean projectShareDetailFlag) {
        this.projectShareDetailFlag = projectShareDetailFlag;
    }

    public static class ShareNumberOfYearMonth {

        private Integer year;

        private Integer month;

        private Float shareNumber;

        private Float fixedNumber;

        private Float remainingNumber;

        private Float totalNumber;

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

        public Float getShareNumber() {
            return shareNumber;
        }

        public void setShareNumber(Float shareNumber) {
            this.shareNumber = shareNumber;
        }

        public Float getFixedNumber() {
            return fixedNumber;
        }

        public void setFixedNumber(Float fixedNumber) {
            this.fixedNumber = fixedNumber;
        }

        public Float getRemainingNumber() {
            return remainingNumber;
        }

        public void setRemainingNumber(Float remainingNumber) {
            this.remainingNumber = remainingNumber;
        }

        public Float getTotalNumber() {
            return totalNumber;
        }

        public void setTotalNumber(Float totalNumber) {
            this.totalNumber = totalNumber;
        }
    }

    public static class ShareDetail {

        @JsonProperty("name")
        private String projectName;

        private Float twoMonthAgoShareNumber;

        private Float lastMonthShareNumber;

        private Float currentMonthShareNumber;

        private Float twoMonthAgoFixedNumber;

        private Float lastMonthFixedNumber;

        private Float currentMonthFixedNumber;

        public String getProjectName() {
            return projectName;
        }

        public void setProjectName(String projectName) {
            this.projectName = projectName;
        }

        public Float getTwoMonthAgoShareNumber() {
            return twoMonthAgoShareNumber;
        }

        public void setTwoMonthAgoShareNumber(Float twoMonthAgoShareNumber) {
            this.twoMonthAgoShareNumber = twoMonthAgoShareNumber;
        }

        public Float getLastMonthShareNumber() {
            return lastMonthShareNumber;
        }

        public void setLastMonthShareNumber(Float lastMonthShareNumber) {
            this.lastMonthShareNumber = lastMonthShareNumber;
        }

        public Float getCurrentMonthShareNumber() {
            return currentMonthShareNumber;
        }

        public void setCurrentMonthShareNumber(Float currentMonthShareNumber) {
            this.currentMonthShareNumber = currentMonthShareNumber;
        }

        public Float getTwoMonthAgoFixedNumber() {
            return twoMonthAgoFixedNumber;
        }

        public void setTwoMonthAgoFixedNumber(Float twoMonthAgoFixedNumber) {
            this.twoMonthAgoFixedNumber = twoMonthAgoFixedNumber;
        }

        public Float getLastMonthFixedNumber() {
            return lastMonthFixedNumber;
        }

        public void setLastMonthFixedNumber(Float lastMonthFixedNumber) {
            this.lastMonthFixedNumber = lastMonthFixedNumber;
        }

        public Float getCurrentMonthFixedNumber() {
            return currentMonthFixedNumber;
        }

        public void setCurrentMonthFixedNumber(Float currentMonthFixedNumber) {
            this.currentMonthFixedNumber = currentMonthFixedNumber;
        }
    }
}
