package com.seasun.management.model;

import java.util.List;

public class UserPerfWorkGroupVo {
    private Long userId;
    private String userName;
    private String loginId;
    private List<MinGroup> inChargeGroups;
    private MinGroup performanceWorkGroup;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public List<MinGroup> getInChargeGroups() {
        return inChargeGroups;
    }

    public void setInChargeGroups(List<MinGroup> inChargeGroups) {
        this.inChargeGroups = inChargeGroups;
    }

    public MinGroup getPerformanceWorkGroup() {
        return performanceWorkGroup;
    }

    public void setPerformanceWorkGroup(MinGroup performanceWorkGroup) {
        this.performanceWorkGroup = performanceWorkGroup;
    }

    public static class MinGroup{
        private String name;
        private Long id;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }
}
