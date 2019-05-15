package com.seasun.management.vo;

import java.util.List;

public class ImportFixMemberDataResultVo {
    private Integer code;
    private String message;
    private String url;
    private Conflict conflict;
    private String fileBackup;

    public static class Conflict {
        List<ConflictMember> removedMembers;
        List<ConflictMember> changedMembers;

        public static class ConflictMember {
            private Long id;
            private String name;
            private String loginId;
            private Group originFixGroup;
            private Group changeTo;
            private String reason;

            public static class Group {
                private Long id;
                private String name;

                public Long getId() {
                    return id;
                }

                public void setId(Long id) {
                    this.id = id;
                }

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }
            }

            public Long getId() {
                return id;
            }

            public void setId(Long id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getLoginId() {
                return loginId;
            }

            public void setLoginId(String loginId) {
                this.loginId = loginId;
            }

            public Group getOriginFixGroup() {
                return originFixGroup;
            }

            public void setOriginFixGroup(Group originFixGroup) {
                this.originFixGroup = originFixGroup;
            }

            public Group getChangeTo() {
                return changeTo;
            }

            public void setChangeTo(Group changeTo) {
                this.changeTo = changeTo;
            }

            public String getReason() {
                return reason;
            }

            public void setReason(String reason) {
                this.reason = reason;
            }
        }

        public List<ConflictMember> getRemovedMembers() {
            return removedMembers;
        }

        public void setRemovedMembers(List<ConflictMember> removedMembers) {
            this.removedMembers = removedMembers;
        }

        public List<ConflictMember> getChangedMembers() {
            return changedMembers;
        }

        public void setChangedMembers(List<ConflictMember> changedMembers) {
            this.changedMembers = changedMembers;
        }
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Conflict getConflict() {
        return conflict;
    }

    public void setConflict(Conflict conflict) {
        this.conflict = conflict;
    }

    public String getFileBackup() {
        return fileBackup;
    }

    public void setFileBackup(String fileBackup) {
        this.fileBackup = fileBackup;
    }
}
