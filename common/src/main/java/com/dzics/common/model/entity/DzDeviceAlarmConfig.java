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
 * 设备告警配置
 * </p>
 *
 * @author NeverEnd
 * @since 2021-12-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dz_device_alarm_config")
@ApiModel(value = "DzDeviceAlarmConfig对象", description = "设备")
public class DzDeviceAlarmConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "alarm_config_id", type = IdType.ASSIGN_ID)
    private String alarmConfigId;

    @ApiModelProperty(value = "订单ID")
    @TableField("order_id")
    private String orderId;

    @ApiModelProperty(value = "产线id")
    @TableField("line_id")
    private String lineId;

    @ApiModelProperty(value = "订单号")
    @TableField("order_no")
    private String orderNo;

    @ApiModelProperty(value = "产线号")
    @TableField("line_no")
    private String lineNo;

    @ApiModelProperty(value = "设备ID")
    @TableField("device_id")
    private String deviceId;

    @TableField("equipment_no")
    private String equipmentNo;

    @TableField("equipment_type")
    private Integer equipmentType;

    @ApiModelProperty(value = "数据表示位置")
    @TableField("location_data")
    private Integer locationData;

    @ApiModelProperty(value = "告警内容")
    @TableField("alarm_name")
    private String alarmName;

    @ApiModelProperty(value = "告警等级")
    @TableField("alarm_grade")
    private Integer alarmGrade;

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

    @TableField(exist = false)
    private String lineName;

}
