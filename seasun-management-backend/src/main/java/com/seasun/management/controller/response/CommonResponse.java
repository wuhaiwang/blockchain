package com.seasun.management.controller.response;

import com.seasun.management.constant.ErrorCode;
import org.apache.commons.lang3.StringUtils;

public class CommonResponse {

    private int code;
    private String message;

    private Object data; // 2018_01_18 添加， commonResponse 一般需要包含返回数据

    public CommonResponse() {
        this.code = 0;
        this.message = "success";
    }

    public CommonResponse(Object data) {
        this.code = 0;
        this.message = "success";
        this.data = data;
    }

    public CommonResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public CommonResponse(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public static CommonResponse buildOperationResponse(Boolean result, Integer errorCode, String errorMessage) {
        errorMessage = StringUtils.isBlank(errorMessage) ? "failed" : errorMessage;
        errorCode = errorCode == null ?  ErrorCode.PARAM_ERROR : errorCode;
        String  responseMessage = (result == Boolean.TRUE ? "success": errorMessage);
        Integer responseCode    =  (result == Boolean.TRUE ? 0 : errorCode);
        return new CommonResponse(responseCode, responseMessage);
    }

}
