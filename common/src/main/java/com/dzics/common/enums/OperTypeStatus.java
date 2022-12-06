package com.dzics.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * @author ZhangChengJun
 * Date 2020/10/27.
 */
public enum OperTypeStatus {
    /**
     * 成功
     */
    SUCCESS(0, "成功"),
    /**
     * 错误
     */
    ERROR(1, "错误");

    OperTypeStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @EnumValue
    private Integer code;
    private String desc;

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
