package com.seasun.management.model;

public class PerformanceObserver {
    private Long id;

    private Long perfManagerId;

    private Long observerId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPerfManagerId() {
        return perfManagerId;
    }

    public void setPerfManagerId(Long perfManagerId) {
        this.perfManagerId = perfManagerId;
    }

    public Long getObserverId() {
        return observerId;
    }

    public void setObserverId(Long observerId) {
        this.observerId = observerId;
    }
}