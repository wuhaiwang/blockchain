package com.seasun.management.model;

import com.seasun.management.dto.BaseParentDto;

public class PerformanceWorkGroup extends BaseParentDto {
    private Long id;

    private Long workGroupId;

    private String name;

    // 严格：1，非严格：0，温和：2
    private Integer strictType;

    private Long parent;

    private Long performanceManagerId;

    private Boolean activeFlag;

    private Boolean projectConfirmFlag;

    public interface Id {
        Long ZHUHAI_ART_CENTER =173L;
    }

    public interface GroupStrictType {
        Integer strictNum = 1;
        Integer unStrictNum = 0;
        Integer normalNum = 2;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getWorkGroupId() {
        return workGroupId;
    }

    public void setWorkGroupId(Long workGroupId) {
        this.workGroupId = workGroupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getStrictType() {
        return strictType;
    }

    public void setStrictType(Integer strictType) {
        this.strictType = strictType;
    }

    public Long getParent() {
        return parent;
    }

    public void setParent(Long parent) {
        this.parent = parent;
    }

    public Long getPerformanceManagerId() {
        return performanceManagerId;
    }

    public void setPerformanceManagerId(Long performanceManagerId) {
        this.performanceManagerId = performanceManagerId;
    }

    public Boolean getActiveFlag() {
        return activeFlag;
    }

    public void setActiveFlag(Boolean activeFlag) {
        this.activeFlag = activeFlag;
    }

    public Boolean getProjectConfirmFlag() {
        return projectConfirmFlag;
    }

    public void setProjectConfirmFlag(Boolean projectConfirmFlag) {
        this.projectConfirmFlag = projectConfirmFlag;
    }
}