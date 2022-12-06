package com.dzics.common.model.constant.mom;

/**
 * 中控编号
 *
 * @author ZhangChengJun
 * Date 2021/6/16.
 */
public class MomReqContent {
    /**
     * 系统编码 中控系统代号
     */
    public static final String REQ_SYS = "DZICS";
    /**
     * 工厂编号
     */
    public static final String FACILITY = "1820";
    /**
     * 顺序号 固定值000000
     */
    public static final String SEQUENCENO = "000000";


    /**
     * mom 请求响应code 为 0 时 表示正常
     */
    public static final String MOM_CODE_OK = "0";

    /**
     * 该订单可叫料数量为0，中控需切换订单再叫料（中兴）
     */
    public static final String MOM_CODE_NEXT = "5";

    /**
     * 等待叫料请求，由人工介入，进行下步处理
     */
    public static final String MOM_CALL_WAIT = "6";
    /**
     * 1:请求来料
     */
    public static final String CALL_AGV_REQ_TYPE_1 = "1";
    /**
     * 2:请求移走空料框；
     */
    public static final String CALL_AGV_REQ_TYPE_2 = "2";
    /**
     * 3:请求空料框；
     */
    public static final String CALL_AGV_REQ_TYPE_3 = "3";
    /**
     * 4:请求移走满料框；
     */
    public static final String CALL_AGV_REQ_TYPE_4 = "4";
    /**
     * 5:请求退料；
     */
    public static final String CALL_AGV_REQ_TYPE_5 = "5";
    /**
     * 6:请求移走NG料；
     */
    public static final String CALL_AGV_REQ_TYPE_6 = "6";


    public static final String MOM_CODE_NEXT_REPORT = "1010";
}
