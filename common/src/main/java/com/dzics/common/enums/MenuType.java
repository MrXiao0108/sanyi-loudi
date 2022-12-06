package com.dzics.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * 菜单类型$
 * $
 *
 * @author ZhangChengJun
 * Date 2021/1/11.
 * @since 1.0.0
 */
public enum MenuType {
    //   菜单类型(0:一级菜单; 1:子菜单:2:按钮权限)
    M(0, "M"),
    C(1, "C"),
    F(2, "F");

    MenuType(Integer code, String desc) {
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
