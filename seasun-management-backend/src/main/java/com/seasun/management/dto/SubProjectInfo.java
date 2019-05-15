package com.seasun.management.dto;

import com.seasun.management.model.Project;

import java.util.ArrayList;
import java.util.List;

public class SubProjectInfo {
    private List<Project> subProjects;
    private String type;

    public interface Type {
        String project = "project";
        String summary = "summary";
        String all = "all";
    }

    public SubProjectInfo() {
        subProjects = new ArrayList<>();
    }

    public List<Project> getSubProjects() {
        return subProjects;
    }

    public void setSubProjects(List<Project> subProjects) {
        this.subProjects = subProjects;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
