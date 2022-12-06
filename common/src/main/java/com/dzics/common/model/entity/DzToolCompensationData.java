package com.dzics.common.model.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.*;
import com.dzics.common.model.write.ToolInfoDataListWrite;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 刀具补偿数据表
 * </p>
 *
 * @author NeverEnd
 * @since 2021-04-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("dz_tool_compensation_data")
@ApiModel(value = "DzToolCompensationData对象", description = "刀具补偿数据表")
public class DzToolCompensationData implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    @ExcelIgnore
    private Integer id;

    @ApiModelProperty(value = "设备id")
    @TableField("equipment_id")
    @ExcelIgnore
    private Long equipmentId;

    @TableField(exist = false)
    @ApiModelProperty(value = "机床编号")
    @ExcelProperty("机床编号")
    private String equipmentNo;

    @ApiModelProperty(value = "组编号")
    @TableField("group_no")
    @ExcelProperty("刀具组编号")
    private Integer groupNo;

    @ApiModelProperty(value = "刀具编号")
    @TableField("tool_no")
    @ExcelProperty("刀具编号")
    private Integer toolNo;

    @ApiModelProperty(value = "刀具寿命")
    @TableField("tool_life")
    @ExcelProperty("刀具寿命")
    private Integer toolLife;

    @ApiModelProperty(value = "刀具寿命计数器")
    @TableField("tool_life_counter")
    @ExcelProperty("刀具寿命计数")
    private Integer toolLifeCounter;

    @ApiModelProperty(value = "刀具寿命计数器类型 0:数量  1:分钟")
    @TableField("tool_life_counter_type")
    @ExcelProperty(value = "刀具寿命计数器类型",converter = ToolInfoDataListWrite.class)
    private Integer toolLifeCounterType;

    @ApiModelProperty(value = "刀具X轴（形状）")
    @TableField("tool_geometry_x")
    @ExcelProperty("刀具X轴（形状）")
    private BigDecimal toolGeometryX;

    @ApiModelProperty(value = "刀具Y轴（形状）")
    @TableField("tool_geometry_y")
    @ExcelProperty("刀具Y轴（形状）")
    private BigDecimal toolGeometryY;

    @ApiModelProperty(value = "刀具Z轴（形状）")
    @TableField("tool_geometry_z")
    @ExcelProperty("刀具Z轴（形状）")
    private BigDecimal toolGeometryZ;

    @ApiModelProperty(value = "刀具C轴（形状）")
    @TableField("tool_geometry_c")
    @ExcelProperty("刀具C轴（形状）")
    private BigDecimal toolGeometryC;

    @ApiModelProperty(value = "刀具半径（形状）")
    @TableField("tool_geometry_radius")
    @ExcelProperty("刀具半径（形状）")
    private BigDecimal toolGeometryRadius;

    @ApiModelProperty(value = "刀具X轴（磨损）")
    @TableField("tool_wear_x")
    @ExcelProperty("刀具X轴（磨损）")
    private BigDecimal toolWearX;

    @ApiModelProperty(value = "刀具Y轴（磨损）")
    @TableField("tool_wear_y")
    @ExcelProperty("刀具Y轴（磨损）")
    private BigDecimal toolWearY;

    @ApiModelProperty(value = "刀具Z轴（磨损）")
    @TableField("tool_wear_z")
    @ExcelProperty("刀具Z轴（磨损）")
    private BigDecimal toolWearZ;

    @ApiModelProperty(value = "刀具C轴（磨损）")
    @TableField("tool_wear_c")
    @ExcelProperty("刀具C轴（磨损）")
    private BigDecimal toolWearC;

    @ApiModelProperty(value = "刀具半径（磨损）")
    @TableField("tool_wear_radius")
    @ExcelProperty("刀具半径（磨损）")
    private BigDecimal toolWearRadius;

    @ApiModelProperty(value = "刀尖方向")
    @TableField("tool_nose_direction")
    @ExcelIgnore
    private Integer toolNoseDirection;

    @ApiModelProperty(value = "刀具描述")
    @TableField("description")
    @ExcelIgnore
    private String description;

    @ApiModelProperty(value = "机构编码")
    @TableField("org_code")
    @ExcelIgnore
    private String orgCode;

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
    @ExcelProperty("时间")
    private Date updateTime;



    @ApiModelProperty(value = "订单id")
    @TableField("order_id")
    @ExcelIgnore
    private Long orderId;


    @ApiModelProperty(value = "产线id")
    @TableField("line_id")
    @ExcelIgnore
    private Long lineId;

}
