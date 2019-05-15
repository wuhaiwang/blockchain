package com.seasun.management.vo;

import com.seasun.management.model.ProjectRole;

import java.util.List;

public class ProjectRoleVo {

    private List<ProjectRoleInfo> projectRoles;
    private List<ProjectRoleInfo> systemRoles;
    private List<ProjectRoleInfo> fixRoles;

    public List<ProjectRoleInfo> getProjectRoles() {
        return projectRoles;
    }

    public void setProjectRoles(List<ProjectRoleInfo> projectRoles) {
        this.projectRoles = projectRoles;
    }

    public List<ProjectRoleInfo> getSystemRoles() {
        return systemRoles;
    }

    public void setSystemRoles(List<ProjectRoleInfo> systemRoles) {
        this.systemRoles = systemRoles;
    }

    public List<ProjectRoleInfo> getFixRoles() {
        return fixRoles;
    }

    public void setFixRoles(List<ProjectRoleInfo> fixRoles) {
        this.fixRoles = fixRoles;
    }

    public static class ProjectRoleInfo {
        private Long id;

        private String name;

        private Boolean activeFlag;

        private Integer systemFlag;

        private boolean projectUseFlag;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Boolean getActiveFlag() {
            return activeFlag;
        }

        public void setActiveFlag(Boolean activeFlag) {
            this.activeFlag = activeFlag;
        }

        public Integer getSystemFlag() {
            return systemFlag;
        }

        public void setSystemFlag(Integer systemFlag) {
            this.systemFlag = systemFlag;
        }

        public boolean isProjectUseFlag() {
            return projectUseFlag;
        }

        public void setProjectUseFlag(boolean projectUseFlag) {
            this.projectUseFlag = projectUseFlag;
        }
    }
}
