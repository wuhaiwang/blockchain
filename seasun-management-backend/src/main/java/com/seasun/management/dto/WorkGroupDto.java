package com.seasun.management.dto;

import com.seasun.management.model.WorkGroup;

import java.util.List;

public class WorkGroupDto<T extends WorkGroupUserDto> extends WorkGroup {

    private Long leaderId;

    private T leader;

    private List<T> Members;

    private List<WorkGroupDto> childWorkGroups;

    /* PerformanceWorkGroup property */
    private Long workGroupId;

    private Integer strictType;

    private Boolean projectConfirmFlag;

    public Long getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(Long leaderId) {
        this.leaderId = leaderId;
    }

    public T getLeader() {
        return leader;
    }

    public void setLeader(T leader) {
        this.leader = leader;
    }

    public List<T> getMembers() {
        return Members;
    }

    public void setMembers(List<T> members) {
        Members = members;
    }

    public List<WorkGroupDto> getChildWorkGroups() {
        return childWorkGroups;
    }

    public void setChildWorkGroups(List<WorkGroupDto> childWorkGroups) {
        this.childWorkGroups = childWorkGroups;
    }

    public Long getWorkGroupId() {
        return workGroupId;
    }

    public void setWorkGroupId(Long workGroupId) {
        this.workGroupId = workGroupId;
    }

    public Integer getStrictType() {
        return strictType;
    }

    public void setStrictType(Integer strictType) {
        this.strictType = strictType;
    }

    public Boolean getProjectConfirmFlag() {
        return projectConfirmFlag;
    }

    public void setProjectConfirmFlag(Boolean projectConfirmFlag) {
        this.projectConfirmFlag = projectConfirmFlag;
    }
}
