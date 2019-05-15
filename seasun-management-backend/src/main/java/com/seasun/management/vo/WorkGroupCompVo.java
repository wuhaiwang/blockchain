package com.seasun.management.vo;

import com.seasun.management.model.IdNameBaseObject;

import java.util.List;

public class WorkGroupCompVo {

    public static class UserSimVo extends IdNameBaseObject{
        private String loginId;

        public String getLoginId() {
            return loginId;
        }

        public void setLoginId(String loginId) {
            this.loginId = loginId;
        }
    }

    private List<UserSimVo> perfRedundantMember;

    private List<UserSimVo> hrRedundantMember;

    public List<UserSimVo> getPerfRedundantMember() {
        return perfRedundantMember;
    }

    public void setPerfRedundantMember(List<UserSimVo> perfRedundantMember) {
        this.perfRedundantMember = perfRedundantMember;
    }

    public List<UserSimVo> getHrRedundantMember() {
        return hrRedundantMember;
    }

    public void setHrRedundantMember(List<UserSimVo> hrRedundantMember) {
        this.hrRedundantMember = hrRedundantMember;
    }
}
