package com.dzics.common.enums;

/**
 * 设备运行状态静态值
 *
 * @author ZhangChengJun
 * Date 2021/1/20.
 * @since
 */
public enum RunStateEnum {
    /**
     * 重启
     */
    CNC_RESET(0, "RESET-重启"),
    /**
     * 停止
     */
    CNC_STOP(1, "STOP-停止"),
    /**
     * 保持
     */
    CNC_HOLD(2, "HOLD-保持"),
    /**
     * 启动
     */
    CNC_START(3, "START-启动"),
    /**
     * 未知
     */
    CNC_MSTR(4, "MSTR-未知");

    RunStateEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private int code;
    private String desc;

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
