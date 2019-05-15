package com.seasun.management.vo;

import com.seasun.management.model.IdNameBaseObject;
import com.seasun.management.model.PerformanceWorkGroup;
import com.seasun.management.model.UserIdMinVo;

import java.util.List;

public class PerformanceWorkGroupVo extends PerformanceWorkGroup {

    private List<UserInfo> directMembers;

    private Long total;

    private String managerName;

    private List<UserIdMinVo> dataManagers;

    private List<UserIdMinVo> observers;

    private List<UserIdMinVo> humanConfigurator;

    private String workGroupName;

    public List<UserInfo> getDirectMembers() {
        return directMembers;
    }

    public void setDirectMembers(List<UserInfo> directMembers) {
        this.directMembers = directMembers;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public List<UserIdMinVo> getDataManagers() {
        return dataManagers;
    }

    public void setDataManagers(List<UserIdMinVo> dataManagers) {
        this.dataManagers = dataManagers;
    }


    public String getWorkGroupName() {
        return workGroupName;
    }

    public void setWorkGroupName(String workGroupName) {
        this.workGroupName = workGroupName;
    }

    public List<UserIdMinVo> getObservers() {
        return observers;
    }

    public void setObservers(List<UserIdMinVo> observers) {
        this.observers = observers;
    }

    public List<UserIdMinVo> getHumanConfigurator() {
        return humanConfigurator;
    }

    public void setHumanConfigurator(List<UserIdMinVo> humanConfigurator) {
        this.humanConfigurator = humanConfigurator;
    }

    @Override
    public String toString() {
        return "PerformanceWorkGroupVo{" +
                "directMembers=" + directMembers +
                ", total=" + total +
                ", managerName='" + managerName + '\'' +
                ", dataManagers=" + dataManagers +
                ", observers=" + observers +
                ", humanConfigurator=" + humanConfigurator +
                ", workGroupName='" + workGroupName + '\'' +
                '}';
    }

    public static class UserInfo {
        private String name;
        private String loginId;
        private String photo;
        private Boolean fixedFlag;
        private String fixedWorkGroupName;

        public Boolean getFixedFlag() {
            return fixedFlag;
        }

        public void setFixedFlag(Boolean fixedFlag) {
            this.fixedFlag = fixedFlag;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLoginId() {
            return loginId;
        }

        public void setLoginId(String loginId) {
            this.loginId = loginId;
        }

        public String getPhoto() {
            return photo;
        }

        public void setPhoto(String photo) {
            this.photo = photo;
        }

        public String getFixedWorkGroupName() {
            return fixedWorkGroupName;
        }

        public void setFixedWorkGroupName(String fixedWorkGroupName) {
            this.fixedWorkGroupName = fixedWorkGroupName;
        }
    }
}
