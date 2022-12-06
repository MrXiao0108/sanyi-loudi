package com.dzics.kanban.enums;

/**
 * @author ZhangChengJun
 * Date 2021/1/13.
 * @since
 */
public enum StatusEnum {
    /**
     * 启用
     */
    Enable(1, "启用"),
    /**
     * 禁用
     */
    Disable(0, "禁用");

    StatusEnum(Integer code, String desc) {
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
