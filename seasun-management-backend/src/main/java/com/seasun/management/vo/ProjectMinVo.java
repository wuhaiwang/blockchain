package com.seasun.management.vo;


import com.seasun.management.model.IdNameBaseObject;

import java.util.List;

public class ProjectMinVo{
    private Long id;
    private String projectName;
    private List<UserPhotoWallVo> items;
    private Integer size;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public List<UserPhotoWallVo> getItems() {
        return items;
    }

    public void setItems(List<UserPhotoWallVo> items) {
        this.items = items;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}