package com.seasun.management.constant;

public interface ErrorMessage {
    String PERSISTENT_LAYER_MESSAGE = "数据获取错误";
    String PARAM_ERROR_MESSAGE = "参数异常";
    String NETWOOK_ERROR = "您的网络异常，请稍后重试";
    String WX_CONNECT_TIMEOUT = "微信服务器连接超时,请稍后重试";
    String SIGN_DECRYPT_ERROR = "sign验证失败";
    String USER_ORDER_PARAM_ERROR = "参数异常，未查询到订单";
    String CACHE_REFRESH_ERROR="刷新cacheName为:%s,storeKey为%s的缓存失败,失败原因:%s";
    String KOA_USER_ERROR = "该功能尚未开放，如需使用请联系杨健东或熊海涛";
    String USER_STATE_PERMISSIONS_ERROR = "您没有权限，如需使用请联系熊海涛。";
    String OPERATION_FAILED = "操作失败，请联系管理员。";
    String PLAT_INVALID_ERROR = "平台不存在或处于非激活状态，无法操作！";
}
