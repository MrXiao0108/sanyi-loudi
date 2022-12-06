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

//import com.sun.org.apache.xpath.internal.operations.Bool;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 订单表
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dz_order")
@ApiModel(value="DzOrder对象", description="订单表")
public class DzOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type =IdType.AUTO)
    @ExcelIgnore
    private Long id;

    @ApiModelProperty(value = " 公司id")
    @TableField("depart_id")
    @ExcelIgnore
    private Long departId;

    @ApiModelProperty(value = "订单编号")
    @TableField("order_no")
    @ExcelProperty(value = "订单编号",index = 0)
    private String orderNo;

    @ApiModelProperty(value = "订单名称")
    @TableField("order_name")
    @ExcelIgnore
    private String orderName;

    @ApiModelProperty(value = "备注")
    @TableField("remarks")
    @ExcelProperty("备注")
    private String remarks;

    @ApiModelProperty(value = "机构编码")
    @TableField("org_code")
    @ExcelIgnore
    private String orgCode;

    @ApiModelProperty(value = "删除状态(0正常 1删除 )")
    @TableField("del_flag")
    @ExcelIgnore
    private Boolean delFlag;

    @ApiModelProperty(value = "创建人")
    @TableField("create_by")
    @ExcelProperty("新增人")
    private String createBy;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @ExcelProperty(value = "新增时间")
    private Date createTime;

    @ApiModelProperty(value = "更新人")
    @TableField(value="update_by", fill = FieldFill.INSERT_UPDATE)
    @ExcelIgnore
    private String updateBy;

    @ApiModelProperty(value = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @ExcelIgnore
    private Date updateTime;


}
