package com.dzics.common.enums;

/**
 * @author ZhangChengJun
 * Date 2021/1/13.
 * @since
 */
public enum CmdStateClassification {
    /**
     * 脉冲信号
     */
    PYLSE_SIGNAL("SIGANL", "脉冲信号"),
    /**
     *指令解析暂存键值 状态
     */
    STATE("STATE", "状态"),
    /**
     *指令解析暂存键值  运行状态
     */
    RUN_STATE("RUN_STATE", "运行状态"),
    /**
     *指令解析暂存键值  成品数量
     */
    CP_DATA("CP_DATA", "成品数量"),
    /**
     * 指令解析暂存键值 毛坯数量
     */
    MP_DATA("MP_DATA", "毛坯数量"),
    /**
     * 指令解析暂存键值 合格数量
     */
    Ok_DATA("Ok_DATA", "合格数量"),
    /**
     * 指令解析暂存键值 数据生产的时间
     */
    DATA_STATE_TIME("DATA_STATE_TIME", "数据生产的时间"),
    /**
     * 指令解析暂存键值 数据分类值
     */
    TCP_ID("TCP_ID", "数据分类值"),
    /**
     * 指令解析暂存键值 数据检测日志
     */
    TCP_CHECK_EQMENT("TCP_CHECK_EQMENT", "数据检测日志"),
    /**
     * 指令解析暂存键值 设备生产工件名称工件编号
     */
    TCP_ROB_WORK_PIECE("TCP_ROB_WORK_PIECE", "设备生产工件名称工件编号"),
    /**
     * 指令解析暂存键值 设备告警状态
     */
    ALARM_RECPRD("ALARM_RECPRD", "设备告警状态"),
    CMD_ROB_WORKPIECE_TOTAL("WORKPIECETOTAL","补偿数据");

    CmdStateClassification(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private String code;
    private String desc;

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
