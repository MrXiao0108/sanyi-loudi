package com.dzics.common.exception;

import com.dzics.common.exception.enums.CustomResponseCode;

/**
 *  该订单可叫料数量为0，中控需切换订单再叫料（中兴）
 * @author ZhangChengJun
 * Date 2022/1/20.
 * @since
 */
public class NextOrderException extends RuntimeException {

    private String message;

    public NextOrderException(String msg) {
        this.message = msg;
    }
    public NextOrderException(CustomResponseCode msg) {
        this.message = msg.getChinese();
    }
    @Override
    public String getMessage() {
        return message;
    }
}