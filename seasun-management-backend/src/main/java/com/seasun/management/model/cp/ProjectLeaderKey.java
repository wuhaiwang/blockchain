package com.seasun.management.model.cp;

public class ProjectLeaderKey {
    private String gameProject;

    private Integer userId;

    private Integer connectionType;

    public String getGameProject() {
        return gameProject;
    }

    public void setGameProject(String gameProject) {
        this.gameProject = gameProject == null ? null : gameProject.trim();
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(Integer connectionType) {
        this.connectionType = connectionType;
    }
}