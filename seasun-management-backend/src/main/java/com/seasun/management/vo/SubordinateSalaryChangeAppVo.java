package com.seasun.management.vo;

import java.util.List;

public class SubordinateSalaryChangeAppVo {

    private Boolean submitFlag;

    private String groupStatus;

    private List<SubordinateSalaryChangeAppVo.GroupStatus> history;

    private SubordinateSalaryChangeAppVo.Profile profile;

    private List<OrdinateSalaryChangeAppVo> leaderSalaryChangeList;

    private SubordinateSalaryChangeAppVo.SubGroup subGroup;


    public static class GroupStatus {
        private Integer year;
        private Integer quarter;
        private String status;

        public interface Status {
            String submitted = "已提交";//待确认
            String waitingForCommit = "待提交";//待提交
            String finished = "已完成";//已完成
        }

        public interface StatusVo {
            String finished = "已完成";//已完成
            String underway = "进行中";//进行中
            String delayed = "延误中";//延误中
            String submitted = "已提交";//已提交
        }

        public Integer getYear() {
            return year;
        }

        public void setYear(Integer year) {
            this.year = year;
        }

        public Integer getQuarter() {
            return quarter;
        }

        public void setQuarter(Integer quarter) {
            this.quarter = quarter;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public GroupStatus() {
            super();
        }

        public GroupStatus(Integer year, Integer quarter, String status) {
            this.year = year;
            this.quarter = quarter;
            this.status = status;
        }

        @Override
        public String toString() {
            return "GroupStatus{" +
                    "year=" + year +
                    ", quarter=" + quarter +
                    ", status='" + status + '\'' +
                    '}';
        }
    }

    public static class Profile {
        private Integer total;
        private Integer salaryIncreaseNumber;
        private Long salaryIncreaseAmount;
        private Integer waitingForCutNumber;

        public Integer getTotal() {
            return total;
        }

        public void setTotal(Integer total) {
            this.total = total;
        }

        public Integer getSalaryIncreaseNumber() {
            return salaryIncreaseNumber;
        }

        public void setSalaryIncreaseNumber(Integer salaryIncreaseNumber) {
            this.salaryIncreaseNumber = salaryIncreaseNumber;
        }

        public Long getSalaryIncreaseAmount() {
            return salaryIncreaseAmount;
        }

        public void setSalaryIncreaseAmount(Long salaryIncreaseAmount) {
            this.salaryIncreaseAmount = salaryIncreaseAmount;
        }

        public Integer getWaitingForCutNumber() {
            return waitingForCutNumber;
        }

        public void setWaitingForCutNumber(Integer waitingForCutNumber) {
            this.waitingForCutNumber = waitingForCutNumber;
        }
    }

    public static class SubGroup {
        private Integer total;
        private Integer memberCount;
        private List<SubordinateSalaryChangeAppVo.SubGroup.GroupSalarySituation> data;

        public static class GroupSalarySituation {
            private Long groupId;
            private String groupName;
            private Integer total;
            private String manager;
            private Integer salaryIncreaseNumber;
            private Long salaryIncreaseAmount;
            private Integer waitingForCutNumber;
            private String status;
            private List<OrdinateSalaryChangeAppVo> subSalaryChangeList;

            public Long getGroupId() {
                return groupId;
            }

            public void setGroupId(Long groupId) {
                this.groupId = groupId;
            }

            public String getGroupName() {
                return groupName;
            }

            public void setGroupName(String groupName) {
                this.groupName = groupName;
            }

            public Integer getTotal() {
                return total;
            }

            public void setTotal(Integer total) {
                this.total = total;
            }

            public String getManager() {
                return manager;
            }

            public void setManager(String manager) {
                this.manager = manager;
            }

            public Integer getSalaryIncreaseNumber() {
                return salaryIncreaseNumber;
            }

            public void setSalaryIncreaseNumber(Integer salaryIncreaseNumber) {
                this.salaryIncreaseNumber = salaryIncreaseNumber;
            }

            public Long getSalaryIncreaseAmount() {
                return salaryIncreaseAmount;
            }

            public void setSalaryIncreaseAmount(Long salaryIncreaseAmount) {
                this.salaryIncreaseAmount = salaryIncreaseAmount;
            }

            public Integer getWaitingForCutNumber() {
                return waitingForCutNumber;
            }

            public void setWaitingForCutNumber(Integer waitingForCutNumber) {
                this.waitingForCutNumber = waitingForCutNumber;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public List<OrdinateSalaryChangeAppVo> getSubSalaryChangeList() {
                return subSalaryChangeList;
            }

            public void setSubSalaryChangeList(List<OrdinateSalaryChangeAppVo> subSalaryChangeList) {
                this.subSalaryChangeList = subSalaryChangeList;
            }
        }

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

        public List<SubordinateSalaryChangeAppVo.SubGroup.GroupSalarySituation> getData() {
            return data;
        }

        public void setData(List<SubordinateSalaryChangeAppVo.SubGroup.GroupSalarySituation> data) {
            this.data = data;
        }

    }


    public String getGroupStatus() {
        return groupStatus;
    }

    public void setGroupStatus(String groupStatus) {
        this.groupStatus = groupStatus;
    }

    public List<SubordinateSalaryChangeAppVo.GroupStatus> getHistory() {
        return history;
    }

    public void setHistory(List<SubordinateSalaryChangeAppVo.GroupStatus> history) {
        this.history = history;
    }

    public SubordinateSalaryChangeAppVo.Profile getProfile() {
        return profile;
    }

    public void setProfile(SubordinateSalaryChangeAppVo.Profile profile) {
        this.profile = profile;
    }

    public List<OrdinateSalaryChangeAppVo> getLeaderSalaryChangeList() {
        return leaderSalaryChangeList;
    }

    public void setLeaderSalaryChangeList(List<OrdinateSalaryChangeAppVo> leaderSalaryChangeList) {
        this.leaderSalaryChangeList = leaderSalaryChangeList;
    }

    public SubordinateSalaryChangeAppVo.SubGroup getSubGroup() {
        return subGroup;
    }

    public void setSubGroup(SubordinateSalaryChangeAppVo.SubGroup subGroup) {
        this.subGroup = subGroup;
    }

    public Boolean getSubmitFlag() {
        return submitFlag;
    }

    public void setSubmitFlag(Boolean submitFlag) {
        this.submitFlag = submitFlag;
    }
}
