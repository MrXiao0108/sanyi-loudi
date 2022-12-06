package com.dzics.common.model.response.plan;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;

@Data
public class ProductionPlanDo  {
    @ExcelProperty("订单编号")
    @ApiModelProperty("订单编号")
    private String orderNo;
    @ExcelProperty("归属站点")
    @ApiModelProperty("站点名称")
    private String departName;
    @ExcelProperty("产线名称")
    @ApiModelProperty("产线名称")
    private String lineName;
    @ExcelProperty("日订单生产计划(件)")
    @ApiModelProperty("计划生产数量，修改必填")
    @Min(0)
    private Long plannedQuantity;
    @ExcelProperty("班次生产计划数量")
    @ApiModelProperty("班次生产计划数量")
    @Min(0)
    private Integer dayClasses;
    @ExcelIgnore
    @ApiModelProperty("id，修改必填")
    private Long id;





}
