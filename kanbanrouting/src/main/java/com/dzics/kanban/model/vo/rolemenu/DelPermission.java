package com.dzics.kanban.model.vo.rolemenu;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;

/**
 * @author ZhangChengJun
 * Date 2021/1/7.
 */
public class DelPermission {

    @ApiModelProperty(value = "采单id", required = true)
    @NotNull(message = "请选择采单")
    private Long menuId;

    public DelPermission(String menuId) {
        this.menuId = Long.valueOf(menuId);
    }

    public DelPermission() {
    }

    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = Long.valueOf(menuId);
    }
}
