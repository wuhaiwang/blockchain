package com.seasun.management.vo;

public class WechatCropBaseVo {

    public static class WechatCropOperationVo extends WechatCropBaseVo {

        private String transactionId;

        @Override
        public String toString() {
            return "WechatCropOperationVo{" +
                    "transactionId='" + transactionId + '\'' +
                    ", errcode=" + super.errcode +
                    ", errmsg='" + super.errmsg + '\'' +
                    '}';
        }

        public String getTransactionId() {
            return transactionId;
        }

        public void setTransactionId(String transactionId) {
            this.transactionId = transactionId;
        }
    }

    private Long errcode;

    private String errmsg;

    public Long getErrcode() {
        return errcode;
    }

    public void setErrcode(Long errcode) {
        this.errcode = errcode;
    }

    @Override
    public String toString() {
        return "WechatCropBaseVo{" +
                "errcode=" + errcode +
                ", errmsg='" + errmsg + '\'' +
                '}';
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }
}
