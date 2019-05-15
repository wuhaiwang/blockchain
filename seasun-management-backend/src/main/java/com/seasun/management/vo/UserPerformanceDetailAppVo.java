package com.seasun.management.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;
import java.util.List;

public class UserPerformanceDetailAppVo {
    private Long performanceId;

    private ProjectFixMemberInfoVo.History historyInfo;

    @JsonIgnore
    private List<SubPerformanceBaseVo.HistoryInfo> history;

    private int selectIndex;

    private int startIndex;

    private int endIndex;

    private UserInfo userInfo;

    private PerformanceInfo performanceInfo;

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

    public Long getPerformanceId() {
        return performanceId;
    }

    public void setPerformanceId(Long performanceId) {
        this.performanceId = performanceId;
    }

    public List<SubPerformanceBaseVo.HistoryInfo> getHistory() {
        return history;
    }

    public void setHistory(List<SubPerformanceBaseVo.HistoryInfo> history) {
        this.history = history;
    }

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

        private String workAge;

        private String workAgeInKs;

        private String post;

        private String workGroup;

        private String workStatus;

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

        public String getWorkAge() {
            return workAge;
        }

        public void setWorkAge(String workAge) {
            this.workAge = workAge;
        }

        public String getWorkAgeInKs() {
            return workAgeInKs;
        }

        public void setWorkAgeInKs(String workAgeInKs) {
            this.workAgeInKs = workAgeInKs;
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

        public String getWorkStatus() {
            return workStatus;
        }

        public void setWorkStatus(String workStatus) {
            this.workStatus = workStatus;
        }
    }

    public static class PerformanceInfo {
        private String selfPerformance;

        private String finalPerformance;

        private String monthGoal;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss", timezone = "GMT+8")
        private Date monthGoalLastModifyTime;

        private String selfComment;

        private String managerComment;

        private String status;

        private String lastModifyUser;

        private String directManagerComment;

        public String getLastModifyUser() {
            return lastModifyUser;
        }

        public void setLastModifyUser(String lastModifyUser) {
            this.lastModifyUser = lastModifyUser;
        }

        public String getDirectManagerComment() {
            return directManagerComment;
        }

        public void setDirectManagerComment(String directManagerComment) {
            this.directManagerComment = directManagerComment;
        }

        public String getSelfPerformance() {
            return selfPerformance;
        }

        public void setSelfPerformance(String selfPerformance) {
            this.selfPerformance = selfPerformance;
        }

        public String getFinalPerformance() {
            return finalPerformance;
        }

        public void setFinalPerformance(String finalPerformance) {
            this.finalPerformance = finalPerformance;
        }

        public String getMonthGoal() {
            return monthGoal;
        }

        public void setMonthGoal(String monthGoal) {
            this.monthGoal = monthGoal;
        }

        public Date getMonthGoalLastModifyTime() {
            return monthGoalLastModifyTime;
        }

        public void setMonthGoalLastModifyTime(Date monthGoalLastModifyTime) {
            this.monthGoalLastModifyTime = monthGoalLastModifyTime;
        }

        public String getSelfComment() {
            return selfComment;
        }

        public void setSelfComment(String selfComment) {
            this.selfComment = selfComment;
        }

        public String getManagerComment() {
            return managerComment;
        }

        public void setManagerComment(String managerComment) {
            this.managerComment = managerComment;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
