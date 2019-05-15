package com.seasun.management.model;

public class FnPlatFavorProject {
    private Long id;

    private Long platId;

    private Long favorProjectId;

    private Integer sort;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPlatId() {
        return platId;
    }

    public void setPlatId(Long platId) {
        this.platId = platId;
    }

    public Long getFavorProjectId() {
        return favorProjectId;
    }

    public void setFavorProjectId(Long favorProjectId) {
        this.favorProjectId = favorProjectId;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}