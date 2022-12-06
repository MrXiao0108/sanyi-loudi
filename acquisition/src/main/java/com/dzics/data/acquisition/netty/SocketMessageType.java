package com.dzics.data.acquisition.netty;

/**
 * 动态发送消息类型常量类
 *
 * @author ZhangChengJun
 * Date 2021/5/25.
 * @since
 */
public class SocketMessageType {
    /**
     * 订阅设备状态
     */
    public static final String DEVICE_STATUS = "getDeviceStatus";

    /**
     * 取消订阅设备状态
     */
    public static final String UN_DEVICE_STATUS = "unGetDeviceStatus";

//===================================================================================
    /**
     * 告警弹框
     */
    public static final String WARING_ALERT = "getWaringAlert";

    /**
     * 取消告警弹框
     */
    public static final String UN_WARING_ALERT = "unGetWaringAlert";

//===================================================================================
    /**
     * 订阅设备日志
     */
    public static final String DEVICE_LOG = "getDeviceLogs";

    /**
     * 取消设备日志订阅
     */
    public static final String UN_DEVICE_LOG = "unGetDeviceLogs";

//===================================================================================
    /**
     * 订阅设备告警日志
     */
    public static final String DEVICE_WARN_LOG = "getDeviceWarnLogs";

    /**
     * 取消设备告警日志订阅
     */
    public static final String UN_DEVICE_WARN_LOG = "unGetDeviceWarnLogs";
//===================================================================================
    /**
     * 订阅检测项记录
     */
    public static final String TEST_ITEM_RECORD = "getTestItemRecord";
    /**
     * 取消订阅检测项
     */
    public static final String UN_TEST_ITEM_RECORD = "unGetTestItemRecord";

//===================================================================================
    /**
     * 订阅刀具检测数据
     */
    public static final String TOOL_TEST_DATA = "getToolTestData";

    /**
     * 取消订阅刀具检测数据
     */
    public static final String UN_TOOL_TEST_DATA = "unGetToolTestData";

//===================================================================================
    /**
     * 订阅工件位置数据
     */
    public static final String WORKPIECE_POSITION = "getWorkpiecePosition";
    /**
     * 取消订阅工件位置
     */
    public static final String UN_WORKPIECE_POSITION = "unGetWorkpiecePosition";

//===================================================================================
    /**
     * 订阅停机次数
     */
    public static final String SHUT_DOWN_TIMES = "getShutDownTimes";

    /**
     * 取消订阅停机次数
     */
    public static final String GET_SHUT_DOWN_TIMES = "unGetShutDownTimes";

//===================================================================================
    /**
     * 版本刷新订阅
     */
    public static final String GET_VERSION_PUSH_REFRESH = "getVersionPushRefresh";

    /**
     * 版本刷新订阅 取消
     */
    public static final String UN_GET_VERSION_PUSH_REFRESH = "unGetVersionPushRefresh";

//===================================================================================


    //===================================================================================
    /**
     * 接收物料信息订阅
     */
    public static final String GET_Material_Information = "getMaterialInformation";

    /**
     * 只获取没有确认的物料信息
     */
    public static final String GET_Material_Information_No_Chceck = "getMaterialInCheck";

    /**
     * 接收物料信息 取消
     */
    public static final String UN_GET_Material_Information = "unGetMaterialInformation";

//===================================================================================


    /**
     * 接收获取日志信息
     */
    public static final String GET_VUE_LOG = "getLogSendingInformation";

    //===================================================================================
    /**
     * 看板检测数据趋势 订阅
     */
    public static final String GET_SNAYI_DETECTION_CURVE = "getSanYiDetectionCurve";

    /**
     * 看板检测数据趋势 取消
     */
    public static final String UN_GET_SNAYI_DETECTION_CURVE = "unGetSanYiDetectionCurve";

    //===================================================================================
    /**
     * 三一三米缸筒看板检测数据单项推送 订阅
     */
    public static final String GET_DETECTION_ONE = "getSanYiDetectionOne";

    /**
     * 三一三米缸筒看板检测数据单项推送  取消
     */
    public static final String UN_GET_DETECTION_ONE = "unGetSanYiDetectionOne";

    //====================================================================================
    /**
     * Mom订单状态实时推送  订阅
     */
    public static final String GET_MOM_ORDER_STATE = "getMomOrderState";
    /**
     * Mom订单状态实时推送  取消
     */
    public static final String UN_GET_MOM_ORDER_STATE = "unGetMomOrderState";


    /**
     *  读取FRID 扫码事件订阅
     */
    public static final String GET_Material_SEARCH_FRID = "getMaterialSearchFrid";

    /**
     *  读取FRID 扫码事件 取消
     */
    public static final String UN_GET_Material_SEARCH_FRID = "unGetMaterialSearchFrid";


    /**
     * 订阅二维码 输入
     */
    public static final String GET_QRCODE = "getQrCode";
    /**
     * 取消 获取二维码 输入
     */
    public static final String UN_GET_QRCODE = "unGetQrCode";

    /**
     * 根据机床区分检测数据  订阅
     */
    public static final String GET_DETECTION_BY_MACHINE  = "getDetectionByMachine";
    /**
     *根据机床区分检测数据 取消
     */
    public static final String UN_GET_DETECTION_BY_MACHINE = "unGetDetectionByMachine";
    /**
     * 检测项多项数据折线图推送  订阅
     */
    public static final String GET_DETECTION_LINE_CHART  = "getDetectionLineChart";
    /**
     *检测项多项数据折线图推送 取消
     */
    public static final String UN_GET_DETECTION_LINE_CHART = "unGetDetectionLineChart";
    /**
     * 智能检测系统看板推送 订阅
     */
    public static final String get_intelligent_detection = "getIntelligentDetection";
    /**
     * 智能检测系统看板推送 取消
     */
    public static final String un_get_intelligent_detection = "unGetIntelligentDetection";

    /**
     * 检测记录看板推送 订阅
     */
    public static final String get_detection_record = "getDetectionRecord";
    /**
     * 检测记录看板推送 取消
     */
    public static final String un_get_detection_record = "unGetDetectionRecord";

    /**
     * 人工打磨马尔表追踪
     * */
    public static final String get_maerbiao_record = "getMaErBiaoRecord";

    /**
     * 人工打磨马尔表追踪  取消
     * */
    public static final String un_get_maerbiao_record = "unGetMaErBiaoRecord";
}
