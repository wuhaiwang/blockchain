package com.seasun.management.model.cp;

public class AutoOrderTempletKey {
    private String gameProject;

    private String artType;

    private String title;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }
}