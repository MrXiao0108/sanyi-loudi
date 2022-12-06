package com.dzics.common.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 等待叫料的订单
 * </p>
 *
 * @author NeverEnd
 * @since 2021-06-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("mom_wait_call_material")
@ApiModel(value = "MomWaitCallMaterial对象", description = "等待叫料的订单")
public class MomWaitCallMaterial implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "wait_material_id", type = IdType.ASSIGN_ID)
    private String waitMaterialId;

    /**
     * dz 订单号
     */
    @TableField("order_no")
    private String orderNo;

    /**
     * dz 产线号
     */
    @TableField("line_no")
    private String lineNo;

    @ApiModelProperty(value = "订单ID")
    @TableField("mom_order_id")
    private String momOrderId;

    @ApiModelProperty("工位编号")
    @TableField("work_station")
    private String workStation;

    @ApiModelProperty("系统编码")
    @TableField("reqSys")
    private String reqSys;

    @ApiModelProperty("工厂编号")
    @TableField("Facility")
    private String facility;

    @ApiModelProperty("顺序号")
    @TableField("SequenceNo")
    private String sequenceNo;

    @ApiModelProperty("工序号")
    @TableField("OprSequenceNo")
    private String oprSequenceNo;

    @ApiModelProperty("生产订单号")
    @TableField("wipOrderNo")
    private String wipOrderNo;

    @ApiModelProperty("1产品物料 2 组件物料")
    @TableField("material_type")
    private String materialType;

    @ApiModelProperty("产品物料")
    @TableField("product_no")
    private  String productNo;
    @ApiModelProperty(value = "物料编码")
    @TableField("materialNo")
    private String materialNo;

    @ApiModelProperty(value = "物料名称")
    @TableField("materialName")
    private String materialName;

    @ApiModelProperty(value = "叫料总数量")
    @TableField("quantity")
    private Integer quantity;

    @ApiModelProperty(value = "已叫料总数量")
    @TableField("sucess_quantity")
    private Integer sucessQuantity;

    /**
     * 剩余叫料总数
     */
    @ApiModelProperty(value = "剩余叫料总数")
    @TableField("surplus_quantity")
    private Integer surplusQuantity;

    @ApiModelProperty(value = "1 叫料已完成 0 叫料未完成")
    @TableField("falg_status")
    private Boolean falgStatus;

    @ApiModelProperty(value = "1 订单已完成 0 订单未完成")
    @TableField("falg_order_status")
    private Integer falgOrderStatus;

    @ApiModelProperty(value = "机构编码")
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


}
