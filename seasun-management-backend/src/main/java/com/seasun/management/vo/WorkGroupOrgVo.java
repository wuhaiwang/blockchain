package com.seasun.management.vo;

import java.util.List;

public class WorkGroupOrgVo {

    private Long id;
    private Long parent;
    private String name;
    private Long userId;
    private String manager;
    private String memberCount;
    private List<WorkGroupOrgVo> children;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParent() {
        return parent;
    }

    public void setParent(Long parent) {
        this.parent = parent;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public String getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(String memberCount) {
        this.memberCount = memberCount;
    }

    public List<WorkGroupOrgVo> getChildren() {
        return children;
    }

    public void setChildren(List<WorkGroupOrgVo> children) {
        this.children = children;
    }

}
