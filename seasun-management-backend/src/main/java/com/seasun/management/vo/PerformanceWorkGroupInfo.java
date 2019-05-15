package com.seasun.management.vo;

import com.seasun.management.model.PerformanceWorkGroup;

public class PerformanceWorkGroupInfo extends PerformanceWorkGroup {
    Long managerPerformanceGroupId;

    Long memberCount;

    Long childGroupCount;

    public Long getChildGroupCount() {
        return childGroupCount;
    }

    public void setChildGroupCount(Long childGroupCount) {
        this.childGroupCount = childGroupCount;
    }

    public Long getManagerPerformanceGroupId() {
        return managerPerformanceGroupId;
    }

    public void setManagerPerformanceGroupId(Long managerPerformanceGroupId) {
        this.managerPerformanceGroupId = managerPerformanceGroupId;
    }

    public Long getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(Long memberCount) {
        this.memberCount = memberCount;
    }
}
