package com.dzics.kanban.model.entity;

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
 * 站点公司表
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_depart")
@ApiModel(value="SysDepart对象", description="站点公司表")
public class SysDepart implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "父机构ID")
    @TableField("parent_id")
    @JsonIgnore
    private Long parentId;

    @ApiModelProperty(value = "站点公司名称")
    @TableField("depart_name")
    private String departName;

    @ApiModelProperty(value = "英文名")
    @TableField("depart_name_en")
    @JsonIgnore
    private String departNameEn;

    @ApiModelProperty(value = "缩写")
    @TableField("depart_name_abbr")
    @JsonIgnore
    private String departNameAbbr;

    @ApiModelProperty(value = "排序")
    @TableField("depart_order")
    private Integer departOrder;

    @ApiModelProperty(value = "描述")
    @TableField("description")
    private String description;

    @ApiModelProperty(value = "机构编码")
    @TableField("org_code")
    private String orgCode;

    @ApiModelProperty(value = "备注")
    @TableField("memo")
    private String memo;

    @ApiModelProperty(value = "状态（1启用，0不启用）")
    @TableField("status")
    private Integer status;

    @ApiModelProperty(value = "删除状态（0，正常，1已删除）")
    @TableField("del_flag")
    @JsonIgnore
    private Boolean delFlag;

    @ApiModelProperty(value = "创建人")
    @TableField("create_by")
    private String createBy;

    @ApiModelProperty(value = "创建日期")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新人")
    @TableField("update_by")
    private String updateBy;

    @ApiModelProperty(value = "更新日期")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;


}
