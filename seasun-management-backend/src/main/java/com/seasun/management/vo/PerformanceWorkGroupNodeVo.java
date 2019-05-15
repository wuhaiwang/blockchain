package com.seasun.management.vo;

import java.util.List;

public class PerformanceWorkGroupNodeVo {
    private Long id;
    private String title;
    private Long workGroupId;
    private Boolean available;
    private List<PerformanceWorkGroupNodeVo> nodes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getWorkGroupId() {
        return workGroupId;
    }

    public void setWorkGroupId(Long workGroupId) {
        this.workGroupId = workGroupId;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public List<PerformanceWorkGroupNodeVo> getNodes() {
        return nodes;
    }

    public void setNodes(List<PerformanceWorkGroupNodeVo> nodes) {
        this.nodes = nodes;
    }
}
