package com.dzics.common.model.constant;

import java.util.Arrays;
import java.util.List;

public final class FinalCode {

    /**
     * 不存在设备编号
     */
    public static final String Device_Code = "AX";
    /**
     * 重置标志类型
     */
    public static final String UDP_CODE_TYPE_EXCHANGE_FLAG_TYPE = "2";

    /**
     * 重置值是 1
     */
    public static final String UDP_CODE_TYPE_EXCHANGE_FLAG_TYPE_VALUE_OK = "1";

    /**
     * 重置值是1 重置为 OK 缓存
     */
    public static final String UDP_CODE_TYPE_EXCHANGE_FLAG_TYPE_VALUE_OK_REST = "OK";
    /**
     * 重置值是 0
     */
    public static final String UDP_CODE_TYPE_EXCHANGE_FLAG_TYPE_VALUE_NG = "0";
    /**
     * 重置值是0 重置为 NG 缓存
     */
    public static final String UDP_CODE_TYPE_EXCHANGE_FLAG_TYPE_VALUE_NG_REST = "NG";
    /**
     * 交换吗类型
     */
    public static final String UDP_CODE_TYPE_EXCHANGE_CODE_TYPE = "1";
    /**
     * 机器人
     */
    public static final Integer ROBOT_EQUIPMENT_CODE = 3;
    /**
     * 机床
     */
    public static final Integer TOOL_EQUIPMENT_CODE = 2;

    /**
     * 淬火机
     */
    public static final Integer TOOL_CUJ_CODE = 8;

    public static final Integer  TOOL_JZJ_CODE = 9 ;
    /**
     * 检测设备
     */
    public static final Integer TEST_EQUIPMENT_CODE = 1;

    public static final String DZ_USE_ORG_CODE = "A00";
    /**
     * 生产计划类型：日
     */
    public static final Integer DZ_PLAN_DAY = 0;
    /**
     * 生产计划类型：周
     */
    public static final Integer DZ_PLAN_WEEK = 1;
    /**
     * 生产计划类型：月
     */
    public static final Integer DZ_PLAN_MONTH = 2;
    /**
     * 生产计划类型：年
     */
    public static final Integer DZ_PLAN_YEAR = 3;

    /**
     * 产品默认图片
     */
    public static final String DZ_PRODUCT = "http://192.168.8.232:8083/file/files/image/png/2021-3-22/Fl-k_I6XwaOAhpWI8tBofmZM5bgB.png";

    /**
     * 默认产品编号
     */
    public static final String DZ_PRODUCT_NO = "默认";
    /**
     * 默认产品名称
     */
    public static final String DZ_PRODUCT_NAME = "默认";
    /**
     * 默认站点名称
     */
    public static final String DZ_DEPART_NAME = "默认";

    /**
     * 五日内产量分析
     */
    public static final String GET_OUT_PUT_BY_LINE_ID = "getOutputByLineId";
    /**
     * 产线五日生产稼动率
     */
    public static final String GET_PRODUCTUIN_PLAN_FIVE_DAY = "getProductionPlanFiveDay";
    /**
     * 产线设备今日用时
     */
    public static final String GET_EQUIMENT_AVAILABLE = "getEquipmentAvailable";
    /**
     * 产线五日计划分析
     */
    public static final String GET_PLAN_ANALYSIS = "getPlanAnalysis";

    /**
     * 机床编号集合
     */
    public static final List<String> DZ_TOOL_CODE_A2 = Arrays.asList("A1", "A2","A4");


    /**
     * 未绑定二维码的检测数据  二维码字段默认值
     */
    public static final String UN_BOUND_QR_CODE = "-9999";

    /**
     * 设备今日用时分析，该设备是否需要前端展示
     */
    public static final String IS_SHOW = "1";


    /**
     * 设备今日用时分析，该设备是否需要前端展示
     */
    public static final Integer SELECT_SUM_EXCEL = 200000;

    /**
     * mom订单状态  已下达
     */
    public static final String MOM_ORDER_STAT_110 = "110";

    /**
     * mom订单状态  进行中
     */
    public static final String MOM_ORDER_STAT_120 = "120";
    /**
     * mom订单状态  已完工
     */
    public static final String MOM_ORDER_STAT_130 = "130";
    /**
     * mom订单状态  已删除 140 -> 170
     */
    public static final String MOM_ORDER_STAT_140 = "170";
    /**
     * mom订单状态  强制关闭
     */
    public static final String MOM_ORDER_STAT_150 = "150";
    /**
     * mom订单状态  暂停
     */
    public static final String MOM_ORDER_STAT_160 = "160";

    public static final String ClASSES_CODE = "day_classes";
    public static final String ORDER_CODE = "order_code";
}
