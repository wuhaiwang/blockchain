package com.seasun.management.vo;

public class UserWeekProjectPermVo extends UserProjectPermVo {

    private boolean platManagerFlag;

    private boolean projectManagerFlag;

    private boolean platObserverFlag;

    public boolean isProjectManagerFlag() {
        return projectManagerFlag;
    }

    public void setProjectManagerFlag(boolean projectManagerFlag) {
        this.projectManagerFlag = projectManagerFlag;
    }

    public boolean isPlatManagerFlag() {
        return platManagerFlag;
    }

    public void setPlatManagerFlag(boolean platManagerFlag) {
        this.platManagerFlag = platManagerFlag;
    }

    public boolean isPlatObserverFlag() {
        return platObserverFlag;
    }

    public void setPlatObserverFlag(boolean platObserverFlag) {
        this.platObserverFlag = platObserverFlag;
    }
}
