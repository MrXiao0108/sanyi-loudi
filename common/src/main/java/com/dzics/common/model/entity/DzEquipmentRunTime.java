package com.dzics.common.model.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import java.time.LocalDate;
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
 * 稼动记录
 * </p>
 *
 * @author NeverEnd
 * @since 2021-03-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dz_equipment_run_time")
@ApiModel(value="DzEquipmentRunTime对象", description="稼动记录")
public class DzEquipmentRunTime implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type =IdType.AUTO)
    private Long id;

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

    @ApiModelProperty(value = "开始运行时间")
    @TableField("stop_time")
    private Date stopTime;

    @ApiModelProperty(value = "停止运行时间")
    @TableField("reset_time")
    private Date resetTime;

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


}
