package com.dzics.common.enums;

/**
 * 生产计划任务
 *
 * @author ZhangChengJun
 * Date 2021/2/19.
 * @since
 */
public enum ProductionPlanEnum {
    /**
     * 开启
     */
    Enable(1, "开启"),
    /**
     * 关闭
     */
    Disable(0, "关闭");

    ProductionPlanEnum(Integer code, String desc) {
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
