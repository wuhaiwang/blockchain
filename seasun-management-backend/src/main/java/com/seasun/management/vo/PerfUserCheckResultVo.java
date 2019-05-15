package com.seasun.management.vo;

import com.seasun.management.model.PerfUserCheckResult;

public class PerfUserCheckResultVo  extends PerfUserCheckResult{

    private String userName ;

    private String workGroupName ;

    private String loginId;

    private String perfWorkGroupName;

    private Boolean cadetFlag;

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public Boolean getCadetFlag() {
        return cadetFlag;
    }

    public void setCadetFlag(Boolean cadetFlag) {
        this.cadetFlag = cadetFlag;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getWorkGroupName() {
        return workGroupName;
    }

    public void setWorkGroupName(String workGroupName) {
        this.workGroupName = workGroupName;
    }

    public String getPerfWorkGroupName() {
        return perfWorkGroupName;
    }

    public void setPerfWorkGroupName(String perfWorkGroupName) {
        this.perfWorkGroupName = perfWorkGroupName;
    }
}
