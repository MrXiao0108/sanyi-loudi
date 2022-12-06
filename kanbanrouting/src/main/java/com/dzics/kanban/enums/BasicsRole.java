package com.dzics.kanban.enums;

/**
 * @author ZhangChengJun
 * Date 2021/1/13.
 * @since
 */
public enum BasicsRole {
    /**
     * 角色类型 超管类型角色
     */
    Admin(0, "超管类型角色"),
    /**
     * 角色类型 是基础角色
     */
    JC(1, "是基础角色"),
    /**
     * 角色类型 不是基础角色
     */
    NJC(2, "不是基础角色");

    BasicsRole(Integer code, String desc) {
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
