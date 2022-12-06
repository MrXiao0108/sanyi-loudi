package com.dzics.common.exception;

import com.dzics.common.exception.enums.CustomResponseCode;

/**
 * @author NeverEnd
 * WMS 请求异常
 */
public class WmsRequestException extends RuntimeException {

    private String message;

    public WmsRequestException(String msg) {
        this.message = msg;
    }
    public WmsRequestException(CustomResponseCode msg) {
        this.message = msg.getChinese();
    }
    @Override
    public String getMessage() {
        return message;
    }
}
