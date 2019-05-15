package com.seasun.management.dto;

import com.seasun.management.model.CfgGuiCategory;

import java.util.List;

public class AppGuiCategoryDto {

    private List<CfgGuiCategory> banner;

    private CfgGuiCategory hotSpot;

    private Boolean forumStartFlag;

    public Boolean getForumStartFlag() {
        return forumStartFlag;
    }

    public void setForumStartFlag(Boolean forumStartFlag) {
        this.forumStartFlag = forumStartFlag;
    }

    public List<CfgGuiCategory> getBanner() {
        return banner;
    }

    public void setBanner(List<CfgGuiCategory> banner) {
        this.banner = banner;
    }

    public CfgGuiCategory getHotSpot() {
        return hotSpot;
    }

    public void setHotSpot(CfgGuiCategory hotSpot) {
        this.hotSpot = hotSpot;
    }
}
