package com.dzics.common.model.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.*;
import com.dzics.common.model.write.ConversionSecond;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 设备表
 * </p>
 *
 * @author NeverEnd
 * @since 2021-01-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dz_equipment")
@ApiModel(value = "DzEquipment对象", description = "设备表")
public class DzEquipment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    @ExcelIgnore
    private Long id;

    /**
     * 底层上发的 停机次数
     */
    @TableField(exist = false)
    private String a812;

    /**
     * 工作台1[切削/循环]
     */
    @TableField(exist = false)
    private String b526;

    /**
     * 工作台2[切削/循环]
     */
    @TableField(exist = false)
    private String b527;
    /**
     * 底层上发的 告警刀具编号
     */
    @TableField(exist = false)
    private String b809;

    /**
     * 底层上发的 运行时间
     */
    @TableField(exist = false)
    private String a541;
    /**
     * 脉冲信号每次增加的值
     */
    @TableField("signal_value")
    @ExcelIgnore
    private Integer signalValue;

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

    @ApiModelProperty("产线序号")
    @TableField("line_no")
    @ExcelIgnore
    private String lineNo;

    @ApiModelProperty("设备安全门编号")
    @TableField("door_code")
    private String doorCode;
    /**
     * 设备序号
     */
    @ApiModelProperty(value = "设备序号")
    @TableField("equipment_no")
    @ExcelProperty("机器人序号")
    private String equipmentNo;

    @ApiModelProperty(value = "设备类型(1检测设备,2机床,3机器人)")
    @TableField("equipment_type")
    @ExcelIgnore
    private Integer equipmentType;

    @ApiModelProperty(value = "设备编码")
    @TableField("equipment_code")
    @ExcelProperty("机器人编号")
    private String equipmentCode;

    @ApiModelProperty(value = "设备名称")
    @TableField("equipment_name")
    @ExcelProperty("机器人名称")
    private String equipmentName;

    @ApiModelProperty("停机时间 单位分钟 min")
    @TableField("down_time")
    @ExcelProperty(value = "累计停机时间", converter = ConversionSecond.class)
    private Long downTime;

    @ApiModelProperty("停机次数")
    @TableField("down_sum")
    @ExcelProperty("停机次数")
    private Long downSum;


    /**
     * 设备清零状态
     */
    @ApiModelProperty("设备清零状态")
    @TableField("clear_count_status")
    @ExcelIgnore
    private String clearCountStatus;

    @TableField("clear_count_status_value")
    @ExcelIgnore
    private Integer clearCountStatusValue;

    /**
     * 清洗时长
     */
    @TableField("clean_time")
    @ExcelIgnore
    private String cleanTime;
    /**
     * 设备状态
     */
    @ApiModelProperty(value = "设备状态")
    @TableField("equipment_status")
    @ExcelIgnore
    private String equipmentStatus;

    @TableField("equipment_status_value")
    @ExcelIgnore
    private Integer equipmentStatusValue;

    /**
     * 运行状态
     */
    @ApiModelProperty("运行状态")
    @TableField("run_status")
    @ExcelIgnore
    private String runStatus;

    @TableField("run_status_value")
    @ExcelIgnore
    private Integer runStatusValue;


    /**
     * 工作状态 -2021-8-13 新增
     */
    @ApiModelProperty("工作状态")
    @TableField("work_status")
    private String workStatus;
    /**
     * 工作状态值 -2021-8-13 新增
     */
    @ApiModelProperty("工作状态值")
    @TableField("work_status_value")
    private Integer workStatusValue;


    /**
     * 压头上下位置
     */
    @TableField("head_position_ud")
    @ApiModelProperty("压头上下位置")
    private String headPositionUd;
    /**
     * 压头左右位置
     */
    @TableField("head_position_lr")
    @ApiModelProperty("压头左右位置")
    private String headPostionLr;


    /**
     * 淬火机 移动速度  mm/s
     */
    @TableField("movement_speed")
    private String movementSpeed;
    /**
     * 工件转速 Rad/min
     */
    @TableField("workpiece_speed")
    private String workpieceSpeed;
    /**
     * 冷却液温度 ℃
     */
    @TableField("coolant_temperature")
    private String coolantTemperature;
    /**
     * 冷却液压力 MPa
     */
    @TableField("coolant_pressure")
    private String coolantPressure;
    /**
     * 冷却液流量 L/s
     */
    @TableField("coolant_flow")
    private String coolantFlow;


    /**
     * 告警状态
     */
    @ApiModelProperty("告警状态")
    @TableField("alarm_status")
    @ExcelIgnore
    private String alarmStatus;

    @TableField("alarm_status_vlaue")
    @ExcelIgnore
    private Integer alarmStatusValue;

    /**
     * 连接状态
     */
    @ApiModelProperty("连接状态")
    @TableField("connect_state")
    @ExcelIgnore
    private String connectState;

    @TableField("connect_state_value")
    @ExcelIgnore
    private Integer connectStateValue;


    /**
     * 操作模式
     */
    @ApiModelProperty("操作模式")
    @TableField("operator_mode")
    @ExcelIgnore
    private String operatorMode;

    @TableField("operator_mode_value")
    @ExcelIgnore
    private Integer operatorModeValue;

    /**
     * 急停状态
     */
    @TableField("emergency_status")
    @ApiModelProperty("急停状态")
    @ExcelIgnore
    private String emergencyStatus;

    @TableField("emergency_status_value")
    @ExcelIgnore
    private Integer emergencyStatusValue;

    @ApiModelProperty(value = "当前位置")
    @TableField("current_location")
    @ExcelIgnore
    private String currentLocation;

    @ApiModelProperty(value = "加工节拍")
    @TableField("machining_time")
    @ExcelIgnore
    private String machiningTime;

    @ApiModelProperty("速度倍率")
    @TableField("speed_ratio")
    @ExcelIgnore
    private String speedRatio;

    /**
     * 进给速度
     */
    @TableField("feed_speed")
    @ExcelIgnore
    private String feedSpeed;

    /**
     * 主轴转速
     */
    @TableField("speed_of_main_shaft")
    @ExcelIgnore
    private String speedOfMainShaft;
    /**
     * 昵称
     */
    @ApiModelProperty("昵称")
    @TableField("nick_name")
    @ExcelIgnore
    private String nickName;

    /**
     * 设备开始运行时间
     */
    @TableField("start_run_time")
    @ExcelIgnore
    private Date startRunTime;

    @ApiModelProperty(value = "机构编码")
    @TableField("org_code")
    @ExcelIgnore
    private String orgCode;
    @ApiModelProperty(value = "备注")
    @TableField("postscript")
    @ExcelIgnore
    private String postscript;

    @ApiModelProperty(value = "删除状态(0-正常,1-已删除)")
    @TableField(value = "del_flag", fill = FieldFill.INSERT)
    @ExcelIgnore
    private Boolean delFlag;

    @ApiModelProperty(value = "创建人")
    @TableField("create_by")
    @ExcelIgnore
    private String createBy;

    @ApiModelProperty(value = "创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @ExcelIgnore
    private Date createTime;

    @ApiModelProperty(value = "更新人")
    @TableField("update_by")
    @ExcelIgnore
    private String updateBy;

    @ApiModelProperty(value = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @ExcelIgnore
    private Date updateTime;

    @ApiModelProperty("是否看板显示该设备(0不显示，1显示)")
    @TableField("is_show")
    @ExcelIgnore
    private Integer isShow;

    @ApiModelProperty("设备作业率")
    @TableField("standardOperationRate")
    @ExcelIgnore
    private Float standardOperationRate;

    /**
     * 当日停机记录
     */
    @TableField(exist = false)
    @ExcelIgnore
    private Long dayDownSum;

    /**
     * 插入停机记录详情
     */
    @TableField(exist = false)
    @ExcelIgnore
    private DzEquipmentDowntimeRecord downtimeRecord;

    /**
     * 更新停机记录详情
     */
    @TableField(exist = false)
    @ExcelIgnore
    private DzEquipmentDowntimeRecord updateDowntimeRecord;


    /**
     * 日志信息
     */
    @TableField(exist = false)
    @ExcelIgnore
    private List<String> logs;

    /**
     * 告警信息
     */
    @TableField(exist = false)
    @ExcelIgnore
    private List<String> logsWar;

    @ApiModelProperty(value = "待机状态")
    @TableField(exist = false)
    @ExcelIgnore
    private String a567;

    @ApiModelProperty("气体流量")
    @TableField(exist = false)
    @ExcelIgnore
    private String gasFlow;



}
