package com.seasun.management.vo;

public class ProjectParentHrVo {

    private Long id;
    private String name;
    private Long parentHrProjectId;
    private String parentHrProjectName;

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

    public Long getParentHrProjectId() {
        return parentHrProjectId;
    }

    public void setParentHrProjectId(Long parentHrProjectId) {
        this.parentHrProjectId = parentHrProjectId;
    }

    public String getParentHrProjectName() {
        return parentHrProjectName;
    }

    public void setParentHrProjectName(String parentHrProjectName) {
        this.parentHrProjectName = parentHrProjectName;
    }
}
