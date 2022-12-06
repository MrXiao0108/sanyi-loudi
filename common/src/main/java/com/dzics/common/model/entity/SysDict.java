package com.dzics.common.model.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 系统字典表
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_dict")
@ApiModel(value="SysDict对象", description="系统字典表")
public class SysDict implements Serializable {

    private static final long serialVersionUID = 1L;


    @ExcelProperty("字典名称")
    @ApiModelProperty(value = "字典名称")
    @TableField("dict_name")
    private String dictName;
    @ExcelProperty("字典编码")
    @ApiModelProperty(value = "字典编码")
    @TableField("dict_code")
    private String dictCode;
    @ExcelProperty("描述")
    @ApiModelProperty(value = "描述")
    @TableField("description")
    private String description;
    @ExcelProperty("新增人")
    @ApiModelProperty(value = "创建人")
    @TableField("create_by")
    private String createBy;
    @ExcelProperty("新增时间")
    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @ExcelIgnore
    @ApiModelProperty(value = "字典类型0为string,1为number")
    @TableField("type")
    private Integer type;
    @ExcelIgnore
    @ApiModelProperty(value = "删除状态(0正常 1删除 )")
    @TableField("del_flag")
    private Boolean delFlag;


    @ExcelIgnore
    @ApiModelProperty(value = "更新人")
    @TableField("update_by")
    private String updateBy;
    @ExcelIgnore
    @ApiModelProperty(value = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
    @ExcelIgnore
    @TableId(value = "id", type =IdType.AUTO)
    private Long id;
}
