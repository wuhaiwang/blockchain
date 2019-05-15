package com.seasun.management.vo;

import java.util.List;

public class SubGroupPerformanceVo {

    private Long groupId;

    private String name;

    private String status;

    private Manager manager;

    private List<Confirm> confirmList;

    private List<SubGroupPerformanceVo> subGroups;

    public List<Confirm> getConfirmList() {
        return confirmList;
    }

    public void setConfirmList(List<Confirm> confirmList) {
        this.confirmList = confirmList;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Manager getManager() {
        return manager;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
    }

    public List<SubGroupPerformanceVo> getSubGroups() {
        return subGroups;
    }

    public void setSubGroups(List<SubGroupPerformanceVo> subGroups) {
        this.subGroups = subGroups;
    }

    public static class Confirm {
        private Long id;

        private String projectName;

        private String projectConfirmerName;

        private Boolean confirmFlag;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getProjectName() {
            return projectName;
        }

        public void setProjectName(String projectName) {
            this.projectName = projectName;
        }

        public String getProjectConfirmerName() {
            return projectConfirmerName;
        }

        public void setProjectConfirmerName(String projectConfirmerName) {
            this.projectConfirmerName = projectConfirmerName;
        }

        public Boolean getConfirmFlag() {
            return confirmFlag;
        }

        public void setConfirmFlag(Boolean confirmFlag) {
            this.confirmFlag = confirmFlag;
        }
    }

    public static class Manager {

        private Long userId;

        private String managerName;

        private String loginId;

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getManagerName() {
            return managerName;
        }

        public void setManagerName(String managerName) {
            this.managerName = managerName;
        }

        public String getLoginId() {
            return loginId;
        }

        public void setLoginId(String loginId) {
            this.loginId = loginId;
        }
    }
}
