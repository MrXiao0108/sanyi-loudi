package com.dzics.kanban.model.response;

import com.baomidou.mybatisplus.annotation.TableField;
import com.dzics.kanban.enums.MenuType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author ZhangChengJun
 * Date 2021/1/7.
 */
@Data
public class MenusInfo implements Serializable {
    /**
     * 子节点
     */
    @ApiModelProperty("子节点")
    private List<MenusInfo> children;

    @ApiModelProperty(value = "路由序号")
    private String pathNumber;

    @ApiModelProperty(value = "采单id")
    private String menuId;

    @ApiModelProperty(value = "父id")
    private String parentId;

    @ApiModelProperty(value = "菜单标题")
    private String menuName;

    @ApiModelProperty(value = "路径")
    private String path;

    @ApiModelProperty(value = "组件")
    private String component;

    @ApiModelProperty(value = "组件名字")
    private String name;

    @ApiModelProperty(value = "面包屑是否点击")
    private String redirect;

    @ApiModelProperty(value = "菜单类型(0:一级菜单; 1:子菜单:2:按钮权限)")
    private MenuType menuType;

    @ApiModelProperty(value = "权限编码", required = false)
    private String perms;

    @ApiModelProperty(value = "菜单排序")
    private Double sortNo;

    @ApiModelProperty(value = "聚合子路由: 1是0否")
    private Integer alwaysShow;

    @ApiModelProperty(value = "菜单图标")
    private String icon;
    @ApiModelProperty("请求类型 GET,HEAD,POST,PUT,PATCH,DELETE,OPTIONS,TRACE")
//    @ApiModelProperty("请求类型 1:GET,2:HEAD,3:POST,4:PUT,5:PATCH,6:DELETE,7:OPTIONS,8:TRACE")
    private String requestMethod;

    @ApiModelProperty(value = "是否路由菜单: 0:不是  1:是（默认值1）")
    private Integer isRoute;

    @ApiModelProperty(value = "是否隐藏路由: 0否,1是")
    private Integer hidden;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "按钮权限状态(0无效1有效)  true 有效 false 无效")
    private Integer status;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty("订单号")
    private String orderNo;

    @TableField("line_no")
    private String lineNo;
}
