package com.seasun.management.vo;

import java.util.List;

public class SalaryChangeCharacterVo {

    private Boolean isLeader;

    private List<GroupManagerIdentity> groupManagerIdentity;

    public List<GroupManagerIdentity> getGroupManagerIdentity() {
        return groupManagerIdentity;
    }

    public void setGroupManagerIdentity(List<GroupManagerIdentity> groupManagerIdentity) {
        this.groupManagerIdentity = groupManagerIdentity;
    }

    public Boolean getLeader() {
        return isLeader;
    }

    public void setLeader(Boolean leader) {
        isLeader = leader;
    }

    public static class GroupManagerIdentity {
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
