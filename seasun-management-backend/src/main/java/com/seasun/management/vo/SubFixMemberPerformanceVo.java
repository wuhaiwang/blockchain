package com.seasun.management.vo;

import java.util.List;

public class SubFixMemberPerformanceVo extends SubPerformanceBaseVo {

    private Integer total;

    private List<Plat> plats;

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<Plat> getPlats() {
        return plats;
    }

    public void setPlats(List<Plat> plats) {
        this.plats = plats;
    }

    public static class Plat {
        private String name;

        private Integer total;

        private List<Project> projects;

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

        public List<Project> getProjects() {
            return projects;
        }

        public void setProjects(List<Project> projects) {
            this.projects = projects;
        }
    }

    public static class Project {
        private String name;

        private Integer total;

        private String manager;

        private Boolean confirmFlag;

        private List<MemberPerformanceAppVo> performances;


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

        public String getManager() {
            return manager;
        }

        public void setManager(String manager) {
            this.manager = manager;
        }

        public Boolean getConfirmFlag() {
            return confirmFlag;
        }

        public void setConfirmFlag(Boolean confirmFlag) {
            this.confirmFlag = confirmFlag;
        }

        public List<MemberPerformanceAppVo> getPerformances() {
            return performances;
        }

        public void setPerformances(List<MemberPerformanceAppVo> performances) {
            this.performances = performances;
        }
    }
}
