package com.seasun.management.vo;

import com.seasun.management.dto.BaseLockFlagDto;
import com.seasun.management.model.IdNameBaseObject;

import java.math.BigDecimal;
import java.util.List;

public class FnWeekShareSumVo {
    private boolean lockFlag;
    private List<IdNameBaseObject> projects;
    private BigDecimal workday;
    private String status;
    private List<Member> members;

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }

    public boolean isLockFlag() {
        return lockFlag;
    }

    public void setLockFlag(boolean lockFlag) {
        this.lockFlag = lockFlag;
    }

    public List<IdNameBaseObject> getProjects() {
        return projects;
    }

    public void setProjects(List<IdNameBaseObject> projects) {
        this.projects = projects;
    }

    public BigDecimal getWorkday() {
        return workday;
    }

    public void setWorkday(BigDecimal workday) {
        this.workday = workday;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static class Member extends BaseLockFlagDto {
        private Long remarkId;

        private String remark;

        private List<ShareDto> data;

        private BigDecimal totalShareAmount;

        private GroupVo workGroup;

        public static class GroupVo extends BaseLockFlagDto{
            private boolean subGroupFlag;

            public boolean isSubGroupFlag() {
                return subGroupFlag;
            }

            public void setSubGroupFlag(boolean subGroupFlag) {
                this.subGroupFlag = subGroupFlag;
            }
        }

        public GroupVo getWorkGroup() {
            return workGroup;
        }

        public void setWorkGroup(GroupVo workGroup) {
            this.workGroup = workGroup;
        }

        public BigDecimal getTotalShareAmount() {
            return totalShareAmount;
        }

        public void setTotalShareAmount(BigDecimal totalShareAmount) {
            this.totalShareAmount = totalShareAmount;
        }

        public Long getRemarkId() {
            return remarkId;
        }

        public void setRemarkId(Long remarkId) {
            this.remarkId = remarkId;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public List<ShareDto> getData() {
            return data;
        }

        public void setData(List<ShareDto> data) {
            this.data = data;
        }

        public static class ShareDto {
            private Long id;
            private Long projectId;
            private BigDecimal shareAmount;
            private BigDecimal lastWeekValue;
            private String remark;

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

            public BigDecimal getShareAmount() {
                return shareAmount;
            }

            public void setShareAmount(BigDecimal shareAmount) {
                this.shareAmount = shareAmount;
            }

            public BigDecimal getLastWeekValue() {
                return lastWeekValue;
            }

            public void setLastWeekValue(BigDecimal lastWeekValue) {
                this.lastWeekValue = lastWeekValue;
            }

            public String getRemark() {
                return remark;
            }

            public void setRemark(String remark) {
                this.remark = remark;
            }
        }
    }


}
