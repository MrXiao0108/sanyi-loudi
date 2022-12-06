package com.dzics.common.model.response.plan;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@Data
public class SelectProductionPlanRecordDo {
    @ExcelProperty("订单编号")
    @ApiModelProperty("订单编号")
    private String orderNo;
    @ExcelProperty("归属站点")
    @ApiModelProperty("站点名称")
    private String departName;
    @ExcelProperty("产线名称")
    @ApiModelProperty("产线名称")
    private String lineName;
    @ExcelProperty("日期")
    @ApiModelProperty("生产日期")
    private String detectorTime;
    @ExcelProperty("日生产计划(件)")
    @ApiModelProperty(value = "计划生产数量")
    @TableField("planned_quantity")
    private Integer plannedQuantity;
    @ExcelProperty("实际完成(件)")
    @ApiModelProperty(value = "已完成数量")
    private Integer completedQuantity;
    @ExcelProperty("完成率(%)")
    @ApiModelProperty(value = "完成率")
    private BigDecimal percentageComplete;

    @ExcelIgnore
    @ApiModelProperty(value = "每次生产计划记录id")
    @TableId(value = "plan_day_id", type =IdType.ASSIGN_ID)
    private Long planDayId;
    @ExcelIgnore
    @ApiModelProperty(value = "计划id")
    @TableField("plan_id")
    private Long planId;





}
