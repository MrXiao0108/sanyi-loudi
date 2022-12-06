package com.dzics.common.model.entity;

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
 * mom 二维码 订单关系表
 * </p>
 *
 * @author NeverEnd
 * @since 2021-08-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("mom_order_qr_code")
@ApiModel(value = "MomOrderQrCode对象", description = "mom 二维码 订单关系表")
public class MomOrderQrCode implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "唯一主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    @TableField("pro_task_order_id")
    @ApiModelProperty("mom 订单ID")
    private String proTaskOrderId;

    @ApiModelProperty("订单号")
    @TableField("order_no")
    private String orderNo;

    @ApiModelProperty("产线号")
    @TableField("line_no")
    private String lineNo;

    @ApiModelProperty(value = "二维码")
    @TableField("product_code")
    private String productCode;

    @ApiModelProperty(value = "mom唯一订单号")
    @TableField("mom_orde_guid")
    private String momOrdeGuid;

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


}
