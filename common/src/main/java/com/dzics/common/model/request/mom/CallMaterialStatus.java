package com.dzics.common.model.request.mom;

import lombok.Data;

/**
 * @author ZhangChengJun
 * Date 2022/1/20.
 * @since
 */
@Data
public class CallMaterialStatus {
    /**
     * 未开始
     */
    public static final int NOT_STARTED = 0;
    /**
     * 已开始
     */
    public static final int STARTED   = 1;

    /**
     * 已完成
     */
    public static final int COMPLETED  = 2;

}
