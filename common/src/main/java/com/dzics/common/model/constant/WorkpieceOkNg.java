package com.dzics.common.model.constant;

import lombok.Data;

/**
 * @author ZhangChengJun
 * Date 2021/5/31.
 * @since
 */
@Data
public class WorkpieceOkNg {
    /**
     * 检测正常
     */
    public static final int WORKPIEC_OK = 1;

    /**
     * 检测错误
     */
    public static final int WORKPIEC_NG = 0;
}
