package com.seasun.management.vo;

import com.seasun.management.model.WorkGroup;

import java.util.List;

public class WorkGroupPerformanceVo extends WorkGroup {

    private Long userId;

    private String userName;

    private String loginId;

    private String parentName;

    private Long workGroupRoleId;

    private List<UserInfo> hrList;

    private List<UserInfo> memberList;

    private UserInfo performanceManager;

    private UserInfo salaryChangeManager;

    private UserInfo gradeChangeManager;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public Long getWorkGroupRoleId() {
        return workGroupRoleId;
    }

    public void setWorkGroupRoleId(Long workGroupRoleId) {
        this.workGroupRoleId = workGroupRoleId;
    }

    public List<UserInfo> getHrList() {
        return hrList;
    }

    public void setHrList(List<UserInfo> hrList) {
        this.hrList = hrList;
    }

    public UserInfo getPerformanceManager() {
        return performanceManager;
    }

    public void setPerformanceManager(UserInfo performanceManager) {
        this.performanceManager = performanceManager;
    }

    public UserInfo getSalaryChangeManager() {
        return salaryChangeManager;
    }

    public void setSalaryChangeManager(UserInfo salaryChangeManager) {
        this.salaryChangeManager = salaryChangeManager;
    }

    public UserInfo getGradeChangeManager() {
        return gradeChangeManager;
    }

    public void setGradeChangeManager(UserInfo gradeChangeManager) {
        this.gradeChangeManager = gradeChangeManager;
    }

    public List<UserInfo> getMemberList() {
        return memberList;
    }

    public void setMemberList(List<UserInfo> memberList) {
        this.memberList = memberList;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public static class UserInfo {
        private String name;
        private String loginId;
        private String photo;

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
    }
}
