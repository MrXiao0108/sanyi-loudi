package com.dzics.kanban.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * @author ZhangChengJun
 * Date 2020/10/27.
 */
public enum OperType {
    //    0其它 1新增 2修改 3删除 4 查询
    OTHER(0, "其它"),
    ADD(1, "新增"),
    UPDATE(2, "修改 "),
    DEL(3, "删除"),
    QUERY(4, "查询"),
    LOGIN(5, "登录"),
    LOOUT(6, "退出");

    OperType(Integer code, String desc) {
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
