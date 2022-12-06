package com.dzics.common.model.response;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.dzics.common.model.entity.DzEquipmentDowntimeRecord;
import com.dzics.common.model.write.DeviceTypeConverter;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class EquipmentListDo  {

    @ApiModelProperty("安全门编号")
    @ExcelIgnore
    private String doorCode;

    @ExcelProperty("订单编号")
    @ApiModelProperty("订单编号")
    private String orderNo;
    @ApiModelProperty("站点名称")
    @ExcelProperty("归属站点")
    private String departName;
    @ApiModelProperty("产线名称")
    @ExcelProperty("产线名称")
    private String lineName;
    @ApiModelProperty(value = "设备序号")
    @TableField("equipment_no")
    @ExcelProperty("设备序号")
    private String equipmentNo;
    @ApiModelProperty(value = "设备编码")
    @TableField("equipment_code")
    @ExcelProperty("设备编码")
    private String equipmentCode;
    @ApiModelProperty(value = "设备名称")
    @TableField("equipment_name")
    @ExcelProperty("设备名称")
    private String equipmentName;
    @ApiModelProperty("昵称")
    @TableField("nick_name")
    @ExcelProperty("设备昵称")
    private String nickName;
    @ApiModelProperty(value = "设备类型(1检测设备,2机床,3机器人)")
    @TableField("equipment_type")
    @ExcelProperty(value = "设备类型",converter = DeviceTypeConverter.class)
    private Integer equipmentType;

    @ApiModelProperty("连接状态")
    @TableField("connect_state")
    @ExcelProperty("连接状态")
    private String connectState;
    @ApiModelProperty("操作模式")
    @TableField("operator_mode")
    @ExcelProperty("操作模式")
    private String operatorMode;
    @ApiModelProperty("运行状态")
    @TableField("run_status")
    @ExcelProperty("运行状态")
    private String runStatus;

    @ApiModelProperty("历史生产总数")
    @ExcelProperty("历史生产总数")
    private String totalNum;

    @ApiModelProperty(value = "新增人")
    @TableField("create_by")
    @ExcelProperty("新增人")
    private String createBy;

    @ApiModelProperty(value = "新增时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @ExcelProperty("新增时间")
    private Date createTime;


    @TableId(value = "id", type = IdType.AUTO)
    @ExcelIgnore
    private Long id;
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
    @ApiModelProperty("产线序号")
    @TableField("line_no")
    @ExcelIgnore
    private String lineNo;
    @ApiModelProperty("停机时间 单位分钟 min")
    @TableField("down_time")
    @ExcelIgnore
    private Long downTime;
    @ApiModelProperty("停机次数")
    @TableField("down_sum")
    @ExcelIgnore
    private Long downSum;

    @ApiModelProperty("设备清零状态")
    @TableField("clear_count_status")
    @ExcelIgnore
    private String clearCountStatus;
    @TableField("clear_count_status_value")
    @ExcelIgnore
    private Integer clearCountStatusValue;
    @ApiModelProperty(value = "设备状态")
    @TableField("equipment_status")
    @ExcelIgnore
    private String equipmentStatus;
    @TableField("equipment_status_value")
    @ExcelIgnore
    private Integer equipmentStatusValue;
    @TableField("run_status_value")
    @ExcelIgnore
    private Integer runStatusValue;
    @ApiModelProperty("告警状态")
    @TableField("alarm_status")
    @ExcelIgnore
    private String alarmStatus;
    @TableField("alarm_status_vlaue")
    @ExcelIgnore
    private Integer alarmStatusValue;
    @TableField("connect_state_value")
    @ExcelIgnore
    private Integer connectStateValue;
    @TableField("operator_mode_value")
    @ExcelIgnore
    private Integer operatorModeValue;
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
    @TableField("feed_speed")
    @ExcelIgnore
    private String feedSpeed;
    @TableField("speed_of_main_shaft")
    @ExcelIgnore
    private String speedOfMainShaft;
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
    @TableField(exist = false)
    @ExcelIgnore
    private Long dayDownSum;
    @TableField(exist = false)
    @ExcelIgnore
    private DzEquipmentDowntimeRecord downtimeRecord;
    @TableField(exist = false)
    @ExcelIgnore
    private DzEquipmentDowntimeRecord updateDowntimeRecord;
    @TableField(exist = false)
    @ExcelIgnore
    private List<String> logs;
    @TableField(exist = false)
    @ExcelIgnore
    private List<String> logsWar;
    @ApiModelProperty("订单id")
    @ExcelIgnore
    private Long orderId;

    @ExcelProperty("机床连接状态")
    @ExcelIgnore
    private String b561;
    @ExcelProperty("机床运行状态")
    @ExcelIgnore
    private String b562;
    @ExcelProperty("机床操作模式")
    @ExcelIgnore
    private String b565;

    @ExcelProperty("机器人连接状态")
    @ExcelIgnore
    private String a561;
    @ExcelProperty("机器人运行状态")
    @ExcelIgnore
    private String a563;
    @ExcelProperty("机器人操作模式")
    @ExcelIgnore
    private String a562;

    @ExcelProperty("淬火机连接状态")
    @ExcelIgnore
    private String h561;
    @ExcelProperty("淬火机运行状态")
    @ExcelIgnore
    private String h562;
    @ExcelProperty("淬火机操作模式")
    @ExcelIgnore
    private String h566;

    @ExcelProperty("校直机连接状态")
    @ExcelIgnore
    private String k561;
    @ExcelProperty("校直机运行状态")
    @ExcelIgnore
    private String k562;
    @ExcelProperty("校直机操作模式")
    @ExcelIgnore
    private String k566;

    @ExcelIgnore
    @ApiModelProperty("设备标准作业率")
    private String standardOperationRate;

}
