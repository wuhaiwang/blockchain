package com.seasun.management.vo;

import java.util.List;

public class RUserProjectPermListVo {
    private List<RUserProjectPermVo> systemRoles;
    private List<RUserProjectPermVo> projectRoles;
    private List<RUserProjectPermPlusVo> fixRoles;

    public List<RUserProjectPermVo> getSystemRoles() {
        return systemRoles;
    }

    public void setSystemRoles(List<RUserProjectPermVo> systemRoles) {
        this.systemRoles = systemRoles;
    }

    public List<RUserProjectPermVo> getProjectRoles() {
        return projectRoles;
    }

    public void setProjectRoles(List<RUserProjectPermVo> projectRoles) {
        this.projectRoles = projectRoles;
    }

    public List<RUserProjectPermPlusVo> getFixRoles() {
        return fixRoles;
    }

    public void setFixRoles(List<RUserProjectPermPlusVo> fixRoles) {
        this.fixRoles = fixRoles;
    }

    public static class RUserProjectPermPlusVo extends RUserProjectPermVo {
        private String type;

        public interface Type {
            String PROJECT = "project";
            String PLAT = "plat";
            String FIRSTCONFIRMER = "firstConfirmer";
            String SECONDCONFIRMER = "secondConfirmer";
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}
