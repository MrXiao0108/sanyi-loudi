package com.dzics.common.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author NeverEnd
 * @since 2021-12-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dz_equipment_alarm_analysis")
@ApiModel(value="DzEquipmentAlarmAnalysis对象", description="")
public class DzEquipmentAlarmAnalysis implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty(value = "设备ID")
    @TableField("device_id")
    private Long deviceId;

    @ApiModelProperty(value = "同组ID")
    @TableField("group_id")
    private Long groupId;

    @ApiModelProperty(value = "订单编号")
    @TableField("order_no")
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

    @ApiModelProperty(value = "告警状态")
    @TableField("alarm_type")
    private String alarmType;

    @ApiModelProperty(value = "开始运行时间")
    @TableField("stop_time")
    private Date stopTime;

    @ApiModelProperty(value = "停止时0-23")
    @TableField("stop_hour")
    private Integer stopHour;

    @ApiModelProperty(value = "停止时间 时分秒00:00:00-23:59:59")
    @TableField("stop_day_Time")
    private LocalTime stopDayTime;

    @ApiModelProperty(value = "停止运行时间")
    @TableField("reset_time")
    private Date resetTime;

    @ApiModelProperty(value = "结束时0-23")
    @TableField("reset_hour")
    private Integer resetHour;

    @ApiModelProperty(value = "结束时间 时分秒00:00:00-23:59:59")
    @TableField("reset_day_Time")
    private LocalTime resetDayTime;

    @ApiModelProperty(value = "运行时长毫秒")
    @TableField("duration")
    private Long duration;

    @ApiModelProperty(value = "运行日期 2021.1.4 日")
    @TableField("stop_data")
    private LocalDate stopData;

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

    @ApiModelProperty(value = "告警类型")
    @TableField("item_value")
    private String itemValue;

}
