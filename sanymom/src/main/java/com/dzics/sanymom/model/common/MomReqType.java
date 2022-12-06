package com.dzics.sanymom.model.common;

/**
 * 工序间配送 操作编码 类型 1出料 还是 2拉空料框
 *
 * @author ZhangChengJun
 * Date 2021/6/17.
 */

public class MomReqType {
    /**
     * 0：下料点请求空料框  1：下料点移出满料框 3 空料框回缓存区
     */
    public static final String REQ_TYPE_IN = "0";
    /**
     * 1：下料点移出满料框
     */
    public static final String REQ_TYPE_OUT = "1";

    /**
     * 3 空料框回缓存区
     */
    @Deprecated
    public static final String REQ_TYPE_NO_OUT = "3";

//    ----------------------AGV搬运反馈信息确认到中控----------------------
    /**
     * 0：送空料框
     */
    public static final String FEEDBACK_EMPTY_FEEDING_FRAME = "0";
    /**
     * 1：取空料框离
     */
    public static final String FEEDBACK_TAKE_EMPTY_FRAME = "1";

    /**
     * 2：取满料框
     */
    public static final String FEEDBACK_TAKE_FULL_FRAME = "2";
    /**
     * 9：送满料框至焊接点
     */
    public static final String FEEDBACK_FULL_FEED_FRAME_POINT = "9";

    /**
     * 4：返回立库
     */
    public static final String FEEDBACK_RETURN_LIBRARY = "4";
//    ----------------------AGV搬运反馈信息确认到中控----------------------

}
