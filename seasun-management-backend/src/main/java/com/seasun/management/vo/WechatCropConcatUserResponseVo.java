package com.seasun.management.vo;

import com.seasun.management.dto.WechatCropConcatUserDto;

public class WechatCropConcatUserResponseVo extends WechatCropConcatUserDto {

    private Long errcode;

    private String errmsg;

    public Long getErrcode() {
        return errcode;
    }

    public void setErrcode(Long errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }


}
