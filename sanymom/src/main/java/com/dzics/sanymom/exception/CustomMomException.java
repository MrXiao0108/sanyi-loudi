package com.dzics.sanymom.exception;

import com.dzics.common.exception.enums.CustomExceptionType;

public class CustomMomException extends RuntimeException {
    //异常错误编码
    private int code;
    //异常信息
    private String message;
    //版本   必填
    private int version;

    //任务ID  随机生成32位UUID，单次下发指令唯一标识   必填
    private String taskId;

    private CustomMomException() {
    }

    public CustomMomException(CustomExceptionType exceptionTypeEnum, String message, Integer version, String taskId) {
        this.code = exceptionTypeEnum.getCode();
        this.message = message;
        this.version = version;
        this.taskId = taskId;
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
