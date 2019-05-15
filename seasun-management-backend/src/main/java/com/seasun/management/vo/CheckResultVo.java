package com.seasun.management.vo;


import java.util.List;

public class CheckResultVo {

    private List<CommonCheckResultDto> commonCheckResults;

    private List<PerfUserCheckResultVo> notInWorkGroups;

    private NotInPerfWorkGroupDto notInPerfWorkGroup;

    private int perfWorkGroupTreeMemberTotal;

    private int workGroupTreeMemberTotal;

    public int getPerfWorkGroupTreeMemberTotal() {
        return perfWorkGroupTreeMemberTotal;
    }

    public void setPerfWorkGroupTreeMemberTotal(int perfWorkGroupTreeMemberTotal) {
        this.perfWorkGroupTreeMemberTotal = perfWorkGroupTreeMemberTotal;
    }

    public int getWorkGroupTreeMemberTotal() {
        return workGroupTreeMemberTotal;
    }

    public void setWorkGroupTreeMemberTotal(int workGroupTreeMemberTotal) {
        this.workGroupTreeMemberTotal = workGroupTreeMemberTotal;
    }

    public List<CommonCheckResultDto> getCommonCheckResults() {
        return commonCheckResults;
    }

    public void setCommonCheckResults(List<CommonCheckResultDto> commonCheckResults) {
        this.commonCheckResults = commonCheckResults;
    }

    public List<PerfUserCheckResultVo> getNotInWorkGroups() {
        return notInWorkGroups;
    }

    public void setNotInWorkGroups(List<PerfUserCheckResultVo> notInWorkGroups) {
        this.notInWorkGroups = notInWorkGroups;
    }

    public NotInPerfWorkGroupDto getNotInPerfWorkGroup() {
        return notInPerfWorkGroup;
    }

    public void setNotInPerfWorkGroup(NotInPerfWorkGroupDto notInPerfWorkGroup) {
        this.notInPerfWorkGroup = notInPerfWorkGroup;
    }

    public static class NotInPerfWorkGroupDto {

        private List<PerfUserCheckResultVo> users;

        private List<IgnoreWorkGroup> ignoreWorkGroups;

        public List<PerfUserCheckResultVo> getUsers() {
            return users;
        }

        public void setUsers(List<PerfUserCheckResultVo> users) {
            this.users = users;
        }

        public List<IgnoreWorkGroup> getIgnoreWorkGroups() {
            return ignoreWorkGroups;
        }

        public void setIgnoreWorkGroups(List<IgnoreWorkGroup> ignoreWorkGroups) {
            this.ignoreWorkGroups = ignoreWorkGroups;
        }

        public static class IgnoreWorkGroup {

            private String workGroupName;

            private int memberCount;

            private int notInCount;

            public String getWorkGroupName() {
                return workGroupName;
            }

            public void setWorkGroupName(String workGroupName) {
                this.workGroupName = workGroupName;
            }

            public int getMemberCount() {
                return memberCount;
            }

            public void setMemberCount(int memberCount) {
                this.memberCount = memberCount;
            }

            public int getNotInCount() {
                return notInCount;
            }

            public void setNotInCount(int notInCount) {
                this.notInCount = notInCount;
            }

            private List<PerfUserCheckResultVo> users;

            public List<PerfUserCheckResultVo> getUsers() {
                return users;
            }

            public void setUsers(List<PerfUserCheckResultVo> users) {
                this.users = users;
            }

        }


    }

    public static class CommonCheckResultDto {

        private String itemName;

        private boolean checkResultFlag;

        private String errorMessage;


        public String getItemName() {
            return itemName;
        }

        public void setItemName(String itemName) {
            this.itemName = itemName;
        }

        public boolean isCheckResultFlag() {
            return checkResultFlag;
        }

        public void setCheckResultFlag(boolean checkResultFlag) {
            this.checkResultFlag = checkResultFlag;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }
    }
}


