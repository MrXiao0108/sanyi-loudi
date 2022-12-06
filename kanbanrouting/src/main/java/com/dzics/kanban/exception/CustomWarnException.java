package com.dzics.kanban.exception;


import com.dzics.kanban.exception.enums.CustomExceptionType;
import com.dzics.kanban.exception.enums.CustomResponseCode;

public class CustomWarnException extends RuntimeException {
    //异常错误编码
    private int code;
    //异常信息
    private String message;

    private CustomWarnException() {
    }
    //自定义参数异常
    public CustomWarnException(String errMsg) {
        this.code = CustomExceptionType.TOKEN_PERRMITRE_ERROR.getCode();
        this.message = errMsg;
    }

    public CustomWarnException(CustomExceptionType exceptionTypeEnum) {
        this.code = exceptionTypeEnum.getCode();
        this.message = exceptionTypeEnum.getTypeDesc();
    }

    public CustomWarnException(CustomExceptionType tokenPerrmitreError, CustomResponseCode err53) {
        this.code = tokenPerrmitreError.getCode();
        this.message = err53.getChinese();
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
