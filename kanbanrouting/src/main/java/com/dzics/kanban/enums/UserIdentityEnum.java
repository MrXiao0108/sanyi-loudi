package com.dzics.kanban.enums;

/**
 * @author ZhangChengJun
 * Date 2021/1/13.
 * @since
 */
public enum UserIdentityEnum {
    /**
     * 大正用户
     */
    DZ(1, "大正用户"),
    /**
     * 直属站点用户
     */
    DEPART(2, "直属站点用户"),
    /**
     * 其他用户
     */
    ORTER(3, "其他用户");

    UserIdentityEnum(Integer code, String desc) {
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
