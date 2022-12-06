package com.dzics.common.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;

/**
 * <p>
 * 设备停机记录
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dz_equipment_downtime_record")
@ApiModel(value = "DzEquipmentDowntimeRecord对象", description = "设备停机记录")
public class DzEquipmentDowntimeRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type =IdType.AUTO)
    private Long id;
    @TableField("order_no")
    @ApiModelProperty("订单编号")
    private String orderNo;

    @ApiModelProperty(value = "产线序号")
    @TableField("line_no")
    private String lineNo;

    @ApiModelProperty(value = "设备序号")
    @TableField("equipment_no")
    private String equipmentNo;

    @ApiModelProperty(value = "设备类型(1检测设备,2机床,3机器人)")
    @TableField("equipment_type")
    private Integer equipmentType;

    @ApiModelProperty(value = "停机时间")
    @TableField("stop_time")
    private Date stopTime;

    @ApiModelProperty("停机日期")
    @TableField("stop_data")
    private LocalDate stopData;

    @ApiModelProperty(value = "恢复运行时间")
    @TableField("reset_time")
    private Date resetTime;

    @ApiModelProperty("停机时长")
    @TableField("duration")
    private Long duration;

    @ApiModelProperty(value = "机构编码")
    @TableField("org_code")
    private String orgCode;

    @ApiModelProperty(value = "删除状态(0正常 1删除 )")
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
