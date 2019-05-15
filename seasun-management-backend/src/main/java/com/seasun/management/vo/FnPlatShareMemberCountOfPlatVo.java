package com.seasun.management.vo;

public class FnPlatShareMemberCountOfPlatVo {
    private Long platId;
    private String platName;
    private Integer shareMemberCount;

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

    public Integer getShareMemberCount() {
        return shareMemberCount;
    }

    public void setShareMemberCount(Integer shareMemberCount) {
        this.shareMemberCount = shareMemberCount;
    }
}
