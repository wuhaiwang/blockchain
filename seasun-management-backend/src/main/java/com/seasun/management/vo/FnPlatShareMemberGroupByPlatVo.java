package com.seasun.management.vo;

import java.util.List;

public class FnPlatShareMemberGroupByPlatVo {
    private List<FnPlatShareMemberCountOfPlatVo> fnPlatShareMemberCountOfPlatVoList;
    private List<FnPlatShareMemberVo> fnPlatShareMemberVoList;

    public List<FnPlatShareMemberCountOfPlatVo> getFnPlatShareMemberCountOfPlatVoList() {
        return fnPlatShareMemberCountOfPlatVoList;
    }

    public void setFnPlatShareMemberCountOfPlatVoList(List<FnPlatShareMemberCountOfPlatVo> fnPlatShareMemberCountOfPlatVoList) {
        this.fnPlatShareMemberCountOfPlatVoList = fnPlatShareMemberCountOfPlatVoList;
    }

    public List<FnPlatShareMemberVo> getFnPlatShareMemberVoList() {
        return fnPlatShareMemberVoList;
    }

    public void setFnPlatShareMemberVoList(List<FnPlatShareMemberVo> fnPlatShareMemberVoList) {
        this.fnPlatShareMemberVoList = fnPlatShareMemberVoList;
    }
}
