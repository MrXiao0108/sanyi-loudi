package com.dzics.business.model.vo.rolemenu;

import com.baomidou.mybatisplus.annotation.TableField;
import com.dzics.common.enums.MenuType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author ZhangChengJun
 * Date 2021/1/7.
 */
@Data
public class AddPermission {


    @ApiModelProperty(value = "父id", required = true)
    private String parentId = "0";

    @ApiModelProperty("面包屑 noRedirect不可点击  子路由不要设置 ")
    private String redirect;

    @ApiModelProperty(value = "菜单标题")
    private String menuName;

    @ApiModelProperty(value = "路径")
    private String path;

    @ApiModelProperty("请求类型 GET,HEAD,POST,PUT,PATCH,DELETE,OPTIONS,TRACE")
//    @ApiModelProperty("请求类型 1:GET,2:HEAD,3:POST,4:PUT,5:PATCH,6:DELETE,7:OPTIONS,8:TRACE")
    private String requestMethod;

    @ApiModelProperty(value = "组件")
    private String component;

    @ApiModelProperty(value = "路由名字")
    private String name;

    @ApiModelProperty(value = "菜单类型(M:一级菜单; C:子菜单:T:按钮权限)", required = true)
    @NotNull(message = "菜单类型必填")
    private MenuType menuType;

    @ApiModelProperty(value = "权限编码", required = false)
    private String perms;

    @ApiModelProperty(value = "菜单排序", required = true)
    @NotNull(message = "排序必填")
    private Double sortNo;

    @ApiModelProperty(value = "菜单图标")
    private String icon;

    @ApiModelProperty(value = "是否路由菜单: 0:不是  1:是（默认值1）", required = false)
    @NotNull(message = "是否路由菜单必填")
    private Boolean isRoute = true;



    @ApiModelProperty(value = "是否隐藏路由: 0否,1是", required = true)
    @NotNull(message = "是否隐藏路由必填 hidden")
    private Integer hidden = 0;


    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "按钮权限状态(0无效1有效)  true 有效 false 无效")
    private Integer status = 1;

    @ApiModelProperty("订单号")
    private String orderNo;

    @TableField("line_no")
    private String lineNo;
}
