package com.seasun.management.model;


import java.io.Serializable;
import java.util.List;

public class WorkGroup {
    private Long id;

    private String name;

    private Boolean activeFlag;

    private Long parent;

    //  the following are user defined...


    private Boolean hasUserFlag;

    private List<WorkGroup> children;

    	// regular member number
    private Integer memberNumber;
    
    // intern member number
    private Integer internNumber;

    private String fullPathName;

    private Boolean shareWeekFlag;

    public Boolean getShareWeekFlag() {
        return shareWeekFlag;
    }

    public void setShareWeekFlag(Boolean shareWeekFlag) {
        this.shareWeekFlag = shareWeekFlag;
    }

    public Integer getMemberNumber() {
        return memberNumber;
    }

    public void setMemberNumber(Integer memberNumber) {
        this.memberNumber = memberNumber;
    }
    
    public Integer getInternNumber() {
		return internNumber;
	}
    
    public void setInternNumber(Integer internNumber) {
		this.internNumber = internNumber;
	}

    public Boolean getHasUserFlag() {
        return hasUserFlag;
    }

    public void setHasUserFlag(Boolean hasUserFlag) {
        this.hasUserFlag = hasUserFlag;
    }

    public List<WorkGroup> getChildren() {
        return children;
    }

    public void setChildren(List<WorkGroup> children) {
        this.children = children;
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
        this.name = name == null ? null : name.trim();
    }

    public Boolean getActiveFlag() {
        return activeFlag;
    }

    public void setActiveFlag(Boolean activeFlag) {
        this.activeFlag = activeFlag;
    }

    public Long getParent() {
        return parent;
    }

    public void setParent(Long parent) {
        this.parent = parent;
    }

    public String getFullPathName() {
        return fullPathName;
    }

    public void setFullPathName(String fullPathName) {
        this.fullPathName = fullPathName;
    }
}