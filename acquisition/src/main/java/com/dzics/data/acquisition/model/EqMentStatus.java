package com.dzics.data.acquisition.model;

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
     * 淬火机 连接状态
     */
    public static final String TCP_CL_ST_CHJ = "H561";
    /**
     * 校值机 连接状态
     */
    public static final String TCP_CL_ST_JZJ = "K561";


    /**
     * 机床 操作模式，自动/手动
     */
    public static final String TCP_OPE_MODE_CNC = "B565";
    /**
     * 机器人 操作模式，自动/手动
     */
    public static final String TCP_OPE_MODE_ROB = "A562";

    /**
     * 淬火机 操作模式，自动/手动
     */
    public static final String TCP_OPE_MODE_CHJ = "H566";
    /**
     * 校直机 操作模式，自动/手动
     */
    public static final String TCP_OPE_MODE_JZJ = "K566";

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
     * 淬火机 运行状态
     */
    public static final String TCP_RUN_STATE_CHJ = "H562";
    /**
     * 校直机 运行状态
     */
    public static final String TCP_RUN_STATE_JZJ = "K562";


    /**
     * 机床急停状态
     */
    public static final String TCP_EMERGENCY_STATUS_CNS = "B568";
    /**
     * 机器人急停状态
     */
    public static final String TCP_EMERGENCY_STATUS_ROB = "A565";
    /**
     * 淬火机 急停状态
     */
    public static final String TCP_EMERGENCY_STATUS_CHJ = "H564";
    /**
     * 校直机 急停状态
     */
    public static final String TCP_EMERGENCY_STATUS_JZJ = "K564";

    /**
     * 机床告警状态
     */
    public static final String TCP_ALARM_STATUS_CNC = "B569";

    /**
     * 气体流量
     */
    public static final String CMD_CNC_GAS_FLOW = "B814";
    /**
     * 机器人告警状态
     */
    public static final String TCP_ALARM_STATUS_ROB = "A566";

    /**
     * 淬火机  告警状态
     */
    public static final String TCP_ALARM_STATUS_CHJ = "H565";

    /**
     * 校直机  告警状态
     */
    public static final String TCP_ALARM_STATUS_JZJ = "K565";

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
    public static final String TCP_PYLSE_SIGNAL = "B812";
    public static final String TCP_PYLSE_SIGNAL_2 = "B810";
    /**
     * 机器人脉冲
     */
    public static final String TCP_ROB_PYLSE_SIGNAL = "A810";

    /**
     * 设备检测指令。探针检测数据
     */
    public static final String TCP_ROB_NEEDLE_DETECT = "A809";




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
    public static final String CMD_ROB_CURPROG_COMMENT = "A594";

    /**
     * 程序行号
     */
    public static final String CMD_ROB_CURPROG_ROW = "A595";
    /**
     * 机床工作状态
     */
    public static final String TCP_WORK_STATE_CNS = "B572";
    /**
     * 淬火机 工作状态
     */
    public static final String TCP_WORK_STATE_CHJ = "H563";
    /**
     * 校直机 工作状态
     */
    public static final String TCP_WORK_STATE_JZJ = "K563";

    /**
     * 压头上下位置
     */
    public static final String TCP_HEAD_POSITION_UD_JZJ = "K803";
    /**
     * 压头左右位置
     */
    public static final String TCP_HEAD_POSITION_LR_JZJ = "K804";
    /**
     * 淬火机 移动速度  mm/s
     */
    public static final String TCP_CHJ_SPEED = "H706";
    /**
     * 工件转速 Rad/min
     */
    public static final String TCP_CHJ_WORKPIECE_SPEED = "H707";
    /**
     * 冷却液温度 ℃
     */
    public static final String TCP_CHJ_COOL_TEMP = "H801";
    /**
     * 冷却液压力 MPa
     */
    public static final String TCP_CHJ_COOL_PRESS = "H804";

    /**
     * 冷却液流量 L/s
     */
    public static final String TCP_CHJ_COOL_FLOW = "H805";


    /**
     * 加工节拍（工作台2[切削/循环]）
     */
    public static final String TCO_CNC_JIE_PAI = "B527";
    /**
     * 切削时间 （工作台1[切削/循环]）
     */
    public static final String CMD_CNC_CUTTING_TIME = "B526";
    /**
     * 底层上发的 运行时间
     */
    public static final String CMD_CNC_RUN_TIME = "A541";
    /**
     * 底层上发的 告警刀具编号
     */
    public static final String CMD_CNC_TOOL_NO = "B809";

    /**
     * 底层上发的 停机次数
     */
    public static final String TCP_ROB_WORK_PIECE = "A812";

    /**
     * 底层上发的 待机状态
     */
    public static final String CMD_ROB_WAIT_STATUS = "A567";
}
