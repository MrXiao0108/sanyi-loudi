package com.dzics.kanban.exception;


import com.dzics.kanban.exception.enums.CustomExceptionType;
import com.dzics.kanban.exception.enums.CustomResponseCode;
import com.dzics.kanban.exception.enums.ExceptionMsgEnum;

public class CustomException extends RuntimeException  {
    //异常错误编码
    private int code ;
    //异常信息
    private String message;

    private CustomException(){}

    public CustomException(CustomExceptionType exceptionTypeEnum, String message) {
        this.code = exceptionTypeEnum.getCode();
        this.message = message;
    }
    public CustomException(CustomExceptionType exceptionTypeEnum) {
        this.code = exceptionTypeEnum.getCode();
        this.message = exceptionTypeEnum.getTypeDesc();
    }
    public CustomException(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public CustomException(CustomExceptionType tokenPerrmitreError, CustomResponseCode err5) {
        this.code = tokenPerrmitreError.getCode();
        this.message = err5.getChinese();
    }

    public CustomException(CustomExceptionType exceptionType, ExceptionMsgEnum err1) {
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
