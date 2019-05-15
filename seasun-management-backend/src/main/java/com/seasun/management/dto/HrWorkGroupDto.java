package com.seasun.management.dto;

import com.seasun.management.model.WorkGroup;
import com.seasun.management.vo.OrgWorkGroupMemberAppVo;

import java.util.List;

public class HrWorkGroupDto extends WorkGroup {
    private String leaderIds;

    private List<OrgWorkGroupMemberAppVo> leaders;

    private List<OrgWorkGroupMemberAppVo> Members;

    private List<HrWorkGroupDto> childWorkGroups;

    public String getLeaderIds() {
        return leaderIds;
    }

    public void setLeaderIds(String leaderIds) {
        this.leaderIds = leaderIds;
    }

    public List<OrgWorkGroupMemberAppVo> getLeaders() {
        return leaders;
    }

    public void setLeaders(List<OrgWorkGroupMemberAppVo> leaders) {
        this.leaders = leaders;
    }

    public List<OrgWorkGroupMemberAppVo> getMembers() {
        return Members;
    }

    public void setMembers(List<OrgWorkGroupMemberAppVo> members) {
        Members = members;
    }

    public List<HrWorkGroupDto> getChildWorkGroups() {
        return childWorkGroups;
    }

    public void setChildWorkGroups(List<HrWorkGroupDto> childWorkGroups) {
        this.childWorkGroups = childWorkGroups;
    }
}
