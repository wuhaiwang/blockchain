package com.seasun.management.vo;

import java.util.List;

public class PerformanceFMGroupsVo {
    private Integer memberCount;
    private Integer groupCount;
    private Boolean perfSubmitFlag;
    private Integer year;
    private Integer month;

    public static class Group {
        private Long id;
        private Long projectId;
        private Long platId;
        private String name;
        private ProjectManager projectManager;
        private List<Member> members;
        private Boolean applyFlag;

        public static class Member {
            private Long id;
            private String name;
            private String loginId;
            private String photoAddress;
            private Boolean permanentFlag;

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

            public String getLoginId() {
                return loginId;
            }

            public void setLoginId(String loginId) {
                this.loginId = loginId;
            }

            public String getPhotoAddress() {
                return photoAddress;
            }

            public void setPhotoAddress(String photoAddress) {
                this.photoAddress = photoAddress;
            }

            public Boolean getPermanentFlag() {
                return permanentFlag;
            }

            public void setPermanentFlag(Boolean permanentFlag) {
                this.permanentFlag = permanentFlag;
            }
        }

        public static class ProjectManager {
            private Long id;
            private String name;

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
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getProjectId() {
            return projectId;
        }

        public void setProjectId(Long projectId) {
            this.projectId = projectId;
        }

        public Long getPlatId() {
            return platId;
        }

        public void setPlatId(Long platId) {
            this.platId = platId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<Member> getMembers() {
            return members;
        }

        public void setMembers(List<Member> members) {
            this.members = members;
        }

        public ProjectManager getProjectManager() {
            return projectManager;
        }

        public void setProjectManager(ProjectManager projectManager) {
            this.projectManager = projectManager;
        }

        public Boolean getApplyFlag() {
            return applyFlag;
        }

        public void setApplyFlag(Boolean applyFlag) {
            this.applyFlag = applyFlag;
        }
    }

    private List<Group> groups;

    public Integer getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(Integer memberCount) {
        this.memberCount = memberCount;
    }

    public Integer getGroupCount() {
        return groupCount;
    }

    public void setGroupCount(Integer groupCount) {
        this.groupCount = groupCount;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public Boolean getPerfSubmitFlag() {
        return perfSubmitFlag;
    }

    public void setPerfSubmitFlag(Boolean perfSubmitFlag) {
        this.perfSubmitFlag = perfSubmitFlag;
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
}
