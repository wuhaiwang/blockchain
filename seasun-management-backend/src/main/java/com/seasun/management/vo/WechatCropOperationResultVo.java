package com.seasun.management.vo;

import java.util.List;

public class WechatCropOperationResultVo {

    public List<List<WechatCropBaseVo.WechatCropOperationVo>> getResultSet() {
        return resultSet;
    }

    public void setResultSet(List<List<WechatCropBaseVo.WechatCropOperationVo>> resultSet) {
        this.resultSet = resultSet;
    }

    public List<WechatCropBaseVo.WechatCropOperationVo> getUserNotFoundSet() {
        return userNotFoundSet;
    }

    public void setUserNotFoundSet(List<WechatCropBaseVo.WechatCropOperationVo> userNotFoundSet) {
        this.userNotFoundSet = userNotFoundSet;
    }

    @Override
    public String toString() {
        return "WechatCropOperationResultVo{" +
                "resultSet=" + resultSet +
                ", userNotFoundSet=" + userNotFoundSet +
                '}';
    }

    List<List< WechatCropBaseVo.WechatCropOperationVo>> resultSet;

    List<WechatCropBaseVo.WechatCropOperationVo> userNotFoundSet;

}
