package com.seasun.management.vo;

import com.seasun.management.dto.BaseLockFlagDto;
import com.seasun.management.dto.SimUserShareDto;

import java.util.List;

public class ShareMemberSumVo {

    private Boolean hasLockPerms;

    private List<ShareWorkGroup> group;

    private List<ShareWorkGroup> subGroup;

    public Boolean getHasLockPerms() {
        return hasLockPerms;
    }

    public void setHasLockPerms(Boolean hasLockPerms) {
        this.hasLockPerms = hasLockPerms;
    }

    public List<ShareWorkGroup> getGroup() {
        return group;
    }

    public void setGroup(List<ShareWorkGroup> group) {
        this.group = group;
    }

    public List<ShareWorkGroup> getSubGroup() {
        return subGroup;
    }

    public void setSubGroup(List<ShareWorkGroup> subGroup) {
        this.subGroup = subGroup;
    }

    public static class ShareWorkGroup extends BaseLockFlagDto {
        List<SimUserShareDto> members;

        public List<SimUserShareDto> getMembers() {
            return members;
        }

        public void setMembers(List<SimUserShareDto> members) {
            this.members = members;
        }
    }
}
