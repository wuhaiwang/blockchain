package com.seasun.management.model.cp;

public class ProjectTypeVerify {
    private Integer id;

    private String gameProject;

    private String artType;

    private Integer verifyBy;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGameProject() {
        return gameProject;
    }

    public void setGameProject(String gameProject) {
        this.gameProject = gameProject == null ? null : gameProject.trim();
    }

    public String getArtType() {
        return artType;
    }

    public void setArtType(String artType) {
        this.artType = artType == null ? null : artType.trim();
    }

    public Integer getVerifyBy() {
        return verifyBy;
    }

    public void setVerifyBy(Integer verifyBy) {
        this.verifyBy = verifyBy;
    }
}