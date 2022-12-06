package com.dzics.common.model.constant;

import lombok.Data;

/**
 * 更新料点料框 类型 定义
 *
 * @author ZhangChengJun
 * Date 2021/11/12.
 * @since
 */
@Data
public class PointType {
    /**
     * 上料点
     */
    public static final String UP = "SL";
    /**
     * NG料点
     */
    public static final String NG = "NG";
    /**
     * 退库
     */
    public static final String TL = "TL";
    /**
     * 上下同料点
     */
    public static final String SLXL = "SLXL";
}
