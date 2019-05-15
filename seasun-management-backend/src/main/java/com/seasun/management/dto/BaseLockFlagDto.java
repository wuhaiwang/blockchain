package com.seasun.management.dto;

import com.seasun.management.model.IdNameBaseObject;

public class BaseLockFlagDto extends IdNameBaseObject {

    private boolean lockFlag;

    public boolean isLockFlag() {
        return lockFlag;
    }

    public void setLockFlag(boolean lockFlag) {
        this.lockFlag = lockFlag;
    }
}
