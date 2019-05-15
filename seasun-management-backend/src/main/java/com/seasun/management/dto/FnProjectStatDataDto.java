package com.seasun.management.dto;

import com.seasun.management.model.FnProjectStatData;

public class FnProjectStatDataDto extends FnProjectStatData {

    private String name;

    private Long parentId;

    private String parentName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }
}
