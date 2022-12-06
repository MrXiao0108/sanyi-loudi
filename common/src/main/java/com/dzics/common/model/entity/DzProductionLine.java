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

import javax.validation.constraints.NotEmpty;

/**
 * <p>
 * 产线表
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dz_production_line")
@ApiModel(value = "DzProductionLine对象", description = "产线表")
public class DzProductionLine implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "订单id")
    @TableField("order_id")
    private Long orderId;

    @ApiModelProperty(value = "统计产线产量设备id")
    @TableField("statistics_equiment_id")
    private Long statisticsEquimentId;

    @ApiModelProperty(value = "产线序号")
    @TableField("line_no")
    private String lineNo;

    @ApiModelProperty(value = "产线编码")
    @TableField("line_code")
    private String lineCode;

    @ApiModelProperty("三一产线编码")
    @TableField("production_line")
    private String productionLine;

    @ApiModelProperty(value = "产线名称")
    @TableField("line_name")
    private String lineName;

    @ApiModelProperty(value = "订单编码")
    @TableField("order_no")
    private String orderNo;

    @ApiModelProperty(value = "机构编码")
    @TableField("org_code")
    private String orgCode;

    @ApiModelProperty(value = "删除状态(0正常 1删除 )")
    @TableField(value = "del_flag", fill = FieldFill.INSERT)
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

    @ApiModelProperty(value = "备注")
    @TableField("remarks")
    private String remarks;

    @ApiModelProperty(value = "1启动 0禁用")
    @TableField(value = "status", fill = FieldFill.INSERT)
    private Integer status;

    /**
     * 产品类型 对应产线制作的类型
     */
    @ApiModelProperty(value = "产线类型(2米活塞杆=2HSG，3米活塞杆=3HSG，2米缸筒=2GT，3米钢筒=3GT)")
    @TableField("line_type")
    private String lineType;

}
