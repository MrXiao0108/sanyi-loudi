package com.dzics.common.enums;

/**
 * 生产计划类型
 *
 * @author ZhangChengJun
 * Date 2021/2/19.
 * @since
 */
public enum PlanType {
    /**
     * 天
     */
    DAY(0, "天"),
    /**
     * 周
     */
    WEEK(1, "周"),
    /**
     * 月
     */
    MOUTH(2, "月"),
    /**
     * 年
     */
    YEAR(3, "年");

    PlanType(Integer code, String desc) {
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
