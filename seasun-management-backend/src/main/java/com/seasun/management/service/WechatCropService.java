package com.seasun.management.service;

import com.seasun.management.vo.RequestUserVo;
import com.seasun.management.vo.WechatCropOperationResultVo;

import java.util.List;

public interface WechatCropService {

    WechatCropOperationResultVo updateUsers (List<RequestUserVo> requestUserVoList, String type) throws Exception;

}
