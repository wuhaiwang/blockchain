package com.seasun.management.vo;


import com.seasun.management.model.IdNameBaseObject;

import java.util.List;

public class UserPhotoWallVo extends IdNameBaseObject{
    private Integer year;
    private Integer month;
    private List<ProjectMinVo> items;
    private String projectName;
    private String loginId;
    private String photo;
    private String source;
    private String targetPath;//压缩文件构建路径
    private String floorNo;
    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public List<ProjectMinVo> getItems() {
        return items;
    }

    public void setItems(List<ProjectMinVo> items) {
        this.items = items;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getTargetPath() {
        return targetPath;
    }

    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
    }

    public String getFloorNo() {
        return floorNo;
    }

    public void setFloorNo(String floorNo) {
        this.floorNo = floorNo;
    }
}
