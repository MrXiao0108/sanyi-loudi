package com.dzics.common.enums;

/**
 * 设备运行模式
 *
 * @author ZhangChengJun
 * Date 2021/3/10.
 * @since
 */
public enum RunModelEnum {
    /**
     * 自动
     */
    AUTOMATIC(0, "自动");

    RunModelEnum(Integer code, String desc) {
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
