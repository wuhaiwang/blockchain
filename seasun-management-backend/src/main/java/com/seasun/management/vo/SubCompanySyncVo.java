package com.seasun.management.vo;

import com.fasterxml.jackson.databind.deser.Deserializers;
import com.seasun.management.model.Subcompany;

public class SubCompanySyncVo extends BaseSyncVo {
    private Subcompany data;

    public Subcompany getData() {
        return data;
    }

    public void setData(Subcompany data) {
        this.data = data;
    }
}
