package com.seasun.management.vo;

import com.seasun.management.model.RMenuProjectRolePerm;

import java.util.List;

public class RMenuProjectRolePermVo {

    private List<RMenuProjectRolePerm> menus;

    private List<ProjectRoleProjectVo> projects;

    public List<RMenuProjectRolePerm> getMenus() {
        return menus;
    }

    public void setMenus(List<RMenuProjectRolePerm> menus) {
        this.menus = menus;
    }

    public List<ProjectRoleProjectVo> getProjects() {
        return projects;
    }

    public void setProjects(List<ProjectRoleProjectVo> projects) {
        this.projects = projects;
    }

    public static class ProjectRoleUserVo {

        private Long id;

        private String userId;

        private String loginId;

        private String userName;

        private String photo;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getLoginId() {
            return loginId;
        }

        public void setLoginId(String loginId) {
            this.loginId = loginId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getPhoto() {
            return photo;
        }

        public void setPhoto(String photo) {
            this.photo = photo;
        }
    }


    public static class ProjectRoleProjectVo {

        private Long projectId;

        private String projectName;

        private List<ProjectRoleUserVo> users ;

        public Long getProjectId() {
            return projectId;
        }

        public void setProjectId(Long projectId) {
            this.projectId = projectId;
        }

        public String getProjectName() {
            return projectName;
        }

        public void setProjectName(String projectName) {
            this.projectName = projectName;
        }

        public List<ProjectRoleUserVo> getUsers() {
            return users;
        }

        public void setUsers(List<ProjectRoleUserVo> users) {
            this.users = users;
        }

    }

}
