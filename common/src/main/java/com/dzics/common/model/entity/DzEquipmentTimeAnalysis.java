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
 * @since 2021-10-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dz_equipment_time_analysis")
@ApiModel(value = "DzEquipmentTimeAnalysis对象", description = "")
public class DzEquipmentTimeAnalysis implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty("设备ID")
    @TableField("device_id")
    private Long deviceId;

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


    @TableField("work_state")
    private Integer workState;

    @TableField("stop_hour")
    private Integer stopHour;
    @TableField("stop_day_Time")
    private LocalTime stopDayTime;

    @ApiModelProperty(value = "开始运行时间")
    @TableField("stop_time")
    private Date stopTime;

    @TableField("reset_hour")
    private Integer resetHour;
    @TableField("reset_day_Time")
    private LocalTime resetDayTime;

    @ApiModelProperty(value = "停止运行时间")
    @TableField("reset_time")
    private Date resetTime;

    @ApiModelProperty(value = "运行时长毫秒")
    @TableField("duration")
    private Long duration;

    @ApiModelProperty("表示是一次完整记录的ID")
    @TableField("group_id")
    private Long groupId;

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


}
