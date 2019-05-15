package com.seasun.management.vo;

import com.seasun.management.model.IdNameBaseObject;

import java.util.List;

public class PlatFavorProjectInfoVo {
    private List<IdNameBaseObject> shareProjects ;
    private List<IdNameBaseObject> favorProjects;

    public List<IdNameBaseObject> getShareProjects() {
        return shareProjects;
    }

    public void setShareProjects(List<IdNameBaseObject> shareProjects) {
        this.shareProjects = shareProjects;
    }

    public List<IdNameBaseObject> getFavorProjects() {
        return favorProjects;
    }

    public void setFavorProjects(List<IdNameBaseObject> favorProjects) {
        this.favorProjects = favorProjects;
    }
}
