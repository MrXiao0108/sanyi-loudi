package com.dzics.sanymom.exception;

import com.dzics.common.exception.enums.CustomExceptionType;

public class CustomMomExceptionReq extends RuntimeException {
    //异常错误编码
    private int code;
    //异常信息
    private String message;
    //版本   必填
    private int version;

    //任务ID  随机生成32位UUID，单次下发指令唯一标识   必填
    private String taskId;

    private CustomMomExceptionReq() {
    }

    public CustomMomExceptionReq(CustomExceptionType exceptionTypeEnum, String message) {
        this.code = exceptionTypeEnum.getCode();
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getTaskId() {
        return taskId;
    }

    public int getVersion() {
        return version;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
