package com.seasun.management.model;

public class PmQualityProjectRelation {
    private Long id;

    private Long itProjectId;

    private Long qualityProjectId;

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

    public Long getQualityProjectId() {
        return qualityProjectId;
    }

    public void setQualityProjectId(Long qualityProjectId) {
        this.qualityProjectId = qualityProjectId;
    }
}