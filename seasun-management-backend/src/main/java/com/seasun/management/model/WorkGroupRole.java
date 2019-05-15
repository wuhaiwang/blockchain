package com.seasun.management.model;

public class WorkGroupRole {

    public interface Role {
        Long performance = 1L;
        Long salary = 2L;
        Long hr = 3L;
        Long grade = 4L;
    }

    private Long id;

    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }
}