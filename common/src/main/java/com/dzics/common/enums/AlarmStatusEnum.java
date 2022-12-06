package com.dzics.common.enums;

/**
 * 设备告警状态值
 *
 * @author ZhangChengJun
 * Date 2021/3/9.
 */
public enum AlarmStatusEnum {
    /**
     *ROBOT 告警状态 其他
     */
    ROBOT_AlARM_STATUS_AUTHER(0, "其他"),
    /**
     *ROBOT 告警状态 报警
     */
    ROBOT_AlARM_STATUS_DANGER(1, "报警"),
    /**
     *CNC 告警状态 其他
     */
    CNC_ALARM_STATUS_AUTHER(0, "其他"),
    /**
     *CNC 告警状态 报警
     */
    CNC_ALARM_STATUS_DANGER(1, "报警");


    AlarmStatusEnum(int code, String desc) {
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
