package com.dzics.kanban.enums;

/**
 * @author ZhangChengJun
 * Date 2021/2/1.
 * @since
 */
public enum BasicsAdmin {
    /**
     * 超级管理员
     */
    Admin("admin", "超级管理员");

    BasicsAdmin(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private String code;
    private String desc;

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
