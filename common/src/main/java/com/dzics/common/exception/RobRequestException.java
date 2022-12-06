package com.dzics.common.exception;

import com.dzics.common.exception.enums.CustomResponseCode;

/**
 * @author NeverEnd
 * 机器人请求异常
 */
public class RobRequestException extends RuntimeException {

    private String message;

    public RobRequestException(String msg) {
        this.message = msg;
    }
    public RobRequestException(CustomResponseCode msg) {
        this.message = msg.getChinese();
    }
    @Override
    public String getMessage() {
        return message;
    }
}
