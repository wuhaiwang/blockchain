package com.seasun.management.vo;

import com.seasun.management.model.IdNameBaseObject;

public class MiniProjectVo extends IdNameBaseObject {

    private String serviceLine;
    private String type;

    public String getServiceLine() {
        return serviceLine;
    }

    public void setServiceLine(String serviceLine) {
        this.serviceLine = serviceLine;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
