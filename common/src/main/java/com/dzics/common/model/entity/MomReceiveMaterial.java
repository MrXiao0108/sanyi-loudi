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
 * 接收来料信息
 * </p>
 *
 * @author NeverEnd
 * @since 2021-07-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("mom_receive_material")
@ApiModel(value = "MomReceiveMaterial对象", description = "接收来料信息")
public class MomReceiveMaterial implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "receive_material_id", type = IdType.ASSIGN_ID)
    private String receiveMaterialId;

    /**
     * mom 唯一订单号
     */
    @TableField("guid")
    private String guid;

    @TableField("order_no")
    private String orderNo;

    @TableField("line_no")
    private String lineNo;

    @ApiModelProperty(value = "mom 订单号")
    @TableField("mom_order_no")
    private String momOrderNo;

    @ApiModelProperty(value = "物料号")
    @TableField("material_no")
    private String materialNo;

    @ApiModelProperty(value = "工序号")
    @TableField("work_no")
    private String workNo;

    @ApiModelProperty(value = "料框编号")
    @TableField("pallet_no")
    private String palletNo;

    @ApiModelProperty(value = "物料数量")
    @TableField("prod_count")
    private String prodCount;

    /**
     * 小车
     */
    @TableField("basket_type")
    private String basketType;

    @ApiModelProperty(value = "机构编码")
    @TableField("org_code")
    private String orgCode;

    @ApiModelProperty(value = "删除状态(0-正常,1-已删除)")
    @TableField("del_flag")
    private Boolean delFlag;

    @ApiModelProperty(value = "创建人")
    @TableField("create_by")
    private String createBy;

    /**
     * 确认时间
     */
    @ApiModelProperty("确认时间")
    @TableField("material_check_time")
    private Date materialCheckTime;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "更新人")
    @TableField("update_by")
    private String updateBy;

    @ApiModelProperty(value = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    @ApiModelProperty(value = "校验结果 ture 通过 false 校验失败")
    @TableField("ok_ng")
    private Boolean okNg;

    @ApiModelProperty(value = "false 未校验，true 已校验")
    @TableField("material_check")
    private Boolean materialCheck;


}
