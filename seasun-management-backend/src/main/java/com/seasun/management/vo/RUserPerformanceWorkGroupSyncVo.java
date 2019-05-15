package com.seasun.management.vo;

public class RUserPerformanceWorkGroupSyncVo extends BaseSyncVo {
    private RUserPerformanceWorkGroup data;

    public RUserPerformanceWorkGroup getData() {
        return data;
    }

    public void setData(RUserPerformanceWorkGroup data) {
        this.data = data;
    }

    public static class RUserPerformanceWorkGroup {
        private Long userId;

        private Long perfWorkGroupId;

        private Long managerId;

        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public Long getPerfWorkGroupId() {
            return perfWorkGroupId;
        }

        public void setPerfWorkGroupId(Long perfWorkGroupId) {
            this.perfWorkGroupId = perfWorkGroupId;
        }

        public Long getManagerId() {
            return managerId;
        }

        public void setManagerId(Long managerId) {
            this.managerId = managerId;
        }
    }
}
