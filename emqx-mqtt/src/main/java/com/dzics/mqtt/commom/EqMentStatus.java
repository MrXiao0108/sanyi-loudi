package com.dzics.mqtt.commom;

/**
 * 设置状态静态信息
 *
 * @author ZhangChengJun
 * Date 2021/1/18.
 * @since
 */
public class EqMentStatus {
    /**
     * 清零状态
     */
    public static final String TCP_CL_CO_ST = "A148";
    /**
     * 机床 连接状态，如联机、脱机、虚拟机
     */
    public static final String TCP_CL_ST_CNC = "B561";
    /**
     * 机器人 连接状态，如联机、脱机、虚拟机
     */
    public static final String TCP_CL_ST_ROB = "A561";


    /**
     * 机床 操作模式，自动/手动
     */
    public static final String TCP_OPE_MODE_CNC = "B565";
    /**
     * 机器人 操作模式，自动/手动
     */
    public static final String TCP_OPE_MODE_ROB = "A562";

    /**
     * 机床绝对坐标 绝对坐标
     */
    public static final String TCP_ABS_POS_CNC = "B501";
    /**
     * 机器人绝对坐标 世界坐标
     */
    public static final String TCP_ABS_POS_ROB = "A502";
    /**
     * 机床 机械坐标
     */
    public static final String TCP_ABS_POS_CNS = "B502";

    /**
     * 机床运行状态
     */
    public static final String TCP_RUN_STATE_CNS = "B562";
    /**
     * 机器人运行状态
     */
    public static final String TCP_RUN_STATE_ROB = "A563";


    /**
     * 机床急停状态
     */
    public static final String TCP_EMERGENCY_STATUS_CNS = "B568";
    /**
     * 机器人急停状态
     */
    public static final String TCP_EMERGENCY_STATUS_ROB = "A565";

    /**
     * 机床告警状态
     */
    public static final String TCP_ALARM_STATUS_CNC = "B569";
    /**
     * 机器人告警状态
     */
    public static final String TCP_ALARM_STATUS_ROB = "A566";


    /**
     * 机床成品数量
     */
    public static final String TCP_WORKPIECE_COUNT_CNC = "B802";
    /**
     * 机器人成品数量
     */
    public static final String TCP_WORKPIECE_COUNT_ROB = "A803";


    /**
     * 机器人毛坯数量
     */
    public static final String TCP_WORKPIECE_COUNT_MP_ROB = "A805";

    /**
     * 机器人 合格成品数量
     */
    public static final String TCP_WORKPIECE_COUNT_HGP_ROB = "A806";


    /**
     * 机床
     */
    public static final String TCP_PYLSE_SIGNAL = "B810";
    /**
     * 机器人脉冲
     */
    public static final String TCP_ROB_PYLSE_SIGNAL = "A810";

    /**
     * 设备检测指令。探针检测数据
     */
    public static final String TCP_ROB_NEEDLE_DETECT = "A809";


    /**
     * 工件定义 在生产哪个种类工件
     */
    public static final String TCP_ROB_WORK_PIECE = "A812";


    /**
     * 加工节拍
     */
    public static final String CMD_ROB_PROCESS_TIME = "A802";
    /**
     * 速度倍率
     */
    public static final String CMD_ROB_SPEED_RATIO = "A521";

    /**
     * 进给速度
     */
    public static final String CMD_CNC_FEED_SPEED = "B541";

    /**
     * 主轴转速
     */
    public static final String CMD_CNC_SPINDLE_SPEED = "B551";

    /**
     * 日志信息
     * A813
     */
    public static final String CMD_ROB_RUN_INFO = "A813";

    /**
     * 刀具寿命
     */
    public static final String CMD_CUTTING_TOOL_FILE = "B804";

    /**
     * 刀具信息
     */
    public static final String CMD_CUTTING_TOOL_INFO = "B803";

    /**
     * 扫码追踪
     */
    public static final String CMD_ROB_QRCODE_TRACE = "A815";

    /**
     * 补偿数据指令
     */
    public static final String CMD_ROB_WORKPIECE_TOTAL = "A814";

    /**
     * 主程序名称
     */
    public static final String CMD_ROB_PARENTPROG_NAME = "A591";
    /**
     * 主程序注释
     */
    public static final String CMD_ROB_PARENTPROG_COMMENT = "A592";
    /**
     * 当前程序名称
     */
    public static final String CMD_ROB_CURPROG_NAME = "A593";

    /**
     * 当前程序注释
     */
    public static final String CMD_ROB_CURPROG_COMMENT= "A594";

    /**
     * 程序行号
     */
    public static final String CMD_ROB_CURPROG_ROW = "A595";
}
