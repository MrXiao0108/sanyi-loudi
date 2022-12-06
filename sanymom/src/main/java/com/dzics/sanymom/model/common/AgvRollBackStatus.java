package com.dzics.sanymom.model.common;

/**
 * agv调度反馈信息 状态
 *
 * @author ZhangChengJun
 * Date 2021/11/9.
 * @since
 */
public class AgvRollBackStatus {
    /**
     * 订单已执行
     */
    public static final String start = "0";
    /**
     * 订单起点完成
     */
    public static final String Starting_Point_OK = "1";
    /**
     * 订单终点完成
     */
    public static final String End_Point_OK = "2";
    ;
}
