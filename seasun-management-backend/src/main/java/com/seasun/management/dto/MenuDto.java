package com.seasun.management.dto;

import com.seasun.management.model.Menu;

public class MenuDto extends Menu {

    private String module;

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }
}
