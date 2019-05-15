package com.seasun.management.vo;

import java.util.List;

public class FnPlatManageAndMemberVo {
    private Long projectRoleId;
    private List<FnPlatShareMemberVo> shareManagers;
    private List<FnPlatShareMemberVo> shareObservers;
    private List<FnPlatShareMemberVo> shareMembers;

    public Long getProjectRoleId() {
        return projectRoleId;
    }

    public void setProjectRoleId(Long projectRoleId) {
        this.projectRoleId = projectRoleId;
    }

    public List<FnPlatShareMemberVo> getShareManagers() {
        return shareManagers;
    }

    public void setShareManagers(List<FnPlatShareMemberVo> shareManagers) {
        this.shareManagers = shareManagers;
    }

    public List<FnPlatShareMemberVo> getShareObservers() {
        return shareObservers;
    }

    public void setShareObservers(List<FnPlatShareMemberVo> shareObservers) {
        this.shareObservers = shareObservers;
    }

    public List<FnPlatShareMemberVo> getShareMembers() {
        return shareMembers;
    }

    public void setShareMembers(List<FnPlatShareMemberVo> shareMembers) {
        this.shareMembers = shareMembers;
    }
}
