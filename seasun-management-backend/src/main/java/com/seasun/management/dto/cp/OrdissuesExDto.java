package com.seasun.management.dto.cp;

import com.seasun.management.model.cp.OrdissuesEx;

public class OrdissuesExDto extends OrdissuesEx {

    private Integer status;

    private Integer cPProjectId;

    public Integer getcPProjectId() {
        return cPProjectId;
    }

    public void setcPProjectId(Integer cPProjectId) {
        this.cPProjectId = cPProjectId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
