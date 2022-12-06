package com.dzics.common.enums;

/**
 * 设备检测状态
 *
 * @author ZhangChengJun
 * Date 2021/2/19.
 * @since
 */
public enum CheckEqState {
    /**
     * 检测值 正常
     */
    OK(1, "OK"),
    /**
     * 检测值异常
     */
    NG(0, "NG"),
    /**
     * 需要做判断处理
     */
    PD(100, "需要判断处理");

    CheckEqState(Integer code, String desc) {
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
