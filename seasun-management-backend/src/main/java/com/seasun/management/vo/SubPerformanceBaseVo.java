package com.seasun.management.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.seasun.management.dto.KeyValueDto;

import java.util.List;

public class SubPerformanceBaseVo {
    private List<KeyValueDto> filter;

    @JsonIgnore
    private List<HistoryInfo> history;

    private ProjectFixMemberInfoVo.History historyInfo;

    private int selectIndex;

    private int startIndex;

    private int endIndex;

    private Profile profile;

    public ProjectFixMemberInfoVo.History getHistoryInfo() {
        return historyInfo;
    }

    public void setHistoryInfo(ProjectFixMemberInfoVo.History historyInfo) {
        this.historyInfo = historyInfo;
    }

    public int getSelectIndex() {
        return selectIndex;
    }

    public void setSelectIndex(int selectIndex) {
        this.selectIndex = selectIndex;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    public List<KeyValueDto> getFilter() {
        return filter;
    }

    public void setFilter(List<KeyValueDto> filter) {
        this.filter = filter;
    }

    public List<HistoryInfo> getHistory() {
        return history;
    }

    public void setHistory(List<HistoryInfo> history) {
        this.history = history;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public static class HistoryInfo {
        public interface Status {
            String delay = "延误中";
            String processing = "进行中";
            String submitted = "已提交";
            String complete = "已完成";
            String confirmed = "已确认";
            String waitingForStart = "未开始";
        }

        private Integer year;

        private Integer month;

        private String status;

        private Integer startDay;

        public Integer getStartDay() {
            return startDay;
        }

        public void setStartDay(Integer startDay) {
            this.startDay = startDay;
        }

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

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    public static class Profile {

        private Integer total;

        private Integer waitingForCommit;

        private Integer waitingForReview;

        private Integer invalided;

        private Integer strictType;

        private Integer managerCount;

        private PerformanceWorkGroupInfoAppVo.PerformancePro performancePro;

        private Boolean projectConfirmFlag;

        private Boolean fmGroupConfirmedFlag;

        private List<FmGroupConfirmInfoVo> fmGroupConfirmInfoList;

        public Integer getTotal() {
            return total;
        }

        public void setTotal(Integer total) {
            this.total = total;
        }

        public Integer getWaitingForCommit() {
            return waitingForCommit;
        }

        public void setWaitingForCommit(Integer waitingForCommit) {
            this.waitingForCommit = waitingForCommit;
        }

        public Integer getWaitingForReview() {
            return waitingForReview;
        }

        public void setWaitingForReview(Integer waitingForReview) {
            this.waitingForReview = waitingForReview;
        }

        public Integer getInvalided() {
            return invalided;
        }

        public void setInvalided(Integer invalided) {
            this.invalided = invalided;
        }

        public Integer getStrictType() {
            return strictType;
        }

        public void setStrictType(Integer strictType) {
            this.strictType = strictType;
        }

        public Integer getManagerCount() {
            return managerCount;
        }

        public void setManagerCount(Integer managerCount) {
            this.managerCount = managerCount;
        }

        public PerformanceWorkGroupInfoAppVo.PerformancePro getPerformancePro() {
            return performancePro;
        }

        public void setPerformancePro(PerformanceWorkGroupInfoAppVo.PerformancePro performancePro) {
            this.performancePro = performancePro;
        }

        public Boolean getProjectConfirmFlag() {
            return projectConfirmFlag;
        }

        public void setProjectConfirmFlag(Boolean projectConfirmFlag) {
            this.projectConfirmFlag = projectConfirmFlag;
        }

        public Boolean getFmGroupConfirmedFlag() {
            return fmGroupConfirmedFlag;
        }

        public void setFmGroupConfirmedFlag(Boolean fmGroupConfirmedFlag) {
            this.fmGroupConfirmedFlag = fmGroupConfirmedFlag;
        }

        public List<FmGroupConfirmInfoVo> getFmGroupConfirmInfoList() {
            return fmGroupConfirmInfoList;
        }

        public void setFmGroupConfirmInfoList(List<FmGroupConfirmInfoVo> fmGroupConfirmInfoList) {
            this.fmGroupConfirmInfoList = fmGroupConfirmInfoList;
        }
    }
}
