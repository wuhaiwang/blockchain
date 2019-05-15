package com.seasun.management.vo;

import java.util.List;

public class OrgProjectWorkGroupInfoAppVo {

    private Long id;

    private String name;

    private Integer memberNumber;

    private List<OrgProjectWorkGroupInfoAppVo> children;

    private List<OrgWorkGroupMemberAppVo> members;

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

    public List<OrgProjectWorkGroupInfoAppVo> getChildrens() {
        return children;
    }

    public void setChildrens(List<OrgProjectWorkGroupInfoAppVo> childrens) {
        this.children = childrens;
    }

    public List<OrgWorkGroupMemberAppVo> getMembers() {
        return members;
    }

    public void setMembers(List<OrgWorkGroupMemberAppVo> members) {
        this.members = members;
    }
}
