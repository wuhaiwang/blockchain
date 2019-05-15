package com.seasun.management.model;

public class FmRole {

    public interface Role {
        Long projectFixManager = 1L;
        Long projectFixFirstConfirmer = 2L;
        Long platFixManager = 3L;
        Long projectFixSecondConfirmer = 4L;
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