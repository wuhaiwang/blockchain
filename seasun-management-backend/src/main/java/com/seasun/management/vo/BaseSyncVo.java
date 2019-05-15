package com.seasun.management.vo;

import com.seasun.management.constant.SyncTarget;
import com.seasun.management.constant.SyncType;
import com.seasun.management.validator.StringEnumeration;


public class BaseSyncVo {

    @StringEnumeration(enumClass = SyncType.class, message = "all available values is：add,update,delete")
    private String type;

    @StringEnumeration(enumClass = SyncTarget.class, message = "targetName do not exist")
    private String targetName;

    public SyncType getType() {
        return SyncType.valueOf(type);
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }
}
