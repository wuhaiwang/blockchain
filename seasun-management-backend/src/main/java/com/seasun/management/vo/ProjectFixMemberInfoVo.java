package com.seasun.management.vo;

import java.util.List;

public class ProjectFixMemberInfoVo {

    private int selectIndex;

    private int startIndex;

    private int endIndex;

    private History historyInfo;

    private List<Project> projects;

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

    public History getHistoryInfo() {
        return historyInfo;
    }

    public void setHistoryInfo(History historyInfo) {
        this.historyInfo = historyInfo;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    public static class Project {
        private Long projectId;

        private String name;

        private Integer total;

        private Integer unsubmittedCount;

        private Integer unconfirmedCount;

        private List<Plat> plats;

        public Long getProjectId() {
            return projectId;
        }

        public void setProjectId(Long projectId) {
            this.projectId = projectId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getTotal() {
            return total;
        }

        public void setTotal(Integer total) {
            this.total = total;
        }

        public Integer getUnsubmittedCount() {
            return unsubmittedCount;
        }

        public void setUnsubmittedCount(Integer unsubmittedCount) {
            this.unsubmittedCount = unsubmittedCount;
        }

        public Integer getUnconfirmedCount() {
            return unconfirmedCount;
        }

        public void setUnconfirmedCount(Integer unconfirmedCount) {
            this.unconfirmedCount = unconfirmedCount;
        }

        public List<Plat> getPlats() {
            return plats;
        }

        public void setPlats(List<Plat> plats) {
            this.plats = plats;
        }
    }

    public static class Plat {
        private String name;

        private List<FmGroupConfirmInfoVo> fmGroupConfirmInfoList;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<FmGroupConfirmInfoVo> getFmGroupConfirmInfoList() {
            return fmGroupConfirmInfoList;
        }

        public void setFmGroupConfirmInfoList(List<FmGroupConfirmInfoVo> fmGroupConfirmInfoList) {
            this.fmGroupConfirmInfoList = fmGroupConfirmInfoList;
        }
    }

    public static class History {
        private Integer startIndex;

        private Integer endIndex;

        private Integer selectIndex;

        private Integer year;

        private Integer month;

        private List<SubPerformanceAppVo.HistoryInfo> all;


        public Integer getEndIndex() {
            return endIndex;
        }

        public void setEndIndex(Integer endIndex) {
            this.endIndex = endIndex;
        }

        public Integer getStartIndex() {
            return startIndex;
        }

        public void setStartIndex(Integer startIndex) {
            this.startIndex = startIndex;
        }

        public Integer getSelectIndex() {
            return selectIndex;
        }

        public void setSelectIndex(Integer selectIndex) {
            this.selectIndex = selectIndex;
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

        public List<SubPerformanceAppVo.HistoryInfo> getAll() {
            return all;
        }

        public void setAll(List<SubPerformanceAppVo.HistoryInfo> all) {
            this.all = all;
        }
    }
}
