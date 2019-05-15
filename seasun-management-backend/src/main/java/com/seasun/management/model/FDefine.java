package com.seasun.management.model;

public class FDefine {

    public interface Name {
        String projectMaxMemberProcessApproval = "人数上限申请";
        String projectMaxMemberProcessDeploy = "人数上限调配";
    }

    private Long id;

    private String name;

    private String description;

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
        this.name = name == null ? null : name.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }
}