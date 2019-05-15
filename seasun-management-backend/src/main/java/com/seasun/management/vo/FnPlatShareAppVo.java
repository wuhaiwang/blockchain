package com.seasun.management.vo;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class FnPlatShareAppVo {
    private JSONObject requestParam;

    private BasePeriodAppVo baseInfo;

    private List<ShareNumberOfYearMonth> statistics;

    @JsonProperty("detail")
    private List<ShareDetailOfProject> shareDetailOfProjectList;

    @JsonProperty("detailByUser")
    private List<FnPlatShareConfigOfUserVo> fnPlatShareConfigOfUserVoList;

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

    public List<ShareDetailOfProject> getShareDetailOfProjectList() {
        return shareDetailOfProjectList;
    }

    public void setShareDetailOfProjectList(List<ShareDetailOfProject> shareDetailOfProjectList) {
        this.shareDetailOfProjectList = shareDetailOfProjectList;
    }

    public List<FnPlatShareConfigOfUserVo> getFnPlatShareConfigOfUserVoList() {
        return fnPlatShareConfigOfUserVoList;
    }

    public void setFnPlatShareConfigOfUserVoList(List<FnPlatShareConfigOfUserVo> fnPlatShareConfigOfUserVoList) {
        this.fnPlatShareConfigOfUserVoList = fnPlatShareConfigOfUserVoList;
    }

    public static class ShareNumberOfYearMonth {

        private Integer year;

        private Integer month;

        private Integer shareNumber;

        private Integer totalNumber;

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

        public Integer getShareNumber() {
            return shareNumber;
        }

        public void setShareNumber(Integer shareNumber) {
            this.shareNumber = shareNumber;
        }

        public Integer getTotalNumber() {
            return totalNumber;
        }

        public void setTotalNumber(Integer totalNumber) {
            this.totalNumber = totalNumber;
        }
    }

    public static class ShareDetailOfProject {

        @JsonProperty("name")
        private String projectName;

        @JsonProperty("twoMonthAgoShareNumber")
        private Float twoMonthAgoSharePro;

        @JsonProperty("lastMonthShareNumber")
        private Float lastMonthSharePro;

        @JsonProperty("currentMonthShareNumber")
        private Float currentMonthSharePro;

        public String getProjectName() {
            return projectName;
        }

        public void setProjectName(String projectName) {
            this.projectName = projectName;
        }

        public Float getTwoMonthAgoSharePro() {
            return twoMonthAgoSharePro;
        }

        public void setTwoMonthAgoSharePro(Float twoMonthAgoSharePro) {
            this.twoMonthAgoSharePro = twoMonthAgoSharePro;
        }

        public Float getLastMonthSharePro() {
            return lastMonthSharePro;
        }

        public void setLastMonthSharePro(Float lastMonthSharePro) {
            this.lastMonthSharePro = lastMonthSharePro;
        }

        public Float getCurrentMonthSharePro() {
            return currentMonthSharePro;
        }

        public void setCurrentMonthSharePro(Float currentMonthSharePro) {
            this.currentMonthSharePro = currentMonthSharePro;
        }
    }
}
