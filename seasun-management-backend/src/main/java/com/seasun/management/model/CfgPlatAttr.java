package com.seasun.management.model;

public class CfgPlatAttr {
    private Integer id;

    private Long platId;

    private Integer shareStartYear;

    private Integer shareStartMonth;

    private Boolean shareDetailFlag;

    private Boolean shareFlag;

    private Boolean shareWeekWriteFlag;

    public Boolean getShareWeekWriteFlag() {
        return shareWeekWriteFlag;
    }

    public void setShareWeekWriteFlag(Boolean shareWeekWriteFlag) {
        this.shareWeekWriteFlag = shareWeekWriteFlag;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getPlatId() {
        return platId;
    }

    public void setPlatId(Long platId) {
        this.platId = platId;
    }

    public Integer getShareStartYear() {
        return shareStartYear;
    }

    public void setShareStartYear(Integer shareStartYear) {
        this.shareStartYear = shareStartYear;
    }

    public Integer getShareStartMonth() {
        return shareStartMonth;
    }

    public void setShareStartMonth(Integer shareStartMonth) {
        this.shareStartMonth = shareStartMonth;
    }

    public Boolean getShareDetailFlag() {
        return shareDetailFlag;
    }

    public void setShareDetailFlag(Boolean shareDetailFlag) {
        this.shareDetailFlag = shareDetailFlag;
    }

    public Boolean getShareFlag() {
        return shareFlag;
    }

    public void setShareFlag(Boolean shareFlag) {
        this.shareFlag = shareFlag;
    }
}