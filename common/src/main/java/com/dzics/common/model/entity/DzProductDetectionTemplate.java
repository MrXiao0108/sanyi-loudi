package com.dzics.common.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 产品检测设置默认模板
 * </p>
 *
 * @author NeverEnd
 * @since 2021-02-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dz_product_detection_template")
@ApiModel(value = "DzProductDetectionTemplate对象", description = "产品检测设置默认模板")
public class DzProductDetectionTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "唯一属性")
    @TableId(value = "detection_id",type =IdType.AUTO)
    private Long detectionId;

    @TableField("depart_id")
    @ApiModelProperty("站点id")
    private Long departId;

    @TableField("group_Id")
    private Long groupId;

    /**
     * 订单ID
     */
    @ApiModelProperty("订单Id")
    @TableField("order_id")
    private Long orderId;

    @ApiModelProperty(value = "订单编号")
    @TableField("order_no")
    private String orderNo;

    /**
     * 产线ID
     */
    @ApiModelProperty("产线ID")
    @TableField("line_id")
    private Long lineId;

    /**
     * 产线编号
     */
    @ApiModelProperty("产线编号")
    @TableField("line_no")
    private String lineNo;


    @ApiModelProperty(value = "产品编号，就是id")
    @TableField("product_no")
    private String productNo;

    @ApiModelProperty(value = "表格字段值")
    @TableField("table_col_val")
    private String tableColVal;

    @ApiModelProperty(value = "检测内容 列名")
    @TableField("table_col_con")
    private String tableColCon;

    @ApiModelProperty(value = "0展示1不展示")
    @TableField("is_show")
    private Integer isShow;

    @ApiModelProperty(value = "检测编号")
    @TableField("serial_number")
    private String serialNumber;

    @ApiModelProperty(value = "标准值")
    @TableField("standard_value")
    private BigDecimal standardValue;

    @ApiModelProperty(value = "上线值")
    @TableField("upper_value")
    private BigDecimal upperValue;

    @ApiModelProperty(value = "下线值")
    @TableField("lower_value")
    private BigDecimal lowerValue;

    @ApiModelProperty(value = "补偿值")
    @TableField("compensation_value")
    private BigDecimal compensationValue;

    @ApiModelProperty(value = "偏移值")
    @TableField("deviation_value")
    private BigDecimal deviationValue;

    @ApiModelProperty(value = "创建数据机构编码")
    @TableField("org_code")
    private String orgCode;

    @ApiModelProperty(value = "删除状态(0-正常,1-已删除)")
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

    @ApiModelProperty(value = "智能检测 0展示,1不展示")
    @TableField("whether_show")
    private Integer whetherShow;

    /**
     *
     */
    @TableField(exist = false)
    private String productName;
}
