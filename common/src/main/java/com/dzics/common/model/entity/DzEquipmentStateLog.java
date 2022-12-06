package com.dzics.common.model.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;

import com.dzics.common.model.write.DeviceTypeConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 设备运行状态记录表
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dz_equipment_state_log")
@ApiModel(value="DzEquipmentStateLog对象", description="设备运行状态记录表")
public class DzEquipmentStateLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "产线id")
    @TableField("line_id")
    @ExcelIgnore
    private Long lineId;

    /**
     * 订单编号
     */
    @TableField("order_no")
    @ExcelProperty(value = "订单编号", order = 0)
    private String orderNo;

    @ExcelProperty("设备编号")
    @ApiModelProperty(value = "设备序号")
    @TableField("equipment_no")
    private String equipmentNo;
    @ExcelProperty("告警状态")
    @ApiModelProperty(value = "告警状态")
    @TableField("alarm_status")
    private String alarmStatus;
    @ExcelProperty("连接状态")
    @ApiModelProperty(value = "连接状态")
    @TableField("connect_state")
    private String connectState;
    @ExcelProperty("设备状态")
    @ApiModelProperty(value = "设备状态")
    @TableField("equipment_status")
    private String equipmentStatus;
    @ExcelProperty("急停状态")
    @ApiModelProperty(value = "急停状态")
    @TableField("emergency_status")
    private String emergencyStatus;
    @ExcelProperty("运行状态")
    @ApiModelProperty(value = "运行状态")
    @TableField("run_status")
    private String runStatus;
    @ExcelProperty("操作模式")
    @ApiModelProperty(value = "操作模式")
    @TableField("operator_mode")
    private String operatorMode;
    @ExcelProperty("产线序号")
    @ApiModelProperty(value = "产线序号")
    @TableField("line_no")
    private String lineNo;
    @ExcelProperty(value = "设备类型",converter = DeviceTypeConverter.class)
    @ApiModelProperty(value = "设备类型(1检测设备,2机床,3机器人)")
    @TableField("equipment_type")
    private Integer equipmentType;
    @ExcelProperty("创建时间")
    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    @ExcelIgnore
    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type =IdType.AUTO)
    @JsonIgnore
    private Long id;


    @ExcelIgnore
    @ApiModelProperty(value = "clearCountStatus")
    @TableField("clear_count_status")
    @JsonIgnore
    private String clearCountStatus;

    @ExcelIgnore
    @ApiModelProperty(value = "当前位置")
    @TableField("current_location")
    @JsonIgnore
    private String currentLocation;
    @ExcelIgnore
    @ApiModelProperty(value = "机构编码")
    @TableField("org_code")
    @JsonIgnore
    private String orgCode;
    @ExcelIgnore
    @ApiModelProperty(value = "删除状态(0正常 1删除 )")
    @TableField("del_flag")
    @JsonIgnore
    private Boolean delFlag;
    @ExcelIgnore
    @ApiModelProperty(value = "创建人")
    @TableField("create_by")
    @JsonIgnore
    private String createBy;

    @ExcelIgnore
    @ApiModelProperty(value = "更新人")
    @TableField("update_by")
    @JsonIgnore
    private String updateBy;
    @ExcelIgnore
    @ApiModelProperty(value = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @JsonIgnore
    private Date updateTime;


}
