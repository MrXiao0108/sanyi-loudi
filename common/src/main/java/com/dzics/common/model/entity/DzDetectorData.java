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
@TableName("dz_detector_data")
@ApiModel(value = "DzDetectorData对象", description = "产品检测数据")
public class DzDetectorData implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "唯一属性")
    @TableId(value = "detector_data_id", type = IdType.AUTO)
    private Long detectorDataId;


    @ApiModelProperty(value = "关联产品表id")
    @TableField("detection_id")
    private Long detectionId;

    @ApiModelProperty("产品条码")
    @TableField("produc_barcode")
    private String producBarcode;

    @ApiModelProperty("机床编号")
    @TableField("machine_number")
    private String machineNumber;

    @ApiModelProperty("工位编号")
    @TableField("work_number")
    private String workNumber;

    @ApiModelProperty("原始总状态")
    @TableField("all_state")
    private Integer allState;

    @ApiModelProperty("总状态1OK 0 NG")
    @TableField("all_state_use")
    private Integer allStateUse;

    @ApiModelProperty("检测项数量")
    @TableField("item_number")
    private String itemNumber;

    @ApiModelProperty("产品id")
    @TableField("product_no")
    private String productNo;

    @ApiModelProperty("设备序号")
    @TableField("equipment_no")
    private String equipmentNo;

    @ApiModelProperty("订单编号")
    @TableField("order_no")
    private String orderNo;

    @ApiModelProperty(value = "检测值")
    @TableField("data_val")
    private BigDecimal dataVal;

    /**
     * 是否合格
     */
    @TableField("is_qualified")
    private Integer isQualified;

    @ApiModelProperty(value = "检测时间")
    @TableField("detector_time")
    private Date detectorTime;

    @TableField("group_key")
    private String groupKey;

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
