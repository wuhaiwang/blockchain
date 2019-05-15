package com.seasun.management.constant;

public interface WechatCropConstant {

     String CROPID = "wx9de6ae0965849551";

    /**
     * 微信企业号，下面有多个app, 微信企业号固定, 根据 secret 区分应用
     * */
    enum WechatCropApp {

        // 企业联系人
        CROPCONCATAPP (CROPID,"YRPFIbbsViepfh5Wr4xMOto4k2gJJRGB2FBBrExaKJg");

        private String secret ;

        private String cropId;

        public String getCropId() {
            return cropId;
        }

        public void setCropId(String cropId) {
            this.cropId = cropId;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        WechatCropApp (String cropId ,String secret) {
            this.secret = secret;
            this.cropId = cropId;
        }

    }


    /**
     * 请求类型 员工号|登录账号|姓名-手机
     * */
    enum  WechatCropRequestEnum {

        WECHATCROPREQUESTLOGINID ("loginId"),

        WECHATCROPREQUESTEMPLOYEENO ("employeeNo"),

        WECHATCROPREQUESTPHONEANDUSERNAME ("phoneAndUserName");

        private String type;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        WechatCropRequestEnum (String type) {
            this.type = type;
        }

    }


    /**
     *
     *微信接口请求 api
     * */
    enum  WechatCropRequestApiEnum {

        GETACCESSTOKEN ("cgi-bin/gettoken"),

        CREATECROPUSER ("cgi-bin/user/create"),

        GETCROPUSER ("cgi-bin/user/get"),

        DELETEUSER ("cgi-bin/user/delete");

        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        static final String DOMAIN = "https://qyapi.weixin.qq.com/";

        WechatCropRequestApiEnum (String url) {
            this.url = DOMAIN + url;
        }

    }

    /**
     * 微信企业号返回报文 枚举
     * */
    enum WechatCropResponseBodyEnum {

        CREATESUCCESS (0L),
        DELETESUCCESS (0L),
        TOKENERROR    (40014L),
        TOKENMISSING  (41001L),
        TOKENEXPIRE   (42001L);
        public Long getErrcode() {
            return errcode;
        }

        public void setErrcode(Long errcode) {
            this.errcode = errcode;
        }

        private Long errcode;

        WechatCropResponseBodyEnum (Long errcode) {
            this.errcode = errcode;
        }

    }



}
