package com.seasun.management.vo;

import java.util.List;

public class WorkGroupMemberPerformanceAppVo {
    private PerformanceWorkGroupInfoAppVo groupInfo;

    private List<MemberPerformanceAppVo> managerPerformanceList;

    public PerformanceWorkGroupInfoAppVo getGroupInfo() {
        return groupInfo;
    }

    public void setGroupInfo(PerformanceWorkGroupInfoAppVo groupInfo) {
        this.groupInfo = groupInfo;
    }

    public List<MemberPerformanceAppVo> getManagerPerformanceList() {
        return managerPerformanceList;
    }

    public void setManagerPerformanceList(List<MemberPerformanceAppVo> managerPerformanceList) {
        this.managerPerformanceList = managerPerformanceList;
    }
}
