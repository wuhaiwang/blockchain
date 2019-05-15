package com.seasun.management.dto;

import com.seasun.management.model.RMenuProjectRolePerm;

public class RMenuProjectRolePermDto extends RMenuProjectRolePerm {
    private String key;

    private String type;

    private String module;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }
}
