package com.seasun.management.exception;


import com.seasun.management.constant.ErrorCode;
import com.seasun.management.constant.ErrorMessage;
import com.seasun.management.controller.response.CommonResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class RuntimeExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(RuntimeExceptionHandler.class);

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public CommonResponse handleOtherException(Exception e) {
        logger.error("business exception", e);
        return new CommonResponse(ErrorCode.SYSTEM_ERROR, e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(FlowException.class)
    @ResponseBody
    public CommonResponse handleFlowException(FlowException e) {
        logger.error("flow exception", e);
        return new CommonResponse(e.getCode(), e.getMessage());
    }

    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler(org.springframework.jdbc.BadSqlGrammarException.class)
    @ResponseBody
    public CommonResponse handlePersistentLayerException(BadSqlGrammarException e) {
        logger.error(ErrorMessage.PERSISTENT_LAYER_MESSAGE, e);
        return new CommonResponse(ErrorCode.PERSISTENT_LAYER_MESSAGE, ErrorMessage.PERSISTENT_LAYER_MESSAGE);
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(CopyShareConfigDataException.class)
    @ResponseBody
    public CommonResponse handleCopyDataException(CopyShareConfigDataException e) {
        logger.error("copy share config data exception", e);
        return new CommonResponse(ErrorCode.COPY_SHARE_CONFIG_ERROR, e.getMessage());
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(GroupPermissionException.class)
    @ResponseBody
    public CommonResponse handleGroupPermissionException(GroupPermissionException e) {
        logger.error("change group permission exception", e);
        return new CommonResponse(ErrorCode.COPY_SHARE_CONFIG_ERROR, e.getMessage());
    }


    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(PasswordVerifyException.class)
    @ResponseBody
    public CommonResponse handlePasswordVerifyException(PasswordVerifyException e) {
        logger.error("password verify exception", e);
        return new CommonResponse(ErrorCode.PARAM_ERROR, e.getMessage());
    }


    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(TokenInvalidException.class)
    @ResponseBody
    public CommonResponse handleTokenInvalidException(TokenInvalidException e) {
        logger.error("token invalid exception", e);
        return new CommonResponse(ErrorCode.TOKEN_INVALID_ERROR, e.getMessage());
    }


    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(DataImportException.class)
    @ResponseBody
    public CommonResponse handleFileImportException(DataImportException e) {
        logger.error("file import exception", e.getMessage());
        return new CommonResponse(e.getCode(), e.getMessage());
    }


    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(SyncException.class)
    @ResponseBody
    public CommonResponse handleSyncException(SyncException e) {
        logger.error("data sync exception", e.getMessage());
        return new CommonResponse(ErrorCode.DATA_SYNC_ERROR, e.getMessage());
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(ParamException.class)
    @ResponseBody
    public CommonResponse handleParamException(ParamException e) {
        logger.error("param exception", e.getMessage());
        return new CommonResponse(ErrorCode.PARAM_ERROR, e.getMessage());
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(UserSalaryChangeException.class)
    @ResponseBody
    public CommonResponse handleUserSalaryChangeException(UserSalaryChangeException e) {
        logger.info("user salary change exception", e.getMessage());
        return new CommonResponse(ErrorCode.SALARY_CHANGE_EXCEPTION, e.getMessage());
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(UserInvalidOperateException.class)
    @ResponseBody
    public CommonResponse handleUserInvalidOperateException(UserInvalidOperateException e) {
        logger.info("user order  exception", e.getMessage());
        return new CommonResponse(ErrorCode.USER_INVALID_OPERATE_ERROR, e.getMessage());
    }
}
