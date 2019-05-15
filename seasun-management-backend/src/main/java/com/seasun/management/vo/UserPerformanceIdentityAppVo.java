package com.seasun.management.vo;

import java.util.List;

public class UserPerformanceIdentityAppVo {

    private Boolean leaderFlag;

    private Boolean fixMemberFlag;

    private Boolean myPerformanceFlag;

    private List<WorkGroup> workGroups;

    public Boolean getLeaderFlag() {
        return leaderFlag;
    }

    public void setLeaderFlag(Boolean leaderFlag) {
        this.leaderFlag = leaderFlag;
    }

    public Boolean getFixMemberFlag() {
        return fixMemberFlag;
    }

    public void setFixMemberFlag(Boolean fixMemberFlag) {
        this.fixMemberFlag = fixMemberFlag;
    }

    public Boolean getMyPerformanceFlag() {
        return myPerformanceFlag;
    }

    public void setMyPerformanceFlag(Boolean myPerformanceFlag) {
        this.myPerformanceFlag = myPerformanceFlag;
    }

    public List<WorkGroup> getWorkGroups() {
        return workGroups;
    }

    public void setWorkGroups(List<WorkGroup> workGroups) {
        this.workGroups = workGroups;
    }

    public static class WorkGroup {

        private Long workGroupId;

        private String workGroupName;

        public Long getWorkGroupId() {
            return workGroupId;
        }

        public void setWorkGroupId(Long workGroupId) {
            this.workGroupId = workGroupId;
        }

        public String getWorkGroupName() {
            return workGroupName;
        }

        public void setWorkGroupName(String workGroupName) {
            this.workGroupName = workGroupName;
        }
    }
}
