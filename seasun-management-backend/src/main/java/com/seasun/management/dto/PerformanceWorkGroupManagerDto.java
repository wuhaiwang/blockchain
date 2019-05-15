package com.seasun.management.dto;

public class PerformanceWorkGroupManagerDto {

    private Long workGroupId;

    private String workGroupName;

    private Long parentId;

    private Long leaderId;

    private String leaderName;

    private String leaderLoginId;

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

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(Long leaderId) {
        this.leaderId = leaderId;
    }

    public String getLeaderName() {
        return leaderName;
    }

    public void setLeaderName(String leaderName) {
        this.leaderName = leaderName;
    }

    public String getLeaderLoginId() {
        return leaderLoginId;
    }

    public void setLeaderLoginId(String leaderLoginId) {
        this.leaderLoginId = leaderLoginId;
    }
}
