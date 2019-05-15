package com.seasun.management.vo;

import java.math.BigDecimal;

public class FnPlatSumProVo {
    private Long platId;
    private String platName;
    private BigDecimal sharePro;
    private boolean lockFlag;
    private String manager;

    public Long getPlatId() {
        return platId;
    }

    public void setPlatId(Long platId) {
        this.platId = platId;
    }

    public String getPlatName() {
        return platName;
    }

    public void setPlatName(String platName) {
        this.platName = platName;
    }

    public BigDecimal getSharePro() {
        return sharePro;
    }

    public void setSharePro(BigDecimal sharePro) {
        this.sharePro = sharePro;
    }

    public boolean isLockFlag() {
        return lockFlag;
    }

    public void setLockFlag(boolean lockFlag) {
        this.lockFlag = lockFlag;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }
}
