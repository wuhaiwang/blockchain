package com.seasun.management.vo;

public class FnProjectShareDetailVo {

    private String platName;
    private Long platId;
    private Float sharePro;
    private String userName;
    private String workGroupName;

    public String getWorkGroupName() {
        return workGroupName;
    }

    public void setWorkGroupName(String workGroupName) {
        this.workGroupName = workGroupName;
    }

    public String getPlatName() {
        return platName;
    }

    public void setPlatName(String platName) {
        this.platName = platName;
    }

    public Long getPlatId() {
        return platId;
    }

    public void setPlatId(Long platId) {
        this.platId = platId;
    }

    public Float getSharePro() {
        return sharePro;
    }

    public void setSharePro(Float sharePro) {
        this.sharePro = sharePro;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
