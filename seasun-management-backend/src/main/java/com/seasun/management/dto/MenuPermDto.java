package com.seasun.management.dto;


public class MenuPermDto {

    private Long id;

    private Long menuId;

    private String key;

    private String module;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }


    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }
}
