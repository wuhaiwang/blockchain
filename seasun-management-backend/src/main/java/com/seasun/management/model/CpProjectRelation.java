package com.seasun.management.model;

public class CpProjectRelation {
    private Long id;

    private Long itProjectId;

    private Long cpProjectId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getItProjectId() {
        return itProjectId;
    }

    public void setItProjectId(Long itProjectId) {
        this.itProjectId = itProjectId;
    }

    public Long getCpProjectId() {
        return cpProjectId;
    }

    public void setCpProjectId(Long cpProjectId) {
        this.cpProjectId = cpProjectId;
    }
}