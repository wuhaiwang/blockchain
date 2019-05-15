package com.seasun.management.vo;

import java.util.List;

public class OrgProjectInfoAppVo {

    private Long id;

    private String name;

    private Integer memberNumber;

    private List<OrgWorkGroupMemberAppVo> members;

    private Integer shareMemberCount;

    private List<FnPlatShareMemberVo> shareMembers;

    private List<Group> groups;

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

    public Integer getMemberNumber() {
        return memberNumber;
    }

    public void setMemberNumber(Integer memberNumber) {
        this.memberNumber = memberNumber;
    }

    public List<OrgWorkGroupMemberAppVo> getMembers() {
        return members;
    }

    public void setMembers(List<OrgWorkGroupMemberAppVo> members) {
        this.members = members;
    }

    public Integer getShareMemberCount() {
        return shareMemberCount;
    }

    public void setShareMemberCount(Integer shareMemberCount) {
        this.shareMemberCount = shareMemberCount;
    }

    public List<FnPlatShareMemberVo> getShareMembers() {
        return shareMembers;
    }

    public void setShareMembers(List<FnPlatShareMemberVo> shareMembers) {
        this.shareMembers = shareMembers;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public static class Group {

        private Long id;

        private String name;

        private Integer memberNumber;

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

        public Integer getMemberNumber() {
            return memberNumber;
        }

        public void setMemberNumber(Integer memberNumber) {
            this.memberNumber = memberNumber;
        }
    }
}
