package com.dzics.common.exception;

import com.dzics.common.exception.enums.CustomExceptionType;
import com.dzics.common.exception.enums.CustomResponseCode;
import com.dzics.common.exception.enums.ExceptionMsgEnum;

public class BindQrCodeException extends RuntimeException  {
    //异常错误编码
    private int code ;
    //异常信息
    private String message;

    private BindQrCodeException(){}

    public BindQrCodeException(CustomExceptionType exceptionTypeEnum, String message) {
        this.code = exceptionTypeEnum.getCode();
        this.message = message;
    }
    public BindQrCodeException(CustomExceptionType exceptionTypeEnum) {
        this.code = exceptionTypeEnum.getCode();
        this.message = exceptionTypeEnum.getTypeDesc();
    }
    public BindQrCodeException(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public BindQrCodeException(CustomExceptionType tokenPerrmitreError, CustomResponseCode err5) {
        this.code = tokenPerrmitreError.getCode();
        this.message = err5.getChinese();
    }

    public BindQrCodeException(CustomExceptionType exceptionType, ExceptionMsgEnum err1) {
        this.code = exceptionType.getCode();
        this.message = err1.getChinese();
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
