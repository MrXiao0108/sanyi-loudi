package com.dzics.common.enums;

/**
 * 设备运行状态
 *
 * @author ZhangChengJun
 * Date 2021/3/10.
 * @since
 */
public enum RunModelStateEnum {
    /**
     * 机床 生产
     */
    MACHINE(3, "生产"),
    /**
     * 机器人 生产
     */
    PRODUCTION(1, "生产");

    RunModelStateEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private Integer code;
    private String desc;

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
