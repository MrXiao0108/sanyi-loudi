package com.dzics.common.model.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 角色表
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_role")
@ApiModel(value = "SysRole对象", description = "角色表")
public class SysRole implements Serializable {

    private static final long serialVersionUID = 1L;
    @ExcelProperty(value = "用户账号")
    @ApiModelProperty(value = "角色名称")
    @TableField("role_name")
    private String roleName;

    @ExcelProperty(value = "创建人")
    @ApiModelProperty(value = "创建人")
    @TableField("create_by")
    private String createBy;

    @ExcelProperty(value = "权限字符")
    @ApiModelProperty(value = "角色编码")
    @TableField("role_code")
    private String roleCode;

    @ExcelProperty(value = "描述")
    @ApiModelProperty(value = "描述")
    @TableField("description")
    private String description;

    @ExcelProperty(value = "创建时间")
    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @ExcelIgnore
    @ApiModelProperty(value = "主键id")
    @TableId(value = "role_id", type =IdType.AUTO)
    private Long roleId;
    @ExcelIgnore
    @ApiModelProperty("是否禁用")
    @TableField(exist = false)
    private Boolean disabled;
    @ExcelIgnore
    @ApiModelProperty("归属站点id")
    @TableField("depart_id")
    private Long departId;
    @ExcelIgnore
    @ApiModelProperty("1是基础角色")
    @TableField("basics_role")
    private Integer basicsRole;
    @ExcelIgnore
    @ApiModelProperty(value = "机构编码")
    @TableField("org_code")
    private String orgCode;
    @ExcelIgnore
    @ApiModelProperty(value = "状态(1-正常,0-冻结)")
    @TableField("status")
    private Integer status;
    @ExcelIgnore
    @ApiModelProperty(value = "删除状态(0-正常,1-已删除)")
    @TableField("del_flag")
    @JsonIgnore
    private Boolean delFlag;




    @ExcelIgnore
    @ApiModelProperty(value = "更新人")
    @TableField("update_by")
    @JsonIgnore
    private String updateBy;
    @ExcelIgnore
    @ApiModelProperty(value = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @JsonIgnore
    private Date updateTime;


}
