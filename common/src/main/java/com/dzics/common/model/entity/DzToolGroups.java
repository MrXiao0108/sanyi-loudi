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
 * 刀具组表
 * </p>
 *
 * @author NeverEnd
 * @since 2021-04-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dz_tool_groups")
@ApiModel(value="DzToolGroups对象", description="刀具组表")
public class DzToolGroups implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @TableId(value = "tool_groups_id", type = IdType.AUTO)
    @ExcelIgnore
    private Long toolGroupsId;

    @ApiModelProperty(value = "刀具组编号")
    @TableField("group_no")
    @ExcelProperty("刀具组编号")
    private Integer groupNo;

    @ApiModelProperty(value = "机构编码")
    @TableField("org_code")
    @ExcelIgnore
    private String orgCode;

    @ApiModelProperty(value = "创建人")
    @TableField("create_by")
    @ExcelIgnore
    private String createBy;


    @TableField(exist = false)
    @ExcelProperty("刀具编号数量")
    private int toolSum;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @ExcelProperty("新增时间")
    private Date createTime;

    @ApiModelProperty(value = "更新人")
    @TableField("update_by")
    @ExcelIgnore
    private String updateBy;

    @ApiModelProperty(value = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @ExcelIgnore
    private Date updateTime;


}
