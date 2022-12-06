package com.dzics.kanban.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.dzics.kanban.enums.MenuType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 菜单权限表
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_permission")
@ApiModel(value = "SysPermission对象", description = "菜单权限表")
public class SysPermission implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "父id")
    @TableField("parent_id")
    private Long parentId;

    @ApiModelProperty(value = "菜单标题")
    @TableField("title")
    private String title;

    @ApiModelProperty("请求类型 GET,HEAD,POST,PUT,PATCH,DELETE,OPTIONS,TRACE")
//    @ApiModelProperty("请求类型 1:GET,2:HEAD,3:POST,4:PUT,5:PATCH,6:DELETE,7:OPTIONS,8:TRACE")
    @TableField("request_method")
    private String requestMethod;

    @ApiModelProperty(value = "路径")
    @TableField("path")
    private String path;

    @ApiModelProperty(value = "组件")
    @TableField("component")
    private String component;

    @ApiModelProperty(value = "组件名字")
    @TableField("name")
    private String name;

    @ApiModelProperty(value = "面包屑是否点击")
    @TableField("redirect")
    private String redirect;

    @ApiModelProperty(value = "菜单类型(0:一级菜单; 1:子菜单:2:按钮权限)")
    @TableField("menu_type")
    private MenuType menuType;

    @ApiModelProperty(value = "菜单权限编码")
    @TableField("perms")
    private String perms;

    @ApiModelProperty(value = "权限策略1显示2禁用")
    @TableField("perms_type")
    private String permsType;

    @ApiModelProperty(value = "菜单排序")
    @TableField("sort_no")
    private Double sortNo;

    @ApiModelProperty(value = "聚合子路由: 1是0否")
    @TableField("always_show")
    private Boolean alwaysShow;

    @ApiModelProperty(value = "菜单图标")
    @TableField("icon")
    private String icon;

    @ApiModelProperty(value = "是否路由菜单: 0:不是  1:是（默认值1）")
    @TableField("is_route")
    private Boolean isRoute;

    @ApiModelProperty(value = "是否缓存该页面:    1:是   0:不是")
    @TableField("keep_alive")
    private Boolean keepAlive;

    @ApiModelProperty(value = "是否隐藏路由: 0否,1是")
    @TableField("hidden")
    private Integer hidden;

    @ApiModelProperty(value = "外链菜单打开方式 0/内部打开 1/外部打开")
    @TableField("internal_or_external")
    private Boolean internalOrExternal;

    @ApiModelProperty(value = "描述")
    @TableField("description")
    private String description;

    @ApiModelProperty(value = "按钮权限状态(0无效1有效, true 有效 false 无效)")
    @TableField("status")
    private Integer status;

    @ApiModelProperty(value = "删除状态 0正常 1已删除")
    @TableField("del_flag")
    private Boolean delFlag;

    @ApiModelProperty(value = "创建人")
    @TableField("create_by")
    private String createBy;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新人")
    @TableField("update_by")
    private String updateBy;

    @ApiModelProperty(value = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;


}
