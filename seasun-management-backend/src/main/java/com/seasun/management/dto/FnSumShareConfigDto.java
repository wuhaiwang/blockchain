package com.seasun.management.dto;

import com.seasun.management.model.FnSumShareConfig;

import java.math.BigDecimal;

/**
 * Created by hedahai on 2017/7/25.
 */
public class FnSumShareConfigDto extends FnSumShareConfig {
    private String platName;
    private String projectName;
    private Long platParentHrId;
    private String platParentHrName;

    public String getPlatName() {
        return platName;
    }

    public void setPlatName(String platName) {
        this.platName = platName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Long getPlatParentHrId() {
        return platParentHrId;
    }

    public void setPlatParentHrId(Long platParentHrId) {
        this.platParentHrId = platParentHrId;
    }

    public String getPlatParentHrName() {
        return platParentHrName;
    }

    public void setPlatParentHrName(String platParentHrName) {
        this.platParentHrName = platParentHrName;
    }
}
