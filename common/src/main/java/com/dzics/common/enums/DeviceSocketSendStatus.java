package com.dzics.common.enums;

/**
 * 设备发送数据状态定义
 *
 * @author ZhangChengJun
 * Date 2021/2/26.
 * @since
 */
public enum DeviceSocketSendStatus {
    /**
     * 刷新看板
     */
    LOG(-1, "收到日志"),
    /**
     * 刷新看板
     */
    REFRESH(0, "刷新看板"),
    /**
     * 状态信息
     */
    DEVICE_SOCKET_SEND_STATE(1, "状态信息"),
    /**
     * 停机次数
     */
    DEVICE_SOCKET_SEND_DOWN_NUMBER(2, "停机次数"),

    /**
     * 设备基础数据 DEVICE
     */
    DEVICE_SOCKET_SEND_DEVICE(5, "设备基础信息"),

    /**
     * 检测数据趋势
     */
    DEVICE_SOCKET_SEND_DATA_TREND_MANY(7, "检测数据趋势"),
    /**
     * 日生产
     */
    DEVICE_SOCKET_SEND_NISSAN(8, "日生产"),
    /**
     * 月生产
     */
    DEVICE_SOCKET_SEND_MONTHLY_OUTPUT(9, "月生产"),
    /**
     * 五日内产量分析
     */
    DEVICE_SOCKET_SEND_PITPUT_IN_FIVE_DAYS_ANALYSIS(10, "五日内产量分析"),
    /**
     * 产线五日生产稼动率
     */
    DEVICE_SOCKET_SEND_FIVE_DAY_YIELD(11, "产线五日生产稼动率"),
    /**
     *
     */
    DEVICE_SOCKET_SEND_DEVICE_CURRENT_DAILY_HOURS(12, "产线设备今日用时"),
    /**
     * 产线五日计划分析
     */
    DEVICE_SOCKET_SEND_FIVE_DAY_PLAN_ANALYSIS(13, "产线五日计划分析"),
    /**
     * 告警日志
     */
    DEVICE_SOCKET_SEND_ALARM_LOG(14, "告警日志"),
    DEVICE_SOCKET_SEND_ALARM_LOG_AGV(14, "告警AGV日志"),
    /**
     * 实时日志
     */
    DEVICE_SOCKET_SEND_REAL_TIME_LOG(15, "实时日志"),
    DEVICE_SOCKET_SEND_REAL_TIME_LOG_AGV(15, "实时AGV日志"),
    /**
     * 机床刀具信息
     */
    DEVICE_SOCKET_SEND_MACHINE_TOOL_INFORMATION(16, "机床刀具信息"),
    /**
     * 报工信息
     */
    DEVICE_SOCKET_SEND_WORK_REPORT_INFORMATION_MANY(17, "报工信息"),
    /**
     * 报工信息单个
     */
    DEVICE_SOCKET_SEND_WORK_REPORT_INFORMATION_SINGLE(18, "报工信息单个"),
    /**
     * 检测数据趋势单条
     */
    DEVICE_SOCKET_SEND_DATA_TREND_SINGLE(6, "产品检测数据单条"),
    /**
     * 十条产品检测数据
     */
    FOUR_PRODUCT_TEST_DATA(19, "产品检测数据"),
    /**
     * 十条未绑定二维码检测数据
     */
    UNBOUND_QR_CODE_DETECTION(20, "未绑定二维码产品检测数据"),

    /**
     * 十条已经绑定二维码检测数据
     */
    QR_CODE_BOUND(21, "已经绑定二维码产品检测数据"),
    /**
     * 单项检测数据趋势
     */
    GET_DETECTION_ONE(22, "单项检测数据"),

    Material_Information(23, "接收物料信息"),
    GET_MOM_ORDER_STATE(24, "订单状态"),
    GET_MOM_ORDER_QUANTITY(25, "订单生产数量"),
    FRID_ANYS(26,"FRID已解析"),
    FRID_OLD(27,"FRID原始信息"),
    QR_CODE_INPUT(28,"二维码输入"),
    DETECTION_BY_MACHINE(29,"根据机床区分检测数据"),
    GET_DETECTION_LINE_CHART(30,"检测项多项数据折线图"),
    Get_Detection_Record_Mom(32,"人工打磨看板检测记录"),
    Get_Intelligent_Detection(31,"智能检测系统推送");

    private final int code;
    private final String info;

    DeviceSocketSendStatus(int code, String info) {
        this.code = code;
        this.info = info;
    }

    public int getCode() {
        return code;
    }

    public String getInfo() {
        return info;
    }
}
