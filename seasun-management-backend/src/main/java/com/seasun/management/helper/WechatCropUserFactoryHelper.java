package com.seasun.management.helper;

import com.seasun.management.dto.WechatCropConcatUserDto;
import com.seasun.management.exception.WechatCropException;
import com.seasun.management.vo.RequestUserVo;
import com.seasun.management.vo.WechatCropBaseVo;

import java.util.List;
import java.util.Map;

public class WechatCropUserFactoryHelper {

    public static abstract class WechatCropUserFactory {

        public WechatCropUserFactory () {

        }

        protected List<RequestUserVo> requestUserVoList;

        public List<RequestUserVo> getRequestUserVoList() {
            return requestUserVoList;
        }

        public WechatCropUserFactory(List<RequestUserVo> requestUserVoList) {
            this.requestUserVoList = requestUserVoList;
        }

        public void setRequestUserVoList(List<RequestUserVo> requestUserVoList) {
            this.requestUserVoList = requestUserVoList;
        }

        public abstract List<WechatCropConcatUserDto> createWechatCropUser() throws WechatCropException;

        public abstract List<WechatCropBaseVo.WechatCropOperationVo>     compareFindUserNotFound (List<WechatCropConcatUserDto> wechatCropConcatUserDtoList);

    }

    WechatCropUserFactory wechatCropUserFactory;

    public WechatCropUserFactoryHelper(WechatCropUserFactory wechatCropUserFactory) {
        this.wechatCropUserFactory = wechatCropUserFactory;
    }

    public WechatCropUserFactory getWechatCropUserFactory() {
        return wechatCropUserFactory;
    }

    public void setWechatCropUserFactory(WechatCropUserFactory wechatCropUserFactory) {
        this.wechatCropUserFactory = wechatCropUserFactory;
    }

    public List<WechatCropConcatUserDto> createWechatCropUser () throws WechatCropException{
        return wechatCropUserFactory.createWechatCropUser ();
    }

}
