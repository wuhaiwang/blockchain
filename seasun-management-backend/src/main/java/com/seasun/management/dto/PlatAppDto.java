package com.seasun.management.dto;

import com.seasun.management.vo.PlatAppVo;

public class PlatAppDto extends PlatAppVo {
    private boolean activeFlag;
    private Integer appShowMode;
    private String serviceLine;
    private Long parentHrId;

    public boolean getActiveFlag() {
        return activeFlag;
    }

    public void setActiveFlag(boolean activeFlag) {
        this.activeFlag = activeFlag;
    }

    public Integer getAppShowMode() {
        return appShowMode;
    }

    public void setAppShowMode(Integer appShowMode) {
        this.appShowMode = appShowMode;
    }

    public String getServiceLine() {
        return serviceLine;
    }

    public void setServiceLine(String serviceLine) {
        this.serviceLine = serviceLine;
    }

    public Long getParentHrId() {
        return parentHrId;
    }

    public void setParentHrId(Long parentHrId) {
        this.parentHrId = parentHrId;
    }
}
