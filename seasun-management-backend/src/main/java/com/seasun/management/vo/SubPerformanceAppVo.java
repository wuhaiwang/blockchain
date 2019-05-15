package com.seasun.management.vo;

import java.util.List;

public class SubPerformanceAppVo extends SubPerformanceBaseVo {

    private List<MemberPerformanceAppVo> managerPerformanceList;

    private SubGroupStatistics subGroup;

    public List<MemberPerformanceAppVo> getManagerPerformanceList() {
        return managerPerformanceList;
    }

    public void setManagerPerformanceList(List<MemberPerformanceAppVo> managerPerformanceList) {
        this.managerPerformanceList = managerPerformanceList;
    }

    public SubGroupStatistics getSubGroup() {
        return subGroup;
    }

    public void setSubGroup(SubGroupStatistics subGroup) {
        this.subGroup = subGroup;
    }

    public static class SubGroupStatistics {

        private Integer total;

        private Integer memberCount;

        private List<PerformanceWorkGroupInfoAppVo> data;

        public Integer getTotal() {
            return total;
        }

        public void setTotal(Integer total) {
            this.total = total;
        }

        public Integer getMemberCount() {
            return memberCount;
        }

        public void setMemberCount(Integer memberCount) {
            this.memberCount = memberCount;
        }

        public List<PerformanceWorkGroupInfoAppVo> getData() {
            return data;
        }

        public void setData(List<PerformanceWorkGroupInfoAppVo> data) {
            this.data = data;
        }
    }
}
