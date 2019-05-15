package com.seasun.management.vo;

import com.seasun.management.model.RUserProjectPerm;

public class UserProjectPermVo extends RUserProjectPerm {

    private boolean managerFlag;

    private String projectName;

    private Float Weight;

    private boolean shareDetailFlag;

    private boolean shareWeekWriteFlag;

    public boolean isShareWeekWriteFlag() {
        return shareWeekWriteFlag;
    }

    public void setShareWeekWriteFlag(boolean shareWeekWriteFlag) {
        this.shareWeekWriteFlag = shareWeekWriteFlag;
    }

    public boolean isShareDetailFlag() {
        return shareDetailFlag;
    }

    public void setShareDetailFlag(boolean shareDetailFlag) {
        this.shareDetailFlag = shareDetailFlag;
    }

    public boolean isManagerFlag() {
        return managerFlag;
    }

    public void setManagerFlag(boolean managerFlag) {
        this.managerFlag = managerFlag;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Float getWeight() {
        return Weight;
    }

    public void setWeight(Float weight) {
        Weight = weight;
    }
}
